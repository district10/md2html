package com.tangzhixiong.java;

import com.sun.jmx.remote.internal.ArrayQueue;

import javax.sound.midi.SysexMessage;
import java.io.*;
import java.util.*;

public class DirectoryListing {
    public static void main(String[] args) throws Exception {
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
