import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  API_URL: string = "http://localhost:8080/"
  LOGIN_MATCHER: string = "api/auth/login"
  REGISTER_MATCHER: string = "api/auth/register"
  LOGOUT_MATCHER: string = "logout"

  constructor(private readonly http: HttpClient) { }

  login(email: String, pwd: String): Observable<any> {
    const body = { email, pwd }
    return this.http.post<{ message: String }>(this.API_URL + this.LOGIN_MATCHER, body)
  }

  register(email: String, pwd: String, firstName: String = "Jhon", lastName: String = "DOE"): Observable<any> {
    const body = { email, pwd, firstName, lastName }
    return this.http.post<{ message: String }>(this.API_URL + this.REGISTER_MATCHER, body)
  }

  logout(): Observable<any> {
    localStorage.removeItem('authToken');
    localStorage.removeItem('userEmail');
    localStorage.removeItem('userFirstName');
    localStorage.removeItem('userLastName');
    return this.http.get<{ message: String }>(this.API_URL + this.LOGOUT_MATCHER)
  }

}
