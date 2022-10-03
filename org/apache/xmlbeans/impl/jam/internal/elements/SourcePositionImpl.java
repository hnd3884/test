package org.apache.xmlbeans.impl.jam.internal.elements;

import java.net.URI;
import org.apache.xmlbeans.impl.jam.mutable.MSourcePosition;

public final class SourcePositionImpl implements MSourcePosition
{
    private int mColumn;
    private int mLine;
    private URI mURI;
    
    SourcePositionImpl() {
        this.mColumn = -1;
        this.mLine = -1;
        this.mURI = null;
    }
    
    @Override
    public void setColumn(final int col) {
        this.mColumn = col;
    }
    
    @Override
    public void setLine(final int line) {
        this.mLine = line;
    }
    
    @Override
    public void setSourceURI(final URI uri) {
        this.mURI = uri;
    }
    
    @Override
    public int getColumn() {
        return this.mColumn;
    }
    
    @Override
    public int getLine() {
        return this.mLine;
    }
    
    @Override
    public URI getSourceURI() {
        return this.mURI;
    }
}
