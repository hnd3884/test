package com.sun.xml.internal.ws.api.server;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.OutputStream;
import com.sun.istack.internal.Nullable;
import java.net.URL;
import java.util.Set;
import com.sun.org.glassfish.gmbal.ManagedAttribute;
import javax.xml.namespace.QName;
import com.sun.org.glassfish.gmbal.ManagedData;

@ManagedData
public interface SDDocument
{
    @ManagedAttribute
    QName getRootName();
    
    @ManagedAttribute
    boolean isWSDL();
    
    @ManagedAttribute
    boolean isSchema();
    
    @ManagedAttribute
    Set<String> getImports();
    
    @ManagedAttribute
    URL getURL();
    
    void writeTo(@Nullable final PortAddressResolver p0, final DocumentAddressResolver p1, final OutputStream p2) throws IOException;
    
    void writeTo(final PortAddressResolver p0, final DocumentAddressResolver p1, final XMLStreamWriter p2) throws XMLStreamException, IOException;
    
    public interface WSDL extends SDDocument
    {
        @ManagedAttribute
        String getTargetNamespace();
        
        @ManagedAttribute
        boolean hasPortType();
        
        @ManagedAttribute
        boolean hasService();
        
        @ManagedAttribute
        Set<QName> getAllServices();
    }
    
    public interface Schema extends SDDocument
    {
        @ManagedAttribute
        String getTargetNamespace();
    }
}
