package com.sun.xml.internal.bind.v2.runtime;

import javax.activation.DataHandler;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public class SwaRefAdapterMarker extends XmlAdapter<String, DataHandler>
{
    @Override
    public DataHandler unmarshal(final String v) throws Exception {
        throw new IllegalStateException("Not implemented");
    }
    
    @Override
    public String marshal(final DataHandler v) throws Exception {
        throw new IllegalStateException("Not implemented");
    }
}
