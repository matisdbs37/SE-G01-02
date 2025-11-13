import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class CountryService {
  private apiUrl = 'https://restcountries.com/v3.1/all?fields=name';

  constructor(private http: HttpClient) {}
  
  getCountries(): Observable<string[]> {
    return this.http.get<any[]>(this.apiUrl).pipe(
      map(countries =>
        countries
          .map(c => c.name.common)
          .sort((a: string, b: string) => a.localeCompare(b))
      )
    );
  }
}