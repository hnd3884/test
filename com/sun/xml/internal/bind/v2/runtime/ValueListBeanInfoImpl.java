package com.sun.xml.internal.bind.v2.runtime;

import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventLocator;
import javax.xml.bind.helpers.ValidationEventImpl;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.lang.reflect.Array;
import javax.xml.bind.JAXBException;
import org.xml.sax.SAXException;
import java.util.List;
import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.WhiteSpaceProcessor;
import com.sun.istack.internal.FinalArrayList;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallingContext;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeTypeInfo;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Loader;

final class ValueListBeanInfoImpl extends JaxBeanInfo
{
    private final Class itemType;
    private final Transducer xducer;
    private final Loader loader;
    
    public ValueListBeanInfoImpl(final JAXBContextImpl owner, final Class arrayType) throws JAXBException {
        super(owner, null, arrayType, false, true, false);
        this.loader = new Loader(true) {
            @Override
            public void text(final UnmarshallingContext.State state, final CharSequence text) throws SAXException {
                final List<Object> r = new FinalArrayList<Object>();
                int idx = 0;
                final int len = text.length();
                while (true) {
                    int p;
                    for (p = idx; p < len && !WhiteSpaceProcessor.isWhiteSpace(text.charAt(p)); ++p) {}
                    final CharSequence token = text.subSequence(idx, p);
                    if (!token.equals("")) {
                        try {
                            r.add(ValueListBeanInfoImpl.this.xducer.parse(token));
                        }
                        catch (final AccessorException e) {
                            Loader.handleGenericException(e, true);
                            continue;
                        }
                    }
                    if (p == len) {
                        break;
                    }
                    while (p < len && WhiteSpaceProcessor.isWhiteSpace(text.charAt(p))) {
                        ++p;
                    }
                    if (p == len) {
                        break;
                    }
                    idx = p;
                }
                state.setTarget(ValueListBeanInfoImpl.this.toArray(r));
            }
        };
        this.itemType = this.jaxbType.getComponentType();
        this.xducer = owner.getBeanInfo(arrayType.getComponentType(), true).getTransducer();
        assert this.xducer != null;
    }
    
    private Object toArray(final List list) {
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
            try {
                this.xducer.writeText(target, item, "arrayItem");
            }
            catch (final AccessorException e) {
                target.reportError("arrayItem", e);
            }
        }
    }
    
    @Override
    public final void serializeURIs(final Object array, final XMLSerializer target) throws SAXException {
        if (this.xducer.useNamespace()) {
            for (int len = Array.getLength(array), i = 0; i < len; ++i) {
                final Object item = Array.get(array, i);
                try {
                    this.xducer.declareNamespace(item, target);
                }
                catch (final AccessorException e) {
                    target.reportError("arrayItem", e);
                }
            }
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
        throw new UnsupportedOperationException();
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
    public final void serializeRoot(final Object array, final XMLSerializer target) throws SAXException {
        target.reportError(new ValidationEventImpl(1, Messages.UNABLE_TO_MARSHAL_NON_ELEMENT.format(array.getClass().getName()), null, null));
    }
    
    @Override
    public final Transducer getTransducer() {
        return null;
    }
    
    @Override
    public final Loader getLoader(final JAXBContextImpl context, final boolean typeSubstitutionCapable) {
        return this.loader;
    }
}
