package com.sun.xml.internal.bind.v2.runtime;

import com.sun.xml.internal.bind.Util;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallerImpl;
import java.lang.reflect.Method;
import java.util.logging.Level;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Loader;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import org.xml.sax.SAXException;
import java.lang.reflect.InvocationTargetException;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallingContext;
import com.sun.istack.internal.NotNull;
import java.util.Arrays;
import java.util.Collections;
import java.util.Collection;
import javax.xml.namespace.QName;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeTypeInfo;
import java.util.logging.Logger;

public abstract class JaxBeanInfo<BeanT>
{
    protected boolean isNilIncluded;
    protected short flag;
    private static final short FLAG_IS_ELEMENT = 1;
    private static final short FLAG_IS_IMMUTABLE = 2;
    private static final short FLAG_HAS_ELEMENT_ONLY_CONTENTMODEL = 4;
    private static final short FLAG_HAS_BEFORE_UNMARSHAL_METHOD = 8;
    private static final short FLAG_HAS_AFTER_UNMARSHAL_METHOD = 16;
    private static final short FLAG_HAS_BEFORE_MARSHAL_METHOD = 32;
    private static final short FLAG_HAS_AFTER_MARSHAL_METHOD = 64;
    private static final short FLAG_HAS_LIFECYCLE_EVENTS = 128;
    private LifecycleMethods lcm;
    public final Class<BeanT> jaxbType;
    private final Object typeName;
    private static final Class[] unmarshalEventParams;
    private static Class[] marshalEventParams;
    private static final Logger logger;
    
    protected JaxBeanInfo(final JAXBContextImpl grammar, final RuntimeTypeInfo rti, final Class<BeanT> jaxbType, final QName[] typeNames, final boolean isElement, final boolean isImmutable, final boolean hasLifecycleEvents) {
        this(grammar, rti, jaxbType, (Object)typeNames, isElement, isImmutable, hasLifecycleEvents);
    }
    
    protected JaxBeanInfo(final JAXBContextImpl grammar, final RuntimeTypeInfo rti, final Class<BeanT> jaxbType, final QName typeName, final boolean isElement, final boolean isImmutable, final boolean hasLifecycleEvents) {
        this(grammar, rti, jaxbType, (Object)typeName, isElement, isImmutable, hasLifecycleEvents);
    }
    
    protected JaxBeanInfo(final JAXBContextImpl grammar, final RuntimeTypeInfo rti, final Class<BeanT> jaxbType, final boolean isElement, final boolean isImmutable, final boolean hasLifecycleEvents) {
        this(grammar, rti, jaxbType, (Object)null, isElement, isImmutable, hasLifecycleEvents);
    }
    
    private JaxBeanInfo(final JAXBContextImpl grammar, final RuntimeTypeInfo rti, final Class<BeanT> jaxbType, final Object typeName, final boolean isElement, final boolean isImmutable, final boolean hasLifecycleEvents) {
        this.isNilIncluded = false;
        this.lcm = null;
        grammar.beanInfos.put(rti, this);
        this.jaxbType = jaxbType;
        this.typeName = typeName;
        this.flag = (short)((isElement ? 1 : 0) | (isImmutable ? 2 : 0) | (hasLifecycleEvents ? 128 : 0));
    }
    
    public final boolean hasBeforeUnmarshalMethod() {
        return (this.flag & 0x8) != 0x0;
    }
    
    public final boolean hasAfterUnmarshalMethod() {
        return (this.flag & 0x10) != 0x0;
    }
    
    public final boolean hasBeforeMarshalMethod() {
        return (this.flag & 0x20) != 0x0;
    }
    
    public final boolean hasAfterMarshalMethod() {
        return (this.flag & 0x40) != 0x0;
    }
    
    public final boolean isElement() {
        return (this.flag & 0x1) != 0x0;
    }
    
    public final boolean isImmutable() {
        return (this.flag & 0x2) != 0x0;
    }
    
    public final boolean hasElementOnlyContentModel() {
        return (this.flag & 0x4) != 0x0;
    }
    
    protected final void hasElementOnlyContentModel(final boolean value) {
        if (value) {
            this.flag |= 0x4;
        }
        else {
            this.flag &= 0xFFFFFFFB;
        }
    }
    
    public boolean isNilIncluded() {
        return this.isNilIncluded;
    }
    
    public boolean lookForLifecycleMethods() {
        return (this.flag & 0x80) != 0x0;
    }
    
    public abstract String getElementNamespaceURI(final BeanT p0);
    
    public abstract String getElementLocalName(final BeanT p0);
    
    public Collection<QName> getTypeNames() {
        if (this.typeName == null) {
            return (Collection<QName>)Collections.emptyList();
        }
        if (this.typeName instanceof QName) {
            return Collections.singletonList(this.typeName);
        }
        return Arrays.asList((QName[])this.typeName);
    }
    
