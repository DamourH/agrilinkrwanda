import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IProduce } from '../produce.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../produce.test-samples';

import { ProduceService } from './produce.service';

const requireRestSample: IProduce = {
  ...sampleWithRequiredData,
};

describe('Produce Service', () => {
  let service: ProduceService;
  let httpMock: HttpTestingController;
  let expectedResult: IProduce | IProduce[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(ProduceService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a Produce', () => {
      const produce = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(produce).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Produce', () => {
      const produce = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(produce).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Produce', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Produce', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Produce', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    it('should handle exceptions for searching a Produce', () => {
      const queryObject: any = {
        page: 0,
        size: 20,
        query: '',
        sort: [],
      };
      service.search(queryObject).subscribe(() => expectedResult);

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(null, { status: 500, statusText: 'Internal Server Error' });
      expect(expectedResult).toBe(null);
    });

    describe('addProduceToCollectionIfMissing', () => {
      it('should add a Produce to an empty array', () => {
        const produce: IProduce = sampleWithRequiredData;
        expectedResult = service.addProduceToCollectionIfMissing([], produce);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(produce);
      });

      it('should not add a Produce to an array that contains it', () => {
        const produce: IProduce = sampleWithRequiredData;
        const produceCollection: IProduce[] = [
          {
            ...produce,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addProduceToCollectionIfMissing(produceCollection, produce);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Produce to an array that doesn't contain it", () => {
        const produce: IProduce = sampleWithRequiredData;
        const produceCollection: IProduce[] = [sampleWithPartialData];
        expectedResult = service.addProduceToCollectionIfMissing(produceCollection, produce);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(produce);
      });

      it('should add only unique Produce to an array', () => {
        const produceArray: IProduce[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const produceCollection: IProduce[] = [sampleWithRequiredData];
        expectedResult = service.addProduceToCollectionIfMissing(produceCollection, ...produceArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const produce: IProduce = sampleWithRequiredData;
        const produce2: IProduce = sampleWithPartialData;
        expectedResult = service.addProduceToCollectionIfMissing([], produce, produce2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(produce);
        expect(expectedResult).toContain(produce2);
      });

      it('should accept null and undefined values', () => {
        const produce: IProduce = sampleWithRequiredData;
        expectedResult = service.addProduceToCollectionIfMissing([], null, produce, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(produce);
      });

      it('should return initial array if no Produce is added', () => {
        const produceCollection: IProduce[] = [sampleWithRequiredData];
        expectedResult = service.addProduceToCollectionIfMissing(produceCollection, undefined, null);
        expect(expectedResult).toEqual(produceCollection);
      });
    });

    describe('compareProduce', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareProduce(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 792 };
        const entity2 = null;

        const compareResult1 = service.compareProduce(entity1, entity2);
        const compareResult2 = service.compareProduce(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 792 };
        const entity2 = { id: 1992 };

        const compareResult1 = service.compareProduce(entity1, entity2);
        const compareResult2 = service.compareProduce(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 792 };
        const entity2 = { id: 792 };

        const compareResult1 = service.compareProduce(entity1, entity2);
        const compareResult2 = service.compareProduce(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
