import { defineStore } from 'pinia'
import type { TaskDTO } from '../types/dtos'
import api from '../utils/api'

export const useTasksStore = defineStore('tasks', {
  state: () => ({
    tasks: [] as TaskDTO[],
  }),
  actions: {
    async fetchTasks(goalId: number) {
      const response = await api.get(`/tasks?goalId=${goalId}`)
      this.tasks = response.data
      return this.tasks
    },
    async fetchTask(id: number) {
      const response = await api.get(`/tasks/${id}`)
      return response.data
    },
    async createTask(task: { title: string; goalId: number; deadline: string }) {
      const response = await api.post('/tasks', task)
      this.tasks.push(response.data)
      return response.data
    },
    async updateTask(id: number, task: { title: string; goalId: number; deadline: string }) {
      const response = await api.put(`/tasks/${id}`, task)
      const index = this.tasks.findIndex((t) => t.id === id)
      if (index !== -1) this.tasks[index] = response.data
      return response.data
    },
    async completeTask(id: number) {
      await api.put(`/tasks/${id}`, { completed: true }, { withCredentials: true })
    },
    async deleteTask(id: number) {
      await api.delete(`/tasks/${id}`)
      this.tasks = this.tasks.filter((t) => t.id !== id)
    },
  },
})
