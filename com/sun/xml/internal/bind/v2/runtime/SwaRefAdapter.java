package com.sun.xml.internal.bind.v2.runtime;

import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.bind.attachment.AttachmentUnmarshaller;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallingContext;
import javax.activation.DataHandler;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public final class SwaRefAdapter extends XmlAdapter<String, DataHandler>
{
    @Override
    public DataHandler unmarshal(final String cid) {
        final AttachmentUnmarshaller au = UnmarshallingContext.getInstance().parent.getAttachmentUnmarshaller();
        return au.getAttachmentAsDataHandler(cid);
    }
    
    @Override
    public String marshal(final DataHandler data) {
        if (data == null) {
            return null;
        }
        final AttachmentMarshaller am = XMLSerializer.getInstance().attachmentMarshaller;
        return am.addSwaRefAttachment(data);
    }
}
