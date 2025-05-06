import { setActivePinia, createPinia } from 'pinia';
import { resetApiMocks, setupApiMock } from './api.mock.ts';

export function setupTestPinia() {
  const pinia = createPinia();
  setActivePinia(pinia);
  return pinia;
}

export function setupStoreTest() {
  setupApiMock();
  setupTestPinia();
}

export function resetMocks() {
  resetApiMocks();
}
