// src/tests/unit/stores/tasks.store.spec.ts
import { describe, it, expect, beforeEach, afterEach, vi } from 'vitest';
import { useTasksStore } from '@/stores/tasks';
import { setupStoreTest, resetMocks } from '../setup/store-test-utils';
import { mockGet, mockPost, mockDelete, mockPut } from '../setup/api.mock';

// Mock error handler to prevent console errors during tests
vi.mock('@/utils/errorHandler', () => ({
  handleApiError: vi.fn((error) => {
    throw error;
  })
}));

describe('Tasks Store', () => {
  beforeEach(() => {
    setupStoreTest();
  });

  afterEach(() => {
    resetMocks();
  });

  describe('fetchTasks', () => {
    it('should fetch tasks for a specific goal', async () => {
      const mockTasks = [
        { id: 1, title: 'Task 1', goalId: 1 },
        { id: 2, title: 'Task 2', goalId: 1 }
      ];
      mockGet.mockResolvedValueOnce({ data: mockTasks });

      const store = useTasksStore();
      const result = await store.fetchTasks(1);

      expect(mockGet).toHaveBeenCalledWith('/tasks?goalId=1');
      expect(store.tasks).toEqual(mockTasks);
      expect(result).toEqual(mockTasks);
    });

    it('should handle errors when fetching tasks', async () => {
      mockGet.mockRejectedValueOnce(new Error('Network error'));
      const store = useTasksStore();

      await expect(store.fetchTasks(1)).rejects.toThrow('Network error');
    });
  });

  describe('fetchTask', () => {
    it('should fetch a specific task by id', async () => {
      const mockTask = { id: 1, title: 'Task 1', goalId: 1 };
      mockGet.mockResolvedValueOnce({ data: mockTask });

      const store = useTasksStore();
      const result = await store.fetchTask(1);

      expect(mockGet).toHaveBeenCalledWith('/tasks/1');
      expect(result).toEqual(mockTask);
    });
  });

  describe('createTask', () => {
    it('should create a new task and add it to the tasks array', async () => {
      const newTask = { title: 'New Task', goalId: 1, deadline: '2023-12-31' };
      const createdTask = { id: 3, title: 'New Task', goalId: 1, deadline: '2023-12-31' };
      mockPost.mockResolvedValueOnce({ data: createdTask });

      const store = useTasksStore();
      store.tasks = [
        {
          id: 1,
          title: 'Task 1',
          goalId: 1,
          completed: false,
          completedAt: null,
          deadline: null,
          createdAt: '2023-01-01T00:00:00Z'
        }
      ];

      const result = await store.createTask(newTask);

      expect(mockPost).toHaveBeenCalledWith('/tasks', newTask);
      expect(store.tasks).toContain(createdTask);
      expect(result).toEqual(createdTask);
    });
  });

  describe('updateTask', () => {
    it('should update a task and refresh it in the tasks array', async () => {
      const taskUpdate = { title: 'Updated Task', goalId: 1, deadline: '2023-12-31' };
      const updatedTask = { id: 1, ...taskUpdate };
      mockPut.mockResolvedValueOnce({ data: updatedTask });

      const store = useTasksStore();
      store.tasks = [
        {
          id: 1,
          title: 'Task 1',
          goalId: 1,
          completed: false,
          completedAt: null,
          deadline: null,
          createdAt: '2023-01-01T00:00:00Z'
        }
      ];

      const result = await store.updateTask(1, taskUpdate);

      expect(mockPut).toHaveBeenCalledWith('/tasks/1', taskUpdate);
      expect(store.tasks[0]).toEqual(updatedTask);
      expect(result).toEqual(updatedTask);
    });

    it('should not update tasks array if task not found', async () => {
      const taskUpdate = { title: 'Updated Task', goalId: 1, deadline: '2023-12-31' };
      const updatedTask = { id: 2, ...taskUpdate };
      mockPut.mockResolvedValueOnce({ data: updatedTask });

      const store = useTasksStore();
      store.tasks = [
        {
          id: 1,
          title: 'Task 1',
          goalId: 1,
          completed: false,
          completedAt: null,
          deadline: null,
          createdAt: '2023-01-01T00:00:00Z'
        }
      ];
      const originalTasks = [...store.tasks];

      await store.updateTask(2, taskUpdate);

      expect(mockPut).toHaveBeenCalledWith('/tasks/2', taskUpdate);
      expect(store.tasks).toEqual(originalTasks);
    });
  });

  describe('completeTask', () => {
    it('should mark a task as complete', async () => {
      mockPut.mockResolvedValueOnce({});

      const store = useTasksStore();
      await store.completeTask(1);

      expect(mockPut).toHaveBeenCalledWith('/tasks/1/complete', { completed: true });
    });
  });

  describe('deleteTask', () => {
    it('should delete a task and remove it from the tasks array', async () => {
      mockDelete.mockResolvedValueOnce({});

      const store = useTasksStore();
      store.tasks = [
        {
          id: 1,
          title: 'Task 1',
          goalId: 1,
          completed: false,
          completedAt: null,
          deadline: null,
          createdAt: '2023-01-01T00:00:00Z'
        },
        {
          id: 2,
          title: 'Task 2',
          goalId: 1,
          completed: false,
          completedAt: null,
          deadline: null,
          createdAt: '2023-01-01T00:00:00Z'
        },
      ];

      await store.deleteTask(1);

      expect(mockDelete).toHaveBeenCalledWith('/tasks/1');
      expect(store.tasks).toHaveLength(1);
      expect(store.tasks[0].id).toBe(2);
    });
  });
});
