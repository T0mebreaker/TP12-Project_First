<script setup lang="ts">
import { MapPinned } from 'lucide-vue-next'

withDefaults(defineProps<{ variant?: 'app' | 'auth' }>(), { variant: 'app' })
</script>
<template>
  <header class="app-header">
    <div class="page-shell app-header__inner">
      <RouterLink to="/" class="brand-link"><MapPinned :size="18" aria-hidden="true" /> <span>Melbourne Sensory-Aware Travel</span></RouterLink>
      <nav v-if="variant === 'app'" aria-label="Primary navigation" class="app-nav">
        <RouterLink to="/home">Explore</RouterLink>
        <a href="#data-limitations">Data and Limitations</a>
      </nav>
      <a v-else href="#privacy-note" class="app-link">Privacy</a>
    </div>
  </header>
</template>
