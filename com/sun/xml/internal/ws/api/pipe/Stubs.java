package com.sun.xml.internal.ws.api.pipe;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import com.sun.xml.internal.ws.developer.WSBindingProvider;
import com.sun.xml.internal.ws.client.sei.SEIStub;
import com.sun.xml.internal.ws.model.SOAPSEIModel;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.client.dispatch.PacketDispatch;
import com.sun.xml.internal.ws.client.dispatch.MessageDispatch;
import com.sun.xml.internal.ws.client.dispatch.JAXBDispatch;
import javax.xml.bind.JAXBContext;
import com.sun.xml.internal.ws.api.message.Packet;
import javax.xml.ws.WebServiceException;
import com.sun.xml.internal.ws.api.message.Message;
import javax.xml.transform.Source;
import com.sun.xml.internal.ws.client.dispatch.DataSourceDispatch;
import javax.activation.DataSource;
import com.sun.xml.internal.ws.api.client.WSPortInfo;
import com.sun.xml.internal.ws.client.dispatch.SOAPMessageDispatch;
import com.sun.xml.internal.ws.binding.BindingImpl;
import com.sun.xml.internal.ws.client.WSServiceDelegate;
import com.sun.xml.internal.ws.client.dispatch.DispatchImpl;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.Dispatch;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import javax.xml.ws.Service;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.WSService;
import javax.xml.namespace.QName;

public abstract class Stubs
{
    private Stubs() {
    }
    
    @Deprecated
    public static Dispatch<SOAPMessage> createSAAJDispatch(final QName portName, final WSService owner, final WSBinding binding, final Service.Mode mode, final Tube next, @Nullable final WSEndpointReference epr) {
        DispatchImpl.checkValidSOAPMessageDispatch(binding, mode);
        return new SOAPMessageDispatch(portName, mode, (WSServiceDelegate)owner, next, (BindingImpl)binding, epr);
    }
    
    public static Dispatch<SOAPMessage> createSAAJDispatch(final WSPortInfo portInfo, final WSBinding binding, final Service.Mode mode, @Nullable final WSEndpointReference epr) {
        DispatchImpl.checkValidSOAPMessageDispatch(binding, mode);
        return new SOAPMessageDispatch(portInfo, mode, (BindingImpl)binding, epr);
    }
    
    @Deprecated
    public static Dispatch<DataSource> createDataSourceDispatch(final QName portName, final WSService owner, final WSBinding binding, final Service.Mode mode, final Tube next, @Nullable final WSEndpointReference epr) {
        DispatchImpl.checkValidDataSourceDispatch(binding, mode);
        return new DataSourceDispatch(portName, mode, (WSServiceDelegate)owner, next, (BindingImpl)binding, epr);
    }
    
    public static Dispatch<DataSource> createDataSourceDispatch(final WSPortInfo portInfo, final WSBinding binding, final Service.Mode mode, @Nullable final WSEndpointReference epr) {
        DispatchImpl.checkValidDataSourceDispatch(binding, mode);
        return new DataSourceDispatch(portInfo, mode, (BindingImpl)binding, epr);
    }
    
    @Deprecated
    public static Dispatch<Source> createSourceDispatch(final QName portName, final WSService owner, final WSBinding binding, final Service.Mode mode, final Tube next, @Nullable final WSEndpointReference epr) {
        return DispatchImpl.createSourceDispatch(portName, mode, (WSServiceDelegate)owner, next, (BindingImpl)binding, epr);
    }
    
    public static Dispatch<Source> createSourceDispatch(final WSPortInfo portInfo, final WSBinding binding, final Service.Mode mode, @Nullable final WSEndpointReference epr) {
        return DispatchImpl.createSourceDispatch(portInfo, mode, (BindingImpl)binding, epr);
    }
    
    public static <T> Dispatch<T> createDispatch(final QName portName, final WSService owner, final WSBinding binding, final Class<T> clazz, final Service.Mode mode, final Tube next, @Nullable final WSEndpointReference epr) {
        if (clazz == SOAPMessage.class) {
            return (Dispatch<T>)createSAAJDispatch(portName, owner, binding, mode, next, epr);
        }
        if (clazz == Source.class) {
            return (Dispatch<T>)createSourceDispatch(portName, owner, binding, mode, next, epr);
        }
        if (clazz == DataSource.class) {
            return (Dispatch<T>)createDataSourceDispatch(portName, owner, binding, mode, next, epr);
        }
        if (clazz == Message.class) {
            if (mode == Service.Mode.MESSAGE) {
                return (Dispatch<T>)createMessageDispatch(portName, owner, binding, next, epr);
            }
            throw new WebServiceException(mode + " not supported with Dispatch<Message>");
        }
        else {
            if (clazz == Packet.class) {
                return (Dispatch<T>)createPacketDispatch(portName, owner, binding, next, epr);
            }
            throw new WebServiceException("Unknown class type " + clazz.getName());
        }
    }
    
