import { useState } from 'react'
import { Link } from 'react-router-dom'
import api, { errorMessage, fieldErrors } from '../api'

export default function ForgotPassword() {
  const [email, setEmail] = useState('')
  const [errors, setErrors] = useState({})
  const [error, setError] = useState('')
  const [sentMessage, setSentMessage] = useState('')
  const [busy, setBusy] = useState(false)

  async function handleSubmit(e) {
    e.preventDefault()
    setError('')
    setSentMessage('')
    if (!/^\S+@\S+\.\S+$/.test(email)) {
      setErrors({ email: 'Enter a valid email address.' })
      return
    }
    setErrors({})
    setBusy(true)
    try {
      const { data } = await api.post('/auth/forgot-password', { email })
      setSentMessage(data.message)
    } catch (err) {
      setErrors(fieldErrors(err))
      setError(errorMessage(err))
    } finally {
      setBusy(false)
    }
  }

  return (
    <div className="auth-card">
      <h1>Forgot password</h1>
      <p className="muted">Enter your account email and we will send you a link to choose a new password.</p>
      {sentMessage && <div className="alert success">{sentMessage}</div>}
      {error && <div className="alert error">{error}</div>}
      <form onSubmit={handleSubmit} noValidate>
        <label htmlFor="email">Email</label>
        <input id="email" type="email" autoComplete="email" value={email} onChange={(e) => setEmail(e.target.value)} />
        {errors.email && <span className="field-error">{errors.email}</span>}
        <button type="submit" disabled={busy}>
          {busy ? 'Sending...' : 'Send reset link'}
        </button>
      </form>
      <p className="muted">
        <Link to="/login">Back to log in</Link>
      </p>
    </div>
  )
}
