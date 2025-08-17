import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'authority',
    data: { pageTitle: 'agriLinkRwandaApp.adminAuthority.home.title' },
    loadChildren: () => import('./admin/authority/authority.routes'),
  },
  {
    path: 'product-category',
    data: { pageTitle: 'agriLinkRwandaApp.productCategory.home.title' },
    loadChildren: () => import('./product-category/product-category.routes'),
  },
  {
    path: 'produce',
    data: { pageTitle: 'agriLinkRwandaApp.produce.home.title' },
    loadChildren: () => import('./produce/produce.routes'),
  },
  {
    path: 'farmer-produce',
    data: { pageTitle: 'agriLinkRwandaApp.farmerProduce.home.title' },
    loadChildren: () => import('./farmer-produce/farmer-produce.routes'),
  },
  {
    path: 'purchase-order',
    data: { pageTitle: 'agriLinkRwandaApp.purchaseOrder.home.title' },
    loadChildren: () => import('./purchase-order/purchase-order.routes'),
  },
  {
    path: 'order-item',
    data: { pageTitle: 'agriLinkRwandaApp.orderItem.home.title' },
    loadChildren: () => import('./order-item/order-item.routes'),
  },
  {
    path: 'delivery',
    data: { pageTitle: 'agriLinkRwandaApp.delivery.home.title' },
    loadChildren: () => import('./delivery/delivery.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
