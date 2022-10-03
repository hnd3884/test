package org.tanukisoftware.wrapper;

import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;

public class WrapperProcess
{
    private WrapperProcessOutputStream m_wpis;
    private WrapperProcessInputStream m_wpos;
    private WrapperProcessInputStream m_wpes;
    private int m_pid;
    private int m_exitcode;
    private boolean m_isDetached;
    private int m_softShutdownTimeout;
    
    private WrapperProcess() {
        this.m_exitcode = Integer.MIN_VALUE;
    }
    
    private native boolean nativeIsAlive();
    
    private native void nativeDestroy();
    
    private native void nativeExitValue();
    
    private native void nativeWaitFor();
    
    protected void finalize() throws Throwable {
        try {
            this.m_wpes.close();
            this.m_wpis.close();
            this.m_wpos.close();
        }
        finally {
            super.finalize();
        }
    }
    
    public int getPID() {
        return this.m_pid;
    }
    
    public InputStream getInputStream() throws IOException {
        return this.m_wpos;
    }
    
    public InputStream getStdOut() throws IOException {
        return this.m_wpos;
    }
    
    public InputStream getErrorStream() throws IOException {
        return this.m_wpes;
    }
    
    public InputStream getStdErr() throws IOException {
        return this.m_wpes;
    }
    
    public OutputStream getOutputStream() throws IOException {
        return this.m_wpis;
    }
    
    public OutputStream getStdIn() throws IOException {
        return this.m_wpis;
    }
    
    public int waitFor() throws InterruptedException {
        if (this.m_exitcode == Integer.MIN_VALUE) {
            this.nativeWaitFor();
        }
        return this.m_exitcode;
    }
    
    public int exitValue() throws IllegalThreadStateException {
        if (this.m_exitcode == Integer.MIN_VALUE) {
            this.nativeExitValue();
        }
        return this.m_exitcode;
    }
    
    public boolean isAlive() {
        return this.nativeIsAlive();
    }
    
    public void destroy() {
        this.nativeDestroy();
    }
}
