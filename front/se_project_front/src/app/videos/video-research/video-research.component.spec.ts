import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VideoResearchComponent } from './video-research.component';

describe('VideoResearchComponent', () => {
  let component: VideoResearchComponent;
  let fixture: ComponentFixture<VideoResearchComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VideoResearchComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(VideoResearchComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
