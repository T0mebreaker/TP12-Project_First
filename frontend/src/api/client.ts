import axios from 'axios'

export class ApiClientError extends Error {
  code?: string

  constructor(message: string, code?: string) {
    super(message)
    this.name = 'ApiClientError'
    this.code = code
  }
}

export const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api',
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
