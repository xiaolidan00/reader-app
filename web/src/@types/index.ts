export type BookType = {
  id: string;
  name: string;
  chapter: number;
  index: number;
  total: number;
  num: number;
  updateTime: number;
  importTime: number;
  readTime: number;
  path: string;
  group: string;
  disable?: boolean;
  size: number;
  regexType?: number;
  regex?: string;
};
export type ChapterType = {
  //   index: number;
  title: string;
  content: string[];
};
export type SearchItemType = {
  content: string;
  chapter: number;
  index: number;
  start: number;
};
