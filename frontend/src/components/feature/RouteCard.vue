<script setup lang="ts">
import StatusBadge from '@/components/ui/StatusBadge.vue'
import type { RouteOption } from '@/types/domain'
defineProps<{ route: RouteOption; selected: boolean }>()
const emit = defineEmits<{ select: [] }>()

function sensoryTone(route: RouteOption): 'high' | 'low' | 'unavailable' {
  if (route.dataStatus === 'unavailable') return 'unavailable'
  return route.sensoryClassification === 'Low' ? 'low' : 'high'
}
</script>
<template>
  <button class="route-card" type="button" :aria-pressed="selected" @click="emit('select')">
    <div class="route-card__top">
      <h3>{{ route.name }}<span v-if="selected" class="route-card__selected"> · Selected</span></h3>
      <StatusBadge :label="route.dataStatus === 'unavailable' ? 'Data unavailable' : route.sensoryClassification" :tone="sensoryTone(route)" />
    </div>
    <p>{{ route.walkingTimeMinutes }} min · {{ route.distanceKm.toFixed(1) }} km<span v-if="route.averagePedestriansPerMinute !== null"> · Avg {{ route.averagePedestriansPerMinute.toFixed(1) }}/min</span></p>
    <p v-if="route.isLowerStimulationAlternative"><strong>Lower-stimulation alternative</strong> based on available pedestrian data.</p>
    <p v-else-if="route.dataStatus === 'unavailable'">Data unavailable — no usable pedestrian data was found for this route.</p>
  </button>
</template>
