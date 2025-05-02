<template>
  <div class="fixed inset-y-0 left-0 w-64 bg-surface shadow-lg transform transition-transform md:translate-x-0" :class="{ '-translate-x-full': !isOpen }">
    <div class="p-4 flex items-center">
      <router-link to="/">
        <img src="/src/assets/logo.png" alt="Achievix Logo" class="h-15" />
      </router-link>
    </div>
    <nav class="mt-4">
      <router-link to="/dashboard" class="block px-4 py-2 text-text hover:bg-primary hover:text-white">Dashboard</router-link>
      <div class="px-4 py-2 text-text hover:bg-primary hover:text-white cursor-pointer">
        <div class="flex justify-between items-center">
          <span class=" font-semibold">My Goals</span>
          <button @click="openGoalModal" class="hover:bg-secondary rounded-md cursor-pointer" title="Add Goal">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
            </svg>
          </button>
        </div>
        <ul class="mt-2">
          <li v-for="goal in goals" :key="goal.id" class="relative group">
            <router-link :to="`/goals/${goal.id}`" class="block px-2 py-1 text-text hover:bg-primary hover:text-white">{{ goal.title }}</router-link>
            <button
              @click="openDeleteModal(goal.id, goal.title)"
              class="absolute right-2 top-1/2 transform -translate-y-1/2 opacity-0 group-hover:opacity-100 text-error hover:text-error-dark"
              title="Delete Goal"
            >
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" /></svg>
            </button>
          </li>
        </ul>
      </div>
      <div class="border-t border-text mx-3 my-2"></div>
      <button @click="logout" class="block w-full text-left px-4 py-2 text-text hover:bg-primary hover:text-white cursor-pointer">Logout</button>
    </nav>
    <DeleteModal
      v-if="showDeleteModal"
      :title="`Delete ${deleteGoalTitle}?`"
      :message="`Are you sure you want to delete ${deleteGoalTitle}? This action cannot be undone.`"
      @confirm="deleteGoal"
      @cancel="closeDeleteModal"
    />
    <GoalModal
      :isVisible="showGoalModal"
      @close="closeGoalModal"
      @submit="createGoal"
    />
  </div>
</template>

<script setup lang="ts">
import {onMounted, ref} from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { useGoalsStore } from '../stores/goals'
import DeleteModal from './DeleteModal.vue'
import GoalModal from '../components/GoalModal.vue';
import type {CreateGoalDTO} from "../types/dtos.ts";

const isOpen = ref(true)
const authStore = useAuthStore()
const goalsStore = useGoalsStore()
const router = useRouter()

const goals = goalsStore.goals
const showDeleteModal = ref(false)
const deleteGoalId = ref<number | null>(null)
const deleteGoalTitle = ref('')
const showGoalModal = ref(false)

onMounted(async () => {
  await goalsStore.fetchGoals()
})

const logout = async () => {
  try {
    await authStore.logout();
  } catch (error) {
    console.error('Error during logout:', error);
  }
}

const openGoalModal = () => {
  showGoalModal.value = true
}

const closeGoalModal = () => {
  showGoalModal.value = false
}

const openDeleteModal = (id: number, title: string) => {
  deleteGoalId.value = id
  deleteGoalTitle.value = title
  showDeleteModal.value = true
}

const closeDeleteModal = () => {
  showDeleteModal.value = false
  deleteGoalId.value = null
  deleteGoalTitle.value = ''
}

const deleteGoal = async () => {
  if (deleteGoalId.value) {
    await goalsStore.deleteGoal(deleteGoalId.value)
    closeDeleteModal()
  }
}

const createGoal = async (goalData: CreateGoalDTO) => {
  try {
    await goalsStore.createGoal(goalData)
    closeGoalModal()
  } catch (error: any) {
    console.error('Error creating goal:', error.message || error)
    throw error
  }
}
</script>

<style scoped>
</style>
