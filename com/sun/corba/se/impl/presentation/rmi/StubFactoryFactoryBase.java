package com.sun.corba.se.impl.presentation.rmi;

import com.sun.corba.se.impl.util.Utility;
import com.sun.corba.se.spi.presentation.rmi.PresentationManager;

public abstract class StubFactoryFactoryBase implements PresentationManager.StubFactoryFactory
{
    @Override
    public String getStubName(final String s) {
        return Utility.stubName(s);
    }
}
