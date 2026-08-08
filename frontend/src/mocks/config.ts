export const MOCK_DELAY_MS = 250

export function mockDelay(): Promise<void> {
  return new Promise((resolve) => window.setTimeout(resolve, MOCK_DELAY_MS))
}
