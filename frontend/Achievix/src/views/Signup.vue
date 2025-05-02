<template>
  <div class="min-h-screen flex items-center justify-center bg-background">
    <div class="bg-surface p-8 rounded-lg shadow-lg w-full max-w-md">
      <h1 class="text-2xl font-bold text-text mb-6">Sign Up</h1>
      <form @submit.prevent="handleSignup">
        <div class="mb-4">
          <label for="email" class="block text-text-secondary mb-2">Email</label>
          <input
            v-model="form.email"
            type="email"
            id="email"
            class="w-full p-2 border rounded"
            :class="{ 'border-error': v$.email.$error }"
            @blur="v$.email.$touch"
            aria-describedby="email-error"
          />
          <span v-if="v$.email.$error" id="email-error" class="text-error text-sm">{{ v$.email.$errors[0].$message }}</span>
        </div>
        <div class="mb-4">
          <label for="password" class="block text-text-secondary mb-2">Password</label>
          <input
            v-model="form.password"
            type="password"
            id="password"
            class="w-full p-2 border rounded"
            :class="{ 'border-error': v$.password.$error }"
            @blur="v$.password.$touch"
            aria-describedby="password-error"
          />
          <span v-if="v$.password.$error" id="password-error" class="text-error text-sm">{{ v$.password.$errors[0].$message }}</span>
        </div>
        <div class="mb-4">
          <label for="confirmPassword" class="block text-text-secondary mb-2">Confirm Password</label>
          <input
            v-model="form.confirmPassword"
            type="password"
            id="confirmPassword"
            class="w-full p-2 border rounded"
            :class="{ 'border-error': v$.confirmPassword.$error }"
            @input="v$.confirmPassword.$touch"
            @blur="v$.confirmPassword.$touch"
            aria-describedby="confirmPassword-error"
          />
          <span v-if="v$.confirmPassword.$error" id="confirmPassword-error" class="text-error text-sm">{{ v$.confirmPassword.$errors[0].$message }}</span>
        </div>
        <button
          type="submit"
          class="w-full bg-primary text-white p-2 rounded hover:bg-opacity-90 transition disabled:opacity-50"
          :disabled="v$.$invalid || isLoading"
        >
          <span v-if="isLoading" class="animate-spin inline-block mr-2">⌀</span>
          Sign Up
        </button>
      </form>
      <div class="mt-4 text-center">
        <router-link to="/login" class="text-primary hover:underline">Already have an account? Log in</router-link>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { useVuelidate } from '@vuelidate/core'
import { required, email, minLength, sameAs } from '@vuelidate/validators'
import { computed } from 'vue'

const form = reactive({
  email: '',
  password: '',
  confirmPassword: '',
})
const isLoading = ref(false)
const router = useRouter()
const authStore = useAuthStore()

// Use computed to get the password value for sameAs validator
const passwordRef = computed(() => form.password)

const rules = {
  email: { required, email, $lazy: true },
  password: { required, minLength: minLength(6), $lazy: true },
  confirmPassword: {
    required,
    sameAs: sameAs(passwordRef, 'Passwords must match'),
    $lazy: true,
  },
}
const v$ = useVuelidate(rules, form)

const handleSignup = async () => {
  isLoading.value = true
  try {
    console.log('Form data:', form)
    await authStore.signup({
      email: form.email,
      password: form.password,
      })
    router.push('/')
  } catch (error) {
    console.error('Signup failed', error)
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped>
</style>
