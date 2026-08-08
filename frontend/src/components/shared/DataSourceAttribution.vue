<script setup lang="ts">
defineProps<{ source: string; href?: string }>()
</script>
<template><p class="data-source">Source: <a v-if="href" :href="href" rel="noreferrer">{{ source }}</a><span v-else>{{ source }}</span></p></template>
