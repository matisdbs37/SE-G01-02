import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { VideoService, Content, Category } from '../services/video.service';
import { UserService } from '../services/users.service';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../auth/services/auth.service';

@Component({
  selector: 'mc-admin',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.scss']
})
export class AdminComponent implements OnInit {
  // All contents and categories in the system
  contents: Content[] = [];
  categories: Category[] = [];

  // Mapping of content IDs to their category names
  contentCategories: { [key: string]: string[] } = {};

  // Loading state and messages
  loading = false;
  message = '';

  // Form data for creating new content
  form: Content = {
    url: '',
    title: '',
    type: 'video',
    durationMin: 0,
    difficulty: 1,
    language: '',
    source: ''
  };
  
  // Selected categories for new content
  selectedCategories: string[] = [];

  constructor(
    private videoService: VideoService,
    private userService: UserService,
    private router: Router,
    private authService: AuthService
  ) {}

  // Component initialization
  ngOnInit(): void {
    this.authService.checkAccess(); // Ensure admin access

    this.fetchRole(); // Verify user role

    this.loadContents(); // Load existing contents
    this.loadCategories(); // Load available categories
  }

  // Fetch user role and redirect if not admin
  private fetchRole(): void {
    this.userService.getUserRole().subscribe({
      next: (res: any) => {
        if (res.authority === 'ROLE_USER') {
          this.router.navigate(['/home']);
        }
      },
      error: (err) => {
        console.error('Error during role fetching', err);
        this.router.navigate(['/home']);
      }
    });
  }

  /* ---------------- LOAD ---------------- */

  // Load all contents and their associated categories
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

  // Load all available categories
  loadCategories() {
    this.videoService.getCategories().subscribe(res => {
      this.categories = res;
    });
  }

  /* ---------------- CREATE ---------------- */

  // Create new content and assign selected categories
  createContent() {
    this.form.createdAt = new Date().toISOString();

    this.videoService.createContent(this.form).subscribe({
      next: (created) => {
        this.assignCategories(created.id!);
      },
      error: (err) => {
        console.error('API ERROR:', err);
      }
    });
  }

  // Assign selected categories to the newly created content
  private assignCategories(contentId: string) {
    // If no categories selected, finish creation
    if (this.selectedCategories.length === 0) {
      this.finishCreation();
      return;
    }

    let done = 0;

    // Assign each selected category
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

  // Finalize content creation process
  private finishCreation() {
    this.loading = false;
    this.message = 'Content created successfully ✅';
    this.resetForm();
    this.loadContents();
  }

  // Reset the content creation form
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

  // Delete content by ID
  deleteContent(id: string) {
    if (!confirm('Delete this content ?')) return;

    this.videoService.deleteContent(id).subscribe(() => {
      this.loadContents();
    });
  }

  // Toggle category selection for new content
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

  // Trigger email sending to users
  triggerEmails() {
    this.userService.triggerEmail().subscribe(() => {
      alert('Emails sent');
    });
  }

  // Navigate back to home
  goHome() {
    this.router.navigate(['/home']);
  }

  // Get human-readable label for difficulty level
  getDifficultyLabel(level?: number): string {
    switch (level) {
      case 1: return 'Easy';
      case 2: return 'Medium';
      case 3: return 'Hard';
      default: return '';
    }
  }
}
