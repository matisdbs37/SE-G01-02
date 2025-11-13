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
  { path: '', redirectTo: 'auth/login', pathMatch: 'full' }
];
