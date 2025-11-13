import { Routes } from '@angular/router';
import { EditProfileComponent } from './profile/edit-profile/edit-profile.component';
import { ResetPasswordComponent } from './profile/reset-password/reset-password.component';

export const routes: Routes = [
    { path: '', redirectTo: 'edit-profile', pathMatch: 'full' },
    { path: 'edit-profile', component: EditProfileComponent },
    { path: 'reset-password', component: ResetPasswordComponent }
];
