import { apiGet } from './client'
import type { FilterOptionsResponse, ModelQuery, ModelResponse, PageResponse } from './types'

export function getModels(query: ModelQuery = {}): Promise<PageResponse<ModelResponse>> {
  const params = new URLSearchParams()
  if (query.q?.trim()) params.set('q', query.q.trim())
  if (query.modality) params.set('modality', query.modality)
  query.providers?.forEach((provider) => params.append('providers', provider))
  query.tags?.forEach((tag) => params.append('tags', tag))
  if (query.minContextLength !== undefined) params.set('minContextLength', String(query.minContextLength))
  if (query.maxContextLength !== undefined) params.set('maxContextLength', String(query.maxContextLength))
  if (query.maxPromptPricePerMillion !== undefined) params.set('maxPromptPricePerMillion', String(query.maxPromptPricePerMillion))
  if (query.maxOutputPricePerMillion !== undefined) params.set('maxOutputPricePerMillion', String(query.maxOutputPricePerMillion))
  if (query.freeOnly) params.set('freeOnly', 'true')
  if (query.discountedOnly) params.set('discountedOnly', 'true')
  if (query.page !== undefined) params.set('page', String(query.page))
  if (query.size !== undefined) params.set('size', String(query.size))
  if (query.sort) params.set('sort', query.sort)
  if (query.direction) params.set('direction', query.direction)
  return apiGet('/models', params)
}

export function getModel(id: string): Promise<ModelResponse> {
  return apiGet(`/models/${encodeURIComponent(id)}`)
}

export function getFilterOptions(): Promise<FilterOptionsResponse> {
  return apiGet('/filters')
}
