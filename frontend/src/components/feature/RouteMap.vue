<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref, watch } from 'vue'
import L from 'leaflet'
import 'leaflet/dist/leaflet.css'
import type { RouteOption } from '@/types/domain'

const props = defineProps<{ routes: RouteOption[]; selectedRouteId: string | null }>()
const emit = defineEmits<{ select: [id: string] }>()
const mapElement = ref<HTMLElement | null>(null)
const failed = ref(false)
let map: L.Map | null = null
let routeLayers: L.Polyline[] = []
let markerLayers: L.CircleMarker[] = []

const lineStyles = [
  { color: '#2563eb', weight: 5, opacity: 0.9 },
  { color: '#0f766e', weight: 4, opacity: 0.85, dashArray: '8 6' },
  { color: '#64748b', weight: 4, opacity: 0.8, dashArray: '4 7' },
]

function renderRoutes() {
  if (!map) return
  routeLayers.forEach((layer) => layer.remove())
  markerLayers.forEach((layer) => layer.remove())
  routeLayers = []
  markerLayers = []
  const bounds: L.LatLngExpression[] = []

  props.routes.forEach((route, index) => {
    const latLngs = route.geometry.coordinates.map((c) => [c.latitude, c.longitude] as L.LatLngExpression)
    bounds.push(...latLngs)
    const style = lineStyles[index % lineStyles.length]
    const selected = route.id === props.selectedRouteId
    const polyline = L.polyline(latLngs, { ...style, weight: selected ? style.weight + 2 : style.weight }).addTo(map!)
    polyline.bindTooltip(`${route.name} · ${route.sensoryClassification}`, { permanent: true, direction: 'center', className: 'route-map-label' })
    polyline.on('click', () => emit('select', route.id))
    routeLayers.push(polyline)
  })

  const first = props.routes[0]?.geometry.coordinates[0]
  const lastCoordinates = props.routes[0]?.geometry.coordinates
  const last = lastCoordinates?.[lastCoordinates.length - 1]
  if (first) markerLayers.push(L.circleMarker([first.latitude, first.longitude], { radius: 7, color: '#1262c4', fillOpacity: 1 }).addTo(map))
  if (last) markerLayers.push(L.circleMarker([last.latitude, last.longitude], { radius: 7, color: '#0f172a', fillOpacity: 1 }).addTo(map))
  if (bounds.length) map.fitBounds(bounds as L.LatLngBoundsExpression, { padding: [24, 24] })
}

onMounted(() => {
  try {
    if (!mapElement.value) return
    map = L.map(mapElement.value, { zoomControl: true, scrollWheelZoom: false }).setView([-37.814, 144.963], 15)
    const tileUrl = import.meta.env.VITE_MAP_TILE_URL || 'https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png'
    const attribution = import.meta.env.VITE_MAP_ATTRIBUTION || '&copy; OpenStreetMap contributors'
    const tiles = L.tileLayer(tileUrl, { maxZoom: 19, attribution }).addTo(map)
    tiles.on('tileerror', () => { failed.value = true })
    renderRoutes()
  } catch {
    failed.value = true
  }
})

watch(() => [props.routes, props.selectedRouteId] as const, renderRoutes, { deep: true })
onBeforeUnmount(() => map?.remove())
</script>

<template>
  <div v-if="failed" class="map-fallback" role="status">
    <div><strong>Map tiles are unavailable.</strong><br />Use the accessible route list below; route status, time and distance remain available.</div>
  </div>
  <div v-else ref="mapElement" class="route-map" aria-label="Map showing route options. Equivalent route information is available in the route list below." />
</template>
