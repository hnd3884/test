package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

public final class ChildLoader
{
    public final Loader loader;
    public final Receiver receiver;
    
    public ChildLoader(final Loader loader, final Receiver receiver) {
        assert loader != null;
        this.loader = loader;
        this.receiver = receiver;
    }
}
