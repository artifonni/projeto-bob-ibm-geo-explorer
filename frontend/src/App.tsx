import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import AppLayout from './components/layout/AppLayout'
import { ErrorBoundary } from './components/ErrorBoundary'
import Dashboard   from './pages/Dashboard'
import Challenge   from './pages/Challenge'
import Certificate from './pages/Certificate'

export default function App() {
  return (
    <BrowserRouter>
      <AppLayout>
        <ErrorBoundary>
          <Routes>
            <Route path="/"            element={<Dashboard   />} />
            <Route path="/challenge"   element={<Challenge   />} />
            <Route path="/certificate" element={<Certificate />} />
            <Route path="*"            element={<Navigate to="/" replace />} />
          </Routes>
        </ErrorBoundary>
      </AppLayout>
    </BrowserRouter>
  )
}
