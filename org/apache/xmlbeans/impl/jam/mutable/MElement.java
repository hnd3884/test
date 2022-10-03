package org.apache.xmlbeans.impl.jam.mutable;

import org.apache.xmlbeans.impl.jam.visitor.MVisitor;
import org.apache.xmlbeans.impl.jam.JamClassLoader;
import org.apache.xmlbeans.impl.jam.JElement;

public interface MElement extends JElement
{
    JamClassLoader getClassLoader();
    
    void setSimpleName(final String p0);
    
    MSourcePosition createSourcePosition();
    
    void removeSourcePosition();
    
    MSourcePosition getMutableSourcePosition();
    
    void accept(final MVisitor p0);
    
    void setArtifact(final Object p0);
}
