import {defineConfig} from "vite";
import vue from "@vitejs/plugin-vue";

// https://vite.dev/config/
export default defineConfig({
  base: process.env.NODE_ENV == "development" ? "/" : "file:///android_asset/",
  plugins: [vue()],

  build: {
    sourcemap: true,
    // minify: false,
    emptyOutDir: true,
    outDir: "../app/src/main/assets/"
  }
});
