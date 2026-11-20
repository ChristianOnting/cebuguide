import { useEffect, useState } from 'react'
import api, { errorMessage } from '../api'

export default function AdminDashboard() {
  const [data, setData] = useState(null)
  const [error, setError] = useState('')

  useEffect(() => {
    api
      .get('/admin/dashboard')
      .then((res) => setData(res.data))
      .catch((err) => setError(errorMessage(err)))
  }, [])

  if (error) return <div className="alert error">{error}</div>
  if (!data) return <p className="center">Loading...</p>

  return (
    <>
      <h1>Administrator dashboard</h1>
      <p className="muted">Overview of registered accounts</p>
      <div className="grid">
        <section className="card stat">
          <span className="stat-number">{data.totalUsers}</span>
          <span>Total users</span>
        </section>
        <section className="card stat">
          <span className="stat-number">{data.tourists}</span>
          <span>Tourists</span>
        </section>
        <section className="card stat">
          <span className="stat-number">{data.administrators}</span>
          <span>Administrators</span>
        </section>
      </div>
      <section className="card">
        <h2>Latest registrations</h2>
        <table>
          <thead>
            <tr>
              <th>Name</th>
              <th>Email</th>
              <th>Role</th>
              <th>Registered</th>
            </tr>
          </thead>
          <tbody>
            {data.recentUsers.map((u) => (
              <tr key={u.id}>
                <td>{u.name}</td>
                <td>{u.email}</td>
                <td>{u.role}</td>
                <td>{new Date(u.createdAt).toLocaleString()}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </section>
    </>
  )
}
