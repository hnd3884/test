package com.sun.jmx.remote.protocol.iiop;

import java.io.IOException;
import javax.management.remote.rmi.RMIConnectorServer;
import java.net.MalformedURLException;
import javax.management.remote.JMXConnectorServer;
import javax.management.MBeanServer;
import java.util.Map;
import javax.management.remote.JMXServiceURL;
import javax.management.remote.JMXConnectorServerProvider;

public class ServerProvider implements JMXConnectorServerProvider
{
    @Override
    public JMXConnectorServer newJMXConnectorServer(final JMXServiceURL jmxServiceURL, final Map<String, ?> map, final MBeanServer mBeanServer) throws IOException {
        if (!jmxServiceURL.getProtocol().equals("iiop")) {
            throw new MalformedURLException("Protocol not iiop: " + jmxServiceURL.getProtocol());
        }
        return new RMIConnectorServer(jmxServiceURL, map, mBeanServer);
    }
}
