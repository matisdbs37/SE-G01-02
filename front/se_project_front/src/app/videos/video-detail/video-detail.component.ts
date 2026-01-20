import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { SafeUrlPipe } from '../../pipes/safe-url.pipe';
import { HistoryService, CommentsEntry } from '../../services/history.service';
import { VideoService, Category } from '../../services/video.service';
import { forkJoin, catchError, of } from 'rxjs';
import { UserService } from '../../services/users.service';

interface CommentWithRating extends CommentsEntry {
  userRating?: number;
  userId: string;
  userName?: string;
}

@Component({
  selector: 'app-video-detail',
  standalone: true,
  imports: [CommonModule, FormsModule, SafeUrlPipe],
  templateUrl: './video-detail.component.html',
  styleUrl: './video-detail.component.css'
})
export class VideoDetailComponent {
  constructor(private router: Router, private videoService: VideoService, private historyService: HistoryService, private userService: UserService) {
    this.video = history.state.video;
  }

  video: any;
  categoryNames: string[] = [];
  globalRating: number = -1;

  userRating = 0;
  newComment = '';
  allComments: CommentWithRating[] = [];
  
  starsArr = [1, 2, 3, 4, 5];
  loading = true;

  ngOnInit() {
    if (!this.video || !this.video.id) {
      this.router.navigate(['/videos/research']);
      return;
    }
    this.loadAllContentData();
  }

  loadAllContentData() {
    this.loading = true;
    
    forkJoin({
      history: this.historyService.getHistory(0, 50).pipe(catchError(() => of({ content: [] }))),
      categories: this.videoService.getContentCategories(this.video.id).pipe(catchError(() => of([]))),
      meanRating: this.videoService.getMeanRating(this.video.id).pipe(catchError(() => of(-1))),
      interactions: this.videoService.getInteractions(this.video.id).pipe(catchError(() => of([])))
    }).subscribe({
      next: (res) => {
        const myEntry = res.history.content.find((h: any) => h.contentId === this.video.id);
        if (myEntry && myEntry.rating !== undefined && myEntry.rating !== null && myEntry.rating !== -1) {
          this.userRating = myEntry.rating / 2;
        } else {
          this.userRating = 0; 
        }

        this.categoryNames = res.categories.map((c: any) => c.name);
        this.globalRating = res.meanRating !== -1 ? res.meanRating / 2 : -1;

        this.processComments(res.interactions);
        
        this.loading = false;
      }
    });
  }

  processComments(interactions: any[]) {
    const processed: CommentWithRating[] = [];
    const userIds = [...new Set(interactions
      .filter(i => i.comments && i.comments.length > 0)
      .map(i => i.userId))];

    if (userIds.length === 0) {
      this.allComments = [];
      return;
    }

    const userRequests = userIds.map(id => 
      this.userService.getUserById(id).pipe(catchError(() => of(null)))
    );

    forkJoin(userRequests).subscribe(users => {
      const userMap = new Map<string, string>();
      users.forEach(user => {
        if (user && user.id) {
          userMap.set(user.id, `${user.firstName} ${user.lastName}`);
        }
      });

      interactions.forEach(inter => {
        if (inter.comments && inter.comments.length > 0) {
          const fullName = userMap.get(inter.userId) || "User Deleted";
          inter.comments.forEach((com: CommentsEntry) => {
            processed.push({
              ...com,
              userId: inter.userId,
              userName: fullName,
              userRating: inter.rating && inter.rating !== -1 ? inter.rating / 2 : undefined
            });
          });
        }
      });

      this.allComments = processed.sort((a, b) => 
        new Date(b.postedAt).getTime() - new Date(a.postedAt).getTime()
      );
    });
  }

  back() {
    this.router.navigate(['/videos/research']);
  }

  setRating(event: MouseEvent, starIndex: number) {
    const rect = (event.target as HTMLElement).getBoundingClientRect();
    const x = event.clientX - rect.left; 
    const value = x < rect.width / 2 ? starIndex - 0.5 : starIndex;
    this.userRating = value;
    this.historyService.rateVideo(this.video.id, value * 2).subscribe();
  }

  addComment() {
    if (!this.newComment.trim()) return;
    this.historyService.addComment(this.video.id, this.newComment).subscribe({
      next: () => {
        this.newComment = '';
        this.videoService.getInteractions(this.video.id).subscribe(inters => this.processComments(inters));
      }
    });
  }

  getEmbedUrl(url: string): string {
    const videoId = url.split('v=')[1]?.split('&')[0];
    return videoId ? `https://www.youtube.com/embed/${videoId}` : url;
  }
}