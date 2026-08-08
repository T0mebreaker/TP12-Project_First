<script setup lang="ts">
withDefaults(defineProps<{ message?: string }>(), { message: 'Loading…' })
</script>
<template><div class="state-panel" role="status" aria-live="polite"><span class="state-spinner" aria-hidden="true" />{{ message }}</div></template>
