package com.sun.xml.internal.bind.v2.runtime.property;

import java.util.LinkedHashMap;
import java.util.TreeMap;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.Iterator;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.ChildLoader;
import com.sun.xml.internal.bind.v2.util.QNameMap;
import com.sun.xml.internal.bind.v2.model.core.PropertyKind;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeTypeInfo;
import java.util.Arrays;
import java.util.Collections;
import javax.xml.namespace.QName;
import java.util.Collection;
import org.xml.sax.SAXException;
import com.sun.xml.internal.bind.api.AccessorException;
import java.util.HashMap;
import com.sun.xml.internal.bind.v2.ClassFactory;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.TagName;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallingContext;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimePropertyInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeMapPropertyInfo;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Receiver;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Loader;
import com.sun.xml.internal.bind.v2.runtime.JaxBeanInfo;
import com.sun.xml.internal.bind.v2.runtime.Name;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import java.util.Map;

final class SingleMapNodeProperty<BeanT, ValueT extends Map> extends PropertyImpl<BeanT>
{
    private final Accessor<BeanT, ValueT> acc;
    private final Name tagName;
    private final Name entryTag;
    private final Name keyTag;
    private final Name valueTag;
    private final boolean nillable;
    private JaxBeanInfo keyBeanInfo;
    private JaxBeanInfo valueBeanInfo;
    private final Class<? extends ValueT> mapImplClass;
    private static final Class[] knownImplClasses;
    private Loader keyLoader;
    private Loader valueLoader;
    private final Loader itemsLoader;
    private final Loader entryLoader;
    private static final Receiver keyReceiver;
    private static final Receiver valueReceiver;
    
    public SingleMapNodeProperty(final JAXBContextImpl context, final RuntimeMapPropertyInfo prop) {
        super(context, prop);
        this.itemsLoader = new Loader(false) {
            private ThreadLocal<BeanT> target = new ThreadLocal<BeanT>();
            private ThreadLocal<ValueT> map = new ThreadLocal<ValueT>();
            private int depthCounter = 0;
            
            @Override
            public void startElement(final UnmarshallingContext.State state, final TagName ea) throws SAXException {
                try {
                    this.target.set((BeanT)state.getPrev().getTarget());
                    this.map.set(SingleMapNodeProperty.this.acc.get(this.target.get()));
                    ++this.depthCounter;
                    if (this.map.get() == null) {
                        this.map.set(ClassFactory.create((Class<ValueT>)SingleMapNodeProperty.this.mapImplClass));
                    }
                    this.map.get().clear();
                    state.setTarget(this.map.get());
                }
                catch (final AccessorException e) {
                    Loader.handleGenericException(e, true);
                    state.setTarget(new HashMap());
                }
            }
            
            @Override
            public void leaveElement(final UnmarshallingContext.State state, final TagName ea) throws SAXException {
                super.leaveElement(state, ea);
                try {
                    SingleMapNodeProperty.this.acc.set(this.target.get(), this.map.get());
                    final int depthCounter = this.depthCounter - 1;
                    this.depthCounter = depthCounter;
                    if (depthCounter == 0) {
                        this.target.remove();
                        this.map.remove();
                    }
                }
                catch (final AccessorException ex) {
                    Loader.handleGenericException(ex, true);
                }
            }
            
            @Override
            public void childElement(final UnmarshallingContext.State state, final TagName ea) throws SAXException {
                if (ea.matches(SingleMapNodeProperty.this.entryTag)) {
                    state.setLoader(SingleMapNodeProperty.this.entryLoader);
                }
                else {
                    super.childElement(state, ea);
                }
            }
            
            @Override
            public Collection<QName> getExpectedChildElements() {
                return Collections.singleton(SingleMapNodeProperty.this.entryTag.toQName());
            }
        };
        this.entryLoader = new Loader(false) {
            @Override
            public void startElement(final UnmarshallingContext.State state, final TagName ea) {
                state.setTarget(new Object[2]);
            }
            
            @Override
            public void leaveElement(final UnmarshallingContext.State state, final TagName ea) {
                final Object[] keyValue = (Object[])state.getTarget();
                final Map map = (Map)state.getPrev().getTarget();
                map.put(keyValue[0], keyValue[1]);
            }
            
            @Override
            public void childElement(final UnmarshallingContext.State state, final TagName ea) throws SAXException {
                if (ea.matches(SingleMapNodeProperty.this.keyTag)) {
                    state.setLoader(SingleMapNodeProperty.this.keyLoader);
                    state.setReceiver(SingleMapNodeProperty.keyReceiver);
                    return;
                }
                if (ea.matches(SingleMapNodeProperty.this.valueTag)) {
                    state.setLoader(SingleMapNodeProperty.this.valueLoader);
                    state.setReceiver(SingleMapNodeProperty.valueReceiver);
                    return;
                }
                super.childElement(state, ea);
            }
            
            @Override
            public Collection<QName> getExpectedChildElements() {
                return Arrays.asList(SingleMapNodeProperty.this.keyTag.toQName(), SingleMapNodeProperty.this.valueTag.toQName());
            }
        };
        this.acc = prop.getAccessor().optimize(context);
        this.tagName = context.nameBuilder.createElementName(prop.getXmlName());
        this.entryTag = context.nameBuilder.createElementName("", "entry");
        this.keyTag = context.nameBuilder.createElementName("", "key");
        this.valueTag = context.nameBuilder.createElementName("", "value");
        this.nillable = prop.isCollectionNillable();
        this.keyBeanInfo = context.getOrCreate(prop.getKeyType());
        this.valueBeanInfo = context.getOrCreate(prop.getValueType());
        final Class<ValueT> sig = Utils.REFLECTION_NAVIGATOR.erasure(prop.getRawType());
        this.mapImplClass = ClassFactory.inferImplClass(sig, SingleMapNodeProperty.knownImplClasses);
    }
    
