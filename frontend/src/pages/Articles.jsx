import { useState, useRef, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { useQuery } from '@tanstack/react-query'
import { api } from '../api'
import { useFavorites } from '../hooks/useFavorites'
import { useDebounce } from '../hooks/useDebounce'
import { useSort } from '../hooks/useSort'
import ArticleCard from '../components/ArticleCard'
import SearchBar from '../components/SearchBar'
import SkeletonCard from '../components/SkeletonCard'
import jsQR from 'jsqr'

export default function Articles() {
  const [search, setSearch] = useState('')
  const [sortMode, setSortMode] = useState(0)
  const [showMenu, setShowMenu] = useState(false)
  const [showScanner, setShowScanner] = useState(false)
  const [toast, setToast] = useState('')
  const videoRef = useRef(null)
  const canvasRef = useRef(null)
  const scanIntervalRef = useRef(null)
  const fileInputRef = useRef(null)
  const navigate = useNavigate()
  const { favorites, toggle: toggleFav } = useFavorites()
  const debouncedSearch = useDebounce(search)

  const handleToggleFav = (ref) => {
    const wasFav = favorites.has(ref)
    toggleFav(ref)
    setToast(wasFav ? 'Removed from favorites' : 'Added to favorites')
    setTimeout(() => setToast(''), 1500)
  }

  const { data: articles, isLoading, error } = useQuery({
    queryKey: ['articles'],
    queryFn: api.getArticles,
  })

  const { data: searchResults, isLoading: searchLoading } = useQuery({
    queryKey: ['articles-search', debouncedSearch],
    queryFn: () => api.searchArticles(debouncedSearch),
    enabled: debouncedSearch.length > 0,
  })

  const displayArticles = debouncedSearch ? searchResults : articles
  const sorted = useSort(displayArticles, sortMode, favorites)
  const loading = isLoading || searchLoading

  // Cleanup scanner on unmount
  useEffect(() => {
    return () => {
      if (scanIntervalRef.current) cancelAnimationFrame(scanIntervalRef.current)
      if (videoRef.current?.srcObject) {
        videoRef.current.srcObject.getTracks().forEach(t => t.stop())
      }
    }
  }, [])

  // Decode QR from image data
  const decodeQR = (imageData, width, height) => {
    return jsQR(imageData, width, height)
  }

  const startScan = async () => {
    setShowScanner(true)
    try {
      const stream = await navigator.mediaDevices.getUserMedia({ video: { facingMode: 'environment', width: 640, height: 480 } })
      if (videoRef.current) {
        videoRef.current.srcObject = stream
        await videoRef.current.play()
        videoRef.current.onloadeddata = () => scanQR()
        setTimeout(() => {
          if (!scanIntervalRef.current) scanQR()
        }, 1000)
      }
    } catch (err) {
      setShowScanner(false)
      if (fileInputRef.current) {
        fileInputRef.current.value = ''
        fileInputRef.current.click()
      } else {
        const msg = err.name === 'NotAllowedError'
          ? 'Camera permission denied. Allow camera access in Safari settings.'
          : err.name === 'NotFoundError'
          ? 'No camera found on this device.'
          : 'Camera unavailable. Enable HTTPS or use QR scan from scanner.'
        setToast(msg)
        setTimeout(() => setToast(''), 4000)
      }
    }
  }

  // Handle file-based QR scan (fallback for HTTP)
  const handleFileScan = (e) => {
    const file = e.target.files?.[0]
    if (!file) return
    const img = new Image()
    const reader = new FileReader()
    reader.onload = () => {
      img.onload = () => {
        const canvas = document.createElement('canvas')
        canvas.width = img.naturalWidth
        canvas.height = img.naturalHeight
        const ctx = canvas.getContext('2d')
        ctx.drawImage(img, 0, 0)
        const imageData = ctx.getImageData(0, 0, canvas.width, canvas.height)
        const code = decodeQR(imageData.data, imageData.width, imageData.height)
        if (code) {
          const ref = code.data.replace(/\s.*/, '')
          api.getArticleByBarcode(ref).then(article => {
            navigate(`/stock/${article.id}/${encodeURIComponent(article.nom || '')}`)
          }).catch(() => {
            setToast('Article not found')
            setTimeout(() => setToast(''), 2000)
          })
        } else {
          setToast('No QR code found in image')
          setTimeout(() => setToast(''), 3000)
        }
      }
      img.src = reader.result
    }
    reader.readAsDataURL(file)
  }

  const scanQR = () => {
    const video = videoRef.current
    const canvas = canvasRef.current
    if (!video || !canvas) return
    const ctx = canvas.getContext('2d')
    let lastTick = 0
    const SCAN_INTERVAL = 300

    const tick = (now) => {
      if (now - lastTick < SCAN_INTERVAL) {
        scanIntervalRef.current = requestAnimationFrame(tick)
        return
      }
      lastTick = now
      if (!video.videoWidth || !video.videoHeight) {
        scanIntervalRef.current = requestAnimationFrame(tick)
        return
      }
      canvas.width = video.videoWidth
      canvas.height = video.videoHeight
      ctx.drawImage(video, 0, 0)
      const imageData = ctx.getImageData(0, 0, canvas.width, canvas.height)
      const code = jsQR(imageData.data, imageData.width, imageData.height)
      if (code) {
        cancelAnimationFrame(scanIntervalRef.current)
        scanIntervalRef.current = null
        const stream = video.srcObject
        if (stream) stream.getTracks().forEach(t => t.stop())
        setShowScanner(false)
        const ref = code.data.replace(/\s.*/, '')
        api.getArticleByBarcode(ref).then(article => {
          navigate(`/stock/${article.id}/${encodeURIComponent(article.nom || '')}`)
        }).catch(() => {
          setToast('Article not found')
          setTimeout(() => setToast(''), 2000)
        })
        return
      }
      scanIntervalRef.current = requestAnimationFrame(tick)
    }
    scanIntervalRef.current = requestAnimationFrame(tick)
  }

  const stopScan = () => {
    if (scanIntervalRef.current) {
      cancelAnimationFrame(scanIntervalRef.current)
      scanIntervalRef.current = null
    }
    if (videoRef.current?.srcObject) {
      videoRef.current.srcObject.getTracks().forEach(t => t.stop())
    }
    setShowScanner(false)
  }

  return (
    <div>
      {/* Sort indicator */}
      <div className="mx-4 mb-2 flex items-center justify-between">
        <span className="text-[11px] text-on-surface-variant font-medium uppercase tracking-wider">All Articles</span>
        <div className="relative">
          <button onClick={() => setShowMenu(!showMenu)} className="text-xs text-on-surface-variant flex items-center gap-1">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="#888"><path d="M3 18h6v-2H3v2zM3 6v2h18V6H3zm0 7h12v-2H3v2z"/></svg>
            Sort
          </button>
          {showMenu && (
            <>
              <div className="fixed inset-0 z-10" onClick={() => setShowMenu(false)} />
              <div className="absolute right-0 top-6 z-20 w-48 rounded-xl bg-surface-variant border border-card-stroke py-1 shadow-xl">
                {[
                  { label: 'Favorites first', mode: 0 },
                  { label: 'In stock first', mode: 1 },
                  { label: 'Alphabetical', mode: 2 },
                ].map(opt => (
                  <button key={opt.mode} onClick={() => { setSortMode(opt.mode); setShowMenu(false) }}
                    className={`w-full text-left px-4 py-2.5 text-sm ${sortMode === opt.mode ? 'text-white bg-card-stroke' : 'text-on-surface-variant'}`}>
                    {opt.label}
                  </button>
                ))}
              </div>
            </>
          )}
        </div>
      </div>

      <SearchBar value={search} onChange={setSearch} onScan={startScan} />

      {loading && !displayArticles && (
        <div>{[1,2,3,4].map(i => <SkeletonCard key={i} />)}</div>
      )}

      {error && (
        <p className="mx-4 text-sm text-on-surface-variant text-center mt-8">Connection error</p>
      )}

      {sorted?.length === 0 && !loading && (
        <p className="mx-4 text-sm text-on-surface-variant text-center mt-8">No articles found</p>
      )}

      <div className="pb-20">
        {sorted?.map(article => (
          <ArticleCard
            key={article.id || article.ref}
            article={article}
            isFav={favorites.has(article.ref)}
            onToggleFav={handleToggleFav}
            onAddToQuote={(a) => navigate(`/devis/${encodeURIComponent(a.ref)}`)}
          />
        ))}
      </div>

      {/* Toast notification */}
      {toast && (
        <div className="fixed bottom-8 left-1/2 -translate-x-1/2 z-50 px-4 py-2.5 rounded-xl bg-card-stroke text-white text-xs shadow-xl whitespace-nowrap">
          {toast}
        </div>
      )}

      {/* Hidden file input for camera fallback (works over HTTP) */}
      <input ref={fileInputRef} type="file" accept="image/*" capture="environment" className="hidden" onChange={handleFileScan} />

      {/* QR Scanner overlay */}
      {showScanner && (
        <div className="fixed inset-0 z-50 bg-black flex items-center justify-center">
          <video ref={videoRef} className="w-full max-w-md" playsInline autoPlay muted />
          <canvas ref={canvasRef} className="hidden" />
          <button onClick={stopScan} className="absolute top-16 right-4 text-sm text-white bg-card-stroke px-4 py-2 rounded-xl">
            Cancel
          </button>
        </div>
      )}
    </div>
  )
}
