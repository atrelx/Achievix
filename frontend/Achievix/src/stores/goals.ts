import { defineStore } from 'pinia'
import api from "../utils/api.ts";
import { handleApiError } from "../utils/errorHandler.ts";
import type { CreateGoalDTO, GoalDTO, DashboardDTO } from '../types/dtos'

export const useGoalsStore = defineStore('goals', {
  state: () => ({
    goals: [] as GoalDTO[],
    goalsLoaded: false,
  }),
  actions: {
    async fetchGoals() {
      try {
        if (!this.goalsLoaded) { // Fetch goals for the first load
          const response = await api.get('/goals')
          this.goals = response.data
          this.goalsLoaded = true
        }
        return this.goals
      } catch (error: any) {
        handleApiError(error);
      }
    },
    async fetchGoal(id: number) {
      try {
        const response = await api.get(`/goals/${id}`)
        return response.data as GoalDTO
      } catch (error: any) {
        handleApiError(error);
      }
    },
    async createGoal(goal: CreateGoalDTO) {
      try {
        const response = await api.post('/goals', goal);
        this.goals.push(response.data);
        this.goalsLoaded = false; // Re-loads the goals
        await this.fetchGoals();
        return response.data;
      } catch (error: any) {
        handleApiError(error);
      }
    },
    async deleteGoal(id: number) {
      try {
        await api.delete(`/goals/${id}`)
        this.goalsLoaded = false; // Re-loads the goals
        await this.fetchGoals();
      } catch (error: any) {
        handleApiError(error);
      }
    },
    async fetchDashboard() {
      try {
        const response = await api.get('/dashboard')
        return response.data as DashboardDTO
      } catch (error: any) {
        handleApiError(error);
      }
    },
  },
})
