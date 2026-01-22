import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { VideoService, Content, Category } from '../../services/video.service';
import { forkJoin, catchError, of, map } from 'rxjs';
import { AuthService } from '../../auth/services/auth.service';

// Extended Content model with rating and category names
interface EnhancedContent extends Content {
  categoryNames?: string[];
  rating?: number;
}

@Component({
  selector: 'app-video-research',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './video-research.component.html',
  styleUrls: ['./video-research.component.css']
})
export class VideoResearchComponent implements OnInit {
  constructor(private router: Router, private videoService: VideoService, private auth: AuthService) {}

  // Filter inputs
  searchText = '';
  selectedCategory = '';
  selectedDuration = '';
  selectedRating = '';
  selectedType = '';
  selectedLanguage = '';
  selectedDifficulty: number | null = null;

  // All content, categories, languages and types of content in the catalog
  allVideos: EnhancedContent[] = [];
  categories: Category[] = [];
  languages: string[] = [];
  types: string[] = ['video', 'audio'];

  // Pagination
  currentPage = 1;
  pageSize = 12;

  // Loading state of the page
  loading = true;

  // Component initialization
  ngOnInit() {
    this.auth.checkAccess(); // Ensure user is authenticated

    this.loadAllData(); // Load all data
  }

  // Load videos, audios, categories, and their associations in parallel
  loadAllData() {
    this.loading = true; // Show loading page while fetching data
    
    // Use forkJoin to perform multiple HTTP calls in parallel :
    // Get videos, audios, categories and all category/content associations
    forkJoin({
      videos: this.videoService.getContentByType('video').pipe(catchError(() => of([]))),
      audios: this.videoService.getContentByType('audio').pipe(catchError(() => of([]))),
      categories: this.videoService.getCategories().pipe(catchError(() => of([]))),
      allLinks: this.videoService.getAllCategoryAssociations().pipe(catchError(() => of([])))
    }).subscribe({
      next: ({ videos, audios, categories, allLinks }) => {
        // Combine videos and audios into a single list
        const combined = [...videos, ...audios] as EnhancedContent[];
        this.categories = categories;

        // Map categories to each content based on associations
        const contentsWithCats = combined.map(content => {
          const linkIds = allLinks.filter(l => l.contentId === content.id).map(l => l.categoryId);
          content.categoryNames = categories.filter(c => linkIds.includes(c.id)).map(c => c.name);
          content.rating = -1;
          return content;
        });

        // Fetch average ratings for each content (rating = -1 is no rating)
        const ratingRequests = contentsWithCats.map(v => 
          this.videoService.getMeanRating(v.id!).pipe(
            map(r => ({ id: v.id, rating: r })),
            catchError(() => of({ id: v.id, rating: -1 }))
          )
        );

        // If there are videos to fetch ratings for
        if (ratingRequests.length > 0) {
          forkJoin(ratingRequests).subscribe(ratings => {
            // Assign the ratings to the corresponding video object
            ratings.forEach(res => {
              const video = contentsWithCats.find(v => v.id === res.id);
              if (video) {
                video.rating = res.rating !== -1 ? res.rating / 2 : -1; // Normalize rating to star system (0-5)
              }
            });
            this.allVideos = contentsWithCats;
            this.finalizeLoading(); // Finalize loading state
          });
        } else {
          this.allVideos = contentsWithCats;
          this.finalizeLoading();
        }
      },
      error: () => this.loading = false
    });
  }

  // Finalize loading by extracting unique languages and hiding loading state
  finalizeLoading() {
    this.languages = Array.from(new Set(this.allVideos.map(v => v.language)));
    this.loading = false;
  }

  // Apply all filters of the user to get the filtered results
  getFilteredResults(): EnhancedContent[] {
    return this.allVideos.filter(v => {
      // Filter by title
      if (this.searchText && !v.title.toLowerCase().includes(this.searchText.toLowerCase())) return false;

      // Filter by type (video/audio)
      if (this.selectedType && v.type !== this.selectedType.toLowerCase()) return false;

      // Filter by language
      if (this.selectedLanguage && v.language !== this.selectedLanguage) return false;

      // Filter by difficulty
      if (this.selectedDifficulty !== null && v.difficulty !== this.selectedDifficulty) return false;

      // Filter by category
      if (this.selectedCategory) {
        const categoryObj = this.categories.find(c => c.id === this.selectedCategory);
        if (!v.categoryNames?.includes(categoryObj?.name || '')) return false;
      }

      // Filter by average rating
      if (this.selectedRating) {
        const currentRating = v.rating ?? -1;

        if (this.selectedRating === 'none') {
          if (currentRating !== -1) return false;
        } else {
          const minRating = parseFloat(this.selectedRating);
          if (currentRating === -1 || currentRating < minRating) return false;
        }
      }

      // Filter by duration
      if (this.selectedDuration === 'short' && v.durationMin >= 20) return false;
      if (this.selectedDuration === 'medium' && (v.durationMin < 20 || v.durationMin > 40)) return false;
      if (this.selectedDuration === 'long' && v.durationMin <= 40) return false;

      return true;
    });
  }

  // Get the paginated videos based on current page and page size
  paginatedVideos() {
    const filtered = this.getFilteredResults();
    const start = (this.currentPage - 1) * this.pageSize;
    return filtered.slice(start, start + this.pageSize);
  }

  // Calculate the total number of pages based on filtered results and page size
  totalPages(): number {
    const count = this.getFilteredResults().length;
    return Math.ceil(count / this.pageSize) || 1;
  }

  // Handle page click for pagination
  onPageClick(p: number | string) {
    if (typeof p === 'number') this.currentPage = p;
  }

  // Navigate to the next page if not on the last page
  nextPage() {
    if (this.currentPage < this.totalPages()) this.currentPage++;
  }

  // Navigate to the previous page if not on the first page
  prevPage() {
    if (this.currentPage > 1) this.currentPage--;
  }

  // Generate the array of pages to display in pagination control
  pagesToDisplay(): (number | string)[] {
    const total = this.totalPages();
    const current = this.currentPage;
    const pages: (number | string)[] = [];

    // Simple case: show all pages if total pages <= 5
    if (total <= 1) return [1];

    // Always show first page
    pages.push(1);

    // Show ellipsis if current page is far from the beginning
    if (current > 3) pages.push('...');

    // Show pages around the current page
    for (let i = Math.max(2, current - 1); i <= Math.min(total - 1, current + 1); i++) {
      pages.push(i);
    }

    // Show ellipsis if current page is far from the end
    if (current < total - 2) pages.push('...');

    // Always show last page
    pages.push(total);

    return pages;
  }

  // Reset to first page when any filter changes
  onFilterChange() {
    this.currentPage = 1;
  }

  // Open video detail page with the selected video
  openVideo(video: Content) {
    this.router.navigate(['/videos/detail', video.id], { state: { video } });
  }

  // Generate thumbnail URL based on video platform
  getThumbnail(url: string): string | null {
    // Return null if URL is not provided
    if (!url) return null;

    // YouTube thumbnail extraction
    if (url.includes('youtube.com') || url.includes('youtu.be')) {
      const videoId = url.split('v=')[1]?.split('&')[0] || url.split('/').pop();
      return `https://img.youtube.com/vi/${videoId}/mqdefault.jpg`;
    }

    // Vimeo thumbnail extraction
    if (url.includes('vimeo.com')) {
      const videoId = url.split('/').pop();
      return `https://vumbnail.com/${videoId}.jpg`;
    }

    // Unsupported platform
    return null; 
  }

  // Back to home
  back() { 
    this.router.navigate(['home']); 
  }
}
