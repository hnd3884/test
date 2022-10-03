package com.sun.org.apache.xalan.internal.xsltc.compiler;

import org.xml.sax.InputSource;

public interface SourceLoader
{
    InputSource loadSource(final String p0, final String p1, final XSLTC p2);
}
