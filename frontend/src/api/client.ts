import axios from 'axios'
import { env } from '@/config/env'

export class ApiClientError extends Error {
  code?: string

  constructor(message: string, code?: string) {
    super(message)
    this.name = 'ApiClientError'
    this.code = code
  }
}

export const apiClient = axios.create({
  baseURL: env.apiBaseUrl,
  timeout: 12000,
  headers: { 'Content-Type': 'application/json' },
})

apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    const message = error?.response?.data?.message || error?.message || 'Unexpected API error'
    const code = error?.response?.data?.code || error?.code
    return Promise.reject(new ApiClientError(message, code))
  },
)
