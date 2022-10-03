package org.htmlparser.lexer;

import java.io.IOException;
import org.htmlparser.util.ParserException;
import java.io.Serializable;
import java.io.Reader;

public abstract class Source extends Reader implements Serializable
{
    public static final int EOF = -1;
    
    public abstract String getEncoding();
    
    public abstract void setEncoding(final String p0) throws ParserException;
    
    public abstract void close() throws IOException;
    
    public abstract int read() throws IOException;
    
    public abstract int read(final char[] p0, final int p1, final int p2) throws IOException;
    
    public abstract int read(final char[] p0) throws IOException;
    
    public abstract boolean ready() throws IOException;
    
    public abstract void reset();
    
    public abstract boolean markSupported();
    
    public abstract void mark(final int p0) throws IOException;
    
    public abstract long skip(final long p0) throws IOException;
    
    public abstract void unread() throws IOException;
    
    public abstract char getCharacter(final int p0) throws IOException;
    
    public abstract void getCharacters(final char[] p0, final int p1, final int p2, final int p3) throws IOException;
    
    public abstract String getString(final int p0, final int p1) throws IOException;
    
    public abstract void getCharacters(final StringBuffer p0, final int p1, final int p2) throws IOException;
    
    public abstract void destroy() throws IOException;
    
    public abstract int offset();
    
    public abstract int available();
}
