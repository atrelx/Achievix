<template>
  <div class="chart-container">
    <Pie :data="chartData" :options="chartOptions" />
  </div>
</template>

<script setup lang="ts">
import { Pie } from 'vue-chartjs'
import { Chart as ChartJS, Title, Tooltip, Legend, ArcElement } from 'chart.js'
import ChartDataLabels from 'chartjs-plugin-datalabels'

ChartJS.register(Title, Tooltip, Legend, ArcElement, ChartDataLabels)

defineProps<{
  chartData: {
    labels: string[]
    datasets: { label: string; backgroundColor: string[]; data: number[] }[]
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
