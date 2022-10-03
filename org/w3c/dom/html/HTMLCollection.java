package org.w3c.dom.html;

import org.w3c.dom.Node;

public interface HTMLCollection
{
    int getLength();
    
    Node item(final int p0);
    
    Node namedItem(final String p0);
}
