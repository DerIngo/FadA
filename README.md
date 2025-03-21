# FadA
Fragen an den Autor<br/>
Backup der Folgen, da diese nach drei Monaten gelöscht werden müssen.

# Datenbank
```bash
cd database
docker compose up -d
docker logs --follow fada-db
```
Die Datenbank muss einmalig mit dem create.sql-Script angelegt werden.

# Backend
Die Application.java Klasse starten, der Scheduler läuft dann im Hintergrund.

# Frontend
## Installation mit Vite
```bash
npm create vite@latest frontend --template react
cd frontend
npm install
npm install --save-dev rimraf
npm install axios
npm run dev
```
## Frontend bauen
```bash
npm run build-win
```

# Docker
## Image bauen
```bash
docker build -t fada:1.0 .
```