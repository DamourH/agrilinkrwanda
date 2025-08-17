import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IFarmerProduce } from '../farmer-produce.model';
import { FarmerProduceService } from '../service/farmer-produce.service';

const farmerProduceResolve = (route: ActivatedRouteSnapshot): Observable<null | IFarmerProduce> => {
  const id = route.params.id;
  if (id) {
    return inject(FarmerProduceService)
      .find(id)
      .pipe(
        mergeMap((farmerProduce: HttpResponse<IFarmerProduce>) => {
          if (farmerProduce.body) {
            return of(farmerProduce.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default farmerProduceResolve;
