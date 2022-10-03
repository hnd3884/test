package org.apache.axiom.core;

public interface Semantics
{
    DetachPolicy getDetachPolicy();
    
    boolean isUseStrictNamespaceLookup();
    
    boolean isParentNode(final NodeType p0);
    
    RuntimeException toUncheckedException(final CoreModelException p0);
}
