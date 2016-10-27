### doc 3

-   doc3.md 引入 doc4.md：（有环，错误，不会引入）

    @include <-=doc4.md=

-   doc3.md 又引入 code2.h

    ```cpp
    @include <-=code2.h=
    ```
