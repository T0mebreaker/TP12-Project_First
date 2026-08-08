<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { AlertTriangle, Info, MapPin, RefreshCw } from 'lucide-vue-next'
import AppLayout from '@/layouts/AppLayout.vue'
import BaseButton from '@/components/ui/BaseButton.vue'
import BaseCard from '@/components/ui/BaseCard.vue'
import StatusBadge from '@/components/ui/StatusBadge.vue'
import RouteMap from '@/components/feature/RouteMap.vue'
import RouteCard from '@/components/feature/RouteCard.vue'
import { useRouteStore } from '@/stores/route'

const store = useRouteStore()
const route = useRoute()
const router = useRouter()
const originId = ref('5')
const destinationId = ref('3')
const departureTime = ref('2025-10-21T17:30')
const validation = ref('')
const scenario = computed(() => (route.query.scenario as string | undefined) || undefined)
const selected = computed(() => store.selectedRoute)
const hasLow = computed(() => store.response?.routes.some((r) => r.sensoryClassification === 'Low') ?? false)
const allValidHigh = computed(() => {
  const valid = store.response?.routes.filter((r) => r.dataStatus === 'available') || []
  return valid.length > 0 && valid.every((r) => r.sensoryClassification === 'High')
})

onMounted(async () => {
  await store.loadLocations()
  if (store.locations.length && !store.locations.some((l) => l.id === originId.value)) originId.value = store.locations[0].id
  if (store.locations.length > 1 && !store.locations.some((l) => l.id === destinationId.value)) destinationId.value = store.locations[1].id
})

watch(scenario, () => store.clear())

async function submit() {
  validation.value = ''
  if (!originId.value || !destinationId.value || !departureTime.value) {
    validation.value = 'Origin, destination and departure time are required.'
    return
  }
  if (originId.value === destinationId.value) {
    validation.value = 'Choose two different supported locations.'
    return
  }
  await store.search({ originId: originId.value, destinationId: destinationId.value, departureTime: departureTime.value, scenario: scenario.value })
}

async function reviseSearch() {
  store.clear()
  await router.replace('/home')
  window.setTimeout(() => document.querySelector<HTMLElement>('#origin')?.focus(), 0)
}

function focusRouteOptions() {
  window.setTimeout(() => document.querySelector<HTMLElement>('#route-list-heading')?.focus(), 0)
}

function returnToOptions() {
  if (store.response?.routes[0]) store.selectRoute(store.response.routes[0].id)
  focusRouteOptions()
}

function reviewRoutes() {
  const low = store.response?.routes.find((r) => r.sensoryClassification === 'Low')
  if (low) store.selectRoute(low.id)
  focusRouteOptions()
}

