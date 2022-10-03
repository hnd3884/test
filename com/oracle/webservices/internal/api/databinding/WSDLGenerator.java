package com.oracle.webservices.internal.api.databinding;

import java.io.File;

public interface WSDLGenerator
{
    WSDLGenerator inlineSchema(final boolean p0);
    
    WSDLGenerator property(final String p0, final Object p1);
    
    void generate(final WSDLResolver p0);
    
    void generate(final File p0, final String p1);
}
