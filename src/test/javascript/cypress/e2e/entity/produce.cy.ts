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

describe('Produce e2e test', () => {
  const producePageUrl = '/produce';
  const producePageUrlPattern = new RegExp('/produce(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const produceSample = { name: 'elver brr oh' };

  let produce;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/produces+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/produces').as('postEntityRequest');
    cy.intercept('DELETE', '/api/produces/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (produce) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/produces/${produce.id}`,
      }).then(() => {
        produce = undefined;
      });
    }
  });

  it('Produces menu should load Produces page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('produce');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Produce').should('exist');
    cy.url().should('match', producePageUrlPattern);
  });

  describe('Produce page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(producePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Produce page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/produce/new$'));
        cy.getEntityCreateUpdateHeading('Produce');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', producePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/produces',
          body: produceSample,
        }).then(({ body }) => {
          produce = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/produces+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [produce],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(producePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Produce page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('produce');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', producePageUrlPattern);
      });

      it('edit button click should load edit Produce page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Produce');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', producePageUrlPattern);
      });

      it('edit button click should load edit Produce page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Produce');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', producePageUrlPattern);
      });

      it('last delete button click should delete instance of Produce', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('produce').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', producePageUrlPattern);

        produce = undefined;
      });
    });
  });

  describe('new Produce page', () => {
    beforeEach(() => {
      cy.visit(producePageUrl);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Produce');
    });

    it('should create an instance of Produce', () => {
      cy.get(`[data-cy="name"]`).type('skean concerning');
      cy.get(`[data-cy="name"]`).should('have.value', 'skean concerning');

      cy.get(`[data-cy="description"]`).type('hm extra-large');
      cy.get(`[data-cy="description"]`).should('have.value', 'hm extra-large');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        produce = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', producePageUrlPattern);
    });
  });
});
