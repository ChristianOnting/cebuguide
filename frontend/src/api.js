import axios from 'axios'

export const TOKEN_KEY = 'cg_token'

const api = axios.create({ baseURL: '/api' })

// Attach the login token to every request.
api.interceptors.request.use((config) => {
  const token = sessionStorage.getItem(TOKEN_KEY)
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

// If the server says the token is no longer valid (expired or logged out), go back to the login page.
api.interceptors.response.use(
  (response) => response,
  (error) => {
    const url = error.config?.url || ''
    if (error.response?.status === 401 && !url.includes('/auth/login') && sessionStorage.getItem(TOKEN_KEY)) {
      sessionStorage.removeItem(TOKEN_KEY)
      window.location.assign('/login?expired=1')
    }
    return Promise.reject(error)
  },
)

export function errorMessage(error) {
  const data = error?.response?.data
  if (data?.message) return data.message
  if (error?.response) return 'Something went wrong. Please try again.'
  return 'Cannot reach the server. Is the backend running?'
}

export function fieldErrors(error) {
  return error?.response?.data?.fieldErrors || {}
}

export default api
