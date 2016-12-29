package com.tangzhixiong.md2html;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

class Config {
    public static boolean watchMode = false;
    public static boolean silentMode = false;
    public static boolean verboseMode = false;
    public static boolean foldMarkdown = false;
    public static boolean expandMarkdown = false;
    public static boolean readmeAsMainIndex = false;
    public static boolean logCommands = false;
    public static boolean printGraph = false;

    public static String srcDirPath = null;
    public static String dstDirPath = null;
    public static String resDirPath = null;
    public static String configYaml = null;
}

public class Main {
    public static void main(String[] args) {
        try {
            System.out.print("[L] Checking pandoc...");
            Process p = new ProcessBuilder().command(new String[]{"pandoc", "-v"}).start();
            p.waitFor(1, TimeUnit.SECONDS);
            System.out.print(" done. [Pandoc Installed]\n");
        } catch (Exception e) {
            System.out.println("[X] Missing pandoc, go download at: \"http://pandoc.org/\"");
            System.exit(0);
        }

        try {
            // hacks copied from StackOverflow: http://stackoverflow.com/questions/361975/setting-the-default-java-character-encoding
            System.setProperty("file.encoding", "UTF-8");
            Field charset = Charset.class.getDeclaredField("defaultCharset");
            charset.setAccessible(true);
            charset.set(null, null);
        }
        catch (Exception e) {
            System.err.println("Tip: You need to explicit specify JVM encoding to UTF-8, like: "+
                    "'java -jar md2html.jar -Dfile.encoding=utf-8'.");
        }
        try {
            // load commandline configs
            parseConfigs(args);

            // config source dir
            if (Config.srcDirPath == null) { Config.srcDirPath = "."; }
            final File srcDirFile = new File(Config.srcDirPath);
            if (!srcDirFile.exists() || !srcDirFile.isDirectory()) {
                System.err.println("Invalid input directory: "+Config.srcDirPath);
                System.exit(1);
            }

            // TODO: if git repo, grab upstream info

            // config destination dir
            if (Config.dstDirPath == null) {
                Config.dstDirPath = String.format("../%s-publish", Utility.getDirName(Config.srcDirPath));
            }
            final File dstDirFile = new File(Config.dstDirPath);
            if (dstDirFile.exists() && !dstDirFile.isDirectory()) {
                System.err.println("Invalid output directory: "+Config.dstDirPath);
                System.exit(2);
            }
            if (!dstDirFile.exists()) {
                dstDirFile.mkdirs();
            }

            // normalize srcDirPath/dstDirPath
            Config.srcDirPath = srcDirFile.getCanonicalPath();
            Config.dstDirPath = dstDirFile.getCanonicalPath();

            // build file mapping strategy
            Bundle.fillBundle(Config.srcDirPath, Config.dstDirPath);

            // extract necessary static resources
            for (String resourcePath: Bundle.resources) {
                Utility.extractResourceFile("/"+resourcePath, Bundle.resourcePath+File.separator+resourcePath);
            }
            // merge configs
            ArrayList<String> partAll = new ArrayList<>();
            {
                // add extracted config
                ArrayList<String> files = Utility.listing();
                partAll.add("files:");
                for (String file: files) {
                    partAll.add("  - "+file.replace('\\', '/'));
                }
            }
            {
                // add your config and global config
                partAll.addAll(Utility.getLinesNaive(Bundle.dotmd2htmlymlPath));
                if (Config.configYaml != null) {
                    partAll.addAll(Utility.getLinesNaive(Config.configYaml));
                }
                // add config block lines
                partAll.add(0, "---");
                partAll.add("---");
            }
            ArrayList<String> yamlLinesToSave = new ArrayList<>();
            {
                for (String line : partAll) {
                    int idx = line.indexOf(":");
                    if (idx >0) {
                        String key = line.substring(0, idx);
                        if (Bundle.aliasMaps.containsKey(key)) {
                            yamlLinesToSave.add(Bundle.aliasMaps.get(key)+line.substring(idx));
                            continue;
                        }
                    }
                    // no alias match
                    yamlLinesToSave.add(line);
                }
            }
            // write merged configs out
            Utility.dump(yamlLinesToSave, new File(Bundle.dotmd2htmlymlPath));

            // load your res files
            if (Config.resDirPath != null) {
                File resDir = new File(Config.resDirPath);
                if (resDir.exists() && resDir.isDirectory()) {
                    Utility.copyResources(Config.resDirPath, Bundle.resourcePath);
                } else {
                    System.out.println("[E] Invalid resource directory.");
                }
            }

            // [srcDir]->[dstDir]: copy or convert, if update needed
            for (String inputPath: Bundle.src2dst.keySet()) {
                String outputPath = Bundle.src2dst.get(inputPath);
                Utility.mappingFile(inputPath, outputPath);
            }

            // a -> b, build links
            if (Config.printGraph) {
                Utility.printInclusionLogs();
            }

            // if not watchMode, done
            if (!Config.watchMode) {
                System.out.println("\nTip: You can turn on [watch mode] with '-w' option.");
                System.exit(0);
            }

            // else, watch folder for changes, update when edits happen
            System.out.println("Watching...");
            long prevTimeStamp = System.currentTimeMillis(), curTimeStamp;
            String lastInputPath = "";
            while (true) {
                WatchKey key = null;
                try {
                    key = Bundle.watchService.take();
                    if (key == null || !Bundle.key2dir.containsKey(key)) {
                        throw new InterruptedException();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    continue;
                }
                System.out.println("Watching...");
                for (WatchEvent<?> event : key.pollEvents()) {
                    // if (event.kind() != StandardWatchEventKinds.ENTRY_MODIFY) { continue; }
                    @SuppressWarnings (value="unchecked")
                    final File hit = new File( Bundle.key2dir.get(key)+ File.separator+     // dirname
                                    ((WatchEvent<Path>) event).context().toFile().getName());   // basename
                    if (!hit.exists() || !hit.isFile()) {
                        continue;
                    }
                    String inputPath = hit.getCanonicalPath();
                    String outputPath = "";
                    curTimeStamp = System.currentTimeMillis();
                    if (inputPath.equals(lastInputPath) && curTimeStamp - prevTimeStamp < 100) {    // 0.1 second
                        continue;
                    } else {
                        lastInputPath = inputPath;
                        prevTimeStamp = curTimeStamp;
                    }
                    if (Bundle.src2dst.keySet().contains(inputPath)) {
                        outputPath = Bundle.src2dst.get(inputPath);
                        Utility.mappingFile(inputPath, outputPath);
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

    public static void printHelp() {
        final String helpMsg = "(TODO: update help section!!!)\n"+
                "Usage: \n"+
                "    $ java -jar md2html.jar\n"+
                "\nOptions:\n"+
                "    -i, -input <SOURCE DIRECTORY>\n"+
                "           specify root of markdown files\n"+
                "    -o, -output <DESTINATION DIRECTORY>\n"+
                "           specify root of output files\n"+
                "    -w, -watch\n"+
                "           watch mode\n"+
                "    -s, -silent\n"+
                "           silent mode\n"+
                "    -v, -verbose\n"+
                "           verbose mode\n"+
                "    -e, -expand\n"+
                "           expand markdown files\n"+
                "    -f, -fold\n"+
                "           fold markdown contents\n"+
                "\nMore Usage Examples\n"+
                "   1. current dir to ../publish:\n" +
                "       $ java -jar md2html.jar -i . -o ../publish\n"+
                "   2. turn on watch, expand markdown\n"+
                "       $ java -jar md2html.jar -we\n";
        System.out.println(helpMsg);
    }

    public static void parseConfigs(String[] args) {
        for (int i = 0; i < args.length; ++i) {
            if (false) {
            } else if (args[i].equals("-h") || args[i].equals("-help")) {
                printHelp();
                System.exit(0);
            } else if (args[i].equals("-i") || args[i].equals("-input")) {
                if (++i < args.length) { Config.srcDirPath = args[i]; }
            } else if (args[i].equals("-o") || args[i].equals("-output")) {
                if (++i < args.length) { Config.dstDirPath = args[i]; }
            } else if (args[i].equals("-r") || args[i].equals("-res")) {
                if (++i < args.length) { Config.resDirPath = args[i]; }
            } else if (args[i].equals("-c") || args[i].equals("-config")) {
                if (++i < args.length) { Config.configYaml = args[i]; }
            } else if (args[i].equals("-w") || args[i].equals("-watch")) {
                Config.watchMode = true;
            } else if (args[i].equals("-s") || args[i].equals("-silent")) {
                Config.silentMode = true;
            } else if (args[i].equals("-v") || args[i].equals("-verbose")) {
                Config.verboseMode = true;
            } else if (args[i].equals("-e") || args[i].equals("-expand")) {
                Config.expandMarkdown = true;
            } else if (args[i].equals("-f") || args[i].equals("-fold")) {
                Config.foldMarkdown = true;
            } else if (args[i].equals("-g") || args[i].equals("-graph")) {
                Config.printGraph = true;
            } else if (args[i].equals("-l") || args[i].equals("-log")) {
                Config.logCommands = true;
            } else if (args[i].startsWith("-")) {
                for (int k = 1; k < args[i].length(); ++k) {
                    switch (args[i].charAt(k)) {
                        case 'w': Config.watchMode = true; break;
                        case 's': Config.silentMode = true; break;
                        case 'v': Config.verboseMode = true; break;
                        case 'e': Config.expandMarkdown = true; break;
                        case 'f': Config.foldMarkdown = true; break;
                        case 'g': Config.printGraph = true; break;
                        case 'l': Config.logCommands = true; break;
                        default:
                            System.err.println("Invalid Config: "+args[i] +", and char: "+String.valueOf(args[i].charAt(k)));
                            printHelp();
                            System.exit(3);
                    }
                }
            } else {
                System.err.println("Invalid Config.");
                printHelp();
                System.exit(3);
            }
        }

        System.err.printf("__________ md2html configs __________\n");
        System.err.printf("    Watch  Mode:             %s\n", Config.watchMode ? "ON" : "OFF");
        System.err.printf("    Silent Mode:             %s\n", Config.silentMode ? "ON" : "OFF");
        System.err.printf("    Verbose Mode:            %s\n", Config.verboseMode ? "ON" : "OFF");
        System.err.printf("    Expand Markdown?:        %s\n", Config.expandMarkdown ? "TRUE" : "FALSE");
        System.err.printf("    Fold   Markdown?:        %s\n", Config.foldMarkdown ? "TRUE" : "FALSE");
        System.err.printf("    Configuration file: %s", Config.configYaml == null ? "NO EXTRA" : Config.configYaml);
        System.err.printf("-------------------------------------\n");
    }
}