    public static <T> Dispatch<T> createDispatch(final WSPortInfo portInfo, final WSService owner, final WSBinding binding, final Class<T> clazz, final Service.Mode mode, @Nullable final WSEndpointReference epr) {
        if (clazz == SOAPMessage.class) {
            return (Dispatch<T>)createSAAJDispatch(portInfo, binding, mode, epr);
        }
        if (clazz == Source.class) {
            return (Dispatch<T>)createSourceDispatch(portInfo, binding, mode, epr);
        }
        if (clazz == DataSource.class) {
            return (Dispatch<T>)createDataSourceDispatch(portInfo, binding, mode, epr);
        }
        if (clazz == Message.class) {
            if (mode == Service.Mode.MESSAGE) {
                return (Dispatch<T>)createMessageDispatch(portInfo, binding, epr);
            }
            throw new WebServiceException(mode + " not supported with Dispatch<Message>");
        }
        else {
            if (clazz != Packet.class) {
                throw new WebServiceException("Unknown class type " + clazz.getName());
            }
            if (mode == Service.Mode.MESSAGE) {
                return (Dispatch<T>)createPacketDispatch(portInfo, binding, epr);
            }
            throw new WebServiceException(mode + " not supported with Dispatch<Packet>");
        }
    }
    
    @Deprecated
    public static Dispatch<Object> createJAXBDispatch(final QName portName, final WSService owner, final WSBinding binding, final JAXBContext jaxbContext, final Service.Mode mode, final Tube next, @Nullable final WSEndpointReference epr) {
        return new JAXBDispatch(portName, jaxbContext, mode, (WSServiceDelegate)owner, next, (BindingImpl)binding, epr);
    }
    
    public static Dispatch<Object> createJAXBDispatch(final WSPortInfo portInfo, final WSBinding binding, final JAXBContext jaxbContext, final Service.Mode mode, @Nullable final WSEndpointReference epr) {
        return new JAXBDispatch(portInfo, jaxbContext, mode, (BindingImpl)binding, epr);
    }
    
    @Deprecated
    public static Dispatch<Message> createMessageDispatch(final QName portName, final WSService owner, final WSBinding binding, final Tube next, @Nullable final WSEndpointReference epr) {
        return new MessageDispatch(portName, (WSServiceDelegate)owner, next, (BindingImpl)binding, epr);
    }
    
    public static Dispatch<Message> createMessageDispatch(final WSPortInfo portInfo, final WSBinding binding, @Nullable final WSEndpointReference epr) {
        return new MessageDispatch(portInfo, (BindingImpl)binding, epr);
    }
    
    public static Dispatch<Packet> createPacketDispatch(final QName portName, final WSService owner, final WSBinding binding, final Tube next, @Nullable final WSEndpointReference epr) {
        return new PacketDispatch(portName, (WSServiceDelegate)owner, next, (BindingImpl)binding, epr);
    }
    
    public static Dispatch<Packet> createPacketDispatch(final WSPortInfo portInfo, final WSBinding binding, @Nullable final WSEndpointReference epr) {
        return new PacketDispatch(portInfo, (BindingImpl)binding, epr);
    }
    
    public <T> T createPortProxy(final WSService service, final WSBinding binding, final SEIModel model, final Class<T> portInterface, final Tube next, @Nullable final WSEndpointReference epr) {
        final SEIStub ps = new SEIStub((WSServiceDelegate)service, (BindingImpl)binding, (SOAPSEIModel)model, next, epr);
        return portInterface.cast(Proxy.newProxyInstance(portInterface.getClassLoader(), new Class[] { portInterface, WSBindingProvider.class }, ps));
    }
    
    public <T> T createPortProxy(final WSPortInfo portInfo, final WSBinding binding, final SEIModel model, final Class<T> portInterface, @Nullable final WSEndpointReference epr) {
        final SEIStub ps = new SEIStub(portInfo, (BindingImpl)binding, (SOAPSEIModel)model, epr);
        return portInterface.cast(Proxy.newProxyInstance(portInterface.getClassLoader(), new Class[] { portInterface, WSBindingProvider.class }, ps));
    }
}
