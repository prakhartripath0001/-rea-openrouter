export interface ProviderOption {
  id: string
  slug: string
  name: string
  description: string | null
  modelCount: number
}

export interface ModelPricing {
  currency: string
  billingUnit: string
  promptRatePerMillion: number
  outputRatePerMillion: number | null
  free: boolean
  discounted: boolean
}

export interface ModelProvider {
  id: string
  slug: string
  name: string
  providerModelKey: string | null
  pricing: ModelPricing[]
}

export interface ModelResponse {
  id: string
  modelKey: string
  name: string
  description: string
  contextLength: number
  providers: ModelProvider[]
  modalities: string[]
  tags: string[]
  supportedParameters: string[]
  createdAt: string
  updatedAt: string
}

export interface PageResponse<T> {
  content: T[]
  page: number
  size: number
  totalElements: number
  totalPages: number
  first: boolean
  last: boolean
}

export interface FilterOptionsResponse {
  modalities: string[]
  providers: ProviderOption[]
  tags: string[]
  supportedParameters: string[]
  minContextLength: number | null
  maxContextLength: number | null
  minPromptRatePerMillion: number | null
  maxPromptRatePerMillion: number | null
  minOutputRatePerMillion: number | null
  maxOutputRatePerMillion: number | null
}

export interface ModelQuery {
  q?: string
  modality?: string
  providers?: string[]
  tags?: string[]
  minContextLength?: number
  maxContextLength?: number
  maxPromptPricePerMillion?: number
  maxOutputPricePerMillion?: number
  freeOnly?: boolean
  discountedOnly?: boolean
  page?: number
  size?: number
  sort?: 'name' | 'contextLength' | 'createdAt' | 'updatedAt'
  direction?: 'asc' | 'desc'
}
