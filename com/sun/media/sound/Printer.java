package com.sun.media.sound;

final class Printer
{
    static final boolean err = false;
    static final boolean debug = false;
    static final boolean trace = false;
    static final boolean verbose = false;
    static final boolean release = false;
    static final boolean SHOW_THREADID = false;
    static final boolean SHOW_TIMESTAMP = false;
    private static long startTime;
    
    private Printer() {
    }
    
    public static void err(final String s) {
    }
    
    public static void debug(final String s) {
    }
    
    public static void trace(final String s) {
    }
    
    public static void verbose(final String s) {
    }
    
    public static void release(final String s) {
    }
    
    public static void println(final String s) {
        System.out.println("" + s);
    }
    
    public static void println() {
        System.out.println();
    }
    
    static {
        Printer.startTime = 0L;
    }
}
