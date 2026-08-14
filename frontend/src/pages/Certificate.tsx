import { useState } from 'react'
import { motion, AnimatePresence } from 'framer-motion'
import { useQuery } from '@tanstack/react-query'
import { Award, Sparkles } from 'lucide-react'
import Button from '../components/ui/Button'
import Card from '../components/ui/Card'
import { Skeleton } from '../components/ui/Skeleton'
import { fetchCertificate } from '../services/geoExplorer'

const TECHNOLOGIES = ['java', 'python', 'javascript']

const techLabel: Record<string, string> = {
  java: 'Java', python: 'Python', javascript: 'JavaScript',
}

export default function Certificate() {
  const [technology, setTechnology] = useState('java')
  const [userName, setUserName]     = useState('')
  const [enabled, setEnabled]       = useState(false)
  const [submitted, setSubmitted]   = useState(false)

  const { data, isLoading, isError, refetch } = useQuery({
    queryKey: ['certificate', technology, userName],
    queryFn: () => fetchCertificate(technology, userName),
    enabled: enabled && userName.trim().length > 0,
  })

  const handleGenerate = () => {
    if (!userName.trim()) return
    setEnabled(true)
    setSubmitted(true)
    refetch()
  }

  const handleClear = () => {
    setSubmitted(false)
    setEnabled(false)
    setUserName('')
  }

  return (
    <motion.div key="certificate" initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }}
      transition={{ type: 'spring', stiffness: 260, damping: 22 }}>

      <div className="mb-10">
        <h1 className="text-3xl md:text-4xl font-bold text-zinc-100 tracking-tight">
          <span className="text-emerald-400">Certificado</span> Digital
        </h1>
        <p className="text-zinc-400 mt-2">Gere seu certificado ao concluir uma trilha.</p>
      </div>

      {/* Form */}
      <Card hoverable={false} className="mb-8 max-w-xl">
        <div className="flex flex-col gap-5">
          {/* Tecnologia */}
          <div>
            <label className="text-xs text-zinc-400 font-medium mb-2 block uppercase tracking-wider">
              Trilha concluída
            </label>
            <div className="flex flex-wrap gap-2">
              {TECHNOLOGIES.map((t) => (
                <button key={t} onClick={() => { setTechnology(t); setEnabled(false) }}
                  className={[
                    'px-4 py-2 rounded-xl text-sm font-medium border transition-all',
                    technology === t
                      ? 'bg-emerald-500/20 border-emerald-500/50 text-emerald-300'
                      : 'border-zinc-700 text-zinc-400 hover:border-zinc-500 hover:text-zinc-200',
                  ].join(' ')}>
                  {techLabel[t]}
                </button>
              ))}
            </div>
          </div>

          {/* Nome */}
          <div>
            <label className="text-xs text-zinc-400 font-medium mb-2 block uppercase tracking-wider">
              Seu nome completo
            </label>
            <input
              type="text"
              value={userName}
              onChange={(e) => { setUserName(e.target.value); setEnabled(false) }}
              placeholder="Ex.: Ana Lima"
              className="w-full rounded-xl bg-zinc-800 border border-zinc-700 px-4 py-3 text-sm text-zinc-100 placeholder-zinc-600 focus:outline-none focus:border-emerald-500 focus:ring-1 focus:ring-emerald-500/50 transition-all"
            />
          </div>

          <Button onClick={handleGenerate} loading={isLoading}
            disabled={!userName.trim()} className="self-start mt-1">
            <Award className="size-4" />
            {isLoading ? 'Gerando…' : 'Emitir Certificado'}
          </Button>
        </div>
      </Card>

      {/* Diploma */}
      <AnimatePresence mode="wait">
        {isLoading && (
          <motion.div key="skel" initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }}
            className="max-w-2xl space-y-4">
            <Skeleton className="h-8 w-3/4 mx-auto" />
            <Skeleton className="h-px w-full" />
            <Skeleton className="h-6 w-1/2 mx-auto" />
            <Skeleton className="h-6 w-2/3 mx-auto" />
            <Skeleton className="h-16 w-16 rounded-full mx-auto mt-4" />
          </motion.div>
        )}

        {isError && (
          <motion.div key="err" initial={{ opacity: 0 }} animate={{ opacity: 1 }}
            className="rounded-xl border border-rose-500/30 bg-rose-500/10 p-5 text-rose-400 text-sm max-w-xl">
            ⚠️ Não foi possível gerar o certificado. Verifique se o backend está rodando.
          </motion.div>
        )}

        {submitted && data && !isLoading && (
          <motion.div key="diploma"
            initial={{ opacity: 0, scale: 0.96 }} animate={{ opacity: 1, scale: 1 }}
            transition={{ type: 'spring', stiffness: 240, damping: 20 }}
            className="max-w-2xl mx-auto">

            {/* Diploma card */}
            <div className="relative rounded-2xl border-2 border-amber-500/40 bg-zinc-900 p-8 md:p-12 overflow-hidden">
              {/* Gradient decorativo */}
              <div className="absolute inset-0 bg-gradient-to-br from-amber-500/5 via-transparent to-emerald-500/5 pointer-events-none" />
              <div className="absolute top-0 left-0 right-0 h-1 bg-gradient-to-r from-amber-500/60 via-emerald-400/60 to-amber-500/60" />

              {/* Selo */}
              <div className="flex justify-center mb-6">
                <div className="relative">
                  <div className="size-20 rounded-full bg-gradient-to-br from-amber-400 to-emerald-500 flex items-center justify-center shadow-xl shadow-amber-500/20">
                    <Award className="size-9 text-zinc-950" />
                  </div>
                  <Sparkles className="size-4 text-amber-300 absolute -top-1 -right-1 animate-pulse" />
                </div>
              </div>

              {/* Título */}
              <div className="text-center mb-6">
                <p className="text-xs text-zinc-500 uppercase tracking-[0.2em] font-medium mb-2">
                  Geo-Explorer · Certificado de Conclusão
                </p>
                <div className="h-px bg-gradient-to-r from-transparent via-amber-500/40 to-transparent mb-6" />
                <p className="text-zinc-400 text-sm mb-2">Certificamos que</p>
                <p className="font-serif text-3xl md:text-4xl font-bold text-amber-300 tracking-wide">
                  {userName}
                </p>
              </div>

              {/* Corpo */}
              <div className="text-center mb-8">
                <p className="text-zinc-400 text-sm mb-1">concluiu com êxito a trilha de estudos em</p>
                <p className="text-xl font-bold text-emerald-400 capitalize">{techLabel[technology]}</p>
              </div>

              <div className="h-px bg-gradient-to-r from-transparent via-zinc-700 to-transparent mb-6" />

              {/* Rodapé */}
              <div className="flex items-center justify-between text-xs text-zinc-600">
                <span>geo-explorer.local</span>
                <span className="flex items-center gap-1.5">
                  <span className="size-2 rounded-full bg-emerald-500 animate-pulse" />
                  Certificado Válido
                </span>
              </div>
            </div>

            <Button variant="outline" onClick={handleClear} className="mt-6 mx-auto flex">
              Limpar
            </Button>
          </motion.div>
        )}
      </AnimatePresence>
    </motion.div>
  )
}
