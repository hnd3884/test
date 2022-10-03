package com.pras.utils;

import java.io.File;
import java.io.PrintStream;

public class Log
{
    public static int DEBUG_LEVEL;
    public static int PRODUCTION_LEVEL;
    private static boolean isLogEnabled;
    private static int logLevel;
    private static String logFile;
    private static PrintStream out;
    private static StringBuffer buf;
    
    static {
        Log.DEBUG_LEVEL = 1;
        Log.PRODUCTION_LEVEL = 2;
        Log.isLogEnabled = true;
        Log.logLevel = Log.DEBUG_LEVEL;
        Log.logFile = String.valueOf(System.getProperty("user.dir")) + File.separator + "apkExtractor_log.txt";
        Log.out = null;
        Log.buf = new StringBuffer();
        System.out.println("Log File: " + Log.logFile);
    }
    
    public static void enableLog() {
        Log.isLogEnabled = true;
    }
    
    public static void disableLog() {
        Log.isLogEnabled = false;
    }
    
    public static void setLogLevel(final int level) {
        Log.logLevel = level;
    }
    
    public static void d(final String msg) {
        if (Log.logLevel == Log.DEBUG_LEVEL) {
            write(null, msg);
        }
    }
    
    public static void d(final String tag, final String msg) {
        if (Log.logLevel == Log.DEBUG_LEVEL) {
            write(null, msg);
        }
    }
    
    public static void p(final String msg) {
        if (Log.logLevel <= Log.PRODUCTION_LEVEL) {
            write(null, msg);
        }
    }
    
    public static void p(final String tag, final String msg) {
        if (Log.logLevel <= Log.PRODUCTION_LEVEL) {
            write(tag, msg);
        }
    }
    
    private static void write(final String tag, final String msg) {
        if (!Log.isLogEnabled) {
            return;
        }
        Log.buf.delete(0, Log.buf.length());
        if (tag != null) {
            Log.buf.append("[" + tag + "] ");
        }
        if (msg != null) {
            Log.buf.append(msg);
        }
        Log.buf.append("\n");
        print(Log.buf.toString());
    }
    
    public static void e(final String tag, final String msg) {
        e(tag, msg, null);
    }
    
    public static void e(final String tag, final String msg, final Exception ex) {
        Log.buf.delete(0, Log.buf.length());
        if (tag != null) {
            Log.buf.append("[" + tag + "] ");
        }
        if (msg != null) {
            Log.buf.append(msg);
        }
        if (Log.buf.length() > 0) {
            Log.buf.append("\n");
            print(Log.buf.toString());
        }
        if (ex != null) {
            try {
                if (Log.out == null) {
                    Log.out = new PrintStream(Log.logFile);
                }
                ex.printStackTrace(Log.out);
            }
            catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    private static void print(final String s) {
        try {
            if (Log.out == null) {
                Log.out = new PrintStream(Log.logFile);
            }
            Log.out.print(s);
            Log.out.flush();
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public static void exitLogger() {
        if (Log.out != null) {
            Log.out.close();
        }
    }
}
