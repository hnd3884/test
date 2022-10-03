package org.apache.xerces.xinclude;

import org.apache.xerces.util.XML11Char;
import java.io.IOException;
import org.apache.xerces.xni.parser.XMLInputSource;

public class XInclude11TextReader extends XIncludeTextReader
{
    public XInclude11TextReader(final XMLInputSource xmlInputSource, final XIncludeHandler xIncludeHandler, final int n) throws IOException {
        super(xmlInputSource, xIncludeHandler, n);
    }
    
    protected boolean isValid(final int n) {
        return XML11Char.isXML11Valid(n);
    }
}
