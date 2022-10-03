package com.sun.xml.internal.bind.v2.runtime.property;

import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import com.sun.xml.internal.bind.api.AccessorException;
import org.xml.sax.SAXException;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimePropertyInfo;

abstract class PropertyImpl<BeanT> implements Property<BeanT>
{
    protected final String fieldName;
    private RuntimePropertyInfo propertyInfo;
    private boolean hiddenByOverride;
    
    public PropertyImpl(final JAXBContextImpl context, final RuntimePropertyInfo prop) {
        this.propertyInfo = null;
        this.hiddenByOverride = false;
        this.fieldName = prop.getName();
        if (context.retainPropertyInfo) {
            this.propertyInfo = prop;
        }
    }
    
    @Override
    public RuntimePropertyInfo getInfo() {
        return this.propertyInfo;
    }
    
    @Override
    public void serializeBody(final BeanT o, final XMLSerializer w, final Object outerPeer) throws SAXException, AccessorException, IOException, XMLStreamException {
    }
    
    @Override
    public void serializeURIs(final BeanT o, final XMLSerializer w) throws SAXException, AccessorException {
    }
    
    @Override
    public boolean hasSerializeURIAction() {
        return false;
    }
    
    @Override
    public Accessor getElementPropertyAccessor(final String nsUri, final String localName) {
        return null;
    }
    
    @Override
    public void wrapUp() {
    }
    
    @Override
    public boolean isHiddenByOverride() {
        return this.hiddenByOverride;
    }
    
    @Override
    public void setHiddenByOverride(final boolean hidden) {
        this.hiddenByOverride = hidden;
    }
    
    @Override
    public String getFieldName() {
        return this.fieldName;
    }
}
