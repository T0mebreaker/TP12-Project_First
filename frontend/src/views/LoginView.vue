<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { LogIn, UserRound } from 'lucide-vue-next'
import AuthLayout from '@/layouts/AuthLayout.vue'
import BaseButton from '@/components/ui/BaseButton.vue'
import BaseInput from '@/components/ui/BaseInput.vue'
import { continueAsGuest, signIn } from '@/api/auth.service'

const router = useRouter()
const email = ref('')
const password = ref('')
const error = ref('')
const emailError = ref('')
const passwordError = ref('')
const loading = ref(false)

async function submit() {
  error.value = ''
  emailError.value = ''
  passwordError.value = ''
  if (!email.value) emailError.value = 'Enter your email address.'
  else if (!/^\S+@\S+\.\S+$/.test(email.value)) emailError.value = 'Enter a valid email address.'
  if (!password.value) passwordError.value = 'Enter your password.'
  if (emailError.value || passwordError.value) return
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
        <h1 class="page-title">Sign in or continue as a guest</h1>
        <p class="page-subtitle">Core pedestrian information is available without an account.</p>
        <form class="auth-form" @submit.prevent="submit" novalidate>
          <BaseInput id="email" v-model="email" name="email" label="Email" type="email" autocomplete="email" inputmode="email" required :error="emailError" />
          <BaseInput id="password" v-model="password" name="password" label="Password" type="password" autocomplete="current-password" required :error="passwordError" />
          <p v-if="error" id="auth-error" class="error-text" role="alert">{{ error }}</p>
          <BaseButton type="submit" variant="secondary" :loading="loading"><LogIn :size="17" aria-hidden="true" class="inline mr-2" />Sign in</BaseButton>
          <span class="auth-separator">or</span>
          <BaseButton @click="guest"><UserRound :size="17" aria-hidden="true" class="inline mr-2" />Continue as guest</BaseButton>
        </form>
        <p id="privacy-note" class="auth-note">Your email is used only for this illustrative sign-in flow. Core information remains available as a guest.</p>
      </section>
      <aside class="auth-visual"><div><strong>Melbourne CBD prototype</strong><p class="auth-note">Pedestrian data is used as one source of sensory guidance. It does not represent noise, lighting, odour or every individual sensory need.</p></div></aside>
    </div>
  </AuthLayout>
</template>
