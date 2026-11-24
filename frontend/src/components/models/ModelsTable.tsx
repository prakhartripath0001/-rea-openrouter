import type { ModelCardModel } from '../../types/model'

interface Props { models: ModelCardModel[] }

export default function ModelsTable({ models }: Props) {
  return (
    <div className="overflow-x-auto rounded-xl border border-line bg-white">
      <table className="w-full min-w-[720px] border-collapse text-left">
        <thead className="bg-[#fafbfa] text-[10px] uppercase tracking-[0.08em] text-muted">
          <tr>
            <th className="px-4 py-3 font-semibold">Model</th>
            <th className="px-4 py-3 font-semibold">Modalities</th>
            <th className="px-4 py-3 font-semibold">Context</th>
            <th className="px-4 py-3 font-semibold">Prompt · mock</th>
            <th className="px-4 py-3 font-semibold">Output · mock</th>
          </tr>
        </thead>
        <tbody className="divide-y divide-line">
          {models.map((model) => (
            <tr key={model.id} className="align-top transition hover:bg-[#fbfcfb]">
              <td className="max-w-[380px] px-4 py-3.5">
                <div className="flex items-start gap-2.5">
                  <span className={`grid h-8 w-8 shrink-0 place-items-center rounded-lg bg-gradient-to-br ${model.accent} text-xs font-bold`}>{model.author.slice(0, 1)}</span>
                  <div className="min-w-0">
                    <div className="flex flex-wrap items-center gap-1.5 text-[12px] font-semibold text-ink">{model.author}: {model.name}<span className="rounded-full bg-[#f0f3f1] px-1.5 py-0.5 text-[8px] uppercase tracking-wide text-muted">Mock</span></div>
                    <p className="mt-1 line-clamp-2 text-[10px] leading-4 text-muted">{model.description}</p>
                    <code className="mt-1 block truncate text-[9px] text-[#929b97]">{model.modelKey}</code>
                  </div>
                </div>
              </td>
              <td className="px-4 py-4 text-[10px] text-muted">{model.modalities.join(', ')}</td>
              <td className="px-4 py-4 text-[11px] font-medium text-ink">{model.context}</td>
              <td className="px-4 py-4 text-[11px] font-medium text-ink">{model.promptPrice}</td>
              <td className="px-4 py-4 text-[11px] font-medium text-ink">{model.outputPrice}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
