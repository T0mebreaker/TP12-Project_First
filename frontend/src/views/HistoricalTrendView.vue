<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import AppLayout from '@/layouts/AppLayout.vue'
import BaseCard from '@/components/ui/BaseCard.vue'
import HistoricalChart from '@/components/feature/HistoricalChart.vue'
import type { HistoricalTrendResult } from '@/types/domain'
import { getHistoricalTrend } from '@/api/history.service'
import LoadingState from '@/components/shared/LoadingState.vue'
import ErrorState from '@/components/shared/ErrorState.vue'
import LimitationNotice from '@/components/shared/LimitationNotice.vue'

const route = useRoute()
const data = ref<HistoricalTrendResult | null>(null)
const loading = ref(true)
const error = ref('')
onMounted(async () => { try { data.value = await getHistoricalTrend(String(route.params.id), String(route.query.scenario || '')) } catch (e) { error.value = e instanceof Error ? e.message : 'Could not load history.' } finally { loading.value = false } })
</script>
<template>
  <AppLayout><section class="page-section"><div class="page-shell">
    <div class="eyebrow">Historical evidence</div>
    <LoadingState v-if="loading" class="mt-4" message="Loading historical patterns…" />
    <ErrorState v-else-if="error" class="mt-4" explanation="Historical information could not be loaded. Return to location detail and try again." />
    <template v-else-if="data">
      <h1 class="page-title">Historical pedestrian patterns · {{ data.locationName }}</h1>
      <p class="page-subtitle">Hourly averages from the available City of Melbourne pedestrian dataset.</p>
      <template v-if="data.available">
        <BaseCard class="chart-card"><p class="kicker">Illustrative historical evidence</p><HistoricalChart :points="data.points" /><p class="text-sm text-slate-600 mt-3"><strong>Plain-language interpretation:</strong> {{ data.summary }}</p><p class="text-xs text-slate-500 mt-2">Sample / illustrative chart. {{ data.dataSource }}</p></BaseCard>
        <div class="period-grid">
          <BaseCard class="period-card"><h3>Higher-activity period</h3><p>{{ data.higherActivityPeriod }}</p></BaseCard>
          <BaseCard class="period-card"><h3>Lower-activity period</h3><p>{{ data.lowerActivityPeriod }}</p></BaseCard>
          <BaseCard class="period-card"><h3>Illustrative insight · Potentially quieter time</h3><p>{{ data.quieterTimeInsight }}</p><p class="mt-2">Based on historical pedestrian patterns. Calculation method is not final. Guidance only.</p></BaseCard>
        </div>
        <LimitationNotice class="mt-4">{{ data.limitation }}</LimitationNotice>
      </template>
      <BaseCard v-else class="detail-card mt-5"><h2>Insufficient historical data</h2><p>There are not enough usable historical readings to generate the chart or a potentially quieter-time insight.</p><RouterLink :to="`/location/${data.locationId}`" class="app-link">Return to Location Detail</RouterLink></BaseCard>
      <div class="hero-actions"><RouterLink :to="`/location/${data.locationId}`" class="base-button base-button--secondary">Return to Location Detail</RouterLink><RouterLink :to="`/location/${data.locationId}/nearby`" class="base-button base-button--ghost">View Nearby Public Places</RouterLink></div>
    </template>
  </div></section></AppLayout>
</template>
