package com.maverick.ssh;

import java.util.Enumeration;
import java.io.IOException;
import java.util.Vector;

public class ShellProcess
{
    Shell h;
    int e;
    Vector b;
    String f;
    boolean c;
    int d;
    static Matcher g;
    
    ShellProcess(final Shell h, final String f) {
        this.e = 0;
        this.b = new Vector();
        this.c = false;
        this.d = -1;
        this.h = h;
        this.f = f;
    }
    
    public synchronized int expect(final String s, final long n) throws ShellTimeoutException {
        return this.expect(s, n, false);
    }
    
    public synchronized int expect(final String s) throws ShellTimeoutException {
        return this.expect(s, 0L, false);
    }
    
    public synchronized int expect(final String s, final boolean b) throws ShellTimeoutException {
        return this.expect(s, 0L, b);
    }
    
    public String getCommandLine() {
        return this.f;
    }
    
    public synchronized String getLine(final int n) {
        if (n >= this.b.size()) {
            throw new IllegalArgumentException("Invalid line number " + n + " (" + this.b.size() + " lines available)");
        }
        return this.b.elementAt(n).toString();
    }
    
    public synchronized int expect(final String s, final long n, final boolean b) throws ShellTimeoutException {
        int i = (this.d == -1) ? this.e : (this.d + 1);
        this.d = -1;
        final long currentTimeMillis = System.currentTimeMillis();
        try {
            while (System.currentTimeMillis() - currentTimeMillis < n || n == 0L) {
                while (i < this.b.size() - 1) {
                    if (this.b(i, s)) {
                        return i;
                    }
                    if (b) {
                        ++this.e;
                    }
                    ++i;
                }
                if (this.b.size() > i && this.b(i, s)) {
                    return i;
                }
                this.wait(1000L);
            }
        }
        catch (final InterruptedException ex) {}
        throw new ShellTimeoutException();
    }
    
    private boolean b(final int d, final String s) {
        if (this.b(this.b.elementAt(d).toString(), s)) {
            this.d = d;
            return true;
        }
        return false;
    }
    
    private boolean b(final String s, final String s2) {
        if (ShellProcess.g != null) {
            return ShellProcess.g.matches(s, s2);
        }
        return s.indexOf(s2) > -1;
    }
    
    public synchronized void type(final String s) throws IOException {
        this.h.type(s);
    }
    
    public synchronized void carriageReturn() throws IOException {
        this.h.carriageReturn();
    }
    
    public synchronized void typeAndReturn(final String s) throws IOException {
        this.h.b(s);
    }
    
    public synchronized void close() {
        this.c = true;
        this.notifyAll();
    }
    
    synchronized void b() {
        this.b.addElement(new StringBuffer(""));
        this.notifyAll();
    }
    
    public synchronized String readLine() throws ShellTimeoutException {
        return this.readLine(0);
    }
    
    public synchronized String readLine(final int n) throws ShellTimeoutException {
        final long currentTimeMillis = System.currentTimeMillis();
        Label_0127: {
            try {
                while (!this.hasCompletedLines()) {
                    this.wait(1000L);
                    if (this.c || this.h.isClosed()) {
                        break Label_0127;
                    }
                    if (!this.hasCompletedLines() && !this.h.r.isConnected()) {
                        throw new ShellTimeoutException("Client disconnected before session was closed.");
                    }
                    if (n > 0 && System.currentTimeMillis() - currentTimeMillis > n) {
                        throw new ShellTimeoutException();
                    }
                }
                return this.b.elementAt(this.e++).toString();
            }
            catch (final InterruptedException ex) {}
        }
        if (this.e >= this.b.size() - 1) {
            return null;
        }
        final StringBuffer sb = this.b.elementAt(this.e++);
        if (sb.toString().equals("") && this.e == this.b.size()) {
            return null;
        }
        return sb.toString();
    }
    
    public synchronized boolean hasCompletedLines() {
        return this.e < this.b.size() - 1;
    }
    
    public synchronized int getLineCount() {
        return this.b.size();
    }
    
    synchronized void b(final String s) {
        if (this.b.size() == 0) {
            this.b.addElement(new StringBuffer(""));
        }
        this.b.elementAt(this.b.size() - 1).append(s);
        this.notifyAll();
    }
    
    public void interrupt() throws IOException {
        this.type(new String(new char[] { '\u0003' }));
    }
    
    public static void setMatcher(final Matcher g) {
        ShellProcess.g = g;
    }
    
    public String toString() {
        final Enumeration elements = this.b.elements();
        final StringBuffer sb = new StringBuffer(this.f + System.getProperty("line.separator"));
        while (elements.hasMoreElements()) {
            sb.append(elements.nextElement().toString() + (elements.hasMoreElements() ? System.getProperty("line.separator") : ""));
        }
        return sb.toString();
    }
    
    static {
        ShellProcess.g = null;
    }
    
    public interface Matcher
    {
        boolean matches(final String p0, final String p1);
    }
}
