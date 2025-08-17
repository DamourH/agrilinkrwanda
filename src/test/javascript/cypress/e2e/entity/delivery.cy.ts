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

describe('Delivery e2e test', () => {
  const deliveryPageUrl = '/delivery';
  const deliveryPageUrlPattern = new RegExp('/delivery(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const deliverySample = {};

  let delivery;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/deliveries+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/deliveries').as('postEntityRequest');
    cy.intercept('DELETE', '/api/deliveries/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (delivery) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/deliveries/${delivery.id}`,
      }).then(() => {
        delivery = undefined;
      });
    }
  });

  it('Deliveries menu should load Deliveries page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('delivery');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Delivery').should('exist');
    cy.url().should('match', deliveryPageUrlPattern);
  });

  describe('Delivery page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(deliveryPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Delivery page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/delivery/new$'));
        cy.getEntityCreateUpdateHeading('Delivery');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', deliveryPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/deliveries',
          body: deliverySample,
        }).then(({ body }) => {
          delivery = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/deliveries+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [delivery],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(deliveryPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Delivery page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('delivery');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', deliveryPageUrlPattern);
      });

      it('edit button click should load edit Delivery page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Delivery');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', deliveryPageUrlPattern);
      });

      it('edit button click should load edit Delivery page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Delivery');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', deliveryPageUrlPattern);
      });

      it('last delete button click should delete instance of Delivery', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('delivery').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', deliveryPageUrlPattern);

        delivery = undefined;
      });
    });
  });

  describe('new Delivery page', () => {
    beforeEach(() => {
      cy.visit(deliveryPageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Delivery');
    });

    it('should create an instance of Delivery', () => {
      cy.get(`[data-cy="pickupDate"]`).type('2025-08-17T08:41');
      cy.get(`[data-cy="pickupDate"]`).blur();
      cy.get(`[data-cy="pickupDate"]`).should('have.value', '2025-08-17T08:41');

      cy.get(`[data-cy="deliveryDate"]`).type('2025-08-17T10:36');
      cy.get(`[data-cy="deliveryDate"]`).blur();
      cy.get(`[data-cy="deliveryDate"]`).should('have.value', '2025-08-17T10:36');

      cy.get(`[data-cy="status"]`).select('PICKED_UP');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        delivery = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', deliveryPageUrlPattern);
    });
  });
});
