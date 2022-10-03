package org.apache.axiom.core;

public interface NodeFactory
{
     <T extends CoreNode> T createNode(final Class<T> p0);
}
