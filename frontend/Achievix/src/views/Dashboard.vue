<template>
  <div class="p-4">
    <div v-if="isLoading" class="flex justify-center">
      <span class="animate-spin text-primary text-4xl">⌀</span>
    </div>
    <div v-else>
      <div class="mb-4">
        <div class="flex justify-center space-x-4">
          <button
            :class="{
              'bg-primary text-white': selectedView === 'goals',
              'bg-gray-300 text-text hover:bg-gray-300/80 cursor-pointer': selectedView !== 'goals',
            }"
            class="px-4 py-2 rounded text-lg font-semibold"
            @click="selectedView = 'goals'"
          >
            Goals
          </button>
          <button
            :class="{
              'bg-primary text-white': selectedView === 'tasks',
              'bg-gray-300 text-text hover:bg-gray-300/80 cursor-pointer': selectedView !== 'tasks',
            }"
            class="px-6 py-2 rounded font-semibold"
            @click="selectedView = 'tasks'"
          >
            Tasks
          </button>
        </div>
      </div>
      <div class="grid grid-cols-2 gap-6">
        <!-- Stats Div -->
        <div class="p-4 bg-surface rounded shadow space-y-6">
          <h2 class="text-2xl font-bold text-text">Global Statistics</h2>
          <p class="text-text text-xl ml-4">
            <span class="font-semibold text-green-600">Completed ✅:</span> {{ selectedView === 'goals' ? dashboard.completedGoals : dashboard.completedTasks }}
          </p>
          <p class="text-text text-xl ml-4">
            <span class="font-semibold text-blue-600">Active 🔄:</span> {{ selectedView === 'goals' ? dashboard.activeGoals : dashboard.activeTasks }}
          </p>
          <p class="text-text text-xl ml-4">
            <span class="font-semibold">Completion Rate 📊:</span> {{
              selectedView === 'goals'
                ? ((dashboard.completedGoals / (dashboard.completedGoals + dashboard.activeGoals)) * 100)
                : ((dashboard.completedTasks / (dashboard.completedTasks + dashboard.activeTasks)) * 100)
            }}%
          </p>
        </div>

        <!-- Recently Completed Div -->
        <div class="p-4 bg-surface rounded shadow col-start-2 row-start-2">
          <h2 class="text-2xl font-bold text-text mb-2">Recently Completed</h2>
          <ul class="text-text space-y-2 text-xl ml-4">
            <li
              v-for="entry in (selectedView === 'goals' ? dashboard.completedGoalsArchive.slice(0, 5) : dashboard.completedTasksArchive.slice(0, 5))"
              :key="entry.title"
            >
              <span class="font-semibold">{{ entry.title }}</span> - {{ formatDate(entry.completedAt) }}
            </li>
          </ul>
        </div>

        <!-- Line Chart -->
        <div class="p-4 bg-surface rounded shadow">
          <LineChart
            :chart-data="selectedView === 'goals' ? goalsLineChartData : tasksLineChartData"
            :chart-options="chartOptions"
          />
        </div>

        <!-- Pie Chart -->
        <div class="p-4 bg-surface rounded shadow">
          <PieChart
            :chart-data="selectedView === 'goals' ? goalsPieChartData : tasksPieChartData"
            :chart-options="pieChartOptions"
          />
        </div>
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
const selectedView = ref('goals') // Default view

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
  const loadingDelay = setTimeout(() => {
    isLoading.value = true;
  }, 200);

  try {
    dashboard.value = await goalsStore.fetchDashboard('month')
  } finally {
    clearTimeout(loadingDelay);
    isLoading.value = false
  }
}

const formatDate = (date: string | null) => (date ? new Date(date).toLocaleDateString() : 'N/A')

onMounted(fetchDashboardData)

const chartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    tooltip: {
      callbacks: {
        label: (context: import('chart.js').TooltipItem<'line'>) => `${context.label}: ${context.raw}`,
      },
    },
  },
  scales: {
    y: {
      ticks: {
        stepSize: 1,
        callback: (value: number) => Number.isInteger(value) ? value : null,
      },
    },
  },
}

const pieChartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    datalabels: {
      color: '#000000', // White numbers
      font: { weight: 'bold', size: 16 },
      formatter: (value: number) => value,
    },
  },
}
</script>

<style scoped>
</style>
