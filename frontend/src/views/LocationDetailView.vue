<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { BarChart3, Landmark, TrainFront } from 'lucide-vue-next'
import AppLayout from '@/layouts/AppLayout.vue'
import BaseCard from '@/components/ui/BaseCard.vue'
import type { LocationDetail } from '@/types/domain'
import { getLocationDetail } from '@/api/locations.service'
import LoadingState from '@/components/shared/LoadingState.vue'
import ErrorState from '@/components/shared/ErrorState.vue'
import DataSourceAttribution from '@/components/shared/DataSourceAttribution.vue'
import StatusNotice from '@/components/ui/StatusNotice.vue'
import Timestamp from '@/components/shared/Timestamp.vue'

const route = useRoute()
const data = ref<LocationDetail | null>(null)
const loading = ref(true)
const error = ref('')

onMounted(async () => {
  try { data.value = await getLocationDetail(String(route.params.id), String(route.query.scenario || '')) }
  catch (e) { error.value = e instanceof Error ? e.message : 'Could not load location detail.' }
  finally { loading.value = false }
})
</script>
<template>
  <AppLayout>
    <section class="page-section"><div class="page-shell">
      <div class="eyebrow">Location detail</div>
      <LoadingState v-if="loading" class="mt-4" message="Loading latest pedestrian activity…" />
      <ErrorState v-else-if="error" class="mt-4" explanation="The location detail could not be loaded. Return to the route planner and try again." />
      <template v-else-if="data">
        <h1 class="page-title">{{ data.name }}</h1>
        <p class="page-subtitle">Latest available pedestrian activity and next-step guidance.</p>
        <p v-if="data.sampleData" class="kicker mt-3">Sample data · illustrative, not live</p>
        <StatusNotice v-if="data.stale" class="mt-4" tone="warning" title="Historical dataset snapshot">The latest available reading is not live. Timestamp and source context are retained below.</StatusNotice>
        <div class="detail-grid">
          <BaseCard class="detail-card">
            <h2>Current activity summary</h2>
            <div v-if="data.latestPedestriansPerMinute !== null" class="big-number">{{ data.latestPedestriansPerMinute.toFixed(1) }}</div>
            <div v-else class="big-number text-3xl">Data unavailable</div>
            <p class="page-subtitle">pedestrians/min · latest available one-minute observation</p>
            <div class="meta-stack mt-5"><Timestamp v-if="data.latestObservedAt" label="Last updated" :value="data.latestObservedAt" /><span v-else><strong>Last updated:</strong> Not available</span><span><strong>Freshness:</strong> {{ data.dataFreshness }}</span><span><strong>Interpretation:</strong> {{ data.interpretation }}</span></div>
            <DataSourceAttribution :source="data.dataSource" />
          </BaseCard>
          <BaseCard class="detail-card">
            <h2>Data context</h2>
            <p>{{ data.sensoryLimitation }}</p><p class="page-subtitle mt-3">Guidance only. The current local build uses a packaged historical dataset snapshot; it is not live data.</p>
            <div v-if="data.nearbyTransport" class="metric-box mt-5"><TrainFront :size="20" aria-hidden="true" /><div class="mt-2 font-bold">{{ data.nearbyTransport.name }}</div><div class="text-sm text-slate-600">{{ data.nearbyTransport.type }} · about {{ data.nearbyTransport.approximateDistanceMetres }} m</div></div>
          </BaseCard>
        </div>
        <h2 class="mt-7 text-xl font-bold">Next actions</h2>
        <div class="action-grid">
          <RouterLink :to="`/location/${data.id}/history`" class="base-card action-card"><BarChart3 :size="24" aria-hidden="true" /><h3>View Historical Patterns</h3><p>Review hourly pedestrian patterns, higher/lower periods and an illustrative potentially quieter-time insight.</p></RouterLink>
          <RouterLink :to="`/location/${data.id}/nearby`" class="base-card action-card"><Landmark :size="24" aria-hidden="true" /><h3>View Nearby Public Places</h3><p>See up to three nearby libraries, parks, gardens or reserves based on public category data.</p></RouterLink>
        </div>
      </template>
    </div></section>
  </AppLayout>
</template>
