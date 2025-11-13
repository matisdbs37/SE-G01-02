import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-reset-password',
  imports: [CommonModule, FormsModule],
  standalone: true,
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.css']
})
export class ResetPasswordComponent {
  user = {
    firstName: 'John',
    lastName: 'Doe',
    email: 'john.doe@example.com',
    country: 'France',
    notifications: 'Yes'
  };

  currentPassword = '';
  newPassword = '';
  confirmNewPassword = '';

  constructor(private router: Router) {}

  submitResetPassword() {
    if (this.newPassword != this.confirmNewPassword) {
      alert('New password and confirmation do not match!');
      return;
    }
    if (this.currentPassword == this.newPassword) {
      alert('New password can\'t be the same as the current password!');
      return;
    }
    alert('Password successfully changed!');
    this.router.navigate(['/edit-profile']);
  }

  goBackToProfile() {
    this.router.navigate(['/edit-profile']);
  }
}
