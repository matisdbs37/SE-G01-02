import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CountryService } from '../services/country.service';
import { Router } from '@angular/router';
import { first } from 'rxjs';
import { AuthService } from '../../auth/services/auth.service';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-edit-profile',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './edit-profile.component.html',
  styleUrl: './edit-profile.component.css'
})


export class EditProfileComponent {
  constructor(private countryService: CountryService, private router: Router, private authService: AuthService, private http: HttpClient) { }
  is_editing: boolean = false;

  user = {
    firstName: "",
    lastName: "",
    email: "",
    country: "Croatia",
    notifications: "No"
  };

  notificationsOptions: string[] = ['Yes', 'No'];

  editableUser = { ...this.user };

  countries: string[] = [];

  errorMessage: string = '';

  loadUserData(data: any) {
    console.log(data)
    console.log(localStorage)
    const cachedFirstName = localStorage.getItem('firstName');
    const cachedLastName = localStorage.getItem('lastName');
    const cachedEmail = localStorage.getItem('email');
    console.log("test2")

    if (cachedFirstName && cachedEmail && cachedLastName) {
      this.user.firstName = cachedFirstName;
      this.user.lastName = cachedLastName;
      this.user.email = cachedEmail;
      this.editableUser = { ...this.user }
    } else {
      this.user.firstName = data.attributes.given_name
      this.user.lastName = data.attributes.family_name;
      this.user.email = data.attributes.email;
      localStorage.setItem('firstName', data.firstName);
      localStorage.setItem('lastName', data.lastName);
      this.editableUser = { ...this.user }
    }
    console.log(this.user)
  }

  ngOnInit() {
    setTimeout(() => {
      this.checkAuthAndLoad();
    }, 1500);
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
    this.authService.updateUser(this.user.email, this.user.firstName, this.user.lastName).subscribe()
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

  checkAuthAndLoad(): any {
    this.http.get('http://localhost:8080/user/me', { withCredentials: true })
      .subscribe({
        next: (data) => {
          console.log('✅ Utilisateur authentifié:', data);
          this.loadUserData(data)
        },
        error: (error) => {
          console.log('❌ Non authentifié, redirection vers Google...');
          window.location.href = 'http://localhost:8080/oauth2/authorization/google';
        }
      });
  }
}
