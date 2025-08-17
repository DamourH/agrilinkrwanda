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

describe('FarmerProduce e2e test', () => {
  const farmerProducePageUrl = '/farmer-produce';
  const farmerProducePageUrlPattern = new RegExp('/farmer-produce(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const farmerProduceSample = { quantity: 30227.09, unit: 'PIECE', pricePerUnit: 21472.95, availableFrom: '2025-08-16T22:29:31.358Z' };

  let farmerProduce;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/farmer-produces+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/farmer-produces').as('postEntityRequest');
    cy.intercept('DELETE', '/api/farmer-produces/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (farmerProduce) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/farmer-produces/${farmerProduce.id}`,
      }).then(() => {
        farmerProduce = undefined;
      });
    }
  });

  it('FarmerProduces menu should load FarmerProduces page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('farmer-produce');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('FarmerProduce').should('exist');
    cy.url().should('match', farmerProducePageUrlPattern);
  });

  describe('FarmerProduce page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(farmerProducePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create FarmerProduce page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/farmer-produce/new$'));
        cy.getEntityCreateUpdateHeading('FarmerProduce');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', farmerProducePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/farmer-produces',
          body: farmerProduceSample,
        }).then(({ body }) => {
          farmerProduce = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/farmer-produces+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/farmer-produces?page=0&size=20>; rel="last",<http://localhost/api/farmer-produces?page=0&size=20>; rel="first"',
              },
              body: [farmerProduce],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(farmerProducePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details FarmerProduce page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('farmerProduce');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', farmerProducePageUrlPattern);
      });

      it('edit button click should load edit FarmerProduce page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('FarmerProduce');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', farmerProducePageUrlPattern);
      });

      it('edit button click should load edit FarmerProduce page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('FarmerProduce');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', farmerProducePageUrlPattern);
      });

      it('last delete button click should delete instance of FarmerProduce', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('farmerProduce').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', farmerProducePageUrlPattern);

        farmerProduce = undefined;
      });
    });
  });

  describe('new FarmerProduce page', () => {
    beforeEach(() => {
      cy.visit(farmerProducePageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('FarmerProduce');
    });

    it('should create an instance of FarmerProduce', () => {
      cy.get(`[data-cy="quantity"]`).type('27987.43');
      cy.get(`[data-cy="quantity"]`).should('have.value', '27987.43');

      cy.get(`[data-cy="unit"]`).select('PIECE');

      cy.get(`[data-cy="pricePerUnit"]`).type('8255.91');
      cy.get(`[data-cy="pricePerUnit"]`).should('have.value', '8255.91');

      cy.get(`[data-cy="availableFrom"]`).type('2025-08-17T00:35');
      cy.get(`[data-cy="availableFrom"]`).blur();
      cy.get(`[data-cy="availableFrom"]`).should('have.value', '2025-08-17T00:35');

      cy.get(`[data-cy="availableUntil"]`).type('2025-08-17T02:50');
      cy.get(`[data-cy="availableUntil"]`).blur();
      cy.get(`[data-cy="availableUntil"]`).should('have.value', '2025-08-17T02:50');

      cy.get(`[data-cy="grade"]`).select('B_GRADE');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        farmerProduce = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', farmerProducePageUrlPattern);
    });
  });
});
