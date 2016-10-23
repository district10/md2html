# md2html

把文件夹下的 Markdown 文件，转化成 GitHub 风格的 HTML。

## 安装 perl、pandoc 和 make

Pandoc 用来转化 markdown 到 html，下载地址：<http://doi-2014.qiniudn.com/pandoc/pandoc-1.17.0.2-windows.msi> (20.4 MB)

Perl 已经包含在 git for windows 里了，不过可能需要手工添加到 %PATH%，到文件夹下找找，看看 CMD 里 `perl -v` 有没有反应。

安装 `make`，下载地址：<https://github.com/whudoc/statics/raw/master/winbin/make.exe> (218 KB)，放到 PATH 里即可

## 一个例子

比如 @judasn 的 [judasn/IntelliJ-IDEA-Tutorial: IntelliJ IDEA 简体中文专题教程](https://github.com/judasn/IntelliJ-IDEA-Tutorial)。
首先下载它的源码，解压后进入文件夹 `IntelliJ-IDEA-Tutorial`。

然后下载本 repo，把 `tools` 文件夹和 `Makefile` 文件拷贝到 `IntelliJ-IDEA-Tutorial` 文件夹，
然后在命令行下运行 `make`。

即有 `publish` 文件夹。里面是生成的 HTML。
打开里面的 index.html：<http://whudoc.qiniudn.com/2016/IntelliJ-IDEA-Tutorial> （我同步到了网盘）。

## Credits

-   Pandoc: <https://github.com/jgm/pandoc>
-   Github Markdown CSS: <https://github.com/sindresorhus/github-markdown-css>
-   jQuery
-   lazyload.js
