import { Component, type ReactNode } from 'react'

interface Props   { children: ReactNode }
interface State   { hasError: boolean; message: string }

export class ErrorBoundary extends Component<Props, State> {
  state: State = { hasError: false, message: '' }

  static getDerivedStateFromError(error: Error): State {
    return { hasError: true, message: error.message }
  }

  render() {
    if (this.state.hasError)
      return (
        <div className="flex flex-col items-center justify-center min-h-[60vh] gap-4 text-center px-6">
          <div className="size-16 rounded-full bg-rose-500/15 flex items-center justify-center text-3xl">⚠️</div>
          <h2 className="text-xl font-bold text-zinc-100">Algo deu errado</h2>
          <p className="text-sm text-zinc-500 max-w-sm">{this.state.message}</p>
          <button
            onClick={() => this.setState({ hasError: false, message: '' })}
            className="mt-2 text-sm text-emerald-400 hover:text-emerald-300 underline"
          >
            Tentar novamente
          </button>
        </div>
      )
    return this.props.children
  }
}
