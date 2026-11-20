import { useEffect, useState } from 'react'
import api, { errorMessage } from '../api'

export default function AdminUsers() {
  const [users, setUsers] = useState(null)
  const [error, setError] = useState('')

  useEffect(() => {
    api
      .get('/admin/users')
      .then((res) => setUsers(res.data))
      .catch((err) => setError(errorMessage(err)))
  }, [])

  if (error) return <div className="alert error">{error}</div>
  if (!users) return <p className="center">Loading...</p>

  return (
    <>
      <h1>Users</h1>
      <p className="muted">{users.length} registered accounts</p>
      <section className="card">
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>Name</th>
              <th>Email</th>
              <th>Role</th>
              <th>Registered</th>
            </tr>
          </thead>
          <tbody>
            {users.map((u) => (
              <tr key={u.id}>
                <td>{u.id}</td>
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
