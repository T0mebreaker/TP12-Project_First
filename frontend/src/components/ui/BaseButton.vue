<script setup lang="ts">
withDefaults(defineProps<{
  variant?: 'primary' | 'secondary' | 'ghost'
  type?: 'button' | 'submit'
  disabled?: boolean
}>(), {
  variant: 'primary',
  type: 'button',
  disabled: false,
})
</script>

<template>
  <button :type="type" :disabled="disabled" class="base-button" :class="`base-button--${variant}`"><slot /></button>
</template>
