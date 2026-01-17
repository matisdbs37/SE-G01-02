import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../auth/services/auth.service';
import { filter } from 'rxjs';
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
    this.checkUser();
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

  checkUser() {
    this.userService.getCurrentUser().subscribe({
      next: (userData) => {
        // L'utilisateur existe, on le stocke et on arrête le chargement
        this.user = userData;
        this.loading = false;
      },
      error: (err) => {
        if (err.status === 404) {
          // L'utilisateur n'existe pas en BDD -> Direction questionnaire
          this.router.navigateByUrl('/questionnaire');
        } else {
          // Autre erreur (ex: 500 ou 401)
          console.error("Erreur serveur", err);
          this.loading = false;
        }
      }
    });
  }
}
