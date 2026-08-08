<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { LibraryBig, Trees } from 'lucide-vue-next'
import AppLayout from '@/layouts/AppLayout.vue'
import BaseCard from '@/components/ui/BaseCard.vue'
import type { NearbyPlacesResult } from '@/types/domain'
import { getNearbyPlaces } from '@/api/places.service'
import LoadingState from '@/components/shared/LoadingState.vue'
import ErrorState from '@/components/shared/ErrorState.vue'
import LimitationNotice from '@/components/shared/LimitationNotice.vue'

const route = useRoute()
const data = ref<NearbyPlacesResult | null>(null)
const loading = ref(true)
const error = ref('')
onMounted(async () => { try { data.value = await getNearbyPlaces(String(route.params.id), String(route.query.scenario || '')) } catch (e) { error.value = e instanceof Error ? e.message : 'Could not load nearby public places.' } finally { loading.value = false } })
</script>
<template>
  <AppLayout><section class="page-section"><div class="page-shell">
    <div class="eyebrow">Nearby public places</div>
    <LoadingState v-if="loading" class="mt-4" message="Loading eligible nearby places…" />
    <ErrorState v-else-if="error" class="mt-4" explanation="Nearby public places could not be loaded. Return to location detail and try again." />
    <template v-else-if="data">
      <h1 class="page-title">Nearby public places · {{ data.locationName }}</h1>
      <p class="page-subtitle">Up to three eligible places, filtered to Library, Park, Garden or Reserve.</p>
      <LimitationNotice class="mt-4">{{ data.limitation }} Guidance only · {{ data.dataSource }}</LimitationNotice>
      <div v-if="data.places.length" class="place-grid">
        <BaseCard v-for="place in data.places" :key="place.id" as="article" class="place-card">
          <div class="place-visual"><LibraryBig v-if="place.category === 'Library'" :size="42" aria-hidden="true" /><Trees v-else :size="42" aria-hidden="true" /></div>
          <div class="place-content"><h3>{{ place.name }}</h3><p><strong>{{ place.category }}</strong></p><p>Approx. {{ place.approximateDistanceMetres }} m from {{ data.locationName }}</p><p v-if="place.sampleData" class="text-xs">Sample data · illustrative category-based selection</p></div>
        </BaseCard>
      </div>
      <BaseCard v-else class="detail-card mt-5"><h2>No eligible nearby public places</h2><p>No Library, Park, Garden or Reserve result is available near {{ data.locationName }} within the prototype search radius.</p><RouterLink :to="`/location/${data.locationId}`" class="app-link">Return to Location Detail</RouterLink></BaseCard>
      <div class="hero-actions"><RouterLink :to="`/location/${data.locationId}`" class="base-button base-button--secondary">Return to Location Detail</RouterLink><RouterLink :to="`/location/${data.locationId}/history`" class="base-button base-button--ghost">View Historical Patterns</RouterLink></div>
    </template>
  </div></section></AppLayout>
</template>
