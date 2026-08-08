<script setup lang="ts">
import { computed } from 'vue'
import type { HistoricalDataPoint } from '@/types/domain'

const props = defineProps<{ points: HistoricalDataPoint[] }>()
const width = 900
const height = 320
const pad = { left: 56, right: 24, top: 24, bottom: 46 }
const maxY = computed(() => Math.max(10, ...props.points.map((p) => p.averagePedestriansPerMinute)) * 1.12)
const x = (hour: number) => pad.left + (hour / 23) * (width - pad.left - pad.right)
const y = (value: number) => pad.top + (1 - value / maxY.value) * (height - pad.top - pad.bottom)
const path = computed(() => props.points.map((p, i) => `${i ? 'L' : 'M'} ${x(p.hour)} ${y(p.averagePedestriansPerMinute)}`).join(' '))
const ticks = computed(() => [0, maxY.value * .25, maxY.value * .5, maxY.value * .75, maxY.value])
</script>
<template>
  <div class="chart-wrap">
    <svg class="chart-svg" :viewBox="`0 0 ${width} ${height}`" role="img" aria-labelledby="history-chart-title history-chart-desc">
      <title id="history-chart-title">Historical pedestrian activity by hour</title>
      <desc id="history-chart-desc">A line chart of average pedestrians per minute by hour. A text summary appears below the chart.</desc>
      <g v-for="tick in ticks" :key="tick">
        <line class="chart-gridline" :x1="pad.left" :x2="width-pad.right" :y1="y(tick)" :y2="y(tick)" />
        <text class="chart-label" :x="pad.left-8" :y="y(tick)+4" text-anchor="end">{{ tick.toFixed(0) }}</text>
      </g>
      <line class="chart-axis" :x1="pad.left" :x2="pad.left" :y1="pad.top" :y2="height-pad.bottom" />
      <line class="chart-axis" :x1="pad.left" :x2="width-pad.right" :y1="height-pad.bottom" :y2="height-pad.bottom" />
      <path class="chart-line" :d="path" />
      <g v-for="point in points.filter((p) => p.hour % 6 === 0 || p.hour === 23)" :key="point.hour">
        <circle class="chart-point" :cx="x(point.hour)" :cy="y(point.averagePedestriansPerMinute)" r="4" />
        <text class="chart-label" :x="x(point.hour)" :y="height-20" text-anchor="middle">{{ point.label }}</text>
      </g>
      <text class="chart-label" x="15" :y="height/2" transform="rotate(-90 15 160)" text-anchor="middle">Pedestrians / minute</text>
    </svg>
  </div>
</template>
