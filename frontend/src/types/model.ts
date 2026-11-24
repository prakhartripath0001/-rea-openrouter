export type Modality = 'Text' | 'Image' | 'Audio' | 'Video' | 'Embeddings'

export interface ModelCardModel {
  id: string
  modelKey: string
  name: string
  author: string
  description: string
  context: string
  promptPrice: string
  outputPrice: string
  modalities: Modality[]
  tags: string[]
  accent: string
}
