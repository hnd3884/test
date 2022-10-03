package com.sun.corba.se.impl.encoding;

interface RestorableInputStream
{
    Object createStreamMemento();
    
    void restoreInternalState(final Object p0);
}
