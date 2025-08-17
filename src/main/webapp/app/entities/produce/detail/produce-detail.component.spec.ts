import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { ProduceDetailComponent } from './produce-detail.component';

describe('Produce Management Detail Component', () => {
  let comp: ProduceDetailComponent;
  let fixture: ComponentFixture<ProduceDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProduceDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./produce-detail.component').then(m => m.ProduceDetailComponent),
              resolve: { produce: () => of({ id: 792 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(ProduceDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ProduceDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load produce on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', ProduceDetailComponent);

      // THEN
      expect(instance.produce()).toEqual(expect.objectContaining({ id: 792 }));
    });
  });

  describe('PreviousState', () => {
    it('should navigate to previous state', () => {
      jest.spyOn(window.history, 'back');
      comp.previousState();
      expect(window.history.back).toHaveBeenCalled();
    });
  });
});