    @Override
    public void reset(final BeanT bean) throws AccessorException {
        this.acc.set(bean, null);
    }
    
    @Override
    public String getIdValue(final BeanT bean) {
        return null;
    }
    
    @Override
    public PropertyKind getKind() {
        return PropertyKind.MAP;
    }
    
    @Override
    public void buildChildElementUnmarshallers(final UnmarshallerChain chain, final QNameMap<ChildLoader> handlers) {
        this.keyLoader = this.keyBeanInfo.getLoader(chain.context, true);
        this.valueLoader = this.valueBeanInfo.getLoader(chain.context, true);
        handlers.put(this.tagName, new ChildLoader(this.itemsLoader, null));
    }
    
    @Override
    public void serializeBody(final BeanT o, final XMLSerializer w, final Object outerPeer) throws SAXException, AccessorException, IOException, XMLStreamException {
        final ValueT v = this.acc.get(o);
        if (v != null) {
            this.bareStartTag(w, this.tagName, v);
            for (final Map.Entry e : v.entrySet()) {
                this.bareStartTag(w, this.entryTag, null);
                final Object key = e.getKey();
                if (key != null) {
                    w.startElement(this.keyTag, key);
                    w.childAsXsiType(key, this.fieldName, this.keyBeanInfo, false);
                    w.endElement();
                }
                final Object value = e.getValue();
                if (value != null) {
                    w.startElement(this.valueTag, value);
                    w.childAsXsiType(value, this.fieldName, this.valueBeanInfo, false);
                    w.endElement();
                }
                w.endElement();
            }
            w.endElement();
        }
        else if (this.nillable) {
            w.startElement(this.tagName, null);
            w.writeXsiNilTrue();
            w.endElement();
        }
    }
    
    private void bareStartTag(final XMLSerializer w, final Name tagName, final Object peer) throws IOException, XMLStreamException, SAXException {
        w.startElement(tagName, peer);
        w.endNamespaceDecls(peer);
        w.endAttributes();
    }
    
    @Override
    public Accessor getElementPropertyAccessor(final String nsUri, final String localName) {
        if (this.tagName.equals(nsUri, localName)) {
            return this.acc;
        }
        return null;
    }
    
    static {
        knownImplClasses = new Class[] { HashMap.class, TreeMap.class, LinkedHashMap.class };
        keyReceiver = new ReceiverImpl(0);
        valueReceiver = new ReceiverImpl(1);
    }
    
    private static final class ReceiverImpl implements Receiver
    {
        private final int index;
        
        public ReceiverImpl(final int index) {
            this.index = index;
        }
        
        @Override
        public void receive(final UnmarshallingContext.State state, final Object o) {
            ((Object[])state.getTarget())[this.index] = o;
        }
    }
}
