import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { QuestionnaireComponent } from "./register_questionnaire/questionnaire/questionnaire.component";

@Component({
  selector: 'app-root',
  imports: [QuestionnaireComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'se_project_front';
}
