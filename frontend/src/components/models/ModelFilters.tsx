import { ChevronDown, SlidersHorizontal } from 'lucide-react'
import type { ReactNode } from 'react'
import type { ProviderOption } from '../../api/types'

interface Props {
  providers: ProviderOption[]
  tags: string[]
  selectedProviders: string[]
  selectedTags: string[]
  minContextLength?: number
  maxContextLength?: number | null
  freeOnly: boolean
  discountedOnly: boolean
  onToggleProvider: (slug: string) => void
  onToggleTag: (tag: string) => void
  onContextLengthChange: (value: number | undefined) => void
  onFreeOnlyChange: (value: boolean) => void
  onDiscountedOnlyChange: (value: boolean) => void
  onClear: () => void
}

export default function ModelFilters({
  providers, tags, selectedProviders, selectedTags, minContextLength, maxContextLength, freeOnly, discountedOnly,
  onToggleProvider, onToggleTag, onContextLengthChange, onFreeOnlyChange, onDiscountedOnlyChange, onClear,
}: Props) {
  const hasFilters = selectedProviders.length > 0 || selectedTags.length > 0 || minContextLength !== undefined || freeOnly || discountedOnly
  const contextOptions = [32_000, 100_000].filter((value) => maxContextLength == null || value <= maxContextLength)

  return (
    <aside className="w-full shrink-0 lg:w-[224px]" aria-label="Model filters">
      <div className="mb-3 flex items-center justify-between px-1">
        <div className="flex items-center gap-2 text-[13px] font-semibold"><SlidersHorizontal size={15} /> Filters</div>
        {hasFilters && <button onClick={onClear} className="text-[11px] font-medium text-muted hover:text-ink">Clear</button>}
      </div>
      <div className="rounded-xl border border-line bg-white px-3.5">
        <FilterGroup title="Capabilities" open>
          {tags.map((tag) => <Checkbox key={tag} label={tag} checked={selectedTags.includes(tag)} onChange={() => onToggleTag(tag)} />)}
          {tags.length === 0 && <p className="text-[11px] text-muted">Loading options…</p>}
        </FilterGroup>
        <FilterGroup title="Pricing">
          <Checkbox label="Free models" checked={freeOnly} onChange={() => onFreeOnlyChange(!freeOnly)} />
          <Checkbox label="Discounted" checked={discountedOnly} onChange={() => onDiscountedOnlyChange(!discountedOnly)} />
        </FilterGroup>
        <FilterGroup title="Context length">
          {contextOptions.map((value) => <Checkbox key={value} label={`${value / 1000}K+`} checked={minContextLength === value} onChange={() => onContextLengthChange(minContextLength === value ? undefined : value)} />)}
        </FilterGroup>
        <FilterGroup title="Providers" open>
          {providers.map((provider) => <Checkbox key={provider.id} label={`${provider.name} (${provider.modelCount})`} checked={selectedProviders.includes(provider.slug)} onChange={() => onToggleProvider(provider.slug)} />)}
          {providers.length === 0 && <p className="text-[11px] text-muted">Loading providers…</p>}
        </FilterGroup>
      </div>
      <p className="mt-3 px-1 text-[10px] leading-4 text-muted">Filters are applied to the local demo catalogue.</p>
    </aside>
  )
}

function FilterGroup({ title, open = false, children }: { title: string; open?: boolean; children: ReactNode }) {
  return (
    <details open={open} className="group border-b border-line last:border-0">
      <summary className="flex cursor-pointer list-none items-center justify-between py-3.5 text-[12px] font-semibold text-ink [&::-webkit-details-marker]:hidden">
        {title}<ChevronDown size={14} className="text-muted transition group-open:rotate-180" />
      </summary>
      <div className="space-y-2.5 pb-3.5">{children}</div>
    </details>
  )
}

function Checkbox({ label, checked, onChange }: { label: string; checked: boolean; onChange: () => void }) {
  return (
    <label className="flex cursor-pointer items-center gap-2.5 text-[12px] text-muted hover:text-ink">
      <input type="checkbox" checked={checked} onChange={onChange} className="h-3.5 w-3.5 rounded border-gray-300 accent-[#4da878]" />{label}
    </label>
  )
}
