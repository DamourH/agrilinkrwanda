import { IProduce, NewProduce } from './produce.model';

export const sampleWithRequiredData: IProduce = {
  id: 25534,
  name: 'pants before',
};

export const sampleWithPartialData: IProduce = {
  id: 3951,
  name: 'until with',
};

export const sampleWithFullData: IProduce = {
  id: 631,
  name: 'scrap petticoat',
  description: 'hourly gah dividend',
};

export const sampleWithNewData: NewProduce = {
  name: 'duh hence where',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
