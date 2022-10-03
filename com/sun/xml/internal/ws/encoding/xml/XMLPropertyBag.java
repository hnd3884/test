package com.sun.xml.internal.ws.encoding.xml;

import com.oracle.webservices.internal.api.message.PropertySet;
import com.oracle.webservices.internal.api.message.BasePropertySet;

public class XMLPropertyBag extends BasePropertySet
{
    private String contentType;
    private static final PropertyMap model;
    
    @Override
    protected PropertyMap getPropertyMap() {
        return XMLPropertyBag.model;
    }
    
    @PropertySet.Property({ "com.sun.jaxws.rest.contenttype" })
    public String getXMLContentType() {
        return this.contentType;
    }
    
    public void setXMLContentType(final String content) {
        this.contentType = content;
    }
    
    static {
        model = BasePropertySet.parse(XMLPropertyBag.class);
    }
}
