function required(name: keyof ImportMetaEnv, fallback: string): string {
  return import.meta.env[name] || fallback
}

export const env = Object.freeze({
  apiBaseUrl: required('VITE_API_BASE_URL', 'http://localhost:8080/api'),
  mapTileUrl: required('VITE_MAP_TILE_URL', 'https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png'),
  mapAttribution: required('VITE_MAP_ATTRIBUTION', '&copy; OpenStreetMap contributors'),
  useMockData: import.meta.env.VITE_USE_MOCK_DATA === 'true',
})
