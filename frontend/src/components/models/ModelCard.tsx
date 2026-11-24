import { ArrowUpRight, AudioLines, Copy, Image, MessageSquareText, Video } from 'lucide-react'
import type { ModelCardModel } from '../../types/model'

const modalityIcons = { Text: MessageSquareText, Image, Audio: AudioLines, Video, Embeddings: MessageSquareText }

interface Props { model: ModelCardModel; view: 'cards' | 'list' }

export default function ModelCard({ model, view }: Props) {
  const list = view === 'list'
  return (
    <article className={`group rounded-xl border border-line bg-white transition hover:border-[#cbd8cf] hover:shadow-soft ${list ? 'flex flex-col gap-4 p-4 sm:flex-row sm:items-center' : 'flex min-h-[230px] flex-col p-4'}`}>
      <div className={`flex min-w-0 items-start gap-3 ${list ? 'sm:flex-1' : ''}`}>
        <div className={`grid h-10 w-10 shrink-0 place-items-center rounded-xl bg-gradient-to-br ${model.accent}`}>
          <span className="text-sm font-bold">{model.author.slice(0, 1)}</span>
        </div>
        <div className="min-w-0 flex-1">
          <div className="flex flex-wrap items-center gap-x-2 gap-y-1">
            <h2 className="truncate text-[14px] font-semibold tracking-[-0.02em] text-ink">{model.name}</h2>
            <span className="text-[11px] text-muted">by {model.author}</span>
            <span className="rounded-full bg-[#f0f3f1] px-1.5 py-0.5 text-[9px] font-semibold uppercase tracking-wide text-[#6b7770]">Mock</span>
          </div>
          <p className={`mt-1.5 text-[11px] leading-[1.55] text-muted ${list ? 'max-w-2xl' : 'line-clamp-2 min-h-[34px]'}`}>{model.description}</p>
        </div>
      </div>
      <div className={`flex flex-wrap gap-1.5 ${list ? 'sm:w-[150px]' : 'mt-3'}`}>
        {model.modalities.map((modality) => {
          const Icon = modalityIcons[modality]
          return <span key={modality} className="inline-flex items-center gap-1 rounded-md border border-line px-1.5 py-1 text-[9px] font-medium text-muted"><Icon size={11} />{modality}</span>
        })}
        {model.tags.map((tag) => <span key={tag} className="rounded-md bg-[#f5f7f5] px-1.5 py-1 text-[9px] font-medium text-[#737d78]">{tag}</span>)}
      </div>
      <div className={`grid grid-cols-3 gap-2 border-t border-line pt-3 ${list ? 'sm:w-[300px] sm:border-0 sm:pt-0' : 'mt-auto mt-4'}`}>
        <Metric label="Context" value={model.context} />
        <Metric label="Prompt · mock" value={model.promptPrice} />
        <Metric label="Output · mock" value={model.outputPrice} />
      </div>
      <div className={`flex items-center gap-1 ${list ? 'self-end sm:self-auto' : 'mt-3 justify-end'}`}>
        <span className="mr-auto text-[9px] text-[#929b97]">Model key: {model.modelKey}</span>
        <button aria-label={`Copy ${model.id}`} className="grid h-7 w-7 place-items-center rounded-md text-muted opacity-70 transition hover:bg-[#f2f5f2] hover:text-ink"><Copy size={13} /></button>
        <button aria-label={`View ${model.name} details`} className="grid h-7 w-7 place-items-center rounded-md text-muted opacity-70 transition hover:bg-[#f2f5f2] hover:text-ink"><ArrowUpRight size={14} /></button>
      </div>
    </article>
  )
}

function Metric({ label, value }: { label: string; value: string }) {
  return <div className="min-w-0"><div className="truncate text-[9px] text-[#929b97]">{label}</div><div className="mt-0.5 truncate text-[11px] font-semibold text-ink">{value}</div></div>
}
