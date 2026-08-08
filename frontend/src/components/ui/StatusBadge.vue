<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{ label: string }>()
const tone = computed(() => props.label.includes('High') || props.label.includes('Higher')
  ? 'high'
  : props.label.includes('Low') || props.label.includes('Lower')
    ? 'low'
    : 'neutral')
</script>
<template><span class="status-badge" :class="`status-badge--${tone}`">{{ label }}</span></template>
