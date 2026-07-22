import { useState } from 'react'
import { Outlet, useNavigate, useLocation } from 'react-router-dom'
import { useAuth } from '../AuthContext'
import { api } from '../api'

export default function Layout() {
  const [open, setOpen] = useState(false)
  const [showExport, setShowExport] = useState(false)
  const [exportMsg, setExportMsg] = useState('')
  const navigate = useNavigate()
  const location = useLocation()
  const { logout, user } = useAuth()
  const email = user?.email
  const role = (user?.role || '').toLowerCase()
  const canSeeInventory = role === 'admin' || role === 'superuser'
  const isDevisPage = location.pathname.startsWith('/devis')

  const nav = (path) => { navigate(path); setOpen(false) }

  const isActive = (path) => {
    if (path === '/') return location.pathname === '/'
    return location.pathname.startsWith(path)
  }

  const handleExportAll = async () => {
    setShowExport(false)
    setExportMsg('Exporting...')
    try {
      const res = await api.exportAllCsv()
      if (!res.ok) throw new Error('Export failed')
      const blob = await res.blob()
      const url = URL.createObjectURL(blob)
      const a = document.createElement('a')
      a.href = url
      a.download = 'devis_export.csv'
      document.body.appendChild(a)
      a.click()
      document.body.removeChild(a)
      URL.revokeObjectURL(url)
      setExportMsg('Exported successfully')
      setTimeout(() => setExportMsg(''), 2000)
    } catch {
      setExportMsg('Failed to export data')
      setTimeout(() => setExportMsg(''), 3000)
    }
  }

  return (
    <div className="min-h-dvh bg-surface">
      {/* Toolbar */}
      <header className="fixed top-0 left-0 right-0 z-50 flex items-center h-[72px] px-4 bg-surface" style={{ paddingTop: 'env(safe-area-inset-top)' }}>
        <button onClick={() => setOpen(!open)} className="p-2 mr-2">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="white"><path d="M3 18h18v-2H3v2zm0-5h18v-2H3v2zm0-7v2h18V6H3z"/></svg>
        </button>
        <span className="text-lg font-medium flex-1">MallZellij</span>
        {isDevisPage && (
          <div className="relative">
            <button onClick={() => setShowExport(!showExport)} className="p-2">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="#888"><path d="M12 8c1.1 0 2-.9 2-2s-.9-2-2-2-2 .9-2 2 .9 2 2 2zm0 2c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2zm0 6c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2z"/></svg>
            </button>
            {showExport && (
              <>
                <div className="fixed inset-0 z-10" onClick={() => setShowExport(false)} />
                <div className="absolute right-0 top-10 z-20 w-52 rounded-xl bg-surface-variant border border-card-stroke py-1 shadow-xl">
                  <button onClick={handleExportAll} className="w-full text-left px-4 py-2.5 text-sm text-on-surface-variant hover:text-white">Export All CSV</button>
                </div>
              </>
            )}
          </div>
        )}
        {location.pathname !== '/' && !isDevisPage && (
          <button onClick={() => navigate(-1)} className="text-sm text-on-surface-variant">Back</button>
        )}
      </header>

      {/* Drawer overlay */}
      {open && <div className="fixed inset-0 bg-black/50 z-40" onClick={() => setOpen(false)} />}

      {/* Drawer */}
      <aside className={`fixed top-0 left-0 bottom-0 w-72 z-50 bg-surface-variant transition-transform duration-200 ${open ? 'translate-x-0' : '-translate-x-full'}`}
        style={{ paddingTop: 'env(safe-area-inset-top)' }}>
        <div className="p-4 pt-12">
          <p className="text-sm text-on-surface-variant mb-4">{email}</p>
          <nav className="flex flex-col gap-1">
            <button onClick={() => nav('/')} className={`text-left px-4 py-3 rounded-lg text-sm ${isActive('/') && location.pathname === '/' ? 'bg-card-stroke text-white' : 'text-on-surface-variant'}`}>
              Articles
            </button>
            <button onClick={() => nav('/devis')} className={`text-left px-4 py-3 rounded-lg text-sm ${isActive('/devis') ? 'bg-card-stroke text-white' : 'text-on-surface-variant'}`}>
              Devi
            </button>
            {canSeeInventory && (
              <button onClick={() => nav('/inventory')} className={`text-left px-4 py-3 rounded-lg text-sm ${isActive('/inventory') ? 'bg-card-stroke text-white' : 'text-on-surface-variant'}`}>
                Inventory
              </button>
            )}
            <button onClick={logout} className="text-left px-4 py-3 rounded-lg text-sm text-on-surface-variant mt-4">
              Logout
            </button>
          </nav>
        </div>
      </aside>

      {/* Main content */}
      <main className="pt-[72px] pb-4" style={{ paddingTop: 'calc(72px + env(safe-area-inset-top))' }}>
        <Outlet />
      </main>

      {/* Export message toast */}
      {exportMsg && (
        <div className="fixed bottom-8 left-1/2 -translate-x-1/2 z-50 px-4 py-2.5 rounded-xl bg-card-stroke text-white text-xs shadow-xl">
          {exportMsg}
        </div>
      )}
    </div>
  )
}
