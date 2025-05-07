import { defineStore } from 'pinia'
import type {TaskCreateDTO, TaskDTO} from '../types/dtos'
import api from '../utils/api'
import {handleApiError} from "@/utils/errorHandler.ts";

export const useTasksStore = defineStore('tasks', {
  state: () => ({
    tasks: [] as TaskDTO[],
  }),
  actions: {
    async fetchTasks(goalId: number) {
      try {
        const response = await api.get(`/tasks?goalId=${goalId}`)
        this.tasks = response.data
        return this.tasks
      } catch (error: any) {
        handleApiError(error);
      }
    },
    async fetchTask(id: number) {
      try {
        const response = await api.get(`/tasks/${id}`)
        return response.data
      } catch (error: any) {
        handleApiError(error);
      }
    },
    async createTask(task: TaskCreateDTO) {
      try{
        const response = await api.post('/tasks', task)
        this.tasks.push(response.data)
        return response.data
      } catch (error: any) {
        handleApiError(error);
      }
    },
    async updateTask(id: number, task: TaskCreateDTO) {
      try {
        const response = await api.put(`/tasks/${id}`, task)
        const index = this.tasks.findIndex((t) => t.id === id)
        if (index !== -1) this.tasks[index] = response.data
        return response.data
      } catch (error: any) {
        handleApiError(error);
      }
    },
    async completeTask(id: number) {
      try {
        await api.put(`/tasks/${id}/complete`, { completed: true })
      } catch (error: any) {
        handleApiError(error);
      }
    },
    async deleteTask(id: number) {
      try {
        await api.delete(`/tasks/${id}`)
        this.tasks = this.tasks.filter((t) => t.id !== id)
      } catch (error: any) {
        handleApiError(error);
      }
    },
  },
})
