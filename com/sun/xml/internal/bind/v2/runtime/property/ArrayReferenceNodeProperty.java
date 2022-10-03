package com.sun.xml.internal.bind.v2.runtime.property;

import com.sun.xml.internal.bind.v2.model.core.ReferencePropertyInfo;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallingContext;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import com.sun.xml.internal.bind.v2.model.core.PropertyKind;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.WildcardLoader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Receiver;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Loader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.ChildLoader;
import org.xml.sax.SAXException;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import com.sun.xml.internal.bind.v2.runtime.reflect.ListIterator;
import javax.xml.bind.JAXBException;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import java.util.Iterator;
import com.sun.xml.internal.bind.v2.ClassFactory;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeTypeInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeElement;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimePropertyInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeReferencePropertyInfo;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.internal.bind.v2.model.core.WildcardMode;
import javax.xml.bind.annotation.DomHandler;
import com.sun.xml.internal.bind.v2.runtime.JaxBeanInfo;
import com.sun.xml.internal.bind.v2.util.QNameMap;

class ArrayReferenceNodeProperty<BeanT, ListT, ItemT> extends ArrayERProperty<BeanT, ListT, ItemT>
{
    private final QNameMap<JaxBeanInfo> expectedElements;
    private final boolean isMixed;
    private final DomHandler domHandler;
    private final WildcardMode wcMode;
    
    public ArrayReferenceNodeProperty(final JAXBContextImpl p, final RuntimeReferencePropertyInfo prop) {
        super(p, prop, prop.getXmlName(), prop.isCollectionNillable());
        this.expectedElements = new QNameMap<JaxBeanInfo>();
        for (final RuntimeElement e : prop.getElements()) {
            final JaxBeanInfo bi = p.getOrCreate(e);
            this.expectedElements.put(e.getElementName().getNamespaceURI(), e.getElementName().getLocalPart(), bi);
        }
        this.isMixed = prop.isMixed();
        if (prop.getWildcard() != null) {
            this.domHandler = ClassFactory.create(((ReferencePropertyInfo<T, Class<DomHandler>>)prop).getDOMHandler());
            this.wcMode = prop.getWildcard();
        }
        else {
            this.domHandler = null;
            this.wcMode = null;
        }
    }
    
    @Override
    protected final void serializeListBody(final BeanT o, final XMLSerializer w, final ListT list) throws IOException, XMLStreamException, SAXException {
        final ListIterator<ItemT> itr = this.lister.iterator(list, w);
        while (itr.hasNext()) {
            try {
                final ItemT item = itr.next();
                if (item == null) {
                    continue;
                }
                if (this.isMixed && item.getClass() == String.class) {
                    w.text((String)item, null);
                }
                else {
                    final JaxBeanInfo bi = w.grammar.getBeanInfo(item, true);
                    if (bi.jaxbType == Object.class && this.domHandler != null) {
                        w.writeDom(item, this.domHandler, o, this.fieldName);
                    }
                    else {
                        bi.serializeRoot(item, w);
                    }
                }
            }
            catch (final JAXBException e) {
                w.reportError(this.fieldName, e);
            }
        }
    }
    
    public void createBodyUnmarshaller(final UnmarshallerChain chain, final QNameMap<ChildLoader> loaders) {
        final int offset = chain.allocateOffset();
        final Receiver recv = new ReceiverImpl(offset);
        for (final QNameMap.Entry<JaxBeanInfo> n : this.expectedElements.entrySet()) {
            final JaxBeanInfo beanInfo = n.getValue();
            loaders.put(n.nsUri, n.localName, new ChildLoader(beanInfo.getLoader(chain.context, true), recv));
        }
        if (this.isMixed) {
            loaders.put(ArrayReferenceNodeProperty.TEXT_HANDLER, new ChildLoader(new MixedTextLoader(recv), null));
        }
        if (this.domHandler != null) {
            loaders.put(ArrayReferenceNodeProperty.CATCH_ALL, new ChildLoader(new WildcardLoader(this.domHandler, this.wcMode), recv));
        }
    }
    
    @Override
    public PropertyKind getKind() {
        return PropertyKind.REFERENCE;
    }
    
    @Override
    public Accessor getElementPropertyAccessor(final String nsUri, final String localName) {
        if (this.wrapperTagName != null) {
            if (this.wrapperTagName.equals(nsUri, localName)) {
                return this.acc;
            }
        }
        else if (this.expectedElements.containsKey(nsUri, localName)) {
            return this.acc;
        }
        return null;
    }
    
    private static final class MixedTextLoader extends Loader
    {
        private final Receiver recv;
        
        public MixedTextLoader(final Receiver recv) {
            super(true);
            this.recv = recv;
        }
        
        @Override
        public void text(final UnmarshallingContext.State state, final CharSequence text) throws SAXException {
            if (text.length() != 0) {
                this.recv.receive(state, text.toString());
            }
        }
    }
}
