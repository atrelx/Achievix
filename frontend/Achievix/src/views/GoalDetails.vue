<template>
  <div class="p-6">
    <div v-if="isLoading" class="flex justify-center">
      <span class="animate-spin text-primary text-4xl">⌀</span>
    </div>
    <div v-else-if="goal">
      <h1 class="text-3xl font-bold text-text mb-4">{{ goal.title }}</h1>
      <div class="bg-surface p-6 rounded-lg shadow-lg mb-6">
        <p class="text-text-secondary">Progress: {{ goal.currentValue }} / {{ goal.targetValue }}</p>
        <div class="w-full bg-gray-200 rounded-full h-2.5 mt-2">
          <div
            class="bg-primary h-2.5 rounded-full"
            :style="{ width: `${(goal.currentValue / goal.targetValue) * 100}%`, transition: 'width 0.5s ease-in-out' }"
          ></div>
        </div>
        <p class="text-text-secondary mt-2">Deadline: {{ goal.deadline ? formatDate(goal.deadline) : 'No deadline' }}</p>
      </div>
      <div class="flex justify-between items-center mb-4">
        <h2 class="text-xl font-semibold text-text">Tasks</h2>
        <button
          @click="openTaskModal"
          class="px-4 py-2 bg-primary text-white rounded hover:bg-primary/85 cursor-pointer"
        >
          Add Task
        </button>
      </div>
      <ul class="space-y-4">
        <li v-for="task in goal.tasks" :key="task.id"
            class="p-4 rounded-lg shadow flex justify-between items-center"
            :class="{
          'bg-surface': !task.completed,
          'bg-secondary/20': task.completed
        }"
        >
          <div>
            <p class="text-text font-semibold" :class="{ 'line-through': task.completed }">
              {{ task.title }}
            </p>
            <p class="text-text-secondary">Due: {{ task.deadline ? formatDate(task.deadline) : 'No deadline' }}</p>
          </div>
          <div class="flex space-x-2">
            <button
              v-if="!task.completed"
              @click="completeTask(task.id)"
              class="text-secondary hover:text-secondary-dark cursor-pointer"
              title="Complete Task"
            >
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" /></svg>
            </button>
            <button
              @click="openDeleteModal(task.id, task.title)"
              class="text-error hover:text-error-dark cursor-pointer"
              title="Delete Task"
            >
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" /></svg>
            </button>
          </div>
        </li>
      </ul>
    </div>
    <TaskModal
      v-if="showTaskModal"
      :isVisible="showTaskModal"
      :goalId="goalId"
      @close="closeTaskModal"
      @submit="createTask"
    />
    <DeleteModal
      v-if="showDeleteModal"
      :title="`Delete ${deleteTaskTitle}?`"
      :message="`Are you sure you want to delete ${deleteTaskTitle}? This action cannot be undone.`"
      @confirm="deleteTask"
      @cancel="closeDeleteModal"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useGoalsStore } from '../stores/goals'
import { useTasksStore } from '../stores/tasks'
import type {GoalDetailsDTO, TaskCreateDTO} from '../types/dtos'
import TaskModal from '../components/TaskModal.vue'
import DeleteModal from '../components/DeleteModal.vue'

const route = useRoute()
const goalsStore = useGoalsStore()
const tasksStore = useTasksStore()
const goal = ref<GoalDetailsDTO | null>(null)
const isLoading = ref(true)
const showTaskModal = ref(false)
const showDeleteModal = ref(false)
const deleteTaskId = ref<number | null>(null)
const deleteTaskTitle = ref('')
const goalId = Number(route.params.id)

const formatDate = (date: string) => new Date(date).toLocaleDateString()

onMounted(async () => {
  isLoading.value = true
  try {
    goal.value = await goalsStore.fetchGoalDetails(goalId)
  } finally {
    isLoading.value = false
  }
})

const openTaskModal = () => {
  showTaskModal.value = true
}

const closeTaskModal = () => {
  showTaskModal.value = false
}

const createTask = async (task: TaskCreateDTO) => {
  console.log('Creating task:', task)
  await tasksStore.createTask(task)
  goal.value = await goalsStore.fetchGoalDetails(goalId)
  closeTaskModal()
}

const completeTask = async (taskId: number) => {
  await tasksStore.completeTask(taskId)
  goal.value = await goalsStore.fetchGoalDetails(goalId)
}

const openDeleteModal = (id: number, title: string) => {
  deleteTaskId.value = id
  deleteTaskTitle.value = title
  showDeleteModal.value = true
}

const closeDeleteModal = () => {
  showDeleteModal.value = false
  deleteTaskId.value = null
  deleteTaskTitle.value = ''
}

const deleteTask = async () => {
  if (deleteTaskId.value) {
    await tasksStore.deleteTask(deleteTaskId.value)
    goal.value = await goalsStore.fetchGoalDetails(goalId)
    closeDeleteModal()
  }
}
</script>

<style scoped>
</style>
