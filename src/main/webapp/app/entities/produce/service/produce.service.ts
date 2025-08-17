import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, asapScheduler, scheduled } from 'rxjs';

import { catchError } from 'rxjs/operators';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { IProduce, NewProduce } from '../produce.model';

export type PartialUpdateProduce = Partial<IProduce> & Pick<IProduce, 'id'>;

export type EntityResponseType = HttpResponse<IProduce>;
export type EntityArrayResponseType = HttpResponse<IProduce[]>;

@Injectable({ providedIn: 'root' })
export class ProduceService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/produces');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/produces/_search');

  create(produce: NewProduce): Observable<EntityResponseType> {
    return this.http.post<IProduce>(this.resourceUrl, produce, { observe: 'response' });
  }

  update(produce: IProduce): Observable<EntityResponseType> {
    return this.http.put<IProduce>(`${this.resourceUrl}/${this.getProduceIdentifier(produce)}`, produce, { observe: 'response' });
  }

  partialUpdate(produce: PartialUpdateProduce): Observable<EntityResponseType> {
    return this.http.patch<IProduce>(`${this.resourceUrl}/${this.getProduceIdentifier(produce)}`, produce, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IProduce>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IProduce[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IProduce[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(catchError(() => scheduled([new HttpResponse<IProduce[]>()], asapScheduler)));
  }

  getProduceIdentifier(produce: Pick<IProduce, 'id'>): number {
    return produce.id;
  }

  compareProduce(o1: Pick<IProduce, 'id'> | null, o2: Pick<IProduce, 'id'> | null): boolean {
    return o1 && o2 ? this.getProduceIdentifier(o1) === this.getProduceIdentifier(o2) : o1 === o2;
  }

  addProduceToCollectionIfMissing<Type extends Pick<IProduce, 'id'>>(
    produceCollection: Type[],
    ...producesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const produces: Type[] = producesToCheck.filter(isPresent);
    if (produces.length > 0) {
      const produceCollectionIdentifiers = produceCollection.map(produceItem => this.getProduceIdentifier(produceItem));
      const producesToAdd = produces.filter(produceItem => {
        const produceIdentifier = this.getProduceIdentifier(produceItem);
        if (produceCollectionIdentifiers.includes(produceIdentifier)) {
          return false;
        }
        produceCollectionIdentifiers.push(produceIdentifier);
        return true;
      });
      return [...producesToAdd, ...produceCollection];
    }
    return produceCollection;
  }
}
