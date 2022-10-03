package com.sun.org.apache.xml.internal.serialize;

import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Document;
import java.io.IOException;
import org.w3c.dom.Element;

public interface DOMSerializer
{
    void serialize(final Element p0) throws IOException;
    
    void serialize(final Document p0) throws IOException;
    
    void serialize(final DocumentFragment p0) throws IOException;
}
