<script setup lang="ts">
import StatusNotice from '@/components/ui/StatusNotice.vue'

defineProps<{ title?: string }>()
</script>
<template><StatusNotice :title="title || 'Data limitation'"><slot /></StatusNotice></template>
