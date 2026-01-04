import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-video-research',
  imports: [CommonModule, FormsModule],
  templateUrl: './video-research.component.html',
  styleUrl: './video-research.component.css'
})
export class VideoResearchComponent {
  constructor(private router: Router) {}

  searchText = '';
  selectedCategory = '';
  selectedDuration = '';
  selectedRating: number | null = null;
  selectedType = '';
  selectedLanguage = '';
  selectedDifficulty: number | null = null;

  currentPage = 1;
  pageSize = 12;

  categories = ['Sleep', 'Stress', 'Focus', 'Relaxation'];
  ratings = [1, 2, 3, 4, 5];
  types = ['Video', 'Audio'];
  languages = ['EN', 'FR', 'ES'];
  difficulties = [1, 2, 3];

  videos = Array.from({ length: 150 }, (_, i) => ({
    title: `Meditation session ${i + 1}`,
    category: this.categories[i % this.categories.length],
    duration: 5 + (i % 30),
    rating: 1 + (i % 5),
    type: this.types[i % 2],
    language: this.languages[i % this.languages.length],
    difficulty: 1 + (i % 3)
  }));

  filteredVideos() {
    return this.videos.filter(v => {

      if (this.searchText &&
          !v.title.toLowerCase().includes(this.searchText.toLowerCase())) {
        return false;
      }

      if (this.selectedCategory && v.category !== this.selectedCategory) {
        return false;
      }

      if (this.selectedType && v.type !== this.selectedType) {
        return false;
      }

      if (this.selectedLanguage && v.language !== this.selectedLanguage) {
        return false;
      }

      if (this.selectedRating !== null && v.rating < this.selectedRating) {
        return false;
      }

      if (this.selectedDifficulty !== null && v.difficulty !== this.selectedDifficulty) {
        return false;
      }

      if (this.selectedDuration === 'short' && v.duration >= 10) return false;
      if (this.selectedDuration === 'medium' && (v.duration < 10 || v.duration > 20)) return false;
      if (this.selectedDuration === 'long' && v.duration <= 20) return false;

      return true;
    });
  }

  paginatedVideos() {
    const start = (this.currentPage - 1) * this.pageSize;
    return this.filteredVideos().slice(start, start + this.pageSize);
  }

  totalPages() {
    return Math.ceil(this.filteredVideos().length / this.pageSize);
  }

  pagesToDisplay(): (number | string)[] {
    const total = this.totalPages();
    const current = this.currentPage;
    const pages: (number | string)[] = [];

    const windowSize = 5;
    let start = Math.max(2, current - 2);
    let end = Math.min(total - 1, current + 2);

    if (current <= 4) {
      start = 2;
      end = Math.min(total - 1, 1 + windowSize);
    }

    if (current >= total - 3) {
      end = total - 1;
      start = Math.max(2, total - windowSize);
    }

    pages.push(1);

    if (start > 2) {
      pages.push('...');
    }

    for (let i = start; i <= end; i++) {
      pages.push(i);
    }

    if (end < total - 1) {
      pages.push('...');
    }

    if (total > 1) {
      pages.push(total);
    }

    return pages;
  }

  goToPage(page: number) {
    this.currentPage = page;
  }

  nextPage() {
    if (this.currentPage < this.totalPages()) this.currentPage++;
  }

  prevPage() {
    if (this.currentPage > 1) this.currentPage--;
  }

  onPageClick(p: number | string) {
    if (typeof p === 'number') {
      this.goToPage(p);
    }
  }

  openVideo(video: any, index: number) {
    this.router.navigate(['/videos/detail', index], {
      state: { video }
    });
  }
}