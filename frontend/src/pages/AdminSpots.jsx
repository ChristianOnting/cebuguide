import { useCallback, useEffect, useState } from 'react'
import { Link, useLocation } from 'react-router-dom'
import api, { errorMessage } from '../api'

export default function AdminSpots() {
  const location = useLocation()
  const [spots, setSpots] = useState(null)
  const [q, setQ] = useState('')
  const [status, setStatus] = useState('ALL')
  const [message, setMessage] = useState(location.state?.saved ? 'The spot has been saved.' : '')
  const [error, setError] = useState('')

  const load = useCallback(() => {
    api
      .get('/admin/spots', { params: { q, status } })
      .then((res) => {
        setSpots(res.data)
        setError('')
      })
      .catch((err) => setError(errorMessage(err)))
  }, [q, status])

  // Search as the administrator types (with a short pause).
  useEffect(() => {
    const timer = setTimeout(load, 250)
    return () => clearTimeout(timer)
  }, [load])

  async function toggle(spot) {
    const deactivating = spot.active
    const question = deactivating
      ? `Deactivate "${spot.name}"? Tourists will no longer see it, and saved itineraries will show it as "No longer available".`
      : `Reactivate "${spot.name}"? It will appear again in the directory.`
    if (!window.confirm(question)) return
    try {
      await api.post(`/admin/spots/${spot.id}/${deactivating ? 'deactivate' : 'reactivate'}`)
      setMessage(`"${spot.name}" was ${deactivating ? 'deactivated' : 'reactivated'}.`)
      load()
    } catch (err) {
      setError(errorMessage(err))
    }
  }

  return (
    <>
      <h1>Spots</h1>
      <p className="muted">Add, edit, and deactivate tourist spots</p>
      <div className="toolbar">
        <input type="search" placeholder="Search by name or description" value={q} onChange={(e) => setQ(e.target.value)} aria-label="Search spots" />
        <select value={status} onChange={(e) => setStatus(e.target.value)} aria-label="Status filter">
          <option value="ALL">All spots</option>
          <option value="ACTIVE">Active only</option>
          <option value="INACTIVE">Deactivated only</option>
        </select>
        <span className="spacer" />
        <Link className="link-button" to="/admin/spots/new">
          Add spot
        </Link>
      </div>
      {message && <div className="alert success">{message}</div>}
      {error && <div className="alert error">{error}</div>}
      {!spots ? (
        <p className="center">Loading...</p>
      ) : spots.length === 0 ? (
        <div className="card">No spots found.</div>
      ) : (
        <section className="card table-scroll">
          <table>
            <thead>
              <tr>
                <th>Name</th>
                <th>Category</th>
                <th>Location</th>
                <th>Status</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {spots.map((s) => (
                <tr key={s.id}>
                  <td>
                    {s.name}
                    {s.indoor && <span className="muted small"> (indoor)</span>}
                  </td>
                  <td>
                    <span className="swatch" style={{ background: s.category.markerColor }} />
                    {s.category.name}
                  </td>
                  <td>
                    {s.latitude}, {s.longitude}
                  </td>
                  <td>
                    <span className={`pill ${s.active ? 'active' : 'inactive'}`}>{s.active ? 'Active' : 'Deactivated'}</span>
                  </td>
                  <td>
                    <div className="actions">
                      <Link className="link-button small-link" to={`/admin/spots/${s.id}/edit`}>
                        Edit
                      </Link>
                      <button className={`small ${s.active ? 'danger' : 'ghost'}`} onClick={() => toggle(s)}>
                        {s.active ? 'Deactivate' : 'Reactivate'}
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </section>
      )}
    </>
  )
}
