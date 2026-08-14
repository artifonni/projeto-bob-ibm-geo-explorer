import { NavLink } from 'react-router-dom'
import { BookOpen, Zap, Award, Globe, Copyright } from 'lucide-react'
import { AnimatePresence } from 'framer-motion'

const navItems = [
  { to: '/',            icon: BookOpen, label: 'Trilhas'     },
  { to: '/challenge',   icon: Zap,      label: 'Desafio'     },
  { to: '/certificate', icon: Award,    label: 'Certificado' },
]

interface AppLayoutProps {
  children: React.ReactNode
}

export default function AppLayout({ children }: AppLayoutProps) {
  return (
    <div className="min-h-screen flex flex-col md:flex-row bg-zinc-950">

      {/* ── Sidebar desktop ── */}
      <aside className="hidden md:flex flex-col w-64 shrink-0 border-r border-zinc-800 bg-zinc-900/60 backdrop-blur-md">
        {/* Logo */}
        <div className="flex items-center gap-3 px-6 py-7 border-b border-zinc-800">
          <div className="size-9 rounded-xl bg-emerald-500 flex items-center justify-center shadow-lg shadow-emerald-500/30">
            <Globe className="size-5 text-zinc-950" />
          </div>
          <div>
            <p className="text-sm font-bold text-zinc-100 leading-none">Geo Explorer</p>
            <p className="text-[11px] text-zinc-500 mt-0.5">Trilhas de Estudo</p>
          </div>
        </div>

        {/* Nav */}
        <nav className="flex flex-col gap-1 p-4 flex-1">
          {navItems.map(({ to, icon: Icon, label }) => (
            <NavLink
              key={to}
              to={to}
              end={to === '/'}
              className={({ isActive }) =>
                [
                  'flex items-center gap-3 px-4 py-3 rounded-xl text-sm font-medium transition-all duration-150',
                  isActive
                    ? 'bg-emerald-500/15 text-emerald-400 border border-emerald-500/30'
                    : 'text-zinc-400 hover:text-zinc-100 hover:bg-zinc-800',
                ].join(' ')
              }
            >
              <Icon className="size-4 shrink-0" />
              {label}
            </NavLink>
          ))}
        </nav>

        <div className="p-4 border-t border-zinc-800">
          <div className="flex items-center justify-center gap-1.5 text-xs text-zinc-500">
            <Globe className="size-3.5 text-emerald-400" />
            Geo Explorer
          </div>
        </div>
      </aside>

      {/* ── Main ── */}
      <div className="flex-1 flex flex-col min-h-screen">
        <main className="flex-1 p-6 md:p-10 pb-24 md:pb-10">
          <AnimatePresence mode="wait">
            {children}
          </AnimatePresence>
        </main>

        {/* ── Rodapé ── */}
        <footer className="pb-20 md:pb-0">
          <div className="border-t border-zinc-800 bg-zinc-900/60">
            <p className="flex items-center justify-center gap-1.5 px-6 py-4 text-xs text-zinc-500">
              <Copyright className="size-3.5 text-zinc-600" />
              Desenvolvido por{' '}
              <span className="font-medium text-zinc-300">Sérgio Artifon</span>
            </p>
          </div>
        </footer>
      </div>

      {/* ── Bottom nav mobile ── */}
      <nav className="md:hidden fixed bottom-0 left-0 right-0 z-50 flex justify-around items-center border-t border-zinc-800 bg-zinc-900/95 backdrop-blur-md py-3 px-2">
        {navItems.map(({ to, icon: Icon, label }) => (
          <NavLink
            key={to}
            to={to}
            end={to === '/'}
            className={({ isActive }) =>
              [
                'flex flex-col items-center gap-1 px-4 py-1 rounded-xl text-xs transition-all',
                isActive ? 'text-emerald-400' : 'text-zinc-500',
              ].join(' ')
            }
          >
            <Icon className="size-5" />
            <span>{label}</span>
          </NavLink>
        ))}
      </nav>
    </div>
  )
}
