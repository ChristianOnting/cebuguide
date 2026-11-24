import { useState } from 'react'
import api, { errorMessage, fieldErrors } from '../api'
import { useAuth } from '../auth/AuthContext.jsx'
import { roleLabel } from '../auth/paths.js'

export default function Profile() {
  const { user, updateUser } = useAuth()
  const [form, setForm] = useState({ name: user.name, email: user.email })
  const [errors, setErrors] = useState({})
  const [message, setMessage] = useState('')
  const [error, setError] = useState('')
  const [busy, setBusy] = useState(false)

  const update = (field) => (e) => setForm({ ...form, [field]: e.target.value })

  function validate() {
    const found = {}
    if (!form.name.trim()) found.name = 'Name is required.'
    if (!/^\S+@\S+\.\S+$/.test(form.email)) found.email = 'Enter a valid email address.'
    return found
  }

  async function handleSubmit(e) {
    e.preventDefault()
    setMessage('')
    setError('')
    const found = validate()
    setErrors(found)
    if (Object.keys(found).length) return
    setBusy(true)
    try {
      const { data } = await api.put('/profile', { name: form.name, email: form.email })
      updateUser(data)
      setForm({ name: data.name, email: data.email })
      setMessage('Your profile has been updated.')
    } catch (err) {
      setErrors(fieldErrors(err))
      setError(errorMessage(err))
    } finally {
      setBusy(false)
    }
  }

  return (
    <>
      <h1>My profile</h1>
      <p className="muted">View and edit your account information</p>
      <div className="grid">
        <section className="card">
          <h2>Account</h2>
          <dl>
            <dt>Role</dt>
            <dd>{roleLabel(user.role)}</dd>
            <dt>Member since</dt>
            <dd>{new Date(user.createdAt).toLocaleDateString()}</dd>
          </dl>
        </section>
        <section className="card">
          <h2>Edit profile</h2>
          {message && <div className="alert success">{message}</div>}
          {error && <div className="alert error">{error}</div>}
          <form onSubmit={handleSubmit} noValidate>
            <label htmlFor="name">Full name</label>
            <input id="name" value={form.name} onChange={update('name')} maxLength={100} />
            {errors.name && <span className="field-error">{errors.name}</span>}
            <label htmlFor="email">Email</label>
            <input id="email" type="email" value={form.email} onChange={update('email')} maxLength={100} />
            {errors.email && <span className="field-error">{errors.email}</span>}
            <button type="submit" disabled={busy}>
              {busy ? 'Saving...' : 'Save changes'}
            </button>
          </form>
        </section>
      </div>
    </>
  )
}
