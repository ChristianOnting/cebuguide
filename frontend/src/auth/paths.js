export function dashboardPath(role) {
  return role === 'ADMIN' ? '/admin/dashboard' : '/tourist/dashboard'
}

export function roleLabel(role) {
  return role === 'ADMIN' ? 'Administrator' : 'Tourist'
}
