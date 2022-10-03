package org.apache.xerces.xni.parser;

import org.apache.xerces.xni.XNIException;
import java.io.IOException;

public interface XMLDTDScanner extends XMLDTDSource, XMLDTDContentModelSource
{
    void setInputSource(final XMLInputSource p0) throws IOException;
    
    boolean scanDTDInternalSubset(final boolean p0, final boolean p1, final boolean p2) throws IOException, XNIException;
    
    boolean scanDTDExternalSubset(final boolean p0) throws IOException, XNIException;
}
