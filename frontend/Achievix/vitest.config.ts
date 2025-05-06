import { fileURLToPath } from 'node:url'
import { mergeConfig, defineConfig, configDefaults } from 'vitest/config'
import viteConfig from './vite.config'
import vue from "@vitejs/plugin-vue";

export default mergeConfig(
  viteConfig,
  defineConfig({
    plugins: [],
    test: {
      environment: 'jsdom',
      globals: true,
      setupFiles: [
        fileURLToPath(new URL('./src/tests/unit/setup/api.mock.ts', import.meta.url)),
        fileURLToPath(new URL('./src/tests/router-mock-setup.ts', import.meta.url)),
      ],
      coverage: {
        provider: 'v8',
        reporter: ['text', 'json', 'html'],
      },
      // exclude: [...configDefaults.exclude, 'e2e/**'],
      // root: fileURLToPath(new URL('./', import.meta.url)),
    },
  }),
)
