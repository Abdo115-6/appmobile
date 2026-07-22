import { createContext, useContext, useState, useCallback } from 'react'
import { useNavigate } from 'react-router-dom'
import { api } from './api'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    const email = localStorage.getItem('email')
    const role = localStorage.getItem('role')
    return email ? { email, role } : null
  })
  const navigate = useNavigate()

  const login = useCallback(async (emailVal, password) => {
    const res = await api.login(emailVal, password)
    if (!res.email) throw new Error(res.message || 'Login failed')
    localStorage.setItem('email', res.email)
    localStorage.setItem('role', res.role || '')
    setUser({ email: res.email, role: res.role || '' })
    navigate('/')
  }, [navigate])

  const logout = useCallback(() => {
    localStorage.removeItem('email')
    localStorage.removeItem('role')
    setUser(null)
    navigate('/login')
  }, [navigate])

  return (
    <AuthContext.Provider value={{ user, login, logout, isAuthenticated: !!user }}>
      {children}
    </AuthContext.Provider>
  )
}

export const useAuth = () => useContext(AuthContext)
