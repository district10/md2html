package com.tangzhixiong.java;

import java.io.*;
import java.nio.file.*;
import java.util.*;

class Bundle {
    final public HashMap<String, String> src2dst;
    final public HashMap<WatchKey, File> key2dir;
    public Bundle() {
        src2dst = new HashMap<>();
        key2dir = new HashMap<>();
    }
}

public class Main {
    public static WatchService watchService;
    // shitshit public static WatchService watchService = FileSystems.getDefault().newWatchService();
    public static String rootStr;
    public static String repoStr;
    public static String distStr;
    public static String htmlTemplatePath;
    public static Runtime runtime;
    public static final String staticsDir = ".md2html";
    public static final String[] resources = {
            "cat.pl",
            "drawer.pl",
            "html.template",
            "jquery-3.0.0.min.js",
            "lazyload.min.js",
            "main.css",
            "main.js",
            "README.txt",
    };

    // @mkdir -p $(@D)
    public static void mkdirHyphenPDollarAtD(File dest) {
        File atd = dest.getParentFile();
        if (!atd.exists()) {
            atd.mkdirs();
        }
    }

    public static String mapToOutputPath(String inputPath) {
        String outputPath = distStr + inputPath.substring(rootStr.length()+1+repoStr.length()+1);
        if (outputPath.endsWith(".md")) {
            outputPath = outputPath.substring(0, outputPath.length()-3) + ".html";
        }
        return outputPath;
    }

    public static Bundle getBundle(String dirName) {
        Bundle bundle = new Bundle();
        File dir = new File(dirName);
        if (!dir.isDirectory()) {
            return bundle;
        }
        ArrayDeque<File> dirs = new ArrayDeque<>();
        dirs.push(dir);
        // bundle.dirs.add(dir);
        while (!dirs.isEmpty()) {
            File file = dirs.pop();
            if (file.isFile()) {
                try {
                    String inputPath = file.getCanonicalPath();
                    bundle.src2dst.put(inputPath, mapToOutputPath(inputPath));
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (file.isDirectory()) {
                try {
                    WatchKey key = file.toPath().register(watchService
                            , StandardWatchEventKinds.ENTRY_MODIFY
                            , StandardWatchEventKinds.ENTRY_CREATE );
                    bundle.key2dir.put(key, file);
                    // System.out.printf("Watching %s...\n", f.getCanonicalPath());
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                for (File f : file.listFiles()) {
                    if (f.isFile()) {
                        try {
                            String inputPath = f.getCanonicalPath();
                            bundle.src2dst.put(inputPath, mapToOutputPath(inputPath));
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if (f.isDirectory()) {
                        String basename = f.getName();
                        if (!basename.startsWith(".")) {
                            dirs.add(f);
                        }
                    }
                }
            }
        }
        return bundle;
    }

    public static String resolvedPath(String output) {
        String frag = output.substring(distStr.length());
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < frag.length(); ++i) {
            if (String.valueOf(frag.charAt(i)).equals(File.separator)) {
                sb.append("../");
            }
        }
        return sb.toString();
    }

    public static void mappingFile(String input, String output) {
        System.out.printf("%s -> %s\n", input, output);
        File inputFile = new File(input);
        File outputFile = new File(output);
        if (!outputFile.exists() || inputFile.lastModified() > outputFile.lastModified()) {
            mkdirHyphenPDollarAtD(outputFile);
            try {
                if (input.endsWith(".md")) {
                    String cmd = String.format( "pandoc -S -s --ascii --mathjax " +
                            "-f markdown+abbreviations+east_asian_line_breaks+emoji " +
                            "-V rootdir=./%s.md2html/ --template %s " +
                            "%s -o %s",
                            resolvedPath(output), htmlTemplatePath,
                            input, output);
                    // System.out.printf("Converting from '%s' to '%s' via `%s`\n", input, output, cmd);
                    runtime.exec(cmd);
                } else if (false && input.endsWith(".java")) {
                    /*
                    outputFile.mkdirs();
                    String indexPath = outputFile.getCanonicalPath()+File.separator+"index.md";
                    PrintStream ps = new PrintStream(new FileOutputStream(indexPath));
                    FileInputStream fis = new FileInputStream(inputFile);
                    byte[] bbuf = new byte[1024];
                    int hasRead = 0;
                    while ((hasRead = fis.read(bbuf)) > 0) {
                        ps.print(new String(bbuf, 0, hasRead));
                    }
                    // mappingFile(indexPath, mapping);
                    */
                } else {
                    // System.out.printf("Copying from '%s' to '%s'\n", input, output);
                    mkdirHyphenPDollarAtD(outputFile);
                    Files.copy(new File(input).toPath(), outputFile.toPath()
                            , StandardCopyOption.REPLACE_EXISTING
                            , StandardCopyOption.COPY_ATTRIBUTES );
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void extractResourceFile(String res, String path) {
        File outputFile = new File(path);
        mkdirHyphenPDollarAtD(outputFile);
        try (
                InputStream is = Main.class.getResourceAsStream(res);
                FileOutputStream fos = new FileOutputStream(outputFile);
        ) {
            if (is == null) {
                System.err.println("shit" + res);
                return;
            }
            byte[] bbuf = new byte[1024];
            int hasRead = 0;
            while ((hasRead = is.read(bbuf)) > 0) {
                fos.write(bbuf, 0, hasRead) ;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        watchService = FileSystems.getDefault().newWatchService();
        runtime = Runtime.getRuntime();
        rootStr = new File("..").getCanonicalPath();
        repoStr = new File(".").getCanonicalPath();
        repoStr = repoStr.substring(1+repoStr.lastIndexOf(File.separator));
        distStr = rootStr + File.separator + "publish-" + repoStr + File.separator;
        htmlTemplatePath = distStr+File.separator+staticsDir+File.separator+"html.template";
        for (String res: resources) {
            extractResourceFile("/"+res, distStr+File.separator+staticsDir+File.separator+res);
        }
        try {
            Bundle bundle = getBundle(".");
            for (String input : bundle.src2dst.keySet()) {
                String output = bundle.src2dst.get(input);
                mappingFile(input, output); // copy or convert, if needed
            }

            long prevTimeStamp = System.currentTimeMillis(), curTimeStamp;
            String lastInput = "";
            int i = 0;
            while (true) {
                WatchKey key = null;
                try {
                    key = watchService.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (key == null) { continue; }
                curTimeStamp = System.currentTimeMillis();
                for (WatchEvent<?> event : key.pollEvents()) {
                    // if (event.kind() != StandardWatchEventKinds.ENTRY_MODIFY) { continue; }
                    // System.out.println("watched out at :" + dir);
                    @SuppressWarnings (value="unchecked")
                    File hitfile = new File( String.format(
                                    "%s%s%s",
                                    bundle.key2dir.get(key).toPath().toAbsolutePath(), // dir
                                    File.separator,
                                    ((WatchEvent<Path>) event).context().toFile().getName())); // file
                    if (!hitfile.isFile() || !hitfile.exists()) {
                        continue;
                    }
                    String input = hitfile.getCanonicalPath();
                    String output = "";
                    if (input.equals(lastInput) && curTimeStamp - prevTimeStamp < 100) {
                        continue;
                    } else {
                        lastInput = input;
                        prevTimeStamp = curTimeStamp;
                    }
                    if (bundle.src2dst.keySet().contains(input)) {
                        output = bundle.src2dst.get(input);
                        mappingFile(input, output);
                    }
                }
                boolean valid = key.reset();
                if (!valid) {
                    break;
                }
            }
        }
        catch (Exception e ) {
            e.printStackTrace();
        }
    }
}
