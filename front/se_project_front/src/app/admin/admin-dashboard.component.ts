import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserService } from '../services/users.service';

@Component({
  standalone: true,
  selector: 'app-admin-dashboard',
  imports: [CommonModule],
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.scss']
})
export class AdminDashboardComponent {

  constructor(private userService: UserService) {}

  sendEmails() {
    this.userService.triggerEmail().subscribe(() => {
      alert('Emails sent!');
    });
  }
}
