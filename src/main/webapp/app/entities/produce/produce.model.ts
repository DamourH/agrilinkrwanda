import { IProductCategory } from 'app/entities/product-category/product-category.model';

export interface IProduce {
  id: number;
  name?: string | null;
  description?: string | null;
  category?: Pick<IProductCategory, 'id'> | null;
}

export type NewProduce = Omit<IProduce, 'id'> & { id: null };
