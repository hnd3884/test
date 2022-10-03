package com.sun.xml.internal.ws.util;

import java.net.URL;
import javax.jws.WebService;
import javax.xml.ws.Service;
import com.sun.xml.internal.ws.api.server.AsyncProvider;
import javax.xml.ws.Provider;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import com.sun.xml.internal.ws.api.databinding.MetadataReader;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import com.sun.xml.internal.ws.handler.HandlerChainsModel;
import com.sun.xml.internal.ws.streaming.XMLStreamReaderUtil;
import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory;
import javax.jws.soap.SOAPMessageHandlers;
import javax.jws.HandlerChain;
import com.sun.xml.internal.ws.model.ReflectAnnotationReader;
import com.sun.xml.internal.ws.server.EndpointFactory;
import com.sun.xml.internal.ws.api.WSBinding;
import javax.xml.namespace.QName;
import com.sun.istack.internal.NotNull;
import java.util.logging.Logger;

public class HandlerAnnotationProcessor
{
    private static final Logger logger;
    
    public static HandlerAnnotationInfo buildHandlerInfo(@NotNull Class<?> clazz, final QName serviceName, final QName portName, final WSBinding binding) {
        MetadataReader metadataReader = EndpointFactory.getExternalMetadatReader(clazz, binding);
        if (metadataReader == null) {
            metadataReader = new ReflectAnnotationReader();
        }
        HandlerChain handlerChain = metadataReader.getAnnotation(HandlerChain.class, clazz);
        if (handlerChain == null) {
            clazz = getSEI(clazz, metadataReader);
            if (clazz != null) {
                handlerChain = metadataReader.getAnnotation(HandlerChain.class, clazz);
            }
            if (handlerChain == null) {
                return null;
            }
        }
        if (clazz.getAnnotation(SOAPMessageHandlers.class) != null) {
            throw new UtilException("util.handler.cannot.combine.soapmessagehandlers", new Object[0]);
        }
        final InputStream iStream = getFileAsStream(clazz, handlerChain);
        final XMLStreamReader reader = XMLStreamReaderFactory.create(null, iStream, true);
        XMLStreamReaderUtil.nextElementContent(reader);
        final HandlerAnnotationInfo handlerAnnInfo = HandlerChainsModel.parseHandlerFile(reader, clazz.getClassLoader(), serviceName, portName, binding);
        try {
            reader.close();
            iStream.close();
        }
        catch (final XMLStreamException e) {
            e.printStackTrace();
            throw new UtilException(e.getMessage(), new Object[0]);
        }
        catch (final IOException e2) {
            e2.printStackTrace();
            throw new UtilException(e2.getMessage(), new Object[0]);
        }
        return handlerAnnInfo;
    }
    
    public static HandlerChainsModel buildHandlerChainsModel(final Class<?> clazz) {
        if (clazz == null) {
            return null;
        }
        final HandlerChain handlerChain = clazz.getAnnotation(HandlerChain.class);
        if (handlerChain == null) {
            return null;
        }
        final InputStream iStream = getFileAsStream(clazz, handlerChain);
        final XMLStreamReader reader = XMLStreamReaderFactory.create(null, iStream, true);
        XMLStreamReaderUtil.nextElementContent(reader);
        final HandlerChainsModel handlerChainsModel = HandlerChainsModel.parseHandlerConfigFile(clazz, reader);
        try {
            reader.close();
            iStream.close();
        }
        catch (final XMLStreamException e) {
            e.printStackTrace();
            throw new UtilException(e.getMessage(), new Object[0]);
        }
        catch (final IOException e2) {
            e2.printStackTrace();
            throw new UtilException(e2.getMessage(), new Object[0]);
        }
        return handlerChainsModel;
    }
    
    static Class getClass(final String className) {
        try {
            return Thread.currentThread().getContextClassLoader().loadClass(className);
        }
        catch (final ClassNotFoundException e) {
            throw new UtilException("util.handler.class.not.found", new Object[] { className });
        }
    }
    
    static Class getSEI(Class<?> clazz, MetadataReader metadataReader) {
        if (metadataReader == null) {
            metadataReader = new ReflectAnnotationReader();
        }
        if (Provider.class.isAssignableFrom(clazz) || AsyncProvider.class.isAssignableFrom(clazz)) {
            return null;
        }
        if (Service.class.isAssignableFrom(clazz)) {
            return null;
        }
        final WebService webService = metadataReader.getAnnotation(WebService.class, clazz);
        if (webService == null) {
            throw new UtilException("util.handler.no.webservice.annotation", new Object[] { clazz.getCanonicalName() });
        }
        final String ei = webService.endpointInterface();
        if (ei.length() <= 0) {
            return null;
        }
        clazz = getClass(webService.endpointInterface());
        final WebService ws = metadataReader.getAnnotation(WebService.class, clazz);
        if (ws == null) {
            throw new UtilException("util.handler.endpoint.interface.no.webservice", new Object[] { webService.endpointInterface() });
        }
        return clazz;
    }
    
    static InputStream getFileAsStream(final Class clazz, final HandlerChain chain) {
        URL url = clazz.getResource(chain.file());
        if (url == null) {
            url = Thread.currentThread().getContextClassLoader().getResource(chain.file());
        }
        if (url == null) {
            String tmp = clazz.getPackage().getName();
            tmp = tmp.replace('.', '/');
            tmp = tmp + "/" + chain.file();
            url = Thread.currentThread().getContextClassLoader().getResource(tmp);
        }
        if (url == null) {
            throw new UtilException("util.failed.to.find.handlerchain.file", new Object[] { clazz.getName(), chain.file() });
        }
        try {
            return url.openStream();
        }
        catch (final IOException e) {
            throw new UtilException("util.failed.to.parse.handlerchain.file", new Object[] { clazz.getName(), chain.file() });
        }
    }
    
    static {
        logger = Logger.getLogger("com.sun.xml.internal.ws.util");
    }
}
