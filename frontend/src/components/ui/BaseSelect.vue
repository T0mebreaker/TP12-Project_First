<script setup lang="ts">
import { computed, useId } from 'vue'

const props = withDefaults(defineProps<{
  modelValue: string
  label: string
  id?: string
  name?: string
  disabled?: boolean
  required?: boolean
  error?: string
}>(), { disabled: false, required: false, error: '' })

const emit = defineEmits<{ 'update:modelValue': [value: string] }>()
const generatedId = useId()
const selectId = computed(() => props.id || generatedId)
const errorId = computed(() => `${selectId.value}-error`)
</script>

<template>
  <div class="field">
    <label :for="selectId">{{ label }}</label>
    <select :id="selectId" :name="name" :value="modelValue" :disabled="disabled" :required="required" :aria-invalid="Boolean(error)" :aria-describedby="error ? errorId : undefined" @change="emit('update:modelValue', ($event.target as HTMLSelectElement).value)">
      <slot />
    </select>
    <p v-if="error" :id="errorId" class="error-text" role="alert">{{ error }}</p>
  </div>
</template>
