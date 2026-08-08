import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', name: 'main', component: () => import('@/views/MainPageView.vue'), meta: { title: 'Welcome' } },
    { path: '/login', name: 'login', component: () => import('@/views/LoginView.vue'), meta: { title: 'Continue' } },
    { path: '/home', name: 'home', component: () => import('@/views/HomeView.vue'), meta: { title: 'Route planner' } },
    { path: '/location/:id', name: 'location-detail', component: () => import('@/views/LocationDetailView.vue'), meta: { title: 'Location detail' } },
    { path: '/location/:id/history', name: 'history', component: () => import('@/views/HistoricalTrendView.vue'), meta: { title: 'Historical patterns' } },
    { path: '/location/:id/nearby', name: 'nearby', component: () => import('@/views/NearbyPublicPlacesView.vue'), meta: { title: 'Nearby public places' } },
    { path: '/:pathMatch(.*)*', name: 'not-found', component: () => import('@/views/NotFoundView.vue'), meta: { title: 'Page not found' } },
  ],
  scrollBehavior: () => ({ top: 0 }),
})

router.afterEach((to) => {
  document.title = `${String(to.meta.title ?? 'Melbourne Sensory-Aware Travel')} | Melbourne Sensory-Aware Travel`
  window.setTimeout(() => {
    const main = document.querySelector<HTMLElement>('#main-content')
    if (main) {
      main.setAttribute('tabindex', '-1')
      main.focus({ preventScroll: true })
    }
  }, 0)
})

export default router
