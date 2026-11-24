# OpenRouter clone foundation

This repository keeps investigation notes separate from application code.

## Project layout

```text
docs/       Reverse-engineering reports and evidence notes
frontend/   React + TypeScript + Vite application
```

The frontend currently contains the Models catalogue UI foundation. Its catalogue entries are clearly labeled mock fixtures; no backend or OpenRouter API contract is implemented.

## Run the frontend

From the repository root:

```sh
npm install
npm run dev
```

Build for production with `npm run build`. Vite and frontend-specific TypeScript and Tailwind configuration live inside `frontend/`; the root npm workspace provides convenient commands and leaves room for future packages.
