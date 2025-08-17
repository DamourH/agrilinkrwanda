import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { FarmerProduceDetailComponent } from './farmer-produce-detail.component';

describe('FarmerProduce Management Detail Component', () => {
  let comp: FarmerProduceDetailComponent;
  let fixture: ComponentFixture<FarmerProduceDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FarmerProduceDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./farmer-produce-detail.component').then(m => m.FarmerProduceDetailComponent),
              resolve: { farmerProduce: () => of({ id: 14315 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(FarmerProduceDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FarmerProduceDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load farmerProduce on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', FarmerProduceDetailComponent);

      // THEN
      expect(instance.farmerProduce()).toEqual(expect.objectContaining({ id: 14315 }));
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
