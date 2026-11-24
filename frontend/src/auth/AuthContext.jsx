import { createContext, useCallback, useContext, useEffect, useState } from 'react'
import api, { TOKEN_KEY } from '../api'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null)
  const [loading, setLoading] = useState(Boolean(sessionStorage.getItem(TOKEN_KEY)))

  // Restore the session after a page reload by asking the server who the token belongs to.
  useEffect(() => {
    if (!sessionStorage.getItem(TOKEN_KEY)) return
    api
      .get('/auth/me')
      .then((res) => setUser(res.data))
      .catch(() => sessionStorage.removeItem(TOKEN_KEY))
      .finally(() => setLoading(false))
  }, [])

  const login = useCallback(async (email, password) => {
    const { data } = await api.post('/auth/login', { email, password })
    sessionStorage.setItem(TOKEN_KEY, data.token)
    setUser(data.user)
    return data.user
  }, [])

  const register = useCallback((name, email, password) => api.post('/auth/register', { name, email, password }), [])

  const logout = useCallback(async () => {
    try {
      await api.post('/auth/logout') // the server revokes the token
    } catch {
      // the token may already be invalid; the local session is cleared either way
    }
    sessionStorage.removeItem(TOKEN_KEY)
    setUser(null)
  }, [])

  // Used after the profile is edited so the menu and pages show the new details.
  const updateUser = useCallback((updated) => setUser(updated), [])

  // Clears the local session only (the server has already ended it, for example after a password change).
  const clearSession = useCallback(() => {
    sessionStorage.removeItem(TOKEN_KEY)
    setUser(null)
  }, [])

  return (
    <AuthContext.Provider value={{ user, loading, login, register, logout, updateUser, clearSession }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  return useContext(AuthContext)
}
