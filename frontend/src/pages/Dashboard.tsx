import { useEffect, useState } from 'react'
import { motion, AnimatePresence, type Variants } from 'framer-motion'
import { useQuery } from '@tanstack/react-query'
import { BookOpen, ChevronRight, Layers, X } from 'lucide-react'
import Card from '../components/ui/Card'
import { TrailCardSkeleton } from '../components/ui/Skeleton'
import { fetchTrail, type TrailDTO } from '../services/geoExplorer'

const TECHNOLOGIES = ['java', 'python', 'javascript'] as const

const levelColors = {
  BEGINNER:     'bg-emerald-500/15 text-emerald-400 border-emerald-500/30',
  INTERMEDIATE: 'bg-amber-500/15 text-amber-400 border-amber-500/30',
  ADVANCED:     'bg-rose-500/15 text-rose-400 border-rose-500/30',
}

const levelLabel = {
  BEGINNER: 'Iniciante', INTERMEDIATE: 'Intermediário', ADVANCED: 'Avançado',
}

// ── Sub-components ──────────────────────────────────────────────────────────

function TrailCard({ technology }: { technology: string }) {
  const { data, isLoading, isError } = useQuery<TrailDTO>({
    queryKey: ['trail', technology],
    queryFn: () => fetchTrail(technology),
  })

  const [open, setOpen] = useState(false)

  useEffect(() => {
    if (!open) return
    const onKey = (e: KeyboardEvent) => {
      if (e.key === 'Escape') setOpen(false)
    }
    window.addEventListener('keydown', onKey)
    return () => window.removeEventListener('keydown', onKey)
  }, [open])

  if (isLoading) return <TrailCardSkeleton />
  if (isError || !data)
    return (
      <div className="rounded-2xl border border-zinc-800 bg-zinc-900/60 p-6 text-zinc-500 text-sm">
        Não foi possível carregar a trilha de <strong>{technology}</strong>.
      </div>
    )

  return (
    <>
      <Card className="group flex flex-col gap-4 h-full" onClick={() => setOpen(true)}>
        {/* Header */}
        <div className="flex items-start justify-between gap-3">
          <div className="size-11 rounded-xl bg-zinc-800 flex items-center justify-center group-hover:bg-emerald-500/20 transition-colors">
            <BookOpen className="size-5 text-emerald-400" />
          </div>
          <span className={`text-xs px-2.5 py-1 rounded-full border font-medium ${levelColors[data.level]}`}>
            {levelLabel[data.level]}
          </span>
        </div>

        {/* Title + desc */}
        <div>
          <h2 className="text-lg font-bold text-zinc-100 capitalize">{data.technology}</h2>
          <p className="text-sm text-zinc-400 mt-1 line-clamp-2">{data.description}</p>
        </div>

        {/* Module list */}
        <ul className="flex flex-col gap-2 flex-1">
          {data.modules.slice(0, 3).map((mod) => (
            <li key={mod.moduleOrder} className="flex items-center gap-2 text-xs text-zinc-400">
              <span className="size-5 rounded-md bg-zinc-800 flex items-center justify-center text-emerald-400 font-bold text-[10px] shrink-0">
                {mod.moduleOrder}
              </span>
              <span className="truncate">{mod.title}</span>
            </li>
          ))}
          {data.modules.length > 3 && (
            <li className="text-xs text-zinc-600 pl-7">+ {data.modules.length - 3} mais módulos</li>
          )}
        </ul>

        {/* Footer */}
        <div className="flex items-center justify-between border-t border-zinc-800 pt-4 mt-auto">
          <div className="flex items-center gap-1.5 text-xs text-zinc-500">
            <Layers className="size-3.5" />
            {data.modules.length} módulos
          </div>
          <ChevronRight className="size-4 text-zinc-600 group-hover:text-emerald-400 transition-colors" />
        </div>
      </Card>

      {/* Modal com o conteúdo da trilha */}
      <AnimatePresence>
        {open && (
          <motion.div
            key="trail-detail"
            className="fixed inset-0 z-50 flex items-center justify-center p-4 md:p-8"
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
          >
            <div
              className="absolute inset-0 bg-zinc-950/80 backdrop-blur-sm"
              onClick={() => setOpen(false)}
            />

            <motion.div
              initial={{ scale: 0.92, y: 24, opacity: 0 }}
              animate={{ scale: 1, y: 0, opacity: 1 }}
              exit={{ scale: 0.92, y: 24, opacity: 0 }}
              transition={{ type: 'spring', stiffness: 260, damping: 24 }}
              className="relative w-full max-w-2xl max-h-[85vh] overflow-y-auto rounded-2xl border border-zinc-800 bg-zinc-900 shadow-2xl"
            >
              {/* Header */}
              <div className="sticky top-0 z-10 flex items-start justify-between gap-4 border-b border-zinc-800 bg-zinc-900/95 backdrop-blur p-6">
                <div>
                  <p className="text-xs text-zinc-500 uppercase tracking-wider mb-1">
                    Trilha · {data.technology}
                  </p>
                  <h2 className="text-xl font-bold text-zinc-100 capitalize">{data.technology}</h2>
                  <p className="text-sm text-zinc-400 mt-1">{data.description}</p>
                </div>
                <button
                  onClick={() => setOpen(false)}
                  aria-label="Fechar trilha"
                  className="size-9 shrink-0 rounded-xl border border-zinc-700 bg-zinc-800 text-zinc-400 hover:text-zinc-100 hover:border-zinc-500 flex items-center justify-center transition-colors"
                >
                  <X className="size-4" />
                </button>
              </div>

              {/* Módulos com conteúdo */}
              <div className="p-6 flex flex-col gap-4">
                {data.modules.map((mod) => (
                  <div key={mod.moduleOrder} className="rounded-xl border border-zinc-800 bg-zinc-950/60 p-5">
                    <div className="flex items-center gap-3 mb-3">
                      <span className="size-6 rounded-lg bg-emerald-500/15 text-emerald-400 flex items-center justify-center text-xs font-bold shrink-0">
                        {mod.moduleOrder}
                      </span>
                      <h3 className="text-base font-semibold text-zinc-100">{mod.title}</h3>
                    </div>
                    <p className="text-sm text-zinc-400 leading-relaxed">{mod.content}</p>
                  </div>
                ))}
              </div>
            </motion.div>
          </motion.div>
        )}
      </AnimatePresence>
    </>
  )
}

