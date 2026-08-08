/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{vue,js,ts,jsx,tsx}'],
  theme: {
    extend: {
      colors: {
        brand: '#1262c4',
        ink: '#1f2937',
        mist: '#f5f8fc',
        line: '#d7e0ea',
      },
      boxShadow: {
        panel: '0 1px 2px rgba(15, 23, 42, 0.06), 0 10px 30px rgba(15, 23, 42, 0.04)',
      },
    },
  },
  plugins: [],
}
