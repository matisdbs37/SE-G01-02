import { Injectable, inject } from '@angular/core';
import { OAuthService, AuthConfig } from 'angular-oauth2-oidc';
import { filter } from 'rxjs';
import { UserService } from '../../services/users.service';
import { Router } from '@angular/router';

// Configuration for OAuth2 Code Flow with Google as the identity provider
export const authCodeFlowConfig: AuthConfig = {
  issuer: 'https://accounts.google.com', // Google's OAuth 2.0 authorization server
  strictDiscoveryDocumentValidation: false, // Disable strict validation for discovery document
  redirectUri: window.location.origin + '/home', // Redirect URI after login
  clientId: '465024361535-6ff2tm4fpaf930rs7bm45mc5cu1v6iqk.apps.googleusercontent.com', // Google OAuth 2.0 Client ID
  scope: 'openid profile email', // Scopes for user information
  showDebugInformation: true, // Enable debug information
};

@Injectable({ providedIn: 'root' })
export class AuthService {
  private oauthService = inject(OAuthService);
  private userService = inject(UserService);
  private router = inject(Router);

  constructor() {
    this.initLogin();
    this.setupLogOnSuccess();
  }

  // Initialize OAuth2 login configuration
  private initLogin() {
    this.oauthService.configure(authCodeFlowConfig);
    this.oauthService.loadDiscoveryDocumentAndTryLogin();
    this.oauthService.setupAutomaticSilentRefresh();
  }

  // Set up logging of user activity upon successful token reception
  private setupLogOnSuccess() {
    this.oauthService.events
      .pipe(filter(e => e.type === 'token_received'))
      .subscribe(() => {
        this.userService.logUserActivity().subscribe({
          error: (err) => console.error('Error during log :', err)
        });
      });
  }

  // Initiate login with Google
  loginWithGoogle() {
    this.oauthService.initImplicitFlow();
  }

  // Logout the user
  logout() {
    this.oauthService.logOut();
  }

  // Get the ID token of the authenticated user
  get token() {
    return this.oauthService.getIdToken();
  }

  // Check if the user is currently logged in
  isLoggedIn(): boolean {
    return this.oauthService.hasValidIdToken();
  }

  // Retrieve identity claims of the authenticated user
  getIdentityClaims() {
    return this.oauthService.getIdentityClaims();
  }

  // Run the initial login sequence
  public runInitialLoginSequence() {
    return this.oauthService.loadDiscoveryDocumentAndTryLogin();
  }

  // Observable stream of OAuth events
  get events$() {
    return this.oauthService.events;
  }

  // Complete logout by revoking tokens
  logoutCompletement() {
    this.oauthService.revokeTokenAndLogout();
  }

  // Check access and redirect to login if not authenticated
  checkAccess(): void {
    if (!this.isLoggedIn()) {
      this.router.navigate(['/auth/login']);
    }
  }
}