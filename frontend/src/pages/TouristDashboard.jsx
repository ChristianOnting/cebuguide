import { useEffect, useState } from 'react'
import api, { errorMessage } from '../api'

export default function TouristDashboard() {
  const [data, setData] = useState(null)
  const [error, setError] = useState('')

  useEffect(() => {
    api
      .get('/tourist/dashboard')
      .then((res) => setData(res.data))
      .catch((err) => setError(errorMessage(err)))
  }, [])

  if (error) return <div className="alert error">{error}</div>
  if (!data) return <p className="center">Loading...</p>

  return (
    <>
      <h1>{data.greeting}</h1>
      <p className="muted">Tourist dashboard</p>
      <div className="grid">
        <section className="card">
          <h2>Your account</h2>
          <dl>
            <dt>Name</dt>
            <dd>{data.user.name}</dd>
            <dt>Email</dt>
            <dd>{data.user.email}</dd>
            <dt>Role</dt>
            <dd>Tourist</dd>
            <dt>Member since</dt>
            <dd>{new Date(data.user.createdAt).toLocaleDateString()}</dd>
          </dl>
        </section>
        <section className="card">
          <h2>Available now</h2>
          <ul>
            {data.availableNow.map((item) => (
              <li key={item}>{item}</li>
            ))}
          </ul>
        </section>
        <section className="card">
          <h2>Coming in later increments</h2>
          <ul>
            {data.comingNext.map((item) => (
              <li key={item}>{item}</li>
            ))}
          </ul>
        </section>
      </div>
    </>
  )
}
