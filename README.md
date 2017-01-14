# md2html

[![Build Status](https://travis-ci.org/district10/md2html.svg?branch=master)](https://travis-ci.org/district10/md2html)

把文件夹下的 Markdown 文件，转化成 GitHub 风格的 HTML。（rst、org-mode also supported）

（代码高亮用了个人比较喜欢的 Pygments 风格。）

## 安装使用

`jar` 文件的运行，需要 JRE（Java Runtime）的支持，可以在这里下载：[Download Free Java Software](https://java.com/en/download/)(Linux: `sudo apt-get install openjdk-8-jdk`)。

Pandoc 是内部用来转化 Markdown 的工具，下载地址（请使用 1.17 以上的版本）：

-   Windows：<http://doi-2014.qiniudn.com/pandoc/pandoc-1.17.0.2-windows.msi> (20.4 MB)
-   Linux：<https://github.com/jgm/pandoc/releases/download/1.17/pandoc-1.17-1-amd64.deb> (19.9 MB)，安装指令：`sudo dpkg -i pandoc*.deb`
-   Mac OSX：<https://github.com/jgm/pandoc/releases/download/1.17.2/pandoc-1.17.2-osx.pkg> (35.9 MB)

### 应用举例

比如 @judasn 的 [judasn/IntelliJ-IDEA-Tutorial: IntelliJ IDEA 简体中文专题教程](https://github.com/judasn/IntelliJ-IDEA-Tutorial)。
首先下载它的源码，解压后进入文件夹 `IntelliJ-IDEA-Tutorial`。

到 [Releases](https://github.com/district10/md2html/releases) 下载 md2html.jar（或者下载 [Nighty build](http://whudoc.qiniudn.com/java/md2html/md2html.jar)），然后把 `jar` 文件放到根目录，
然后运行 `java -jar md2html.jar`（或者双击它），就有
`../IntelliJ-IDEA-Tutorial-master-publish` 文件夹。打开里面的 `README.html`（或者 `index.html`） 看效果。

我把生成的网页上传到了网盘，在线查看：<http://whudoc.qiniudn.com/2016/IntelliJ-IDEA-Tutorial>。

注：Markdown 中的链接中以 `.md` 结尾的会自动转为以 `.html` 结尾。

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

### `md2html.jar` 的打包

```
make                # 调用 mvn package
```

## 更多参数的配置

可以输入 `java -jar md2html.jar -help` 查看帮助：

```
Usage:
    $ java -jar md2html.jar

Options:
    -i, -input <SOURCE DIRECTORY>
           specify root of markdown files
    -o, -output <DESTINATION DIRECTORY>
           specify root of output files
    -w, -watch
           watch mode
    -s, -silent
           silent mode
    -v, -verbose
           verbose mode
    -e, -expand
           expand markdown files
    -f, -fold
           fold markdown contents

More Usage Examples
   1. current dir to ../publish:
       $ java -jar md2html.jar -i . -o ../publish
   2. turn on watch, expand markdown
       $ java -jar md2html.jar -we
```

现在默认不 watch 了。

That's it.

## Credits

-   [Pandoc](https://github.com/jgm/pandoc)（Markdown 转化）
-   [Github Markdown CSS](https://github.com/sindresorhus/github-markdown-css)（Github 风格的 CSS）
-   jQuery，lazyload.js（图片延迟加载）
-   [我的笔记本](http://tangzx.qiniudn.com/notes/)（使用类似方法生成的 HTML，我复用了自己笔记的 js，css 以及 HTML 模板）
-   clipboard.js, etc.

---

These are for myself:

## Tips

Click on `<code>...</code>` to copy it to your clipboard.

位置标记：

```html
<head>
    $mh-head-head                               (raw html fragment)         alias: raw-head

             +----- $mh-css-before-cdn          (remote css url)
            /
           +------- $mh-css-before              (local css path)
          /
    CSS -+--------- $mh-css                     (local css path)
          \
           `+------ $mh-css-after-cdn           (remote css url)            alias: remote-css
             \
              `+--- $mh-css-after               (local css path)            alias: local-css
                \
                 `- $mh-css-raw                 (raw css fragment)          alias: raw-css

    $mh-head-tail
</head>

<body>
    $mh-nav                                     (Use 'mh-nav: false to dismiss')
              +---- $body-before                (raw html fragment)
             /
    $body --+
             `----- $body-after                 (raw html fragment)

              +---- $mh-info                    (Use 'mh-info: false' to dismiss)
             /
            +------ $mh-js-before-cdn           (remote js url)
           /
          +-------- $mh-js-before               (local js file path)
         /
    JS -+---------- $mh-js                      (local js file path)
         \
          `+------- $mh-js-after-cdn            (remote js url)             alias: remote-js
            \
             `----- $mh-js-after                (local js file path)        alias: local-js
               \
                `-- $mh-js-raw                  (raw js fragment)           alias: raw-js

    $mh-body-tail                               (raw html fragment)
</body>
```

in your `.md2html.yml`

```
mh-css:
  - a.js
  - b.js
```

## TODO

更多的 options：

-   文件映射
    -   noDefaultFileMappings
    -   extraFileMappings
-   URL 映射
    -   noDefaultUrlMappings
    -   extraUrlMappings
-   CSS，JS
    -   noDefaultCSS
    -   noDefaultJS
    -   extraCSSs
    -   extraJSs
-   Debug 模式
    -   debug
-   GitHub Pages 兼容模式
    -   githubPagesCompatiable
-   分词和搜索
    -   [yanyiwu/cppjieba: "结巴"中文分词的C++版本](https://github.com/yanyiwu/cppjieba)
    -   [huaban/jieba-analysis: 结巴分词(java版)](https://github.com/huaban/jieba-analysis)
    -   [yanyiwu/simhash: 中文文档simhash值计算](https://github.com/yanyiwu/simhash)
    -   参考：[jQuery-based Local Search Engine for Hexo | HaHack](http://hahack.com/codes/local-search-engine-for-hexo/)
    -   自动 tagging 功能

最后，最重要的 TODO：不要 dive into this project。Try WebSock intead！学 Java，要全面不能囿于一室。
