package com.sun.xml.internal.ws.api.message;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import javax.xml.transform.Source;
import javax.activation.DataHandler;
import com.sun.istack.internal.NotNull;

public interface Attachment
{
    @NotNull
    String getContentId();
    
    String getContentType();
    
    byte[] asByteArray();
    
    DataHandler asDataHandler();
    
    Source asSource();
    
    InputStream asInputStream();
    
    void writeTo(final OutputStream p0) throws IOException;
    
    void writeTo(final SOAPMessage p0) throws SOAPException;
}
