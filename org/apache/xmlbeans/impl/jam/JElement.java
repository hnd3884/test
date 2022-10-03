package org.apache.xmlbeans.impl.jam;

import org.apache.xmlbeans.impl.jam.visitor.JVisitor;

public interface JElement
{
    JElement getParent();
    
    String getSimpleName();
    
    String getQualifiedName();
    
    JSourcePosition getSourcePosition();
    
    void accept(final JVisitor p0);
    
    Object getArtifact();
    
    String toString();
}
