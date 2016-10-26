# md2html

把文件夹下的 Markdown 文件，转化成 GitHub 风格的 HTML。

### 应用举例

比如 @judasn 的 [judasn/IntelliJ-IDEA-Tutorial: IntelliJ IDEA 简体中文专题教程](https://github.com/judasn/IntelliJ-IDEA-Tutorial)。
首先下载它的源码，解压后进入文件夹 `IntelliJ-IDEA-Tutorial`。

下载 [`md2html.jar`](https://github.com/district10/md2html/releases)，然后把 `jar` 文件放到根目录，
然后运行 `java -jar md2html.jar`（双击的话，就看不到 log，也不能用 Control-C 停止它），就有
`../md2html-publish` 文件夹。打开里面的 `README.html` 看效果。

我把生成的网页上传到了网盘，在线查看：<http://whudoc.qiniudn.com/2016/IntelliJ-IDEA-Tutorial>。

注：生成的 HTML 会自动转化 `.md` 路径为 `.html`。

## 安装使用

`jar` 文件的运行，需要 JRE（Java Runtime）的支持，可以在这里下载：[Download Free Java Software](https://java.com/en/download/)。

Pandoc 是内部用来转化 Markdown 的工具，下载地址：

-   Windows：<http://doi-2014.qiniudn.com/pandoc/pandoc-1.17.0.2-windows.msi> (20.4 MB)
-   Linux：<https://github.com/jgm/pandoc/releases/download/1.17/pandoc-1.17-1-amd64.deb> (19.9 MB)，安装指令：`sudo dpkg -i pandoc*.deb`
-   Mac OSX：<https://github.com/jgm/pandoc/releases/download/1.17.2/pandoc-1.17.2-osx.pkg> (35.9 MB)

### `md2html.jar` 使用

除了 `java -jar md2html.jar` 这种简单的使用，还可以指定一些参数，比如输入目录和输出目录，
下面是使用方法：

```bash
# 当前文件夹拷贝到 ../<当前文件夹名称>-publish，并转化其中的 .md 文件
$ java -jar md2html.jar

# 指定输入、输出文件夹
$ java -jar md2html.jar -i source_dir -o publish_dir
$ java -jar md2html.jar -input source_dir -output publish_dir
```

如果安装了浏览器自动更新的插件，比如 [Auto Reload :: Firefox 附加组件](https://addons.mozilla.org/zh-CN/firefox/addon/auto-reload/?src=api)，
还可以自动刷新。这样，把浏览器和编辑器对半放，然后运行 `jar` 程序，就可以实时预览了~

### `md2html.jar` 的打包:

#### 方法 1，利用 IntelliJ IDEA 的 Artifact

用 IntelliJ IDEA 打开这个工程应该就可以生成 `md2html.jar`。

#### 方法 2，利用 Makefile 打包

如果没有 `make`，安装之，下载地址：<https://github.com/whudoc/statics/raw/master/winbin/make.exe> (218 KB)，放到 %PATH%（比如 `C:\Windows\System32`）里即可。

如果没有 `zip`，安装之，下载地址：<https://github.com/whudoc/statics/raw/master/winbin/zip.exe> (298 KB)，放到 %PATH% 里即可。

最后，`make md2html.jar`，当前目录即有文件 `md2html.jar`。

（在 Windows 和 Linux 上测试通过，均可以正确生成 `jar` 包。）

## Credits

-   [Pandoc](https://github.com/jgm/pandoc)（Markdown 转化）
-   [Github Markdown CSS](https://github.com/sindresorhus/github-markdown-css)（Github 风格的 CSS）
-   jQuery，lazyload.js（图片延迟加载）
-   [我的笔记本](http://tangzx.qiniudn.com/notes/)（使用类似方法生成的 HTML，我复用了自己笔记的 js，css 以及 HTML 模板）
