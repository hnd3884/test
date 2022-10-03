package com.sun.xml.internal.messaging.saaj.packaging.mime;

public class MessagingException extends Exception
{
    private Exception next;
    
    public MessagingException() {
    }
    
    public MessagingException(final String s) {
        super(s);
    }
    
    public MessagingException(final String s, final Exception e) {
        super(s);
        this.next = e;
    }
    
    public Exception getNextException() {
        return this.next;
    }
    
    public synchronized boolean setNextException(final Exception ex) {
        Exception theEnd;
        for (theEnd = this; theEnd instanceof MessagingException && ((MessagingException)theEnd).next != null; theEnd = ((MessagingException)theEnd).next) {}
        if (theEnd instanceof MessagingException) {
            ((MessagingException)theEnd).next = ex;
            return true;
        }
        return false;
    }
    
    @Override
    public String getMessage() {
        if (this.next == null) {
            return super.getMessage();
        }
        Exception n = this.next;
        final String s = super.getMessage();
        final StringBuffer sb = new StringBuffer((s == null) ? "" : s);
        while (n != null) {
            sb.append(";\n  nested exception is:\n\t");
            if (n instanceof MessagingException) {
                final MessagingException mex = (MessagingException)n;
                sb.append(n.getClass().toString());
                final String msg = mex.getSuperMessage();
                if (msg != null) {
                    sb.append(": ");
                    sb.append(msg);
                }
                n = mex.next;
            }
            else {
                sb.append(n.toString());
                n = null;
            }
        }
        return sb.toString();
    }
    
    private String getSuperMessage() {
        return super.getMessage();
    }
}
