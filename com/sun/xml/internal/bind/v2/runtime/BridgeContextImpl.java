package com.sun.xml.internal.bind.v2.runtime;

import javax.xml.bind.attachment.AttachmentUnmarshaller;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.bind.JAXBException;
import javax.xml.bind.ValidationEventHandler;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallerImpl;
import com.sun.xml.internal.bind.api.BridgeContext;

public final class BridgeContextImpl extends BridgeContext
{
    public final UnmarshallerImpl unmarshaller;
    public final MarshallerImpl marshaller;
    
    BridgeContextImpl(final JAXBContextImpl context) {
        this.unmarshaller = context.createUnmarshaller();
        this.marshaller = context.createMarshaller();
    }
    
    @Override
    public void setErrorHandler(final ValidationEventHandler handler) {
        try {
            this.unmarshaller.setEventHandler(handler);
            this.marshaller.setEventHandler(handler);
        }
        catch (final JAXBException e) {
            throw new Error(e);
        }
    }
    
    @Override
    public void setAttachmentMarshaller(final AttachmentMarshaller m) {
        this.marshaller.setAttachmentMarshaller(m);
    }
    
    @Override
    public void setAttachmentUnmarshaller(final AttachmentUnmarshaller u) {
        this.unmarshaller.setAttachmentUnmarshaller(u);
    }
    
    @Override
    public AttachmentMarshaller getAttachmentMarshaller() {
        return this.marshaller.getAttachmentMarshaller();
    }
    
    @Override
    public AttachmentUnmarshaller getAttachmentUnmarshaller() {
        return this.unmarshaller.getAttachmentUnmarshaller();
    }
}
