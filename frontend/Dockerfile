# =============================================================================
# Geo-Explorer — Frontend (React 19 + Vite 8)
# Multi-stage: deps -> dev (hot reload) | build -> prod (nginx)
# =============================================================================

# -----------------------------------------------------------------------------
# Estágio 1 — Dependências (a partir do lockfile)
# -----------------------------------------------------------------------------
FROM node:22-slim AS deps
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm ci

# -----------------------------------------------------------------------------
# Estágio 2 — Desenvolvimento (Vite com hot reload) — target: dev
# -----------------------------------------------------------------------------
FROM deps AS dev
COPY . .
EXPOSE 5173
CMD ["npm", "run", "dev", "--", "--host", "0.0.0.0"]

# -----------------------------------------------------------------------------
# Estágio 3 — Build de produção (tsc + vite build)
# -----------------------------------------------------------------------------
FROM deps AS build
COPY . .
RUN npm run build

# -----------------------------------------------------------------------------
# Estágio 4 — Produção (nginx servindo o dist + proxy /api) — target: prod
# -----------------------------------------------------------------------------
FROM nginx:1.27-alpine AS prod
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist /usr/share/nginx/html
EXPOSE 80
