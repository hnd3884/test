package com.sun.corba.se.impl.orbutil.graph;

import java.util.Set;

public interface Graph extends Set
{
    NodeData getNodeData(final Node p0);
    
    Set getRoots();
}
