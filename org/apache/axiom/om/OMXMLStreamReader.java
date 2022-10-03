package org.apache.axiom.om;

import javax.xml.stream.XMLStreamReader;

public interface OMXMLStreamReader extends XMLStreamReader, OMAttachmentAccessor
{
    @Deprecated
    boolean isInlineMTOM();
    
    @Deprecated
    void setInlineMTOM(final boolean p0);
}
