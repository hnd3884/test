package com.me.devicemanagement.onpremise.start;

import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.FileDescriptor;
import java.io.PrintStream;

public class DCConsoleOut
{
    public static PrintStream out;
    
    public static void print(final String msg) {
        DCConsoleOut.out.print(msg);
    }
    
    public static void println(final String msg) {
        DCConsoleOut.out.println(msg);
    }
    
    public static void main(final String[] args) {
        print(args[0]);
        for (int i = args[0].length(); i < 50; ++i) {
            print(" ");
        }
        println(args[1]);
    }
    
    static {
        final FileOutputStream fdOut = new FileOutputStream(FileDescriptor.out);
        DCConsoleOut.out = new PrintStream(new BufferedOutputStream(fdOut, 128), true);
    }
}
