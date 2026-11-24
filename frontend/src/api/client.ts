const configuredBaseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api/v1'
export const API_BASE_URL = configuredBaseUrl.replace(/\/$/, '')

export async function apiGet<T>(path: string, query?: URLSearchParams): Promise<T> {
  const suffix = query && query.size > 0 ? `?${query.toString()}` : ''
  const response = await fetch(`${API_BASE_URL}${path}${suffix}`, {
    headers: { Accept: 'application/json' },
  })

  if (!response.ok) {
    let message = `Request failed (${response.status})`
    try {
      const errorBody = await response.json() as { message?: string }
      if (errorBody.message) message = errorBody.message
    } catch {
      // Keep the status-based message if the server did not return JSON.
    }
    throw new Error(message)
  }

  return response.json() as Promise<T>
}
