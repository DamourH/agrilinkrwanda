import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IPurchaseOrder } from '../purchase-order.model';
import { PurchaseOrderService } from '../service/purchase-order.service';

const purchaseOrderResolve = (route: ActivatedRouteSnapshot): Observable<null | IPurchaseOrder> => {
  const id = route.params.id;
  if (id) {
    return inject(PurchaseOrderService)
      .find(id)
      .pipe(
        mergeMap((purchaseOrder: HttpResponse<IPurchaseOrder>) => {
          if (purchaseOrder.body) {
            return of(purchaseOrder.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default purchaseOrderResolve;
