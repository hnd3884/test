package com.sun.xml.internal.ws.wsdl.writer;

import com.sun.istack.internal.Nullable;

public interface DocumentLocationResolver
{
    @Nullable
    String getLocationFor(final String p0, final String p1);
}
