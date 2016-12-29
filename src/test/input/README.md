# md2html.jar

Fork me on GitHub: [district10/md2html: 把文件夹下的 Markdown 文件，转化成 GitHub 风格的 HTML](https://github.com/district10/md2html)。

下载 [Nighty build](md2html.jar)。

## Features 特性

Definition Term | 定义 -<

:   definition description | 描述

    -   1
    -   2
    -   3

    ```cpp
    printf( "hello," " world\n" );
    ```

Definition Term | 定义 +<

:   definition description | 描述

    -   1
    -   2
    -   3

    ```cpp
    printf( "hello," " world\n" );
    ```

Include file:

.md2html.yml

```yml
@include <-=.md2html.yml=
```

formats/index.md

```
@include <-=formats/index.md=
```

## recursive include

@include <-=include/index.md=

## config

-   [conf1](conf1/index.html) 关闭了 CSS `mh-css:`
-   [conf2](conf2/index.html) 关闭了 CSS `mh-js:`

查看本文：

    @include <-=README.md=
