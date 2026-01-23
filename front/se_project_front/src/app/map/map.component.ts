import { Component, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AuthService } from '../auth/services/auth.service';
import * as L from 'leaflet';

@Component({
  selector: 'app-psychologues-map',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.scss']
})
export class MapComponent implements AfterViewInit {
  // Leaflet map and markers layer
  map!: L.Map;
  markersLayer = L.layerGroup();

  // Loaded psychologists data
  psychologists: any[] = [];

  constructor(private authService: AuthService) {}

  ngOnInit() {
    this.authService.checkAccess(); // Ensure user access
  }

  // After view initialization
  ngAfterViewInit() {
    // Initialize the map and locate the user
    this.initMap();
    this.locateUser();
  }

  /* ---------------- MAP ---------------- */

  // Initialize the Leaflet map
  initMap() {
    // Centered on Paris by default
    this.map = L.map('map').setView([48.8566, 2.3522], 12);

    // Add OpenStreetMap tile layer
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '© OpenStreetMap'
    }).addTo(this.map);

    // Add markers layer to the map
    this.markersLayer.addTo(this.map);

    // Load psychologists when the map view changes
    this.map.on('moveend', () => {
      const center = this.map.getCenter();
      this.loadPsychologists([center.lat, center.lng]);
    });
  }

  // Locate the user using browser geolocation
  locateUser() {
    // Use browser geolocation API
    navigator.geolocation.getCurrentPosition(
      pos => {
        const lat = pos.coords.latitude;
        const lng = pos.coords.longitude;

        // Center the map on the user's location
        this.map.setView([lat, lng], 13);

        // Add a marker for the user's location
        L.marker([lat, lng], {
          icon: this.userIcon()
        })
          .addTo(this.map)
          .bindPopup('You are here')
          .openPopup();

        // Load psychologists around the user's location
        this.loadPsychologists([lat, lng]);
      },
      () => {
        // If geolocation fails, load psychologists around Paris
        this.loadPsychologists([48.8566, 2.3522]);
      }
    );
  }

  /* ---------------- DATA ---------------- */

  // Load psychologists from Overpass API based on map center
  async loadPsychologists([lat, lng]: [number, number]) {
    const zoom = this.map.getZoom();
    const radius = zoom < 11 ? 25000 : zoom < 13 ? 15000 : 7000;

    // Overpass QL query to find psychologists and related healthcare providers
    const query = `
    [out:json][timeout:25];
    (
      /* Psychologues explicites */
      node["healthcare"="psychologist"](around:${radius},${lat},${lng});
      node["healthcare"="psychotherapist"](around:${radius},${lat},${lng});
      node["office"="psychologist"](around:${radius},${lat},${lng});

      /* Santé mentale */
      node["healthcare"="mental_health"](around:${radius},${lat},${lng});
      node["healthcare"="psychiatrist"](around:${radius},${lat},${lng});

      /* Cabinets médicaux */
      node["amenity"="clinic"](around:${radius},${lat},${lng});
      node["amenity"="doctors"](around:${radius},${lat},${lng});

      /* Bâtiments */
      way["healthcare"="psychologist"](around:${radius},${lat},${lng});
      way["healthcare"="psychotherapist"](around:${radius},${lat},${lng});
      way["office"="psychologist"](around:${radius},${lat},${lng});
    );
    out tags center;
    `;

    // Overpass API endpoint
    const url = 'https://overpass.kumi.systems/api/interpreter';

    // Fetch data from Overpass API
    try {
      // Send POST request to Overpass API
      const res = await fetch(url, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: `data=${encodeURIComponent(query)}`
      });

      // Parse the JSON response
      const text = await res.text();
      const data = JSON.parse(text);

      // Filter and store valid psychologists
      this.psychologists = (data.elements || []).filter((el: any) => {
        const name = el.tags?.name?.toLowerCase() || '';
        return !name.includes('veter') &&
              !name.includes('ophthal') &&
              !name.includes('dentist');
      });

      // Render markers on the map
      this.renderMarkers();

    } catch (e) {
      console.error('Overpass error', e);
    }
  }


  /* ---------------- FILTER ---------------- */

  // Check if an element is a valid psychologist
  isValidPsychologist(el: any): boolean {
    // Filter out unwanted specialties
    const tags = el.tags || {};
    const blob = JSON.stringify(tags).toLowerCase();

    // We exclude these specialties
    const banned = [
      'veter',
      'oph',
      'opt',
      'dent',
      'clinic',
      'hospital'
    ];

    // Filter out unwanted specialties
    if (banned.some(b => blob.includes(b))) return false;

    // Ensure it relates to psychology
    return blob.includes('psy');
  }

  /* ---------------- MARKERS ---------------- */

  // Render psychologist markers on the map
  renderMarkers() {
    // Clear existing markers
    this.markersLayer.clearLayers();

    // Add a marker for each psychologist
    this.psychologists.forEach(el => {
      const lat = el.lat;
      const lng = el.lon;
      if (!lat || !lng) return;

      // Extract name and formatted address
      const name = el.tags?.name || 'Psychologist';
      const address = this.formatAddress(el.tags);

      // Add marker to the map
      L.marker([lat, lng], { icon: this.psyIcon() })
        .addTo(this.markersLayer)
        .bindPopup(`
          <b>${name}</b><br/>
          ${address}
        `);
    });
  }

  // Format address from OSM tags
  formatAddress(tags: any) {
    if (!tags) return '';

    // Construct address string
    return `
    ${tags['addr:housenumber'] || ''}
    ${tags['addr:street'] || ''}<br/>
    ${tags['addr:postcode'] || ''}
    ${tags['addr:city'] || ''}
    `;
  }

  /* ---------------- ICONS ---------------- */

  // Icon for user's location
  userIcon() {
    return L.divIcon({
      className: 'user-marker',
      html: '📍',
      iconSize: [30, 30]
    });
  }

  // Icon for psychologist markers
  psyIcon() {
    return L.divIcon({
      className: 'psy-marker',
      html: '🧠',
      iconSize: [30, 30]
    });
  }
}