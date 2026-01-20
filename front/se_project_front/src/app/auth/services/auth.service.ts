import { Injectable, inject } from '@angular/core';
import { OAuthService, AuthConfig } from 'angular-oauth2-oidc';
import { filter } from 'rxjs';
import { UserService } from '../../services/users.service';

export const authCodeFlowConfig: AuthConfig = {
  issuer: 'https://accounts.google.com',
  strictDiscoveryDocumentValidation: false,
  redirectUri: window.location.origin + '/home',
  clientId: '465024361535-6ff2tm4fpaf930rs7bm45mc5cu1v6iqk.apps.googleusercontent.com',
  scope: 'openid profile email',
  showDebugInformation: true,
};

@Injectable({ providedIn: 'root' })
export class AuthService {
  private oauthService = inject(OAuthService);
  private userService = inject(UserService);

  constructor() {
    this.initLogin();
    this.setupLogOnSuccess();
  }

  private initLogin() {
    this.oauthService.configure(authCodeFlowConfig);
    this.oauthService.loadDiscoveryDocumentAndTryLogin();
    this.oauthService.setupAutomaticSilentRefresh();
  }

  private setupLogOnSuccess() {
    this.oauthService.events
      .pipe(filter(e => e.type === 'token_received'))
      .subscribe(() => {
        this.userService.logUserActivity().subscribe({
          error: (err) => console.error('Error during log :', err)
        });
      });
  }

  loginWithGoogle() {
    this.oauthService.initImplicitFlow();
  }

  logout() {
    this.oauthService.logOut();
  }

  get token() {
    return this.oauthService.getIdToken();
  }

  isLoggedIn(): boolean {
    return this.oauthService.hasValidIdToken();
  }

  getIdentityClaims() {
    return this.oauthService.getIdentityClaims();
  }

  public runInitialLoginSequence() {
    return this.oauthService.loadDiscoveryDocumentAndTryLogin();
  }

  get events$() {
    return this.oauthService.events;
  }

  logoutCompletement() {
    this.oauthService.revokeTokenAndLogout();
  }
}