// ── Page ─────────────────────────────────────────────────────────────────────

const containerVariants: Variants = {
  hidden: {},
  show: { transition: { staggerChildren: 0.12 } },
}

const itemVariants: Variants = {
  hidden: { opacity: 0, y: 24 },
  show:   { opacity: 1, y: 0, transition: { type: 'spring', stiffness: 260, damping: 22 } },
}

export default function Dashboard() {
  return (
    <motion.div
      key="dashboard"
      initial="hidden"
      animate="show"
      variants={containerVariants}
    >
      {/* Hero */}
      <motion.div variants={itemVariants} className="mb-10">
        <h1 className="text-3xl md:text-4xl font-bold text-zinc-100 tracking-tight">
          Explorador de <span className="text-emerald-400">Trilhas</span>
        </h1>
        <p className="text-zinc-400 mt-2 text-base">
          Escolha uma tecnologia e comece sua jornada de aprendizado.
        </p>
      </motion.div>

      {/* Grid de cards */}
      <motion.div
        variants={containerVariants}
        className="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-3 gap-6"
      >
        {TECHNOLOGIES.map((tech) => (
          <motion.div key={tech} variants={itemVariants} className="flex">
            <TrailCard technology={tech} />
          </motion.div>
        ))}
      </motion.div>
    </motion.div>
  )
}
