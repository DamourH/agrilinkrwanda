import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import PurchaseOrderResolve from './route/purchase-order-routing-resolve.service';

const purchaseOrderRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/purchase-order.component').then(m => m.PurchaseOrderComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/purchase-order-detail.component').then(m => m.PurchaseOrderDetailComponent),
    resolve: {
      purchaseOrder: PurchaseOrderResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/purchase-order-update.component').then(m => m.PurchaseOrderUpdateComponent),
    resolve: {
      purchaseOrder: PurchaseOrderResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/purchase-order-update.component').then(m => m.PurchaseOrderUpdateComponent),
    resolve: {
      purchaseOrder: PurchaseOrderResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default purchaseOrderRoute;
