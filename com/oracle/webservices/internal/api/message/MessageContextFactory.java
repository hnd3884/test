package com.oracle.webservices.internal.api.message;

import com.sun.xml.internal.ws.api.SOAPVersion;
import java.util.Iterator;
import com.sun.xml.internal.ws.util.ServiceFinder;
import javax.xml.soap.MimeHeaders;
import java.io.IOException;
import java.io.InputStream;
import com.oracle.webservices.internal.api.EnvelopeStyle;
import javax.xml.transform.Source;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.WebServiceFeature;

public abstract class MessageContextFactory
{
    private static final MessageContextFactory DEFAULT;
    
    protected abstract MessageContextFactory newFactory(final WebServiceFeature... p0);
    
    public abstract MessageContext createContext();
    
    public abstract MessageContext createContext(final SOAPMessage p0);
    
    public abstract MessageContext createContext(final Source p0);
    
    public abstract MessageContext createContext(final Source p0, final EnvelopeStyle.Style p1);
    
    public abstract MessageContext createContext(final InputStream p0, final String p1) throws IOException;
    
    @Deprecated
    public abstract MessageContext createContext(final InputStream p0, final MimeHeaders p1) throws IOException;
    
    public static MessageContextFactory createFactory(final WebServiceFeature... f) {
        return createFactory((ClassLoader)null, f);
    }
    
    public static MessageContextFactory createFactory(final ClassLoader cl, final WebServiceFeature... f) {
        for (final MessageContextFactory factory : ServiceFinder.find(MessageContextFactory.class, cl)) {
            final MessageContextFactory newfac = factory.newFactory(f);
            if (newfac != null) {
                return newfac;
            }
        }
        return new com.sun.xml.internal.ws.api.message.MessageContextFactory(f);
    }
    
    @Deprecated
    public abstract MessageContext doCreate();
    
    @Deprecated
    public abstract MessageContext doCreate(final SOAPMessage p0);
    
    @Deprecated
    public abstract MessageContext doCreate(final Source p0, final SOAPVersion p1);
    
    @Deprecated
    public static MessageContext create(final ClassLoader... classLoader) {
        return serviceFinder(classLoader, new Creator() {
            @Override
            public MessageContext create(final MessageContextFactory f) {
                return f.doCreate();
            }
        });
    }
    
    @Deprecated
    public static MessageContext create(final SOAPMessage m, final ClassLoader... classLoader) {
        return serviceFinder(classLoader, new Creator() {
            @Override
            public MessageContext create(final MessageContextFactory f) {
                return f.doCreate(m);
            }
        });
    }
    
    @Deprecated
    public static MessageContext create(final Source m, final SOAPVersion v, final ClassLoader... classLoader) {
        return serviceFinder(classLoader, new Creator() {
            @Override
            public MessageContext create(final MessageContextFactory f) {
                return f.doCreate(m, v);
            }
        });
    }
    
    @Deprecated
    private static MessageContext serviceFinder(final ClassLoader[] classLoader, final Creator creator) {
        final ClassLoader cl = (classLoader.length == 0) ? null : classLoader[0];
        for (final MessageContextFactory factory : ServiceFinder.find(MessageContextFactory.class, cl)) {
            final MessageContext messageContext = creator.create(factory);
            if (messageContext != null) {
                return messageContext;
            }
        }
        return creator.create(MessageContextFactory.DEFAULT);
    }
    
    static {
        DEFAULT = new com.sun.xml.internal.ws.api.message.MessageContextFactory(new WebServiceFeature[0]);
    }
    
    @Deprecated
    private interface Creator
    {
        MessageContext create(final MessageContextFactory p0);
    }
}
