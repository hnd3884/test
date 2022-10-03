package com.sun.xml.internal.ws.client.dispatch;

import com.sun.xml.internal.ws.api.message.Message;
import javax.xml.ws.WebServiceException;
import com.sun.xml.internal.ws.api.WSFeatureList;
import com.sun.xml.internal.ws.encoding.xml.XMLMessage;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.client.WSPortInfo;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.binding.BindingImpl;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.client.WSServiceDelegate;
import javax.xml.ws.Service;
import javax.xml.namespace.QName;
import javax.activation.DataSource;

public class DataSourceDispatch extends DispatchImpl<DataSource>
{
    @Deprecated
    public DataSourceDispatch(final QName port, final Service.Mode mode, final WSServiceDelegate service, final Tube pipe, final BindingImpl binding, final WSEndpointReference epr) {
        super(port, mode, service, pipe, binding, epr);
    }
    
    public DataSourceDispatch(final WSPortInfo portInfo, final Service.Mode mode, final BindingImpl binding, final WSEndpointReference epr) {
        super(portInfo, mode, binding, epr);
    }
    
    @Override
    Packet createPacket(final DataSource arg) {
        switch (this.mode) {
            case PAYLOAD: {
                throw new IllegalArgumentException("DataSource use is not allowed in Service.Mode.PAYLOAD\n");
            }
            case MESSAGE: {
                return new Packet(XMLMessage.create(arg, this.binding.getFeatures()));
            }
            default: {
                throw new WebServiceException("Unrecognized message mode");
            }
        }
    }
    
    @Override
    DataSource toReturnValue(final Packet response) {
        final Message message = response.getInternalMessage();
        return (message instanceof XMLMessage.MessageDataSource) ? ((XMLMessage.MessageDataSource)message).getDataSource() : XMLMessage.getDataSource(message, this.binding.getFeatures());
    }
}
