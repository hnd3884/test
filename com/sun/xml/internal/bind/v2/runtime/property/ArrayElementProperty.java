package com.sun.xml.internal.bind.v2.runtime.property;

import com.sun.xml.internal.bind.v2.model.core.TypeInfo;
import com.sun.xml.internal.bind.v2.runtime.reflect.NullSafeAccessor;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import com.sun.xml.internal.bind.v2.runtime.Transducer;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.TextLoader;
import com.sun.xml.internal.bind.v2.model.core.PropertyKind;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Loader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Receiver;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.DefaultValueLoaderDecorator;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.ChildLoader;
import com.sun.xml.internal.bind.v2.util.QNameMap;
import com.sun.xml.internal.bind.api.AccessorException;
import org.xml.sax.SAXException;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import com.sun.xml.internal.bind.v2.runtime.reflect.ListIterator;
import javax.xml.bind.JAXBException;
import com.sun.xml.internal.bind.v2.runtime.reflect.Lister;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import java.util.Iterator;
import java.util.List;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeTypeInfo;
import com.sun.xml.internal.bind.v2.runtime.RuntimeUtil;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeTypeRef;
import java.util.HashMap;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimePropertyInfo;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.internal.bind.v2.runtime.Name;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeElementPropertyInfo;
import com.sun.xml.internal.bind.v2.runtime.JaxBeanInfo;
import java.lang.reflect.Type;
import com.sun.xml.internal.bind.v2.model.core.TypeRef;
import java.util.Map;

abstract class ArrayElementProperty<BeanT, ListT, ItemT> extends ArrayERProperty<BeanT, ListT, ItemT>
{
    private final Map<Class, TagAndType> typeMap;
    private Map<TypeRef<Type, Class>, JaxBeanInfo> refs;
    protected RuntimeElementPropertyInfo prop;
    private final Name nillableTagName;
    
    protected ArrayElementProperty(final JAXBContextImpl grammar, final RuntimeElementPropertyInfo prop) {
        super(grammar, prop, prop.getXmlName(), prop.isCollectionNillable());
        this.typeMap = new HashMap<Class, TagAndType>();
        this.refs = new HashMap<TypeRef<Type, Class>, JaxBeanInfo>();
        this.prop = prop;
        final List<? extends RuntimeTypeRef> types = prop.getTypes();
        Name n = null;
        for (final RuntimeTypeRef typeRef : types) {
            Class type = ((TypeInfo<Class, C>)typeRef.getTarget()).getType();
            if (type.isPrimitive()) {
                type = RuntimeUtil.primitiveToBox.get(type);
            }
            final JaxBeanInfo beanInfo = grammar.getOrCreate(typeRef.getTarget());
            final TagAndType tt = new TagAndType(grammar.nameBuilder.createElementName(typeRef.getTagName()), beanInfo);
            this.typeMap.put(type, tt);
            this.refs.put(typeRef, beanInfo);
            if (typeRef.isNillable() && n == null) {
                n = tt.tagName;
            }
        }
        this.nillableTagName = n;
    }
    
    @Override
    public void wrapUp() {
        super.wrapUp();
        this.refs = null;
        this.prop = null;
    }
    
    @Override
    protected void serializeListBody(final BeanT beanT, final XMLSerializer w, final ListT list) throws IOException, XMLStreamException, SAXException, AccessorException {
        final ListIterator<ItemT> itr = this.lister.iterator(list, w);
        final boolean isIdref = itr instanceof Lister.IDREFSIterator;
        while (itr.hasNext()) {
            try {
                final ItemT item = itr.next();
                if (item != null) {
                    Class itemType = item.getClass();
                    if (isIdref) {
                        itemType = ((Lister.IDREFSIterator)itr).last().getClass();
                    }
                    TagAndType tt;
                    for (tt = this.typeMap.get(itemType); tt == null && itemType != null; itemType = itemType.getSuperclass(), tt = this.typeMap.get(itemType)) {}
                    if (tt == null) {
                        w.startElement(this.typeMap.values().iterator().next().tagName, null);
                        w.childAsXsiType(item, this.fieldName, w.grammar.getBeanInfo(Object.class), false);
                    }
                    else {
                        w.startElement(tt.tagName, null);
                        this.serializeItem(tt.beanInfo, item, w);
                    }
                    w.endElement();
                }
                else {
                    if (this.nillableTagName == null) {
                        continue;
                    }
                    w.startElement(this.nillableTagName, null);
                    w.writeXsiNilTrue();
                    w.endElement();
                }
            }
            catch (final JAXBException e) {
                w.reportError(this.fieldName, e);
            }
        }
    }
    
    protected abstract void serializeItem(final JaxBeanInfo p0, final ItemT p1, final XMLSerializer p2) throws SAXException, AccessorException, IOException, XMLStreamException;
    
    public void createBodyUnmarshaller(final UnmarshallerChain chain, final QNameMap<ChildLoader> loaders) {
        final int offset = chain.allocateOffset();
        final Receiver recv = new ReceiverImpl(offset);
        for (final RuntimeTypeRef typeRef : this.prop.getTypes()) {
            final Name tagName = chain.context.nameBuilder.createElementName(typeRef.getTagName());
            Loader item = this.createItemUnmarshaller(chain, typeRef);
            if (typeRef.isNillable() || chain.context.allNillable) {
                item = new XsiNilLoader.Array(item);
            }
            if (typeRef.getDefaultValue() != null) {
                item = new DefaultValueLoaderDecorator(item, typeRef.getDefaultValue());
            }
            loaders.put(tagName, new ChildLoader(item, recv));
        }
    }
    
    @Override
    public final PropertyKind getKind() {
        return PropertyKind.ELEMENT;
    }
    
    private Loader createItemUnmarshaller(final UnmarshallerChain chain, final RuntimeTypeRef typeRef) {
        if (PropertyFactory.isLeaf(typeRef.getSource())) {
            final Transducer xducer = typeRef.getTransducer();
            return new TextLoader(xducer);
        }
        return this.refs.get(typeRef).getLoader(chain.context, true);
    }
    
    @Override
    public Accessor getElementPropertyAccessor(final String nsUri, final String localName) {
        if (this.wrapperTagName != null) {
            if (this.wrapperTagName.equals(nsUri, localName)) {
                return this.acc;
            }
        }
        else {
            for (final TagAndType tt : this.typeMap.values()) {
                if (tt.tagName.equals(nsUri, localName)) {
                    return new NullSafeAccessor(this.acc, this.lister);
                }
            }
        }
        return null;
    }
}
