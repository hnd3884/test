package org.apache.catalina.mbeans;

import org.apache.catalina.Executor;
import javax.management.MBeanException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.Service;

public class ServiceMBean extends BaseCatalinaMBean<Service>
{
    public void addConnector(final String address, final int port, final boolean isAjp, final boolean isSSL) throws MBeanException {
        final Service service = this.doGetManagedResource();
        final String protocol = isAjp ? "AJP/1.3" : "HTTP/1.1";
        final Connector connector = new Connector(protocol);
        if (address != null && address.length() > 0) {
            connector.setProperty("address", address);
        }
        connector.setPort(port);
        connector.setSecure(isSSL);
        connector.setScheme(isSSL ? "https" : "http");
        service.addConnector(connector);
    }
    
    public void addExecutor(final String type) throws MBeanException {
        final Service service = this.doGetManagedResource();
        final Executor executor = (Executor)BaseCatalinaMBean.newInstance(type);
        service.addExecutor(executor);
    }
    
    public String[] findConnectors() throws MBeanException {
        final Service service = this.doGetManagedResource();
        final Connector[] connectors = service.findConnectors();
        final String[] str = new String[connectors.length];
        for (int i = 0; i < connectors.length; ++i) {
            str[i] = connectors[i].toString();
        }
        return str;
    }
    
    public String[] findExecutors() throws MBeanException {
        final Service service = this.doGetManagedResource();
        final Executor[] executors = service.findExecutors();
        final String[] str = new String[executors.length];
        for (int i = 0; i < executors.length; ++i) {
            str[i] = executors[i].toString();
        }
        return str;
    }
    
    public String getExecutor(final String name) throws MBeanException {
        final Service service = this.doGetManagedResource();
        final Executor executor = service.getExecutor(name);
        return executor.toString();
    }
}
