package com.adventnet.tools.update.installer.log;

import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public class LogPrintWriter extends PrintWriter
{
    int lineCount;
    
    public LogPrintWriter(final FileOutputStream fop, final boolean autoflush) {
        super(fop, autoflush);
        this.lineCount = 0;
    }
    
    @Override
    public void println(final char[] arr) {
        ++this.lineCount;
        super.println(arr);
    }
}
