<script setup lang="ts">
import type { Component } from 'vue'

withDefaults(defineProps<{
  as?: string | Component
  elevated?: boolean
}>(), {
  as: 'div',
  elevated: false,
})
</script>

<template><component :is="as" class="base-card" :class="{ 'base-card--elevated': elevated }"><slot /></component></template>
