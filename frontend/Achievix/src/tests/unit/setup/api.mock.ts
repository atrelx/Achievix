import { vi } from 'vitest';

export const mockGet = vi.fn();
export const mockPost = vi.fn();
export const mockPut = vi.fn();
export const mockDelete = vi.fn();

export function setupApiMock() {
  vi.mock('@/utils/api.ts', () => ({
    default: {
      get: mockGet,
      post: mockPost,
      put: mockPut,
      delete: mockDelete
    }
  }));
}

export function resetApiMocks() {
  mockGet.mockReset();
  mockPost.mockReset();
  mockPut.mockReset();
  mockDelete.mockReset();
}
