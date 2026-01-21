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

  ngOnInit(): void {
    this.auth.events$.pipe(
      filter(e => e.type === 'token_received'),
      first()
    ).subscribe(() => {
      this.checkUser();
    });

    if (this.auth.isLoggedIn()) {
      this.checkUser();
    }
  }

  logout(): void {
    this.auth.logout();
    this.auth.logoutCompletement();
    this.router.navigate(['/auth/login'], { replaceUrl: true });
  }

  goToProfile(): void {
    this.router.navigateByUrl('/profile');
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

    this.userService.createUser(userToCreate).subscribe({
      next: (response) => {
        this.router.navigateByUrl('/questionnaire');
      },
      error: (err) => {
        if (err.status === 409) {
          this.userService.getCurrentUser().subscribe({
            next: (user) => {
              this.user = user;
              this.loading = false;
            },
            error: (e) => {
              console.error("Erreur récupération utilisateur", e);
              this.loading = false;
            }
          });
        } else {
          console.error("Erreur technique lors de la création", err);
          this.loading = false;
        }
      }
    });
  }
}
