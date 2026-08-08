import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import type { LocationReference, RouteRequest, RouteResponse } from '@/types/domain'
import { generateRoutes, getSupportedLocations } from '@/api/routes.service'
import { ApiClientError } from '@/api/client'

export const useRouteStore = defineStore('route', () => {
  const locations = ref<LocationReference[]>([])
  const response = ref<RouteResponse | null>(null)
  const selectedRouteId = ref<string | null>(null)
  const loading = ref(false)
  const error = ref<string | null>(null)
  const errorCode = ref<string | null>(null)

  const selectedRoute = computed(() =>
    response.value?.routes.find((route) => route.id === selectedRouteId.value) || response.value?.routes[0] || null,
  )

  async function loadLocations() {
    if (locations.value.length) return
    try {
      locations.value = await getSupportedLocations()
    } catch (e) {
      error.value = e instanceof Error ? e.message : 'Could not load supported locations.'
      errorCode.value = e instanceof ApiClientError ? e.code || null : null
    }
  }

  async function search(request: RouteRequest) {
    loading.value = true
    error.value = null
    errorCode.value = null
    try {
      response.value = await generateRoutes(request)
      selectedRouteId.value = response.value.routes[0]?.id || null
    } catch (e) {
      if (e instanceof ApiClientError && e.code === 'ERR_CANCELED') return
      error.value = e instanceof Error ? e.message : 'Could not generate route options.'
      errorCode.value = e instanceof ApiClientError ? e.code || null : null
      response.value = null
    } finally {
      loading.value = false
    }
  }

  function selectRoute(id: string) {
    selectedRouteId.value = id
  }

  function clear() {
    response.value = null
    selectedRouteId.value = null
    error.value = null
    errorCode.value = null
  }

  return { locations, response, selectedRouteId, selectedRoute, loading, error, errorCode, loadLocations, search, selectRoute, clear }
})
