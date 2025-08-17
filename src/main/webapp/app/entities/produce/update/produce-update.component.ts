import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IProductCategory } from 'app/entities/product-category/product-category.model';
import { ProductCategoryService } from 'app/entities/product-category/service/product-category.service';
import { IProduce } from '../produce.model';
import { ProduceService } from '../service/produce.service';
import { ProduceFormGroup, ProduceFormService } from './produce-form.service';

@Component({
  selector: 'jhi-produce-update',
  templateUrl: './produce-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class ProduceUpdateComponent implements OnInit {
  isSaving = false;
  produce: IProduce | null = null;

  productCategoriesSharedCollection: IProductCategory[] = [];

  protected produceService = inject(ProduceService);
  protected produceFormService = inject(ProduceFormService);
  protected productCategoryService = inject(ProductCategoryService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: ProduceFormGroup = this.produceFormService.createProduceFormGroup();

  compareProductCategory = (o1: IProductCategory | null, o2: IProductCategory | null): boolean =>
    this.productCategoryService.compareProductCategory(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ produce }) => {
      this.produce = produce;
      if (produce) {
        this.updateForm(produce);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const produce = this.produceFormService.getProduce(this.editForm);
    if (produce.id !== null) {
      this.subscribeToSaveResponse(this.produceService.update(produce));
    } else {
      this.subscribeToSaveResponse(this.produceService.create(produce));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IProduce>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(produce: IProduce): void {
    this.produce = produce;
    this.produceFormService.resetForm(this.editForm, produce);

    this.productCategoriesSharedCollection = this.productCategoryService.addProductCategoryToCollectionIfMissing<IProductCategory>(
      this.productCategoriesSharedCollection,
      produce.category,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.productCategoryService
      .query()
      .pipe(map((res: HttpResponse<IProductCategory[]>) => res.body ?? []))
      .pipe(
        map((productCategories: IProductCategory[]) =>
          this.productCategoryService.addProductCategoryToCollectionIfMissing<IProductCategory>(productCategories, this.produce?.category),
        ),
      )
      .subscribe((productCategories: IProductCategory[]) => (this.productCategoriesSharedCollection = productCategories));
  }
}
