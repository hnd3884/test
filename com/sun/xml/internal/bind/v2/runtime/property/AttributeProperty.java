package com.sun.xml.internal.bind.v2.runtime.property;

import com.sun.xml.internal.bind.v2.model.core.PropertyKind;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.ChildLoader;
import com.sun.xml.internal.bind.v2.util.QNameMap;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import com.sun.xml.internal.bind.api.AccessorException;
import org.xml.sax.SAXException;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeNonElementRef;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimePropertyInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeAttributePropertyInfo;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import com.sun.xml.internal.bind.v2.runtime.reflect.TransducedAccessor;
import com.sun.xml.internal.bind.v2.runtime.Name;

public final class AttributeProperty<BeanT> extends PropertyImpl<BeanT> implements Comparable<AttributeProperty>
{
    public final Name attName;
    public final TransducedAccessor<BeanT> xacc;
    private final Accessor acc;
    
    public AttributeProperty(final JAXBContextImpl context, final RuntimeAttributePropertyInfo prop) {
        super(context, prop);
        this.attName = context.nameBuilder.createAttributeName(prop.getXmlName());
        this.xacc = TransducedAccessor.get(context, prop);
        this.acc = prop.getAccessor();
    }
    
    public void serializeAttributes(final BeanT o, final XMLSerializer w) throws SAXException, AccessorException, IOException, XMLStreamException {
        final CharSequence value = this.xacc.print(o);
        if (value != null) {
            w.attribute(this.attName, value.toString());
        }
    }
    
    @Override
    public void serializeURIs(final BeanT o, final XMLSerializer w) throws AccessorException, SAXException {
        this.xacc.declareNamespace(o, w);
    }
    
    @Override
    public boolean hasSerializeURIAction() {
        return this.xacc.useNamespace();
    }
    
    @Override
    public void buildChildElementUnmarshallers(final UnmarshallerChain chainElem, final QNameMap<ChildLoader> handlers) {
        throw new IllegalStateException();
    }
    
    @Override
    public PropertyKind getKind() {
        return PropertyKind.ATTRIBUTE;
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
    public int compareTo(final AttributeProperty that) {
        return this.attName.compareTo(that.attName);
    }
}
