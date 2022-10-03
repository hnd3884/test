package org.apache.xmlbeans.impl.jam.mutable;

import org.apache.xmlbeans.impl.jam.JClass;
import org.apache.xmlbeans.impl.jam.JParameter;

public interface MParameter extends JParameter, MMember
{
    void setType(final String p0);
    
    void setType(final JClass p0);
    
    void setUnqualifiedType(final String p0);
}
