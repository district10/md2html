# md2html

把文件夹下的 Markdown 文件，转化成 GitHub 风格的 HTML。

## 1. 脚本版

### 安装 perl、pandoc 和 make

Pandoc 用来转化 markdown 到 html，下载地址：<http://doi-2014.qiniudn.com/pandoc/pandoc-1.17.0.2-windows.msi> (20.4 MB)

Perl 已经包含在 git for windows 里了，不过可能需要手工添加到 %PATH%，到文件夹下找找，看看 CMD 里 `perl -v` 有没有反应。

安装 `make`，下载地址：<https://github.com/whudoc/statics/raw/master/winbin/make.exe> (218 KB)，放到 PATH 里即可

### 一个例子

比如 @judasn 的 [judasn/IntelliJ-IDEA-Tutorial: IntelliJ IDEA 简体中文专题教程](https://github.com/judasn/IntelliJ-IDEA-Tutorial)。
首先下载它的源码，解压后进入文件夹 `IntelliJ-IDEA-Tutorial`。

然后下载本 repo，把 `tools` 文件夹和 `Makefile` 文件拷贝到 `IntelliJ-IDEA-Tutorial` 文件夹，
然后在命令行下运行 `make`。

即有 `publish` 文件夹。里面是生成的 HTML。
打开里面的 index.html：<http://whudoc.qiniudn.com/2016/IntelliJ-IDEA-Tutorial> （我同步到了网盘）。

## 2. Java 版

按照上面安装 pandoc。

在 Release 页面下载 md2html.jar 文件，运行之：`java -jar md2html.jar`。会把当前
目录的 markdown 文件转化为 html。
输出到 `../publish-md2html`。

这几个文档是一个示例：

-   [part1](doc/part1.md)
-   [part2](doc/part2.md)
-   part3
    -   [part31](doc/part3/part31.md)
    -   [part32](doc/part3/part32.md)

## Credits

-   Pandoc: <https://github.com/jgm/pandoc>
-   Github Markdown CSS: <https://github.com/sindresorhus/github-markdown-css>
-   jQuery
-   lazyload.js

## Notes

还可以把 CSS、JavaScript 以及图片打包到 HTML 里，可以把 `--mathjax` 去掉，再加上 `--self-contained`。
