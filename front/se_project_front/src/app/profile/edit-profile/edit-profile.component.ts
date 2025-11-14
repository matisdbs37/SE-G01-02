import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CountryService } from '../services/country.service';
import { Router } from '@angular/router';
import { first } from 'rxjs';

@Component({
  selector: 'app-edit-profile',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './edit-profile.component.html',
  styleUrl: './edit-profile.component.css'
})
export class EditProfileComponent {
  is_editing: boolean = false;

  user = {
    firstName: localStorage.getItem('firstName'),
    lastName: localStorage.getItem('lastName'),
    email: localStorage.getItem('email'),
    country: 'France',
    notifications: 'Yes'
  };

  notificationsOptions: string[] = ['Yes', 'No'];

  editableUser = { ...this.user };

  countries: string[] = [];

  errorMessage: string = '';

  constructor(private countryService: CountryService, private router: Router) { }

  ngOnInit() {
    this.countryService.getCountries().subscribe(countries => {
      this.countries = countries;
    });
  }

  toggleEdit() {
    this.is_editing = !this.is_editing;
    this.editableUser = { ...this.user };
  }

  saveChanges() {
    if (!this.editableUser.firstName || !this.editableUser.lastName || !this.editableUser.email || !this.editableUser.country || !this.editableUser.notifications) {
      this.errorMessage = "Fields can't be empty.";
      return;
    }
    this.errorMessage = '';
    this.is_editing = false;
    this.user = { ...this.editableUser };
    console.log("User updated : ", this.user);
  }

  resetPassword() {
    this.router.navigate(['/profile/reset_password']);
  }

  goToDelete() {
    this.router.navigate(['/profile/delete_account']);
  }

  logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('email');
    localStorage.removeItem('firstName');
    localStorage.removeItem('lastName');
    this.router.navigate(['/auth/login']);
  }
}
