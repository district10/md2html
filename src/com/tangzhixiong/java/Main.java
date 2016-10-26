package com.tangzhixiong.java;

import java.io.*;
import java.nio.file.*;

public class Main {
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

    public static void main(String[] args) throws IOException {
        try {
            String srcDirPath = ".";
            String dstDirPath = String.format("../%s-publish", Utility.getDirName("."));
            for (int i = 0; i < args.length; ++i) {
                switch (args[i]) {
                    case "-i":case "-input":
                        if (++i < args.length) { srcDirPath = args[i]; }
                        break;
                    case "-o":case "-output":
                        if (++i < args.length) { dstDirPath = args[i]; }
                        break;
                    default:
                        ;
                }
            }
            final File srcDirFile = new File(srcDirPath);
            if (!srcDirFile.exists() || !srcDirFile.isDirectory()) {
                System.out.println("Invalid input directory: "+srcDirPath);
                return;
            }
            final File dstDirFile = new File(dstDirPath);
            if (dstDirFile.exists() && !dstDirFile.isDirectory()) {
                System.out.println("Invalid output directory: "+dstDirPath);
                return;
            }
            if (!dstDirFile.exists()) {
                dstDirFile.mkdirs();
            }

            Bundle.fillBundle(srcDirFile.getCanonicalPath(), dstDirFile.getCanonicalPath());
            for (String resourcePath: Bundle.resources) {
                extractResourceFile("/"+resourcePath, Bundle.resourcePath+File.separator+resourcePath);
            }
            for (String src: Bundle.src2dst.keySet()) {
                String dst = Bundle.src2dst.get(src);
                Utility.mappingFile(src, dst); // copy or convert, if needed
            }

            // String files = DirectoryListing.listing();
            // System.out.println("files are:\n"+files);

            long prevTimeStamp = System.currentTimeMillis(), curTimeStamp;
            String lastInput = "";
            while (true) {
                WatchKey key = null;
                try {
                    key = Bundle.watchService.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    continue;
                }
                if (key == null || !Bundle.key2dir.containsKey(key)) {
                    continue;
                }
                for (WatchEvent<?> event : key.pollEvents()) {
                    // if (event.kind() != StandardWatchEventKinds.ENTRY_MODIFY) { continue; }
                    @SuppressWarnings (value="unchecked")
                    final File hit = new File(
                            String.format( "%s%s%s",
                                    Bundle.key2dir.get(key), // dir
                                    File.separator,
                                    ((WatchEvent<Path>) event).context().toFile().getName())); // file
                    if (!hit.isFile() || !hit.exists()) {
                        continue;
                    }
                    String input = hit.getCanonicalPath();
                    String output = "";
                    curTimeStamp = System.currentTimeMillis();
                    if (input.equals(lastInput) && curTimeStamp - prevTimeStamp < 100) { // 0.1 second
                        continue;
                    } else {
                        lastInput = input;
                        prevTimeStamp = curTimeStamp;
                    }
                    if (Bundle.src2dst.keySet().contains(input)) {
                        output = Bundle.src2dst.get(input);
                        Utility.mappingFile(input, output);
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
