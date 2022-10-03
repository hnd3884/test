package com.sun.xml.internal.ws.server.sei;

import com.sun.xml.internal.ws.api.message.Headers;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import com.sun.xml.internal.ws.message.JAXBAttachment;
import com.sun.xml.internal.ws.message.DataHandlerAttachment;
import com.sun.xml.internal.ws.api.message.Attachment;
import com.sun.xml.internal.ws.message.ByteArrayAttachment;
import java.util.UUID;
import javax.xml.transform.Source;
import javax.activation.DataHandler;
import java.io.UnsupportedEncodingException;
import javax.xml.ws.WebServiceException;
import java.net.URLEncoder;
import com.sun.xml.internal.ws.model.ParameterImpl;
import com.sun.xml.internal.ws.api.message.Message;

public abstract class MessageFiller
{
    protected final int methodPos;
    
    protected MessageFiller(final int methodPos) {
        this.methodPos = methodPos;
    }
    
    public abstract void fillIn(final Object[] p0, final Object p1, final Message p2);
    
    private static boolean isXMLMimeType(final String mimeType) {
        return mimeType.equals("text/xml") || mimeType.equals("application/xml");
    }
    
    public abstract static class AttachmentFiller extends MessageFiller
    {
        protected final ParameterImpl param;
        protected final ValueGetter getter;
        protected final String mimeType;
        private final String contentIdPart;
        
        protected AttachmentFiller(final ParameterImpl param, final ValueGetter getter) {
            super(param.getIndex());
            this.param = param;
            this.getter = getter;
            this.mimeType = param.getBinding().getMimeType();
            try {
                this.contentIdPart = URLEncoder.encode(param.getPartName(), "UTF-8") + '=';
            }
            catch (final UnsupportedEncodingException e) {
                throw new WebServiceException(e);
            }
        }
        
        public static MessageFiller createAttachmentFiller(final ParameterImpl param, final ValueGetter getter) {
            final Class type = (Class)param.getTypeInfo().type;
            if (DataHandler.class.isAssignableFrom(type) || Source.class.isAssignableFrom(type)) {
                return new DataHandlerFiller(param, getter);
            }
            if (byte[].class == type) {
                return new ByteArrayFiller(param, getter);
            }
            if (isXMLMimeType(param.getBinding().getMimeType())) {
                return new JAXBFiller(param, getter);
            }
            return new DataHandlerFiller(param, getter);
        }
        
        String getContentId() {
            return this.contentIdPart + UUID.randomUUID() + "@jaxws.sun.com";
        }
    }
    
    private static class ByteArrayFiller extends AttachmentFiller
    {
        protected ByteArrayFiller(final ParameterImpl param, final ValueGetter getter) {
            super(param, getter);
        }
        
        @Override
        public void fillIn(final Object[] methodArgs, final Object returnValue, final Message msg) {
            final String contentId = this.getContentId();
            final Object obj = (this.methodPos == -1) ? returnValue : this.getter.get(methodArgs[this.methodPos]);
            if (obj != null) {
                final Attachment att = new ByteArrayAttachment(contentId, (byte[])obj, this.mimeType);
                msg.getAttachments().add(att);
            }
        }
    }
    
    private static class DataHandlerFiller extends AttachmentFiller
    {
        protected DataHandlerFiller(final ParameterImpl param, final ValueGetter getter) {
            super(param, getter);
        }
        
        @Override
        public void fillIn(final Object[] methodArgs, final Object returnValue, final Message msg) {
            final String contentId = this.getContentId();
            final Object obj = (this.methodPos == -1) ? returnValue : this.getter.get(methodArgs[this.methodPos]);
            final DataHandler dh = (DataHandler)((obj instanceof DataHandler) ? obj : new DataHandler(obj, this.mimeType));
            final Attachment att = new DataHandlerAttachment(contentId, dh);
            msg.getAttachments().add(att);
        }
    }
    
    private static class JAXBFiller extends AttachmentFiller
    {
        protected JAXBFiller(final ParameterImpl param, final ValueGetter getter) {
            super(param, getter);
        }
        
        @Override
        public void fillIn(final Object[] methodArgs, final Object returnValue, final Message msg) {
            final String contentId = this.getContentId();
            final Object obj = (this.methodPos == -1) ? returnValue : this.getter.get(methodArgs[this.methodPos]);
            final Attachment att = new JAXBAttachment(contentId, obj, this.param.getXMLBridge(), this.mimeType);
            msg.getAttachments().add(att);
        }
    }
    
    public static final class Header extends MessageFiller
    {
        private final XMLBridge bridge;
        private final ValueGetter getter;
        
        public Header(final int methodPos, final XMLBridge bridge, final ValueGetter getter) {
            super(methodPos);
            this.bridge = bridge;
            this.getter = getter;
        }
        
        @Override
        public void fillIn(final Object[] methodArgs, final Object returnValue, final Message msg) {
            final Object value = (this.methodPos == -1) ? returnValue : this.getter.get(methodArgs[this.methodPos]);
            msg.getHeaders().add(Headers.create(this.bridge, value));
        }
    }
}
