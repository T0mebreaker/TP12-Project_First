<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { BarChart3, Landmark, TrainFront } from 'lucide-vue-next'
import AppLayout from '@/layouts/AppLayout.vue'
import BaseCard from '@/components/ui/BaseCard.vue'
import type { LocationDetail } from '@/types/domain'
import { getLocationDetail } from '@/api/locations.service'

const route = useRoute()
const data = ref<LocationDetail | null>(null)
const loading = ref(true)
const error = ref('')

onMounted(async () => {
  try { data.value = await getLocationDetail(String(route.params.id)) }
  catch (e) { error.value = e instanceof Error ? e.message : 'Could not load location detail.' }
  finally { loading.value = false }
})
</script>
<template>
  <AppLayout>
    <section class="page-section"><div class="page-shell">
      <div class="eyebrow">Location detail</div>
      <div v-if="loading" class="base-card detail-card mt-4" role="status">Loading latest pedestrian activity…</div>
      <div v-else-if="error" class="notice notice--error" role="alert">{{ error }}</div>
      <template v-else-if="data">
        <h1 class="page-title">{{ data.name }}</h1>
        <p class="page-subtitle">Latest available pedestrian activity and next-step guidance.</p>
        <div v-if="data.stale" class="notice notice--warning"><strong>Historical dataset snapshot.</strong> The latest available reading is not live. Timestamp and source context are retained below.</div>
        <div class="detail-grid">
          <BaseCard class="detail-card">
            <h2>Current activity summary</h2>
            <div v-if="data.latestPedestriansPerMinute !== null" class="big-number">{{ data.latestPedestriansPerMinute.toFixed(1) }}</div>
            <div v-else class="big-number text-3xl">Data unavailable</div>
            <p class="page-subtitle">pedestrians/min · latest available one-minute observation</p>
            <div class="meta-stack mt-5"><span><strong>Last available:</strong> {{ data.latestObservedAt || 'Not available' }}</span><span><strong>Freshness:</strong> {{ data.dataFreshness }}</span><span><strong>Interpretation:</strong> {{ data.interpretation }}</span><span><strong>Source:</strong> {{ data.dataSource }}</span></div>
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
