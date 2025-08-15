import type {BookType, ChapterType} from "./@types";

import {ref} from "vue";

export const selectBook = ref("");
export const dataList = ref<Array<BookType>>([]);
export const bookItem = ref<BookType>();
export const chapterList = ref<ChapterType[]>([]);
export const loading = ref<boolean>(false);
export const listSearchKey = ref<string>("");

export const searchHighlight = new Highlight();
CSS.highlights.set(`search-highlight`, searchHighlight);
export function setHighlight(start: number, textNode: Node, searchLen: number) {
  const range = new Range();

  range.setStart(textNode, start);
  range.setEnd(textNode, start + searchLen);
  searchHighlight.add(range);
}

export const currentChapter = ref<number>(0);
export const currentIndex = ref<number>(0);
