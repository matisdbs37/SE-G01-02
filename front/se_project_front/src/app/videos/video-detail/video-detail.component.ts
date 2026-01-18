import { Component, OnInit, inject } from '@angular/core'; // Ajout de OnInit et inject
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { SafeUrlPipe } from '../../pipes/safe-url.pipe';
import { HistoryService, HistoryEntry, CommentsEntry } from '../../services/history.service';

@Component({
  selector: 'app-video-detail',
  standalone: true,
  imports: [CommonModule, FormsModule, SafeUrlPipe],
  templateUrl: './video-detail.component.html',
  styleUrl: './video-detail.component.css'
})
export class VideoDetailComponent implements OnInit {
  private historyService = inject(HistoryService);
  private router = inject(Router);

  video: any;
  userRating = 0; // Note sur 5 (ex: 3.5)
  newComment = '';
  comments: CommentsEntry[] = [];
  
  // Pour l'affichage des étoiles
  starsArr = [1, 2, 3, 4, 5];

  constructor() {
    this.video = history.state.video;
  }

  ngOnInit() {
    if (this.video && this.video.id) {
      this.loadUserHistory();
    }
  }

  loadUserHistory() {
    // On récupère l'historique pour trouver la note et les coms de cette vidéo
    this.historyService.getHistory(0, 50).subscribe({
      next: (page) => {
        const entry = page.content.find(h => h.videoId === this.video.id);
        if (entry) {
          // Conversion note 10 -> note 5
          this.userRating = entry.rating ? entry.rating / 2 : 0;
          this.comments = entry.comments || [];
        }
      }
    });
  }

  back() {
    this.router.navigate(['/videos/research']);
  }

  // Gestion des demi-étoiles au clic
  setRating(event: MouseEvent, starIndex: number) {
    const rect = (event.target as HTMLElement).getBoundingClientRect();
    const x = event.clientX - rect.left; 
    // Si on clique sur la moitié gauche de l'étoile -> .5, sinon entier
    const value = x < rect.width / 2 ? starIndex - 0.5 : starIndex;
    
    this.userRating = value;

    // Envoi au backend (note * 2 pour atteindre l'échelle 0-10)
    this.historyService.rateVideo(this.video.id, value * 2).subscribe();
  }

  addComment() {
    if (!this.newComment.trim()) return;

    this.historyService.addComment(this.video.id, this.newComment).subscribe({
      next: (updatedEntry) => {
        this.comments = updatedEntry.comments;
        this.newComment = '';
      }
    });
  }
}