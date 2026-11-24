import { useEffect, useState } from 'react'
import { ArrowRight, LoaderCircle, Sparkles } from 'lucide-react'
import { getFilterOptions, getModels } from '../api/catalogue'
import type { FilterOptionsResponse } from '../api/types'
import { toModelCardModel } from '../adapters/modelAdapter'
import ModelCard from '../components/models/ModelCard'
import ModelFilters from '../components/models/ModelFilters'
import ModalityTabs from '../components/models/ModalityTabs'
import ModelsToolbar, { type ModelSort } from '../components/models/ModelsToolbar'
import ModelsTable from '../components/models/ModelsTable'
import type { ModelCardModel } from '../types/model'

export default function ModelsPage() {
  const [query, setQuery] = useState('')
  const [debouncedQuery, setDebouncedQuery] = useState('')
  const [modality, setModality] = useState('All models')
  const [view, setView] = useState<'list' | 'table'>('list')
  const [sort, setSort] = useState<ModelSort>('name')
  const [selectedProviders, setSelectedProviders] = useState<string[]>([])
  const [selectedTags, setSelectedTags] = useState<string[]>([])
  const [minContextLength, setMinContextLength] = useState<number>()
  const [freeOnly, setFreeOnly] = useState(false)
  const [discountedOnly, setDiscountedOnly] = useState(false)
  const [models, setModels] = useState<ModelCardModel[]>([])
  const [totalModels, setTotalModels] = useState(0)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [filterOptions, setFilterOptions] = useState<FilterOptionsResponse | null>(null)
  const [filtersLoading, setFiltersLoading] = useState(true)
  const [filtersError, setFiltersError] = useState<string | null>(null)
  const [retryKey, setRetryKey] = useState(0)

  useEffect(() => {
    const timer = window.setTimeout(() => setDebouncedQuery(query), 250)
    return () => window.clearTimeout(timer)
  }, [query])

  useEffect(() => {
    let cancelled = false
    setLoading(true)
    setError(null)
    getModels({
      q: debouncedQuery,
      modality: modality === 'All models' ? undefined : modality,
      providers: selectedProviders,
      tags: selectedTags,
      minContextLength,
      freeOnly,
      discountedOnly,
      page: 0,
      size: 50,
      sort,
      direction: 'asc',
    })
      .then((page) => {
        if (cancelled) return
        setModels(page.content.map(toModelCardModel))
        setTotalModels(page.totalElements)
      })
      .catch((requestError: unknown) => {
        if (cancelled) return
        setModels([])
        setTotalModels(0)
        setError(requestError instanceof Error ? requestError.message : 'Could not load models.')
      })
      .finally(() => {
        if (!cancelled) setLoading(false)
      })
    return () => { cancelled = true }
  }, [debouncedQuery, modality, selectedProviders, selectedTags, minContextLength, freeOnly, discountedOnly, sort, retryKey])

  useEffect(() => {
    let cancelled = false
    setFiltersLoading(true)
    setFiltersError(null)
    getFilterOptions()
      .then((options) => { if (!cancelled) setFilterOptions(options) })
      .catch((requestError: unknown) => {
        if (!cancelled) setFiltersError(requestError instanceof Error ? requestError.message : 'Could not load filter options.')
      })
      .finally(() => { if (!cancelled) setFiltersLoading(false) })
    return () => { cancelled = true }
  }, [retryKey])

  function toggle(values: string[], value: string, setValues: (next: string[]) => void) {
    setValues(values.includes(value) ? values.filter((item) => item !== value) : [...values, value])
  }

  function clearFilters() {
    setSelectedProviders([])
    setSelectedTags([])
    setMinContextLength(undefined)
    setFreeOnly(false)
    setDiscountedOnly(false)
  }

  return (
    <main id="models" className="mx-auto max-w-[1440px] px-4 pb-16 pt-7 sm:px-7 sm:pt-10">
      <div className="mb-7 flex flex-col justify-between gap-4 border-b border-line pb-6 sm:flex-row sm:items-end">
        <div>
          <div className="mb-3 inline-flex items-center gap-1.5 rounded-full border border-[#d9e9de] bg-[#eff8f1] px-2.5 py-1 text-[10px] font-semibold uppercase tracking-[0.12em] text-[#467a59]"><Sparkles size={12} /> Catalogue UI · sample fixtures</div>
          <h1 className="text-[30px] font-semibold tracking-[-0.045em] text-ink sm:text-[36px]">Models</h1>
          <p className="mt-1.5 max-w-xl text-[13px] leading-5 text-muted">Explore the local model catalogue and its provider, modality, and pricing filters.</p>
        </div>
        <a href="#catalogue" className="inline-flex items-center gap-1.5 self-start text-[12px] font-medium text-[#317451] hover:text-[#1b5638] sm:self-auto">Browse the catalogue <ArrowRight size={14} /></a>
      </div>

      <div className="mb-5 rounded-lg border border-[#e5e9e6] bg-white px-3.5 py-2.5 text-[11px] leading-5 text-muted">
        <span className="mr-2 inline-flex rounded bg-[#f0f2f1] px-1.5 py-0.5 text-[9px] font-bold uppercase tracking-wide text-[#58635d]">Demo data</span>
        Models and prices come from this clone's local seed catalogue. They are illustrative and are not live OpenRouter data.
      </div>

      <div className="mb-5"><ModalityTabs active={modality} onChange={setModality} /></div>
      <ModelsToolbar query={query} onQueryChange={setQuery} view={view} onViewChange={setView} sort={sort} onSortChange={setSort} />

      <div id="catalogue" className="mt-5 flex flex-col gap-5 lg:flex-row lg:items-start">
        <ModelFilters
          providers={filterOptions?.providers ?? []}
          tags={filterOptions?.tags ?? []}
          selectedProviders={selectedProviders}
          selectedTags={selectedTags}
          minContextLength={minContextLength}
          maxContextLength={filterOptions?.maxContextLength}
          freeOnly={freeOnly}
          discountedOnly={discountedOnly}
          onToggleProvider={(slug) => toggle(selectedProviders, slug, setSelectedProviders)}
          onToggleTag={(tag) => toggle(selectedTags, tag, setSelectedTags)}
          onContextLengthChange={setMinContextLength}
          onFreeOnlyChange={setFreeOnly}
          onDiscountedOnlyChange={setDiscountedOnly}
          onClear={clearFilters}
        />
        <section className="min-w-0 flex-1" aria-label="Model results" aria-busy={loading}>
          <div className="mb-3 flex items-center justify-between px-0.5">
            <p className="text-[11px] text-muted"><span className="font-semibold text-ink">{totalModels}</span> {totalModels === 1 ? 'model' : 'models'}</p>
            <span className="text-[10px] text-[#929b97]">{filtersLoading ? 'Loading filter options…' : 'Local catalogue'}</span>
          </div>

          {filtersError && <p role="alert" className="mb-3 rounded-lg border border-amber-200 bg-amber-50 px-3 py-2 text-xs text-amber-900">Filter options unavailable: {filtersError}</p>}
          {error ? (
            <div role="alert" className="grid min-h-56 place-items-center rounded-xl border border-red-200 bg-white px-6 text-center">
              <div><p className="text-sm font-semibold text-ink">Couldn’t load models</p><p className="mt-1 text-xs text-muted">{error}</p><button onClick={() => setRetryKey((value) => value + 1)} className="mt-3 rounded-lg bg-ink px-3 py-2 text-xs font-medium text-white hover:bg-[#293632]">Retry</button></div>
            </div>
          ) : loading ? (
            <div role="status" className="grid min-h-56 place-items-center rounded-xl border border-line bg-white text-xs text-muted"><span className="inline-flex items-center gap-2"><LoaderCircle size={15} className="animate-spin" /> Loading models…</span></div>
          ) : models.length === 0 ? (
            <div className="grid min-h-56 place-items-center rounded-xl border border-dashed border-[#d8dfda] bg-white px-6 text-center">
              <div><p className="text-sm font-semibold text-ink">No models match these filters</p><p className="mt-1 text-xs text-muted">Try another search or clear one of the selected filters.</p></div>
            </div>
          ) : view === 'list' ? (
            <div className="flex flex-col gap-2.5">{models.map((model) => <ModelCard key={model.id} model={model} view="list" />)}</div>
          ) : (
            <ModelsTable models={models} />
          )}
        </section>
      </div>
      <footer className="mt-12 border-t border-line pt-5 text-[10px] text-[#89928e]">OpenRouter clone · local model catalogue</footer>
    </main>
  )
}
