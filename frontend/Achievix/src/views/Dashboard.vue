<template>
  <div class="p-6">
    <h1 class="text-3xl font-bold text-text mb-6">Dashboard</h1>
    <div v-if="isLoading" class="flex justify-center">
      <span class="animate-spin text-primary text-4xl">⌀</span>
    </div>
    <div v-else>
      <div class="mb-4">
        <label for="periodType" class="block text-text-secondary mb-2">Select Period Type:</label>
        <select
          id="periodType"
          v-model="selectedPeriodType"
          class="p-2 border rounded w-full md:w-1/3"
        >
          <option value="day">Day</option>
          <option value="month">Month</option>
          <option value="year">Year</option>
        </select>
      </div>
      <div class="mb-4">
        <div class="flex justify-center space-x-4">
          <button
            :class="{
              'bg-primary text-white': selectedView === 'goals',
              'bg-gray-200 text-text': selectedView !== 'goals',
            }"
            class="px-4 py-2 rounded"
            @click="selectedView = 'goals'"
          >
            Goals
          </button>
          <button
            :class="{
              'bg-primary text-white': selectedView === 'tasks',
              'bg-gray-200 text-text': selectedView !== 'tasks',
            }"
            class="px-4 py-2 rounded"
            @click="selectedView = 'tasks'"
          >
            Tasks
          </button>
        </div>
      </div>
      <div class="grid grid-cols-2 gap-6">
        <!-- Left Top Div -->
        <div class="p-4 bg-surface rounded shadow">
          <h2 class="text-xl font-bold text-text mb-2">Stats</h2>
          <p class="text-text-secondary">
            Completed: {{ selectedView === 'goals' ? dashboard.completedGoals : dashboard.completedTasks }}
          </p>
          <p class="text-text-secondary">
            Active: {{ selectedView === 'goals' ? dashboard.activeGoals : dashboard.activeTasks }}
          </p>
          <p class="text-text-secondary">
            Completion Rate: {{
              selectedView === 'goals'
                ? ((dashboard.completedGoals / (dashboard.completedGoals + dashboard.activeGoals)) * 100).toFixed(2)
                : ((dashboard.completedTasks / (dashboard.completedTasks + dashboard.activeTasks)) * 100).toFixed(2)
            }}%
          </p>
        </div>

        <!-- Right Bottom Div -->
        <div class="p-4 bg-surface rounded shadow col-start-2 row-start-2">
          <h2 class="text-xl font-bold text-text mb-2">Recently Completed</h2>
          <ul class="text-text-secondary space-y-2">
            <li
              v-for="entry in (selectedView === 'goals' ? dashboard.completedGoalsArchive : dashboard.completedTasksArchive)"
              :key="entry.title"
            >
              {{ entry.title }} - {{ formatDate(entry.completedAt) }}
            </li>
          </ul>
        </div>

        <!-- Line Chart -->
        <LineChart
          :chart-data="selectedView === 'goals' ? goalsLineChartData : tasksLineChartData"
          :chart-options="chartOptions"
          class="mt-4"
        />

        <!-- Pie Chart -->
        <PieChart
          :chart-data="selectedView === 'goals' ? goalsPieChartData : tasksPieChartData"
          :chart-options="chartOptions"
          class="mt-4"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, watch } from 'vue'
import LineChart from '../components/LineChart.vue'
import PieChart from '../components/PieChart.vue'
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
const selectedPeriodType = ref('day') // Default period type
const selectedView = ref('goals') // Default view

const chartOptions = {
  responsive: true,
  maintainAspectRatio: false,
}

const goalsLineChartData = computed(() => ({
  labels: Object.keys(dashboard.value.goalsCompletedByPeriod),
  datasets: [
    {
      label: 'Goals Completed',
      borderColor: '#4F46E5',
      data: Object.values(dashboard.value.goalsCompletedByPeriod),
    },
  ],
}))

const tasksLineChartData = computed(() => ({
  labels: Object.keys(dashboard.value.tasksCompletedByPeriod),
  datasets: [
    {
      label: 'Tasks Completed',
      borderColor: '#10B981',
      data: Object.values(dashboard.value.tasksCompletedByPeriod),
    },
  ],
}))

const goalsPieChartData = computed(() => ({
  labels: ['Completed Goals', 'Active Goals'],
  datasets: [
    {
      label: 'Goals Distribution',
      backgroundColor: ['#4F46E5', '#A5B4FC'],
      data: [dashboard.value.completedGoals, dashboard.value.activeGoals],
    },
  ],
}))

const tasksPieChartData = computed(() => ({
  labels: ['Completed Tasks', 'Active Tasks'],
  datasets: [
    {
      label: 'Tasks Distribution',
      backgroundColor: ['#10B981', '#6EE7B7'],
      data: [dashboard.value.completedTasks, dashboard.value.activeTasks],
    },
  ],
}))

const fetchDashboardData = async () => {
  isLoading.value = true
  try {
    dashboard.value = await goalsStore.fetchDashboard(selectedPeriodType.value)
  } finally {
    isLoading.value = false
  }
}

const formatDate = (date: string | null) => (date ? new Date(date).toLocaleDateString() : 'N/A')

onMounted(fetchDashboardData)

// Watch for changes in the selected period type and fetch data dynamically
watch(selectedPeriodType, fetchDashboardData)
</script>

<style scoped>
</style>
