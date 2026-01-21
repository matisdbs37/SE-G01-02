import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: 'auth/login',
    loadComponent: () =>
      import('./auth/components/login/login.component')
        .then(m => m.LoginComponent)
  },

  {
    path: 'questionnaire',
    loadComponent: () =>
      import('./register_questionnaire/questionnaire/questionnaire.component')
        .then(m => m.QuestionnaireComponent)
  },

  {
    path: 'profile',
    loadComponent: () => import('./profile/profile/profile.component').then(m => m.ProfileComponent)
  },

  {
  path: 'home',
  loadComponent: () =>
    import('./home/home.component').then(m => m.HomeComponent)
  },

  {
    path: 'videos/research',
    loadComponent: () => import('./videos/video-research/video-research.component').then(m => m.VideoResearchComponent)
  },
  {
    path: 'videos/detail/:id',
    loadComponent: () => import('./videos/video-detail/video-detail.component').then(m => m.VideoDetailComponent)
  },

  {
    path: 'profile/checkin',
    loadComponent: () => import('./profile/checkin/checkin.component').then(m => m.CheckinComponent)
  },

  {
  path: 'psychologists',
  loadComponent: () =>
    import('./map/map.component').then(m => m.MapComponent)
  },

  {
  path: 'admin',
  loadComponent: () =>
    import('./admin/admin-dashboard.component')
      .then(m => m.AdminDashboardComponent)
  },

  { path: '', redirectTo: 'auth/login', pathMatch: 'full' },

  { path: '**', redirectTo: 'auth/login' }
];
