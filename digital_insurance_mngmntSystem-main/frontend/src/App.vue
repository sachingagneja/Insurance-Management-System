
<script setup lang="ts">
import NavBar from './components/core/NavBar.vue';
</script>

<template>
  <div class="app-container">
    <header class="app-header">
      <NavBar />
    </header>

    <main class="app-content">
      <router-view />
    </main>
  </div>
</template>
