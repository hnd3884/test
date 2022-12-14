package org.apache.tomcat.util.log;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.ByteArrayOutputStream;

class CaptureLog
{
    private final ByteArrayOutputStream baos;
    private final PrintStream ps;
    
    protected CaptureLog() {
        this.baos = new ByteArrayOutputStream();
        this.ps = new PrintStream(this.baos);
    }
    
    protected PrintStream getStream() {
        return this.ps;
    }
    
    protected void reset() {
        this.baos.reset();
    }
    
    protected String getCapture() {
        return this.baos.toString();
    }
}
