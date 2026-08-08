<script setup lang="ts">
defineProps<{ to: string; accessibleName?: string }>()
</script>

<template><RouterLink :to="to" class="app-link" :aria-label="accessibleName"><slot /></RouterLink></template>
