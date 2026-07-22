import { useParams } from 'react-router-dom'
import { useQuery } from '@tanstack/react-query'
import { api } from '../api'
import { useAuth } from '../AuthContext'

export default function ArticleStock() {
  const { id, name } = useParams()
  const { user } = useAuth()
  const articleName = name ? decodeURIComponent(name) : ''
  const role = (user?.role || '').toLowerCase()
  const canSeeAll = role === 'superuser' || role === 'admin'

  const { data: stocks, isLoading, error } = useQuery({
    queryKey: ['stocks', id],
    queryFn: () => api.getArticleStocks(id),
    enabled: !!id,
  })

  const sorted = stocks
    ? [...stocks].sort((a, b) => {
        const aZero = (a.quantity || 0) === 0
        const bZero = (b.quantity || 0) === 0
        if (aZero !== bZero) return aZero ? 1 : -1
        return (a.quantity || 0) - (b.quantity || 0)
      })
    : []

  const fmt = (n) => {
    if (n == null) return null
    return Number(n).toLocaleString('fr-FR', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
  }

  const fmtQty = (n) => {
    if (n == null) return null
    return Number(n).toLocaleString('fr-FR')
  }

  const hasPriceData = sorted.some(s => s.prix != null || s.prixPromo != null || s.prixPrevendor != null)

  return (
    <div className="px-4">
      {isLoading && (
        <div className="space-y-3 mt-8">
          {[1,2,3].map(i => (
            <div key={i} className="rounded-2xl border border-card-stroke bg-card-dark p-5 animate-pulse">
              <div className="h-5 w-16 bg-card-stroke rounded mb-4" />
              <div className="h-8 w-24 bg-card-stroke rounded mb-4" />
              <div className="h-px bg-card-stroke my-4" />
              <div className="h-5 w-20 bg-card-stroke rounded mb-4" />
              {hasPriceData && <div className="h-px bg-card-stroke my-4" />}
              {hasPriceData && <div className="h-6 w-28 bg-card-stroke rounded" />}
            </div>
          ))}
        </div>
      )}

      {error && <p className="text-sm text-on-surface-variant text-center mt-8">Failed to load stock details</p>}

      {sorted.length === 0 && !isLoading && !error && (
        <p className="text-sm text-on-surface-variant text-center mt-8">No stock information available</p>
      )}

      {sorted.length > 0 && (
        <>
          {articleName && (
            <div className="rounded-2xl border border-card-stroke bg-card-dark p-5 mb-3">
              <div className="w-8 h-0.5 bg-white mb-3" />
              <h2 className="text-lg font-medium text-white leading-tight">{articleName}</h2>
              <p className="text-[10px] text-on-surface-variant font-medium tracking-wider mt-2 uppercase">Stock Breakdown</p>
            </div>
          )}

          <div className="flex items-center justify-between px-5 py-3.5 mb-2 rounded-xl bg-card-dark border border-card-stroke">
            <span className="text-[11px] text-on-surface-variant font-medium uppercase tracking-wider">Site</span>
            <span className="text-[11px] text-on-surface-variant font-medium uppercase tracking-wider">Quantity</span>
          </div>

          {sorted.map((s, i) => (
            <div key={i} className="rounded-2xl border border-card-stroke bg-card-dark p-5 mb-2">
              {/* Site Header */}
              <div className="flex items-center gap-2.5">
                <div className="w-2 h-2 rounded-full bg-white shrink-0" />
                <span className="text-base font-medium text-white">{s.siteName}</span>
              </div>

              {/* On-Hand Stock */}
              <div className="flex items-center justify-between mt-4">
                <div>
                  <p className="text-[11px] text-on-surface-variant tracking-wide">Stock</p>
                  <p className="text-[22px] font-medium text-white mt-0.5">{fmtQty(s.quantity)}</p>
                </div>
                <div className="w-20 h-1 rounded-full bg-card-stroke overflow-hidden">
                  <div className="h-full bg-white rounded-full" style={{ width: Math.min(100, ((s.quantity || 0) / (Math.max(...sorted.map(x => x.quantity || 0)) || 1)) * 100) + '%' }} />
                </div>
              </div>

              <div className="h-px bg-card-stroke my-4" />

              {/* Reserve */}
              {s.quantiteALouer != null && (
                <>
                  <div className="flex items-center justify-between">
                    <div>
                      <p className="text-[11px] text-on-surface-variant tracking-wide">Reserve</p>
                      <p className="text-lg font-medium text-white mt-0.5">{fmtQty(s.quantiteALouer)}</p>
                    </div>
                    <span className="text-[10px] text-on-surface-variant font-medium tracking-wider uppercase">Reserve</span>
                  </div>
                  <div className="h-px bg-card-stroke my-4" />
                </>
              )}

              {/* Prix */}
              {s.prix != null && (
                <div className="flex items-center justify-between">
                  <div>
                    <p className="text-[11px] text-on-surface-variant tracking-wide">Prix</p>
                    <p className="text-xs text-on-surface-variant mt-0.5">Standard rate</p>
                  </div>
                  <div className="flex items-baseline gap-0.5">
                    <span className="text-xl font-medium text-white">{fmt(s.prix)}</span>
                    <span className="text-[13px] text-on-surface-variant font-medium">MAD</span>
                  </div>
                </div>
              )}

              {/* Prix Promo */}
              {canSeeAll && s.prixPromo != null && (
                <div className="flex items-center justify-between mt-3">
                  <p className="text-[10px] text-on-surface-variant font-medium tracking-wider uppercase">Prix Promo</p>
                  <div className="flex items-baseline gap-0.5">
                    <span className="text-xl font-medium text-white">{fmt(s.prixPromo)}</span>
                    <span className="text-[13px] text-on-surface-variant font-medium">MAD</span>
                  </div>
                </div>
              )}

              {/* Prix Prevendor */}
              {canSeeAll && s.prixPrevendor != null && (
                <div className="flex items-center justify-between mt-3">
                  <p className="text-[10px] text-on-surface-variant font-medium tracking-wider uppercase">Prix Prevendeur</p>
                  <div className="flex items-baseline gap-0.5">
                    <span className="text-xl font-medium text-white">{fmt(s.prixPrevendor)}</span>
                    <span className="text-[13px] text-on-surface-variant font-medium">MAD</span>
                  </div>
                </div>
              )}
            </div>
          ))}
        </>
      )}
    </div>
  )
}