async function goToLocation() {
  const origin = store.response?.origin
  if (!origin) return
  await router.push(`/location/${origin.id}`)
}
</script>
<template>
  <AppLayout>
    <section class="page-section">
      <div class="page-shell">
        <div class="eyebrow">Explore</div>
        <h1 class="page-title">Plan a sensory-aware walking route</h1>
        <p class="page-subtitle">Enter supported Melbourne CBD locations and compare route options using available pedestrian data.</p>

        <form class="search-panel" @submit.prevent="submit">
          <div class="search-grid">
            <div class="field"><label for="origin">From</label><select id="origin" v-model="originId"><option v-for="location in store.locations" :key="location.id" :value="location.id">{{ location.name }}</option></select></div>
            <div class="field"><label for="destination">To</label><select id="destination" v-model="destinationId"><option v-for="location in store.locations" :key="location.id" :value="location.id">{{ location.name }}</option></select></div>
            <div class="field"><label for="depart">Depart</label><input id="depart" v-model="departureTime" type="datetime-local" /></div>
            <BaseButton type="submit" :disabled="store.loading">{{ store.loading ? 'Generating…' : 'Generate routes' }}</BaseButton>
          </div>
          <p v-if="validation" class="error-text" role="alert">{{ validation }}</p>
        </form>

        <div v-if="store.errorCode === 'OUTSIDE_COVERAGE'" class="notice notice--warning" role="alert"><AlertTriangle :size="16" aria-hidden="true" /><div><strong>Outside supported Melbourne CBD coverage.</strong><br />{{ store.error }}</div><BaseButton variant="secondary" @click="reviseSearch">Revise search</BaseButton></div>
        <div v-else-if="store.error" class="notice notice--error" role="alert"><AlertTriangle :size="16" aria-hidden="true" />{{ store.error }} <BaseButton variant="ghost" @click="submit"><RefreshCw :size="15" class="inline" /> Retry</BaseButton></div>
        <div v-else-if="store.response?.peakHour.isPeakHour" class="notice"><Info :size="16" aria-hidden="true" /><strong>{{ store.response.peakHour.label }}</strong> · Same High/Low threshold applies during peak hours.</div>
        <div v-else-if="store.response" class="notice"><Info :size="16" aria-hidden="true" />Route check · Available pedestrian data · Guidance only.</div>
        <div v-if="store.response && store.response.scenario !== 'DATA_DRIVEN'" class="notice notice--warning" role="status"><Info :size="16" aria-hidden="true" /><strong>Illustrative demo scenario: {{ store.response.scenario }}</strong> · Values may be intentionally overridden so the acceptance-criteria state can be demonstrated.</div>

        <template v-if="store.response && selected">
          <div v-if="allValidHigh" class="notice notice--warning" role="status"><AlertTriangle :size="16" aria-hidden="true" /><strong>No lower-stimulation alternative is currently available<span v-if="store.response.peakHour.isPeakHour"> for the selected time</span>.</strong></div>
          <div class="route-workspace">
            <BaseCard class="map-panel"><RouteMap :routes="store.response.routes" :selected-route-id="store.selectedRouteId" @select="store.selectRoute" /></BaseCard>
            <BaseCard class="route-summary">
              <div class="kicker">Selected route</div>
              <h2>{{ selected.name }}</h2>
              <p class="page-subtitle">{{ store.response.origin.name }} → {{ store.response.destination.name }}</p>
              <div class="metric-box"><div class="metric-label">Sensory indicator</div><div class="metric-value">{{ selected.sensoryClassification }} <StatusBadge :label="selected.sensoryClassification" /></div></div>
              <div class="meta-stack">
                <span>{{ selected.walkingTimeMinutes }} min walk · {{ selected.distanceKm.toFixed(1) }} km</span>
                <span v-if="selected.averagePedestriansPerMinute !== null">Average count: {{ selected.averagePedestriansPerMinute.toFixed(1) }} pedestrians/min</span>
                <span>{{ selected.classificationThreshold }}</span>
                <span v-if="selected.highCongestionSegment">High-congestion segment: {{ selected.highCongestionSegment }}</span>
              </div>
              <div v-if="selected.prediction.status !== 'Prediction unavailable'" class="metric-box">
                <div class="metric-label">Next-hour historical prediction</div>
                <div class="font-bold mt-1">{{ selected.prediction.status }}</div>
                <div class="text-sm text-slate-600 mt-2">{{ selected.prediction.comparableReadingCount }} comparable readings · {{ selected.prediction.timeframe }}</div>
                <div v-if="selected.prediction.predictedAveragePedestriansPerMinute !== null" class="text-sm text-slate-600 mt-1">Predicted average: {{ selected.prediction.predictedAveragePedestriansPerMinute.toFixed(1) }} pedestrians/min</div>
                <div class="text-xs text-slate-500 mt-2">Eligibility requires at least 4 comparable readings from the same sensor, same hour and same weekday/weekend type.</div>
                <div class="text-xs text-slate-500 mt-2">{{ selected.prediction.limitation }}</div>
              </div>
              <div v-else class="metric-box"><div class="metric-label">Prediction status</div><div class="font-bold mt-1">Prediction unavailable</div><div class="text-sm text-slate-600 mt-2">Insufficient comparable historical data.</div></div>
              <div v-if="allValidHigh" class="flex gap-2 flex-wrap"><BaseButton @click="goToLocation">Continue with warning</BaseButton><BaseButton variant="secondary" @click="returnToOptions">Return to route options</BaseButton></div>
              <BaseButton v-else @click="goToLocation"><MapPin :size="16" class="inline mr-1" />View starting location details</BaseButton>
            </BaseCard>
          </div>

          <section class="route-list-section" aria-labelledby="route-list-heading">
            <div class="section-heading-row"><h2 id="route-list-heading" tabindex="-1">Accessible route results</h2><p>The same route information is available without using the map.</p></div>
            <div class="route-list">
              <RouteCard v-for="routeOption in store.response.routes" :key="routeOption.id" :route="routeOption" :selected="routeOption.id === store.selectedRouteId" @select="store.selectRoute(routeOption.id)" />
            </div>
            <div class="legend"><div class="legend__items"><span class="legend__dot">High</span><span class="legend__dot">Low</span><span class="legend__dot">Data unavailable</span></div><span>{{ store.response.limitation }}</span></div>
          </section>
          <div v-if="selected.prediction.status === 'Higher pedestrian activity likely'" class="notice notice--warning" role="status"><AlertTriangle :size="16" /><strong>Next-hour predictive alert · Higher pedestrian activity likely</strong> · {{ selected.prediction.affectedArea }} · {{ selected.prediction.timeframe }} <BaseButton variant="secondary" @click="reviewRoutes">Review routes</BaseButton></div>
          <div v-if="!hasLow && !allValidHigh" class="notice">No Low route is present in the current result set because usable pedestrian data is limited.</div>
        </template>
        <div v-else-if="store.loading" class="base-card detail-card mt-4" role="status">Generating route options…</div>
        <div v-else class="base-card detail-card mt-4"><h2>Supported sensor locations</h2><p class="page-subtitle">Choose two locations above, then generate routes. The backend loads the cleaned FIT5120 pedestrian datasets included with this project.</p></div>
      </div>
    </section>
  </AppLayout>
</template>
