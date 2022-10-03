package com.oracle.webservices.internal.api.databinding;

import javax.xml.ws.Holder;
import javax.xml.transform.Result;

public interface WSDLResolver
{
    Result getWSDL(final String p0);
    
    Result getAbstractWSDL(final Holder<String> p0);
    
    Result getSchemaOutput(final String p0, final Holder<String> p1);
}
