package com.sun.xml.internal.bind.v2.runtime;

import com.sun.xml.internal.bind.v2.model.core.ClassInfo;
import com.sun.xml.internal.bind.v2.model.core.ElementInfo;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.TagName;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Intercepter;
import java.lang.reflect.InvocationTargetException;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallingContext;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Discarder;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.ChildLoader;
import com.sun.xml.internal.bind.v2.util.QNameMap;
import com.sun.xml.internal.bind.v2.runtime.property.UnmarshallerChain;
import com.sun.xml.internal.bind.v2.model.core.PropertyKind;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import org.xml.sax.SAXException;
import com.sun.xml.internal.bind.api.AccessorException;
import javax.xml.bind.JAXBException;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimePropertyInfo;
import com.sun.xml.internal.bind.v2.runtime.property.PropertyFactory;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeTypeInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeElementInfo;
import java.lang.reflect.Constructor;
import javax.xml.namespace.QName;
import com.sun.xml.internal.bind.v2.runtime.property.Property;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Loader;
import javax.xml.bind.JAXBElement;

public final class ElementBeanInfoImpl extends JaxBeanInfo<JAXBElement>
{
    private Loader loader;
    private final Property property;
    private final QName tagName;
    public final Class expectedType;
    private final Class scope;
    private final Constructor<? extends JAXBElement> constructor;
    
    ElementBeanInfoImpl(final JAXBContextImpl grammar, final RuntimeElementInfo rei) {
        super(grammar, rei, rei.getType(), true, false, true);
        this.property = PropertyFactory.create(grammar, rei.getProperty());
        this.tagName = rei.getElementName();
        this.expectedType = Utils.REFLECTION_NAVIGATOR.erasure(((ElementInfo<Class, C>)rei).getContentInMemoryType());
        this.scope = ((rei.getScope() == null) ? JAXBElement.GlobalScope.class : ((ClassInfo<T, Class<JAXBElement.GlobalScope>>)rei.getScope()).getClazz());
        final Class type = Utils.REFLECTION_NAVIGATOR.erasure(rei.getType());
        if (type == JAXBElement.class) {
            this.constructor = null;
        }
        else {
            try {
                this.constructor = type.getConstructor(this.expectedType);
            }
            catch (final NoSuchMethodException e) {
                final NoSuchMethodError x = new NoSuchMethodError("Failed to find the constructor for " + type + " with " + this.expectedType);
                x.initCause(e);
                throw x;
            }
        }
    }
    
    protected ElementBeanInfoImpl(final JAXBContextImpl grammar) {
        super(grammar, null, JAXBElement.class, true, false, true);
        this.tagName = null;
        this.expectedType = null;
        this.scope = null;
        this.constructor = null;
        this.property = new Property<JAXBElement>() {
            @Override
            public void reset(final JAXBElement o) {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public void serializeBody(final JAXBElement e, final XMLSerializer target, final Object outerPeer) throws SAXException, IOException, XMLStreamException {
                Class scope = e.getScope();
                if (e.isGlobalScope()) {
                    scope = null;
                }
                final QName n = e.getName();
                final ElementBeanInfoImpl bi = grammar.getElement(scope, n);
                if (bi == null) {
                    JaxBeanInfo tbi;
                    try {
                        tbi = grammar.getBeanInfo(e.getDeclaredType(), true);
                    }
                    catch (final JAXBException x) {
                        target.reportError(null, x);
                        return;
                    }
                    final Object value = e.getValue();
                    target.startElement(n.getNamespaceURI(), n.getLocalPart(), n.getPrefix(), null);
                    if (value == null) {
                        target.writeXsiNilTrue();
                    }
                    else {
                        target.childAsXsiType(value, "value", tbi, false);
                    }
                    target.endElement();
                }
                else {
                    try {
                        bi.property.serializeBody(e, target, e);
                    }
                    catch (final AccessorException x2) {
                        target.reportError(null, x2);
                    }
                }
            }
            
            @Override
            public void serializeURIs(final JAXBElement o, final XMLSerializer target) {
            }
            
            @Override
            public boolean hasSerializeURIAction() {
                return false;
            }
            
            @Override
            public String getIdValue(final JAXBElement o) {
                return null;
            }
            
            @Override
            public PropertyKind getKind() {
                return PropertyKind.ELEMENT;
            }
            
            @Override
            public void buildChildElementUnmarshallers(final UnmarshallerChain chain, final QNameMap<ChildLoader> handlers) {
            }
            
            @Override
            public Accessor getElementPropertyAccessor(final String nsUri, final String localName) {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public void wrapUp() {
            }
            
            @Override
            public RuntimePropertyInfo getInfo() {
                return ElementBeanInfoImpl.this.property.getInfo();
            }
            
            @Override
            public boolean isHiddenByOverride() {
                return false;
            }
            
            @Override
            public void setHiddenByOverride(final boolean hidden) {
                throw new UnsupportedOperationException("Not supported on jaxbelements.");
            }
            
            @Override
            public String getFieldName() {
                return null;
            }
        };
    }
    
    @Override
    public String getElementNamespaceURI(final JAXBElement e) {
        return e.getName().getNamespaceURI();
    }
    
    @Override
    public String getElementLocalName(final JAXBElement e) {
        return e.getName().getLocalPart();
    }
    
    @Override
    public Loader getLoader(final JAXBContextImpl context, final boolean typeSubstitutionCapable) {
        if (this.loader == null) {
            final UnmarshallerChain c = new UnmarshallerChain(context);
            final QNameMap<ChildLoader> result = new QNameMap<ChildLoader>();
            this.property.buildChildElementUnmarshallers(c, result);
            if (result.size() == 1) {
                this.loader = new IntercepterLoader(result.getOne().getValue().loader);
            }
            else {
                this.loader = Discarder.INSTANCE;
            }
        }
        return this.loader;
    }
    
    @Override
    public final JAXBElement createInstance(final UnmarshallingContext context) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        return this.createInstanceFromValue(null);
    }
    
    public final JAXBElement createInstanceFromValue(final Object o) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        if (this.constructor == null) {
            return new JAXBElement(this.tagName, this.expectedType, this.scope, (T)o);
        }
        return (JAXBElement)this.constructor.newInstance(o);
    }
    
