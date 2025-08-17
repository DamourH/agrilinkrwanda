import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { Unit } from 'app/entities/enumerations/unit.model';
import { QualityGrade } from 'app/entities/enumerations/quality-grade.model';
import { FarmerProduceService } from '../service/farmer-produce.service';
import { IFarmerProduce } from '../farmer-produce.model';
import { FarmerProduceFormGroup, FarmerProduceFormService } from './farmer-produce-form.service';

@Component({
  selector: 'jhi-farmer-produce-update',
  templateUrl: './farmer-produce-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class FarmerProduceUpdateComponent implements OnInit {
  isSaving = false;
  farmerProduce: IFarmerProduce | null = null;
  unitValues = Object.keys(Unit);
  qualityGradeValues = Object.keys(QualityGrade);

  usersSharedCollection: IUser[] = [];

  protected farmerProduceService = inject(FarmerProduceService);
  protected farmerProduceFormService = inject(FarmerProduceFormService);
  protected userService = inject(UserService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: FarmerProduceFormGroup = this.farmerProduceFormService.createFarmerProduceFormGroup();

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ farmerProduce }) => {
      this.farmerProduce = farmerProduce;
      if (farmerProduce) {
        this.updateForm(farmerProduce);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const farmerProduce = this.farmerProduceFormService.getFarmerProduce(this.editForm);
    if (farmerProduce.id !== null) {
      this.subscribeToSaveResponse(this.farmerProduceService.update(farmerProduce));
    } else {
      this.subscribeToSaveResponse(this.farmerProduceService.create(farmerProduce));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IFarmerProduce>>): void {
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

  protected updateForm(farmerProduce: IFarmerProduce): void {
    this.farmerProduce = farmerProduce;
    this.farmerProduceFormService.resetForm(this.editForm, farmerProduce);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, farmerProduce.farmer);
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.farmerProduce?.farmer)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }
}
