package com.sun.xml.internal.ws.protocol.soap;

import com.sun.xml.internal.ws.api.pipe.helper.AbstractTubeImpl;
import com.sun.xml.internal.ws.api.pipe.NextAction;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import com.sun.xml.internal.ws.client.HandlerConfiguration;
import com.sun.xml.internal.ws.api.pipe.Tube;
import javax.xml.namespace.QName;
import java.util.Set;
import com.sun.xml.internal.ws.api.pipe.ServerTubeAssemblerContext;

public class ServerMUTube extends MUTube
{
    private ServerTubeAssemblerContext tubeContext;
    private final Set<String> roles;
    private final Set<QName> handlerKnownHeaders;
    
    public ServerMUTube(final ServerTubeAssemblerContext tubeContext, final Tube next) {
        super(tubeContext.getEndpoint().getBinding(), next);
        this.tubeContext = tubeContext;
        final HandlerConfiguration handlerConfig = this.binding.getHandlerConfig();
        this.roles = handlerConfig.getRoles();
        this.handlerKnownHeaders = this.binding.getKnownHeaders();
    }
    
    protected ServerMUTube(final ServerMUTube that, final TubeCloner cloner) {
        super(that, cloner);
        this.tubeContext = that.tubeContext;
        this.roles = that.roles;
        this.handlerKnownHeaders = that.handlerKnownHeaders;
    }
    
    @Override
    public NextAction processRequest(final Packet request) {
        final Set<QName> misUnderstoodHeaders = this.getMisUnderstoodHeaders(request.getMessage().getHeaders(), this.roles, this.handlerKnownHeaders);
        if (misUnderstoodHeaders == null || misUnderstoodHeaders.isEmpty()) {
            return this.doInvoke(super.next, request);
        }
        return this.doReturnWith(request.createServerResponse(this.createMUSOAPFaultMessage(misUnderstoodHeaders), this.tubeContext.getWsdlModel(), this.tubeContext.getSEIModel(), this.tubeContext.getEndpoint().getBinding()));
    }
    
    @Override
    public ServerMUTube copy(final TubeCloner cloner) {
        return new ServerMUTube(this, cloner);
    }
}
