package com.tangzhixiong.java;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * Created by tzx on 2016/10/25.
 */
public class Utility {
    /**
     * @param fullname: "C:\home\dir\and\sub\dir\file.txt"
     * @param dirname:  "C:\home\dir"
     * @return: "../../../"
     */
    public static String resolveToRoot(String fullname, String dirname) {
        String frag = fullname.substring(dirname.length()+1);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < frag.length(); ++i) {
            if (String.valueOf(frag.charAt(i)).equals(File.separator)) {
                sb.append("../");
            }
        }
        return sb.toString();
    }

    // @mkdir -p $(@D)
    public static void mkdirHyphenPDollarAtD(File dest) {
        File atd = dest.getParentFile();
        if (!atd.exists()) {
            atd.mkdirs();
        }
    }

    public static void mappingFile(String input, String output) {
        Runtime runtime = Runtime.getRuntime();
        File inputFile = new File(input);
        File outputFile = new File(output);
        if (!outputFile.exists() || inputFile.lastModified() > outputFile.lastModified()) {
            System.out.printf("[+] %s -> %s\n", input, output);
            Utility.mkdirHyphenPDollarAtD(outputFile);
            try {
                if (input.endsWith(".md")) {
                    String cmd = String.format( "pandoc -S -s --ascii --mathjax " +
                                    "-f markdown+abbreviations+east_asian_line_breaks+emoji " +
                                    "-V rootdir=%s_md2html/ --template %s " +
                                    "%s -o %s",
                            Utility.resolveToRoot(output, Bundle.dstDir), Bundle.htmltemplatePath,
                            input, output);
                    // System.out.printf("Converting from '%s' to '%s' via `%s`\n", input, output, cmd);
                    runtime.exec(cmd);
                } else if (false && input.endsWith(".java")) {
                } else {
                    // System.out.printf("Copying from '%s' to '%s'\n", input, output);
                    Utility.mkdirHyphenPDollarAtD(outputFile);
                    Files.copy(new File(input).toPath(), outputFile.toPath()
                            , StandardCopyOption.REPLACE_EXISTING
                            , StandardCopyOption.COPY_ATTRIBUTES );
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.printf("[ ] %s -> %s\n", input, output);
        }
    }

    public static String getDirName(String path) {
        String dirname = "md2html";
        try {
            File dir = new File(path);
            if (!dir.isDirectory()) {
                dirname = dir.getParentFile().getCanonicalPath();
            } else {
                dirname = dir.getCanonicalPath();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        int pos = dirname.lastIndexOf(File.separator);
        if (0 <= pos && pos < dirname.length()-1) {
            dirname = dirname.substring(pos+1);
        }
        return dirname;
    }
}
