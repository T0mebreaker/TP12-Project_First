<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { LibraryBig, Trees } from 'lucide-vue-next'
import AppLayout from '@/layouts/AppLayout.vue'
import BaseCard from '@/components/ui/BaseCard.vue'
import type { NearbyPlacesResult } from '@/types/domain'
import { getNearbyPlaces } from '@/api/places.service'

const route = useRoute()
const data = ref<NearbyPlacesResult | null>(null)
const loading = ref(true)
const error = ref('')
onMounted(async () => { try { data.value = await getNearbyPlaces(String(route.params.id)) } catch (e) { error.value = e instanceof Error ? e.message : 'Could not load nearby public places.' } finally { loading.value = false } })
</script>
<template>
  <AppLayout><section class="page-section"><div class="page-shell">
    <div class="eyebrow">Nearby public places</div>
    <div v-if="loading" class="base-card detail-card mt-4" role="status">Loading eligible nearby places…</div>
    <div v-else-if="error" class="notice notice--error" role="alert">{{ error }}</div>
    <template v-else-if="data">
      <h1 class="page-title">Nearby public places · {{ data.locationName }}</h1>
      <p class="page-subtitle">Up to three eligible places, filtered to Library, Park, Garden or Reserve.</p>
      <div class="notice">{{ data.limitation }} Guidance only · {{ data.dataSource }}</div>
      <div v-if="data.places.length" class="place-grid">
        <BaseCard v-for="place in data.places" :key="place.id" class="place-card">
          <div class="place-visual"><LibraryBig v-if="place.category === 'Library'" :size="42" aria-hidden="true" /><Trees v-else :size="42" aria-hidden="true" /></div>
          <div class="place-content"><h3>{{ place.name }}</h3><p><strong>{{ place.category }}</strong></p><p>Approx. {{ place.approximateDistanceMetres }} m from selected location</p><p v-if="place.sampleData" class="text-xs">Dataset-derived location · prototype display</p></div>
        </BaseCard>
      </div>
      <BaseCard v-else class="detail-card mt-5"><h2>No eligible nearby public places</h2><p>No Library, Park, Garden or Reserve result is available within the prototype search radius for this location.</p></BaseCard>
      <div class="hero-actions"><RouterLink :to="`/location/${data.locationId}`" class="base-button base-button--secondary">Return to Location Detail</RouterLink><RouterLink :to="`/location/${data.locationId}/history`" class="base-button base-button--ghost">View Historical Patterns</RouterLink></div>
    </template>
  </div></section></AppLayout>
</template>
