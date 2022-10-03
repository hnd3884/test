package javax.management.remote;

import java.util.Map;
import java.io.IOException;

public interface JMXConnectorServerMBean
{
    void start() throws IOException;
    
    void stop() throws IOException;
    
    boolean isActive();
    
    void setMBeanServerForwarder(final MBeanServerForwarder p0);
    
    String[] getConnectionIds();
    
    JMXServiceURL getAddress();
    
    Map<String, ?> getAttributes();
    
    JMXConnector toJMXConnector(final Map<String, ?> p0) throws IOException;
}
