<script setup lang="ts">
import { AlertCircle, AlertTriangle, CheckCircle2, Info } from 'lucide-vue-next'

withDefaults(defineProps<{ tone?: 'information' | 'warning' | 'error' | 'success'; title: string }>(), { tone: 'information' })
const icons = { information: Info, warning: AlertTriangle, error: AlertCircle, success: CheckCircle2 }
</script>

<template><div class="status-notice" :class="`status-notice--${tone}`" :role="tone === 'error' ? 'alert' : 'status'"><component :is="icons[tone]" :size="18" aria-hidden="true" /><div><strong>{{ title }}</strong><div class="status-notice__body"><slot /></div></div></div></template>
