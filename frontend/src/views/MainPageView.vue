<script setup lang="ts">
import AppLayout from '@/layouts/AppLayout.vue'
import { useRouter } from 'vue-router'
import BaseButton from '@/components/ui/BaseButton.vue'

const router = useRouter()
</script>
<template>
  <AppLayout>
    <section class="hero">
      <div class="page-shell hero-grid">
        <div>
          <div class="eyebrow">Sensory-aware urban travel · Melbourne CBD prototype</div>
          <h1>Plan a walking route with pedestrian activity in view.</h1>
          <p>Compare route options using available City of Melbourne pedestrian data. The experience is designed as guidance for sensory-sensitive commuters, not as a guarantee of quiet or sensory-safe conditions.</p>
          <div class="hero-actions">
            <BaseButton @click="router.push('/login')">Get started</BaseButton>
            <a href="#data-limitations" class="base-button base-button--secondary inline-flex items-center no-underline">View data limitations</a>
          </div>
        </div>
        <div class="hero-visual" aria-label="Abstract illustration of route options on a city map">
          <div class="route-line-art" aria-hidden="true">
            <svg viewBox="0 0 500 320">
              <path d="M40 250 C140 220 150 80 250 110 S360 80 455 45" fill="none" stroke="#1262c4" stroke-width="8" stroke-linecap="round" />
              <path d="M42 252 C105 180 185 245 265 175 S385 145 455 45" fill="none" stroke="#0f766e" stroke-width="6" stroke-dasharray="12 10" stroke-linecap="round" />
              <circle cx="40" cy="250" r="10" fill="#fff" stroke="#1262c4" stroke-width="5" />
              <circle cx="455" cy="45" r="10" fill="#fff" stroke="#0f172a" stroke-width="5" />
            </svg>
          </div>
        </div>
      </div>
    </section>
    <section class="page-section">
      <div class="page-shell detail-grid">
        <section class="base-card detail-card"><h2>Route-level guidance</h2><p>High, Low and Data unavailable are shown as text, using the agreed threshold of more than 60 pedestrians per minute for High.</p></section>
        <section class="base-card detail-card"><h2>Honest limitations</h2><p>Results use available pedestrian data and historical patterns only. Actual conditions may differ and pedestrian counts do not represent every sensory condition.</p></section>
      </div>
    </section>
  </AppLayout>
</template>
