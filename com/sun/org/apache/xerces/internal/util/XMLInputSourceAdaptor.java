package com.sun.org.apache.xerces.internal.util;

import com.sun.org.apache.xerces.internal.impl.XMLEntityManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import javax.xml.transform.Source;

public final class XMLInputSourceAdaptor implements Source
{
    public final XMLInputSource fSource;
    
    public XMLInputSourceAdaptor(final XMLInputSource core) {
        this.fSource = core;
    }
    
    @Override
    public void setSystemId(final String systemId) {
        this.fSource.setSystemId(systemId);
    }
    
    @Override
    public String getSystemId() {
        try {
            return XMLEntityManager.expandSystemId(this.fSource.getSystemId(), this.fSource.getBaseSystemId(), false);
        }
        catch (final URI.MalformedURIException e) {
            return this.fSource.getSystemId();
        }
    }
}
