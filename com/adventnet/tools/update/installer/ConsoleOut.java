package com.adventnet.tools.update.installer;

import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.FileDescriptor;
import java.io.PrintStream;

public class ConsoleOut
{
    public static PrintStream out;
    
    public static void print(final String msg) {
        ConsoleOut.out.print(msg);
    }
    
    public static void println(final String msg) {
        ConsoleOut.out.println(msg);
    }
    
    static {
        final FileOutputStream fdOut = new FileOutputStream(FileDescriptor.out);
        ConsoleOut.out = new PrintStream(new BufferedOutputStream(fdOut), true);
    }
}
