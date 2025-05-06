import { defineStore } from 'pinia'
import api from '../utils/api.ts';
import type { AuthRequest } from '../types/dtos'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    isAuthenticated: false,
  }),
  actions: {
    async checkAuth() {
      try {
        await api.get('/auth/check')
        this.isAuthenticated = true
      } catch (error: any) {
        console.error('Error checking authentication:', error)
        this.isAuthenticated = false
      }
    },
    async login(credentials: AuthRequest) {
      try {
        const response = await api.post('/auth/login', credentials)
        this.isAuthenticated = true
      } catch (error: any) {
        this.isAuthenticated = false
        const errorMessage = error.response?.data?.message || 'Login failed';
        throw new Error(errorMessage);
      }
    },
    async signup(credentials: AuthRequest) {
      try {
        const response = await api.post('/auth/register', credentials)
        this.isAuthenticated = true
      } catch (error: any) {
        this.isAuthenticated = false
        const errorMessage = error.response?.data?.message || 'Signup failed';
        throw new Error(errorMessage);
      }
    },
    async logout() {
      try {
        await api.post('/auth/logout')
        this.isAuthenticated = false
      } catch (error: any) {
        console.error('Error during logout:', error)
        this.isAuthenticated = true
      }
    },
  },
})
