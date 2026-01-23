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
  // Index of the currently displayed question (-1 means questionnaire not started yet)
  currentQuestionIndex: number = -1;

  // Index of the currently displayed question (-1 means questionnaire not started yet)
  questions: Question[] = QUESTIONS;

  // User's answers to the questionnaire questions
  answers: any[] = [];

  // Error message shown when user tries to proceed without answering
  errorMessage: string = '';

  constructor(private countryService: CountryService, private router: Router, private auth: AuthService, private userService: UserService, private planService: PlanService) {}

  ngOnInit() {
    this.auth.checkAccess(); // Ensure user is authenticated

    // Load countries for the country question
    this.countryService.getCountries().subscribe(countries => {
      const countryQuestion = this.questions.find(q => q.text.includes('Where are you from ?'));
      if (countryQuestion) {
        countryQuestion.options = countries;
      }
    });
  }

  // Start the questionnaire by setting the current question index to 0
  startQuestionnaire() {
    this.currentQuestionIndex = 0;
    // Initialize the answer for the first question
    if (this.answers[this.currentQuestionIndex] === undefined) {
      this.answers[this.currentQuestionIndex] = '';
    }
  }

  // Navigate to the previous question (if it's not the first question)
  previousQuestion() {
    this.errorMessage = ''; // Clear any existing error message
    if (this.currentQuestionIndex > 0) {
      this.currentQuestionIndex--;
    }
  }

  // Navigate to the next question (if the current question is answered)
  nextQuestion() {
    const currentAnswer = this.answers[this.currentQuestionIndex];

    // Validate that the current question has been answered
    if (currentAnswer == undefined || currentAnswer == null) {
      this.errorMessage = "Please provide an answer before proceeding.";
      return;
    }

    // Additional check for text-type questions to ensure non-empty answers
    if (typeof currentAnswer === 'string' && currentAnswer.trim() === '') {
      this.errorMessage = "Please provide an answer before proceeding.";
      return;
    }

    // Clear any existing error message
    this.errorMessage = '';

    // Move to the next question if not at the end
    if (this.currentQuestionIndex < this.questions.length) {
      this.currentQuestionIndex++;

      // Initialize the answer for the next question if not already set
      if (this.answers[this.currentQuestionIndex] === undefined) {
        this.answers[this.currentQuestionIndex] = '';
      }
    }
  }

  // Calculate and return the progress percentage of the questionnaire
  getProgress() {
    if (this.currentQuestionIndex <= 0) {
      return 0;
    }
    else return (this.currentQuestionIndex / (this.questions.length)) * 100;
  }

  // Determine the plan level based on the mental health score
  private determinePlanLevel(score: number): PlanLevel {
    if (score >= 8) {
      return PlanLevel.EASY;
    } else if (score >= 5) {
      return PlanLevel.INTERMEDIATE;
    } else {
      return PlanLevel.ADVANCED;
    }
  }

  // Save the user's answers and create a meditation plan
  save() {
    // Prepare user data to update
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

    // Determine plan level based on mental health score
    const level = this.determinePlanLevel(this.answers[5]);

    // Update user and create plan
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
