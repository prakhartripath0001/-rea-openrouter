import type { ModelFixture } from '../types/model'

/** Fictional UI fixtures. Values are placeholders, not OpenRouter API data. */
export const mockModels: ModelFixture[] = [
  {
    id: 'northstar/chat-2',
    name: 'Chat 2',
    author: 'Northstar',
    description: 'A balanced general-purpose assistant for writing, analysis, and everyday questions.',
    context: '128K', promptPrice: '$0.20', outputPrice: '$0.80',
    modalities: ['Text', 'Image'], tags: ['Tools', 'Fast'], accent: 'from-emerald-100 to-teal-50 text-emerald-700',
  },
  {
    id: 'aster/vision-pro',
    name: 'Vision Pro',
    author: 'Aster',
    description: 'A multimodal model designed to understand documents, charts, and visual scenes.',
    context: '96K', promptPrice: '$0.35', outputPrice: '$1.10',
    modalities: ['Text', 'Image'], tags: ['Vision', 'Tools'], accent: 'from-violet-100 to-fuchsia-50 text-violet-700',
  },
  {
    id: 'orbit/embed-large',
    name: 'Embed Large',
    author: 'Orbit',
    description: 'Semantic embeddings for retrieval, clustering, and long-form knowledge collections.',
    context: '32K', promptPrice: '$0.08', outputPrice: '—',
    modalities: ['Text', 'Embeddings'], tags: ['Embedding'], accent: 'from-sky-100 to-cyan-50 text-sky-700',
  },
  {
    id: 'ember/speech-live',
    name: 'Speech Live',
    author: 'Ember',
    description: 'Low-latency speech understanding with natural turn-taking and audio responses.',
    context: '64K', promptPrice: '$0.45', outputPrice: '$1.40',
    modalities: ['Text', 'Audio'], tags: ['Audio', 'Fast'], accent: 'from-amber-100 to-orange-50 text-amber-800',
  },
  {
    id: 'northstar/reasoner',
    name: 'Reasoner',
    author: 'Northstar',
    description: 'A deliberate reasoning model for multi-step planning and technical problem solving.',
    context: '160K', promptPrice: '$0.55', outputPrice: '$2.20',
    modalities: ['Text'], tags: ['Reasoning', 'Tools'], accent: 'from-rose-100 to-pink-50 text-rose-700',
  },
  {
    id: 'lumen/video-understand',
    name: 'Video Understand',
    author: 'Lumen',
    description: 'Summarize clips and answer questions about scenes, timing, and visual details.',
    context: '48K', promptPrice: '$0.60', outputPrice: '$1.80',
    modalities: ['Text', 'Video'], tags: ['Video'], accent: 'from-indigo-100 to-blue-50 text-indigo-700',
  },
]
