import { defineStore } from 'pinia'
import api from "../utils/api.ts";
import axios from 'axios'
import type { CreateGoalDTO, GoalDTO, DashboardDTO } from '../types/dtos'

export const useGoalsStore = defineStore('goals', {
  state: () => ({
    goals: [] as GoalDTO[],
    goalsLoaded: false,
  }),
  actions: {
    async fetchGoals() {
      if (!this.goalsLoaded) { // Fetch goals for the first load
        const response = await api.get('/goals')
        this.goals = response.data
        this.goalsLoaded = true
      }
      return this.goals
    },
    async fetchGoal(id: number) {
      const response = await api.get(`/goals/${id}`)
      return response.data
    },
    async createGoal(goal: CreateGoalDTO) {
      const response = await api.post('/goals', goal)
      this.goals.push(response.data)
      this.goalsLoaded = false // Re-loads the goals
      return response.data
    },
    async deleteGoal(id: number) {
      await api.delete(`/goals/${id}`)
      this.goals = this.goals.filter((g) => g.id !== id)
    },
    async fetchDashboard() {
      const response = await api.get('/dashboard')
      return response.data as DashboardDTO
    },
  },
})
