import {createRouter, createWebHistory} from "vue-router";
import BookPage from "./components/Book.vue";
import ListPage from "./components/List.vue";
export const router = createRouter({
  history: createWebHistory(),
  routes: [
    {path: "/", component: ListPage},
    {path: "/book", component: BookPage}
  ]
});
