import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import api, { errorMessage, fieldErrors } from '../api'
import { useAuth } from '../auth/AuthContext.jsx'

const PASSWORD_RULE = /^(?=.*[A-Za-z])(?=.*\d).{8,72}$/

export default function ChangePassword() {
  const { clearSession } = useAuth()
  const navigate = useNavigate()
  const [form, setForm] = useState({ currentPassword: '', newPassword: '', confirmPassword: '' })
  const [errors, setErrors] = useState({})
  const [error, setError] = useState('')
  const [busy, setBusy] = useState(false)

  const update = (field) => (e) => setForm({ ...form, [field]: e.target.value })

  function validate() {
    const found = {}
    if (!form.currentPassword) found.currentPassword = 'Enter your current password.'
    if (!PASSWORD_RULE.test(form.newPassword)) found.newPassword = 'Password must be 8 to 72 characters and contain both letters and numbers.'
    else if (form.newPassword === form.currentPassword) found.newPassword = 'New password must be different from the current password.'
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
      await api.post('/profile/change-password', form)
      // The server ended this session; log in again with the new password.
      clearSession()
      navigate('/login', { replace: true, state: { passwordChanged: true } })
    } catch (err) {
      setErrors(fieldErrors(err))
      setError(errorMessage(err))
    } finally {
      setBusy(false)
    }
  }

  return (
    <>
      <h1>Change password</h1>
      <p className="muted">You will be asked to log in again after the change.</p>
      <section className="card" style={{ maxWidth: 480 }}>
        {error && <div className="alert error">{error}</div>}
        <form onSubmit={handleSubmit} noValidate>
          <label htmlFor="currentPassword">Current password</label>
          <input id="currentPassword" type="password" autoComplete="current-password" value={form.currentPassword} onChange={update('currentPassword')} />
          {errors.currentPassword && <span className="field-error">{errors.currentPassword}</span>}
          <label htmlFor="newPassword">New password</label>
          <input id="newPassword" type="password" autoComplete="new-password" value={form.newPassword} onChange={update('newPassword')} />
          {errors.newPassword && <span className="field-error">{errors.newPassword}</span>}
          <label htmlFor="confirmPassword">Confirm new password</label>
          <input id="confirmPassword" type="password" autoComplete="new-password" value={form.confirmPassword} onChange={update('confirmPassword')} />
          {errors.confirmPassword && <span className="field-error">{errors.confirmPassword}</span>}
          <button type="submit" disabled={busy}>
            {busy ? 'Changing password...' : 'Change password'}
          </button>
        </form>
      </section>
    </>
  )
}
