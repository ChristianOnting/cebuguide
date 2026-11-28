import { NavLink, Outlet, useNavigate } from 'react-router-dom'
import { useAuth } from '../auth/AuthContext.jsx'
import { roleLabel } from '../auth/paths.js'

// The menu depends on the role of the logged-in user.
const MENUS = {
  TOURIST: [
    { to: '/tourist/dashboard', label: 'Dashboard' },
    { to: '/profile', label: 'My Profile' },
    { to: '/change-password', label: 'Change Password' },
  ],
  ADMIN: [
    { to: '/admin/dashboard', label: 'Dashboard' },
    { to: '/admin/spots', label: 'Spots' },
    { to: '/admin/categories', label: 'Categories' },
    { to: '/admin/users', label: 'Users' },
    { to: '/profile', label: 'My Profile' },
    { to: '/change-password', label: 'Change Password' },
  ],
}

export default function Layout() {
  const { user, logout } = useAuth()
  const navigate = useNavigate()

  async function handleLogout() {
    await logout()
    navigate('/login', { replace: true })
  }

  return (
    <>
      <header className="topbar">
        <span className="brand">CebuGuide</span>
        <nav>
          {MENUS[user.role].map((item) => (
            <NavLink key={item.to} to={item.to} className={({ isActive }) => (isActive ? 'active' : undefined)}>
              {item.label}
            </NavLink>
          ))}
        </nav>
        <div className="who">
          <span>{user.name}</span>
          <span className={`badge ${user.role.toLowerCase()}`}>{roleLabel(user.role)}</span>
          <button className="secondary" onClick={handleLogout}>
            Log out
          </button>
        </div>
      </header>
      <main className="page">
        <Outlet />
      </main>
    </>
  )
}
