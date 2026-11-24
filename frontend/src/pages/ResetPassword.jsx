import { useEffect, useState } from 'react'
import { Link, useNavigate, useSearchParams } from 'react-router-dom'
import api, { errorMessage, fieldErrors } from '../api'

const PASSWORD_RULE = /^(?=.*[A-Za-z])(?=.*\d).{8,72}$/

export default function ResetPassword() {
  const [params] = useSearchParams()
  const token = params.get('token') || ''
  const navigate = useNavigate()
  const [status, setStatus] = useState('checking') // checking | valid | invalid
  const [form, setForm] = useState({ newPassword: '', confirmPassword: '' })
  const [errors, setErrors] = useState({})
  const [error, setError] = useState('')
  const [invalidMessage, setInvalidMessage] = useState('')
  const [busy, setBusy] = useState(false)

  // Check the link as soon as the page opens, so an expired link is reported before the user types anything.
  useEffect(() => {
    if (!token) {
      setInvalidMessage('This password reset link is invalid. Please request a new one.')
      setStatus('invalid')
      return
    }
    api
      .get('/auth/reset-password/validate', { params: { token } })
      .then(() => setStatus('valid'))
      .catch((err) => {
        setInvalidMessage(errorMessage(err))
        setStatus('invalid')
      })
  }, [token])

  const update = (field) => (e) => setForm({ ...form, [field]: e.target.value })

  function validate() {
    const found = {}
    if (!PASSWORD_RULE.test(form.newPassword)) found.newPassword = 'Password must be 8 to 72 characters and contain both letters and numbers.'
    if (form.newPassword !== form.confirmPassword) found.confirmPassword = 'Passwords do not match.'
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
      await api.post('/auth/reset-password', { token, ...form })
      navigate('/login', { replace: true, state: { passwordReset: true } })
    } catch (err) {
      setErrors(fieldErrors(err))
      setError(errorMessage(err))
    } finally {
      setBusy(false)
    }
  }

  if (status === 'checking') return <p className="center">Checking your reset link...</p>

  if (status === 'invalid') {
    return (
      <div className="auth-card">
        <h1>Link not valid</h1>
        <div className="alert error">{invalidMessage}</div>
        <p className="muted">
          <Link to="/forgot-password">Request a new reset link</Link>
        </p>
      </div>
    )
  }

  return (
    <div className="auth-card">
      <h1>Choose a new password</h1>
      <p className="muted">This link can be used only once.</p>
      {error && <div className="alert error">{error}</div>}
      <form onSubmit={handleSubmit} noValidate>
        <label htmlFor="newPassword">New password</label>
        <input id="newPassword" type="password" autoComplete="new-password" value={form.newPassword} onChange={update('newPassword')} />
        {errors.newPassword && <span className="field-error">{errors.newPassword}</span>}
        <label htmlFor="confirmPassword">Confirm new password</label>
        <input id="confirmPassword" type="password" autoComplete="new-password" value={form.confirmPassword} onChange={update('confirmPassword')} />
        {errors.confirmPassword && <span className="field-error">{errors.confirmPassword}</span>}
        <button type="submit" disabled={busy}>
          {busy ? 'Saving...' : 'Reset password'}
        </button>
      </form>
    </div>
  )
}
