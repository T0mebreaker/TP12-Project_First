<script setup lang="ts">
import AppHeader from '@/components/shared/AppHeader.vue'
import AppFooter from '@/components/shared/AppFooter.vue'
</script>
<template>
  <div class="app-layout">
    <a class="skip-link" href="#main-content">Skip to main content</a>
    <AppHeader />
    <main id="main-content" class="app-main"><slot /></main>
    <AppFooter />
  </div>
</template>
