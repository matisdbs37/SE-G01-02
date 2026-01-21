import { Component, OnInit } from '@angular/core';
import { Question, QUESTIONS } from '../data/questions';
import { CommonModule } from '@angular/common';
import { CountryService } from '../services/country.service';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../auth/services/auth.service';
import { UserService } from '../../services/users.service';
import { PlanService, PlanLevel } from '../../services/plan.service';
import { switchMap } from 'rxjs';

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

  constructor(private countryService: CountryService, private router: Router, private auth: AuthService, private userService: UserService, private planService: PlanService) {}

  ngOnInit() {
    this.auth.checkAccess();

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
    if (currentAnswer == undefined || currentAnswer == null) {
      this.errorMessage = "Please provide an answer before proceeding.";
      return;
    }

    if (typeof currentAnswer === 'string' && currentAnswer.trim() === '') {
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

  private determinePlanLevel(score: number): PlanLevel {
    if (score >= 8) {
      return PlanLevel.EASY;
    } else if (score >= 5) {
      return PlanLevel.INTERMEDIATE;
    } else {
      return PlanLevel.ADVANCED;
    }
  }

  save() {
    const claims: any = this.auth.getIdentityClaims();

    const userToUpdate: any = {
      firstName: this.answers[0],
      lastName: this.answers[1],
      preferences: this.answers[4],
      mental: this.answers[5],
      sleep: this.answers[6],
      stress: this.answers[7],
      meditation: this.answers[8],
      locale: this.answers[2],
      city: this.answers[3],
      updatedAt: new Date().toISOString()
    };

    const level = this.determinePlanLevel(this.answers[5]);

    this.userService.updateUser(userToUpdate).pipe(
      switchMap(() => {
        return this.planService.createPlan(level);
      })
    ).subscribe({
      next: (planResponse) => {
        this.router.navigate(['/home']);
      },
      error: (err) => {
        console.error("Error in plan process :", err);
        this.errorMessage = "An error occurred while creating your plan. Please try again later.";
      }
    });
  }
}
