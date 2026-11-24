import { List, Search, Table2, X } from 'lucide-react'

interface Props {
  query: string
  onQueryChange: (value: string) => void
  view: 'list' | 'table'
  onViewChange: (value: 'list' | 'table') => void
}

export default function ModelsToolbar({ query, onQueryChange, view, onViewChange }: Props) {
  return (
    <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
      <label className="flex h-10 min-w-0 flex-1 items-center gap-2.5 rounded-lg border border-line bg-white px-3 text-muted transition focus-within:border-[#9bc9aa] focus-within:ring-2 focus-within:ring-[#dff1e5] sm:max-w-[460px]">
        <Search size={16} />
        <input value={query} onChange={(event) => onQueryChange(event.target.value)} placeholder="Search demo models..." className="w-full bg-transparent text-[13px] text-ink outline-none placeholder:text-[#929b97]" aria-label="Search demo models" />
        {query && <button onClick={() => onQueryChange('')} aria-label="Clear search" className="text-muted hover:text-ink"><X size={15} /></button>}
        {!query && <kbd className="hidden rounded border border-line px-1.5 py-0.5 text-[10px] text-[#8b9490] sm:inline">⌘ K</kbd>}
      </label>
      <div className="flex items-center justify-between gap-3 sm:justify-end">
        <span className="text-[11px] text-muted">Sort: <span className="font-medium text-ink">Featured</span></span>
        <div className="flex rounded-lg border border-line bg-white p-0.5" aria-label="Display style">
          <button aria-label="List view" aria-pressed={view === 'list'} onClick={() => onViewChange('list')} className={`grid h-7 w-8 place-items-center rounded-md transition ${view === 'list' ? 'bg-[#eef2ef] text-ink' : 'text-muted hover:text-ink'}`}><List size={16} /></button>
          <button aria-label="Table view" aria-pressed={view === 'table'} onClick={() => onViewChange('table')} className={`grid h-7 w-8 place-items-center rounded-md transition ${view === 'table' ? 'bg-[#eef2ef] text-ink' : 'text-muted hover:text-ink'}`}><Table2 size={15} /></button>
        </div>
      </div>
    </div>
  )
}
