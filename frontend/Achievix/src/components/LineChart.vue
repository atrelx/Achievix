<template>
  <div class="chart-container">
    <Line :data="chartData" :options="chartOptions" />
  </div>
</template>

<script setup lang="ts">
import { Line } from 'vue-chartjs'
import { Chart as ChartJS, Title, Tooltip, Legend, LineElement, PointElement, CategoryScale, LinearScale } from 'chart.js'

ChartJS.register(Title, Tooltip, Legend, LineElement, PointElement, CategoryScale, LinearScale)

defineProps<{
  chartData: {
    labels: string[]
    datasets: { label: string; borderColor: string; data: number[] }[]
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
