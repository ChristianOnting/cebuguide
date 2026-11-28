import { Navigate, Route, Routes } from 'react-router-dom'
import { useAuth } from './auth/AuthContext.jsx'
import { dashboardPath } from './auth/paths.js'
import ProtectedRoute from './auth/ProtectedRoute.jsx'
import Layout from './components/Layout.jsx'
import Login from './pages/Login.jsx'
import Register from './pages/Register.jsx'
import ForgotPassword from './pages/ForgotPassword.jsx'
import ResetPassword from './pages/ResetPassword.jsx'
import TouristDashboard from './pages/TouristDashboard.jsx'
import AdminDashboard from './pages/AdminDashboard.jsx'
import AdminUsers from './pages/AdminUsers.jsx'
import AdminSpots from './pages/AdminSpots.jsx'
import SpotForm from './pages/SpotForm.jsx'
import AdminCategories from './pages/AdminCategories.jsx'
import Profile from './pages/Profile.jsx'
import ChangePassword from './pages/ChangePassword.jsx'
import Unauthorized from './pages/Unauthorized.jsx'
import NotFound from './pages/NotFound.jsx'

// Visitors who are already logged in are sent straight to their own dashboard.
function GuestOnly({ children }) {
  const { user, loading } = useAuth()
  if (loading) return <p className="center">Loading...</p>
  return user ? <Navigate to={dashboardPath(user.role)} replace /> : children
}

function Home() {
  const { user, loading } = useAuth()
  if (loading) return <p className="center">Loading...</p>
  return <Navigate to={user ? dashboardPath(user.role) : '/login'} replace />
}

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<Home />} />
      <Route path="/login" element={<GuestOnly><Login /></GuestOnly>} />
      <Route path="/register" element={<GuestOnly><Register /></GuestOnly>} />
      <Route path="/forgot-password" element={<GuestOnly><ForgotPassword /></GuestOnly>} />
      <Route path="/reset-password" element={<ResetPassword />} />

      <Route element={<ProtectedRoute />}>
        <Route element={<Layout />}>
          <Route path="/profile" element={<Profile />} />
          <Route path="/change-password" element={<ChangePassword />} />
          <Route path="/unauthorized" element={<Unauthorized />} />

          <Route element={<ProtectedRoute roles={['TOURIST']} />}>
            <Route path="/tourist/dashboard" element={<TouristDashboard />} />
          </Route>

          <Route element={<ProtectedRoute roles={['ADMIN']} />}>
            <Route path="/admin/dashboard" element={<AdminDashboard />} />
            <Route path="/admin/users" element={<AdminUsers />} />
            <Route path="/admin/spots" element={<AdminSpots />} />
            <Route path="/admin/spots/new" element={<SpotForm />} />
            <Route path="/admin/spots/:id/edit" element={<SpotForm />} />
            <Route path="/admin/categories" element={<AdminCategories />} />
          </Route>
        </Route>
      </Route>

      <Route path="*" element={<NotFound />} />
    </Routes>
  )
}
