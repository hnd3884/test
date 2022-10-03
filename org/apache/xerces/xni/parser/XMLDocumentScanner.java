package org.apache.xerces.xni.parser;

import org.apache.xerces.xni.XNIException;
import java.io.IOException;

public interface XMLDocumentScanner extends XMLDocumentSource
{
    void setInputSource(final XMLInputSource p0) throws IOException;
    
    boolean scanDocument(final boolean p0) throws IOException, XNIException;
}
