package com.sun.xml.internal.bind.v2.runtime.property;

import com.sun.xml.internal.bind.v2.model.core.PropertyKind;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Loader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Receiver;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.LeafPropertyXsiLoader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.DefaultValueLoaderDecorator;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.LeafPropertyLoader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.ChildLoader;
import com.sun.xml.internal.bind.v2.util.QNameMap;
import java.lang.reflect.Modifier;
import javax.xml.bind.JAXBElement;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import com.sun.xml.internal.bind.v2.runtime.JaxBeanInfo;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import org.xml.sax.SAXException;
import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.model.core.ID;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeNonElementRef;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeTypeRef;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimePropertyInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeElementPropertyInfo;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.internal.bind.v2.runtime.reflect.TransducedAccessor;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import com.sun.xml.internal.bind.v2.runtime.Name;

final class SingleElementLeafProperty<BeanT> extends PropertyImpl<BeanT>
{
    private final Name tagName;
    private final boolean nillable;
    private final Accessor acc;
    private final String defaultValue;
    private final TransducedAccessor<BeanT> xacc;
    private final boolean improvedXsiTypeHandling;
    private final boolean idRef;
    
    public SingleElementLeafProperty(final JAXBContextImpl context, final RuntimeElementPropertyInfo prop) {
        super(context, prop);
        final RuntimeTypeRef ref = (RuntimeTypeRef)prop.getTypes().get(0);
        this.tagName = context.nameBuilder.createElementName(ref.getTagName());
        assert this.tagName != null;
        this.nillable = ref.isNillable();
        this.defaultValue = ref.getDefaultValue();
        this.acc = prop.getAccessor().optimize(context);
        this.xacc = TransducedAccessor.get(context, ref);
        assert this.xacc != null;
        this.improvedXsiTypeHandling = context.improvedXsiTypeHandling;
        this.idRef = (ref.getSource().id() == ID.IDREF);
    }
    
    @Override
    public void reset(final BeanT o) throws AccessorException {
        this.acc.set(o, null);
    }
    
    @Override
    public String getIdValue(final BeanT bean) throws AccessorException, SAXException {
        return this.xacc.print(bean).toString();
    }
    
    @Override
    public void serializeBody(final BeanT o, final XMLSerializer w, final Object outerPeer) throws SAXException, AccessorException, IOException, XMLStreamException {
        final boolean hasValue = this.xacc.hasValue(o);
        Object obj = null;
        try {
            obj = this.acc.getUnadapted(o);
        }
        catch (final AccessorException ex) {}
        final Class valueType = this.acc.getValueType();
        if (this.xsiTypeNeeded(o, w, obj, valueType)) {
            w.startElement(this.tagName, outerPeer);
            w.childAsXsiType(obj, this.fieldName, w.grammar.getBeanInfo((Class<Object>)valueType), false);
            w.endElement();
        }
        else if (hasValue) {
            this.xacc.writeLeafElement(w, this.tagName, o, this.fieldName);
        }
        else if (this.nillable) {
            w.startElement(this.tagName, null);
            w.writeXsiNilTrue();
            w.endElement();
        }
    }
    
    private boolean xsiTypeNeeded(final BeanT bean, final XMLSerializer w, final Object value, final Class valueTypeClass) {
        return this.improvedXsiTypeHandling && !this.acc.isAdapted() && value != null && !value.getClass().equals(valueTypeClass) && !this.idRef && !valueTypeClass.isPrimitive() && (this.acc.isValueTypeAbstractable() || this.isNillableAbstract(bean, w.grammar, value, valueTypeClass));
    }
    
    private boolean isNillableAbstract(final BeanT bean, final JAXBContextImpl context, final Object value, final Class valueTypeClass) {
        if (!this.nillable) {
            return false;
        }
        if (valueTypeClass != Object.class) {
            return false;
        }
        if (bean.getClass() != JAXBElement.class) {
            return false;
        }
        final JAXBElement jaxbElement = (JAXBElement)bean;
        final Class valueClass = value.getClass();
        final Class declaredTypeClass = jaxbElement.getDeclaredType();
        return !declaredTypeClass.equals(valueClass) && declaredTypeClass.isAssignableFrom(valueClass) && Modifier.isAbstract(declaredTypeClass.getModifiers()) && this.acc.isAbstractable(declaredTypeClass);
    }
    
    @Override
    public void buildChildElementUnmarshallers(final UnmarshallerChain chain, final QNameMap<ChildLoader> handlers) {
        Loader l = new LeafPropertyLoader(this.xacc);
        if (this.defaultValue != null) {
            l = new DefaultValueLoaderDecorator(l, this.defaultValue);
        }
        if (this.nillable || chain.context.allNillable) {
            l = new XsiNilLoader.Single(l, this.acc);
        }
        if (this.improvedXsiTypeHandling) {
            l = new LeafPropertyXsiLoader(l, this.xacc, this.acc);
        }
        handlers.put(this.tagName, new ChildLoader(l, null));
    }
    
    @Override
    public PropertyKind getKind() {
        return PropertyKind.ELEMENT;
    }
    
    @Override
    public Accessor getElementPropertyAccessor(final String nsUri, final String localName) {
        if (this.tagName.equals(nsUri, localName)) {
            return this.acc;
        }
        return null;
    }
}
