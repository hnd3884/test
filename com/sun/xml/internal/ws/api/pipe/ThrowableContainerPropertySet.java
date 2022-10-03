package com.sun.xml.internal.ws.api.pipe;

import com.oracle.webservices.internal.api.message.PropertySet;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.message.Message;
import com.oracle.webservices.internal.api.message.BasePropertySet;

public class ThrowableContainerPropertySet extends BasePropertySet
{
    public static final String FIBER_COMPLETION_THROWABLE = "com.sun.xml.internal.ws.api.pipe.fiber-completion-throwable";
    private Throwable throwable;
    public static final String FAULT_MESSAGE = "com.sun.xml.internal.ws.api.pipe.fiber-completion-fault-message";
    private Message faultMessage;
    public static final String RESPONSE_PACKET = "com.sun.xml.internal.ws.api.pipe.fiber-completion-response-packet";
    private Packet responsePacket;
    public static final String IS_FAULT_CREATED = "com.sun.xml.internal.ws.api.pipe.fiber-completion-is-fault-created";
    private boolean isFaultCreated;
    private static final PropertyMap model;
    
    public ThrowableContainerPropertySet(final Throwable throwable) {
        this.isFaultCreated = false;
        this.throwable = throwable;
    }
    
    @PropertySet.Property({ "com.sun.xml.internal.ws.api.pipe.fiber-completion-throwable" })
    public Throwable getThrowable() {
        return this.throwable;
    }
    
    public void setThrowable(final Throwable throwable) {
        this.throwable = throwable;
    }
    
    @PropertySet.Property({ "com.sun.xml.internal.ws.api.pipe.fiber-completion-fault-message" })
    public Message getFaultMessage() {
        return this.faultMessage;
    }
    
    public void setFaultMessage(final Message faultMessage) {
        this.faultMessage = faultMessage;
    }
    
    @PropertySet.Property({ "com.sun.xml.internal.ws.api.pipe.fiber-completion-response-packet" })
    public Packet getResponsePacket() {
        return this.responsePacket;
    }
    
    public void setResponsePacket(final Packet responsePacket) {
        this.responsePacket = responsePacket;
    }
    
    @PropertySet.Property({ "com.sun.xml.internal.ws.api.pipe.fiber-completion-is-fault-created" })
    public boolean isFaultCreated() {
        return this.isFaultCreated;
    }
    
    public void setFaultCreated(final boolean isFaultCreated) {
        this.isFaultCreated = isFaultCreated;
    }
    
    @Override
    protected PropertyMap getPropertyMap() {
        return ThrowableContainerPropertySet.model;
    }
    
    static {
        model = BasePropertySet.parse(ThrowableContainerPropertySet.class);
    }
}
