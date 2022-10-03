package com.adventnet.webclient.util;

import javax.xml.transform.TransformerException;
import javax.xml.transform.ErrorListener;

public class ErrorListenerImpl implements ErrorListener
{
    public void warning(final TransformerException e) throws TransformerException {
        System.err.println("TRANSFORMATION WARNING!");
        System.err.println(e.getMessageAndLocation());
        throw e;
    }
    
    public void error(final TransformerException e) throws TransformerException {
        System.err.println("TRANSFORMATION ERROR!");
        System.err.println(e.getMessageAndLocation());
        throw e;
    }
    
    public void fatalError(final TransformerException e) throws TransformerException {
        System.err.println("TRANSFORMATION FATAL ERROR!!");
        System.err.println(e.getMessageAndLocation());
        throw e;
    }
}
