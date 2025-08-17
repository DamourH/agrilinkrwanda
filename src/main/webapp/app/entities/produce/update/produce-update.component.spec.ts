import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IProductCategory } from 'app/entities/product-category/product-category.model';
import { ProductCategoryService } from 'app/entities/product-category/service/product-category.service';
import { ProduceService } from '../service/produce.service';
import { IProduce } from '../produce.model';
import { ProduceFormService } from './produce-form.service';

import { ProduceUpdateComponent } from './produce-update.component';

describe('Produce Management Update Component', () => {
  let comp: ProduceUpdateComponent;
  let fixture: ComponentFixture<ProduceUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let produceFormService: ProduceFormService;
  let produceService: ProduceService;
  let productCategoryService: ProductCategoryService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ProduceUpdateComponent],
      providers: [
        provideHttpClient(),
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(ProduceUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ProduceUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    produceFormService = TestBed.inject(ProduceFormService);
    produceService = TestBed.inject(ProduceService);
    productCategoryService = TestBed.inject(ProductCategoryService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call ProductCategory query and add missing value', () => {
      const produce: IProduce = { id: 1992 };
      const category: IProductCategory = { id: 29286 };
      produce.category = category;

      const productCategoryCollection: IProductCategory[] = [{ id: 29286 }];
      jest.spyOn(productCategoryService, 'query').mockReturnValue(of(new HttpResponse({ body: productCategoryCollection })));
      const additionalProductCategories = [category];
      const expectedCollection: IProductCategory[] = [...additionalProductCategories, ...productCategoryCollection];
      jest.spyOn(productCategoryService, 'addProductCategoryToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ produce });
      comp.ngOnInit();

      expect(productCategoryService.query).toHaveBeenCalled();
      expect(productCategoryService.addProductCategoryToCollectionIfMissing).toHaveBeenCalledWith(
        productCategoryCollection,
        ...additionalProductCategories.map(expect.objectContaining),
      );
      expect(comp.productCategoriesSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const produce: IProduce = { id: 1992 };
      const category: IProductCategory = { id: 29286 };
      produce.category = category;

      activatedRoute.data = of({ produce });
      comp.ngOnInit();

      expect(comp.productCategoriesSharedCollection).toContainEqual(category);
      expect(comp.produce).toEqual(produce);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProduce>>();
      const produce = { id: 792 };
      jest.spyOn(produceFormService, 'getProduce').mockReturnValue(produce);
      jest.spyOn(produceService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ produce });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: produce }));
      saveSubject.complete();

      // THEN
      expect(produceFormService.getProduce).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(produceService.update).toHaveBeenCalledWith(expect.objectContaining(produce));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProduce>>();
      const produce = { id: 792 };
      jest.spyOn(produceFormService, 'getProduce').mockReturnValue({ id: null });
      jest.spyOn(produceService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ produce: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: produce }));
      saveSubject.complete();

      // THEN
      expect(produceFormService.getProduce).toHaveBeenCalled();
      expect(produceService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProduce>>();
      const produce = { id: 792 };
      jest.spyOn(produceService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ produce });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(produceService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareProductCategory', () => {
      it('should forward to productCategoryService', () => {
        const entity = { id: 29286 };
        const entity2 = { id: 19244 };
        jest.spyOn(productCategoryService, 'compareProductCategory');
        comp.compareProductCategory(entity, entity2);
        expect(productCategoryService.compareProductCategory).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
