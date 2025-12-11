import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WrongBookComponent } from './wrong-book.component';

describe('WrongBookComponent', () => {
  let component: WrongBookComponent;
  let fixture: ComponentFixture<WrongBookComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [WrongBookComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(WrongBookComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
