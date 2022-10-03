package com.sun.xml.internal.bind.v2.runtime;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventLocator;
import javax.xml.bind.helpers.ValidationEventImpl;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Loader;
import org.xml.sax.SAXException;
import java.lang.reflect.InvocationTargetException;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallingContext;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeTypeInfo;
import com.sun.xml.internal.bind.api.CompositeStructure;

public class CompositeStructureBeanInfo extends JaxBeanInfo<CompositeStructure>
{
    public CompositeStructureBeanInfo(final JAXBContextImpl context) {
        super(context, null, CompositeStructure.class, false, true, false);
    }
    
    @Override
    public String getElementNamespaceURI(final CompositeStructure o) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public String getElementLocalName(final CompositeStructure o) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public CompositeStructure createInstance(final UnmarshallingContext context) throws IllegalAccessException, InvocationTargetException, InstantiationException, SAXException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean reset(final CompositeStructure o, final UnmarshallingContext context) throws SAXException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public String getId(final CompositeStructure o, final XMLSerializer target) throws SAXException {
        return null;
    }
    
    @Override
    public Loader getLoader(final JAXBContextImpl context, final boolean typeSubstitutionCapable) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void serializeRoot(final CompositeStructure o, final XMLSerializer target) throws SAXException, IOException, XMLStreamException {
        target.reportError(new ValidationEventImpl(1, Messages.UNABLE_TO_MARSHAL_NON_ELEMENT.format(o.getClass().getName()), null, null));
    }
    
    @Override
    public void serializeURIs(final CompositeStructure o, final XMLSerializer target) throws SAXException {
    }
    
    @Override
    public void serializeAttributes(final CompositeStructure o, final XMLSerializer target) throws SAXException, IOException, XMLStreamException {
    }
    
    @Override
    public void serializeBody(final CompositeStructure o, final XMLSerializer target) throws SAXException, IOException, XMLStreamException {
        for (int len = o.bridges.length, i = 0; i < len; ++i) {
            final Object value = o.values[i];
            final InternalBridge bi = (InternalBridge)o.bridges[i];
            bi.marshal(value, target);
        }
    }
    
    @Override
    public Transducer<CompositeStructure> getTransducer() {
        return null;
    }
}
