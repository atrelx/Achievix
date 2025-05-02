<template>
  <div class="min-h-screen bg-background flex flex-col items-center justify-center p-6">
    <img src="/src/assets/logo.png" alt="Achievix Logo" class="h-16 mb-6" />
    <h1 class="text-4xl font-bold text-text mb-4">Welcome to Achievix</h1>
    <p class="text-text-secondary text-lg text-center max-w-2xl mb-8">
      Achievix helps you set, track, and achieve your goals. Create tasks, monitor progress, and stay motivated with our intuitive dashboard and real-time updates.
    </p>
    <router-link
      to="/dashboard"
      class="px-6 py-3 bg-primary text-white rounded-lg hover:bg-opacity-90 transition mb-4"
    >
      Check Your Progress
    </router-link>
    <div class="bg-surface p-6 rounded-lg shadow-lg w-full max-w-md">
      <h2 class="text-xl font-bold text-text mb-4">Create a New Goal</h2>
      <form @submit.prevent="createGoal">
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
          <label for="targetValue" class="block text-text-secondary mb-2">Target Value</label>
          <input
            v-model.number="form.targetValue"
            type="number"
            id="targetValue"
            class="w-full p-2 border rounded"
            :class="{ 'border-error': v$.targetValue.$error }"
            @blur="v$.targetValue.$touch"
          />
          <span v-if="v$.targetValue.$error" class="text-error text-sm">{{ v$.targetValue.$errors[0].$message }}</span>
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
        <button
          type="submit"
          class="w-full bg-primary text-white p-2 rounded hover:bg-opacity-90 transition"
          :disabled="v$.$invalid || isLoading"
        >
          <span v-if="isLoading" class="animate-spin inline-block mr-2">⌀</span>
          Create Goal
        </button>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useGoalsStore } from '../stores/goals'
import { useVuelidate } from '@vuelidate/core'
import { required, minValue, helpers } from '@vuelidate/validators'

const router = useRouter()
const goalsStore = useGoalsStore()
const isLoading = ref(false)

const form = ref({
  title: '',
  targetValue: 1,
  deadline: '',
})

const today = new Date().toISOString().split('T')[0]
const rules = {
  title: { required, $lazy: true },
  targetValue: { required, minValue: minValue(1), $lazy: true },
  deadline: {
    required,
    minDate: helpers.withMessage('Deadline must be today or later', (value: string) => value >= today),
    $lazy: true,
  },
}
const v$ = useVuelidate(rules, form)

const createGoal = async () => {
  isLoading.value = true
  try {
    await goalsStore.createGoal(form.value)
    router.push('/dashboard')
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped>
</style>
