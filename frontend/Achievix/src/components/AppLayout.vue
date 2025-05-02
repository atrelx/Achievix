<template>
  <div class="flex min-h-screen bg-background">
    <NavigationDrawer />
    <main class="flex-1 p-6 ml-0 md:ml-64">
      <router-view />
    </main>
  </div>
</template>

<script setup lang="ts">
import NavigationDrawer from './NavigationDrawer.vue'
</script>

<style scoped>
</style>
