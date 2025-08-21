export default {
  //打开txt文件
  openTxt() {
    window.Android.openTxt();
  },

  //删除记录或删除文件
  delTxt(delBooks: string[], type: "record" | "file") {
    window.Android.delTxt(delBooks, type);
  },
  //
  getList() {
    window.Android.getList();
  },
  //章节正则表达式
  chapterRegex(id: string, regex: string, regexType: number) {
    window.Android.setChapterRegex(id, regex, regexType);
  },
  //阅读完毕保存进度
  readedTxt(id: string, chapter: number, index: number, total: number) {
    window.Android.readedTxt(id, chapter, index, total);
  },
  //获取当前书内容
  getTxt(id: string) {
    // window.Android.getTxt(id);
  },
  //获取内容
  readTxt(path: string) {
    window.Android.readTxt(path);
  },
  //文件不存在返回列表
  backList() {},
  //关闭前保存
  closedBook(id: string, chapter: number, index: number, total: number) {
    window.Android.closedBook(id, chapter, index, total);
  }
};
