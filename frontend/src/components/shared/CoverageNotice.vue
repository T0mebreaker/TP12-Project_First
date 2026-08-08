<script setup lang="ts">
import StatusNotice from '@/components/ui/StatusNotice.vue'

defineProps<{ title?: string }>()
</script>
<template><StatusNotice tone="warning" :title="title || 'Coverage information'"><slot /></StatusNotice></template>
