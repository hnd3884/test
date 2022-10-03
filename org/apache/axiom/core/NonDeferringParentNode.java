package org.apache.axiom.core;

import org.apache.axiom.om.OMXMLParserWrapper;

public interface NonDeferringParentNode extends CoreParentNode
{
    void build();
    
    void coreSetBuilder(final OMXMLParserWrapper p0);
    
    OMXMLParserWrapper getBuilder();
}
