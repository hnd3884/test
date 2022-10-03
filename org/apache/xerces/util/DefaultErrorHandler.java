package org.apache.xerces.util;

import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLParseException;
import java.io.OutputStream;
import java.io.PrintWriter;
import org.apache.xerces.xni.parser.XMLErrorHandler;

public class DefaultErrorHandler implements XMLErrorHandler
{
    protected PrintWriter fOut;
    
    public DefaultErrorHandler() {
        this(new PrintWriter(System.err));
    }
    
    public DefaultErrorHandler(final PrintWriter fOut) {
        this.fOut = fOut;
    }
    
    public void warning(final String s, final String s2, final XMLParseException ex) throws XNIException {
        this.printError("Warning", ex);
    }
    
    public void error(final String s, final String s2, final XMLParseException ex) throws XNIException {
        this.printError("Error", ex);
    }
    
    public void fatalError(final String s, final String s2, final XMLParseException ex) throws XNIException {
        this.printError("Fatal Error", ex);
        throw ex;
    }
    
    private void printError(final String s, final XMLParseException ex) {
        this.fOut.print("[");
        this.fOut.print(s);
        this.fOut.print("] ");
        String s2 = ex.getExpandedSystemId();
        if (s2 != null) {
            final int lastIndex = s2.lastIndexOf(47);
            if (lastIndex != -1) {
                s2 = s2.substring(lastIndex + 1);
            }
            this.fOut.print(s2);
        }
        this.fOut.print(':');
        this.fOut.print(ex.getLineNumber());
        this.fOut.print(':');
        this.fOut.print(ex.getColumnNumber());
        this.fOut.print(": ");
        this.fOut.print(ex.getMessage());
        this.fOut.println();
        this.fOut.flush();
    }
}
