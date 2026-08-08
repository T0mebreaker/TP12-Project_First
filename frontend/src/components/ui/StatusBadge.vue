<script setup lang="ts">
defineProps<{
  label: string
  tone: 'information' | 'warning' | 'error' | 'success' | 'unavailable' | 'high' | 'low'
}>()
</script>
<template><span class="status-badge" :class="`status-badge--${tone}`">{{ label }}</span></template>
