import { useState, useEffect, useRef } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { useQuery, useMutation } from '@tanstack/react-query'
import { api } from '../api'
import { useAuth } from '../AuthContext'
import jsQR from 'jsqr'

export default function Devis() {
  const { ref: urlRef } = useParams()
  const navigate = useNavigate()
  const { email } = useAuth()

  const [site, setSite] = useState('')
  const [clientQuery, setClientQuery] = useState('')
  const [articleQuery, setArticleQuery] = useState(urlRef || '')
  const [qty, setQty] = useState('')
  const [price, setPrice] = useState('')
  const [selectedClient, setSelectedClient] = useState(null)
  const [selectedArticle, setSelectedArticle] = useState(null)
  const [entries, setEntries] = useState([])
  const [review, setReview] = useState(false)
  const [error, setError] = useState('')
  const [clientDebounce, setClientDebounce] = useState(null)
  const [articleDebounce, setArticleDebounce] = useState(null)
  const [showScanner, setShowScanner] = useState(false)
  const [exportingAll, setExportingAll] = useState(false)
  const [toast, setToast] = useState('')
  const videoRef = useRef(null)
  const canvasRef = useRef(null)
  const scanIntervalRef = useRef(null)
  const fileInputRef = useRef(null)

  const sites = ['FBC', 'FMD']

  // Fetch article from URL param
  useEffect(() => {
    if (urlRef) {
      const timer = setTimeout(() => setArticleDebounce(urlRef), 100)
      return () => clearTimeout(timer)
    }
  }, [urlRef])

  const { data: clients = [] } = useQuery({
    queryKey: ['clients', clientDebounce],
    queryFn: () => api.searchClients(clientDebounce),
    enabled: (clientDebounce?.length || 0) >= 2,
  })

  const { data: articles = [] } = useQuery({
    queryKey: ['articles-search', articleDebounce],
    queryFn: () => api.searchArticles(articleDebounce),
    enabled: (articleDebounce?.length || 0) >= 2,
  })

  const { data: barcodeArticle } = useQuery({
    queryKey: ['barcode', urlRef],
    queryFn: () => api.getArticleByBarcode(urlRef),
    enabled: !!urlRef,
  })

  useEffect(() => {
    if (barcodeArticle) {
      setSelectedArticle(barcodeArticle)
      setArticleQuery(barcodeArticle.nom + (barcodeArticle.sau ? ' (' + barcodeArticle.sau + ')' : ''))
    }
  }, [barcodeArticle])

  const confirmMutation = useMutation({
    mutationFn: (body) => api.confirmDevis(body),
    onSuccess: () => {
      const msg = 'Devi confirmed'
      setToast(msg)
      setTimeout(() => setToast(''), 2000)
      setReview(false)
      setEntries([])
      clearForm()
    },
    onError: (err) => {
      const msg = 'Error: ' + err.message
      setError(msg)
      setToast(msg)
      setTimeout(() => setToast(''), 3000)
    },
  })

  const exportMutation = useMutation({
    mutationFn: (body) => api.confirmAndExport(body),
    onSuccess: () => {
      const msg = 'CSV saved on server'
      setToast(msg)
      setTimeout(() => setToast(''), 2000)
      setReview(false)
      setEntries([])
      clearForm()
    },
    onError: (err) => {
      const msg = 'Error: ' + err.message
      setError(msg)
      setToast(msg)
      setTimeout(() => setToast(''), 3000)
    },
  })

  const showToast = (msg, isError) => {
    setToast(msg)
    setTimeout(() => setToast(''), 2000)
  }

  const onClientChange = (val) => {
    setClientQuery(val)
    clearTimeout(window._clientTimer)
    window._clientTimer = setTimeout(() => setClientDebounce(val), 400)
  }

  const onArticleChange = (val) => {
    setArticleQuery(val)
    clearTimeout(window._articleTimer)
    window._articleTimer = setTimeout(() => setArticleDebounce(val), 400)
  }

  const addArticle = () => {
    if (!selectedArticle || !qty) {
      const msg = 'Select an article and enter quantity'
      setError(msg)
      setToast(msg)
      setTimeout(() => setToast(''), 2000)
      return
    }
    const coeff = selectedArticle.coefficient || 1
    const parsedQty = parseFloat(qty)
    if (isNaN(parsedQty)) { setError('Invalid quantity'); return }
    const cartons = Math.ceil(parsedQty / parseFloat(coeff))
    const adjustedQty = parseFloat(coeff) * cartons
    const parsedPrice = parseFloat(price) || 0

    setEntries(prev => [...prev, {
      article: selectedArticle,
      quantity: adjustedQty,
      price: parsedPrice,
      cartons,
      coefficient: coeff,
    }])
    setSelectedArticle(null)
    setArticleQuery('')
    setQty('')
    setPrice('')
    setError('')
  }

  const removeArticle = (idx) => {
    setEntries(prev => prev.filter((_, i) => i !== idx))
  }

  const generateDevis = () => {
    if (!site || !selectedClient || entries.length === 0) {
      setError('Select site, client, and add at least one article')
      return
    }
    setReview(true)
    setError('')
  }

  const buildBody = () => ({
    site,
    clientCode: selectedClient.code,
    clientName: selectedClient.name,
    creusr0: email,
    articles: entries.map(e => ({
      articleRef: e.article.ref,
      articleName: e.article.nom,
      quantity: e.quantity,
      price: e.price,
      coefficient: e.coefficient,
      cartons: e.cartons,
      unit: e.article.sau || 'UN',
    })),
  })

  const handleConfirm = () => {
    if (entries.length === 0) return
    confirmMutation.mutate(buildBody())
  }

  const handleExport = () => {
    if (entries.length === 0) return
    exportMutation.mutate(buildBody())
  }

  const handleExportAll = async () => {
    setExportingAll(true)
    try {
      const msg = await api.exportAllCsvServer()
      setToast(msg)
      setTimeout(() => setToast(''), 3000)
    } catch (err) {
      const msg = 'Export error: ' + err.message
      setError(msg)
      setToast(msg)
      setTimeout(() => setToast(''), 3000)
    } finally {
      setExportingAll(false)
    }
  }

  const clearForm = () => {
    setSite('')
    setClientQuery('')
    setArticleQuery('')
    setQty('')
    setPrice('')
    setSelectedClient(null)
    setSelectedArticle(null)
  }

  // Cleanup scanner on unmount
  useEffect(() => {
    return () => {
      if (scanIntervalRef.current) cancelAnimationFrame(scanIntervalRef.current)
      if (videoRef.current?.srcObject) {
        videoRef.current.srcObject.getTracks().forEach(t => t.stop())
      }
    }
  }, [])

  // Decode QR from image data (used by both camera and file fallback)
  const decodeQR = (imageData, width, height) => {
    return jsQR(imageData, width, height)
  }

  // QR Scanner
  const startScan = async () => {
    setShowScanner(true)
    try {
      const stream = await navigator.mediaDevices.getUserMedia({ video: { facingMode: 'environment', width: 640, height: 480 } })
      if (videoRef.current) {
        videoRef.current.srcObject = stream
        await videoRef.current.play()
        // Wait for video to have actual frames
        videoRef.current.onloadeddata = () => scanQR()
        // Fallback: start scanning after 1s even if onloadeddata doesn't fire
        setTimeout(() => {
          if (!scanIntervalRef.current) scanQR()
        }, 1000)
      }
    } catch (err) {
      setShowScanner(false)
      // Fallback: use file input camera (works on HTTP)
      if (fileInputRef.current) {
        fileInputRef.current.value = ''
        fileInputRef.current.click()
      } else {
        const msg = err.name === 'NotAllowedError'
          ? 'Camera permission denied. Allow camera access in Safari settings.'
          : err.name === 'NotFoundError'
          ? 'No camera found on this device.'
          : 'Camera unavailable. Enable HTTPS or use QR scan from Articles page.'
        setError(msg)
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
          setArticleQuery(ref)
          api.getArticleByBarcode(ref).then(article => {
            setSelectedArticle(article)
            setArticleQuery(article.nom + (article.sau ? ' (' + article.sau + ')' : ''))
          }).catch(() => {
            const msg = 'Article not found'
            setError(msg)
            setToast(msg)
            setTimeout(() => setToast(''), 2000)
          })
        } else {
          const msg = 'No QR code found in image'
          setError(msg)
          setToast(msg)
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
        setArticleQuery(ref)
        api.getArticleByBarcode(ref).then(article => {
          setSelectedArticle(article)
          setArticleQuery(article.nom + (article.sau ? ' (' + article.sau + ')' : ''))
        }).catch(() => {
          const msg = 'Article not found'
          setError(msg)
          setToast(msg)
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

  if (review) {
    return (
      <div className="px-4">
        <h2 className="text-lg font-medium text-white mb-2">Review Devi</h2>

        {/* Info */}
        <div className="mb-4 space-y-0.5">
          <div className="flex text-sm"><span className="w-20 text-on-surface-variant">Site</span><span className="text-white font-medium">{site}</span></div>
          <div className="flex text-sm"><span className="w-20 text-on-surface-variant">Client</span><span className="text-white font-medium">{selectedClient?.code} - {selectedClient?.name}</span></div>
        </div>

        <p className="text-[11px] text-on-surface-variant font-medium tracking-wider uppercase mb-2">Articles</p>

        <div className="flex text-[11px] text-on-surface-variant font-medium uppercase tracking-wider px-2 pb-2 border-b border-card-stroke">
          <span className="w-6">#</span>
          <span className="flex-1">Article</span>
          <span className="w-14 text-right">Qty</span>
          <span className="w-16 text-right">Price</span>
          <span className="w-14 text-right">Coeff</span>
          <span className="w-12 text-right">Cart</span>
        </div>

        {entries.map((e, i) => (
          <div key={i} className="flex items-center text-xs text-white py-1.5 px-2 border-b border-card-stroke/50">
            <span className="w-6">{i + 1}</span>
            <span className="flex-1 truncate">{e.article.nom}</span>
            <span className="w-14 text-right">{e.quantity}</span>
            <span className="w-16 text-right">{e.price}</span>
            <span className="w-14 text-right">{e.coefficient}</span>
            <span className="w-12 text-right">{e.cartons}</span>
          </div>
        ))}

        {error && <p className={`text-xs mt-4 ${error.includes('Error') ? 'text-error' : 'text-stock-green'}`}>{error}</p>}

        <div className="flex gap-2 mt-6">
          <button onClick={() => setReview(false)} className="flex-1 py-3 rounded-xl bg-card-stroke text-white text-xs font-medium">Edit</button>
          <button onClick={handleConfirm} disabled={confirmMutation.isPending}
            className="flex-1 py-3 rounded-xl bg-white text-black text-xs font-medium disabled:opacity-50">
            {confirmMutation.isPending ? 'Saving...' : 'Confirm'}
          </button>
          <button onClick={handleExport} disabled={exportMutation.isPending}
            className="flex-1 py-3 rounded-xl bg-white text-black text-xs font-medium disabled:opacity-50">
            {exportMutation.isPending ? 'Saving...' : 'Download CSV'}
          </button>
        </div>
      </div>
    )
  }

  return (
    <div className="px-4 space-y-4">
      {/* Export All button */}
      <button onClick={handleExportAll} disabled={exportingAll}
        className="w-full py-2.5 rounded-xl bg-card-stroke text-white text-xs font-medium disabled:opacity-50">
        {exportingAll ? 'Exporting...' : 'Export All CSVs'}
      </button>

      {/* Site */}
      <div>
        <label className="text-xs text-on-surface-variant font-medium mb-1 block">Site</label>
        <select value={site} onChange={e => setSite(e.target.value)}
          className="w-full px-4 py-3 rounded-xl bg-surface-variant border border-card-stroke text-white text-sm outline-none">
          <option value="">Select site</option>
          {sites.map(s => <option key={s} value={s}>{s}</option>)}
        </select>
      </div>

      {/* Client */}
      <div>
        <label className="text-xs text-on-surface-variant font-medium mb-1 block">Client</label>
        <input type="text" value={clientQuery} onChange={e => onClientChange(e.target.value)}
          placeholder="Search client..."
          className="w-full px-4 py-3 rounded-xl bg-surface-variant border border-card-stroke text-white text-sm outline-none placeholder:text-on-surface-variant" />
        {clientQuery.length >= 2 && clients.length > 0 && (
          <div className="mt-1 rounded-xl bg-surface-variant border border-card-stroke overflow-hidden">
            {clients.map((c, i) => (
              <button key={i} onClick={() => { setSelectedClient(c); setClientQuery(c.code + ' - ' + c.name); setClientDebounce('') }}
                className="w-full text-left px-4 py-2.5 text-sm text-white hover:bg-card-stroke">
                {c.code} - {c.name}
              </button>
            ))}
          </div>
        )}
      </div>

      {/* Article with scan button */}
      <div>
        <label className="text-xs text-on-surface-variant font-medium mb-1 block">Article</label>
        <div className="flex gap-2">
          <input type="text" value={articleQuery} onChange={e => onArticleChange(e.target.value)}
            placeholder="Search article..."
            className="flex-1 px-4 py-3 rounded-xl bg-surface-variant border border-card-stroke text-white text-sm outline-none placeholder:text-on-surface-variant" />
          <button onClick={startScan} className="w-12 h-12 shrink-0 flex items-center justify-center rounded-xl bg-surface-variant border border-card-stroke">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="white"><path d="M9.5 6.5v3h-3v-3h3M11 5H5v6h6V5zm-1.5 9.5v3h-3v-3h3M11 13H5v6h6v-6zm6.5-6.5v3h-3v-3h3M19 5h-6v6h6V5zm-6 8h1.5v1.5H13V13zm1.5 1.5H16V16h-1.5v-1.5zM16 13h1.5v1.5H16V13zm-3 3h1.5v1.5H13V16zm1.5 1.5H16V19h-1.5v-1.5zM16 16h1.5v1.5H16V16zm1.5-1.5H19V16h-1.5v-1.5zm0 3H19V19h-1.5v-1.5z"/></svg>
          </button>
        </div>
        {articleQuery.length >= 2 && articles.length > 0 && (
          <div className="mt-1 rounded-xl bg-surface-variant border border-card-stroke overflow-hidden max-h-48 overflow-y-auto">
            {articles.map((a, i) => (
              <button key={i} onClick={() => { setSelectedArticle(a); setArticleQuery(a.nom + (a.sau ? ' (' + a.sau + ')' : '')) }}
                className="w-full text-left px-4 py-2.5 text-sm text-white hover:bg-card-stroke">
                {a.nom}{a.sau ? ' (' + a.sau + ')' : ''}
              </button>
            ))}
          </div>
        )}
      </div>

      {/* Quantity + Price */}
      <div className="flex gap-3">
        <div className="flex-1">
          <label className="text-xs text-on-surface-variant font-medium mb-1 block">Quantity</label>
          <input type="number" value={qty} onChange={e => setQty(e.target.value)}
            className="w-full px-4 py-3 rounded-xl bg-surface-variant border border-card-stroke text-white text-sm outline-none"
            placeholder="0" step="any" />
        </div>
        <div className="flex-1">
          <label className="text-xs text-on-surface-variant font-medium mb-1 block">Price</label>
          <input type="number" value={price} onChange={e => setPrice(e.target.value)}
            className="w-full px-4 py-3 rounded-xl bg-surface-variant border border-card-stroke text-white text-sm outline-none"
            placeholder="0.00" step="any" />
        </div>
      </div>

      <button onClick={addArticle}
        className="w-full py-3 rounded-xl bg-card-stroke text-white text-xs font-medium">
        Add Article
      </button>

      {/* Article list */}
      <div className="space-y-1">
        {entries.length === 0 && <p className="text-xs text-on-surface-variant text-center py-4">No articles added yet</p>}
        {entries.map((e, i) => (
          <div key={i} className="flex items-center justify-between px-4 py-2.5 rounded-xl bg-card-dark border border-card-stroke">
            <span className="text-xs text-white flex-1 truncate">{i + 1}. {e.article.nom}  |  Qty: {e.quantity}  |  Price: {e.price}</span>
            <button onClick={() => removeArticle(i)} className="text-xs text-error shrink-0 ml-2">Remove</button>
          </div>
        ))}
      </div>

      {error && <p className="text-xs text-error">{error}</p>}

      {entries.length > 0 && (
        <button onClick={generateDevis}
          className="w-full py-3 rounded-xl bg-white text-black text-sm font-medium">
          Generate
        </button>
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

      {/* Toast notification */}
      {toast && (
        <div className="fixed bottom-8 left-1/2 -translate-x-1/2 z-50 px-4 py-2.5 rounded-xl bg-card-stroke text-white text-xs shadow-xl whitespace-nowrap">
          {toast}
        </div>
      )}
    </div>
  )
}
