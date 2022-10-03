package com.sun.org.apache.xerces.internal.xni.parser;

import com.sun.org.apache.xerces.internal.xni.XNIException;
import java.io.IOException;

public interface XMLDocumentScanner extends XMLDocumentSource
{
    void setInputSource(final XMLInputSource p0) throws IOException;
    
    boolean scanDocument(final boolean p0) throws IOException, XNIException;
    
    int next() throws XNIException, IOException;
}
