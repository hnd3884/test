package com.sun.mail.handlers;

import javax.mail.Message;
import java.io.OutputStream;
import javax.mail.MessageContext;
import javax.mail.MessagingException;
import java.io.IOException;
import javax.mail.internet.MimeMessage;
import javax.mail.Authenticator;
import javax.mail.Session;
import java.util.Properties;
import javax.mail.MessageAware;
import javax.activation.DataSource;
import javax.activation.ActivationDataFlavor;

public class message_rfc822 extends handler_base
{
    private static ActivationDataFlavor[] ourDataFlavor;
    
    @Override
    protected ActivationDataFlavor[] getDataFlavors() {
        return message_rfc822.ourDataFlavor;
    }
    
    @Override
    public Object getContent(final DataSource ds) throws IOException {
        try {
            Session session;
            if (ds instanceof MessageAware) {
                final MessageContext mc = ((MessageAware)ds).getMessageContext();
                session = mc.getSession();
            }
            else {
                session = Session.getDefaultInstance(new Properties(), null);
            }
            return new MimeMessage(session, ds.getInputStream());
        }
        catch (final MessagingException me) {
            final IOException ioex = new IOException("Exception creating MimeMessage in message/rfc822 DataContentHandler");
            ioex.initCause(me);
            throw ioex;
        }
    }
    
    @Override
    public void writeTo(final Object obj, final String mimeType, final OutputStream os) throws IOException {
        if (obj instanceof Message) {
            final Message m = (Message)obj;
            try {
                m.writeTo(os);
            }
            catch (final MessagingException me) {
                final IOException ioex = new IOException("Exception writing message");
                ioex.initCause(me);
                throw ioex;
            }
            return;
        }
        throw new IOException("unsupported object");
    }
    
    static {
        message_rfc822.ourDataFlavor = new ActivationDataFlavor[] { new ActivationDataFlavor(Message.class, "message/rfc822", "Message") };
    }
}
