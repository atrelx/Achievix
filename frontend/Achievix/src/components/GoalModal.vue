<template>
  <teleport to="body">
    <div v-if="isVisible"
         class="fixed inset-0 bg-black/70 bg-opacity-50 flex items-center justify-center modal-bg-animation"
         @click.self="close">
    >
      <div
        class="bg-surface p-6 rounded-lg w-full max-w-md modal-content-animation"
        @click.stop
      >
        <h2 class="text-xl font-bold text-text mb-4">Create Goal</h2>
        <form @submit.prevent="handleSubmit">
          <div class="mb-4">
            <label for="title" class="block text-text-secondary mb-2">
              Title <span class="text-8px text-error">*</span>
            </label>
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
          <div v-if="errorMessage" class="text-error text-sm mb-4">{{ errorMessage }}</div>
          <div class="flex justify-end">
            <button
              type="button"
              @click="close"
              class="mr-2 px-4 py-2 text-surface bg-error hover:bg-error/80 rounded cursor-pointer"
            >
              Cancel
            </button>
            <button
              type="submit"
              class="px-4 py-2 bg-primary text-white rounded hover:bg-primary/90 cursor-pointer"
              :disabled="v$.$invalid || isLoading"
            >
              Create
            </button>
          </div>
        </form>
      </div>
    </div>
  </teleport>
</template>

<script setup lang="ts">
import { ref, defineProps, defineEmits } from 'vue'
import { useVuelidate } from '@vuelidate/core'
import { required, minValue, helpers } from '@vuelidate/validators'
import type {CreateGoalDTO} from "@/types/dtos.ts";

defineProps({
  isVisible: Boolean,
})

const emit = defineEmits(['close', 'submit'])

const isLoading = ref(false)
const errorMessage = ref('')
const form = ref<CreateGoalDTO>({
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

const close = () => {
  emit('close')
  form.value = { title: '', targetValue: 1, deadline: '' }
  v$.value.$reset()
}

const handleSubmit = async () => {
  isLoading.value = true
  errorMessage.value = ''
  try {
    emit('submit', form.value)
    console.log('Form submitted:', form.value)
    close()
  } catch (error: any) {
    errorMessage.value = error.message || 'An error occurred while creating the goal.' // Show error
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped>
</style>
