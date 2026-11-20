import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../auth/AuthContext.jsx'
import { errorMessage, fieldErrors } from '../api'

const PASSWORD_RULE = /^(?=.*[A-Za-z])(?=.*\d).{8,72}$/

export default function Register() {
  const { register } = useAuth()
  const navigate = useNavigate()
  const [form, setForm] = useState({ name: '', email: '', password: '', confirm: '' })
  const [errors, setErrors] = useState({})
  const [error, setError] = useState('')
  const [busy, setBusy] = useState(false)

  const update = (field) => (e) => setForm({ ...form, [field]: e.target.value })

  function validate() {
    const found = {}
    if (!form.name.trim()) found.name = 'Name is required.'
    if (!/^\S+@\S+\.\S+$/.test(form.email)) found.email = 'Enter a valid email address.'
    if (!PASSWORD_RULE.test(form.password)) found.password = 'Password must be 8 to 72 characters and contain both letters and numbers.'
    if (form.password !== form.confirm) found.confirm = 'Passwords do not match.'
    return found
  }

  async function handleSubmit(e) {
    e.preventDefault()
    setError('')
    const found = validate()
    setErrors(found)
    if (Object.keys(found).length) return
    setBusy(true)
    try {
      await register(form.name, form.email, form.password)
      navigate('/login', { replace: true, state: { registered: true } })
    } catch (err) {
      setErrors(fieldErrors(err))
      setError(errorMessage(err))
    } finally {
      setBusy(false)
    }
  }

  return (
    <div className="auth-card">
      <h1>Create account</h1>
      <p className="muted">Register as a tourist to plan your Cebu trip</p>
      {error && <div className="alert error">{error}</div>}
      <form onSubmit={handleSubmit} noValidate>
        <label htmlFor="name">Full name</label>
        <input id="name" value={form.name} onChange={update('name')} maxLength={100} />
        {errors.name && <span className="field-error">{errors.name}</span>}
        <label htmlFor="email">Email</label>
        <input id="email" type="email" autoComplete="email" value={form.email} onChange={update('email')} maxLength={100} />
        {errors.email && <span className="field-error">{errors.email}</span>}
        <label htmlFor="password">Password</label>
        <input id="password" type="password" autoComplete="new-password" value={form.password} onChange={update('password')} />
        {errors.password && <span className="field-error">{errors.password}</span>}
        <label htmlFor="confirm">Confirm password</label>
        <input id="confirm" type="password" autoComplete="new-password" value={form.confirm} onChange={update('confirm')} />
        {errors.confirm && <span className="field-error">{errors.confirm}</span>}
        <p className="muted small">We only store your name, email, and a secure hash of your password.</p>
        <button type="submit" disabled={busy}>
          {busy ? 'Creating account...' : 'Register'}
        </button>
      </form>
      <p className="muted">
        Already registered? <Link to="/login">Log in</Link>
      </p>
    </div>
  )
}
