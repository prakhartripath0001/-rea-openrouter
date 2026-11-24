import type { ModelResponse } from '../api/types'
import type { ModelCardModel, Modality } from '../types/model'

const accents = [
  'from-emerald-100 to-teal-50 text-emerald-700',
  'from-violet-100 to-fuchsia-50 text-violet-700',
  'from-sky-100 to-cyan-50 text-sky-700',
  'from-amber-100 to-orange-50 text-amber-800',
  'from-rose-100 to-pink-50 text-rose-700',
  'from-indigo-100 to-blue-50 text-indigo-700',
]

function formatContextLength(tokens: number): string {
  if (tokens >= 1000) {
    const thousands = tokens / 1000
    return `${Number.isInteger(thousands) ? thousands : thousands.toFixed(1)}K`
  }
  return tokens.toLocaleString()
}

function formatRate(value: number | null | undefined, currency: string): string {
  if (value == null) return '—'
  const symbol = currency.toUpperCase() === 'USD' ? '$' : `${currency.toUpperCase()} `
  return `${symbol}${value.toFixed(2)}`
}

export function toModelCardModel(model: ModelResponse): ModelCardModel {
  const primaryProvider = model.providers[0]
  const tokenPrices = primaryProvider?.pricing.filter((price) => price.billingUnit === 'PER_MILLION_TOKENS') ?? []
  const displayPrice = tokenPrices[0]
  const author = primaryProvider?.name ?? 'Unknown provider'
  const accentIndex = primaryProvider
    ? Array.from(primaryProvider.slug).reduce((value, character) => value + character.charCodeAt(0), 0) % accents.length
    : 0

  return {
    id: model.id,
    modelKey: model.modelKey,
    name: model.name,
    author,
    description: model.description,
    context: formatContextLength(model.contextLength),
    promptPrice: formatRate(displayPrice?.promptRatePerMillion, displayPrice?.currency ?? 'USD'),
    outputPrice: formatRate(displayPrice?.outputRatePerMillion, displayPrice?.currency ?? 'USD'),
    modalities: model.modalities as Modality[],
    tags: model.tags,
    accent: accents[accentIndex],
  }
}
