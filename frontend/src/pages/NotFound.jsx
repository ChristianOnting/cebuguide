import { Link } from 'react-router-dom'

export default function NotFound() {
  return (
    <div className="auth-card">
      <h1>Page not found</h1>
      <Link to="/">Go to the home page</Link>
    </div>
  )
}
