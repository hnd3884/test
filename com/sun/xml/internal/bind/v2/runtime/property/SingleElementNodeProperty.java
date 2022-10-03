package com.sun.xml.internal.bind.v2.runtime.property;

import com.sun.xml.internal.bind.v2.model.core.PropertyKind;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Loader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Receiver;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.DefaultValueLoaderDecorator;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import com.sun.xml.internal.bind.v2.model.core.TypeRef;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.ChildLoader;
import com.sun.xml.internal.bind.v2.util.QNameMap;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import org.xml.sax.SAXException;
import javax.xml.bind.JAXBElement;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.runtime.JaxBeanInfo;
import java.util.Iterator;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeTypeInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeTypeRef;
import java.util.HashMap;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimePropertyInfo;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.internal.bind.v2.runtime.Name;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeElementPropertyInfo;
import java.util.Map;
import javax.xml.namespace.QName;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;

final class SingleElementNodeProperty<BeanT, ValueT> extends PropertyImpl<BeanT>
{
    private final Accessor<BeanT, ValueT> acc;
    private final boolean nillable;
    private final QName[] acceptedElements;
    private final Map<Class, TagAndType> typeNames;
    private RuntimeElementPropertyInfo prop;
    private final Name nullTagName;
    
    public SingleElementNodeProperty(final JAXBContextImpl context, final RuntimeElementPropertyInfo prop) {
        super(context, prop);
        this.typeNames = new HashMap<Class, TagAndType>();
        this.acc = prop.getAccessor().optimize(context);
        this.prop = prop;
        QName nt = null;
        boolean nil = false;
        this.acceptedElements = new QName[prop.getTypes().size()];
        for (int i = 0; i < this.acceptedElements.length; ++i) {
            this.acceptedElements[i] = ((RuntimeTypeRef)prop.getTypes().get(i)).getTagName();
        }
        for (final RuntimeTypeRef e : prop.getTypes()) {
            final JaxBeanInfo beanInfo = context.getOrCreate(e.getTarget());
            if (nt == null) {
                nt = e.getTagName();
            }
            this.typeNames.put(beanInfo.jaxbType, new TagAndType(context.nameBuilder.createElementName(e.getTagName()), beanInfo));
            nil |= e.isNillable();
        }
        this.nullTagName = context.nameBuilder.createElementName(nt);
        this.nillable = nil;
    }
    
    @Override
    public void wrapUp() {
        super.wrapUp();
        this.prop = null;
    }
    
    @Override
    public void reset(final BeanT bean) throws AccessorException {
        this.acc.set(bean, null);
    }
    
    @Override
    public String getIdValue(final BeanT beanT) {
        return null;
    }
    
    @Override
    public void serializeBody(final BeanT o, final XMLSerializer w, final Object outerPeer) throws SAXException, AccessorException, IOException, XMLStreamException {
        final ValueT v = this.acc.get(o);
        if (v != null) {
            final Class vtype = v.getClass();
            TagAndType tt = this.typeNames.get(vtype);
            if (tt == null) {
                for (final Map.Entry<Class, TagAndType> e : this.typeNames.entrySet()) {
                    if (e.getKey().isAssignableFrom(vtype)) {
                        tt = e.getValue();
                        break;
                    }
                }
            }
            final boolean addNilDecl = o instanceof JAXBElement && ((JAXBElement)o).isNil();
            if (tt == null) {
                w.startElement(this.typeNames.values().iterator().next().tagName, null);
                w.childAsXsiType(v, this.fieldName, w.grammar.getBeanInfo(Object.class), addNilDecl && this.nillable);
            }
            else {
                w.startElement(tt.tagName, null);
                w.childAsXsiType(v, this.fieldName, tt.beanInfo, addNilDecl && this.nillable);
            }
            w.endElement();
        }
        else if (this.nillable) {
            w.startElement(this.nullTagName, null);
            w.writeXsiNilTrue();
            w.endElement();
        }
    }
    
    @Override
    public void buildChildElementUnmarshallers(final UnmarshallerChain chain, final QNameMap<ChildLoader> handlers) {
        final JAXBContextImpl context = chain.context;
        for (final TypeRef<Type, Class> e : this.prop.getTypes()) {
            final JaxBeanInfo bi = context.getOrCreate((RuntimeTypeInfo)e.getTarget());
            Loader l = bi.getLoader(context, !Modifier.isFinal(bi.jaxbType.getModifiers()));
            if (e.getDefaultValue() != null) {
                l = new DefaultValueLoaderDecorator(l, e.getDefaultValue());
            }
            if (this.nillable || chain.context.allNillable) {
                l = new XsiNilLoader.Single(l, this.acc);
            }
            handlers.put(e.getTagName(), new ChildLoader(l, this.acc));
        }
    }
    
    @Override
    public PropertyKind getKind() {
        return PropertyKind.ELEMENT;
    }
    
    @Override
    public Accessor getElementPropertyAccessor(final String nsUri, final String localName) {
        for (final QName n : this.acceptedElements) {
            if (n.getNamespaceURI().equals(nsUri) && n.getLocalPart().equals(localName)) {
                return this.acc;
            }
        }
        return null;
    }
}
