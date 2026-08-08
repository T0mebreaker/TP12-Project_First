<script setup lang="ts">
import { computed, useId } from 'vue'

const props = withDefaults(defineProps<{
  modelValue: string
  label: string
  id?: string
  type?: 'text' | 'email' | 'password' | 'search' | 'datetime-local'
  error?: string
  disabled?: boolean
  readonly?: boolean
  required?: boolean
  name?: string
  autocomplete?: string
  inputmode?: 'none' | 'text' | 'decimal' | 'numeric' | 'tel' | 'search' | 'email' | 'url'
  placeholder?: string
}>(), { type: 'text', error: '', disabled: false, readonly: false, required: false, placeholder: '' })

const emit = defineEmits<{ 'update:modelValue': [value: string] }>()
const generatedId = useId()
const inputId = computed(() => props.id || generatedId)
const errorId = computed(() => `${inputId.value}-error`)
</script>

<template>
  <div class="field">
    <label :for="inputId">{{ label }}</label>
    <div class="input-wrap"><slot name="leading" /><input :id="inputId" :value="modelValue" :type="type" :name="name" :disabled="disabled" :readonly="readonly" :required="required" :autocomplete="autocomplete" :inputmode="inputmode" :placeholder="placeholder" :aria-invalid="Boolean(error)" :aria-describedby="error ? errorId : undefined" @input="emit('update:modelValue', ($event.target as HTMLInputElement).value)" /></div>
    <p v-if="error" :id="errorId" class="error-text" role="alert">{{ error }}</p>
  </div>
</template>
