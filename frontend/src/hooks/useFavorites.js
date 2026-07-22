import { useState, useCallback } from 'react'

export function useFavorites() {
  const [favorites, setFavorites] = useState(() => {
    try {
      const stored = localStorage.getItem('favorites')
      return stored ? new Set(JSON.parse(stored)) : new Set()
    } catch { return new Set() }
  })

  const toggle = useCallback((ref) => {
    setFavorites(prev => {
      const next = new Set(prev)
      if (next.has(ref)) next.delete(ref)
      else next.add(ref)
      localStorage.setItem('favorites', JSON.stringify([...next]))
      return next
    })
  }, [])

  return { favorites, toggle }
}
