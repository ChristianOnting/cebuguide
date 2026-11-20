import { Link } from 'react-router-dom'
import { useAuth } from '../auth/AuthContext.jsx'
import { dashboardPath } from '../auth/paths.js'

export default function Unauthorized() {
  const { user } = useAuth()
  return (
    <div className="card center-card">
      <h1>Access denied</h1>
      <p>Your account does not have permission to open that page.</p>
      <Link to={dashboardPath(user.role)}>Back to my dashboard</Link>
    </div>
  )
}
