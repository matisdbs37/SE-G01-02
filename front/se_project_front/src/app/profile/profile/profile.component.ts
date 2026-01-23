import { Component } from '@angular/core';
import { CommonModule} from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CountryService } from '../services/country.service';
import { Router } from '@angular/router';
import { AuthService } from '../../auth/services/auth.service';
import { UserService } from '../../services/users.service';
import { forkJoin, catchError, of } from 'rxjs';
import { HistoryEntry, HistoryService } from '../../services/history.service';
import { VideoService } from '../../services/video.service';
import { PlanService, Plan } from '../../services/plan.service';

@Component({
  selector: 'app-profile',
  imports: [CommonModule, FormsModule],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css'
})

export class ProfileComponent {
  constructor(private countryService: CountryService, private router: Router, private auth: AuthService, private userService: UserService, private historyService: HistoryService, private videoService: VideoService, private planService: PlanService) { }

  // Active section in the profile (initially dashboard)
  activeSection = 'dashboard';

  // Editing state for the section account details
  isEditing = false;

  // List of countries for the country selection in section account details
  countries: string[] = [];
  // Options for notifications preference in section account details
  notificationsOptions: string[] = ['Yes', 'No'];

  // Confirmation text for account deletion
  confirmationText: string = '';

  // Messages for user feedback
  errorMessage: string = '';
  successMessage: string = '';

  // Current user data and editable copy for section account details
  user: any = null;
  editableUser = { ...this.user };

  // User history data for section history
  userHistory: HistoryEntry[] = [];
  historyLoading = false;
  // Pagination state for history
  currentPage = 0;
  isLastPage = false;

  // User plan data for section plan
  userPlan: Plan | null = null;
  planContent: any[] = [];
  planLoading = false;

  // Loading state of the profile page
  loading = true;

  ngOnInit() {
    this.auth.checkAccess(); // Ensure user is authenticated
    
    this.loading = true;

    // Load countries and current user data in parallel
    forkJoin({
      countries: this.countryService.getCountries(),
      user: this.userService.getCurrentUser()
    }).subscribe({
      next: ({ countries, user }) => {
        this.countries = countries;
        this.user = user;
        this.editableUser = { ...user };
        this.loading = false;
      },
      error: (err) => {
        console.error('Error during profile loading', err);
        this.loading = false;
        this.router.navigate(['/auth/login']);
      }
    });
  }

  // Set the active section in the profile
  setSection(section: string) {
    this.errorMessage = '';
    this.successMessage = '';
    this.activeSection = section;
    this.isEditing = false;
    this.editableUser = { ...this.user };

    // Load data for the selected section
    if (section == 'history') {
      this.loadHistoryWithTitles();
    }
    if (section == 'plan') {
      this.loadMyPlan();
    }
  }

  // Catalogue mapping content IDs to titles for history section
  videoCatalogue: Map<string, string> = new Map();

  // Load the user's meditation plan and associated content
  loadMyPlan() {
    this.planLoading = true;
    
    // Load user's plans and all content in parallel
    forkJoin({
      plans: this.planService.getMyPlans().pipe(catchError(() => of([]))),
      videos: this.videoService.getContentByType('Video').pipe(catchError(() => of([]))),
      audios: this.videoService.getContentByType('Audio').pipe(catchError(() => of([])))
    }).subscribe({
      next: ({ plans, videos, audios }) => {
        const allPlans = plans as Plan[];

        // If there are plans, take the latest one
        if (allPlans && allPlans.length > 0) {
          // Sort plans by creation date descending and take the latest
          this.userPlan = allPlans.sort((a, b) => 
            new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()
          )[0];

          // Map plan entries to full content details
          const allContent = [...(videos || []), ...(audios || [])];
          if (this.userPlan && this.userPlan.toWatch) {
            this.planContent = this.userPlan.toWatch.map(entry => {
              const contentDetail = allContent.find(c => c.id === entry.contentId);
              return {
                ...contentDetail,
                notified: entry.notified
              };
            });
          }
        }
        this.planLoading = false;
      },
      error: (err) => {
        console.error("Error loading plan", err);
        this.planLoading = false;
      }
    });
  }

  // Navigate to content detail page in section plan
  openContent(content: any) {
    if (!content || !content.id) return;

    this.router.navigate(['/videos/detail', content.id], { 
      state: { video: content } 
    });
  }

  // Load user history along with content titles
  loadHistoryWithTitles(page: number = 0) {
    this.historyLoading = true;

    // Load all videos and audios to build the catalogue, along with the user's history page
    forkJoin({
      videos: this.videoService.getContentByType('Video').pipe(catchError(() => of([]))),
      audios: this.videoService.getContentByType('Audio').pipe(catchError(() => of([]))),
      historyPage: this.historyService.getHistory(page, 20).pipe(catchError(() => of({ content: [], totalPages: 0, number: 0 })))
    }).subscribe({
      next: ({ videos, audios, historyPage }) => {
        const allContent = [...(videos || []), ...(audios || [])];
        
        // Build or update the video catalogue
        allContent.forEach(item => {
          if (item.id) this.videoCatalogue.set(item.id, item.title);
        });

        // Update user history with titles
        let newData = historyPage.content || [];
        this.userHistory = page === 0 ? newData : [...this.userHistory, ...newData];

        // Sort history by most recent update or watch date
        this.userHistory.sort((a, b) => {
          const dateA = new Date(a.updatedAt || a.watchedAt).getTime();
          const dateB = new Date(b.updatedAt || b.watchedAt).getTime();
          return dateB - dateA;
        });

        // Update pagination state
        this.isLastPage = historyPage.number >= (historyPage.totalPages || 1) - 1;
        this.historyLoading = false;
      },
      error: (err) => {
        console.error("Error during history loading", err);
        this.historyLoading = false;
      }
    });
  }

