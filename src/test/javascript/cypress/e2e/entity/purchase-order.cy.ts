import {
  entityConfirmDeleteButtonSelector,
  entityCreateButtonSelector,
  entityCreateCancelButtonSelector,
  entityCreateSaveButtonSelector,
  entityDeleteButtonSelector,
  entityDetailsBackButtonSelector,
  entityDetailsButtonSelector,
  entityEditButtonSelector,
  entityTableSelector,
} from '../../support/entity';

describe('PurchaseOrder e2e test', () => {
  const purchaseOrderPageUrl = '/purchase-order';
  const purchaseOrderPageUrlPattern = new RegExp('/purchase-order(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const purchaseOrderSample = { orderDate: '2025-08-17T16:07:18.032Z', status: 'CONFIRMED', totalAmount: 2257.04, deliveryAddress: 'er' };

  let purchaseOrder;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/purchase-orders+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/purchase-orders').as('postEntityRequest');
    cy.intercept('DELETE', '/api/purchase-orders/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (purchaseOrder) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/purchase-orders/${purchaseOrder.id}`,
      }).then(() => {
        purchaseOrder = undefined;
      });
    }
  });

  it('PurchaseOrders menu should load PurchaseOrders page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('purchase-order');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('PurchaseOrder').should('exist');
    cy.url().should('match', purchaseOrderPageUrlPattern);
  });

  describe('PurchaseOrder page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(purchaseOrderPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create PurchaseOrder page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/purchase-order/new$'));
        cy.getEntityCreateUpdateHeading('PurchaseOrder');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', purchaseOrderPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/purchase-orders',
          body: purchaseOrderSample,
        }).then(({ body }) => {
          purchaseOrder = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/purchase-orders+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/purchase-orders?page=0&size=20>; rel="last",<http://localhost/api/purchase-orders?page=0&size=20>; rel="first"',
              },
              body: [purchaseOrder],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(purchaseOrderPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details PurchaseOrder page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('purchaseOrder');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', purchaseOrderPageUrlPattern);
      });

      it('edit button click should load edit PurchaseOrder page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('PurchaseOrder');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', purchaseOrderPageUrlPattern);
      });

      it('edit button click should load edit PurchaseOrder page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('PurchaseOrder');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', purchaseOrderPageUrlPattern);
      });

      it('last delete button click should delete instance of PurchaseOrder', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('purchaseOrder').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', purchaseOrderPageUrlPattern);

        purchaseOrder = undefined;
      });
    });
  });

  describe('new PurchaseOrder page', () => {
    beforeEach(() => {
      cy.visit(purchaseOrderPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('PurchaseOrder');
    });

    it('should create an instance of PurchaseOrder', () => {
      cy.get(`[data-cy="orderDate"]`).type('2025-08-16T20:49');
      cy.get(`[data-cy="orderDate"]`).blur();
      cy.get(`[data-cy="orderDate"]`).should('have.value', '2025-08-16T20:49');

      cy.get(`[data-cy="status"]`).select('PENDING');

      cy.get(`[data-cy="totalAmount"]`).type('24102.96');
      cy.get(`[data-cy="totalAmount"]`).should('have.value', '24102.96');

      cy.get(`[data-cy="deliveryAddress"]`).type('but');
      cy.get(`[data-cy="deliveryAddress"]`).should('have.value', 'but');

      cy.get(`[data-cy="notes"]`).type('without bah');
      cy.get(`[data-cy="notes"]`).should('have.value', 'without bah');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        purchaseOrder = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', purchaseOrderPageUrlPattern);
    });
  });
});
