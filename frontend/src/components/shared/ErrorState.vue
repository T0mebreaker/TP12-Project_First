<script setup lang="ts">
withDefaults(defineProps<{ heading?: string; explanation: string }>(), { heading: 'Something went wrong' })
</script>
<template><section class="state-panel state-panel--error" role="alert"><div><h2>{{ heading }}</h2><p>{{ explanation }}</p><slot name="action" /></div></section></template>
