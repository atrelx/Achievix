<template>
  <div class="min-h-screen flex items-center justify-center bg-background">
    <div class="bg-surface p-8 rounded-lg shadow-lg w-full max-w-md">
      <h1 class="text-2xl font-bold text-text mb-6">Login</h1>
      <form @submit.prevent="handleLogin">
        <div class="mb-4">
          <label for="email" class="block text-text-secondary mb-2">Email</label>
          <input
            v-model="form.email"
            type="email"
            id="email"
            class="w-full p-2 border rounded focus:outline-none focus:ring-2 focus:ring-primary"
            required
            aria-describedby="email-error"
          />
          <span v-if="errors.email" id="email-error" class="text-error text-sm">{{ errors.email }}</span>
        </div>
        <div class="mb-4">
          <label for="password" class="block text-text-secondary mb-2">Password</label>
          <input
            v-model="form.password"
            type="password"
            id="password"
            class="w-full p-2 border rounded focus:outline-none focus:ring-2 focus:ring-primary"
            required
            aria-describedby="password-error"
          />
          <span v-if="errors.password" id="password-error" class="text-error text-sm">{{ errors.password }}</span>
        </div>
        <button
          type="submit"
          class="w-full bg-primary text-white p-2 rounded hover:bg-opacity-90 transition disabled:opacity-50"
          :disabled="isLoading"
        >
          <span v-if="isLoading" class="animate-spin inline-block mr-2">⌀</span>
          Login
        </button>
      </form>
      <div class="mt-2 text-center">
        <router-link to="/signup" class="text-primary hover:underline">Sign Up</router-link>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import type { AuthRequest } from '../types/dtos'

const form = ref<AuthRequest>({ email: '', password: '' })
const errors = ref<{ email?: string; password?: string }>({})
const isLoading = ref(false)
const router = useRouter()
const authStore = useAuthStore()

const handleLogin = async () => {
  errors.value = {}
  isLoading.value = true
  try {
    await authStore.login(form.value)
    await router.push('/')
  } catch (error) {
    errors.value = { email: 'Invalid credentials', password: 'Invalid credentials' }
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped>
</style>
