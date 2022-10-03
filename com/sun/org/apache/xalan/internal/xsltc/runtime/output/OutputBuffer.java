package com.sun.org.apache.xalan.internal.xsltc.runtime.output;

interface OutputBuffer
{
    String close();
    
    OutputBuffer append(final char p0);
    
    OutputBuffer append(final String p0);
    
    OutputBuffer append(final char[] p0, final int p1, final int p2);
}
