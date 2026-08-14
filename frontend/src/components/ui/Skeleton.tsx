interface SkeletonProps {
  className?: string
}

export function Skeleton({ className = '' }: SkeletonProps) {
  return (
    <div
      className={['rounded-lg bg-zinc-800 animate-pulse', className].join(' ')}
    />
  )
}

export function TrailCardSkeleton() {
  return (
    <div className="rounded-2xl border border-zinc-800 bg-zinc-900/80 p-6 space-y-4">
      <Skeleton className="h-5 w-1/3" />
      <Skeleton className="h-3 w-full" />
      <Skeleton className="h-3 w-4/5" />
      <div className="flex gap-2 pt-2">
        <Skeleton className="h-6 w-20 rounded-full" />
        <Skeleton className="h-6 w-16 rounded-full" />
      </div>
    </div>
  )
}

export function CertificateSkeleton() {
  return (
    <div className="rounded-2xl border border-zinc-800 bg-zinc-900/80 p-10 space-y-6 max-w-2xl mx-auto">
      <Skeleton className="h-8 w-2/3 mx-auto" />
      <Skeleton className="h-px w-full" />
      <Skeleton className="h-6 w-1/2 mx-auto" />
      <Skeleton className="h-4 w-3/4 mx-auto" />
      <Skeleton className="h-16 w-16 rounded-full mx-auto" />
    </div>
  )
}
