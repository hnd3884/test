package com.sun.xml.internal.ws.client.dispatch;

import com.sun.xml.internal.ws.api.message.MessageHeaders;
import com.sun.xml.internal.ws.message.source.PayloadSourceMessage;
import com.sun.xml.internal.ws.api.message.Messages;
import com.sun.xml.internal.ws.api.message.Message;
import java.io.IOException;
import javax.xml.transform.stream.StreamSource;
import com.sun.xml.internal.ws.api.WSFeatureList;
import com.sun.xml.internal.ws.encoding.xml.XMLMessage;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.client.WSPortInfo;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.binding.BindingImpl;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.client.WSServiceDelegate;
import javax.xml.ws.Service;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;

final class RESTSourceDispatch extends DispatchImpl<Source>
{
    @Deprecated
    public RESTSourceDispatch(final QName port, final Service.Mode mode, final WSServiceDelegate owner, final Tube pipe, final BindingImpl binding, final WSEndpointReference epr) {
        super(port, mode, owner, pipe, binding, epr);
        assert DispatchImpl.isXMLHttp(binding);
    }
    
    public RESTSourceDispatch(final WSPortInfo portInfo, final Service.Mode mode, final BindingImpl binding, final WSEndpointReference epr) {
        super(portInfo, mode, binding, epr);
        assert DispatchImpl.isXMLHttp(binding);
    }
    
    @Override
    Source toReturnValue(final Packet response) {
        final Message msg = response.getMessage();
        try {
            return new StreamSource(XMLMessage.getDataSource(msg, this.binding.getFeatures()).getInputStream());
        }
        catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    Packet createPacket(final Source msg) {
        Message message;
        if (msg == null) {
            message = Messages.createEmpty(this.soapVersion);
        }
        else {
            message = new PayloadSourceMessage(null, msg, this.setOutboundAttachments(), this.soapVersion);
        }
        return new Packet(message);
    }
}
