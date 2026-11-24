import { ChevronDown, SlidersHorizontal } from 'lucide-react'

const sections = [
  { title: 'Capabilities', options: ['Tools', 'Reasoning', 'Structured outputs'] },
  { title: 'Pricing', options: ['Free models', 'Discounted'] },
  { title: 'Context length', options: ['32K+', '100K+'] },
  { title: 'Providers', options: ['Northstar', 'Aster', 'Orbit', 'Ember', 'Lumen'] },
]

interface Props { selected: string[]; onToggle: (value: string) => void }

export default function ModelFilters({ selected, onToggle }: Props) {
  return (
    <aside className="w-full shrink-0 lg:w-[224px]" aria-label="Model filters">
      <div className="mb-3 flex items-center justify-between px-1">
        <div className="flex items-center gap-2 text-[13px] font-semibold"><SlidersHorizontal size={15} /> Filters</div>
        {selected.length > 0 && <button onClick={() => selected.forEach(onToggle)} className="text-[11px] font-medium text-muted hover:text-ink">Clear</button>}
      </div>
      <div className="rounded-xl border border-line bg-white px-3.5">
        {sections.map((section) => (
          <details key={section.title} open={section.title === 'Capabilities' || section.title === 'Providers'} className="group border-b border-line last:border-0">
            <summary className="flex cursor-pointer list-none items-center justify-between py-3.5 text-[12px] font-semibold text-ink [&::-webkit-details-marker]:hidden">
              {section.title}<ChevronDown size={14} className="text-muted transition group-open:rotate-180" />
            </summary>
            <div className="space-y-2.5 pb-3.5">
              {section.options.map((option) => (
                <label key={option} className="flex cursor-pointer items-center gap-2.5 text-[12px] text-muted hover:text-ink">
                  <input type="checkbox" checked={selected.includes(option)} onChange={() => onToggle(option)} className="h-3.5 w-3.5 rounded border-gray-300 accent-[#4da878]" />{option}
                </label>
              ))}
            </div>
          </details>
        ))}
      </div>
      <p className="mt-3 px-1 text-[10px] leading-4 text-muted">Demo filters are local UI state only. No API calls are configured.</p>
    </aside>
  )
}
