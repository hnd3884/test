package org.apache.xmlbeans.impl.jam.internal;

import java.util.HashSet;
import java.util.Iterator;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Set;
import org.apache.xmlbeans.impl.jam.provider.JamLogger;

public class JamLoggerImpl implements JamLogger
{
    private boolean mShowWarnings;
    private Set mVerboseClasses;
    private PrintWriter mOut;
    
    public JamLoggerImpl() {
        this.mShowWarnings = true;
        this.mVerboseClasses = null;
        this.mOut = new PrintWriter(System.out, true);
    }
    
    protected void setOut(final PrintWriter out) {
        this.mOut = out;
    }
    
    @Override
    public boolean isVerbose(final Object o) {
        if (this.mVerboseClasses == null) {
            return false;
        }
        for (final Class c : this.mVerboseClasses) {
            if (c.isAssignableFrom(o.getClass())) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean isVerbose(final Class aClass) {
        if (this.mVerboseClasses == null) {
            return false;
        }
        for (final Class c : this.mVerboseClasses) {
            if (c.isAssignableFrom(aClass)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void setVerbose(final Class c) {
        if (c == null) {
            throw new IllegalArgumentException();
        }
        if (this.mVerboseClasses == null) {
            this.mVerboseClasses = new HashSet();
        }
        this.mVerboseClasses.add(c);
    }
    
    @Override
    public void setShowWarnings(final boolean b) {
        this.mShowWarnings = b;
    }
    
    @Override
    public void verbose(final String msg, final Object o) {
        if (this.isVerbose(o)) {
            this.verbose(msg);
        }
    }
    
    @Override
    public void verbose(final Throwable t, final Object o) {
        if (this.isVerbose(o)) {
            this.verbose(t);
        }
    }
    
    @Override
    public void verbose(final String msg) {
        this.printVerbosePrefix();
        this.mOut.println(msg);
    }
    
    @Override
    public void verbose(final Throwable t) {
        this.printVerbosePrefix();
        this.mOut.println();
        t.printStackTrace(this.mOut);
    }
    
    @Override
    public void warning(final Throwable t) {
        if (this.mShowWarnings) {
            this.mOut.println("[JAM] Warning: unexpected exception thrown: ");
            t.printStackTrace();
        }
    }
    
    @Override
    public void warning(final String w) {
        if (this.mShowWarnings) {
            this.mOut.print("[JAM] Warning: ");
            this.mOut.println(w);
        }
    }
    
    @Override
    public void error(final Throwable t) {
        this.mOut.println("[JAM] Error: unexpected exception thrown: ");
        t.printStackTrace(this.mOut);
    }
    
    @Override
    public void error(final String msg) {
        this.mOut.print("[JAM] Error: ");
        this.mOut.println(msg);
    }
    
    public void setVerbose(final boolean v) {
        this.setVerbose(Object.class);
    }
    
    @Override
    public boolean isVerbose() {
        return this.mVerboseClasses != null;
    }
    
    private void printVerbosePrefix() {
        final StackTraceElement[] st = new Exception().getStackTrace();
        this.mOut.println("[JAM] Verbose: ");
        this.mOut.print('(');
        this.mOut.print(shortName(st[2].getClassName()));
        this.mOut.print('.');
        this.mOut.print(st[2].getMethodName());
        this.mOut.print(':');
        this.mOut.print(st[2].getLineNumber());
        this.mOut.print(")  ");
    }
    
    private static String shortName(String className) {
        final int index = className.lastIndexOf(46);
        if (index != -1) {
            className = className.substring(index + 1, className.length());
        }
        return className;
    }
}
