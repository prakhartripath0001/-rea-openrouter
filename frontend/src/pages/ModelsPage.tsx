import { useMemo, useState } from 'react'
import { ArrowRight, Sparkles } from 'lucide-react'
import ModelCard from '../components/models/ModelCard'
import ModelFilters from '../components/models/ModelFilters'
import ModalityTabs from '../components/models/ModalityTabs'
import ModelsToolbar from '../components/models/ModelsToolbar'
import ModelsTable from '../components/models/ModelsTable'
import { mockModels } from '../data/mockModels'
import type { Modality } from '../types/model'

export default function ModelsPage() {
  const [query, setQuery] = useState('')
  const [modality, setModality] = useState('All models')
  const [view, setView] = useState<'list' | 'table'>('list')
  const [selectedFilters, setSelectedFilters] = useState<string[]>([])

  const visibleModels = useMemo(() => mockModels.filter((model) => {
    const text = `${model.name} ${model.author} ${model.description} ${model.id}`.toLowerCase()
    const matchesQuery = text.includes(query.trim().toLowerCase())
    const matchesModality = modality === 'All models' || model.modalities.includes(modality as Modality)
    const providers = ['Northstar', 'Aster', 'Orbit', 'Ember', 'Lumen']
    const providerFilters = selectedFilters.filter((filter) => providers.includes(filter))
    const capabilityFilters = selectedFilters.filter((filter) => !providers.includes(filter))
    const matchesProvider = providerFilters.length === 0 || providerFilters.includes(model.author)
    const matchesCapability = capabilityFilters.every((filter) => model.tags.includes(filter))
    return matchesQuery && matchesModality && matchesProvider && matchesCapability
  }), [modality, query, selectedFilters])

  function toggleFilter(value: string) {
    setSelectedFilters((current) => current.includes(value) ? current.filter((item) => item !== value) : [...current, value])
  }

  return (
    <main id="models" className="mx-auto max-w-[1440px] px-4 pb-16 pt-7 sm:px-7 sm:pt-10">
      <div className="mb-7 flex flex-col justify-between gap-4 border-b border-line pb-6 sm:flex-row sm:items-end">
        <div>
          <div className="mb-3 inline-flex items-center gap-1.5 rounded-full border border-[#d9e9de] bg-[#eff8f1] px-2.5 py-1 text-[10px] font-semibold uppercase tracking-[0.12em] text-[#467a59]"><Sparkles size={12} /> Catalogue UI · sample fixtures</div>
          <h1 className="text-[30px] font-semibold tracking-[-0.045em] text-ink sm:text-[36px]">Models</h1>
          <p className="mt-1.5 max-w-xl text-[13px] leading-5 text-muted">Explore model cards and catalogue controls. All entries and prices on this page are fictional demo data.</p>
        </div>
        <a href="#catalogue" className="inline-flex items-center gap-1.5 self-start text-[12px] font-medium text-[#317451] hover:text-[#1b5638] sm:self-auto">Browse the catalogue <ArrowRight size={14} /></a>
      </div>

      <div className="mb-5 rounded-lg border border-[#e5e9e6] bg-white px-3.5 py-2.5 text-[11px] leading-5 text-muted">
        <span className="mr-2 inline-flex rounded bg-[#f0f2f1] px-1.5 py-0.5 text-[9px] font-bold uppercase tracking-wide text-[#58635d]">Mock data</span>
        UI-only fixtures for layout review. No OpenRouter API contract, endpoint, or response schema is implemented.
      </div>

      <div className="mb-5"><ModalityTabs active={modality} onChange={setModality} /></div>
      <ModelsToolbar query={query} onQueryChange={setQuery} view={view} onViewChange={setView} />

      <div id="catalogue" className="mt-5 flex flex-col gap-5 lg:flex-row lg:items-start">
        <ModelFilters selected={selectedFilters} onToggle={toggleFilter} />
        <section className="min-w-0 flex-1" aria-label="Model results">
          <div className="mb-3 flex items-center justify-between px-0.5">
            <p className="text-[11px] text-muted"><span className="font-semibold text-ink">{visibleModels.length}</span> demo {visibleModels.length === 1 ? 'model' : 'models'}</p>
            <span className="text-[10px] text-[#929b97]">Sample catalogue</span>
          </div>
          {visibleModels.length > 0 ? (
            view === 'list'
              ? <div className="flex flex-col gap-2.5">{visibleModels.map((model) => <ModelCard key={model.id} model={model} view="list" />)}</div>
              : <ModelsTable models={visibleModels} />
          ) : (
            <div className="grid min-h-56 place-items-center rounded-xl border border-dashed border-[#d8dfda] bg-white px-6 text-center">
              <div><p className="text-sm font-semibold text-ink">No demo models match</p><p className="mt-1 text-xs text-muted">Try another search or clear one of the selected filters.</p></div>
            </div>
          )}
        </section>
      </div>
      <footer className="mt-12 border-t border-line pt-5 text-[10px] text-[#89928e]">OpenRouter UI foundation · local frontend prototype</footer>
    </main>
  )
}
