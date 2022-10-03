package com.sun.xml.internal.ws.transport.http;

import java.util.Iterator;
import javax.xml.namespace.QName;
import com.sun.xml.internal.ws.api.server.PortAddressResolver;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import java.util.AbstractList;

public abstract class HttpAdapterList<T extends HttpAdapter> extends AbstractList<T> implements DeploymentDescriptorParser.AdapterFactory<T>
{
    private final List<T> adapters;
    private final Map<PortInfo, String> addressMap;
    
    public HttpAdapterList() {
        this.adapters = new ArrayList<T>();
        this.addressMap = new HashMap<PortInfo, String>();
    }
    
    @Override
    public T createAdapter(final String name, final String urlPattern, final WSEndpoint<?> endpoint) {
        final T t = this.createHttpAdapter(name, urlPattern, endpoint);
        this.adapters.add(t);
        final WSDLPort port = endpoint.getPort();
        if (port != null) {
            final PortInfo portInfo = new PortInfo(port.getOwner().getName(), port.getName().getLocalPart(), endpoint.getImplementationClass());
            this.addressMap.put(portInfo, this.getValidPath(urlPattern));
        }
        return t;
    }
    
    protected abstract T createHttpAdapter(final String p0, final String p1, final WSEndpoint<?> p2);
    
    private String getValidPath(@NotNull final String urlPattern) {
        if (urlPattern.endsWith("/*")) {
            return urlPattern.substring(0, urlPattern.length() - 2);
        }
        return urlPattern;
    }
    
    public PortAddressResolver createPortAddressResolver(final String baseAddress, final Class<?> endpointImpl) {
        return new PortAddressResolver() {
            @Override
            public String getAddressFor(@NotNull final QName serviceName, @NotNull final String portName) {
                String urlPattern = HttpAdapterList.this.addressMap.get(new PortInfo(serviceName, portName, endpointImpl));
                if (urlPattern == null) {
                    for (final Map.Entry<PortInfo, String> e : HttpAdapterList.this.addressMap.entrySet()) {
                        if (serviceName.equals(e.getKey().serviceName) && portName.equals(e.getKey().portName)) {
                            urlPattern = e.getValue();
                            break;
                        }
                    }
                }
                return (urlPattern == null) ? null : (baseAddress + urlPattern);
            }
        };
    }
    
    @Override
    public T get(final int index) {
        return this.adapters.get(index);
    }
    
    @Override
    public int size() {
        return this.adapters.size();
    }
    
    private static class PortInfo
    {
        private final QName serviceName;
        private final String portName;
        private final Class<?> implClass;
        
        PortInfo(@NotNull final QName serviceName, @NotNull final String portName, final Class<?> implClass) {
            this.serviceName = serviceName;
            this.portName = portName;
            this.implClass = implClass;
        }
        
        @Override
        public boolean equals(final Object portInfo) {
            if (!(portInfo instanceof PortInfo)) {
                return false;
            }
            final PortInfo that = (PortInfo)portInfo;
            if (this.implClass == null) {
                return this.serviceName.equals(that.serviceName) && this.portName.equals(that.portName) && that.implClass == null;
            }
            return this.serviceName.equals(that.serviceName) && this.portName.equals(that.portName) && this.implClass.equals(that.implClass);
        }
        
        @Override
        public int hashCode() {
            final int retVal = this.serviceName.hashCode() + this.portName.hashCode();
            return (this.implClass != null) ? (retVal + this.implClass.hashCode()) : retVal;
        }
    }
}
