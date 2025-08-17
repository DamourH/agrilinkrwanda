import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IProduce } from '../produce.model';
import { ProduceService } from '../service/produce.service';

const produceResolve = (route: ActivatedRouteSnapshot): Observable<null | IProduce> => {
  const id = route.params.id;
  if (id) {
    return inject(ProduceService)
      .find(id)
      .pipe(
        mergeMap((produce: HttpResponse<IProduce>) => {
          if (produce.body) {
            return of(produce.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default produceResolve;
