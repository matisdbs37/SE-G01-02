import { Component, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import * as L from 'leaflet';

@Component({
  selector: 'app-psychologues-map',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.scss']
})
export class MapComponent implements AfterViewInit {

  map!: L.Map;
  markersLayer = L.layerGroup();
  psychologists: any[] = [];

  ngAfterViewInit() {
    this.initMap();
    this.locateUser();
  }

  /* ---------------- MAP ---------------- */

  initMap() {
    this.map = L.map('map').setView([48.8566, 2.3522], 12);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '© OpenStreetMap'
    }).addTo(this.map);

    this.markersLayer.addTo(this.map);

    // recharge quand on bouge la carte
    this.map.on('moveend', () => {
      const center = this.map.getCenter();
      this.loadPsychologists([center.lat, center.lng]);
    });
  }

  locateUser() {
    navigator.geolocation.getCurrentPosition(
      pos => {
        const lat = pos.coords.latitude;
        const lng = pos.coords.longitude;

        this.map.setView([lat, lng], 13);

        L.marker([lat, lng], {
          icon: this.userIcon()
        })
          .addTo(this.map)
          .bindPopup('You are here')
          .openPopup();

        this.loadPsychologists([lat, lng]);
      },
      () => {
        // fallback si refus géoloc
        this.loadPsychologists([48.8566, 2.3522]);
      }
    );
  }

  /* ---------------- DATA ---------------- */

  async loadPsychologists([lat, lng]: [number, number]) {

    const zoom = this.map.getZoom();
    const radius = zoom < 11 ? 25000 : zoom < 13 ? 15000 : 7000;

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


    const url = 'https://overpass.kumi.systems/api/interpreter';

    try {

      const res = await fetch(url, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: `data=${encodeURIComponent(query)}`
      });

      const text = await res.text();
      const data = JSON.parse(text);

      this.psychologists = (data.elements || []).filter((el: any) => {
        const name = el.tags?.name?.toLowerCase() || '';
        return !name.includes('veter') &&
              !name.includes('ophthal') &&
              !name.includes('dentist');
      });

      this.renderMarkers();

    } catch (e) {
      console.error('Overpass error', e);
    }
  }


  /* ---------------- FILTER ---------------- */

  isValidPsychologist(el: any): boolean {

    const tags = el.tags || {};
    const blob = JSON.stringify(tags).toLowerCase();

    // on exclut
    const banned = [
      'veter',
      'oph',
      'opt',
      'dent',
      'clinic',
      'hospital'
    ];

    if (banned.some(b => blob.includes(b))) return false;

    // on garde seulement psy
    return blob.includes('psy');
  }

  /* ---------------- MARKERS ---------------- */

  renderMarkers() {
    this.markersLayer.clearLayers();

    this.psychologists.forEach(el => {

      const lat = el.lat;
      const lng = el.lon;
      if (!lat || !lng) return;

      const name = el.tags?.name || 'Psychologist';
      const address = this.formatAddress(el.tags);

      L.marker([lat, lng], { icon: this.psyIcon() })
        .addTo(this.markersLayer)
        .bindPopup(`
          <b>${name}</b><br/>
          ${address}
        `);
    });
  }

  formatAddress(tags: any) {
    if (!tags) return '';

    return `
${tags['addr:housenumber'] || ''}
${tags['addr:street'] || ''}<br/>
${tags['addr:postcode'] || ''}
${tags['addr:city'] || ''}
`;
  }

  /* ---------------- ICONS ---------------- */

  userIcon() {
    return L.divIcon({
      className: 'user-marker',
      html: '📍',
      iconSize: [30, 30]
    });
  }

  psyIcon() {
    return L.divIcon({
      className: 'psy-marker',
      html: '🧠',
      iconSize: [30, 30]
    });
  }
}
