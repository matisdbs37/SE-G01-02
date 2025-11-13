import { Component } from '@angular/core';
import {FormBuilder, FormGroup, Validators, ReactiveFormsModule, AbstractControl} from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'mc-register',
  standalone: true,
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss'],
  imports: [CommonModule, ReactiveFormsModule]
})
export class RegisterComponent {

  loading = false;
  form!: FormGroup;

  constructor(
    private fb: FormBuilder,
    private auth: AuthService,
    private router: Router
  ) {
    this.form = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirm: ['', [Validators.required]]
    }, {
      validators: (group) => this.samePassword(group)
    });
  }

  get email() { return this.form.get('email'); }
  get password() { return this.form.get('password'); }
  get confirm() { return this.form.get('confirm'); }

  samePassword(control: AbstractControl) {
    const p = control.get('password')?.value;
    const c = control.get('confirm')?.value;
    return p === c ? null : { mismatch: true };
  }

  submit() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const { email, password } = this.form.value;

    this.loading = true;

    this.auth.register(email!, password!).subscribe({
      next: () => this.router.navigateByUrl('/auth/login'),
      error: (e) => {
        this.loading = false;
        alert(e.message ?? 'Register failed');
      },
      complete: () => this.loading = false
    });
  }

  oauth(provider: 'google' | 'apple' | 'microsoft') {
    this.auth.oauth(provider);
  }
}
