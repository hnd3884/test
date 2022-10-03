package com.sun.org.apache.xerces.internal.xinclude;

import com.sun.org.apache.xerces.internal.util.XML11Char;
import java.io.IOException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;

public class XInclude11TextReader extends XIncludeTextReader
{
    public XInclude11TextReader(final XMLInputSource source, final XIncludeHandler handler, final int bufferSize) throws IOException {
        super(source, handler, bufferSize);
    }
    
    @Override
    protected boolean isValid(final int ch) {
        return XML11Char.isXML11Valid(ch);
    }
}
