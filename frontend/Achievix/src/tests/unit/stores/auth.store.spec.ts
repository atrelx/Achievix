// src/tests/unit/stores/auth.store.spec.ts
import { describe, it, expect, beforeEach, afterEach } from 'vitest';
import { useAuthStore } from '@/stores/auth';
import { setupStoreTest, resetMocks } from '../setup/store-test-utils';
import { mockGet, mockPost } from '../setup/api.mock.ts';

describe('Auth Store', () => {
  beforeEach(() => {
    setupStoreTest();
  });

  afterEach(() => {
    resetMocks();
  });

  describe('checkAuth action', () => {
    it('should set isAuthenticated to true when check succeeds', async () => {
      mockGet.mockResolvedValueOnce({});
      const store = useAuthStore();

      await store.checkAuth();

      expect(mockGet).toHaveBeenCalledWith('/auth/check');
      expect(store.isAuthenticated).toBe(true);
    });

    it('should set isAuthenticated to false when check fails', async () => {
      mockGet.mockRejectedValueOnce(new Error('Auth check failed'));
      const store = useAuthStore();

      await store.checkAuth();

      expect(mockGet).toHaveBeenCalledWith('/auth/check');
      expect(store.isAuthenticated).toBe(false);
    });
  });

  describe('login action', () => {
    it('should set isAuthenticated to true when login succeeds', async () => {
      mockPost.mockResolvedValueOnce({ data: {} });
      const store = useAuthStore();
      const credentials = { email: 'test@example.com', password: 'password123' };

      await store.login(credentials);

      expect(mockPost).toHaveBeenCalledWith('/auth/login', credentials);
      expect(store.isAuthenticated).toBe(true);
    });

    it('should throw error when login fails', async () => {
      const errorResponse = {
        response: { data: { message: 'Invalid credentials' } }
      };
      mockPost.mockRejectedValueOnce(errorResponse);
      const store = useAuthStore();
      const credentials = { email: 'test@example.com', password: 'wrong' };

      await expect(store.login(credentials)).rejects.toThrow('Invalid credentials');
      expect(store.isAuthenticated).toBe(false);
    });
  });

  describe('signup action', () => {
    it('should set isAuthenticated to true when signup succeeds', async () => {
      mockPost.mockResolvedValueOnce({ data: {} });
      const store = useAuthStore();
      const credentials = { email: 'new@example.com', password: 'newpass123' };

      await store.signup(credentials);

      expect(mockPost).toHaveBeenCalledWith('/auth/register', credentials);
      expect(store.isAuthenticated).toBe(true);
    });

    it('should throw error when signup fails', async () => {
      const errorResponse = {
        response: { data: { message: 'Email already exists' } }
      };
      mockPost.mockRejectedValueOnce(errorResponse);
      const store = useAuthStore();

      await expect(store.signup({
        email: 'existing@example.com',
        password: 'pass123'
      })).rejects.toThrow('Email already exists');
      expect(store.isAuthenticated).toBe(false);
    });
  });

  describe('logout action', () => {
    it('should set isAuthenticated to false and redirect when logout succeeds', async () => {
      mockPost.mockResolvedValueOnce({});
      const store = useAuthStore();
      store.isAuthenticated = true;

      await store.logout();

      expect(mockPost).toHaveBeenCalledWith('/auth/logout');
      expect(store.isAuthenticated).toBe(false);
    });

    it('should keep isAuthenticated true when logout fails', async () => {
      mockPost.mockRejectedValueOnce(new Error('Logout failed'));
      const store = useAuthStore();
      store.isAuthenticated = true;

      await store.logout();

      expect(mockPost).toHaveBeenCalledWith('/auth/logout');
      expect(store.isAuthenticated).toBe(true);
    });
  });
});
