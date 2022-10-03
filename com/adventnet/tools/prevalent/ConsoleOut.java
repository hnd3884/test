package com.adventnet.tools.prevalent;

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
    
    public static void main(final String[] args) {
        print(args[0]);
        for (int i = args[0].length(); i < 50; ++i) {
            print(" ");
        }
        println(args[1]);
    }
    
    static {
        final FileOutputStream fdOut = new FileOutputStream(FileDescriptor.out);
        ConsoleOut.out = new PrintStream(new BufferedOutputStream(fdOut, 128), true);
    }
}
