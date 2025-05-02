<template>
  <div class="p-6">
    <h1 class="text-3xl font-bold text-text mb-6">{{ isEdit ? 'Edit Task' : 'Create Task' }}</h1>
    <div class="bg-surface p-6 rounded-lg shadow-lg w-full max-w-md">
      <form @submit.prevent="handleSubmit">
        <div class="mb-4">
          <label for="title" class="block text-text-secondary mb-2">Title</label>
          <input
            v-model="form.title"
            id="title"
            class="w-full p-2 border rounded"
            :class="{ 'border-error': v$.title.$error }"
            @blur="v$.title.$touch"
          />
          <span v-if="v$.title.$error" class="text-error text-sm">{{ v$.title.$errors[0].$message }}</span>
        </div>
        <div class="mb-4">
          <label for="goalId" class="block text-text-secondary mb-2">Goal</label>
          <select
            v-model="form.goalId"
            id="goalId"
            class="w-full p-2 border rounded"
            :class="{ 'border-error': v$.goalId.$error }"
            @blur="v$.goalId.$touch"
          >
            <option v-for="goal in goals" :key="goal.id" :value="goal.id">{{ goal.title }}</option>
          </select>
          <span v-if="v$.goalId.$error" class="text-error text-sm">{{ v$.goalId.$errors[0].$message }}</span>
        </div>
        <div class="mb-4">
          <label for="deadline" class="block text-text-secondary mb-2">Deadline</label>
          <input
            v-model="form.deadline"
            type="date"
            id="deadline"
            class="w-full p-2 border rounded"
            :class="{ 'border-error': v$.deadline.$error }"
            @blur="v$.deadline.$touch"
          />
          <span v-if="v$.deadline.$error" class="text-error text-sm">{{ v$.deadline.$errors[0].$message }}</span>
        </div>
        <div class="flex justify-end">
          <router-link to="/dashboard" class="mr-2 px-4 py-2 text-text">Cancel</router-link>
          <button
            type="submit"
            class="px-4 py-2 bg-primary text-white rounded hover:bg-opacity-90"
            :disabled="v$.$invalid || isLoading"
          >
            <span v-if="isLoading" class="animate-spin inline-block mr-2">⌀</span>
            {{ isEdit ? 'Update' : 'Create' }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useGoalsStore } from '../stores/goals'
import { useTasksStore } from '../stores/tasks'
import { useVuelidate } from '@vuelidate/core'
import { required, helpers } from '@vuelidate/validators'
import type { GoalDTO } from '../types/dtos'

const router = useRouter()
const route = useRoute()
const goalsStore = useGoalsStore()
const tasksStore = useTasksStore()
const isLoading = ref(false)
const goals = ref<GoalDTO[]>([])
const isEdit = ref(false)

const form = ref({
  title: '',
  goalId: null as number | null,
  deadline: '',
})

const today = new Date().toISOString().split('T')[0]
const rules = {
  title: { required, $lazy: true },
  goalId: { required, $lazy: true },
  deadline: {
    required,
    minDate: helpers.withMessage('Deadline must be today or later', (value: string) => value >= today),
    $lazy: true,
  },
}
const v$ = useVuelidate(rules, form)

onMounted(async () => {
  goals.value = await goalsStore.fetchGoals()
  if (route.params.id) {
    isEdit.value = true
    const task = await tasksStore.fetchTask(Number(route.params.id))
    form.value = {
      title: task.title,
      goalId: task.goalId,
      deadline: task.deadline,
    }
  }
})

const handleSubmit = async () => {
  isLoading.value = true
  try {
    if (isEdit.value) {
      await tasksStore.updateTask(Number(route.params.id), form.value)
    } else {
      await tasksStore.createTask(form.value)
    }
    router.push('/')
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped>
</style>
