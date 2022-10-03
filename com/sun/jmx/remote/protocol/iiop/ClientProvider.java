package com.sun.jmx.remote.protocol.iiop;

import java.io.IOException;
import javax.management.remote.rmi.RMIConnector;
import java.net.MalformedURLException;
import javax.management.remote.JMXConnector;
import java.util.Map;
import javax.management.remote.JMXServiceURL;
import javax.management.remote.JMXConnectorProvider;

public class ClientProvider implements JMXConnectorProvider
{
    @Override
    public JMXConnector newJMXConnector(final JMXServiceURL jmxServiceURL, final Map<String, ?> map) throws IOException {
        if (!jmxServiceURL.getProtocol().equals("iiop")) {
            throw new MalformedURLException("Protocol not iiop: " + jmxServiceURL.getProtocol());
        }
        return new RMIConnector(jmxServiceURL, map);
    }
}
