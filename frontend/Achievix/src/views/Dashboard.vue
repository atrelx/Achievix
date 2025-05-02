<template>
  <div class="p-6">
    <h1 class="text-3xl font-bold text-text mb-6">Dashboard</h1>
    <div v-if="isLoading" class="flex justify-center">
      <span class="animate-spin text-primary text-4xl">⌀</span>
    </div>
    <div v-else class="grid grid-cols-1 md:grid-cols-2 gap-6">
      <div class="bg-surface p-6 rounded-lg shadow-lg">
        <h2 class="text-xl font-semibold text-text mb-4">Goal Statistics</h2>
        <div class="grid grid-cols-2 gap-4">
          <div>
            <p class="text-text-secondary">Completed Goals</p>
            <p class="text-2xl font-bold text-text">{{ dashboard.completedGoals }}</p>
          </div>
          <div>
            <p class="text-text-secondary">Active Goals</p>
            <p class="text-2xl font-bold text-text">{{ dashboard.activeGoals }}</p>
          </div>
        </div>
        <BarChart :chart-data="goalsChartData" :chart-options="chartOptions" class="mt-4" />
      </div>
      <div class="bg-surface p-6 rounded-lg shadow-lg">
        <h2 class="text-xl font-semibold text-text mb-4">Task Statistics</h2>
        <div class="grid grid-cols-2 gap-4">
          <div>
            <p class="text-text-secondary">Completed Tasks</p>
            <p class="text-2xl font-bold text-text">{{ dashboard.completedTasks }}</p>
          </div>
          <div>
            <p class="text-text-secondary">Active Tasks</p>
            <p class="text-2xl font-bold text-text">{{ dashboard.activeTasks }}</p>
          </div>
        </div>
        <BarChart :chart-data="tasksChartData" :chart-options="chartOptions" class="mt-4" />
      </div>
      <div class="bg-surface p-6 rounded-lg shadow-lg">
        <h2 class="text-xl font-semibold text-text mb-4">Recent Completed Goals</h2>
        <ul>
          <li v-for="goal in dashboard.completedGoalsArchive" :key="goal.title" class="py-2">
            {{ goal.title }} - {{ formatDate(goal.completedAt) }}
          </li>
        </ul>
      </div>
      <div class="bg-surface p-6 rounded-lg shadow-lg">
        <h2 class="text-xl font-semibold text-text mb-4">Recent Completed Tasks</h2>
        <ul>
          <li v-for="task in dashboard.completedTasksArchive" :key="task.title" class="py-2">
            {{ task.title }} - {{ formatDate(task.completedAt) }}
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import BarChart from '../components/BarChart.vue'
import { useGoalsStore } from '../stores/goals'
import type { DashboardDTO } from '../types/dtos'

const goalsStore = useGoalsStore()
const dashboard = ref<DashboardDTO>({
  completedGoals: 0,
  completedTasks: 0,
  activeGoals: 0,
  activeTasks: 0,
  goalsCompletedByPeriod: {},
  tasksCompletedByPeriod: {},
  completedGoalsArchive: [],
  completedTasksArchive: [],
})
const isLoading = ref(true)

const chartOptions = {
  responsive: true,
  maintainAspectRatio: false,
}

const goalsChartData = computed(() => ({
  labels: Object.keys(dashboard.value.goalsCompletedByPeriod),
  datasets: [
    {
      label: 'Goals Completed',
      backgroundColor: '#4F46E5',
      data: Object.values(dashboard.value.goalsCompletedByPeriod),
    },
  ],
}))

const tasksChartData = computed(() => ({
  labels: Object.keys(dashboard.value.tasksCompletedByPeriod),
  datasets: [
    {
      label: 'Tasks Completed',
      backgroundColor: '#10B981',
      data: Object.values(dashboard.value.tasksCompletedByPeriod),
    },
  ],
}))

const formatDate = (date: string) => new Date(date).toLocaleDateString()

onMounted(async () => {
  isLoading.value = true
  try {
    dashboard.value = await goalsStore.fetchDashboard()
  } finally {
    isLoading.value = false
  }
})
</script>

<style scoped>
</style>
