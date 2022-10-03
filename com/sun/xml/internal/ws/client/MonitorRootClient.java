package com.sun.xml.internal.ws.client;

import com.sun.xml.internal.ws.api.model.wsdl.WSDLService;
import java.net.URL;
import javax.xml.namespace.QName;
import java.util.Map;
import com.sun.org.glassfish.gmbal.ManagedAttribute;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.org.glassfish.gmbal.AMXMetadata;
import com.sun.org.glassfish.gmbal.Description;
import com.sun.org.glassfish.gmbal.ManagedObject;
import com.sun.xml.internal.ws.server.MonitorBase;

@ManagedObject
@Description("Metro Web Service client")
@AMXMetadata(type = "WSClient")
public final class MonitorRootClient extends MonitorBase
{
    private final Stub stub;
    
    MonitorRootClient(final Stub stub) {
        this.stub = stub;
    }
    
    @ManagedAttribute
    private Container getContainer() {
        return this.stub.owner.getContainer();
    }
    
    @ManagedAttribute
    private Map<QName, PortInfo> qnameToPortInfoMap() {
        return this.stub.owner.getQNameToPortInfoMap();
    }
    
    @ManagedAttribute
    private QName serviceName() {
        return this.stub.owner.getServiceName();
    }
    
    @ManagedAttribute
    private Class serviceClass() {
        return this.stub.owner.getServiceClass();
    }
    
    @ManagedAttribute
    private URL wsdlDocumentLocation() {
        return this.stub.owner.getWSDLDocumentLocation();
    }
    
    @ManagedAttribute
    private WSDLService wsdlService() {
        return this.stub.owner.getWsdlService();
    }
}
