import { memo } from 'react'
import { useNavigate } from 'react-router-dom'
import StockBadge from './StockBadge'

const ArticleCard = memo(function ArticleCard({ article, isFav, onToggleFav, onAddToQuote }) {
  const navigate = useNavigate()
  const { nom, ref, physicalStock, availableStock } = article

  return (
    <div className="mx-4 mb-2 rounded-2xl border border-card-stroke bg-card-dark overflow-hidden" onClick={() => navigate(`/stock/${article.id}/${encodeURIComponent(nom || '')}`)}>
      <div className="p-4">
        {/* Top row */}
        <div className="flex items-start gap-2">
          <StockBadge physicalStock={physicalStock} />
          <span className="flex-1 text-[15px] font-medium text-white leading-tight line-clamp-2">{nom}</span>
        </div>

        {/* Stock row */}
        <div className="flex items-center justify-between mt-2">
          <span className="text-xs text-on-surface-variant font-medium">Physical: {physicalStock || 0}</span>
          <span className="text-xs text-on-surface-variant font-medium">Reserved: {availableStock || 0}</span>
        </div>

        {/* Separator */}
        <div className="h-px bg-card-stroke my-3" />

        {/* Actions */}
        <div className="flex items-center gap-2" onClick={e => e.stopPropagation()}>
          <button onClick={() => onToggleFav(ref)}
            className="w-10 h-10 flex items-center justify-center rounded-xl bg-surface-variant border border-card-stroke">
            <svg width="18" height="18" viewBox="0 0 24 24" fill={isFav ? 'white' : 'none'} stroke="white" strokeWidth="2">
              <path d="M12 17.27L18.18 21l-1.64-7.03L22 9.24l-7.19-.61L12 2 9.19 8.63 2 9.24l5.46 4.73L5.82 21z" />
            </svg>
          </button>
          <button onClick={() => onAddToQuote(article)}
            className="flex-1 h-10 rounded-xl bg-card-stroke text-white text-xs font-medium">
            Devis
          </button>
        </div>
      </div>
    </div>
  )
})

export default ArticleCard
