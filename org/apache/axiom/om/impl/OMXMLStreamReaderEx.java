package org.apache.axiom.om.impl;

import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.OMXMLStreamReader;

public interface OMXMLStreamReaderEx extends OMXMLStreamReader
{
    void enableDataSourceEvents(final boolean p0);
    
    OMDataSource getDataSource();
}
