import { useCallback, useEffect, useState } from 'react'
import api, { errorMessage } from '../api'

function CategoryRow({ category, onChanged, onError }) {
  const [name, setName] = useState(category.name)
  const [color, setColor] = useState(category.markerColor)
  const changed = name.trim() !== category.name || color !== category.markerColor

  async function save() {
    try {
      await api.put(`/admin/categories/${category.id}`, { name, markerColor: color })
      onChanged(`Category "${name.trim()}" was updated.`)
    } catch (err) {
      onError(errorMessage(err))
    }
  }

  async function remove() {
    if (!window.confirm(`Remove the category "${category.name}"?`)) return
    try {
      await api.delete(`/admin/categories/${category.id}`)
      onChanged(`Category "${category.name}" was removed.`)
    } catch (err) {
      onError(errorMessage(err))
    }
  }

  return (
    <tr>
      <td>
        <input value={name} onChange={(e) => setName(e.target.value)} maxLength={50} aria-label="Category name" />
      </td>
      <td>
        <input type="color" value={color} onChange={(e) => setColor(e.target.value)} aria-label="Marker color" />
      </td>
      <td>{category.spotCount}</td>
      <td>
        <div className="actions">
          <button className="small" disabled={!changed} onClick={save}>
            Save
          </button>
          <button className="small danger" onClick={remove}>
            Remove
          </button>
        </div>
      </td>
    </tr>
  )
}

export default function AdminCategories() {
  const [categories, setCategories] = useState(null)
  const [name, setName] = useState('')
  const [color, setColor] = useState('#1f4e79')
  const [message, setMessage] = useState('')
  const [error, setError] = useState('')

  const load = useCallback(() => {
    api
      .get('/admin/categories')
      .then((res) => setCategories(res.data))
      .catch((err) => setError(errorMessage(err)))
  }, [])

  useEffect(() => {
    load()
  }, [load])

  function changed(text) {
    setMessage(text)
    setError('')
    load()
  }

  function failed(text) {
    setMessage('')
    setError(text)
  }

  async function add(e) {
    e.preventDefault()
    if (!name.trim()) {
      failed('Category name is required.')
      return
    }
    try {
      await api.post('/admin/categories', { name, markerColor: color })
      setName('')
      changed(`Category "${name.trim()}" was added.`)
    } catch (err) {
      failed(errorMessage(err))
    }
  }

  return (
    <>
      <h1>Categories</h1>
      <p className="muted">Add, rename, and remove spot categories. A category that still has spots cannot be removed.</p>
      {message && <div className="alert success">{message}</div>}
      {error && <div className="alert error">{error}</div>}
      <section className="card">
        <h2>Add a category</h2>
        <form className="toolbar" onSubmit={add} noValidate>
          <input placeholder="Category name" value={name} onChange={(e) => setName(e.target.value)} maxLength={50} aria-label="New category name" />
          <input type="color" value={color} onChange={(e) => setColor(e.target.value)} aria-label="New category color" />
          <button type="submit" className="small">
            Add category
          </button>
        </form>
      </section>
      {!categories ? (
        <p className="center">Loading...</p>
      ) : (
        <section className="card table-scroll">
          <table>
            <thead>
              <tr>
                <th>Name</th>
                <th>Marker color</th>
                <th>Spots</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {categories.map((c) => (
                <CategoryRow key={`${c.id}-${c.name}-${c.markerColor}`} category={c} onChanged={changed} onError={failed} />
              ))}
            </tbody>
          </table>
        </section>
      )}
    </>
  )
}
