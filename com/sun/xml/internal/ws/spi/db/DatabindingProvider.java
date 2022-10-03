package com.sun.xml.internal.ws.spi.db;

import com.oracle.webservices.internal.api.databinding.WSDLGenerator;
import com.oracle.webservices.internal.api.databinding.Databinding;
import com.sun.xml.internal.ws.api.databinding.DatabindingConfig;
import java.util.Map;

public interface DatabindingProvider
{
    boolean isFor(final String p0);
    
    void init(final Map<String, Object> p0);
    
    Databinding create(final DatabindingConfig p0);
    
    WSDLGenerator wsdlGen(final DatabindingConfig p0);
}
