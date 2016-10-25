# md2html

把文件夹下的 Markdown 文件，转化成 GitHub 风格的 HTML。

## 1. 脚本版

（推荐使用 Java 版的。）

### 安装 perl、pandoc 和 make

Pandoc 用来转化 markdown 到 html，下载地址：

-   <http://doi-2014.qiniudn.com/pandoc/pandoc-1.17.0.2-windows.msi> (20.4 MB)
-   <https://github.com/jgm/pandoc/releases/download/1.17/pandoc-1.17-1-amd64.deb> (19.9 MB)，安装 `sudo dpkg -i pandoc*.deb`
-   <https://github.com/jgm/pandoc/releases/download/1.17.2/pandoc-1.17.2-osx.pkg> (35.9 MB)

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

Java 版跨平台，只需要安装 Pandoc。而且会监控文件夹，在修改后会自动更新 HTML。

如果你安装了浏览器自动更新的插件，比如 [Auto Reload :: Firefox 附加组件](https://addons.mozilla.org/zh-CN/firefox/addon/auto-reload/?src=api)，
还可以自动刷新。这样，把浏览器和编辑器对半放，然后运行 jar 程序，就可以实时预览了~

下载本 repo 和 [md2html.jar](https://github.com/district10/md2html/releases)，
然后把 jar 文件放到根目录，然后运行 `java -jar md2html.jar`（双击的话，就看不到 log，也不能用 Control-C 停止它），
就有 `../publish-md2html` 文件夹。打开里面的 README.html 看效果。

这几个文档是一个示例：

-   [part1](doc/part1.md)
-   [part2](doc/part2.md)
-   part3
    -   [part31](doc/part3/part31.md)
    -   [part32](doc/part3/part32.md)

（生成的 HTML 会自动转化 `.md` 路径为 `.html`。）

## Credits

-   Pandoc: <https://github.com/jgm/pandoc>
-   Github Markdown CSS: <https://github.com/sindresorhus/github-markdown-css>
-   jQuery
-   lazyload.js

## Notes

还可以把 CSS、JavaScript 以及图片打包到 HTML 里，可以把 `--mathjax` 去掉，再加上 `--self-contained`。
