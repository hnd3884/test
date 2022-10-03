package com.sun.xml.internal.ws.client;

import java.security.AccessController;
import java.lang.reflect.Method;
import javax.xml.ws.WebEndpoint;
import java.io.IOException;
import com.sun.xml.internal.ws.util.JAXWSUtils;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceClient;
import java.security.PrivilegedAction;
import javax.xml.namespace.QName;
import java.util.ArrayList;

final class SCAnnotations
{
    final ArrayList<QName> portQNames;
    final ArrayList<Class> classes;
    
    SCAnnotations(final Class<?> sc) {
        this.portQNames = new ArrayList<QName>();
        this.classes = new ArrayList<Class>();
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                final WebServiceClient wsc = sc.getAnnotation(WebServiceClient.class);
                if (wsc == null) {
                    throw new WebServiceException("Service Interface Annotations required, exiting...");
                }
                final String tns = wsc.targetNamespace();
                try {
                    JAXWSUtils.getFileOrURL(wsc.wsdlLocation());
                }
                catch (final IOException e) {
                    throw new WebServiceException(e);
                }
                for (final Method method : sc.getDeclaredMethods()) {
                    final WebEndpoint webEndpoint = method.getAnnotation(WebEndpoint.class);
                    if (webEndpoint != null) {
                        final String endpointName = webEndpoint.name();
                        final QName portQName = new QName(tns, endpointName);
                        SCAnnotations.this.portQNames.add(portQName);
                    }
                    final Class<?> seiClazz = method.getReturnType();
                    if (seiClazz != Void.TYPE) {
                        SCAnnotations.this.classes.add(seiClazz);
                    }
                }
                return null;
            }
        });
    }
}
