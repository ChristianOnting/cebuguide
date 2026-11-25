import { useEffect, useState } from 'react'
import { Link, useLocation, useNavigate, useSearchParams } from 'react-router-dom'
import { useAuth } from '../auth/AuthContext.jsx'
import { errorMessage } from '../api'
import { dashboardPath } from '../auth/paths.js'

export default function Login() {
  const { login } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()
  const [params] = useSearchParams()
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [busy, setBusy] = useState(false)

  const [flash] = useState(() => sessionStorage.getItem('cg_flash'))
  useEffect(() => {
    sessionStorage.removeItem('cg_flash')
  }, [])

  async function handleSubmit(e) {
    e.preventDefault()
    setError('')
    setBusy(true)
    try {
      const user = await login(email, password)
      navigate(dashboardPath(user.role), { replace: true })
    } catch (err) {
      setError(errorMessage(err))
    } finally {
      setBusy(false)
    }
  }

  return (
    <div className="auth-card">
      <h1>CebuGuide</h1>
      <p className="muted">Log in to your account</p>
      {location.state?.registered && <div className="alert success">Account created. You can log in now.</div>}
      {(location.state?.passwordChanged || flash === 'passwordChanged') && <div className="alert success">Password changed. Log in with your new password.</div>}
      {location.state?.passwordReset && <div className="alert success">Your password has been reset. Log in with your new password.</div>}
      {params.get('expired') && <div className="alert error">Your session has ended. Please log in again.</div>}
      {error && <div className="alert error">{error}</div>}
      <form onSubmit={handleSubmit}>
        <label htmlFor="email">Email</label>
        <input id="email" type="email" autoComplete="email" required value={email} onChange={(e) => setEmail(e.target.value)} />
        <label htmlFor="password">Password</label>
        <input id="password" type="password" autoComplete="current-password" required value={password} onChange={(e) => setPassword(e.target.value)} />
        <button type="submit" disabled={busy}>
          {busy ? 'Logging in...' : 'Log in'}
        </button>
      </form>
      <p className="muted">
        <Link to="/forgot-password">Forgot password?</Link>
      </p>
      <p className="muted">
        No account yet? <Link to="/register">Register</Link>
      </p>
    </div>
  )
}
