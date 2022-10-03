package org.apache.axiom.core;

public interface CoreNSUnawareNamedNode extends CoreNamedNode
{
    String coreGetName();
    
    void coreSetName(final String p0);
    
    void initName(final CoreNamedNode p0);
}
