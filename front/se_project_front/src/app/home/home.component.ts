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
    console.log("1. ngOnInit : On attend que le token arrive...");

    // CONDITION A : Pour le premier login (on attend l'événement)
    this.auth.events$.pipe(
      filter(e => e.type === 'token_received'),
      first() // Importe 'first' de 'rxjs' pour ne le faire qu'une fois
    ).subscribe(() => {
      console.log("2. Événement reçu ! Le token est là, on appelle le back.");
      this.checkUser();
    });

    // CONDITION B : Si on rafraîchit la page (le token est déjà là)
    if (this.auth.isLoggedIn()) {
      console.log("2bis. Déjà logué (F5), on appelle le back.");
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

  checkUser() {
    // 1. On récupère les infos Google
    const claims: any = this.auth.getIdentityClaims();
    
    if (!claims) {
      console.error("Impossible de récupérer les infos Google");
      return;
    }

    // 2. On prépare l'objet User selon le format JSON attendu
    const userToCreate: any = {
      email: claims.email,
      firstName: claims.given_name,
      lastName: claims.family_name,
      isActive: true,
      locale: claims.locale || 'fr',
      preferences: "default",
      mental: 10,
      sleep: 10,
      stress: 10,
      meditation: 10,
      city: "Non renseignée",
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString()
    };

    console.log("Tentative de création de l'utilisateur...", userToCreate);

    // 3. On appelle le PUT create
    this.userService.createUser(userToCreate).subscribe({
      next: (response) => {
        console.log("Utilisateur créé avec succès !", response);
        this.user = response;
        this.loading = false;
      },
      error: (err) => {
        console.error("Erreur lors de la création de l'utilisateur", err);
        // Si même le CREATE renvoie 401, c'est que le problème de jeton persiste
        this.loading = false;
      }
    });
  }
}
