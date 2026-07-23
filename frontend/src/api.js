const BASE = '/api'

async function request(url, options = {}) {
  const headers = { 'Content-Type': 'application/json', ...options.headers }
  const res = await fetch(BASE + url, { ...options, headers })
  const ct = res.headers.get('content-type') || ''
  if (ct.includes('application/json')) {
    const data = await res.json()
    if (!res.ok) throw new Error(data.message || `HTTP ${res.status}`)
    return data
  }
  if (!res.ok) {
    const text = await res.text().catch(() => '')
    throw new Error(text || `HTTP ${res.status}`)
  }
  return res
}

export const api = {
  login: (email, password) =>
    request('/auth/login', { method: 'POST', body: JSON.stringify({ email, password }) }),

  getArticles: () => request('/articles'),
  searchArticles: (q) => request('/articles/search?q=' + encodeURIComponent(q)),
  getArticleByBarcode: (ean) => request('/articles/barcode/' + encodeURIComponent(ean)),
  getArticleStocks: (id) => request('/articles/' + id + '/stocks'),

  searchClients: (q) => request('/devis/clients?q=' + encodeURIComponent(q)),
  confirmDevis: (body) =>
    request('/devis/confirm', { method: 'POST', body: JSON.stringify(body) }),
  confirmAndExport: (body) =>
    request('/devis/confirm-and-export', { method: 'POST', body: JSON.stringify(body) }),
  exportAllCsv: () => fetch(BASE + '/devis/export').then(r => { if (!r.ok) throw new Error('Export failed'); return r }),
  exportAllCsvServer: async () => {
    const res = await fetch(BASE + '/devis/export-all', { method: 'POST' })
    if (!res.ok) throw new Error(await res.text().catch(() => 'HTTP ' + res.status))
    return await res.text()
  },

  submitInventory: (body) =>
    request('/inventory', { method: 'POST', body: JSON.stringify(body) }),
  downloadInventory: async (depot, equipe, zone, format) => {
    const params = new URLSearchParams()
    if (depot) params.set('depot', depot)
    if (equipe) params.set('equipe', equipe)
    if (zone) params.set('zone', zone)
    params.set('format', format || 'csv')
    const res = await fetch(BASE + '/inventory/export?' + params.toString())
    if (!res.ok) throw new Error(`HTTP ${res.status}`)
    const blob = await res.blob()
    const url = URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = 'inventory.' + (format === 'xlsx' ? 'xlsx' : 'csv')
    document.body.appendChild(a)
    a.click()
    document.body.removeChild(a)
    URL.revokeObjectURL(url)
  },
}
