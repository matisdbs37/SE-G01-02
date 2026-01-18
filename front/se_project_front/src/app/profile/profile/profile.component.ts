import { Component } from '@angular/core';
import { CommonModule} from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CountryService } from '../services/country.service';
import { Router } from '@angular/router';
import { AuthService } from '../../auth/services/auth.service';
import { UserService } from '../../services/users.service';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-profile',
  imports: [CommonModule, FormsModule],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css'
})

export class ProfileComponent {
  constructor(private countryService: CountryService, private router: Router, private auth: AuthService, private userService: UserService) { }

  activeSection = 'dashboard';
  isEditing = false;

  countries: string[] = [];
  notificationsOptions: string[] = ['Yes', 'No'];

  confirmationText: string = '';

  errorMessage: string = '';
  successMessage: string = '';

  user: any = null;
  editableUser = { ...this.user };

  loading = true;

  ngOnInit() {
    this.loading = true;
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
        console.error('Erreur chargement profil', err);
        this.loading = false;
        this.router.navigate(['/auth/login']);
      }
    });
  }

  setSection(section: string) {
    this.errorMessage = '';
    this.successMessage = '';
    this.activeSection = section;
    this.isEditing = false;
    this.editableUser = { ...this.user };
  }

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

  saveChanges() {
    if (
      !this.editableUser.firstName ||
      !this.editableUser.lastName ||
      !this.editableUser.locale ||
      !this.editableUser.preferences
    ) {
      this.errorMessage = "Fields can't be empty.";
      return 0;
    }

    const changed =
      this.editableUser.firstName !== this.user.firstName ||
      this.editableUser.lastName !== this.user.lastName ||
      this.editableUser.locale !== this.user.locale ||
      this.editableUser.preferences !== this.user.preferences;

    if (!changed) {
      this.errorMessage = 'No changes detected, nothing to update.';
      return 1;
    }

    const updatedData = {
      firstName: this.editableUser.firstName,
      lastName: this.editableUser.lastName,
      locale: this.editableUser.locale,
      preferences: this.editableUser.preferences,
      updatedAt: new Date().toISOString()
    };

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

  logout() {
    this.auth.logout();
    this.auth.logoutCompletement();
    this.router.navigate(['/auth/login'], { replaceUrl: true });
  }

  submitDeleteAccount() {
    if (!this.confirmationText) {
      this.successMessage = '';
      this.errorMessage = 'All fields are required!';
      return;
    }

    if (this.confirmationText !== "I want to delete my account.") {
      this.successMessage = '';
      this.errorMessage = 'Confirmation text does not match.';
      return;
    }

    this.userService.deleteUser().subscribe({
      next: (response) => {
        this.auth.logout();
        this.auth.logoutCompletement();
        this.router.navigate(['/auth/login'], { replaceUrl: true });
      },
      error: (err) => {
        console.error("Erreur suppression utilisateur", err);
        this.successMessage = '';
        this.errorMessage = 'Failed to delete account.';
      }
    });
  }

  getMetricColor(value: number): string {
    const hue = (value / 10) * 120;
    return `hsl(${hue}, 70%, 45%)`;
  }

  getStressColor(value: number): string {
    const hue = 120 - (value / 10) * 120;
    return `hsl(${hue}, 70%, 45%)`;
  }

  back() {
    this.router.navigate(['home']);
  }
}