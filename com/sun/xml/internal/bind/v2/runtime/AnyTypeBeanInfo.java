package com.sun.xml.internal.bind.v2.runtime;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Loader;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventLocator;
import javax.xml.bind.helpers.ValidationEventImpl;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Attr;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import org.xml.sax.SAXException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.bind.annotation.DomHandler;
import org.w3c.dom.Element;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallingContext;
import javax.xml.namespace.QName;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeTypeInfo;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiTypeLoader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.DomLoader;
import javax.xml.bind.annotation.W3CDomHandler;

final class AnyTypeBeanInfo extends JaxBeanInfo<Object> implements AttributeAccessor
{
    private boolean nilIncluded;
    private static final W3CDomHandler domHandler;
    private static final DomLoader domLoader;
    private final XsiTypeLoader substLoader;
    
    public AnyTypeBeanInfo(final JAXBContextImpl grammar, final RuntimeTypeInfo anyTypeInfo) {
        super(grammar, anyTypeInfo, Object.class, new QName("http://www.w3.org/2001/XMLSchema", "anyType"), false, true, false);
        this.nilIncluded = false;
        this.substLoader = new XsiTypeLoader(this);
    }
    
    @Override
    public String getElementNamespaceURI(final Object element) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public String getElementLocalName(final Object element) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Object createInstance(final UnmarshallingContext context) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean reset(final Object element, final UnmarshallingContext context) {
        return false;
    }
    
    @Override
    public String getId(final Object element, final XMLSerializer target) {
        return null;
    }
    
    @Override
    public void serializeBody(final Object element, final XMLSerializer target) throws SAXException, IOException, XMLStreamException {
        final NodeList childNodes = ((Element)element).getChildNodes();
        for (int len = childNodes.getLength(), i = 0; i < len; ++i) {
            final Node child = childNodes.item(i);
            switch (child.getNodeType()) {
                case 3:
                case 4: {
                    target.text(child.getNodeValue(), null);
                    break;
                }
                case 1: {
                    target.writeDom((Element)child, AnyTypeBeanInfo.domHandler, null, null);
                    break;
                }
            }
        }
    }
    
    @Override
    public void serializeAttributes(final Object element, final XMLSerializer target) throws SAXException {
        final NamedNodeMap al = ((Element)element).getAttributes();
        for (int len = al.getLength(), i = 0; i < len; ++i) {
            final Attr a = (Attr)al.item(i);
            String uri = a.getNamespaceURI();
            if (uri == null) {
                uri = "";
            }
            String local = a.getLocalName();
            final String name = a.getName();
            if (local == null) {
                local = name;
            }
            if (uri.equals("http://www.w3.org/2001/XMLSchema-instance") && "nil".equals(local)) {
                this.isNilIncluded = true;
            }
            if (!name.startsWith("xmlns")) {
                target.attribute(uri, local, a.getValue());
            }
        }
    }
    
    @Override
    public void serializeRoot(final Object element, final XMLSerializer target) throws SAXException {
        target.reportError(new ValidationEventImpl(1, Messages.UNABLE_TO_MARSHAL_NON_ELEMENT.format(element.getClass().getName()), null, null));
    }
    
    @Override
    public void serializeURIs(final Object element, final XMLSerializer target) {
        final NamedNodeMap al = ((Element)element).getAttributes();
        final int len = al.getLength();
        final NamespaceContext2 context = target.getNamespaceContext();
        for (int i = 0; i < len; ++i) {
            final Attr a = (Attr)al.item(i);
            if ("xmlns".equals(a.getPrefix())) {
                context.force(a.getValue(), a.getLocalName());
            }
            else if ("xmlns".equals(a.getName())) {
                if (element instanceof Element) {
                    context.declareNamespace(a.getValue(), null, false);
                }
                else {
                    context.force(a.getValue(), "");
                }
            }
            else {
                final String nsUri = a.getNamespaceURI();
                if (nsUri != null && nsUri.length() > 0) {
                    context.declareNamespace(nsUri, a.getPrefix(), true);
                }
            }
        }
    }
    
    @Override
    public Transducer<Object> getTransducer() {
        return null;
    }
    
    @Override
    public Loader getLoader(final JAXBContextImpl context, final boolean typeSubstitutionCapable) {
        if (typeSubstitutionCapable) {
            return this.substLoader;
        }
        return AnyTypeBeanInfo.domLoader;
    }
    
    static {
        domHandler = new W3CDomHandler();
        domLoader = new DomLoader((DomHandler<?, ResultT>)AnyTypeBeanInfo.domHandler);
    }
}
