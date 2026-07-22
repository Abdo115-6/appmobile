import { useState } from 'react'
import { useAuth } from '../AuthContext'

export default function Login() {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const { login } = useAuth()

  const handleSubmit = async (e) => {
    e.preventDefault()
    if (!email || !password) { setError('Please fill in all fields'); return }
    setLoading(true)
    setError('')
    try {
      await login(email, password)
    } catch (err) {
      setError(err.message || 'Login failed')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-dvh bg-surface flex flex-col items-center justify-center px-8">
      <div className="w-full max-w-sm">
        <div className="w-8 h-0.5 bg-white mb-6" />
        <h1 className="text-2xl font-bold text-white mb-8">Welcome</h1>
        <form onSubmit={handleSubmit} className="flex flex-col gap-4">
          <input
            type="text"
            value={email}
            onChange={e => setEmail(e.target.value)}
            placeholder="Login"
            className="w-full px-4 py-3.5 rounded-xl bg-surface-variant border border-card-stroke text-white text-sm outline-none focus:border-white/30 placeholder:text-on-surface-variant"
            autoComplete="username"
          />
          <input
            type="password"
            value={password}
            onChange={e => setPassword(e.target.value)}
            placeholder="Password"
            className="w-full px-4 py-3.5 rounded-xl bg-surface-variant border border-card-stroke text-white text-sm outline-none focus:border-white/30 placeholder:text-on-surface-variant"
            autoComplete="current-password"
          />
          {error && <p className="text-xs text-error">{error}</p>}
          <button type="submit" disabled={loading}
            className="w-full py-3.5 rounded-xl bg-white text-black text-sm font-medium disabled:opacity-50">
            {loading ? 'Logging in…' : 'Login'}
          </button>
        </form>
      </div>
    </div>
  )
}
