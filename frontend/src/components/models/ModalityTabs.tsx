import { AudioLines, Image, MessageSquareText, Shapes, Video } from 'lucide-react'

const tabs = [
  { label: 'All models', icon: Shapes },
  { label: 'Text', icon: MessageSquareText },
  { label: 'Image', icon: Image },
  { label: 'Audio', icon: AudioLines },
  { label: 'Video', icon: Video },
]

interface Props { active: string; onChange: (value: string) => void }

export default function ModalityTabs({ active, onChange }: Props) {
  return (
    <div className="scrollbar-none flex gap-1 overflow-x-auto border-b border-line" role="tablist" aria-label="Filter models by modality">
      {tabs.map(({ label, icon: Icon }) => (
        <button key={label} role="tab" aria-selected={active === label} onClick={() => onChange(label)} className={`-mb-px inline-flex shrink-0 items-center gap-2 border-b-2 px-3.5 py-3 text-[13px] font-medium transition ${active === label ? 'border-ink text-ink' : 'border-transparent text-muted hover:text-ink'}`}>
          <Icon size={15} strokeWidth={1.8} />{label}
        </button>
      ))}
    </div>
  )
}
