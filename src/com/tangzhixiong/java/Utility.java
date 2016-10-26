package com.tangzhixiong.java;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

public class Utility {
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

    // like `mkdir -p $(@D)` in makefile
    public static void mkdirHyphenPDollarAtD(File dest) {
        if (dest.isDirectory()) { return; }
        File atd = dest.getParentFile();
        if (!atd.exists()) {
            atd.mkdirs();
        }
    }

    public static void mappingFile(String inputPath, String outputPath) {
        // write log
        //  [+] 'D:\tzx\git\md2html\README.md' -> 'D:\tzx\git\md2html-publish\README.html'
        mappingFile(inputPath, outputPath, true);
    }

    public static void mappingFile(String inputPath, String outputPath, boolean writeLog) {
        Runtime runtime = Runtime.getRuntime();
        File inputFile = new File(inputPath);
        File outputFile = new File(outputPath);
        if (!outputFile.exists() || inputFile.lastModified() > outputFile.lastModified()) {
            mkdirHyphenPDollarAtD(outputFile);
            if (writeLog) {
                System.out.printf("[+] %s -> %s\n", inputPath, outputPath);
            }
            try {
                // expand markdown file
                boolean isMarkdownFile = inputPath.endsWith(".md");
                if (isMarkdownFile) {
                    // src/dir/input.md -> dst/dir/input.md
                    InclusionParams params = new InclusionParams();
                    ArrayList<String> lines = expandLines(inputPath, params, isMarkdownFile);
                    dump(lines, outputFile, isMarkdownFile);

                    String cmd = String.format( "pandoc -S -s --ascii --mathjax " +
                            "-f markdown+abbreviations+east_asian_line_breaks+emoji " +
                            "-V rootdir=%s -V md2htmldir=%s/ --template %s %s -o %s",
                            resolveToRoot(outputPath, Bundle.dstDir),
                            Bundle.resourceDirName, Bundle.htmltemplatePath,
                            outputPath, outputPath.substring(0, outputPath.length()-3)+".html");
                    // dst/dir/input.md -> dst/dir/input.html
                    runtime.exec(cmd);
                } else {
                    Files.copy(inputFile.toPath(), outputFile.toPath()
                            , StandardCopyOption.REPLACE_EXISTING
                            , StandardCopyOption.COPY_ATTRIBUTES);
                }
                generateCodeFragmentIfPossible(outputPath);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            if (writeLog) {
                System.out.printf("[ ] %s -> %s\n", inputPath, outputPath);
            }
        }
    }

    /**
     *
     * @param path: src/to/dir/and/subdir
     * @return: subdir
     */
    public static String getDirName(String path) {
        String dirname = ".";
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
        if (0 <= pos && pos+1 < dirname.length()) {
            dirname = dirname.substring(pos+1);
        }
        return dirname;
    }

    public static void updateCodeFragmentIfNecessary(String inputPath, String label, String outputPath) {
        // input, inputFile, file suffix, highlighting code,
        File inputFile = new File(inputPath);
        File outputFile = new File(outputPath);
        if (!outputFile.exists() || inputFile.lastModified() > outputFile.lastModified()) {
            Process proc = null;
            try {
                proc = Runtime.getRuntime().exec("pandoc -s -S --ascii");
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            try (
                    FileInputStream fis = new FileInputStream(inputFile);
                    FileOutputStream fos = new FileOutputStream(outputFile);
                    PrintStream ps = new PrintStream(proc.getOutputStream());
            ) {
                // pipe in
                byte[] buf = new byte[1024];
                int hasRead = 0;
                ps.print("```"+label+"\n");
                while ((hasRead = fis.read(buf)) > 0) {
                    ps.write(buf, 0, hasRead) ;
                }
                ps.print("```\n");
                ps.close();
                // pipe out
                BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    fos.write(line.getBytes());
                    fos.write("\n".getBytes());
                }
                fos.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /*
    abc, actionscript, ada, agda, apache, asn1, asp, awk, bash, bibtex, boo, c,
    changelog, clojure, cmake, coffee, coldfusion, commonlisp, cpp, cs, css,
    curry, d, diff, djangotemplate, dockerfile, dot, doxygen, doxygenlua, dtd,
    eiffel, elixir, email, erlang, fasm, fortran, fsharp, gcc, glsl,
    gnuassembler, go, hamlet, haskell, haxe, html, idris, ini, isocpp, java,
    javadoc, javascript, json, jsp, julia, kotlin, latex, lex, lilypond,
    literatecurry, literatehaskell, llvm, lua, m4, makefile, mandoc, markdown,
    mathematica, matlab, maxima, mediawiki, metafont, mips, modelines, modula2,
    modula3, monobasic, nasm, noweb, objectivec, objectivecpp, ocaml, octave,
    opencl, pascal, perl, php, pike, postscript, prolog, pure, python, r,
    relaxng, relaxngcompact, rest, rhtml, roff, ruby, rust, scala, scheme, sci,
    sed, sgml, sql, sqlmysql, sqlpostgresql, tcl, tcsh, texinfo, verilog, vhdl,
    xml, xorg, xslt, xul, yacc, yaml, zsh
    */
    public static void generateCodeFragmentIfPossible(String outputPath) {
        int cut = outputPath.lastIndexOf(".");
        if (cut >= 0) {
            switch (outputPath.substring(cut)) {
                // Java
                case ".java":
                    updateCodeFragmentIfNecessary(outputPath, "java", outputPath+".html");
                    break;
                // C++
                case ".h": case ".cpp": case ".cc": case ".hpp":
                    updateCodeFragmentIfNecessary(outputPath, "cpp", outputPath+".html");
                    break;
                // Python
                case ".py":
                    updateCodeFragmentIfNecessary(outputPath, "python", outputPath+".html");
                    break;
                // Perl
                case ".pl":
                    updateCodeFragmentIfNecessary(outputPath, "perl", outputPath+".html");
                    break;
                // JavaScript
                case ".js":
                    updateCodeFragmentIfNecessary(outputPath, "javascript", outputPath+".html");
                    break;
                // JSON
                case ".json":
                    updateCodeFragmentIfNecessary(outputPath, "json", outputPath+".html");
                    break;
                // CSS
                case ".css":
                    updateCodeFragmentIfNecessary(outputPath, "css", outputPath+".html");
                    break;
                // Makefile
                case ".mk":
                    updateCodeFragmentIfNecessary(outputPath, "makefile", outputPath+".html");
                    break;
                // YAML
                case ".yml": case ".yaml":
                    updateCodeFragmentIfNecessary(outputPath, "yml", outputPath+".html");
                    break;
                default:
                    // System.out.println("Maybe you `md2html` should support this kind of file: *"+input.substring(cut));
                    ;
            }
        }
    }

    public static boolean canExpandLine(String line, InclusionParams params) {
        if (!line.endsWith(InclusionParams.right)) {
            return false;
        }
        int i = 0, len = line.length();
        while (i < len && line.charAt(i) == ' ') {
            ++i;
        }
        if (i >= len) { return false; }
        params.leftSpace = i;
        String include = line.substring(i);
        if (!include.startsWith(InclusionParams.flag)) {
            return false;
        }
        i = include.indexOf(InclusionParams.left);
        if (i == len-1) {
            return false;
        }
        params.pad = include.substring(InclusionParams.flaglen, i);
        params.path = include.substring(i+1, include.length()-1);
        if (params.path.isEmpty()) {
            return false;
        }
        System.err.println("path: "+params.path);
        return true;
    }

    public static ArrayList<String> expandLines(String rawInputPath, InclusionParams params, boolean parseInclusion) {
        ArrayList<String> lines = new ArrayList<>();
        File inputFile = new File(rawInputPath);
        String inputPath = "";
        String inputDirname = "";
        try {
            if (!inputFile.exists() || !inputFile.isFile()) {
                throw new IOException();
            }
            inputPath = inputFile.getCanonicalPath();
            int cut = inputPath.lastIndexOf(File.separator);
            if (cut < 0) {
                throw new IOException();
            }
            inputDirname = inputPath.substring(0, cut);
        }
        catch (IOException e) {
            e.printStackTrace();
            lines.add(String.format("`Invalid include file: '%s' (not found).`{.warning}", inputPath));
            return lines;
        }
        if (params.parents.contains(inputPath)) {
            System.err.println("Loop detected. Will not process.");
            lines.add(String.format("`Invalid include file: '%s' (circular inclusion).`{.warning}", inputPath));
            return lines;
        }
        if (inputPath == null || inputPath.isEmpty() || inputDirname == null || inputDirname.isEmpty()) {
            return lines;
        }
        try {
            params.parents.add(inputPath);
            Scanner scanner = new Scanner(inputFile);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (!parseInclusion) {
                    lines.add(line);
                } else {
                    if (canExpandLine(line, params)) {
                        String includedPath = inputDirname+File.separator+params.path;
                        ArrayList<String> moreLines = expandLines(includedPath, params, includedPath.endsWith(".md"));
                        for (String ml: moreLines) {
                            lines.add(ml);
                        }
                    } else {
                        lines.add(line);
                    }
                }
            }
            scanner.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        finally {
            params.parents.remove(inputPath);
        }

        if (Bundle.src2dst.keySet().contains(inputPath)) {
            String outputPath = Bundle.src2dst.get(inputPath);
            File outputFile = new File(outputPath);
            if (!outputFile.exists() || inputFile.lastModified() > outputFile.lastModified()) {
                dump(lines, outputFile, outputPath.endsWith(".md"));
            }
        }
        return lines;
    }

    public static void dump(ArrayList<String> lines, File outputFile, boolean isMarkdownFile) {
        try (
                FileOutputStream fos = new FileOutputStream(outputFile);
        ) {
            for (String line: lines) {
                if (!isMarkdownFile) {
                    fos.write(line.getBytes());
                } else {
                    if (line.endsWith(" -<")) {
                        fos.write(line.substring(0, line.length() - 3).getBytes());
                        fos.write(" `@`{.foldable}".getBytes());
                    } else if (line.endsWith(" +<")) {
                        fos.write(line.substring(0, line.length() - 3).getBytes());
                        fos.write(" `@`{.foldable}".getBytes());
                    } else {
                        fos.write(line.getBytes());
                    }
                }
                fos.write("\n".getBytes());
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class InclusionParams {
    public final static String left = "=";
    public final static String right = "=";
    public final static String flag = "@include <-";
    public final static Integer flaglen;
    static {
        flaglen = flag.length();
    }
    public int leftSpace;
    public String pad;
    public String path;
    // public ArrayList<String> parents;
    public HashSet<String> parents;
    InclusionParams() {
        pad = "";
        path = "";
        parents = new HashSet<>();
    }
}
