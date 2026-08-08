<script setup lang="ts">
import AppLayout from '@/layouts/AppLayout.vue'
import { useRouter } from 'vue-router'
import BaseButton from '@/components/ui/BaseButton.vue'
import BaseCard from '@/components/ui/BaseCard.vue'
import { BarChart3, Landmark, Map, Search, ShieldCheck } from 'lucide-vue-next'

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
              <path class="route-line-art__primary" d="M40 250 C140 220 150 80 250 110 S360 80 455 45" fill="none" stroke-width="8" stroke-linecap="round" />
              <path class="route-line-art__alternate" d="M42 252 C105 180 185 245 265 175 S385 145 455 45" fill="none" stroke-width="6" stroke-dasharray="12 10" stroke-linecap="round" />
              <circle class="route-line-art__primary" cx="40" cy="250" r="10" fill="white" stroke-width="5" />
              <circle class="route-line-art__destination" cx="455" cy="45" r="10" fill="white" stroke-width="5" />
            </svg>
          </div>
        </div>
      </div>
    </section>
    <section class="page-section">
      <div class="page-shell section-stack">
        <div><div class="eyebrow">Product value</div><h2 class="section-title">More context before a Melbourne CBD walk</h2><p class="section-intro">Use available public pedestrian information to make a calmer, more informed decision without claiming that any route is guaranteed quiet or sensory-safe.</p></div>
        <div class="feature-grid">
          <BaseCard as="article" class="feature-card"><Map :size="24" aria-hidden="true" /><h3>Compare route information</h3><p>Review walking time, distance and visible High, Low or Data unavailable sensory indicators.</p></BaseCard>
          <BaseCard as="article" class="feature-card"><BarChart3 :size="24" aria-hidden="true" /><h3>Understand historical patterns</h3><p>Explore illustrative hourly trends and cautiously framed potentially quieter-time insights.</p></BaseCard>
          <BaseCard as="article" class="feature-card"><Landmark :size="24" aria-hidden="true" /><h3>Find nearby public places</h3><p>See up to three category-based libraries, parks, gardens or reserves near a selected location.</p></BaseCard>
        </div>
        <section aria-labelledby="how-it-works"><div class="eyebrow">How it works</div><h2 id="how-it-works" class="section-title">A short, transparent planning flow</h2><ol class="steps-grid"><li><Search :size="22" aria-hidden="true" /><strong>1. Choose locations</strong><span>Select supported Melbourne CBD points and a departure time.</span></li><li><Map :size="22" aria-hidden="true" /><strong>2. Review routes</strong><span>Compare the map, accessible route list and historical prediction guidance.</span></li><li><ShieldCheck :size="22" aria-hidden="true" /><strong>3. Decide with context</strong><span>Open location detail, historical patterns or nearby public places.</span></li></ol></section>
        <section class="transparency-panel"><div><div class="eyebrow">Accessibility and transparency</div><h2 class="section-title">Public data, explained honestly</h2><p>Map information is repeated in an accessible list. Status never relies on colour alone. Predictions are illustrative and based on historical patterns, not guarantees of future conditions.</p></div><a href="#data-limitations" class="app-link">Read data limitations</a></section>
        <section class="final-cta"><div><h2>Ready to review sensory-aware route information?</h2><p>Continue with an illustrative sign-in or use guest access for the complete presentation flow.</p></div><BaseButton @click="router.push('/login')">Get started</BaseButton></section>
      </div>
    </section>
  </AppLayout>
</template>
