<template>
  <div class="min-h-screen bg-background flex flex-col items-center justify-center">
    <img src="/src/assets/logo.png" alt="Achievix Logo" class="h-30" />
    <h1 class="text-5xl font-bold text-text mb-4">Welcome to Achievix 🏆</h1>
    <p class="text-text-secondary text-xl text-center max-w-3xl mb-4">
      Achievix helps you set, track, and achieve your goals. Create tasks, monitor progress, and stay motivated with our intuitive dashboard and real-time updates.
    </p>
    <router-link
      to="/dashboard"
      class="font-semibold text-lg px-6 py-3 bg-primary text-white rounded-lg hover:bg-primary/85 transition mb-6"
    >
      Check Your Progress
    </router-link>
  </div>
</template>

<script setup lang="ts">
</script>

<style scoped>
</style>
