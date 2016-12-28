package com.tangzhixiong.md2html;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.*;

public class Bundle {
    public static String[] mdExtsString = {
            "Rmd",
            "markdown",
            "md",
            "mdown",
            "mdwn",
            "mkd",
            "mkdn",
    };
    public static String[] markupExtsString = {
            "org",
            "rst",
            "asc",
            "asciidoc",
            "tex",
            "texi",
            "textile",
            "wiki",
            "docx",
            "epub",
    };
    public static HashSet<String> mdExts = new HashSet<>();
    public static HashSet<String> markupExts = new HashSet<>();
    static {
        for (String s : mdExtsString) {
            mdExts.add(s.toLowerCase());
        }
        for (String s : markupExtsString) {
            markupExts.add(s.toLowerCase());
        }
    };

    public static String srcDir;
    public static String dstDir;
    public static WatchService watchService = null;

    final public static HashMap<String, String> src2dst = new HashMap<>();
    final public static HashMap<WatchKey, String> key2dir = new HashMap<>();

    public static final String resourceDirName = "_md2html";
    public static final String md2htmlymlRes = ".md2html.yml";
    public static final String htmltemplateRes = "html.template";
    public static final String[] resources = {
            md2htmlymlRes,
            "cat.pl",
            "drawer.pl",
            htmltemplateRes,
            "jquery-3.0.0.min.js",
            "lazyload.min.js",
            "clipboard.min.js",
            "main.css",
            "main.js",
            "README.txt",
    };
    public static String resourcePath;
    public static String htmltemplatePath;
    public static String dotmd2htmlymlPath;

    public static void fillBundle(String srcDirPath, String dstDirPath) throws Exception {
        srcDir = srcDirPath;
        dstDir = dstDirPath;
        resourcePath = dstDir+File.separator+ resourceDirName;
        htmltemplatePath = resourcePath+File.separator+htmltemplateRes;
        dotmd2htmlymlPath = resourcePath+File.separator+md2htmlymlRes;
        try {
            if (watchService != null) {
                watchService.close();
            }
            watchService = FileSystems.getDefault().newWatchService();
        }
        catch (Exception e) {
            e.printStackTrace();
            return;
        }
        if (watchService == null) {
            return;
        }

        src2dst.clear();
        key2dir.clear();

        boolean stillInBasedir = true;
        final ArrayDeque<File> queue = new ArrayDeque<>();
        queue.add(new File(srcDirPath));
        while (!queue.isEmpty()) {
            File pwd = queue.poll();
            try {
                // System.out.printf("Watching %s...\n", pwd.getCanonicalPath());
                WatchKey key = pwd.toPath().register(watchService
                        , StandardWatchEventKinds.ENTRY_MODIFY
                        , StandardWatchEventKinds.ENTRY_CREATE );
                key2dir.put(key, pwd.getCanonicalPath());
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            final File[] entries;
            try {
                entries = pwd.listFiles();
            }
            catch (NullPointerException e) { continue; }
            if (entries == null) { continue; }
            for (final File entry: entries) {
                final String entryCanoPath = entry.getCanonicalPath();
                final String entryBasename = entry.getName();
                if (entry.isFile()) {
                    src2dst.put(entryCanoPath, dstDir + File.separator + entryCanoPath.substring(srcDir.length()+1));
                } else if (entry.isDirectory()) {
                    if (stillInBasedir && entryBasename.equals(resourceDirName)) { continue; }
                    if (dstDir.endsWith(entryBasename) && dstDir.equals(entryCanoPath)) {
                        continue;
                    }
                    if (!entryBasename.startsWith(".")) {
                        queue.add(entry);
                    }
                }
            }
            stillInBasedir = false;
        }
        for (String dir: key2dir.values()) {
            // TODO: if no index.html was generated, generate it!
        }
    }

    public static Vector<String> getFiles() {
        Vector<String> files = new Vector<>();
        try {
            // files
            for (String f: src2dst.keySet()) {
                files.add(f);
            }
            // dirs
            for (String f: key2dir.values()) {
                files.add(f+File.separator);
            }
            files.sort(new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    return o1.compareTo(o2);
                }
            });
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return files;
    }
}
