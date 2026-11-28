import { useEffect, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import api, { errorMessage, fieldErrors } from '../api'

// Allowed area (the approximate bounding box of Cebu Province, FR-009).
const LAT = [9.4, 11.4]
const LON = [123.2, 124.5]

const EMPTY = { name: '', description: '', categoryId: '', address: '', latitude: '', longitude: '', indoor: false }

export default function SpotForm() {
  const { id } = useParams()
  const editing = Boolean(id)
  const navigate = useNavigate()
  const [form, setForm] = useState(EMPTY)
  const [categories, setCategories] = useState([])
  const [errors, setErrors] = useState({})
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(true)
  const [busy, setBusy] = useState(false)

  useEffect(() => {
    const requests = [api.get('/admin/categories')]
    if (editing) requests.push(api.get(`/admin/spots/${id}`))
    Promise.all(requests)
      .then(([cats, spot]) => {
        setCategories(cats.data)
        if (spot) {
          const s = spot.data
          setForm({
            name: s.name,
            description: s.description,
            categoryId: String(s.category.id),
            address: s.address,
            latitude: String(s.latitude),
            longitude: String(s.longitude),
            indoor: s.indoor,
          })
        }
      })
      .catch((err) => setError(errorMessage(err)))
      .finally(() => setLoading(false))
  }, [id, editing])

  const update = (field) => (e) => setForm({ ...form, [field]: e.target.type === 'checkbox' ? e.target.checked : e.target.value })

  function validate() {
    const found = {}
    if (!form.name.trim()) found.name = 'Name is required.'
    if (!form.description.trim()) found.description = 'Description is required.'
    if (!form.categoryId) found.categoryId = 'Choose a category.'
    if (!form.address.trim()) found.address = 'Address is required.'
    const lat = Number(form.latitude)
    const lon = Number(form.longitude)
    if (form.latitude === '' || Number.isNaN(lat)) found.latitude = 'Latitude is required.'
    else if (lat < LAT[0] || lat > LAT[1]) found.latitude = `Latitude must be between ${LAT[0]} and ${LAT[1]} (Cebu Province).`
    if (form.longitude === '' || Number.isNaN(lon)) found.longitude = 'Longitude is required.'
    else if (lon < LON[0] || lon > LON[1]) found.longitude = `Longitude must be between ${LON[0]} and ${LON[1]} (Cebu Province).`
    return found
  }

  async function handleSubmit(e) {
    e.preventDefault()
    setError('')
    const found = validate()
    setErrors(found)
    if (Object.keys(found).length) return
    setBusy(true)
    const payload = {
      name: form.name,
      description: form.description,
      categoryId: Number(form.categoryId),
      address: form.address,
      latitude: Number(form.latitude),
      longitude: Number(form.longitude),
      indoor: form.indoor,
    }
    try {
      if (editing) await api.put(`/admin/spots/${id}`, payload)
      else await api.post('/admin/spots', payload)
      navigate('/admin/spots', { replace: true, state: { saved: true } })
    } catch (err) {
      setErrors(fieldErrors(err))
      setError(errorMessage(err))
    } finally {
      setBusy(false)
    }
  }

  if (loading) return <p className="center">Loading...</p>

  return (
    <>
      <h1>{editing ? 'Edit spot' : 'Add spot'}</h1>
      <p className="muted">
        <Link to="/admin/spots">Back to spots</Link>
      </p>
      <section className="card">
        {error && <div className="alert error">{error}</div>}
        <form onSubmit={handleSubmit} noValidate>
          <div className="form-grid">
            <div className="full">
              <label htmlFor="name">Name</label>
              <input id="name" value={form.name} onChange={update('name')} maxLength={150} />
              {errors.name && <span className="field-error">{errors.name}</span>}
            </div>
            <div className="full">
              <label htmlFor="description">Description</label>
              <textarea id="description" value={form.description} onChange={update('description')} maxLength={2000} />
              {errors.description && <span className="field-error">{errors.description}</span>}
            </div>
            <div>
              <label htmlFor="categoryId">Category</label>
              <select id="categoryId" value={form.categoryId} onChange={update('categoryId')}>
                <option value="">Choose a category</option>
                {categories.map((c) => (
                  <option key={c.id} value={c.id}>
                    {c.name}
                  </option>
                ))}
              </select>
              {errors.categoryId && <span className="field-error">{errors.categoryId}</span>}
            </div>
            <div>
              <label htmlFor="address">Address</label>
              <input id="address" value={form.address} onChange={update('address')} maxLength={255} />
              {errors.address && <span className="field-error">{errors.address}</span>}
            </div>
            <div>
              <label htmlFor="latitude">Latitude</label>
              <input id="latitude" inputMode="decimal" placeholder="e.g. 10.2935" value={form.latitude} onChange={update('latitude')} />
              {errors.latitude && <span className="field-error">{errors.latitude}</span>}
            </div>
            <div>
              <label htmlFor="longitude">Longitude</label>
              <input id="longitude" inputMode="decimal" placeholder="e.g. 123.9021" value={form.longitude} onChange={update('longitude')} />
              {errors.longitude && <span className="field-error">{errors.longitude}</span>}
            </div>
            <div className="full">
              <label className="checkbox" htmlFor="indoor">
                <input id="indoor" type="checkbox" checked={form.indoor} onChange={update('indoor')} />
                This is an indoor spot (suggested on rainy days)
              </label>
            </div>
          </div>
          <button type="submit" disabled={busy}>
            {busy ? 'Saving...' : editing ? 'Save changes' : 'Add spot'}
          </button>
        </form>
      </section>
    </>
  )
}
