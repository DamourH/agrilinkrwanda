import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, asapScheduler, map, scheduled } from 'rxjs';

import { catchError } from 'rxjs/operators';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IFarmerProduce, NewFarmerProduce } from '../farmer-produce.model';

export type PartialUpdateFarmerProduce = Partial<IFarmerProduce> & Pick<IFarmerProduce, 'id'>;

type RestOf<T extends IFarmerProduce | NewFarmerProduce> = Omit<T, 'availableFrom' | 'availableUntil'> & {
  availableFrom?: string | null;
  availableUntil?: string | null;
};

export type RestFarmerProduce = RestOf<IFarmerProduce>;

export type NewRestFarmerProduce = RestOf<NewFarmerProduce>;

export type PartialUpdateRestFarmerProduce = RestOf<PartialUpdateFarmerProduce>;

export type EntityResponseType = HttpResponse<IFarmerProduce>;
export type EntityArrayResponseType = HttpResponse<IFarmerProduce[]>;

@Injectable({ providedIn: 'root' })
export class FarmerProduceService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/farmer-produces');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/farmer-produces/_search');

  create(farmerProduce: NewFarmerProduce): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(farmerProduce);
    return this.http
      .post<RestFarmerProduce>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(farmerProduce: IFarmerProduce): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(farmerProduce);
    return this.http
      .put<RestFarmerProduce>(`${this.resourceUrl}/${this.getFarmerProduceIdentifier(farmerProduce)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(farmerProduce: PartialUpdateFarmerProduce): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(farmerProduce);
    return this.http
      .patch<RestFarmerProduce>(`${this.resourceUrl}/${this.getFarmerProduceIdentifier(farmerProduce)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestFarmerProduce>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestFarmerProduce[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<RestFarmerProduce[]>(this.resourceSearchUrl, { params: options, observe: 'response' }).pipe(
      map(res => this.convertResponseArrayFromServer(res)),

      catchError(() => scheduled([new HttpResponse<IFarmerProduce[]>()], asapScheduler)),
    );
  }

  getFarmerProduceIdentifier(farmerProduce: Pick<IFarmerProduce, 'id'>): number {
    return farmerProduce.id;
  }

  compareFarmerProduce(o1: Pick<IFarmerProduce, 'id'> | null, o2: Pick<IFarmerProduce, 'id'> | null): boolean {
    return o1 && o2 ? this.getFarmerProduceIdentifier(o1) === this.getFarmerProduceIdentifier(o2) : o1 === o2;
  }

  addFarmerProduceToCollectionIfMissing<Type extends Pick<IFarmerProduce, 'id'>>(
    farmerProduceCollection: Type[],
    ...farmerProducesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const farmerProduces: Type[] = farmerProducesToCheck.filter(isPresent);
    if (farmerProduces.length > 0) {
      const farmerProduceCollectionIdentifiers = farmerProduceCollection.map(farmerProduceItem =>
        this.getFarmerProduceIdentifier(farmerProduceItem),
      );
      const farmerProducesToAdd = farmerProduces.filter(farmerProduceItem => {
        const farmerProduceIdentifier = this.getFarmerProduceIdentifier(farmerProduceItem);
        if (farmerProduceCollectionIdentifiers.includes(farmerProduceIdentifier)) {
          return false;
        }
        farmerProduceCollectionIdentifiers.push(farmerProduceIdentifier);
        return true;
      });
      return [...farmerProducesToAdd, ...farmerProduceCollection];
    }
    return farmerProduceCollection;
  }

  protected convertDateFromClient<T extends IFarmerProduce | NewFarmerProduce | PartialUpdateFarmerProduce>(farmerProduce: T): RestOf<T> {
    return {
      ...farmerProduce,
      availableFrom: farmerProduce.availableFrom?.toJSON() ?? null,
      availableUntil: farmerProduce.availableUntil?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restFarmerProduce: RestFarmerProduce): IFarmerProduce {
    return {
      ...restFarmerProduce,
      availableFrom: restFarmerProduce.availableFrom ? dayjs(restFarmerProduce.availableFrom) : undefined,
      availableUntil: restFarmerProduce.availableUntil ? dayjs(restFarmerProduce.availableUntil) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestFarmerProduce>): HttpResponse<IFarmerProduce> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestFarmerProduce[]>): HttpResponse<IFarmerProduce[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
