<script setup lang="ts">
withDefaults(defineProps<{
  variant?: 'primary' | 'secondary' | 'ghost'
  type?: 'button' | 'submit'
  disabled?: boolean
  loading?: boolean
  fullWidth?: boolean
}>(), {
  variant: 'primary',
  type: 'button',
  disabled: false,
  loading: false,
  fullWidth: false,
})

defineEmits<{ click: [event: MouseEvent] }>()
</script>

<template>
  <button :type="type" :disabled="disabled || loading" :aria-busy="loading" class="base-button" :class="[`base-button--${variant}`, { 'base-button--full': fullWidth }]" @click="$emit('click', $event)">
    <span v-if="loading" class="base-button__loader" aria-hidden="true" />
    <span class="base-button__content"><slot /></span>
  </button>
</template>
