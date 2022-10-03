package com.sun.xml.internal.ws.wsdl;

import com.sun.xml.internal.ws.api.model.JavaMethod;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.internal.ws.model.JavaMethodImpl;
import com.sun.xml.internal.ws.api.model.WSDLOperationMapping;
import javax.xml.namespace.QName;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.istack.internal.Nullable;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;

public abstract class WSDLOperationFinder
{
    protected final WSDLPort wsdlModel;
    protected final WSBinding binding;
    protected final SEIModel seiModel;
    
    public WSDLOperationFinder(@NotNull final WSDLPort wsdlModel, @NotNull final WSBinding binding, @Nullable final SEIModel seiModel) {
        this.wsdlModel = wsdlModel;
        this.binding = binding;
        this.seiModel = seiModel;
    }
    
    @Deprecated
    public QName getWSDLOperationQName(final Packet request) throws DispatchException {
        final WSDLOperationMapping m = this.getWSDLOperationMapping(request);
        return (m != null) ? m.getOperationName() : null;
    }
    
    public WSDLOperationMapping getWSDLOperationMapping(final Packet request) throws DispatchException {
        return null;
    }
    
    protected WSDLOperationMapping wsdlOperationMapping(final JavaMethodImpl j) {
        return new WSDLOperationMappingImpl(j.getOperation(), j);
    }
    
    protected WSDLOperationMapping wsdlOperationMapping(final WSDLBoundOperation o) {
        return new WSDLOperationMappingImpl(o, null);
    }
    
    static class WSDLOperationMappingImpl implements WSDLOperationMapping
    {
        private WSDLBoundOperation wsdlOperation;
        private JavaMethod javaMethod;
        private QName operationName;
        
        WSDLOperationMappingImpl(final WSDLBoundOperation wsdlOperation, final JavaMethodImpl javaMethod) {
            this.wsdlOperation = wsdlOperation;
            this.javaMethod = javaMethod;
            this.operationName = ((javaMethod != null) ? javaMethod.getOperationQName() : wsdlOperation.getName());
        }
        
        @Override
        public WSDLBoundOperation getWSDLBoundOperation() {
            return this.wsdlOperation;
        }
        
        @Override
        public JavaMethod getJavaMethod() {
            return this.javaMethod;
        }
        
        @Override
        public QName getOperationName() {
            return this.operationName;
        }
    }
}
