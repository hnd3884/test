package com.sun.xml.internal.ws.handler;

import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.WSBinding;
import java.util.Set;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.handler.MessageHandlerContext;

public class MessageHandlerContextImpl extends MessageUpdatableContext implements MessageHandlerContext
{
    @Nullable
    private SEIModel seiModel;
    private Set<String> roles;
    private WSBinding binding;
    @Nullable
    private WSDLPort wsdlModel;
    
    public MessageHandlerContextImpl(@Nullable final SEIModel seiModel, final WSBinding binding, @Nullable final WSDLPort wsdlModel, final Packet packet, final Set<String> roles) {
        super(packet);
        this.seiModel = seiModel;
        this.binding = binding;
        this.wsdlModel = wsdlModel;
        this.roles = roles;
    }
    
    @Override
    public Message getMessage() {
        return this.packet.getMessage();
    }
    
    @Override
    public void setMessage(final Message message) {
        this.packet.setMessage(message);
    }
    
    @Override
    public Set<String> getRoles() {
        return this.roles;
    }
    
    @Override
    public WSBinding getWSBinding() {
        return this.binding;
    }
    
    @Nullable
    @Override
    public SEIModel getSEIModel() {
        return this.seiModel;
    }
    
    @Nullable
    @Override
    public WSDLPort getPort() {
        return this.wsdlModel;
    }
    
    @Override
    void updateMessage() {
    }
    
    @Override
    void setPacketMessage(final Message newMessage) {
        this.setMessage(newMessage);
    }
}
