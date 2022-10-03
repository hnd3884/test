package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import java.util.Collection;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
import com.sun.xml.internal.bind.api.AccessorException;
import java.util.HashMap;
import com.sun.xml.internal.bind.v2.runtime.property.Property;
import com.sun.xml.internal.bind.v2.runtime.property.StructureLoaderBuilder;
import com.sun.xml.internal.bind.v2.runtime.property.AttributeProperty;
import com.sun.xml.internal.bind.v2.runtime.property.UnmarshallerChain;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.internal.bind.v2.runtime.ClassBeanInfoImpl;
import com.sun.xml.internal.bind.v2.runtime.JaxBeanInfo;
import javax.xml.namespace.QName;
import java.util.Map;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import com.sun.xml.internal.bind.v2.runtime.reflect.TransducedAccessor;
import com.sun.xml.internal.bind.v2.util.QNameMap;

public final class StructureLoader extends Loader
{
    private final QNameMap<ChildLoader> childUnmarshallers;
    private ChildLoader catchAll;
    private ChildLoader textHandler;
    private QNameMap<TransducedAccessor> attUnmarshallers;
    private Accessor<Object, Map<QName, String>> attCatchAll;
    private final JaxBeanInfo beanInfo;
    private int frameSize;
    private static final QNameMap<TransducedAccessor> EMPTY;
    
    public StructureLoader(final ClassBeanInfoImpl beanInfo) {
        super(true);
        this.childUnmarshallers = new QNameMap<ChildLoader>();
        this.beanInfo = beanInfo;
    }
    
    public void init(final JAXBContextImpl context, final ClassBeanInfoImpl beanInfo, final Accessor<?, Map<QName, String>> attWildcard) {
        final UnmarshallerChain chain = new UnmarshallerChain(context);
        for (ClassBeanInfoImpl bi = beanInfo; bi != null; bi = bi.superClazz) {
            for (int i = bi.properties.length - 1; i >= 0; --i) {
                final Property p = bi.properties[i];
                switch (p.getKind()) {
                    case ATTRIBUTE: {
                        if (this.attUnmarshallers == null) {
                            this.attUnmarshallers = new QNameMap<TransducedAccessor>();
                        }
                        final AttributeProperty ap = (AttributeProperty)p;
                        this.attUnmarshallers.put(ap.attName.toQName(), ap.xacc);
                        break;
                    }
                    case ELEMENT:
                    case REFERENCE:
                    case MAP:
                    case VALUE: {
                        p.buildChildElementUnmarshallers(chain, this.childUnmarshallers);
                        break;
                    }
                }
            }
        }
        this.frameSize = chain.getScopeSize();
        this.textHandler = this.childUnmarshallers.get(StructureLoaderBuilder.TEXT_HANDLER);
        this.catchAll = this.childUnmarshallers.get(StructureLoaderBuilder.CATCH_ALL);
        if (attWildcard != null) {
            this.attCatchAll = (Accessor<Object, Map<QName, String>>)attWildcard;
            if (this.attUnmarshallers == null) {
                this.attUnmarshallers = StructureLoader.EMPTY;
            }
        }
        else {
            this.attCatchAll = null;
        }
    }
    
    @Override
    public void startElement(final UnmarshallingContext.State state, final TagName ea) throws SAXException {
        final UnmarshallingContext context = state.getContext();
        assert !this.beanInfo.isImmutable();
        Object child = context.getInnerPeer();
        if (child != null && this.beanInfo.jaxbType != child.getClass()) {
            child = null;
        }
        if (child != null) {
            this.beanInfo.reset(child, context);
        }
        if (child == null) {
            child = context.createInstance(this.beanInfo);
        }
        context.recordInnerPeer(child);
        state.setTarget(child);
        this.fireBeforeUnmarshal(this.beanInfo, child, state);
        context.startScope(this.frameSize);
        if (this.attUnmarshallers != null) {
            final Attributes atts = ea.atts;
            for (int i = 0; i < atts.getLength(); ++i) {
                final String auri = atts.getURI(i);
                String alocal = atts.getLocalName(i);
                if ("".equals(alocal)) {
                    alocal = atts.getQName(i);
                }
                final String avalue = atts.getValue(i);
                final TransducedAccessor xacc = this.attUnmarshallers.get(auri, alocal);
                try {
                    if (xacc != null) {
                        xacc.parse(child, avalue);
                    }
                    else if (this.attCatchAll != null) {
                        final String qname = atts.getQName(i);
                        if (!atts.getURI(i).equals("http://www.w3.org/2001/XMLSchema-instance")) {
                            final Object o = state.getTarget();
                            Map<QName, String> map = this.attCatchAll.get(o);
                            if (map == null) {
                                if (!this.attCatchAll.valueType.isAssignableFrom(HashMap.class)) {
                                    context.handleError(Messages.UNABLE_TO_CREATE_MAP.format(this.attCatchAll.valueType));
                                    return;
                                }
                                map = new HashMap<QName, String>();
                                this.attCatchAll.set(o, map);
                            }
                            final int idx = qname.indexOf(58);
                            String prefix;
                            if (idx < 0) {
                                prefix = "";
                            }
                            else {
                                prefix = qname.substring(0, idx);
                            }
                            map.put(new QName(auri, alocal, prefix), avalue);
                        }
                    }
                }
                catch (final AccessorException e) {
                    Loader.handleGenericException(e, true);
                }
            }
        }
    }
    
    @Override
    public void childElement(final UnmarshallingContext.State state, final TagName arg) throws SAXException {
        ChildLoader child = this.childUnmarshallers.get(arg.uri, arg.local);
        if (child == null) {
            child = this.catchAll;
            if (child == null) {
                super.childElement(state, arg);
                return;
            }
        }
        state.setLoader(child.loader);
        state.setReceiver(child.receiver);
    }
    
    @Override
    public Collection<QName> getExpectedChildElements() {
        return this.childUnmarshallers.keySet();
    }
    
    @Override
    public Collection<QName> getExpectedAttributes() {
        return this.attUnmarshallers.keySet();
    }
    
    @Override
    public void text(final UnmarshallingContext.State state, final CharSequence text) throws SAXException {
        if (this.textHandler != null) {
            this.textHandler.loader.text(state, text);
        }
    }
    
    @Override
    public void leaveElement(final UnmarshallingContext.State state, final TagName ea) throws SAXException {
        state.getContext().endScope(this.frameSize);
        this.fireAfterUnmarshal(this.beanInfo, state.getTarget(), state.getPrev());
    }
    
    public JaxBeanInfo getBeanInfo() {
        return this.beanInfo;
    }
    
    static {
        EMPTY = new QNameMap<TransducedAccessor>();
    }
}
