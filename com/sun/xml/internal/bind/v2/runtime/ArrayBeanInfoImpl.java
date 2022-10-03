package com.sun.xml.internal.bind.v2.runtime;

import java.util.Collections;
import javax.xml.namespace.QName;
import java.util.Collection;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.TagName;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Receiver;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventLocator;
import javax.xml.bind.helpers.ValidationEventImpl;
import java.util.ArrayList;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallingContext;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import org.xml.sax.SAXException;
import java.lang.reflect.Array;
import java.util.List;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeTypeInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeArrayInfo;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Loader;

final class ArrayBeanInfoImpl extends JaxBeanInfo
{
    private final Class itemType;
    private final JaxBeanInfo itemBeanInfo;
    private Loader loader;
    
    public ArrayBeanInfoImpl(final JAXBContextImpl owner, final RuntimeArrayInfo rai) {
        super(owner, rai, rai.getType(), rai.getTypeName(), false, true, false);
        this.itemType = this.jaxbType.getComponentType();
        this.itemBeanInfo = owner.getOrCreate(rai.getItemType());
    }
    
    @Override
    protected void link(final JAXBContextImpl grammar) {
        this.getLoader(grammar, false);
        super.link(grammar);
    }
    
    protected Object toArray(final List list) {
        final int len = list.size();
        final Object array = Array.newInstance(this.itemType, len);
        for (int i = 0; i < len; ++i) {
            Array.set(array, i, list.get(i));
        }
        return array;
    }
    
    @Override
    public void serializeBody(final Object array, final XMLSerializer target) throws SAXException, IOException, XMLStreamException {
        for (int len = Array.getLength(array), i = 0; i < len; ++i) {
            final Object item = Array.get(array, i);
            target.startElement("", "item", null, null);
            if (item == null) {
                target.writeXsiNilTrue();
            }
            else {
                target.childAsXsiType(item, "arrayItem", this.itemBeanInfo, false);
            }
            target.endElement();
        }
    }
    
    @Override
    public final String getElementNamespaceURI(final Object array) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public final String getElementLocalName(final Object array) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public final Object createInstance(final UnmarshallingContext context) {
        return new ArrayList();
    }
    
    @Override
    public final boolean reset(final Object array, final UnmarshallingContext context) {
        return false;
    }
    
    @Override
    public final String getId(final Object array, final XMLSerializer target) {
        return null;
    }
    
    @Override
    public final void serializeAttributes(final Object array, final XMLSerializer target) {
    }
    
    @Override
    public final void serializeRoot(final Object array, final XMLSerializer target) throws SAXException, IOException, XMLStreamException {
        target.reportError(new ValidationEventImpl(1, Messages.UNABLE_TO_MARSHAL_NON_ELEMENT.format(array.getClass().getName()), null, null));
    }
    
    @Override
    public final void serializeURIs(final Object array, final XMLSerializer target) {
    }
    
    @Override
    public final Transducer getTransducer() {
        return null;
    }
    
    @Override
    public final Loader getLoader(final JAXBContextImpl context, final boolean typeSubstitutionCapable) {
        if (this.loader == null) {
            this.loader = new ArrayLoader(context);
        }
        return this.loader;
    }
    
    private final class ArrayLoader extends Loader implements Receiver
    {
        private final Loader itemLoader;
        
        public ArrayLoader(final JAXBContextImpl owner) {
            super(false);
            this.itemLoader = ArrayBeanInfoImpl.this.itemBeanInfo.getLoader(owner, true);
        }
        
        @Override
        public void startElement(final UnmarshallingContext.State state, final TagName ea) {
            state.setTarget(new ArrayList());
        }
        
        @Override
        public void leaveElement(final UnmarshallingContext.State state, final TagName ea) {
            state.setTarget(ArrayBeanInfoImpl.this.toArray((List)state.getTarget()));
        }
        
        @Override
        public void childElement(final UnmarshallingContext.State state, final TagName ea) throws SAXException {
            if (ea.matches("", "item")) {
                state.setLoader(this.itemLoader);
                state.setReceiver(this);
            }
            else {
                super.childElement(state, ea);
            }
        }
        
        @Override
        public Collection<QName> getExpectedChildElements() {
            return Collections.singleton(new QName("", "item"));
        }
        
        @Override
        public void receive(final UnmarshallingContext.State state, final Object o) {
            ((List)state.getTarget()).add(o);
        }
    }
}
