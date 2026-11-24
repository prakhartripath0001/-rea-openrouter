# OpenRouter Models Catalogue: Reverse-Engineering Notes

**Investigation date:** 2026-11-24  
**Target:** `https://openrouter.ai/models`  
**Browser:** Chrome 158.0.8127.38 via CDP `http://127.0.0.1:9222`  
**Scope:** Public pages and passive REA inspection. No account data, purchases, or website changes.

## Executive summary

REA inspected the Models catalogue and a currently open Claude Haiku 5.5 detail page. The catalogue exposes search, modality tabs, a large set of expandable filters, model cards, and list/table display controls. Cards visibly contain model/provider labels, descriptions, prices, context values, modalities, and usage metrics. The detail page adds provider-specific pricing, cache-related values, reasoning, performance, and availability displays.

Static analysis positively fingerprints **Next.js** (high confidence) and **React** (medium confidence) in captured script bytes. It found route and endpoint candidates, but no model-listing or pricing API request was captured. REA’s live page inspection returned no attach-window requests and explicitly reported that prior activity was unavailable. The action-driven browser capture provider failed, so I could not observe search, filter, sort, pagination, compare, or card-click requests. No endpoint, method, response schema, or debounce behavior is asserted without that evidence.

## Evidence index

| Evidence ID | REA operation | Observed subject |
|---|---|---|
| `ev_778784edb5eeea22295832b96cfb585555a94113c7c64ae240a1341d451ee786` | `list_browser_targets` | Models tab at `https://openrouter.ai/models?utm_source=chatgpt.com` |
| `ev_1689d59c786d9ce8345fcdcd036403d6693a48790b859436f0f70a18f542282e` | `inspect_web_page` | Catalogue DOM, accessibility tree, scripts/resources, attach-window network |
| `ev_c0fa1ecacf5a61fe585582897721630aeb640e86ed0f3ccaa22a3558fed2e684` | `analyze_web_bundle` view | Static bundle analysis of a Models-page capture whose URL included `q=claude` |
| `ev_f36df89c4ed89d7120cbdd8876c4de7ad277af2faf2e2749bf410193715e1873` | `inspect_analysis_view` | Static route inventory page; `/models` candidate and source evidence |
| `ev_315a0f9ca8013de3c25b9ec5f90b4dcd09e12d6454da9e103f556d7f5f1c4c9e` | `analyze_web_bundle` view | Static bundle analysis of the detail-page capture |
| `ev_7fd2c84f102b4557f12748094f40c0cebac373e7b3bbe4db187dbc3dd0c00eb8` | `inspect_web_page` | Claude Haiku 5.5 detail page at `/anthropic/claude-haiku-5.5#providers` |

The Models-page static analysis parent Evidence is `ev_d17c54eb109c5200d0cb68994a75dc9c778b442ef8526b298d6def79d86da545`; detail-page analysis parent Evidence is `ev_ed845cccc9158f4d8619db63349356bca401f6a7a05e941e9edd8a811f096ea6`. These retain the complete analysis records for REA views.

## Frontend architecture and JavaScript

### Confirmed observations

- The catalogue inspection captured 2,545 DOM nodes, 1,944 accessibility nodes, 73 script records, and 72 resources. The DOM included a `MAIN`, one `H1` (“Models”), a filter/search region, and model-list content. These counts describe the inspected page snapshot, not the entire application.
- Captured script/resource URLs include hashed assets under `/_next/static/immutable/chunks/`, and CSS assets under the same prefix. Examples include `1x82q1li5fgr-.js`, `2d5n-p6s1pf_p.js`, `2pzvytralkq21.js`, `turbopack-35u-62ruoxs7i.js`, and CSS chunks `1buzy7ke9s3lr.css`, `3npfmtvedmzwh.css`, and `2mspynbk5d6rg.css`.
- REA’s static bundle analysis fingerprinted Next.js with **high confidence** in multiple captured scripts and React with **medium confidence**. This is stronger than inferring from filenames alone, but remains static evidence from captured code, not runtime instrumentation.
- Source-map URLs were declared for script chunks. The analyzer reported that some requested maps were unavailable or incomplete.

### Module attribution limits

