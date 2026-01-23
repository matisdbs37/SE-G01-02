import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../auth/services/auth.service';
import { filter, first } from 'rxjs';
import { UserService } from '../services/users.service';

@Component({
  selector: 'mc-home',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {
  // Current logged-in user information
  user: any;

  // Loading state of the component
  loading = true;

  constructor(
    private auth: AuthService,
    private router: Router,
    private userService: UserService
  ) {}

  // Flag indicating if the user has admin privileges
  isAdmin = false;

  ngOnInit(): void {
    this.auth.checkAccess(); // Ensure user is authenticated

    // Listen for token reception events to fetch user and role information
    this.auth.events$.pipe(
      filter(e => e.type === 'token_received'),
      first()
    ).subscribe(() => {
      this.fetchUserAndRole();
    });

    // Initial fetch if already logged in
    if (this.auth.isLoggedIn()) {
      this.fetchUserAndRole();
    }
  }

  // Fetch current user and determine if they have admin role
  private fetchUserAndRole(): void {
    // Fetch current user information
    this.checkUser();

    // Fetch user role to determine admin status
    this.userService.getUserRole().subscribe({
      next: (res: any) => {
        this.isAdmin = res.authority === 'ROLE_ADMIN';
      },
      error: (err) => console.error('Error during role fetching', err)
    });
  }

  // Logout the user and navigate to login page
  logout(): void {
    this.auth.logout();
    this.auth.logoutCompletement();
    this.router.navigate(['/auth/login'], { replaceUrl: true });
  }

  // Navigate to the profile page
  goToProfile(): void {
    this.router.navigateByUrl('/profile');
  }

  // Navigate to the admin dashboard
  goToAdmin() {
  this.router.navigate(['/admin']);
  }

  // Navigate to the videos research page
  goToVideos(): void {
    this.router.navigateByUrl('/videos/research');
  }

  // Navigate to the psychologists map page
  goToPsychologists() {
  this.router.navigate(['/psychologists']);
  }

  // Check if the user profile is complete and redirect if necessary
  checkUser() {
    // Get identity claims from AuthService
    const claims: any = this.auth.getIdentityClaims();

    // If no claims are found, log an error and return
    if (!claims) {
      console.error("Error during user claims retrieval");
      return;
    }

    // Fetch current user details from UserService
    this.userService.getCurrentUser().subscribe({
      next: (user) => {
        this.user = user;

        // Check if profile is incomplete (missing first name or last name)
        const isProfileIncomplete = !user.firstName?.trim() || !user.lastName?.trim();

        // Redirect to questionnaire if profile is incomplete
        if (isProfileIncomplete) {
          this.router.navigateByUrl('/questionnaire');
        }

        // End loading state
        this.loading = false;
      },
      error: (err) => {
        console.error("Error retrieving user information", err);
        this.loading = false;
      }
    });
  }
}
