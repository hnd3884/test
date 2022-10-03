package com.sun.org.apache.xerces.internal.xni.parser;

import com.sun.org.apache.xerces.internal.utils.XMLLimitAnalyzer;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import java.io.IOException;

public interface XMLDTDScanner extends XMLDTDSource, XMLDTDContentModelSource
{
    void setInputSource(final XMLInputSource p0) throws IOException;
    
    boolean scanDTDInternalSubset(final boolean p0, final boolean p1, final boolean p2) throws IOException, XNIException;
    
    boolean scanDTDExternalSubset(final boolean p0) throws IOException, XNIException;
    
    boolean skipDTD(final boolean p0) throws IOException;
    
    void setLimitAnalyzer(final XMLLimitAnalyzer p0);
}
