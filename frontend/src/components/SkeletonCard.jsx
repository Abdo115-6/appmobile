export default function SkeletonCard() {
  return (
    <div className="mx-4 mb-2 rounded-2xl border border-card-stroke bg-card-dark p-4 animate-pulse">
      <div className="flex gap-2">
        <div className="w-20 h-5 rounded-lg bg-card-stroke" />
        <div className="flex-1 h-5 rounded bg-card-stroke" />
      </div>
      <div className="flex gap-4 mt-3">
        <div className="flex-1 h-4 rounded bg-card-stroke" />
        <div className="flex-1 h-4 rounded bg-card-stroke" />
      </div>
      <div className="h-px bg-card-stroke my-3" />
      <div className="flex gap-2">
        <div className="w-10 h-10 rounded-xl bg-card-stroke" />
        <div className="flex-1 h-10 rounded-xl bg-card-stroke" />
      </div>
    </div>
  )
}
