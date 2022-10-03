package org.apache.xmlbeans.impl.jam.mutable;

import org.apache.xmlbeans.impl.jam.JClass;
import org.apache.xmlbeans.impl.jam.JInvokable;

public interface MInvokable extends JInvokable, MMember
{
    void addException(final String p0);
    
    void addException(final JClass p0);
    
    void removeException(final String p0);
    
    void removeException(final JClass p0);
    
    MParameter addNewParameter();
    
    void removeParameter(final MParameter p0);
    
    MParameter[] getMutableParameters();
}
