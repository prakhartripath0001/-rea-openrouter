import { ArrowUpRight, Menu, Sparkles } from 'lucide-react'

const navItems = ['Models', 'Ranking', 'Activity', 'Apps', 'Docs']

export default function AppHeader() {
  return (
    <header className="sticky top-0 z-30 border-b border-white/10 bg-[#17211f] text-white">
      <div className="mx-auto flex h-[68px] max-w-[1440px] items-center justify-between px-4 sm:px-7">
        <div className="flex items-center gap-8">
          <a href="#models" className="flex items-center gap-2.5" aria-label="OpenRouter UI Foundation home">
            <span className="grid h-8 w-8 place-items-center rounded-xl bg-brand text-[#14231a]"><Sparkles size={17} strokeWidth={2.5} /></span>
            <span className="text-[17px] font-semibold tracking-tight">openrouter<span className="text-brand">.ai</span></span>
          </a>
          <nav className="hidden items-center gap-1 md:flex" aria-label="Primary navigation">
            {navItems.map((item) => (
              <a key={item} href={item === 'Models' ? '#models' : '#'} className={`rounded-lg px-3 py-2 text-[13px] transition ${item === 'Models' ? 'bg-white/10 font-medium text-white' : 'text-white/65 hover:bg-white/5 hover:text-white'}`}>
                {item}
              </a>
            ))}
          </nav>
        </div>
        <div className="flex items-center gap-2">
          <span className="hidden rounded-full border border-brand/30 bg-brand/10 px-3 py-1.5 text-[11px] font-semibold uppercase tracking-[0.12em] text-[#a8ebbd] sm:inline-flex">UI foundation · mock data</span>
          <button className="hidden items-center gap-1.5 rounded-lg bg-brand px-3.5 py-2 text-[13px] font-semibold text-[#14231a] transition hover:bg-[#78d49a] sm:flex">
            Sign in <ArrowUpRight size={14} />
          </button>
          <button className="grid h-9 w-9 place-items-center rounded-lg text-white/80 hover:bg-white/10 md:hidden" aria-label="Open navigation menu"><Menu size={20} /></button>
        </div>
      </div>
    </header>
  )
}
