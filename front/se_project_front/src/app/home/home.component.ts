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
    const claims: any = this.auth.getIdentityClaims();
    
    if (!claims) {
      console.error("Impossible de récupérer les infos Google");
      return;
    }

    // On prépare l'objet (même si le backend utilise le JWT, 
    // il vaut mieux envoyer un objet vide ou minimal pour éviter les erreurs de parsing)
    const userToCreate = {};

    this.userService.createUser(userToCreate).subscribe({
      next: (response) => {
        // CAS 1 : L'utilisateur vient d'être créé
        console.log("Nouvel utilisateur créé ! Redirection vers le questionnaire.");
        this.user = response;
        this.loading = false;
        this.router.navigateByUrl('/questionnaire');
      },
      error: (err) => {
        if (err.status === 409) {
          // CAS 2 : L'email existe déjà. On ne fait rien, on reste sur Home.
          // On peut éventuellement charger les données de l'user ici si besoin.
          this.loading = false;
        } else {
          // CAS 3 : Une vraie erreur (500, 401, etc.)
          console.error("Erreur technique lors de la création", err);
          this.loading = false;
        }
      }
    });
  }
}