  // Format watch time in a human-readable format
  formatWatchTime(seconds: number | undefined): string {
    if (seconds === undefined || seconds === null || seconds === 0) return '0s';

    const hours = Math.floor(seconds / 3600);
    const minutes = Math.floor((seconds % 3600) / 60);
    const secs = seconds % 60;

    // Build the formatted string based on available time units
    if (hours > 0) {
      return `${hours}h ${minutes.toString().padStart(2, '0')}m`;
    } else if (minutes > 0) {
      return `${minutes}m ${secs.toString().padStart(2, '0')}s`;
    } else {
      return `${secs}s`;
    }
  }

  // Load more history entries for pagination
  loadMore() {
    if (!this.isLastPage) {
      this.loadHistoryWithTitles(this.currentPage + 1);
    }
  }

  // Get an array representing star ratings for display
  getStarsArray(rating: number | undefined) {
    if (rating === undefined || rating === null) return [];
    
    const rate = rating / 2; // Convert 0-10 scale to 0-5 scale
    const stars = [];
    
    // Build the stars array based on the rating
    for (let i = 1; i <= 5; i++) {
      if (rate >= i) {
        stars.push('full');
      } else if (rate >= i - 0.5) {
        stars.push('half');
      } else {
        stars.push('empty');
      }
    }
    return stars;
  }

  // Toggle editing mode and save changes if applicable in section account details
  toggleEdit() {
    this.errorMessage = '';
    this.successMessage = '';
    let isWorking = 1;
    if (this.isEditing) {
      isWorking = this.saveChanges();
    }
    if (isWorking == 1) {
      this.isEditing = !this.isEditing;
    }
  }

  // Save changes made to user profile in section account details
  saveChanges() {
    // Validate required fields
    if (
      !this.editableUser.firstName ||
      !this.editableUser.lastName ||
      !this.editableUser.locale ||
      !this.editableUser.city ||
      !this.editableUser.preferences
    ) {
      this.errorMessage = "Fields can't be empty.";
      return 0;
    }

    // Check if any changes were made
    const changed =
      this.editableUser.firstName !== this.user.firstName ||
      this.editableUser.lastName !== this.user.lastName ||
      this.editableUser.locale !== this.user.locale ||
      this.editableUser.city !== this.user.city ||
      this.editableUser.preferences !== this.user.preferences;

    // If no changes, inform the user
    if (!changed) {
      this.errorMessage = 'No changes detected, nothing to update.';
      return 1;
    }

    // Prepare updated data
    const updatedData = {
      firstName: this.editableUser.firstName,
      lastName: this.editableUser.lastName,
      locale: this.editableUser.locale,
      city: this.editableUser.city,
      preferences: this.editableUser.preferences,
      updatedAt: new Date().toISOString()
    };

    // Call the user service to update the user data
    this.userService.updateUser(updatedData).subscribe({
      next: (response) => {
        this.user = { ...this.user, ...updatedData };
        this.errorMessage = '';
        this.successMessage = 'Profile updated successfully.';
      },
      error: (err) => {
        this.errorMessage = "Failed to update user.";
        console.error("Error updating user:", err);
      }
    });

    return 1;
  }

  // Logout the user and navigate to login page
  logout() {
    this.auth.logout();
    this.auth.logoutCompletement();
    this.router.navigate(['/auth/login'], { replaceUrl: true });
  }

  // Submit account deletion request after confirmation
  submitDeleteAccount() {
    // Validate confirmation text
    if (!this.confirmationText) {
      this.successMessage = '';
      this.errorMessage = 'All fields are required!';
      return;
    }

    // Check if confirmation text matches
    if (this.confirmationText !== "I want to delete my account.") {
      this.successMessage = '';
      this.errorMessage = 'Confirmation text does not match.';
      return;
    }

    // Call user service to delete the account
    this.userService.deleteUser().subscribe({
      next: (response) => {
        this.auth.logout();
        this.auth.logoutCompletement();
        this.router.navigate(['/auth/login'], { replaceUrl: true });
      },
      error: (err) => {
        console.error("Error during account deletion", err);
        this.successMessage = '';
        this.errorMessage = 'Failed to delete account.';
      }
    });
  }

  // Get color based on metric value for visualization in section dashboard
  getMetricColor(value: number): string {
    const hue = (value / 10) * 120;
    return `hsl(${hue}, 70%, 45%)`;
  }

  // Get color for stress metric (inverted scale) for visualization in section dashboard
  getStressColor(value: number): string {
    const hue = 120 - (value / 10) * 120;
    return `hsl(${hue}, 70%, 45%)`;
  }

  // Navigate back to home page
  back() {
    this.router.navigate(['home']);
  }

  // Navigate to mood check-in questionnaire
  goToMoodCheckIn() {
    this.router.navigate(['/profile/checkin']);
  }
}