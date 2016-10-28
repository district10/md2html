package com.tangzhixiong.java;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

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
            if (Config.verboseMode) {
                System.out.println("[/] making directory: "+atd.getAbsolutePath());
            }
            atd.mkdirs();
        }
    }

    public static void md2html(String outputPath) {
        Runtime runtime = Runtime.getRuntime();
        String outputPathHTML = outputPath.substring(0, outputPath.length()-3) + ".html";
        String pathBasedOnRoot = outputPath.substring(Bundle.dstDir.length()+1);
        String cmd = String.format( "pandoc -S -s --ascii --mathjax " +
                        "-f markdown+abbreviations+east_asian_line_breaks+emoji " +
                        "-V rootdir=%s -V md2htmldir=%s/ -V thispath=%s --template %s %s %s -o %s",
                        resolveToRoot(outputPath, Bundle.dstDir), Bundle.resourceDirName, pathBasedOnRoot.replace('\\', '/'),
                        Bundle.htmltemplatePath, Bundle.dotmd2htmlymlPath,
                        outputPath, outputPathHTML);

        try {
            if (!Config.silentMode) {
                System.out.printf("[P] %s -> %s\n", outputPath, outputPathHTML);
            }
            runtime.exec(cmd);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void mappingFile(String inputPath, String outputPath) {
        // write log
        //  [+] 'D:\tzx\git\md2html\README.md' -> 'D:\tzx\git\md2html-publish\README.html'
        mappingFile(inputPath, outputPath, !Config.silentMode);
    }

    public static void mappingFile(String inputPath, String outputPath, boolean writeLog) {
        File inputFile = new File(inputPath);
        File outputFile = new File(outputPath);
        if (!outputFile.exists() || inputFile.lastModified() > outputFile.lastModified()) {
            mkdirHyphenPDollarAtD(outputFile);
            try {
                // expand markdown file
                boolean isMarkdownFile = inputPath.endsWith(".md");
                if (isMarkdownFile) {
                    // src/dir/file.md -> dst/dir/file.md
                    if (!Config.expandMarkdown) {
                        if (writeLog) {
                            System.out.printf("[C] %s -> %s\n", inputPath, outputPath);
                        }
                        Files.copy(inputFile.toPath(), outputFile.toPath()
                                , StandardCopyOption.REPLACE_EXISTING
                                , StandardCopyOption.COPY_ATTRIBUTES);
                    } else {
                        InclusionParams params = new InclusionParams();
                        List<String> lines = expandLines(inputPath, params);
                        if (writeLog) {
                            System.out.printf("[E] %s -> %s\n", inputPath, outputPath);
                        }
                        dump(lines, outputFile, isMarkdownFile);
                    }
                    // dst/dir/file.md -> dst/dir/file.html
                    md2html(outputPath);
                } else {
                    if (writeLog) {
                        System.out.printf("[C] %s -> %s\n", inputPath, outputPath);
                    }
                    Files.copy(inputFile.toPath(), outputFile.toPath()
                            , StandardCopyOption.REPLACE_EXISTING
                            , StandardCopyOption.COPY_ATTRIBUTES);
                }
                // have already copied, if code snippet, and you want to generate code fragment, then do it
                if (Config.generateCodeFragment) {
                    generateCodeFragmentIfPossible(outputPath);
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // no need to update
            if (writeLog) {
                System.out.printf("[ ] %s -> %s\n", inputPath, outputPath);
            }
        }
    }

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
            return "";
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
        if (!inputFile.exists() || !inputFile.isFile() || inputFile.length() > 2048 ) {
            if (!Config.silentMode) {
                System.out.printf("[X] %s -> %s\n", inputPath, outputPath);
            }
            return;
        }
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
                if (!Config.silentMode) {
                    System.out.printf("[C] %s -> %s\n", inputPath, outputPath);
                }
                // pipe in
                byte[] buf = new byte[1024];
                int hasRead = 0;
                ps.printf("~~~~~~~~~~~~~~~~~~~~~ {.%s .numberLines}\n", label);
                while ((hasRead = fis.read(buf)) > 0) { ps.write(buf, 0, hasRead) ; }
                ps.print("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
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
        return true;
    }

    public static List<String> getLinesNaive(String inputPath) {
        try {
            return Files.readAllLines(new File(inputPath).toPath(), Charset.defaultCharset() );
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // inputPath is a CanonicalPath, params is for INPUT
    public static List<String> expandLines(String inputPath, InclusionParams params) {
        boolean isMarkdownFile = inputPath.endsWith(".md");
        if (!isMarkdownFile) {
            return getLinesNaive(inputPath);
        }
        ArrayList<String> lines = new ArrayList<>();
        int cut = inputPath.lastIndexOf(File.separator);
        if (cut < 0) {
            return lines;
        }
        String inputPathDirname = inputPath.substring(0, cut);
        File inputFile = new File(inputPath);
        if (!inputFile.isFile() || !inputFile.canRead()) {
            return lines;
        }
        if (params.parents.contains(inputPath)) {
            System.err.printf("Loop detected, %s will not be included.\n", inputPath);
            StringBuilder sb = new StringBuilder();
            sb.append("\n```\nLOOP->-[");
            for (String path: params.parents) {
                sb.append(path);
                sb.append("]\n        ");
            }
            sb.append(inputPath);
            sb.append("]->-| LOOP |\n```\n");
            lines.add(sb.toString());
            System.err.println(sb.toString());
            return lines;
        }
        try {
            params.parents.add(inputPath);
            Scanner scanner = new Scanner(inputFile);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                InclusionParams paramsAnother = new InclusionParams();
                if (canExpandLine(line, paramsAnother)) {
                    String includePathAnother = inputPathDirname+File.separator+paramsAnother.path;
                    try {
                        includePathAnother = new File(includePathAnother).getCanonicalPath();
                        int leftSpace  = paramsAnother.leftSpace;
                        String pad = paramsAnother.pad;
                        List<String> moreLines = expandLines(includePathAnother, params);
                        if (moreLines != null && moreLines.size() > 0) {
                            for (String ml: moreLines) {
                                lines.add(InclusionParams.spaces[leftSpace]+pad+ml);
                            }
                        }
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                        continue;
                    }
                } else {
                    lines.add(line);
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

        /*
        if (Bundle.src2dst.keySet().contains(inputPath)) {
            String outputPath = Bundle.src2dst.get(inputPath);
            File outputFile = new File(outputPath);
            if (!outputFile.exists() || inputFile.lastModified() > outputFile.lastModified()) {
                dump(lines, outputFile, true);
                md2html(outputPath);
            }
        } else {
            System.out.println("Not fould.???");
        }
        */
        return lines;
    }

    public static void dump(List<String> lines, File outputFile, boolean isMarkdownFile) {
        mkdirHyphenPDollarAtD(outputFile);
        try (
            FileOutputStream fos = new FileOutputStream(outputFile);
        ) {
            if (isMarkdownFile && Config.foldMarkdown) {
                // twist a little bit for each line
                for (String line: lines) {
                    if (line.endsWith(" -<")) {
                        fos.write(line.substring(0, line.length() - 3).getBytes());
                        fos.write(" `@`{.fold}".getBytes());
                    } else if (line.endsWith(" +<")) {
                        fos.write(line.substring(0, line.length() - 3).getBytes());
                        fos.write(" `@`{.foldable}".getBytes());
                    } else {
                        fos.write(line.getBytes());
                    }
                    fos.write("\n".getBytes());
                }
            } else {
                // twist a little bit for each line
                for (String line: lines) {
                    fos.write(line.getBytes());
                    fos.write("\n".getBytes());
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void extractResourceFile(String resourcePath, String outputPath) {
        File outputFile = new File(outputPath);
        Utility.mkdirHyphenPDollarAtD(outputFile);
        try (
                InputStream is = Main.class.getResourceAsStream(resourcePath);
                FileOutputStream fos = new FileOutputStream(outputFile);
        ) {
            if (is == null) { return; }
            byte[] buf = new byte[1024];
            int hasRead = 0;
            while ((hasRead = is.read(buf)) > 0) {
                fos.write(buf, 0, hasRead) ;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<String> listing() {
        ArrayList<String> lines = new ArrayList<>();
        Vector<String> files = Bundle.getFiles();
        if (files.isEmpty()) {
            return lines;
        }
        /*
         *  -   "Makefile"
         *  -   "README.md"
         *  -   "demo\"
         *  -   "demo\code1.cpp"
         *  -   "demo\code2.h"
        */
        for (int i = 1; i < files.size(); ++i) {
            lines.add(files.elementAt(i).substring(1+Bundle.srcDir.length()));
        }
        return lines;
    }
}

class InclusionParams {
    public final static String left = "=";
    public final static String right = "=";
    public final static int maxNumOfSpaces = 50;
    public final static String[] spaces = new String[maxNumOfSpaces];
    public final static String flag = "@include <-";
    public final static Integer flaglen;
    static {
        spaces[0]  = "";
        for (int i = 1; i < maxNumOfSpaces; ++i) {
            char[] chars = new char[i];
            Arrays.fill(chars, ' ');
            spaces[i]  = new String(chars);
        }
        flaglen = flag.length();
    }

    public int leftSpace;
    public String pad;
    public String path;
    public LinkedHashSet<String> parents;
    InclusionParams() {
        pad = "";
        path = "";
        parents = new LinkedHashSet<>();
    }
}
