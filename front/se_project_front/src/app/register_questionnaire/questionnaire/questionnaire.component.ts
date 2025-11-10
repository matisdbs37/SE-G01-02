import { Component } from '@angular/core';
import { Question, QUESTIONS } from '../data/questions';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-questionnaire',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './questionnaire.component.html',
  styleUrl: './questionnaire.component.css'
})
export class QuestionnaireComponent {
  currentQuestionIndex: number = -1;

  questions: Question[] = QUESTIONS;

  startQuestionnaire() {
    this.currentQuestionIndex = 0;
  }

  nextQuestion() {
    if (this.currentQuestionIndex < this.questions.length) {
      this.currentQuestionIndex++;
    }
  }

  getProgress() {
    if (this.currentQuestionIndex <= 0) {
      return 0;
    }
    else return (this.currentQuestionIndex / (this.questions.length)) * 100;
  }
}
