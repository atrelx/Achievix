<template>
  <div class="fixed inset-y-0 left-0 w-64 bg-surface shadow-lg transform transition-transform md:translate-x-0" :class="{ '-translate-x-full': !isOpen }">
    <div class="p-4 flex items-center">
      <router-link to="/">
        <img src="/src/assets/logo.png" alt="Achievix Logo" class="h-15" />
      </router-link>
    </div>
    <nav class="mt-4">
      <router-link to="/dashboard" class="block px-4 py-2 text-text hover:bg-primary hover:text-white transition-colors duration-300">Dashboard</router-link>

      <div class="px-4 py-2 text-text cursor-pointer transition-colors duration-300 group/section hover:bg-primary hover:text-white">
        <div class="flex justify-between items-center">
          <span class="font-semibold">My Goals</span>
          <button @click="openGoalModal" class="hover:bg-secondary rounded-md cursor-pointer transition-colors duration-300" title="Add Goal">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
            </svg>
          </button>
        </div>
        <ul class="mt-2">
          <li
            v-for="goal in goals"
            :key="goal.id"
            class="group flex justify-between items-center py-1 hover:bg-secondary/60 transition-colors duration-300"
          >
            <router-link
              :to="`/goals/${goal.id}`"
              class="block px-2 text-text group-hover/section:text-white transition-colors duration-300"
            >
              {{ goal.title }}
            </router-link>
            <button
              @click="openDeleteModal(goal.id, goal.title)"
              class="ml-2 opacity-0 group-hover:opacity-100 text-error hover:text-error-dark transition-colors duration-300 cursor-pointer"
              title="Delete Goal"
            >
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="3" d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          </li>
        </ul>
      </div>

      <div class="border-t border-text mx-3 my-2"></div>
      <button @click="logout" class="block w-full text-left px-4 py-2 text-text hover:bg-primary hover:text-white cursor-pointer transition-colors duration-300">Logout</button>
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
import {computed, onMounted, ref} from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { useGoalsStore } from '../stores/goals'
import DeleteModal from './DeleteModal.vue'
import GoalModal from '../components/GoalModal.vue';
import type {GoalCreateDTO} from "../types/dtos.ts";

const isOpen = ref(true)
const authStore = useAuthStore()
const goalsStore = useGoalsStore()
const router = useRouter()

const goals = computed(() => goalsStore.goals);
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

const createGoal = async (goalData: GoalCreateDTO) => {
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
