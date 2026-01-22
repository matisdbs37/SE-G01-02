import { bootstrapApplication } from '@angular/platform-browser';
import { AppComponent } from './app/app.component';
import { appConfig } from './app/app.config';
import { importProvidersFrom } from '@angular/core';
import { provideHttpClient } from '@angular/common/http';
import { provideOAuthClient } from 'angular-oauth2-oidc';

// Bootstrap the Angular application using the standalone API
bootstrapApplication(AppComponent, {
  providers: [
    // Application-wide providers (routing, zone config, etc.)
    ...appConfig.providers,

    // Enable HttpClient for API calls
    provideHttpClient(),

    // Provide OAuth2/OIDC support for authentication
    provideOAuthClient()
  ]
})
// Log any error that occurs during application startup
.catch((err) => console.error(err));