    @Override
    public boolean reset(final JAXBElement e, final UnmarshallingContext context) {
        e.setValue(null);
        return true;
    }
    
    @Override
    public String getId(final JAXBElement e, final XMLSerializer target) {
        final Object o = e.getValue();
        if (o instanceof String) {
            return (String)o;
        }
        return null;
    }
    
    @Override
    public void serializeBody(final JAXBElement element, final XMLSerializer target) throws SAXException, IOException, XMLStreamException {
        try {
            this.property.serializeBody(element, target, null);
        }
        catch (final AccessorException x) {
            target.reportError(null, x);
        }
    }
    
    @Override
    public void serializeRoot(final JAXBElement e, final XMLSerializer target) throws SAXException, IOException, XMLStreamException {
        this.serializeBody(e, target);
    }
    
    @Override
    public void serializeAttributes(final JAXBElement e, final XMLSerializer target) {
    }
    
    @Override
    public void serializeURIs(final JAXBElement e, final XMLSerializer target) {
    }
    
    @Override
    public final Transducer<JAXBElement> getTransducer() {
        return null;
    }
    
    @Override
    public void wrapUp() {
        super.wrapUp();
        this.property.wrapUp();
    }
    
    public void link(final JAXBContextImpl grammar) {
        super.link(grammar);
        this.getLoader(grammar, true);
    }
    
    private final class IntercepterLoader extends Loader implements Intercepter
    {
        private final Loader core;
        
        public IntercepterLoader(final Loader core) {
            this.core = core;
        }
        
        @Override
        public final void startElement(final UnmarshallingContext.State state, final TagName ea) throws SAXException {
            state.setLoader(this.core);
            state.setIntercepter(this);
            final UnmarshallingContext context = state.getContext();
            Object child = context.getOuterPeer();
            if (child != null && ElementBeanInfoImpl.this.jaxbType != child.getClass()) {
                child = null;
            }
            if (child != null) {
                ElementBeanInfoImpl.this.reset((JAXBElement)child, context);
            }
            if (child == null) {
                child = context.createInstance(ElementBeanInfoImpl.this);
            }
            this.fireBeforeUnmarshal(ElementBeanInfoImpl.this, child, state);
            context.recordOuterPeer(child);
            final UnmarshallingContext.State p = state.getPrev();
            p.setBackup(p.getTarget());
            p.setTarget(child);
            this.core.startElement(state, ea);
        }
        
        @Override
        public Object intercept(final UnmarshallingContext.State state, final Object o) throws SAXException {
            final JAXBElement e = (JAXBElement)state.getTarget();
            state.setTarget(state.getBackup());
            state.setBackup(null);
            if (state.isNil()) {
                e.setNil(true);
                state.setNil(false);
            }
            if (o != null) {
                e.setValue(o);
            }
            this.fireAfterUnmarshal(ElementBeanInfoImpl.this, e, state);
            return e;
        }
    }
}
