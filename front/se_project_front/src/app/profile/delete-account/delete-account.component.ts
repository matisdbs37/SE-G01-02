import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-delete-account',
  imports: [CommonModule, FormsModule],
  templateUrl: './delete-account.component.html',
  styleUrl: './delete-account.component.css'
})
export class DeleteAccountComponent {
  user = {
    firstName: 'John',
    lastName: 'Doe',
    email: 'john.doe@example.com',
    country: 'France',
    notifications: 'Yes'
  };

  password: string = '';
  confirmationText: string = '';

  errorMessage: string = '';
  successMessage: string = '';

  constructor(private router: Router) {}

  goBackToProfile() {
    this.router.navigate(['/profile/edit_profile']);
  }

  submitDeleteAccount() {
    if (!this.confirmationText) {
      this.successMessage = '';
      this.errorMessage = 'You must type your password.';
      return;
    }
    if (this.confirmationText != "I want to delete my account.") {
      this.successMessage = '';
      this.errorMessage = 'Confirmation text does not match.';
      return;
    }
    this.router.navigate(['/auth/login']);
  }

  logout() {
    this.router.navigate(['/auth/login']);
  }
}
