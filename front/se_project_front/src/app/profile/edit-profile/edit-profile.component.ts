import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CountryService } from '../services/country.service';
import { Router } from '@angular/router';

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
    firstName: 'John',
    lastName: 'Doe',
    email: 'john.doe@example.com',
    country: 'France',
    notifications: 'Yes'
  };

  notificationsOptions: string[] = ['Yes', 'No'];

  editableUser = { ...this.user };

  countries: string[] = [];

  constructor(private countryService: CountryService, private router: Router) {}

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
    this.is_editing = false;
    this.user = { ...this.editableUser };
    console.log("User updated : ", this.user);
  }

  resetPassword() {
    this.router.navigate(['/reset-password']);
  }
}
