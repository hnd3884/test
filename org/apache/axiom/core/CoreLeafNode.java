package org.apache.axiom.core;

public interface CoreLeafNode extends CoreChildNode
{
     <T> void cloneChildrenIfNecessary(final ClonePolicy<T> p0, final T p1, final CoreNode p2);
}
