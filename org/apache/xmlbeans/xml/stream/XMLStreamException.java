package org.apache.xmlbeans.xml.stream;

import java.io.PrintWriter;
import java.io.PrintStream;
import org.apache.xmlbeans.xml.stream.utils.NestedThrowable;
import java.io.IOException;

public class XMLStreamException extends IOException implements NestedThrowable
{
    protected Throwable th;
    
    public XMLStreamException() {
    }
    
    public XMLStreamException(final String msg) {
        super(msg);
    }
    
    public XMLStreamException(final Throwable th) {
        this.th = th;
    }
    
    public XMLStreamException(final String msg, final Throwable th) {
        super(msg);
        this.th = th;
    }
    
    public Throwable getNestedException() {
        return this.getNested();
    }
    
    @Override
    public String getMessage() {
        final String msg = super.getMessage();
        if (msg == null && this.th != null) {
            return this.th.getMessage();
        }
        return msg;
    }
    
    @Override
    public Throwable getNested() {
        return this.th;
    }
    
    @Override
    public String superToString() {
        return super.toString();
    }
    
    @Override
    public void superPrintStackTrace(final PrintStream ps) {
        super.printStackTrace(ps);
    }
    
    @Override
    public void superPrintStackTrace(final PrintWriter pw) {
        super.printStackTrace(pw);
    }
    
    @Override
    public String toString() {
        return Util.toString(this);
    }
    
    @Override
    public void printStackTrace(final PrintStream s) {
        Util.printStackTrace(this, s);
    }
    
    @Override
    public void printStackTrace(final PrintWriter w) {
        Util.printStackTrace(this, w);
    }
    
    @Override
    public void printStackTrace() {
        this.printStackTrace(System.err);
    }
}
