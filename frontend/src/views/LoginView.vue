<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { LogIn, UserRound } from 'lucide-vue-next'
import AuthLayout from '@/layouts/AuthLayout.vue'
import BaseButton from '@/components/ui/BaseButton.vue'
import { continueAsGuest, signIn } from '@/api/auth.service'

const router = useRouter()
const email = ref('')
const password = ref('')
const error = ref('')
const loading = ref(false)

async function submit() {
  error.value = ''
  loading.value = true
  try {
    const session = await signIn(email.value, password.value)
    sessionStorage.setItem('sensory-session', JSON.stringify(session))
    await router.push('/home')
  } catch (e) {
    error.value = e instanceof Error ? e.message : 'Sign in failed.'
  } finally {
    loading.value = false
  }
}

async function guest() {
  sessionStorage.setItem('sensory-session', JSON.stringify(continueAsGuest()))
  await router.push('/home')
}
</script>
<template>
  <AuthLayout>
    <div class="auth-wrap">
      <section class="base-card auth-card">
        <div class="eyebrow">Prototype entry</div>
        <h1 class="page-title">Continue to sensory-aware route planning</h1>
        <p class="page-subtitle">Sign in is illustrative only. Core pedestrian information remains available to guests.</p>
        <form class="auth-form" @submit.prevent="submit" novalidate>
          <div class="field"><label for="email">Email</label><input id="email" v-model="email" type="email" autocomplete="email" required :aria-invalid="Boolean(error)" aria-describedby="auth-error" /></div>
          <div class="field"><label for="password">Password</label><input id="password" v-model="password" type="password" autocomplete="current-password" required :aria-invalid="Boolean(error)" aria-describedby="auth-error" /></div>
          <p v-if="error" id="auth-error" class="error-text" role="alert">{{ error }}</p>
          <BaseButton type="submit"><LogIn :size="17" aria-hidden="true" class="inline mr-2" />{{ loading ? 'Signing in…' : 'Sign in' }}</BaseButton>
          <BaseButton variant="secondary" @click="guest"><UserRound :size="17" aria-hidden="true" class="inline mr-2" />Continue as guest</BaseButton>
        </form>
        <p class="auth-note">Privacy note: this prototype does not create an account or persist a production authentication token.</p>
      </section>
      <aside class="auth-visual"><div><strong>Melbourne CBD prototype</strong><p class="auth-note">Pedestrian data is used as one source of sensory guidance. It does not represent noise, lighting, odour or every individual sensory need.</p></div></aside>
    </div>
  </AuthLayout>
</template>
