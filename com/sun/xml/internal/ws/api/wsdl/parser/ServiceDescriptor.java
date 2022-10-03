package com.sun.xml.internal.ws.api.wsdl.parser;

import com.sun.istack.internal.NotNull;
import javax.xml.transform.Source;
import java.util.List;

public abstract class ServiceDescriptor
{
    @NotNull
    public abstract List<? extends Source> getWSDLs();
    
    @NotNull
    public abstract List<? extends Source> getSchemas();
}
