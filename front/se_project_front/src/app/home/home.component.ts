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

  user: any;
  loading = true;

  constructor(
    private auth: AuthService,
    private router: Router,
    private userService: UserService
  ) {}

  isAdmin = false;

  ngOnInit(): void {
    this.auth.checkAccess();

    this.auth.events$.pipe(
      filter(e => e.type === 'token_received'),
      first()
    ).subscribe(() => {
      this.fetchUserAndRole();
    });

    if (this.auth.isLoggedIn()) {
      this.fetchUserAndRole();
    }
  }

  private fetchUserAndRole(): void {
    this.checkUser();

    this.userService.getUserRole().subscribe({
      next: (res: any) => {
        this.isAdmin = res.authority === 'ROLE_ADMIN';
      },
      error: (err) => console.error('Error during role fetching', err)
    });
  }

  logout(): void {
    this.auth.logout();
    this.auth.logoutCompletement();
    this.router.navigate(['/auth/login'], { replaceUrl: true });
  }

  goToProfile(): void {
    this.router.navigateByUrl('/profile');
  }

  goToAdmin() {
  this.router.navigate(['/admin']);
  }

  goToVideos(): void {
    this.router.navigateByUrl('/videos/research');
  }

  goToPsychologists() {
  this.router.navigate(['/psychologists']);
  }

  checkUser() {
    const claims: any = this.auth.getIdentityClaims();

    if (!claims) {
      console.error("Impossible de récupérer les infos Google");
      return;
    }

    const userToCreate = {};

    this.userService.getCurrentUser().subscribe({
      next: (user) => {
        this.user = user;

        const isProfileIncomplete = !user.firstName?.trim() || !user.lastName?.trim();

        if (isProfileIncomplete) {
          this.router.navigateByUrl('/questionnaire');
        }

        this.loading = false;
      },
      error: (err) => {
        console.error("Erreur récupération utilisateur", err);
        this.loading = false;
      }
    });
  }
}
