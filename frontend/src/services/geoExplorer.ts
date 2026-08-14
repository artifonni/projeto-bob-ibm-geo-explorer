import api from './api'

// ── Types ────────────────────────────────────────────────────────────────────

export type Level = 'BEGINNER' | 'INTERMEDIATE' | 'ADVANCED'

export interface ModuleDTO {
  moduleOrder: number
  title: string
  content: string
}

export interface TrailDTO {
  technology: string
  description: string
  level: Level
  modules: ModuleDTO[]
}

export interface ChallengeDTO {
  title: string
  description: string
  level: Level
}

// ── API calls ────────────────────────────────────────────────────────────────

export const fetchTrail = (technology: string): Promise<TrailDTO> =>
  api.get<TrailDTO>(`/trail?technology=${technology}`).then((r) => r.data)

export const fetchChallenge = (technology: string, level: Level): Promise<ChallengeDTO> =>
  api
    .get<ChallengeDTO>(`/challenge?technology=${technology}&level=${level}`)
    .then((r) => r.data)

export const fetchCertificate = (technology: string, userName: string): Promise<string> =>
  api
    .get<string>(`/certificate?technology=${technology}&user=${encodeURIComponent(userName)}`)
    .then((r) => r.data)
