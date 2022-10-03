package org.apache.xmlbeans.impl.jam.mutable;

import java.net.URI;
import org.apache.xmlbeans.impl.jam.JSourcePosition;

public interface MSourcePosition extends JSourcePosition
{
    void setColumn(final int p0);
    
    void setLine(final int p0);
    
    void setSourceURI(final URI p0);
}
