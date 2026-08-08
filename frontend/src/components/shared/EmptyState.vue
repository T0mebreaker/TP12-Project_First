<script setup lang="ts">
withDefaults(defineProps<{ heading?: string; explanation: string }>(), { heading: 'No results' })
</script>
<template><section class="state-panel"><div><h2>{{ heading }}</h2><p>{{ explanation }}</p><slot name="action" /></div></section></template>
