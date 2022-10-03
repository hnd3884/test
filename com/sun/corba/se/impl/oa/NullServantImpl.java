package com.sun.corba.se.impl.oa;

import org.omg.CORBA.SystemException;
import com.sun.corba.se.spi.oa.NullServant;

public class NullServantImpl implements NullServant
{
    private SystemException sysex;
    
    public NullServantImpl(final SystemException sysex) {
        this.sysex = sysex;
    }
    
    @Override
    public SystemException getException() {
        return this.sysex;
    }
}
