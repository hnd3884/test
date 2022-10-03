package org.htmlparser.util;

import java.io.PrintWriter;
import java.io.PrintStream;
import java.util.Vector;

public class ChainedException extends Exception
{
    protected Throwable throwable;
    
    public ChainedException() {
    }
    
    public ChainedException(final String message) {
        super(message);
    }
    
    public ChainedException(final Throwable throwable) {
        this.throwable = throwable;
    }
    
    public ChainedException(final String message, final Throwable throwable) {
        super(message);
        this.throwable = throwable;
    }
    
    public String[] getMessageChain() {
        final Vector list = this.getMessageList();
        final String[] chain = new String[list.size()];
        list.copyInto(chain);
        return chain;
    }
    
    public Vector getMessageList() {
        final Vector list = new Vector();
        list.addElement(this.getMessage());
        if (this.throwable != null) {
            if (this.throwable instanceof ChainedException) {
                final ChainedException chain = (ChainedException)this.throwable;
                final Vector sublist = chain.getMessageList();
                for (int i = 0; i < sublist.size(); ++i) {
                    list.addElement(sublist.elementAt(i));
                }
            }
            else {
                final String message = this.throwable.getMessage();
                if (message != null && !message.equals("")) {
                    list.addElement(message);
                }
            }
        }
        return list;
    }
    
    public Throwable getThrowable() {
        return this.throwable;
    }
    
    public void printStackTrace() {
        this.printStackTrace(System.err);
    }
    
    public void printStackTrace(final PrintStream out) {
        synchronized (out) {
            if (this.throwable != null) {
                out.println(this.getClass().getName() + ": " + this.getMessage() + ";");
                this.throwable.printStackTrace(out);
            }
            else {
                super.printStackTrace(out);
            }
        }
    }
    
    public void printStackTrace(final PrintWriter out) {
        synchronized (out) {
            if (this.throwable != null) {
                out.println(this.getClass().getName() + ": " + this.getMessage() + ";");
                this.throwable.printStackTrace(out);
            }
            else {
                super.printStackTrace(out);
            }
        }
    }
}
