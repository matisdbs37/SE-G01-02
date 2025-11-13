import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { EditProfileComponent } from "./profile/edit-profile/edit-profile.component";

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [EditProfileComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'se_project_front';
}
