import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { SafeUrlPipe } from '../../pipes/safe-url.pipe';

@Component({
  selector: 'app-video-detail',
  standalone: true,
  imports: [CommonModule, FormsModule, SafeUrlPipe],
  templateUrl: './video-detail.component.html',
  styleUrl: './video-detail.component.css'
})
export class VideoDetailComponent {

  video: any;

  userRating = 0;
  newComment = '';

  comments = [
    { user: 'Alice', text: 'Very relaxing!', rating: 5 },
    { user: 'Tom', text: 'Good session, I feel calmer.', rating: 4 },
    { user: 'Sarah', text: 'Could focus better next time.', rating: 3 },
    { user: 'Mike', text: 'Perfect background for meditation.', rating: 5 },
    { user: 'Anna', text: 'Too short, wanted more minutes.', rating: 4 },
    { user: 'John', text: 'Nice audio quality.', rating: 4 },
    { user: 'Emma', text: 'Loved the guidance voice.', rating: 5 },
    { user: 'Liam', text: 'Background music was soothing.', rating: 4 },
    { user: 'Olivia', text: 'Relaxing but a bit repetitive.', rating: 3 },
    { user: 'Noah', text: 'Felt sleepy in a good way.', rating: 5 },
    { user: 'Ava', text: 'Great meditation, helped me focus.', rating: 5 },
    { user: 'Ethan', text: 'I wish it was longer.', rating: 3 },
    { user: 'Sophia', text: 'Good intro and outro.', rating: 4 },
    { user: 'Mason', text: 'Easy to follow along.', rating: 5 },
    { user: 'Isabella', text: 'Background sound a little loud.', rating: 3 },
    { user: 'Lucas', text: 'Perfect before bedtime.', rating: 5 },
    { user: 'Mia', text: 'Felt refreshed after the session.', rating: 4 },
    { user: 'Oliver', text: 'Guided meditation was clear.', rating: 5 },
    { user: 'Charlotte', text: 'Could add more breathing exercises.', rating: 4 },
    { user: 'Elijah', text: 'Great for stress relief.', rating: 5 },
    { user: 'Amelia', text: 'Very calm and soothing.', rating: 5 },
    { user: 'Benjamin', text: 'Perfect length for a break.', rating: 4 },
    { user: 'Harper', text: 'Loved it!', rating: 5 },
    { user: 'James', text: 'Nice pacing and guidance.', rating: 4 },
    { user: 'Evelyn', text: 'Good variety of sessions.', rating: 5 },
    { user: 'William', text: 'Excellent meditation quality.', rating: 5 },
    { user: 'Abigail', text: 'I felt relaxed after this.', rating: 5 },
    { user: 'Henry', text: 'Helpful and calming.', rating: 5 },
    { user: 'Emily', text: 'Background music is nice.', rating: 4 },
    { user: 'Alexander', text: 'Very professional audio.', rating: 5 }
  ];

  constructor(private router: Router) {
    this.video = history.state.video;
  }

  back() {
    this.router.navigate(['/videos/research']);
  }

  setRating(r: number) {
    this.userRating = r;
  }

  addComment() {
    if (!this.newComment.trim()) return;

    this.comments.unshift({
      user: 'You',
      text: this.newComment,
      rating: this.userRating
    });

    this.newComment = '';
  }
}
