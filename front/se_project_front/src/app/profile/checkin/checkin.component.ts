import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Question, QUESTIONS } from '../data/questions';
import { Router } from '@angular/router';
import { UserService } from '../../services/users.service';
import { PlanService, PlanLevel } from '../../services/plan.service';
import { switchMap } from 'rxjs';
import { AuthService } from '../../auth/services/auth.service';

@Component({
  selector: 'app-checkin',
  imports: [CommonModule, FormsModule],
  templateUrl: './checkin.component.html',
  styleUrl: './checkin.component.css'
})
export class CheckinComponent {
  // Index of the currently displayed question (-1 means questionnaire not started yet)
  currentQuestionIndex: number = -1;

  // Array of all questions in the questionnaire
  questions: Question[] = QUESTIONS;

  // User's answers to the questionnaire questions
  answers: any[] = [];

  // Error message shown when user tries to proceed without answering
  errorMessage: string = '';

  constructor(private router: Router, private userService: UserService, private planService: PlanService, private auth: AuthService) {}

  ngOnInit(): void {
    this.auth.checkAccess(); // Ensure user is authenticated
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
    this.errorMessage = '';
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

    // Additional check for string type answers to avoid empty strings
    if (typeof currentAnswer === 'string' && currentAnswer.trim() === '') {
      this.errorMessage = "Please provide an answer before proceeding.";
      return;
    }

    // Clear any existing error message
    this.errorMessage = '';

    // Move to the next question if not at the end
    if (this.currentQuestionIndex < this.questions.length) {
      this.currentQuestionIndex++;

      // Initialize the answer for the new current question
      if (this.answers[this.currentQuestionIndex] === undefined) {
        this.answers[this.currentQuestionIndex] = '';
      }
    }
  }

  // Calculate and return the progress percentage through the questionnaire
  getProgress() {
    if (this.currentQuestionIndex <= 0) {
      return 0;
    }
    else return (this.currentQuestionIndex / (this.questions.length)) * 100;
  }

  // Determine the plan level based on the mental health score
  private determinePlanLevel(score: number): PlanLevel {
    if (score >= 8) return PlanLevel.EASY;
    if (score >= 5) return PlanLevel.INTERMEDIATE;
    return PlanLevel.ADVANCED;
  }

  // Save the user's answers and create a meditation plan
  save() {
    // Prepare user data to update
    const userToUpdate: any = {
      mental: this.answers[0],
      sleep: this.answers[1],
      stress: this.answers[2],
      meditation: this.answers[3],
      updatedAt: new Date().toISOString()
    };

    // Determine plan level based on mental health score
    const level = this.determinePlanLevel(this.answers[0]);

    // Update user and create plan
    this.userService.updateUser(userToUpdate).pipe(
      switchMap(() => {
        return this.planService.createPlan(level);
      })
    ).subscribe({
      next: (response) => {
        this.router.navigate(['/profile']);
      },
      error: (err) => {
        console.error("Error in plan process :", err);
        this.errorMessage = "An error occurred while creating your plan. Please try again later.";
      }
    });
  }
}
