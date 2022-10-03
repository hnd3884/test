package com.adventnet.client.components.cthelp.web;

import java.util.regex.Matcher;
import java.io.RandomAccessFile;
import java.io.IOException;
import java.io.File;
import java.util.HashMap;
import java.util.regex.Pattern;

public class CTHelpIndexGenerator
{
    private static final Pattern TARGETPAT;
    private static StringBuffer strBuf;
    private static HashMap allTargets;
    private static StringBuffer errorBuf;
    private static String parentPath;
    private static String pathPrefix;
    
    public static void processFileList(final File[] fileArgs) throws IOException {
        for (int i = 0; i < fileArgs.length; ++i) {
            final File curFile = fileArgs[i];
            if (curFile.isDirectory()) {
                System.out.println("Processing directory " + curFile + ".....");
                processFileList(curFile.listFiles());
            }
            else {
                processFile(curFile);
            }
        }
    }
    
    public static String getFileAsString(final String fileName) throws IOException {
        final RandomAccessFile rd = new RandomAccessFile(fileName, "r");
        final byte[] arr = new byte[(int)rd.length()];
        rd.readFully(arr);
        rd.close();
        return new String(arr);
    }
    
    public static void processFile(final File curFile) throws IOException {
        final String curFilePath = curFile.getCanonicalPath();
        System.out.println("Processing " + curFilePath + "...");
        final String fileData = getFileAsString(curFilePath);
        final Matcher mat = CTHelpIndexGenerator.TARGETPAT.matcher(fileData);
        String subPath = curFilePath.substring(CTHelpIndexGenerator.parentPath.length());
        subPath = subPath.replace('\\', '/');
        while (mat.find()) {
            final String target = mat.group(1);
            if (CTHelpIndexGenerator.allTargets.get(target) != null) {
                CTHelpIndexGenerator.errorBuf.append("\n------------\nDuplicated Target " + target + "\nFirst File:" + CTHelpIndexGenerator.allTargets.get(target) + "\nDuplicated File:" + curFilePath);
            }
            else {
                CTHelpIndexGenerator.allTargets.put(target, curFilePath);
                CTHelpIndexGenerator.strBuf.append("\n<ACContextHelp target=\"" + target + "\" url=\"" + CTHelpIndexGenerator.pathPrefix + subPath + "#__" + target + "\"/>");
            }
        }
    }
    
    public static void generateHelpIndex(final String rootDir, final String prefixArg) throws IOException {
        CTHelpIndexGenerator.strBuf.append("<helpindexes>");
        final File rootFile = new File(rootDir);
        CTHelpIndexGenerator.parentPath = rootFile.getCanonicalPath();
        CTHelpIndexGenerator.pathPrefix = prefixArg;
        processFileList(rootFile.listFiles());
        CTHelpIndexGenerator.strBuf.append("\n</helpindexes>");
        writeStringAsFile("ContextHelpIndex.xml", CTHelpIndexGenerator.strBuf.toString());
        if (CTHelpIndexGenerator.errorBuf.length() > 0) {
            System.out.println("Errors have occurred!!!. Writing to errors.txt");
            writeStringAsFile("Errors.txt", "Following Targets are duplicated across files\n " + CTHelpIndexGenerator.errorBuf.toString());
        }
    }
    
    public static void writeStringAsFile(final String fileName, final String stringToWrite) throws IOException {
        System.out.println("Writing file " + fileName);
        final File f = new File(fileName);
        f.delete();
        final String parent = f.getParent();
        if (parent != null) {
            final File parentFile = new File(parent);
            if (!parentFile.exists()) {
                System.err.println(parent + " not present. Trying to create one");
                parentFile.mkdirs();
            }
        }
        final RandomAccessFile rd = new RandomAccessFile(fileName, "rw");
        rd.write(stringToWrite.getBytes());
        rd.close();
    }
    
    public static void main(final String[] args) throws IOException {
        generateHelpIndex(args[0], args[1]);
    }
    
    static {
        TARGETPAT = Pattern.compile("<a\\s*name=\"__([^\"]*)\"", 32);
        CTHelpIndexGenerator.strBuf = new StringBuffer();
        CTHelpIndexGenerator.allTargets = new HashMap();
        CTHelpIndexGenerator.errorBuf = new StringBuffer();
    }
}
