export interface SessionState {
  mode: 'guest' | 'illustrative-sign-in'
  email?: string
}

export async function signIn(email: string, password: string): Promise<SessionState> {
  await new Promise((resolve) => setTimeout(resolve, 350))
  if (!/^\S+@\S+\.\S+$/.test(email)) throw new Error('Enter a valid email address.')
  if (!password) throw new Error('Enter your password.')
  return { mode: 'illustrative-sign-in', email }
}

export function continueAsGuest(): SessionState {
  return { mode: 'guest' }
}
