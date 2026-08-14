import { useState, useEffect } from 'react'
import { motion, AnimatePresence } from 'framer-motion'
import { useQuery } from '@tanstack/react-query'
import { Zap, Terminal } from 'lucide-react'
import Button from '../components/ui/Button'
import Card from '../components/ui/Card'
import { Skeleton } from '../components/ui/Skeleton'
import { fetchChallenge, type Level } from '../services/geoExplorer'

const TECHNOLOGIES = ['java', 'python', 'javascript']
const LEVELS: { value: Level; label: string }[] = [
  { value: 'BEGINNER',     label: 'Iniciante'     },
  { value: 'INTERMEDIATE', label: 'Intermediário' },
  { value: 'ADVANCED',     label: 'Avançado'      },
]

const levelBadge: Record<Level, string> = {
  BEGINNER:     'bg-emerald-500/15 text-emerald-400',
  INTERMEDIATE: 'bg-amber-500/15 text-amber-400',
  ADVANCED:     'bg-rose-500/15 text-rose-400',
}

export default function Challenge() {
  const [technology, setTechnology] = useState('java')
  const [level, setLevel]           = useState<Level>('BEGINNER')
  const [enabled, setEnabled]       = useState(false)
  const [generation, setGeneration] = useState(0)
  const [pending, setPending]       = useState<'generate' | 'another' | null>(null)

  const { data, isLoading, isFetching, isError } = useQuery({
    queryKey: ['challenge', technology, level, generation],
    queryFn: () => fetchChallenge(technology, level),
    enabled,
  })

  useEffect(() => {
    if (!isFetching) setPending(null)
  }, [isFetching])

  const handleGenerate = (from: 'generate' | 'another') => {
    setPending(from)
    setEnabled(true)
    setGeneration((g) => g + 1)
  }

  return (
    <motion.div key="challenge" initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }}
      transition={{ type: 'spring', stiffness: 260, damping: 22 }}>

      <div className="mb-10">
        <h1 className="text-3xl md:text-4xl font-bold text-zinc-100 tracking-tight">
          <span className="text-emerald-400">Desafio</span> de Código
        </h1>
        <p className="text-zinc-400 mt-2">Gere um desafio aleatório e teste seu conhecimento.</p>
      </div>

      {/* Form */}
      <Card hoverable={false} className="mb-8 max-w-xl">
        <div className="flex flex-col gap-5">
          {/* Tecnologia */}
          <div>
            <label className="text-xs text-zinc-400 font-medium mb-2 block uppercase tracking-wider">
              Tecnologia
            </label>
            <div className="flex flex-wrap gap-2">
              {TECHNOLOGIES.map((t) => (
                <button
                  key={t}
                  onClick={() => { setTechnology(t); setEnabled(false) }}
                  className={[
                    'px-4 py-2 rounded-xl text-sm font-medium border transition-all',
                    technology === t
                      ? 'bg-emerald-500/20 border-emerald-500/50 text-emerald-300'
                      : 'border-zinc-700 text-zinc-400 hover:border-zinc-500 hover:text-zinc-200',
                  ].join(' ')}
                >
                  {t}
                </button>
              ))}
            </div>
          </div>

          {/* Nível */}
          <div>
            <label className="text-xs text-zinc-400 font-medium mb-2 block uppercase tracking-wider">
              Nível
            </label>
            <div className="flex flex-wrap gap-2">
              {LEVELS.map(({ value, label }) => (
                <button
                  key={value}
                  onClick={() => { setLevel(value); setEnabled(false) }}
                  className={[
                    'px-4 py-2 rounded-xl text-sm font-medium border transition-all',
                    level === value
                      ? 'bg-emerald-500/20 border-emerald-500/50 text-emerald-300'
                      : 'border-zinc-700 text-zinc-400 hover:border-zinc-500 hover:text-zinc-200',
                  ].join(' ')}
                >
                  {label}
                </button>
              ))}
            </div>
          </div>

          <Button onClick={() => handleGenerate('generate')} loading={pending === 'generate'} className="self-start mt-1">
            <Zap className="size-4" />
            {pending === 'generate' ? 'Gerando…' : 'Gerar Desafio'}
          </Button>
        </div>
      </Card>

      {/* Result */}
      <AnimatePresence mode="wait">
        {isLoading && (
          <motion.div key="skel" initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }}
            className="max-w-xl space-y-3">
            <Skeleton className="h-6 w-2/3" />
            <Skeleton className="h-4 w-full" />
            <Skeleton className="h-4 w-4/5" />
            <Skeleton className="h-32 w-full rounded-xl" />
          </motion.div>
        )}

        {isError && (
          <motion.div key="err" initial={{ opacity: 0 }} animate={{ opacity: 1 }}
            className="rounded-xl border border-rose-500/30 bg-rose-500/10 p-5 text-rose-400 text-sm max-w-xl">
            ⚠️ Não foi possível buscar um desafio. Verifique se o backend está rodando.
          </motion.div>
        )}

        {data && !isLoading && (
          <motion.div key="result" initial={{ opacity: 0, y: 16 }} animate={{ opacity: 1, y: 0 }}
            transition={{ type: 'spring', stiffness: 260, damping: 22 }}
            className="max-w-2xl">
            <Card hoverable={false} className="border-emerald-500/20">
              {/* Header do desafio */}
              <div className="flex items-start justify-between gap-4 mb-5">
                <div className="flex items-center gap-3">
                  <div className="size-10 rounded-xl bg-emerald-500/15 flex items-center justify-center">
                    <Terminal className="size-5 text-emerald-400" />
                  </div>
                  <h2 className="text-lg font-bold text-zinc-100">{data.title}</h2>
                </div>
                <span className={`text-xs px-2.5 py-1 rounded-full font-medium ${levelBadge[data.level]}`}>
                  {LEVELS.find(l => l.value === data.level)?.label}
                </span>
              </div>

              {/* Enunciado estilizado como editor */}
              <div className="rounded-xl bg-zinc-950 border border-zinc-800 p-5">
                <div className="flex items-center gap-1.5 mb-4">
                  <span className="size-3 rounded-full bg-rose-500/70" />
                  <span className="size-3 rounded-full bg-amber-500/70" />
                  <span className="size-3 rounded-full bg-emerald-500/70" />
                  <span className="ml-2 text-xs text-zinc-600 font-mono">{technology}.challenge</span>
                </div>
                <p className="text-sm text-zinc-300 leading-relaxed font-mono whitespace-pre-wrap">
                  {data.description}
                </p>
              </div>

              <Button variant="secondary" onClick={() => handleGenerate('another')} loading={pending === 'another'} className="mt-4">
                Gerar Outro
              </Button>
            </Card>
          </motion.div>
        )}
      </AnimatePresence>
    </motion.div>
  )
}
