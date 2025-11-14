import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, of, throwError, delay, timeout } from 'rxjs';



@Injectable({
  providedIn: 'root'
})
export class AuthService {

  API_URL: string = "http://localhost:8080/"
  LOGIN_MATCHER: string = "api/auth/login"
  REGISTER_MATCHER: string = "api/auth/register"
  LOGOUT_MATCHER: string = "logout"
  USER_MATCHER: String = "user/me"

  constructor(private readonly http: HttpClient) { }

  login(email: String, password: String): Observable<any> {
    const body = { email, password }
    return this.http.post<{ message: String }>(this.API_URL + this.LOGIN_MATCHER, body)

  }

  register(email: String, password: String, firstName: String = "Jhon", lastName: String = "DOE"): Observable<any> {
    const body = { email, password, firstName, lastName }
    return this.http.post<{ message: String }>(this.API_URL + this.REGISTER_MATCHER, body)
  }

  logout(): Observable<any> {
    localStorage.removeItem('authToken');
    localStorage.removeItem('userEmail');
    localStorage.removeItem('userFirstName');
    localStorage.removeItem('userLastName');
    return this.http.get<{ message: String }>(this.API_URL + this.LOGOUT_MATCHER)
  }

  oauth(): void {
     window.location.href = 'http://localhost:8080/login';

  }

  getUser():Observable<any>{
    return this.http.get<{ body: JSON }>(this.API_URL + this.USER_MATCHER)
  }

}