    public QName getTypeName(@NotNull final BeanT instance) {
        if (this.typeName == null) {
            return null;
        }
        if (this.typeName instanceof QName) {
            return (QName)this.typeName;
        }
        return ((QName[])this.typeName)[0];
    }
    
    public abstract BeanT createInstance(final UnmarshallingContext p0) throws IllegalAccessException, InvocationTargetException, InstantiationException, SAXException;
    
    public abstract boolean reset(final BeanT p0, final UnmarshallingContext p1) throws SAXException;
    
    public abstract String getId(final BeanT p0, final XMLSerializer p1) throws SAXException;
    
    public abstract void serializeBody(final BeanT p0, final XMLSerializer p1) throws SAXException, IOException, XMLStreamException;
    
    public abstract void serializeAttributes(final BeanT p0, final XMLSerializer p1) throws SAXException, IOException, XMLStreamException;
    
    public abstract void serializeRoot(final BeanT p0, final XMLSerializer p1) throws SAXException, IOException, XMLStreamException;
    
    public abstract void serializeURIs(final BeanT p0, final XMLSerializer p1) throws SAXException;
    
    public abstract Loader getLoader(final JAXBContextImpl p0, final boolean p1);
    
    public abstract Transducer<BeanT> getTransducer();
    
    protected void link(final JAXBContextImpl grammar) {
    }
    
    public void wrapUp() {
    }
    
    protected final void setLifecycleFlags() {
        try {
            Class<BeanT> jt = this.jaxbType;
            if (this.lcm == null) {
                this.lcm = new LifecycleMethods();
            }
            while (jt != null) {
                for (final Method m : jt.getDeclaredMethods()) {
                    final String name = m.getName();
                    if (this.lcm.beforeUnmarshal == null && name.equals("beforeUnmarshal") && this.match(m, JaxBeanInfo.unmarshalEventParams)) {
                        this.cacheLifecycleMethod(m, (short)8);
                    }
                    if (this.lcm.afterUnmarshal == null && name.equals("afterUnmarshal") && this.match(m, JaxBeanInfo.unmarshalEventParams)) {
                        this.cacheLifecycleMethod(m, (short)16);
                    }
                    if (this.lcm.beforeMarshal == null && name.equals("beforeMarshal") && this.match(m, JaxBeanInfo.marshalEventParams)) {
                        this.cacheLifecycleMethod(m, (short)32);
                    }
                    if (this.lcm.afterMarshal == null && name.equals("afterMarshal") && this.match(m, JaxBeanInfo.marshalEventParams)) {
                        this.cacheLifecycleMethod(m, (short)64);
                    }
                }
                jt = (Class<BeanT>)jt.getSuperclass();
            }
        }
        catch (final SecurityException e) {
            JaxBeanInfo.logger.log(Level.WARNING, Messages.UNABLE_TO_DISCOVER_EVENTHANDLER.format(this.jaxbType.getName(), e));
        }
    }
    
    private boolean match(final Method m, final Class[] params) {
        return Arrays.equals(m.getParameterTypes(), params);
    }
    
    private void cacheLifecycleMethod(final Method m, final short lifecycleFlag) {
        if (this.lcm == null) {
            this.lcm = new LifecycleMethods();
        }
        m.setAccessible(true);
        this.flag |= lifecycleFlag;
        switch (lifecycleFlag) {
            case 8: {
                this.lcm.beforeUnmarshal = m;
                break;
            }
            case 16: {
                this.lcm.afterUnmarshal = m;
                break;
            }
            case 32: {
                this.lcm.beforeMarshal = m;
                break;
            }
            case 64: {
                this.lcm.afterMarshal = m;
                break;
            }
        }
    }
    
    public final LifecycleMethods getLifecycleMethods() {
        return this.lcm;
    }
    
    public final void invokeBeforeUnmarshalMethod(final UnmarshallerImpl unm, final Object child, final Object parent) throws SAXException {
        final Method m = this.getLifecycleMethods().beforeUnmarshal;
        this.invokeUnmarshallCallback(m, child, unm, parent);
    }
    
    public final void invokeAfterUnmarshalMethod(final UnmarshallerImpl unm, final Object child, final Object parent) throws SAXException {
        final Method m = this.getLifecycleMethods().afterUnmarshal;
        this.invokeUnmarshallCallback(m, child, unm, parent);
    }
    
    private void invokeUnmarshallCallback(final Method m, final Object child, final UnmarshallerImpl unm, final Object parent) throws SAXException {
        try {
            m.invoke(child, unm, parent);
        }
        catch (final IllegalAccessException e) {
            UnmarshallingContext.getInstance().handleError(e, false);
        }
        catch (final InvocationTargetException e2) {
            UnmarshallingContext.getInstance().handleError(e2, false);
        }
    }
    
    static {
        unmarshalEventParams = new Class[] { Unmarshaller.class, Object.class };
        JaxBeanInfo.marshalEventParams = new Class[] { Marshaller.class };
        logger = Util.getClassLogger();
    }
}
