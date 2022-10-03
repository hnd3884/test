package com.sun.xml.internal.ws.client.sei;

import com.sun.xml.internal.ws.api.message.Message;
import com.oracle.webservices.internal.api.databinding.JavaCallInfo;
import javax.xml.stream.XMLStreamException;
import javax.xml.bind.JAXBException;
import com.sun.xml.internal.ws.encoding.soap.DeserializationException;
import com.oracle.webservices.internal.api.message.MessageContext;
import javax.xml.ws.WebServiceException;
import com.sun.xml.internal.ws.resources.DispatchMessages;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.client.RequestContext;
import com.sun.xml.internal.ws.client.ResponseContextReceiver;
import com.sun.xml.internal.ws.model.JavaMethodImpl;

final class SyncMethodHandler extends MethodHandler
{
    final boolean isVoid;
    final boolean isOneway;
    final JavaMethodImpl javaMethod;
    
    SyncMethodHandler(final SEIStub owner, final JavaMethodImpl jm) {
        super(owner, jm.getMethod());
        this.javaMethod = jm;
        this.isVoid = Void.TYPE.equals(jm.getMethod().getReturnType());
        this.isOneway = jm.getMEP().isOneWay();
    }
    
    @Override
    Object invoke(final Object proxy, final Object[] args) throws Throwable {
        return this.invoke(proxy, args, this.owner.requestContext, this.owner);
    }
    
    Object invoke(final Object proxy, final Object[] args, final RequestContext rc, final ResponseContextReceiver receiver) throws Throwable {
        JavaCallInfo call = this.owner.databinding.createJavaCallInfo(this.method, args);
        final Packet req = (Packet)this.owner.databinding.serializeRequest(call);
        final Packet reply = this.owner.doProcess(req, rc, receiver);
        final Message msg = reply.getMessage();
        if (msg == null) {
            if (!this.isOneway || !this.isVoid) {
                throw new WebServiceException(DispatchMessages.INVALID_RESPONSE());
            }
            return null;
        }
        else {
            try {
                call = this.owner.databinding.deserializeResponse(reply, call);
                if (call.getException() != null) {
                    throw call.getException();
                }
                return call.getReturnValue();
            }
            catch (final JAXBException e) {
                throw new DeserializationException(DispatchMessages.INVALID_RESPONSE_DESERIALIZATION(), new Object[] { e });
            }
            catch (final XMLStreamException e2) {
                throw new DeserializationException(DispatchMessages.INVALID_RESPONSE_DESERIALIZATION(), new Object[] { e2 });
            }
            finally {
                if (reply.transportBackChannel != null) {
                    reply.transportBackChannel.close();
                }
            }
        }
    }
    
    ValueGetterFactory getValueGetterFactory() {
        return ValueGetterFactory.SYNC;
    }
}
