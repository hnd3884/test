package com.sun.org.apache.xml.internal.serializer;

import java.io.IOException;
import org.w3c.dom.Node;

public interface DOMSerializer
{
    void serialize(final Node p0) throws IOException;
}
