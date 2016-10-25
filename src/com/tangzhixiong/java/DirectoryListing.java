package com.tangzhixiong.java;

import com.sun.jmx.remote.internal.ArrayQueue;
// TODO: 生成目录结构。共前端使用。

import java.io.*;
import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Date;
import java.util.Vector;

public class DirectoryListing {
    public static void main(String[] args) throws Exception {
        File dotfile = new File("README.md");
        if (dotfile.exists() && dotfile.isFile()) {
            PrintStream ps = new PrintStream(new FileOutputStream("Engineer.txt", true));
            BufferedReader reader=new BufferedReader(new InputStreamReader(new FileInputStream(dotfile)));
            String line;
            while ((line = reader.readLine()) != null) {
                ps.print(line);
                ps.print("\n");
            }
            ps.print("shi.....................\n");
            ps.close();
        }
        /*
        Process proc = Runtime.getRuntime().exec("pandoc");
        OutputStream os = proc.getOutputStream();
        PrintStream ps = new PrintStream(os);
        ps.print("good bad");
        ps.print("nice");
        ps.close();
        os.close();
        BufferedReader reader=new BufferedReader(new InputStreamReader(proc.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println("line: "+line);
        }
        */
    }
    public static void test() throws Exception {
        Process proc = Runtime.getRuntime().exec("clip");
        InputStream is = proc.getInputStream();
        byte[] buf = new byte[1024];
        int nread;
        while ((nread = is.read(buf)) > 0) {
            System.out.println(new String(buf, 0, nread));
        }
    }
    public static String listing() throws Exception {
        Vector<String> files = Bundle.getFiles();
        StringBuilder sb = new StringBuilder();
        // "files:\n"
        // "  - path/to/file/1\n"
        // "  - path/to/file/2\n"
        sb.append("files:\n");
        for (String f: files) {
            sb.append("  - ");
            sb.append(f);
            sb.append("\n");
        }
        return sb.toString();
    }
}
