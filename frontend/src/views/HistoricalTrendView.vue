<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import AppLayout from '@/layouts/AppLayout.vue'
import BaseCard from '@/components/ui/BaseCard.vue'
import HistoricalChart from '@/components/feature/HistoricalChart.vue'
import type { HistoricalTrendResult } from '@/types/domain'
import { getHistoricalTrend } from '@/api/history.service'

const route = useRoute()
const data = ref<HistoricalTrendResult | null>(null)
const loading = ref(true)
const error = ref('')
onMounted(async () => { try { data.value = await getHistoricalTrend(String(route.params.id)) } catch (e) { error.value = e instanceof Error ? e.message : 'Could not load history.' } finally { loading.value = false } })
</script>
<template>
  <AppLayout><section class="page-section"><div class="page-shell">
    <div class="eyebrow">Historical evidence</div>
    <div v-if="loading" class="base-card detail-card mt-4" role="status">Loading historical patterns…</div>
    <div v-else-if="error" class="notice notice--error" role="alert">{{ error }}</div>
    <template v-else-if="data">
      <h1 class="page-title">Historical pedestrian patterns · {{ data.locationName }}</h1>
      <p class="page-subtitle">Hourly averages from the available City of Melbourne pedestrian dataset.</p>
      <template v-if="data.available">
        <BaseCard class="chart-card"><HistoricalChart :points="data.points" /><p class="text-sm text-slate-600 mt-3"><strong>Text summary:</strong> {{ data.summary }}</p><p class="text-xs text-slate-500 mt-2">Illustrative/sample historical view. {{ data.dataSource }}</p></BaseCard>
        <div class="period-grid">
          <BaseCard class="period-card"><h3>Higher-activity period</h3><p>{{ data.higherActivityPeriod }}</p></BaseCard>
          <BaseCard class="period-card"><h3>Lower-activity period</h3><p>{{ data.lowerActivityPeriod }}</p></BaseCard>
          <BaseCard class="period-card"><h3>Illustrative insight · Potentially quieter time</h3><p>{{ data.quieterTimeInsight }}</p></BaseCard>
        </div>
        <div class="notice mt-4">{{ data.limitation }}</div>
      </template>
      <BaseCard v-else class="detail-card mt-5"><h2>Insufficient Historical Data</h2><p>There are not enough usable historical readings to generate the chart or a potentially quieter-time insight.</p></BaseCard>
      <div class="hero-actions"><RouterLink :to="`/location/${data.locationId}`" class="base-button base-button--secondary">Return to Location Detail</RouterLink><RouterLink :to="`/location/${data.locationId}/nearby`" class="base-button base-button--ghost">View Nearby Public Places</RouterLink></div>
    </template>
  </div></section></AppLayout>
</template>
