/// <reference types="cypress" />

describe('Login Page', () => {
  beforeEach(() => {
    cy.visit('/login')
  })

  it('should display login form', () => {
    cy.get('form').should('exist')
    cy.get('input[type="email"]').should('exist')
    cy.get('input[type="password"]').should('exist')
    cy.get('button[type="submit"]').should('contain', 'Login')
  })

  it('should show error on invalid login', () => {
    cy.intercept('POST', '/api/auth/login', { statusCode: 401, body: { message: 'Invalid credentials' } })
    cy.get('input[type="email"]').type('wrong@example.com')
    cy.get('input[type="password"]').type('wrong')
    cy.get('button[type="submit"]').click()
    cy.get('.text-error').should('exist')
  })

  it('should redirect to dashboard on successful login', () => {
    cy.intercept('POST', '/api/auth/login', { statusCode: 200, body: { accessToken: 'fake-token' } })
    cy.get('input[type="email"]').type('test@example.com')
    cy.get('input[type="password"]').type('password')
    cy.get('button[type="submit"]').click()
    cy.url().should('include', '/dashboard')
  })
})
