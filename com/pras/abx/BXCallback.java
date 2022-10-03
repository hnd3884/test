package com.pras.abx;

public interface BXCallback
{
    void startDoc(final String p0);
    
    void startNode(final Node p0);
    
    void nodeValue(final int p0, final String p1, final String p2);
    
    void endNode(final Node p0);
    
    void endDoc() throws Exception;
}
