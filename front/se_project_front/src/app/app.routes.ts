import { Routes } from '@angular/router';

export const routes: Routes = [
  // Login page for authentication
  {
    path: 'auth/login',
    loadComponent: () =>
      import('./auth/components/login/login.component')
        .then(m => m.LoginComponent)
  },

  // Questionnaire page for new users
  {
    path: 'questionnaire',
    loadComponent: () =>
      import('./register_questionnaire/questionnaire/questionnaire.component')
        .then(m => m.QuestionnaireComponent)
  },

  // User profile page
  {
    path: 'profile',
    loadComponent: () => import('./profile/profile/profile.component')
      .then(m => m.ProfileComponent)
  },

  // Home page
  {
    path: 'home',
    loadComponent: () =>
      import('./home/home.component').then(m => m.HomeComponent)
  },

  // Video research / library page
  {
    path: 'videos/research',
    loadComponent: () =>
      import('./videos/video-research/video-research.component')
        .then(m => m.VideoResearchComponent)
  },

  // Video detail page (dynamic route with video ID parameter)
  {
    path: 'videos/detail/:id',
    loadComponent: () =>
      import('./videos/video-detail/video-detail.component')
        .then(m => m.VideoDetailComponent)
  },

  // Profile check-in page
  {
    path: 'profile/checkin',
    loadComponent: () =>
      import('./profile/checkin/checkin.component')
        .then(m => m.CheckinComponent)
  },

  // Map page showing psychologists
  {
    path: 'psychologists',
    loadComponent: () =>
      import('./map/map.component').then(m => m.MapComponent)
  },

  // Admin dashboard / management page
  {
    path: 'admin',
    loadComponent: () =>
      import('./admin/admin.component').then(m => m.AdminComponent)
  },

  // Default route: redirect to login
  { path: '', redirectTo: 'auth/login', pathMatch: 'full' },

  // Wildcard route: catch-all redirect to login for undefined routes
  { path: '**', redirectTo: 'auth/login' }
];