<script setup lang="ts">
import { Search } from 'lucide-vue-next'
import BaseInput from '@/components/ui/BaseInput.vue'

defineProps<{ modelValue: string; label?: string; placeholder?: string }>()
defineEmits<{ 'update:modelValue': [value: string] }>()
</script>

<template><BaseInput :model-value="modelValue" :label="label || 'Search'" type="search" :placeholder="placeholder" @update:model-value="$emit('update:modelValue', $event)"><template #leading><Search :size="18" aria-hidden="true" /></template></BaseInput></template>
