/// <reference types="cypress" />

describe('Authentication', () => {
  beforeEach(() => {
    cy.visit('/login') // Adjust the path if necessary
  })

  it('should display login form', () => {
    cy.get('h1').should('contain', 'Login')
    cy.get('input[name="email"]').should('exist')
    cy.get('input[name="password"]').should('exist')
    cy.get('button[type="submit"]').should('contain', 'Login')
  })

  it('should login successfully with valid credentials', () => {
    cy.intercept('POST', '/api/auth/login', { statusCode: 200, body: { token: 'fake-jwt-token' } })
    cy.get('input[name="email"]').type('test@example.com')
    cy.get('input[name="password"]').type('password123')
    cy.get('button[type="submit"]').click()
    cy.url().should('include', '/dashboard')
  })

  it('should show error for invalid login credentials', () => {
    cy.intercept('POST', '/api/auth/login', { statusCode: 401, body: { message: 'Invalid credentials' } })
    cy.get('input[name="email"]').type('wrong@example.com')
    cy.get('input[name="password"]').type('wrongpassword')
    cy.get('button[type="submit"]').click()
    cy.get('.error-message').should('contain', 'Invalid credentials')
  })

  it('should display signup form', () => {
    cy.visit('/signup')
    cy.get('h1').should('contain', 'Sign Up')
    cy.get('input[name="email"]').should('exist')
    cy.get('input[name="password"]').should('exist')
    cy.get('input[name="confirmPassword"]').should('exist')
    cy.get('button[type="submit"]').should('contain', 'Sign Up')
  })

  it('should signup successfully with valid details', () => {
    cy.intercept('POST', '/api/auth/signup', { statusCode: 201, body: { message: 'User created' } })
    cy.get('input[name="email"]').type('newuser@example.com')
    cy.get('input[name="password"]').type('password123')
    cy.get('input[name="confirmPassword"]').type('password123')
    cy.get('button[type="submit"]').click()
    cy.url().should('include', '/login')
  })
})
