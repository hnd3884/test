package org.owasp.esapi.waf.internal;

import java.io.IOException;
import java.util.Locale;
import java.io.Writer;
import java.io.PrintWriter;

public class InterceptingPrintWriter extends PrintWriter
{
    public InterceptingPrintWriter(final Writer out) {
        super(out);
    }
    
    @Override
    public PrintWriter append(final char c) {
        return super.append(c);
    }
    
    @Override
    public PrintWriter append(final CharSequence csq, final int start, final int end) {
        return super.append(csq, start, end);
    }
    
    @Override
    public PrintWriter append(final CharSequence csq) {
        return super.append(csq);
    }
    
    @Override
    public boolean checkError() {
        return super.checkError();
    }
    
    @Override
    public void close() {
        super.close();
    }
    
    @Override
    public void flush() {
        super.flush();
    }
    
    @Override
    public PrintWriter format(final Locale l, final String format, final Object... args) {
        return super.format(l, format, args);
    }
    
    @Override
    public PrintWriter format(final String format, final Object... args) {
        return super.format(format, args);
    }
    
    @Override
    public void print(final boolean b) {
        super.print(b);
    }
    
    @Override
    public void print(final char c) {
        super.print(c);
    }
    
    @Override
    public void print(final char[] s) {
        super.print(s);
    }
    
    @Override
    public void print(final double d) {
        super.print(d);
    }
    
    @Override
    public void print(final float f) {
        super.print(f);
    }
    
    @Override
    public void print(final int i) {
        super.print(i);
    }
    
    @Override
    public void print(final long l) {
        super.print(l);
    }
    
    @Override
    public void print(final Object obj) {
        super.print(obj);
    }
    
    @Override
    public void print(final String s) {
        super.print(s);
    }
    
    @Override
    public PrintWriter printf(final Locale l, final String format, final Object... args) {
        return super.printf(l, format, args);
    }
    
    @Override
    public PrintWriter printf(final String format, final Object... args) {
        return super.printf(format, args);
    }
    
    @Override
    public void println() {
        super.println();
    }
    
    @Override
    public void println(final boolean x) {
        super.println(x);
    }
    
    @Override
    public void println(final char x) {
        super.println(x);
    }
    
    @Override
    public void println(final char[] x) {
        super.println(x);
    }
    
    @Override
    public void println(final double x) {
        super.println(x);
    }
    
    @Override
    public void println(final float x) {
        super.println(x);
    }
    
    @Override
    public void println(final int x) {
        super.println(x);
    }
    
    @Override
    public void println(final long x) {
        super.println(x);
    }
    
    @Override
    public void println(final Object x) {
        super.println(x);
    }
    
    @Override
    public void println(final String x) {
        super.println(x);
    }
    
    @Override
    protected void setError() {
        super.setError();
    }
    
    @Override
    public void write(final char[] buf, final int off, final int len) {
        super.write(buf, off, len);
    }
    
    @Override
    public void write(final char[] buf) {
        super.write(buf);
    }
    
    @Override
    public void write(final int c) {
        super.write(c);
    }
    
    @Override
    public void write(final String s, final int off, final int len) {
        super.write(s, off, len);
    }
    
    @Override
    public void write(final String s) {
        super.write(s);
    }
}