The route inventory associates `/models` strings with 12 source records, including chunks `2cpojuduomq5s.js`, `2pzvytralkq21.js`, `2j42a-xeh2e29.js`, `09szk1ubzvdew.js`, and `208ycsrjkpwzq.js`. The 21 `/models` occurrences were classified as low-confidence navigation targets or route-string constants. They establish route references in source, not that those chunks implement catalogue loading or filtering.

No source-level module names or component ownership could be reliably assigned. Static analysis of the Models-page capture parsed 90 scripts with no parse failures and visited 2,077,315 AST nodes, but overall completeness was `partial`: some per-script JavaScript analysis hit resource/alias limits, source maps were incomplete, and the analyzer explicitly leaves string-built endpoints and server-side behavior unknown. It also warns that endpoint candidates are not a complete call-site inventory and do not prove network execution.

## Catalogue page and model cards

### Observed structure and controls

The accessibility tree reports a “Models” H1 and the following catalogue controls:

- Search textbox: **“Search models...”**
- Input modalities: Text, Image, File, Audio, Video
- Expandable filters: Discounted, Context length, Prompt pricing, Output pricing, Series, Categories, Supported parameters, Distillable, Zero data retention, In-region routing, Model age, Tool calling, Throughput, Inactive models, Providers, and Model authors
- Modality/category tabs with counts in the captured state: All, Text 473, Image 71, Video 33, Speech 42, Decisions 40, Embeddings 45, Rerank 12, and More
- Display controls: List view and Table view
- Per-card actions visible in the tree include Pin model, Copy to clipboard, and usage/video-activity buttons

The filter headings were visible, but most groups were collapsed in the snapshot. Their option lists and effects were not captured.

### Card fields visible in the snapshot

Catalogue card text includes provider/name labels (for example, “Google: Gemini 4 Neon” and “Perplexity: Embed V2 Context 0.6B”), descriptions, context-length values, modality-related information, usage counts, and price strings. Examples in the inspected page include `196K tokens`, `$0.02/M tokens`, `$0.12/M tokens`, `$20/M characters`, and video rates such as `from $0.05/second`. The snapshot shows heterogeneous units; do not assume every card uses per-token pricing.

The page also exposes model identifiers alongside names in accessible text in some cases, but the catalogue inspection did not produce a complete structured card dataset or a confirmed response schema.

## Model detail page example

During inspection, the existing tab was at `https://openrouter.ai/anthropic/claude-haiku-5.5#providers`. This was an observed tab state; I did not attribute its navigation to an REA action.

The page identifies:

- Display name: **Anthropic: Claude Haiku 5.5**
- Model ID text: **`claude-haiku-5.5`**
- Description: Anthropic’s small, fast model; the visible text says it accepts text and image input and has a 1M-token context window.
- Reasoning text: adaptive thinking is on by default, with effort described as a control for depth, latency, and cost.
- Provider section and provider filter; provider routing modes are described in page text as Balanced, Nitro, and Floor.
- Detail sections for pricing and price history, performance, uptime, benchmarks, apps, activity, regional availability, documentation, and FAQs.
- Visible availability value: 99.83% for the displayed three-day view.
- Pricing text includes `$0.10 / $0.50` for the headline input/output display; provider text includes “Google Vertex: $0.11/M tokens input, $0.55/M tokens output” and the same listed values for Amazon Bedrock. The page also displays an average-price series (`$0.07892` and `$0.5834`) and cache-related figures such as `$0.01` in provider rows.

These are values shown in this page snapshot; they may change over time. The inspection did not capture a JSON source payload or fully join every numeric value to its provider/price column. Cache pricing is visible in the page data, but the complete cache-pricing schema is unknown.

## Network evidence and API endpoint catalogue

### Live network capture

Both the catalogue and detail-page `inspect_web_page` records report:

- `network.requests: []`
- `network.websocket_connections: []`
- `prior_activity_available: false`
- `attach_limited` coverage for network requests and metadata

The capture starts when REA attaches. Therefore the empty lists mean **no requests were retained in those attach windows**, not that the pages made no requests during their load. No response body, method, status, query parameters, or response shape was captured for model listing or pricing.

### Static endpoint candidates (not runtime requests)

