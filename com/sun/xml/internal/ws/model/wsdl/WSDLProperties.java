package com.sun.xml.internal.ws.model.wsdl;

import org.xml.sax.InputSource;
import com.oracle.webservices.internal.api.message.PropertySet;
import javax.xml.namespace.QName;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.oracle.webservices.internal.api.message.BasePropertySet;

public abstract class WSDLProperties extends BasePropertySet
{
    private static final PropertyMap model;
    @Nullable
    private final SEIModel seiModel;
    
    protected WSDLProperties(@Nullable final SEIModel seiModel) {
        this.seiModel = seiModel;
    }
    
    @PropertySet.Property({ "javax.xml.ws.wsdl.service" })
    public abstract QName getWSDLService();
    
    @PropertySet.Property({ "javax.xml.ws.wsdl.port" })
    public abstract QName getWSDLPort();
    
    @PropertySet.Property({ "javax.xml.ws.wsdl.interface" })
    public abstract QName getWSDLPortType();
    
    @PropertySet.Property({ "javax.xml.ws.wsdl.description" })
    public InputSource getWSDLDescription() {
        return (this.seiModel != null) ? new InputSource(this.seiModel.getWSDLLocation()) : null;
    }
    
    @Override
    protected PropertyMap getPropertyMap() {
        return WSDLProperties.model;
    }
    
    static {
        model = BasePropertySet.parse(WSDLProperties.class);
    }
}
