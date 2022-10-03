package com.sun.xml.internal.stream;

import com.sun.org.apache.xerces.internal.util.XMLStringBuffer;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.QName;
import java.io.IOException;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;

public abstract class XMLEntityReader implements XMLLocator
{
    public abstract void setEncoding(final String p0) throws IOException;
    
    @Override
    public abstract String getEncoding();
    
    @Override
    public abstract int getCharacterOffset();
    
    public abstract void setVersion(final String p0);
    
    public abstract String getVersion();
    
    public abstract boolean isExternal();
    
    public abstract int peekChar() throws IOException;
    
    public abstract int scanChar() throws IOException;
    
    public abstract String scanNmtoken() throws IOException;
    
    public abstract String scanName() throws IOException;
    
    public abstract boolean scanQName(final QName p0) throws IOException;
    
    public abstract int scanContent(final XMLString p0) throws IOException;
    
    public abstract int scanLiteral(final int p0, final XMLString p1) throws IOException;
    
    public abstract boolean scanData(final String p0, final XMLStringBuffer p1) throws IOException;
    
    public abstract boolean skipChar(final int p0) throws IOException;
    
    public abstract boolean skipSpaces() throws IOException;
    
    public abstract boolean skipString(final String p0) throws IOException;
    
    public abstract void registerListener(final XMLBufferListener p0);
}
