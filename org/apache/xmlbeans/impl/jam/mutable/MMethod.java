package org.apache.xmlbeans.impl.jam.mutable;

import org.apache.xmlbeans.impl.jam.JClass;
import org.apache.xmlbeans.impl.jam.JMethod;

public interface MMethod extends JMethod, MInvokable
{
    void setReturnType(final String p0);
    
    void setUnqualifiedReturnType(final String p0);
    
    void setReturnType(final JClass p0);
}
