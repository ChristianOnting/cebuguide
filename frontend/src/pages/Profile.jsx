import { useAuth } from '../auth/AuthContext.jsx'
import { roleLabel } from '../auth/paths.js'

export default function Profile() {
  const { user } = useAuth()
  return (
    <>
      <h1>My profile</h1>
      <section className="card">
        <dl>
          <dt>Name</dt>
          <dd>{user.name}</dd>
          <dt>Email</dt>
          <dd>{user.email}</dd>
          <dt>Role</dt>
          <dd>{roleLabel(user.role)}</dd>
          <dt>Member since</dt>
          <dd>{new Date(user.createdAt).toLocaleDateString()}</dd>
        </dl>
      </section>
    </>
  )
}
