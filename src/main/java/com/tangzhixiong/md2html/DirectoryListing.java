package com.tangzhixiong.md2html;

public class DirectoryListing {
    public static void main(String[] args)  {
        String test = "abaoei.txt";
        System.out.println(
                test.substring(0, test.lastIndexOf("."))
        );
    }
}
