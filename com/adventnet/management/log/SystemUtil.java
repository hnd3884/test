package com.adventnet.management.log;

import java.io.PrintStream;

public class SystemUtil
{
    public static PrintStream cout;
    public static PrintStream cerr;
    private static boolean initialized;
    
    public static void init() {
        if (SystemUtil.initialized) {
            System.out.println("SystemUtil class is already initialized");
            return;
        }
        SystemUtil.cout = System.out;
        SystemUtil.cerr = System.err;
        SystemUtil.initialized = true;
    }
    
    static {
        SystemUtil.cout = System.out;
        SystemUtil.cerr = System.err;
        SystemUtil.initialized = false;
    }
}
