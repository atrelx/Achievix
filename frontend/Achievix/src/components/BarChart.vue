<template>
  <div class="chart-container">
    <Bar :data="chartData" :options="chartOptions" />
  </div>
</template>

<script setup lang="ts">
import { Bar } from 'vue-chartjs'
import { Chart as ChartJS, Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale } from 'chart.js'

ChartJS.register(Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale)

defineProps<{
  chartData: {
    labels: string[]
    datasets: { label: string; backgroundColor: string; data: number[] }[]
  }
  chartOptions: {
    responsive: boolean
    maintainAspectRatio: boolean
  }
}>()
</script>

<style scoped>
.chart-container {
  height: 200px;
}
</style>
