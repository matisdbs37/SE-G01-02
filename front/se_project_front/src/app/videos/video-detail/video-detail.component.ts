import { Component, OnInit, inject, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { SafeUrlPipe } from '../../pipes/safe-url.pipe';
import { HistoryService, CommentsEntry } from '../../services/history.service';
import { VideoService, Category } from '../../services/video.service';
import { forkJoin, catchError, of } from 'rxjs';
import { UserService } from '../../services/users.service';
import { AuthService } from '../../auth/services/auth.service';

declare var YT: any; // Declare YouTube iFrame API global variable

// Extend comments to include user info and rating
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
  constructor(private router: Router, private videoService: VideoService, private historyService: HistoryService, private userService: UserService, private auth: AuthService) {
    // Retrieve video passed through router state
    this.video = history.state.video;
  }

  // Content video to display with its categories and average rating
  video: any;
  categoryNames: string[] = [];
  globalRating: number = -1;

  // User specific data : rating and comments for this content (initialized to 0 and '')
  userRating = 0;
  newComment = '';

  // All comments for this content
  allComments: CommentWithRating[] = [];
  
  // Array for rendering rating stars
  starsArr = [1, 2, 3, 4, 5];

  // Loading state of the page
  loading = true;

  // YouTube Player instance and progress tracking
  player: any;
  progressInterval: any;
  lastUpdateSeconds = 0;

  ngOnInit() {
    this.auth.checkAccess(); // Ensure user is authenticated

    // Redirect back if no video is provided
    if (!this.video || !this.video.id) {
      this.router.navigate(['/videos/research']);
      return;
    }

    // Load all related data for the video
    this.loadAllContentData();

    // Initialize YouTube Player after a short delay to ensure DOM is ready
    setTimeout(() => {
      this.initYouTubePlayer();
    }, 1000);
  }

  // Load history, categories, mean rating and interactions in parallel
  loadAllContentData() {
    this.loading = true;
    
    forkJoin({
      history: this.historyService.getHistory(0, 50).pipe(catchError(() => of({ content: [] }))),
      categories: this.videoService.getContentCategories(this.video.id).pipe(catchError(() => of([]))),
      meanRating: this.videoService.getMeanRating(this.video.id).pipe(catchError(() => of(-1))),
      interactions: this.videoService.getInteractions(this.video.id).pipe(catchError(() => of([])))
    }).subscribe({
      next: (res) => {
        // Find current user's history entry for this video
        const myEntry = res.history.content.find((h: any) => h.contentId === this.video.id);

        // Find and set user rating for this content if it exists
        if (myEntry && myEntry.rating !== undefined && myEntry.rating !== null && myEntry.rating !== -1) {
          this.userRating = myEntry.rating / 2;
        } else {
          this.userRating = 0; 
        }

        // Set categories and global rating for this content
        this.categoryNames = res.categories.map((c: any) => c.name);
        this.globalRating = res.meanRating !== -1 ? res.meanRating / 2 : -1;

        // Process and display all comments for this content
        this.processComments(res.interactions);
        
        // End of the loading state
        this.loading = false;
      }
    });
  }

  // Process interactions to extract comments with user info and ratings
  processComments(interactions: any[]) {
    const processed: CommentWithRating[] = [];

    // Extract unique user IDs from interactions with comments
    const userIds = [...new Set(interactions
      .filter(i => i.comments && i.comments.length > 0)
      .map(i => i.userId))];

    if (userIds.length === 0) {
      this.allComments = [];
      return;
    }

    // Fetch user details for each unique user ID
    const userRequests = userIds.map(id => 
      this.userService.getUserById(id).pipe(catchError(() => of(null)))
    );

    // Once all user details are fetched, map them to comments
    forkJoin(userRequests).subscribe(users => {
      // Create a map of userId to full name for easy lookup
      const userMap = new Map<string, string>();

      // Populate the user map
      users.forEach(user => {
        if (user && user.id) {
          userMap.set(user.id, `${user.firstName} ${user.lastName}`);
        }
      });

      // Process each interaction to extract comments with user info
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

      // Sort comments by posted date (most recent first)
      this.allComments = processed.sort((a, b) => 
        new Date(b.postedAt).getTime() - new Date(a.postedAt).getTime()
      );
    });
  }

  // Back button
  back() {
    this.router.navigate(['/videos/research']);
  }

  // Set user rating based on star click position (supports half-stars)
  setRating(event: MouseEvent, starIndex: number) {
    const rect = (event.target as HTMLElement).getBoundingClientRect();
    const x = event.clientX - rect.left; 
    const value = x < rect.width / 2 ? starIndex - 0.5 : starIndex;
    this.userRating = value;
    this.historyService.rateVideo(this.video.id, value * 2).subscribe();
  }

  // Add a new comment for this content and reload interactions
  addComment() {
    if (!this.newComment.trim()) return;
    this.historyService.addComment(this.video.id, this.newComment).subscribe({
      next: () => {
        this.newComment = '';
        this.videoService.getInteractions(this.video.id).subscribe(inters => this.processComments(inters));
      }
    });
  }

  // Generate embed URL for YouTube videos
  getEmbedUrl(url: string): string {
    const videoId = url.split('v=')[1]?.split('&')[0];
    return videoId ? `https://www.youtube.com/embed/${videoId}` : url;
  }

  // Initialize YouTube Player and set up event listeners for tracking
  initYouTubePlayer() {
    if (typeof YT !== 'undefined' && YT.Player) {
      this.player = new YT.Player('youtube-player', {
        events: {
          'onStateChange': (event: any) => {
            this.onPlayerStateChange(event);
          }
        }
      });
    }
  }

  // Handle YouTube player state changes to start/stop progress tracking
  onPlayerStateChange(event: any) {
    if (event.data === 1) { // Video is playing
      this.startTracking();
    } else { // Video is paused or ended
      this.stopTracking();
      this.saveProgress();
    }
  }

  // Start periodic progress tracking every 30 seconds
  startTracking() {
    if (this.progressInterval) return;
    this.progressInterval = setInterval(() => {
      this.saveProgress();
    }, 30000);
  }

  // Stop periodic progress tracking
  stopTracking() {
    clearInterval(this.progressInterval);
    this.progressInterval = null;
  }

  // Save current progress if it has advanced since last update
  saveProgress() {
    if (this.player && typeof this.player.getCurrentTime === 'function') {
      const currentTime = Math.floor(this.player.getCurrentTime());
      
      if (currentTime > this.lastUpdateSeconds) {
        this.historyService.updateProgress(this.video.id, currentTime).subscribe({
          next: () => this.lastUpdateSeconds = currentTime,
          error: (err) => console.error('Erreur API Progress', err)
        });
      }
    }
  }

  // Update tracking on component destroy (when user leaves the page)
  ngOnDestroy() {
    this.stopTracking();
    this.saveProgress();
  }

  // Update progress every 10 seconds during audio playback
  onAudioTimeUpdate(currentTime: number) {
    const roundedTime = Math.floor(currentTime);
    if (roundedTime % 1 === 0 && roundedTime !== this.lastUpdateSeconds) {
      this.lastUpdateSeconds = roundedTime;
      this.historyService.updateProgress(this.video.id, roundedTime).subscribe();
    }
  }
}