| Candidate | Static evidence | What is and is not established |
|---|---|---|
| `/api/frontend/v1/public/newsletter/subscribe` | Models-page bundle endpoint view; `call:fetch` in a captured script | A literal fetch-like endpoint string exists in captured code. It is newsletter-related by its path. No HTTP method, request body, response, or runtime occurrence was captured. It is not evidence of the catalogue API. |
| `/api/frontend/statsig-bootstrap` | Models-page `web-bundle` inference; low-confidence endpoint candidate from a `fetch` call site | A static candidate only. Receiver resolution and execution are unverified; no catalogue relationship is established. |
| `/api/v1/videos` | Detail-page bundle endpoint view; `call:fetch` | A fetch-like literal exists in the detail-page capture. No method/body/response or runtime occurrence is established; no model-listing or pricing relationship is established. |
| `https://www.google.com/ccm/geo` | Literal in captured script analysis | Third-party URL-shaped static candidate, not an observed request. The exact-origin page inspection filtered external resources. |

No confirmed model-listing, search, provider, or pricing API endpoint is available from current evidence. No request/response example or JSON response structure can be supplied without inventing it.

## Filter and interaction behavior

| Interaction | Evidence status |
|---|---|
| Search typing / debounce | Search textbox observed. A Models-page bundle capture had URL `...?utm_source=chatgpt.com&q=claude`; this confirms `q=claude` was present in that capture’s URL. It does not prove which action set it, whether it is the search control’s parameter, or debounce timing. |
| Input modalities | Text/Image/File/Audio/Video controls observed. No click or resulting request was captured. |
| Discount, context length, prompt/output pricing, series, categories, supported parameters, distillable, zero data retention, in-region routing, model age, tool calling, throughput, inactive, provider, author filters | Expandable filter controls observed. Option values and behavior remain unobserved. |
| Sorting | No sort control or sort request was established in the captured accessibility/network evidence. |
| Pagination / infinite scroll | Not tested; no page transition or scrolling capture. |
| Filter combinations | Not tested. |
| Model card clicks / detail navigation | A detail page was observed in the existing tab, but no captured action links that navigation to a card click. |
| Compare | A Compare link is visible on the detail page. No compare interaction or request was captured. |

The REA `capture_browser_scenario` provider was attempted in the prior investigation with the discovered existing target. It returned `execution_failure` from provider `rea-playwright-browser-scenario` (“could not complete … and REA did not classify the failure”), with no diagnostics. A reduced retry also failed with the same error. I did not repeat that unchanged provider call. As a result, the requested interaction/network comparisons could not be captured.

## REA network tools checked

- **`inspect_web_page`** accepts `cdp_endpoint` and a returned `target_id`; it attached to the existing tab and returned DOM, accessibility, scripts/resources, and bounded attach-window network metadata. It cannot recover requests from before attachment.
- **`observe_web_session`** accepts the same endpoint and target and can observe an existing tab during a window, but its documented event set is navigation, redirect, lifecycle, and failure events—not full API request/response bodies.
- **`inspect_web_network_capture`** requires a local capture path and format (`har`, `mitmproxy`, `saz`, or `qlog`). It inspects a saved capture and does not attach to a live tab.
- **`analyze_web_bundle`** attaches to a selected page to inspect captured script sources and emit static route/endpoint/vendor candidates. It does not execute application JavaScript or establish that a candidate was requested.

The exact-origin inspection scope was `https://openrouter.ai`. The Models-page record reports third-party exclusions (including 21 metadata items, 15 resources, and 9 scripts with `disallowed_origin`), so the inspection does not provide a complete inventory of external resources. Browser target discovery also returned `attached: null`; REA could not establish debugger attachment state for the page.

## Unknowns and limitations

1. The runtime endpoint and response schema that populate the model catalogue remain unknown.
2. The mechanism, timing, and URL effects of search, including debounce, remain unknown.
3. Filter option values, query encoding, sorting semantics, pagination, and filter combinations were not exercised.
4. Model card fields were observed as rendered UI text, not mapped to a confirmed JSON object.
5. Cache-pricing values appear on the detail page, but their complete provider/model data schema was not recovered.
6. Static bundle analysis was partial; source maps were incomplete and some script analysis hit resource/alias limits.
7. No screenshot was captured. Evidence IDs above are the references for the page and analysis records.
8. The live network evidence is attach-window only and contains no retained requests; it cannot establish absence of load-time traffic.

## Safety and file changes

Only the requested Markdown report was created in the project. Temporary analysis evidence was written under `/private/tmp`, outside the project. The website was inspected passively; no account, purchase, or data-changing action was performed.
