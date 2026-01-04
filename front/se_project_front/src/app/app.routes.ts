import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: 'auth/login',
    loadComponent: () => import('./auth/components/login/login.component').then(m => m.LoginComponent)
  },
  {
    path: 'auth/register',
    loadComponent: () => import('./auth/components/register/register.component').then(m => m.RegisterComponent)
  },
  {
    path: 'questionnaire',
    loadComponent: () => import('./register_questionnaire/questionnaire/questionnaire.component').then(m => m.QuestionnaireComponent)
  },
  {
    path: 'profile/edit_profile',
    loadComponent: () => import('./profile/edit-profile/edit-profile.component').then(m => m.EditProfileComponent)
  },
  {
    path: 'profile/reset_password',
    loadComponent: () => import('./profile/reset-password/reset-password.component').then(m => m.ResetPasswordComponent)
  },
  {
    path: 'profile/delete_account',
    loadComponent: () => import('./profile/delete-account/delete-account.component').then(m => m.DeleteAccountComponent)
  },
  {
    path: 'videos/research',
    loadComponent: () => import('./videos/video-research/video-research.component').then(m => m.VideoResearchComponent)
  },
  {
    path: 'videos/detail/:id',
    loadComponent: () => import('./videos/video-detail/video-detail.component').then(m => m.VideoDetailComponent)
  },
  { path: '', redirectTo: 'auth/login', pathMatch: 'full' }
];
