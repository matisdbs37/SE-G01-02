import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { VideoService, Content, Categories } from '../../services/video.service';
import { forkJoin } from 'rxjs';

interface ContentWithCategory extends Content {
  category?: string;
}

@Component({
  selector: 'app-video-research',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './video-research.component.html',
  styleUrls: ['./video-research.component.css']
})
export class VideoResearchComponent implements OnInit {
  constructor(private router: Router, private videoService: VideoService) {}

  searchText = '';
  selectedCategory = '';
  selectedDuration = '';
  selectedType = '';
  selectedLanguage = '';
  selectedDifficulty: number | null = null;

  currentPage = 1;
  pageSize = 12;

  videos: ContentWithCategory[] = [];
  categories: Categories[] = [];
  types: string[] = ['video', 'audio'];
  languages: string[] = [];
  difficulties = [1, 2, 3];

  loading = true;

  ngOnInit() {
    this.loading = true;

    forkJoin([
      this.videoService.getContentByType('video'),
      this.videoService.getContentByType('audio'),
      this.videoService.getCategories()
    ]).subscribe({
      next: ([videos, audios, categories]) => { 
        const allContent: ContentWithCategory[] = [...videos, ...audios];

        /*allContent.forEach(c => {
          const cat = categories.find(cat => cat.contentId === c.id);
          c['category'] = cat?.categoryId ?? '';
        });*/

        this.videos = allContent;
        this.categories = categories;

        this.languages = Array.from(new Set(this.videos.map(v => v.language)));

        this.loading = false;
      }
    });
  }

  filteredVideos() {
    return this.videos.filter(v => {
      if (this.searchText &&
          !v.title.toLowerCase().includes(this.searchText.toLowerCase())) {
        return false;
      }

      if (this.selectedCategory && v.category !== this.selectedCategory) return false;
      if (this.selectedType && v.type !== this.selectedType) return false;
      if (this.selectedLanguage && v.language !== this.selectedLanguage) return false;
      if (this.selectedDifficulty !== null && v.difficulty !== this.selectedDifficulty) return false;

      if (this.selectedDuration === 'short' && v.durationMin >= 10) return false;
      if (this.selectedDuration === 'medium' && (v.durationMin < 10 || v.durationMin > 20)) return false;
      if (this.selectedDuration === 'long' && v.durationMin <= 20) return false;

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
    if (start > 2) pages.push('...');
    for (let i = start; i <= end; i++) pages.push(i);
    if (end < total - 1) pages.push('...');
    if (total > 1) pages.push(total);

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
    if (typeof p === 'number') this.goToPage(p);
  }

  openVideo(video: Content, index: number) {
    this.router.navigate(['/videos/detail', index], { state: { video } });
  }

  back() {
    this.router.navigate(['home']);
  }

  onFilterChange() {
    this.currentPage = 1;
  }
}
