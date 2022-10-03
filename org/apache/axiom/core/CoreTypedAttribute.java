package org.apache.axiom.core;

public interface CoreTypedAttribute extends CoreAttribute, CoreNamedNode
{
    String coreGetType();
    
    void coreSetType(final String p0);
    
     <T> void init(final ClonePolicy<T> p0, final T p1, final CoreNode p2);
}
