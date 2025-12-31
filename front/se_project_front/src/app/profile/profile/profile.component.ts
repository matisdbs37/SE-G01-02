import { Component } from '@angular/core';
import { CommonModule} from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CountryService } from '../services/country.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-profile',
  imports: [CommonModule, FormsModule],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css'
})

export class ProfileComponent {
  constructor(private countryService: CountryService, private router: Router) { }

  activeSection = 'dashboard';
  isEditing = false;

  countries: string[] = [];
  notificationsOptions: string[] = ['Yes', 'No'];

  delete_account_password: string = '';
  confirmationText: string = '';

  currentPassword: string = '';
  newPassword: string = '';
  confirmNewPassword: string = '';

  errorMessage: string = '';
  successMessage: string = '';

  mentalHealth: number = 1;
  sleepQuality: number = 4;
  stressLevel: number = 10;
  meditationExperience: number = 9;

  user = {
    firstName: 'John',
    lastName: 'Doe',
    email: 'john@doe.com',
    country: 'Croatia',
    notifications: 'No'
  };

  editableUser = { ...this.user };

  ngOnInit() {
    this.countryService.getCountries().subscribe(countries => {
      this.countries = countries;
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
    let isWorking = 1;
    if (this.isEditing) {
      isWorking = this.saveChanges();
    }
    if (isWorking == 1) {
      this.isEditing = !this.isEditing;
    }
  }

  saveChanges() {
    if (!this.editableUser.firstName || !this.editableUser.lastName || !this.editableUser.email || !this.editableUser.country || !this.editableUser.notifications) {
      this.errorMessage = "Fields can't be empty.";
      return 0;
    }
    this.errorMessage = '';
    this.user = { ...this.editableUser };
    console.log("User updated : ", this.user);
    return 1;
  }

  logout() {
    this.router.navigate(['/auth/login']);
  }

  submitDeleteAccount() {
    if (!this.confirmationText || !this.delete_account_password) {
      this.successMessage = '';
      this.errorMessage = 'All fields are required!';
      return;
    }
    if (this.confirmationText != "I want to delete my account.") {
      this.successMessage = '';
      this.errorMessage = 'Confirmation text does not match.';
      return;
    }
    this.router.navigate(['/auth/login']);
  }

  submitResetPassword() {
    if (!this.currentPassword || !this.newPassword || !this.confirmNewPassword) {
      this.successMessage = '';
      this.errorMessage = 'All fields are required!';
      return;
    }
    if (this.newPassword != this.confirmNewPassword) {
      this.successMessage = '';
      this.errorMessage = 'New password and confirmation do not match!';
      return;
    }
    if (this.currentPassword == this.newPassword) {
      this.successMessage = '';
      this.errorMessage = 'New password can\'t be the same as the current password!';
      return;
    }
    this.errorMessage = '';
    this.successMessage = 'Password successfully changed!';
  }

  getMetricColor(value: number): string {
    const hue = (value / 10) * 120;
    return `hsl(${hue}, 70%, 45%)`;
  }

  getStressColor(value: number): string {
    const hue = 120 - (value / 10) * 120;
    return `hsl(${hue}, 70%, 45%)`;
  }
}