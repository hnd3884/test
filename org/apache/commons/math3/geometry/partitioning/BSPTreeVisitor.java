package org.apache.commons.math3.geometry.partitioning;

import org.apache.commons.math3.geometry.Space;

public interface BSPTreeVisitor<S extends Space>
{
    Order visitOrder(final BSPTree<S> p0);
    
    void visitInternalNode(final BSPTree<S> p0);
    
    void visitLeafNode(final BSPTree<S> p0);
    
    public enum Order
    {
        PLUS_MINUS_SUB, 
        PLUS_SUB_MINUS, 
        MINUS_PLUS_SUB, 
        MINUS_SUB_PLUS, 
        SUB_PLUS_MINUS, 
        SUB_MINUS_PLUS;
    }
}
