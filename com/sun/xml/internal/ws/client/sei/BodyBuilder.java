package com.sun.xml.internal.ws.client.sei;

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

abstract class BodyBuilder
{
    static final BodyBuilder EMPTY_SOAP11;
    static final BodyBuilder EMPTY_SOAP12;
    
    abstract Message createMessage(final Object[] p0);
    
    static {
        EMPTY_SOAP11 = new Empty(SOAPVersion.SOAP_11);
        EMPTY_SOAP12 = new Empty(SOAPVersion.SOAP_12);
    }
    
    private static final class Empty extends BodyBuilder
    {
        private final SOAPVersion soapVersion;
        
        public Empty(final SOAPVersion soapVersion) {
            this.soapVersion = soapVersion;
        }
        
        @Override
        Message createMessage(final Object[] methodArgs) {
            return Messages.createEmpty(this.soapVersion);
        }
    }
    
    private abstract static class JAXB extends BodyBuilder
    {
        private final XMLBridge bridge;
        private final SOAPVersion soapVersion;
        
        protected JAXB(final XMLBridge bridge, final SOAPVersion soapVersion) {
            assert bridge != null;
            this.bridge = bridge;
            this.soapVersion = soapVersion;
        }
        
        @Override
        final Message createMessage(final Object[] methodArgs) {
            return JAXBMessage.create(this.bridge, this.build(methodArgs), this.soapVersion);
        }
        
        abstract Object build(final Object[] p0);
    }
    
    static final class Bare extends JAXB
    {
        private final int methodPos;
        private final ValueGetter getter;
        
        Bare(final ParameterImpl p, final SOAPVersion soapVersion, final ValueGetter getter) {
            super(p.getXMLBridge(), soapVersion);
            this.methodPos = p.getIndex();
            this.getter = getter;
        }
        
        @Override
        Object build(final Object[] methodArgs) {
            return this.getter.get(methodArgs[this.methodPos]);
        }
    }
    
    abstract static class Wrapped extends JAXB
    {
        protected final int[] indices;
        protected final ValueGetter[] getters;
        protected XMLBridge[] parameterBridges;
        protected List<ParameterImpl> children;
        
        protected Wrapped(final WrapperParameter wp, final SOAPVersion soapVersion, final ValueGetterFactory getter) {
            super(wp.getXMLBridge(), soapVersion);
            this.children = wp.getWrapperChildren();
            this.indices = new int[this.children.size()];
            this.getters = new ValueGetter[this.children.size()];
            for (int i = 0; i < this.indices.length; ++i) {
                final ParameterImpl p = this.children.get(i);
                this.indices[i] = p.getIndex();
                this.getters[i] = getter.get(p);
            }
        }
        
        protected WrapperComposite buildWrapperComposite(final Object[] methodArgs) {
            final WrapperComposite cs = new WrapperComposite();
            cs.bridges = this.parameterBridges;
            cs.values = new Object[this.parameterBridges.length];
            for (int i = this.indices.length - 1; i >= 0; --i) {
                final Object arg = this.getters[i].get(methodArgs[this.indices[i]]);
                if (arg == null) {
                    throw new WebServiceException("Method Parameter: " + this.children.get(i).getName() + " cannot be null. This is BP 1.1 R2211 violation.");
                }
                cs.values[i] = arg;
            }
            return cs;
        }
    }
    
    static final class DocLit extends Wrapped
    {
        private final PropertyAccessor[] accessors;
        private final Class wrapper;
        private BindingContext bindingContext;
        private boolean dynamicWrapper;
        
        DocLit(final WrapperParameter wp, final SOAPVersion soapVersion, final ValueGetterFactory getter) {
            super(wp, soapVersion, getter);
            this.bindingContext = wp.getOwner().getBindingContext();
            this.wrapper = (Class)wp.getXMLBridge().getTypeInfo().type;
            this.dynamicWrapper = WrapperComposite.class.equals(this.wrapper);
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
                        this.accessors[i] = p.getOwner().getBindingContext().getElementPropertyAccessor((Class<Object>)this.wrapper, name.getNamespaceURI(), name.getLocalPart());
                    }
                    catch (final JAXBException e) {
                        throw new WebServiceException(this.wrapper + " do not have a property of the name " + name, e);
                    }
                }
            }
        }
        
        @Override
        Object build(final Object[] methodArgs) {
            if (this.dynamicWrapper) {
                return this.buildWrapperComposite(methodArgs);
            }
            try {
                final Object bean = this.bindingContext.newWrapperInstace(this.wrapper);
                for (int i = this.indices.length - 1; i >= 0; --i) {
                    this.accessors[i].set(bean, this.getters[i].get(methodArgs[this.indices[i]]));
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
    
    static final class RpcLit extends Wrapped
    {
        RpcLit(final WrapperParameter wp, final SOAPVersion soapVersion, final ValueGetterFactory getter) {
            super(wp, soapVersion, getter);
            assert wp.getTypeInfo().type == WrapperComposite.class;
            this.parameterBridges = new XMLBridge[this.children.size()];
            for (int i = 0; i < this.parameterBridges.length; ++i) {
                this.parameterBridges[i] = this.children.get(i).getXMLBridge();
            }
        }
        
        @Override
        Object build(final Object[] methodArgs) {
            return this.buildWrapperComposite(methodArgs);
        }
    }
}
