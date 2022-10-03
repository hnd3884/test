package com.adventnet.tools.update.installer;

import javax.swing.JApplet;
import java.applet.Applet;
import java.io.File;

public final class GeneralUtility
{
    public static boolean debug;
    private static String[] keywords;
    
    public static String getBaseName(final String label, final String ext) {
        String lastName = "";
        if (label.startsWith("http://")) {
            lastName = getLastName(label, "/");
        }
        else {
            lastName = getLastName(label, File.separator);
        }
        final int index = lastName.lastIndexOf(ext);
        String tmp = lastName;
        if (index != -1) {
            try {
                tmp = lastName.substring(0, index);
                return tmp;
            }
            catch (final Exception ex) {}
        }
        return tmp;
    }
    
    public static String changeForWindows(String input) {
        if (System.getProperty("os.name").startsWith("Window")) {
            input = input.replace('/', '\\');
        }
        return input;
    }
    
    public static String getLastName(final String label, final String sep) {
        String tmp = label;
        final int i = tmp.lastIndexOf(sep);
        if (i != -1) {
            tmp = tmp.substring(i + 1);
        }
        return tmp;
    }
    
    public static boolean isAppletConstructorPresent(final Class cl) {
        final Class[] in = { Applet.class };
        try {
            cl.getConstructor((Class[])in);
            return true;
        }
        catch (final Exception ex) {
            final Class[] jap = { JApplet.class };
            try {
                cl.getConstructor((Class[])jap);
                return true;
            }
            catch (final Exception ex2) {
                return false;
            }
        }
    }
    
    public static String getDirectoryName(final String input) {
        final int index = input.lastIndexOf(File.separator);
        String classDir;
        if (index != -1) {
            classDir = input.substring(0, index);
        }
        else {
            classDir = input;
        }
        return classDir;
    }
    
    public static String replaceBackSlash(String input) {
        if (System.getProperty("os.name").startsWith("Window")) {
            input = input.replace('\\', '/');
        }
        return input;
    }
    
    public static String genSrcFileNameOnType(final String srcFileName, final int type) {
        return srcFileName + ".java";
    }
    
    public static boolean isJavaKeyWord(final String input) {
        if (input != null) {
            for (int i = 0; i < GeneralUtility.keywords.length; ++i) {
                if (input.equals(GeneralUtility.keywords[i])) {
                    return true;
                }
            }
        }
        return false;
    }
    
    static {
        GeneralUtility.debug = false;
        GeneralUtility.keywords = new String[] { "new", "instanceof", "cast", "field", "method", "null", "convert", "expr", "array", "goto", "Identifier", "boolean", "byte", "char", "short", "int", "long", "float", "double", "string", "void", "true", "false", "this", "super", "if", "else", "for", "while", "do", "expression", "declaration", "import", "class", "extends", "implements", "interface", "package", "private", "public", "protected", "const", "static", "transient", "synchronized", "native", "final", "volatile", "abstract", "strictfp", "throws", "error", "comment", "type", "length", "inline-return", "inline-method", "inline-new", "break", "switch", "case", "finally", "catch", "throw", "try", "continue", "return", "default" };
    }
}
