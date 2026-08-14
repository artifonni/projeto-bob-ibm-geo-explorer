import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  timeout: 10_000,
  headers: { 'Content-Type': 'application/json' },
})

// Request interceptor
api.interceptors.request.use(
  (config) => config,
  (error) => {
    console.error('[API Request Error]', error)
    return Promise.reject(error)
  }
)

// Response interceptor — log global de erros
api.interceptors.response.use(
  (response) => response,
  (error) => {
    const status = error.response?.status
    const message = error.response?.data?.message ?? error.message

    console.error(`[API Error ${status ?? 'NETWORK'}]`, message)

    if (status === 404) console.warn('[API] Recurso não encontrado:', error.config?.url)
    if (!status)        console.error('[API] Backend offline ou sem resposta')

    return Promise.reject(error)
  }
)

export default api
