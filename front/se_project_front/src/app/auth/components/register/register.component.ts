import { Component } from '@angular/core';
import {
  AbstractControl,
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';
import { Router } from '@angular/router';
import { RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService, MockUser } from '../../services/auth.service';

@Component({
  selector: 'mc-register',
  standalone: true,
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss'],
  imports: [CommonModule, ReactiveFormsModule, RouterLink]
})
export class RegisterComponent {

  form: FormGroup;
  loading = false;
  errorMessage: string | null = null;

  constructor(
    private fb: FormBuilder,
    private auth: AuthService,
    private router: Router
  ) {
    this.form = this.fb.group({
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],

      password: ['', [Validators.required, Validators.minLength(6)]],
      confirm: ['', Validators.required],

      stressLevel: [3, [Validators.required, Validators.min(1), Validators.max(5)]],
      goal: ['', Validators.required],

      acceptTerms: [false, Validators.requiredTrue]
    }, {
      validators: this.samePassword
    });
  }

  // -------------------------
  // Getters
  // -------------------------
  get f() {
    return this.form.controls;
  }

  // -------------------------
  // Validators
  // -------------------------
  samePassword(control: AbstractControl) {
    const password = control.get('password')?.value;
    const confirm = control.get('confirm')?.value;
    return password === confirm ? null : { mismatch: true };
  }

  // -------------------------
  // Submit
  // -------------------------
  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.loading = true;
    this.errorMessage = null;

    const user: MockUser = {
      email: this.f['email'].value,
      password: this.f['password'].value,
      firstName: this.f['firstName'].value,
      lastName: this.f['lastName'].value,
      stressLevel: this.f['stressLevel'].value,
      goal: this.f['goal'].value
    };

    this.auth.register(user.email, user.password, user.firstName, user.lastName).subscribe({
      next: () => {
        this.router.navigateByUrl('/questionnaire');
      },
      error: (err) => {
        this.errorMessage = err.message ?? 'Registration failed';
        this.loading = false;
      },
      complete: () => this.loading = false
    });
  }
}
