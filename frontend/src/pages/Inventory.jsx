import { useState } from 'react'
import { useMutation } from '@tanstack/react-query'
import { api } from '../api'
import { useAuth } from '../AuthContext'

const EQUIPE_OPTIONS = ['Équipe 1', 'Équipe 2', 'Équipe 3']
const DEPOT_OPTIONS = ['Dépôt 1', 'Dépôt 2', 'Dépôt 3']
const ZONE_OPTIONS = ['Zone A', 'Zone B', 'Zone C']
const FORMATS = ['csv', 'xlsx']

export default function Inventory() {
  const { email } = useAuth()
  const [equipe, setEquipe] = useState('')
  const [depot, setDepot] = useState('')
  const [zone, setZone] = useState('')
  const [article, setArticle] = useState('')
  const [pallet, setPallet] = useState('')
  const [carton, setCarton] = useState('')
  const [metreCarre, setMetreCarre] = useState('')
  const [error, setError] = useState('')
  const [showConfirm, setShowConfirm] = useState(false)
  const [showDownload, setShowDownload] = useState(false)
  const [dlDepot, setDlDepot] = useState('')
  const [dlEquipe, setDlEquipe] = useState('')
  const [dlZone, setDlZone] = useState('')
  const [dlFormat, setDlFormat] = useState('csv')
  const [dlLoading, setDlLoading] = useState(false)

  const submitMutation = useMutation({
    mutationFn: (body) => api.submitInventory(body),
    onSuccess: () => {
      setError('Inventory record saved successfully')
      setArticle(''); setPallet(''); setCarton(''); setMetreCarre('')
      setShowConfirm(false)
    },
    onError: (err) => {
      setError('Error: ' + err.message)
      setShowConfirm(false)
    },
  })

  const handleSubmit = () => {
    if (!article || !pallet || !carton || !metreCarre) {
      setError('All fields are required')
      return
    }
    setShowConfirm(true)
    setError('')
  }

  const confirmSubmit = () => {
    submitMutation.mutate({
      ydepot0: depot,
      yequipe0: equipe,
      yzone0: zone,
      yitmref0: article,
      yqtyplt0: parseFloat(pallet) || 0,
      yqtycrt0: parseFloat(carton) || 0,
      yqtymtr0: parseFloat(metreCarre) || 0,
      creusr0: email,
    })
  }

  const handleDownload = async () => {
    setDlLoading(true)
    setError('')
    try {
      await api.downloadInventory(
        dlDepot === 'All' ? '' : dlDepot,
        dlEquipe === 'All' ? '' : dlEquipe,
        dlZone === 'All' ? '' : dlZone,
        dlFormat
      )
      setShowDownload(false)
    } catch (err) {
      setError('Download error: ' + err.message)
    } finally {
      setDlLoading(false)
    }
  }

  return (
    <div className="px-4 space-y-4">
      {/* Confirmation Dialog */}
      {showConfirm && (
        <div className="fixed inset-0 bg-black/50 z-50 flex items-center justify-center p-4">
          <div className="bg-surface-variant rounded-2xl p-6 w-full max-w-sm border border-card-stroke">
            <h3 className="text-sm font-medium text-white mb-4">Confirm Inventory</h3>
            <div className="space-y-2 text-xs text-white">
              {[
                ['Équipe', equipe],
                ['Dépôt', depot],
                ['Zone', zone],
                ['Article', article],
                ['Pallet', pallet],
                ['Carton', carton],
                ['Mètre Carré', metreCarre + ' m²'],
                ['Opérateur', email],
              ].map(([label, val]) => (
                <div key={label} className="flex justify-between py-1 border-b border-card-stroke/50">
                  <span className="text-on-surface-variant">{label}</span>
                  <span>{val}</span>
                </div>
              ))}
            </div>
            <div className="flex gap-2 mt-6">
              <button onClick={() => setShowConfirm(false)} className="flex-1 py-3 rounded-xl bg-card-stroke text-white text-xs font-medium">Edit</button>
              <button onClick={confirmSubmit} disabled={submitMutation.isPending}
                className="flex-1 py-3 rounded-xl bg-white text-black text-xs font-medium disabled:opacity-50">
                {submitMutation.isPending ? 'Saving...' : 'Confirm'}
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Download Dialog */}
      {showDownload && (
        <div className="fixed inset-0 bg-black/50 z-50 flex items-center justify-center p-4">
          <div className="bg-surface-variant rounded-2xl p-6 w-full max-w-sm border border-card-stroke">
            <h3 className="text-sm font-medium text-white mb-4">Download Inventory</h3>

            <div className="space-y-3">
              <div>
                <label className="text-xs text-on-surface-variant mb-1 block">Format</label>
                <div className="flex gap-2">
                  {FORMATS.map(f => (
                    <button key={f} onClick={() => setDlFormat(f)}
                      className={`flex-1 py-2 rounded-lg text-xs font-medium ${dlFormat === f ? 'bg-white text-black' : 'bg-card-stroke text-white'}`}>
                      {f.toUpperCase()}
                    </button>
                  ))}
                </div>
              </div>

              <Select label="Dépôt" value={dlDepot} onChange={setDlDepot} options={['All', ...DEPOT_OPTIONS]} />
              <Select label="Équipe" value={dlEquipe} onChange={setDlEquipe} options={['All', ...EQUIPE_OPTIONS]} />
              <Select label="Zone" value={dlZone} onChange={setDlZone} options={['All', ...ZONE_OPTIONS]} />
            </div>

            <div className="flex gap-2 mt-6">
              <button onClick={() => setShowDownload(false)} className="flex-1 py-3 rounded-xl bg-card-stroke text-white text-xs font-medium">Cancel</button>
              <button onClick={handleDownload} disabled={dlLoading}
                className="flex-1 py-3 rounded-xl bg-white text-black text-xs font-medium disabled:opacity-50">
                {dlLoading ? 'Downloading...' : 'Download'}
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Main Form */}
      <Select label="Équipe" value={equipe} onChange={setEquipe} options={EQUIPE_OPTIONS} />
      <Select label="Dépôt" value={depot} onChange={setDepot} options={DEPOT_OPTIONS} />
      <Select label="Zone" value={zone} onChange={setZone} options={ZONE_OPTIONS} />

      <div>
        <label className="text-xs text-on-surface-variant font-medium mb-1 block">Article</label>
        <input type="text" value={article} onChange={e => setArticle(e.target.value)}
          placeholder="Article reference"
          className="w-full px-4 py-3 rounded-xl bg-surface-variant border border-card-stroke text-white text-sm outline-none placeholder:text-on-surface-variant" />
      </div>

      <div className="flex gap-3">
        <Input label="Pallet" value={pallet} onChange={setPallet} />
        <Input label="Carton" value={carton} onChange={setCarton} />
        <Input label="M²" value={metreCarre} onChange={setMetreCarre} />
      </div>

      {error && <p className={`text-xs ${error.includes('Error') ? 'text-error' : 'text-stock-green'}`}>{error}</p>}

      <button onClick={handleSubmit} disabled={submitMutation.isPending}
        className="w-full py-3 rounded-xl bg-white text-black text-sm font-medium disabled:opacity-50">
        {submitMutation.isPending ? 'Saving...' : 'Submit Inventory'}
      </button>

      <button onClick={() => setShowDownload(true)}
        className="w-full py-3 rounded-xl bg-card-stroke text-white text-xs font-medium">
        Download CSV
      </button>
    </div>
  )
}

function Select({ label, value, onChange, options }) {
  return (
    <div>
      <label className="text-xs text-on-surface-variant font-medium mb-1 block">{label}</label>
      <select value={value} onChange={e => onChange(e.target.value)}
        className="w-full px-4 py-3 rounded-xl bg-surface-variant border border-card-stroke text-white text-sm outline-none">
        <option value="">Select {label}</option>
        {options.map(o => <option key={o} value={o}>{o}</option>)}
      </select>
    </div>
  )
}

function Input({ label, value, onChange }) {
  return (
    <div className="flex-1">
      <label className="text-xs text-on-surface-variant font-medium mb-1 block">{label}</label>
      <input type="number" value={value} onChange={e => onChange(e.target.value)}
        className="w-full px-4 py-3 rounded-xl bg-surface-variant border border-card-stroke text-white text-sm outline-none"
        placeholder="0" step="any" />
    </div>
  )
}
