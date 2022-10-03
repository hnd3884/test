package org.apache.axiom.core;

import org.apache.axiom.om.OMXMLParserWrapper;

public interface DeferringParentNode extends CoreParentNode
{
    void coreSetBuilder(final OMXMLParserWrapper p0);
    
    OMXMLParserWrapper getBuilder();
}
