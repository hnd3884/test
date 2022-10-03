package com.sun.xml.internal.ws.protocol.soap;

import com.sun.xml.internal.ws.api.pipe.helper.AbstractTubeImpl;
import com.sun.istack.internal.NotNull;
import javax.xml.namespace.QName;
import java.util.Set;
import com.sun.xml.internal.ws.client.HandlerConfiguration;
import com.sun.xml.internal.ws.api.pipe.NextAction;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.WSBinding;

public class ClientMUTube extends MUTube
{
    public ClientMUTube(final WSBinding binding, final Tube next) {
        super(binding, next);
    }
    
    protected ClientMUTube(final ClientMUTube that, final TubeCloner cloner) {
        super(that, cloner);
    }
    
    @NotNull
    @Override
    public NextAction processResponse(final Packet response) {
        if (response.getMessage() == null) {
            return super.processResponse(response);
        }
        HandlerConfiguration handlerConfig = response.handlerConfig;
        if (handlerConfig == null) {
            handlerConfig = this.binding.getHandlerConfig();
        }
        final Set<QName> misUnderstoodHeaders = this.getMisUnderstoodHeaders(response.getMessage().getHeaders(), handlerConfig.getRoles(), this.binding.getKnownHeaders());
        if (misUnderstoodHeaders == null || misUnderstoodHeaders.isEmpty()) {
            return super.processResponse(response);
        }
        throw this.createMUSOAPFaultException(misUnderstoodHeaders);
    }
    
    @Override
    public ClientMUTube copy(final TubeCloner cloner) {
        return new ClientMUTube(this, cloner);
    }
}
