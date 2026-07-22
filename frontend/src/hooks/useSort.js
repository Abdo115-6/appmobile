import { useMemo } from 'react'

export function useSort(articles, mode, favorites) {
  return useMemo(() => {
    if (!articles) return []
    const sorted = [...articles]
    sorted.sort((a, b) => {
      if (mode === 0) {
        const aFav = favorites.has(a.ref)
        const bFav = favorites.has(b.ref)
        if (aFav !== bFav) return aFav ? -1 : 1
      }
      if (mode === 0 || mode === 1) {
        const aStock = (a.physicalStock || 0) > 0
        const bStock = (b.physicalStock || 0) > 0
        if (aStock !== bStock) return aStock ? -1 : 1
      }
      return (a.nom || '').localeCompare(b.nom || '')
    })
    return sorted
  }, [articles, mode, favorites])
}
