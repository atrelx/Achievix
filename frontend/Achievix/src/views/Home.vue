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
  </div>
</template>

<script setup lang="ts">
</script>

<style scoped>
</style>
