package com.adventnet.management.log;

import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public class NmsPrintWriter extends PrintWriter
{
    int lineCount;
    
    public NmsPrintWriter(final FileOutputStream fileOutputStream, final boolean b) {
        super(fileOutputStream, b);
        this.lineCount = 0;
    }
    
    public void println(final char[] array) {
        ++this.lineCount;
        super.println(array);
    }
}
