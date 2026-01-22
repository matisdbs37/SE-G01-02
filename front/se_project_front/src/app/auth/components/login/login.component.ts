import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'mc-login',
  standalone: true,
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
  imports: [CommonModule]
})
export class LoginComponent {
  // Loading state and error message
  loading = false;
  errorMessage: string | null = null;

  constructor(
    private auth: AuthService,
  ) {}

  // Trigger login with Google OAuth
  loginGoogle(): void {
    this.auth.loginWithGoogle();
  }
}
