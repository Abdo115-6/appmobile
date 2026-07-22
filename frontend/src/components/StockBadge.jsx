export default function StockBadge({ physicalStock }) {
  const inStock = physicalStock > 0
  return (
    <span className={`inline-flex items-center gap-1.5 px-2.5 py-0.5 rounded-lg text-[10px] font-bold ${inStock ? 'text-stock-green' : 'text-stock-red'}`}
      style={{ backgroundColor: inStock ? 'rgba(76,175,80,0.1)' : 'rgba(255,82,82,0.1)' }}>
      <span className={`w-2 h-2 rounded-full ${inStock ? 'bg-stock-green' : 'bg-stock-red'}`} />
      {inStock ? 'IN STOCK' : 'OUT OF STOCK'}
    </span>
  )
}
