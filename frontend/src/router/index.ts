import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', name: 'main', component: () => import('@/views/MainPageView.vue') },
    { path: '/login', name: 'login', component: () => import('@/views/LoginView.vue') },
    { path: '/home', name: 'home', component: () => import('@/views/HomeView.vue') },
    { path: '/location/:id', name: 'location-detail', component: () => import('@/views/LocationDetailView.vue') },
    { path: '/location/:id/history', name: 'history', component: () => import('@/views/HistoricalTrendView.vue') },
    { path: '/location/:id/nearby', name: 'nearby', component: () => import('@/views/NearbyPublicPlacesView.vue') },
    { path: '/:pathMatch(.*)*', name: 'not-found', component: () => import('@/views/NotFoundView.vue') },
  ],
  scrollBehavior: () => ({ top: 0 }),
})

router.afterEach(() => {
  window.setTimeout(() => {
    const main = document.querySelector<HTMLElement>('#main-content')
    if (main) {
      main.setAttribute('tabindex', '-1')
      main.focus({ preventScroll: true })
    }
  }, 0)
})

export default router
