<script setup lang="ts">
import { computed } from 'vue'
import { Clock3 } from 'lucide-vue-next'
const props = defineProps<{ value: string | Date; label?: string }>()
const date = computed(() => props.value instanceof Date ? props.value : new Date(props.value))
const valid = computed(() => !Number.isNaN(date.value.getTime()))
const formatted = computed(() => valid.value ? new Intl.DateTimeFormat('en-AU', {
  dateStyle: 'medium',
  timeStyle: 'short',
  timeZone: 'Australia/Melbourne',
}).format(date.value) : '')
</script>

<template><span class="timestamp"><Clock3 :size="14" aria-hidden="true" /><span><span v-if="label">{{ label }}: </span><time v-if="valid" :datetime="date.toISOString()">{{ formatted }}</time><span v-else>Not available</span></span></span></template>
