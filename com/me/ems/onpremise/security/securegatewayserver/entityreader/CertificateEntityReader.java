package com.me.ems.onpremise.security.securegatewayserver.entityreader;

import javax.ws.rs.WebApplicationException;
import java.io.IOException;
import java.security.cert.CertificateException;
import javax.ws.rs.ProcessingException;
import java.io.ByteArrayInputStream;
import org.apache.commons.io.IOUtils;
import java.security.cert.CertificateFactory;
import java.io.InputStream;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.MediaType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.Consumes;
import java.security.cert.X509Certificate;
import javax.ws.rs.ext.MessageBodyReader;

@Consumes({ "application/x-x509-user-cert" })
public class CertificateEntityReader implements MessageBodyReader<X509Certificate>
{
    public boolean isReadable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        return type == X509Certificate.class;
    }
    
    public X509Certificate readFrom(final Class<X509Certificate> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, String> httpHeaders, final InputStream entityStream) throws IOException, WebApplicationException {
        try {
            return (X509Certificate)CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(IOUtils.toByteArray(entityStream)));
        }
        catch (final CertificateException certificateException) {
            throw new ProcessingException("Error deserializing a X509Certificate.", (Throwable)certificateException);
        }
    }
}
