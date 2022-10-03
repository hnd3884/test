package org.apache.axiom.core;

public interface ClonePolicy<T>
{
    Class<? extends CoreNode> getTargetNodeClass(final T p0, final CoreNode p1);
    
    boolean repairNamespaces(final T p0);
    
    boolean cloneAttributes(final T p0);
    
    boolean cloneChildren(final T p0, final NodeType p1);
    
    void postProcess(final T p0, final CoreNode p1);
}
