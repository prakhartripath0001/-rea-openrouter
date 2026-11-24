/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{ts,tsx}'],
  theme: {
    extend: {
      colors: {
        ink: '#18211f',
        muted: '#69736f',
        line: '#e7ebe8',
        canvas: '#f7f8f7',
        brand: '#5dbb83',
      },
      boxShadow: {
        soft: '0 8px 32px rgba(24, 33, 31, 0.06)',
      },
    },
  },
  plugins: [],
}
