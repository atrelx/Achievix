// src/tests/unit/stores/goals.store.spec.ts
import { describe, it, expect, beforeEach, afterEach, vi } from 'vitest';
import { useGoalsStore } from '@/stores/goals';
import { setupStoreTest, resetMocks } from '../setup/store-test-utils';
import { mockGet, mockPost, mockDelete, mockPut } from '../setup/api.mock';

// Mock error handler to prevent console errors during tests
vi.mock('@/utils/errorHandler', () => ({
  handleApiError: vi.fn((error) => {
    throw error;
  })
}));

describe('Goals Store', () => {
  beforeEach(() => {
    setupStoreTest();
  });

  afterEach(() => {
    resetMocks();
  });

  describe('fetchGoals', () => {
    it('should fetch goals on first load', async () => {
      const mockGoals = [{ id: 1, title: 'Test Goal' }];
      mockGet.mockResolvedValueOnce({ data: mockGoals });

      const store = useGoalsStore();
      const result = await store.fetchGoals();

      expect(mockGet).toHaveBeenCalledWith('/goals');
      expect(store.goals).toEqual(mockGoals);
      expect(store.goalsLoaded).toBe(true);
      expect(result).toEqual(mockGoals);
    });

    it('should not fetch goals if already loaded', async () => {
      const mockGoals = [{
        id: 1,
        title: 'Test Goal',
        targetValue: 100,
        currentValue: 50,
        deadline: null,
        createdAt: '2023-01-01T00:00:00Z',
        updatedAt: null
      }];
      const store = useGoalsStore();
      store.goals = mockGoals;
      store.goalsLoaded = true;

      const result = await store.fetchGoals();

      expect(mockGet).not.toHaveBeenCalled();
      expect(result).toEqual(mockGoals);
    });

    it('should handle errors when fetching goals', async () => {
      mockGet.mockRejectedValueOnce(new Error('Network error'));
      const store = useGoalsStore();

      await expect(store.fetchGoals()).rejects.toThrow('Network error');
    });
  });

  describe('fetchGoal', () => {
    it('should fetch a specific goal by id', async () => {
      const mockGoal = { id: 1, title: 'Test Goal' };
      mockGet.mockResolvedValueOnce({ data: mockGoal });

      const store = useGoalsStore();
      const result = await store.fetchGoal(1);

      expect(mockGet).toHaveBeenCalledWith('/goals/1');
      expect(result).toEqual(mockGoal);
    });

    it('should handle errors when fetching a goal', async () => {
      mockGet.mockRejectedValueOnce(new Error('Goal not found'));
      const store = useGoalsStore();

      await expect(store.fetchGoal(999)).rejects.toThrow('Goal not found');
    });
  });

  describe('fetchGoalDetails', () => {
    it('should fetch goal details with tasks', async () => {
      const mockDetails = {
        id: 1,
        title: 'Test Goal',
        tasks: [{ id: 1, title: 'Task 1' }]
      };
      mockGet.mockResolvedValueOnce({ data: mockDetails });

      const store = useGoalsStore();
      const result = await store.fetchGoalDetails(1);

      expect(mockGet).toHaveBeenCalledWith('/goals/1/details');
      expect(result).toEqual(mockDetails);
    });
  });

  describe('createGoal', () => {
    it('should create a new goal and refresh goals list', async () => {
      const newGoal = { title: 'New Goal', deadline: '2023-12-31' };
      const createdGoal = { id: 2, title: 'New Goal', deadline: '2023-12-31' };
      mockPost.mockResolvedValueOnce({ data: createdGoal });
      mockGet.mockResolvedValueOnce({ data: [createdGoal] });

      const store = useGoalsStore();
      const result = await store.createGoal(newGoal);

      expect(mockPost).toHaveBeenCalledWith('/goals', newGoal);
      expect(store.goalsLoaded).toBe(true);
      expect(result).toEqual(createdGoal);
    });
  });

  describe('deleteGoal', () => {
    it('should delete a goal and refresh goals list', async () => {
      mockDelete.mockResolvedValueOnce({});
      const updatedGoals = [{ id: 2, title: 'Other Goal' }];
      mockGet.mockResolvedValueOnce({ data: updatedGoals });

      const store = useGoalsStore();
      await store.deleteGoal(1);

      expect(mockDelete).toHaveBeenCalledWith('/goals/1');
      expect(mockGet).toHaveBeenCalledWith('/goals');
      expect(store.goals).toEqual(updatedGoals);
    });
  });

  describe('fetchDashboard', () => {
    it('should fetch dashboard data for specified period', async () => {
      const mockDashboard = {
        completedGoals: 5,
        completedTasks: 20,
        activeGoals: 3,
        activeTasks: 10
      };
      mockGet.mockResolvedValueOnce({ data: mockDashboard });

      const store = useGoalsStore();
      const result = await store.fetchDashboard('week');

      expect(mockGet).toHaveBeenCalledWith('/dashboard?periodType=week');
      expect(result).toEqual(mockDashboard);
    });
  });
});
