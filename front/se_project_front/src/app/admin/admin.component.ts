import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { VideoService, Content, Category } from '../services/video.service';
import { UserService } from '../services/users.service';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'mc-admin',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.scss']
})
export class AdminComponent implements OnInit {

  contents: Content[] = [];
  categories: Category[] = [];

  contentCategories: { [key: string]: string[] } = {};

  loading = false;
  message = '';

  // formulaire
  form: Content = {
    url: '',
    title: '',
    type: 'video',
    durationMin: 0,
    difficulty: 1,
    language: '',
    source: ''
  };

  selectedCategories: string[] = [];

  constructor(
    private videoService: VideoService,
    private userService: UserService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadContents();
    this.loadCategories();
  }

  /* ---------------- LOAD ---------------- */

  loadContents() {
    this.videoService.getAllContent(0, 100).subscribe(res => {
      this.contents = res.content;

      this.contents.forEach(c => {
        this.videoService.getContentCategories(c.id!)
          .subscribe(cats => {
            this.contentCategories[c.id!] =
              cats.map(cat => cat.name);
          });
      });
    });
  }

  loadCategories() {
    this.videoService.getCategories().subscribe(res => {
      this.categories = res;
    });
  }

  /* ---------------- CREATE ---------------- */

  createContent() {
    this.form.createdAt = new Date().toISOString();

    console.log('PAYLOAD:', this.form);

    this.videoService.createContent(this.form).subscribe({
      next: (created) => {
        this.assignCategories(created.id!);
      },
      error: (err) => {
        console.error('API ERROR:', err);
      }
    });
  }

  private assignCategories(contentId: string) {

    if (this.selectedCategories.length === 0) {
      this.finishCreation();
      return;
    }

    let done = 0;

    this.selectedCategories.forEach(catId => {
      this.videoService.assignCategory({
        contentId,
        categoryId: catId
      }).subscribe({
        next: () => {
          done++;
          if (done === this.selectedCategories.length) {
            this.finishCreation();
          }
        }
      });
    });
  }

  private finishCreation() {
    this.loading = false;
    this.message = 'Content created successfully ✅';
    this.resetForm();
    this.loadContents();
  }

  resetForm() {
    this.form = {
      url: '',
      title: '',
      type: 'video',
      durationMin: 0,
      difficulty: 1,
      language: '',
      source: ''
    };
    this.selectedCategories = [];
  }

  /* ---------------- ACTIONS ---------------- */

  deleteContent(id: string) {
    if (!confirm('Delete this content ?')) return;

    this.videoService.deleteContent(id).subscribe(() => {
      this.loadContents();
    });
  }

  toggleCategory(id: string, checked: boolean) {
    if (checked) {
      if (!this.selectedCategories.includes(id)) {
        this.selectedCategories.push(id);
      }
    } else {
      this.selectedCategories =
        this.selectedCategories.filter(c => c !== id);
    }
  }

  triggerEmails() {
    this.userService.triggerEmail().subscribe(() => {
      alert('Emails sent');
    });
  }

  goHome() {
  this.router.navigate(['/home']);
  }

  getDifficultyLabel(level?: number): string {
  switch (level) {
    case 1: return 'Easy';
    case 2: return 'Medium';
    case 3: return 'Hard';
    default: return '';
    }
  }

}
