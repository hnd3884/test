package com.sun.xml.internal.ws.server.sei;

import com.oracle.webservices.internal.api.databinding.JavaCallInfo;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.wsdl.DispatchException;
import java.lang.reflect.InvocationTargetException;
import com.oracle.webservices.internal.api.message.MessageContext;
import com.sun.xml.internal.ws.api.pipe.NextAction;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.server.Invoker;
import com.sun.xml.internal.ws.model.AbstractSEIModelImpl;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.server.InvokerTube;

public class SEIInvokerTube extends InvokerTube
{
    private final WSBinding binding;
    private final AbstractSEIModelImpl model;
    
    public SEIInvokerTube(final AbstractSEIModelImpl model, final Invoker invoker, final WSBinding binding) {
        super(invoker);
        this.binding = binding;
        this.model = model;
    }
    
    @NotNull
    @Override
    public NextAction processRequest(@NotNull final Packet req) {
        final JavaCallInfo call = this.model.getDatabinding().deserializeRequest(req);
        if (call.getException() == null) {
            try {
                if (req.getMessage().isOneWay(this.model.getPort()) && req.transportBackChannel != null) {
                    req.transportBackChannel.close();
                }
                final Object ret = this.getInvoker(req).invoke(req, call.getMethod(), call.getParameters());
                call.setReturnValue(ret);
            }
            catch (final InvocationTargetException e) {
                call.setException(e);
            }
            catch (final Exception e2) {
                call.setException(e2);
            }
        }
        else if (call.getException() instanceof DispatchException) {
            final DispatchException e3 = (DispatchException)call.getException();
            return this.doReturnWith(req.createServerResponse(e3.fault, this.model.getPort(), null, this.binding));
        }
        Packet res = (Packet)this.model.getDatabinding().serializeResponse(call);
        res = req.relateServerResponse(res, req.endpoint.getPort(), this.model, req.endpoint.getBinding());
        assert res != null;
        return this.doReturnWith(res);
    }
    
    @NotNull
    @Override
    public NextAction processResponse(@NotNull final Packet response) {
        return this.doReturnWith(response);
    }
    
    @NotNull
    @Override
    public NextAction processException(@NotNull final Throwable t) {
        return this.doThrow(t);
    }
}
