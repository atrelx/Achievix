<template>
  <teleport to ="body">
    <div
      class="fixed inset-0 bg-black/70 bg-opacity-50 flex items-center justify-center modal-bg-animation">
      <div class="bg-surface p-6 rounded-lg w-full max-w-md">
        <h2 class="text-xl font-bold text-text mb-4">{{ title }}</h2>
        <p class="text-text-secondary mb-6">{{ message }}</p>
        <div class="flex justify-end">
          <button @click="$emit('cancel')" class="mr-2 px-4 py-2 text-text hover:bg-gray-100 rounded">Cancel</button>
          <button @click="$emit('confirm')" class="px-4 py-2 bg-error text-white rounded hover:bg-opacity-90">Delete</button>
        </div>
      </div>
    </div>
  </teleport>
</template>

<script setup lang="ts">
defineProps<{
  title: string
  message: string
}>()

defineEmits(['confirm', 'cancel'])
</script>

<style scoped>
</style>
