package com.sun.xml.internal.ws.server.sei;

import com.sun.xml.internal.ws.spi.db.DatabindingException;
import javax.xml.namespace.QName;
import javax.xml.bind.JAXBException;
import com.sun.xml.internal.ws.spi.db.BindingContext;
import com.sun.xml.internal.ws.spi.db.PropertyAccessor;
import javax.xml.ws.WebServiceException;
import com.sun.xml.internal.ws.spi.db.WrapperComposite;
import com.sun.xml.internal.ws.model.WrapperParameter;
import java.util.List;
import com.sun.xml.internal.ws.model.ParameterImpl;
import com.sun.xml.internal.ws.message.jaxb.JAXBMessage;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import com.sun.xml.internal.ws.api.message.Messages;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.message.Message;

public abstract class EndpointResponseMessageBuilder
{
    public static final EndpointResponseMessageBuilder EMPTY_SOAP11;
    public static final EndpointResponseMessageBuilder EMPTY_SOAP12;
    
    public abstract Message createMessage(final Object[] p0, final Object p1);
    
    static {
        EMPTY_SOAP11 = new Empty(SOAPVersion.SOAP_11);
        EMPTY_SOAP12 = new Empty(SOAPVersion.SOAP_12);
    }
    
    private static final class Empty extends EndpointResponseMessageBuilder
    {
        private final SOAPVersion soapVersion;
        
        public Empty(final SOAPVersion soapVersion) {
            this.soapVersion = soapVersion;
        }
        
        @Override
        public Message createMessage(final Object[] methodArgs, final Object returnValue) {
            return Messages.createEmpty(this.soapVersion);
        }
    }
    
    private abstract static class JAXB extends EndpointResponseMessageBuilder
    {
        private final XMLBridge bridge;
        private final SOAPVersion soapVersion;
        
        protected JAXB(final XMLBridge bridge, final SOAPVersion soapVersion) {
            assert bridge != null;
            this.bridge = bridge;
            this.soapVersion = soapVersion;
        }
        
        @Override
        public final Message createMessage(final Object[] methodArgs, final Object returnValue) {
            return JAXBMessage.create(this.bridge, this.build(methodArgs, returnValue), this.soapVersion);
        }
        
        abstract Object build(final Object[] p0, final Object p1);
    }
    
    public static final class Bare extends JAXB
    {
        private final int methodPos;
        private final ValueGetter getter;
        
        public Bare(final ParameterImpl p, final SOAPVersion soapVersion) {
            super(p.getXMLBridge(), soapVersion);
            this.methodPos = p.getIndex();
            this.getter = ValueGetter.get(p);
        }
        
        @Override
        Object build(final Object[] methodArgs, final Object returnValue) {
            if (this.methodPos == -1) {
                return returnValue;
            }
            return this.getter.get(methodArgs[this.methodPos]);
        }
    }
    
    abstract static class Wrapped extends JAXB
    {
        protected final int[] indices;
        protected final ValueGetter[] getters;
        protected XMLBridge[] parameterBridges;
        protected List<ParameterImpl> children;
        
        protected Wrapped(final WrapperParameter wp, final SOAPVersion soapVersion) {
            super(wp.getXMLBridge(), soapVersion);
            this.children = wp.getWrapperChildren();
            this.indices = new int[this.children.size()];
            this.getters = new ValueGetter[this.children.size()];
            for (int i = 0; i < this.indices.length; ++i) {
                final ParameterImpl p = this.children.get(i);
                this.indices[i] = p.getIndex();
                this.getters[i] = ValueGetter.get(p);
            }
        }
        
        WrapperComposite buildWrapperComposite(final Object[] methodArgs, final Object returnValue) {
            final WrapperComposite cs = new WrapperComposite();
            cs.bridges = this.parameterBridges;
            cs.values = new Object[this.parameterBridges.length];
            for (int i = this.indices.length - 1; i >= 0; --i) {
                Object v;
                if (this.indices[i] == -1) {
                    v = this.getters[i].get(returnValue);
                }
                else {
                    v = this.getters[i].get(methodArgs[this.indices[i]]);
                }
                if (v == null) {
                    throw new WebServiceException("Method Parameter: " + this.children.get(i).getName() + " cannot be null. This is BP 1.1 R2211 violation.");
                }
                cs.values[i] = v;
            }
            return cs;
        }
    }
    
    public static final class DocLit extends Wrapped
    {
        private final PropertyAccessor[] accessors;
        private final Class wrapper;
        private boolean dynamicWrapper;
        private BindingContext bindingContext;
        
        public DocLit(final WrapperParameter wp, final SOAPVersion soapVersion) {
            super(wp, soapVersion);
            this.bindingContext = wp.getOwner().getBindingContext();
            this.wrapper = (Class)wp.getXMLBridge().getTypeInfo().type;
            this.dynamicWrapper = WrapperComposite.class.equals(this.wrapper);
            this.children = wp.getWrapperChildren();
            this.parameterBridges = new XMLBridge[this.children.size()];
            this.accessors = new PropertyAccessor[this.children.size()];
            for (int i = 0; i < this.accessors.length; ++i) {
                final ParameterImpl p = this.children.get(i);
                final QName name = p.getName();
                if (this.dynamicWrapper) {
                    this.parameterBridges[i] = this.children.get(i).getInlinedRepeatedElementBridge();
                    if (this.parameterBridges[i] == null) {
                        this.parameterBridges[i] = this.children.get(i).getXMLBridge();
                    }
                }
                else {
                    try {
                        this.accessors[i] = (this.dynamicWrapper ? null : p.getOwner().getBindingContext().getElementPropertyAccessor((Class<Object>)this.wrapper, name.getNamespaceURI(), name.getLocalPart()));
                    }
                    catch (final JAXBException e) {
                        throw new WebServiceException(this.wrapper + " do not have a property of the name " + name, e);
                    }
                }
            }
        }
        
        @Override
        Object build(final Object[] methodArgs, final Object returnValue) {
            if (this.dynamicWrapper) {
                return this.buildWrapperComposite(methodArgs, returnValue);
            }
            try {
                final Object bean = this.bindingContext.newWrapperInstace(this.wrapper);
                for (int i = this.indices.length - 1; i >= 0; --i) {
                    if (this.indices[i] == -1) {
                        this.accessors[i].set(bean, returnValue);
                    }
                    else {
                        this.accessors[i].set(bean, this.getters[i].get(methodArgs[this.indices[i]]));
                    }
                }
                return bean;
            }
            catch (final InstantiationException e) {
                final Error x = new InstantiationError(e.getMessage());
                x.initCause(e);
                throw x;
            }
            catch (final IllegalAccessException e2) {
                final Error x = new IllegalAccessError(e2.getMessage());
                x.initCause(e2);
                throw x;
            }
            catch (final DatabindingException e3) {
                throw new WebServiceException(e3);
            }
        }
    }
    
    public static final class RpcLit extends Wrapped
    {
        public RpcLit(final WrapperParameter wp, final SOAPVersion soapVersion) {
            super(wp, soapVersion);
            assert wp.getTypeInfo().type == WrapperComposite.class;
            this.parameterBridges = new XMLBridge[this.children.size()];
            for (int i = 0; i < this.parameterBridges.length; ++i) {
                this.parameterBridges[i] = this.children.get(i).getXMLBridge();
            }
        }
        
        @Override
        Object build(final Object[] methodArgs, final Object returnValue) {
            return this.buildWrapperComposite(methodArgs, returnValue);
        }
    }
}
