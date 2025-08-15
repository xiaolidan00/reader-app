export const PageNum = 21;
export const LineNum = 24;

export const chapterRegex = [
  { name: '第100章 标题', value: /\s*第\s*[0-9]+\s*章/.source },
  { name: '第一百章 标题', value: /\s*第\s*[一二三四五六七八九十零百千万]+\s*章/.source },
  { name: '第100节 标题', value: /\s*第\s*[0-9]+\s*节/.source },
  { name: '第一百节 标题', value: /\s*第\s*[一二三四五六七八九十零百千万]+\s*节/.source },
  { name: '第100回 标题', value: /\s*第\s*[0-9]+\s*回/.source },
  { name: '第一百回 标题', value: /\s*第\s*[一二三四五六七八九十零百千万]+\s*回/.source },
  { name: '100.标题', value: /\s*[0-9]+\./.source },
  { name: '一零零.标题', value: /\s*[一二三四五六七八九十零百千万]+\./.source },
  { name: '100、标题', value: /\s*[0-9]+、/.source },
  { name: '一零零、标题', value: /\s*[一二三四五六七八九十零百千万]+、/.source },
  { name: '100 标题', value: /\s*[0-9]+/.source },
  { name: '一零零 标题', value: /\s*[一二三四五六七八九十零百千万]+/.source },
  { name: '（100）标题', value: /\s*[\(|（][0-9]+[\)|）]/.source },
  { name: '（一零零） 标题', value: /\s*[\(|（][一二三四五六七八九十零百千万]+[\)|）]/.source },
  { name: '没有标点符号的标题', value: /^[^"“”。,，\！？—\-\+=\?\*…_、\:：]+$/.source }
];
