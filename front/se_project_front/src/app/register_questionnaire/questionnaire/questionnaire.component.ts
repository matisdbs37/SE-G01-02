import { Component, OnInit } from '@angular/core';
import { Question, QUESTIONS } from '../data/questions';
import { CommonModule } from '@angular/common';
import { CountryService } from '../services/country.service';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../auth/services/auth.service';
import { UserService } from '../../services/users.service';

@Component({
  selector: 'app-questionnaire',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './questionnaire.component.html',
  styleUrl: './questionnaire.component.css',
})
export class QuestionnaireComponent {
  currentQuestionIndex: number = -1;

  questions: Question[] = QUESTIONS;
  answers: any[] = [];

  errorMessage: string = '';

  constructor(private countryService: CountryService, private router: Router, private auth: AuthService, private userService: UserService) {}

  ngOnInit() {
    this.countryService.getCountries().subscribe(countries => {
      const countryQuestion = this.questions.find(q => q.text.includes('Where are you from ?'));
      if (countryQuestion) {
        countryQuestion.options = countries;
      }
    });
  }

  startQuestionnaire() {
    this.currentQuestionIndex = 0;
    if (this.answers[this.currentQuestionIndex] === undefined) {
      this.answers[this.currentQuestionIndex] = '';
    }
  }

  previousQuestion() {
    this.errorMessage = '';
    if (this.currentQuestionIndex > 0) {
      this.currentQuestionIndex--;
    }
  }

  nextQuestion() {
    const currentAnswer = this.answers[this.currentQuestionIndex];
    console.log(this.answers);
    if (currentAnswer == '' || currentAnswer == undefined || currentAnswer == null) {
      this.errorMessage = "Please provide an answer before proceeding.";
      return;
    }

    this.errorMessage = '';
    if (this.currentQuestionIndex < this.questions.length) {
      this.currentQuestionIndex++;
      if (this.answers[this.currentQuestionIndex] === undefined) {
        this.answers[this.currentQuestionIndex] = '';
      }
    }
  }

  getProgress() {
    if (this.currentQuestionIndex <= 0) {
      return 0;
    }
    else return (this.currentQuestionIndex / (this.questions.length)) * 100;
  }

  save() {
    const claims: any = this.auth.getIdentityClaims();

    const userToUpdate: any = {
      preferences: this.answers[1],
      mental: this.answers[2],
      sleep: this.answers[3],
      stress: this.answers[4],
      meditation: this.answers[5],
      locale: this.answers[0],
      updatedAt: new Date().toISOString()
    };


    this.userService.updateUser(userToUpdate).subscribe({
      next: (response) => {
        this.router.navigate(['/home']);
      },
      error: (err) => {
        console.error("Erreur lors de l'update", err);
      }
    });
  }
}
