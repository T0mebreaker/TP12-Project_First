<script setup lang="ts">
import AppHeader from '@/components/shared/AppHeader.vue'
</script>
<template>
  <div class="min-h-screen bg-slate-50 text-slate-800"><a class="skip-link" href="#main-content">Skip to main content</a>
    <AppHeader /><main id="main-content"><slot /></main></div>
</template>
