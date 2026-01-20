import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Question, QUESTIONS } from '../data/questions';
import { Router } from '@angular/router';
import { UserService } from '../../services/users.service';
import { PlanService, PlanLevel } from '../../services/plan.service';
import { switchMap } from 'rxjs';

@Component({
  selector: 'app-checkin',
  imports: [CommonModule, FormsModule],
  templateUrl: './checkin.component.html',
  styleUrl: './checkin.component.css'
})
export class CheckinComponent {
  currentQuestionIndex: number = -1;

  questions: Question[] = QUESTIONS;
  answers: any[] = [];

  errorMessage: string = '';

  constructor(private router: Router, private userService: UserService, private planService: PlanService) {}

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
    if (score >= 8) return PlanLevel.EASY;
    if (score >= 5) return PlanLevel.INTERMEDIATE;
    return PlanLevel.ADVANCED;
  }

  save() {
    const userToUpdate: any = {
      mental: this.answers[0],
      sleep: this.answers[1],
      stress: this.answers[2],
      meditation: this.answers[3],
      updatedAt: new Date().toISOString()
    };

    const level = this.determinePlanLevel(this.answers[0]);

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
