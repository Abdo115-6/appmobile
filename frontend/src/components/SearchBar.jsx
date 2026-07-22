export default function SearchBar({ value, onChange, onScan }) {
  return (
    <div className="mx-4 mb-3">
      <div className="flex items-center gap-2 rounded-2xl border border-card-stroke bg-surface-variant p-1">
        <div className="flex-1 flex items-center gap-2 px-3">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="#888"><path d="M15.5 14h-.79l-.28-.27A6.471 6.471 0 0 0 16 9.5 6.5 6.5 0 1 0 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z"/></svg>
          <input
            type="text"
            value={value}
            onChange={e => onChange(e.target.value)}
            placeholder="Search articles..."
            className="flex-1 bg-transparent text-sm text-white outline-none py-2.5 placeholder:text-on-surface-variant"
          />
        </div>
        {onScan && (
          <button onClick={onScan} className="w-12 h-12 flex items-center justify-center rounded-xl bg-surface-variant border border-card-stroke">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="white"><path d="M9.5 6.5v3h-3v-3h3M11 5H5v6h6V5zm-1.5 9.5v3h-3v-3h3M11 13H5v6h6v-6zm6.5-6.5v3h-3v-3h3M19 5h-6v6h6V5zm-6 8h1.5v1.5H13V13zm1.5 1.5H16V16h-1.5v-1.5zM16 13h1.5v1.5H16V13zm-3 3h1.5v1.5H13V16zm1.5 1.5H16V19h-1.5v-1.5zM16 16h1.5v1.5H16V16zm1.5-1.5H19V16h-1.5v-1.5zm0 3H19V19h-1.5v-1.5z"/></svg>
          </button>
        )}
      </div>
    </div>
  )
}
