package org.apache.xmlbeans.impl.soap;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

public abstract class SOAPMessage
{
    public static final String CHARACTER_SET_ENCODING = "javax.xml.soap.character-set-encoding";
    public static final String WRITE_XML_DECLARATION = "javax.xml.soap.write-xml-declaration";
    
    public abstract String getContentDescription();
    
    public abstract void setContentDescription(final String p0);
    
    public abstract SOAPPart getSOAPPart();
    
    public abstract void removeAllAttachments();
    
    public abstract int countAttachments();
    
    public abstract Iterator getAttachments();
    
    public abstract Iterator getAttachments(final MimeHeaders p0);
    
    public abstract void addAttachmentPart(final AttachmentPart p0);
    
    public abstract AttachmentPart createAttachmentPart();
    
    public abstract MimeHeaders getMimeHeaders();
    
    public AttachmentPart createAttachmentPart(final Object content, final String contentType) {
        final AttachmentPart attachmentpart = this.createAttachmentPart();
        attachmentpart.setContent(content, contentType);
        return attachmentpart;
    }
    
    public abstract void saveChanges() throws SOAPException;
    
    public abstract boolean saveRequired();
    
    public abstract void writeTo(final OutputStream p0) throws SOAPException, IOException;
    
    public abstract SOAPBody getSOAPBody() throws SOAPException;
    
    public abstract SOAPHeader getSOAPHeader() throws SOAPException;
    
    public abstract void setProperty(final String p0, final Object p1) throws SOAPException;
    
    public abstract Object getProperty(final String p0) throws SOAPException;
}
