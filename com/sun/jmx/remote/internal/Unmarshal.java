package com.sun.jmx.remote.internal;

import java.io.IOException;
import java.rmi.MarshalledObject;

public interface Unmarshal
{
    Object get(final MarshalledObject<?> p0) throws IOException, ClassNotFoundException;
}
