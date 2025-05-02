/// <reference types="cypress" />

describe('Home Page', () => {
  beforeEach(() => {
    cy.visit('/')
  })

  it('should display home page content', () => {
    cy.get('h1').should('contain', 'Welcome to Achievix')
    cy.get('p').should('exist')
    cy.get('img[alt="Achievix Logo"]').should('exist')
  })

  it('should have login and signup links', () => {
    cy.get('a').contains('Login').should('have.attr', 'href', '/login')
    cy.get('a').contains('Sign Up').should('have.attr', 'href', '/signup')
  })
})
