package javax.management.remote;

import java.io.IOException;
import javax.management.MBeanServer;
import java.util.Map;

public interface JMXConnectorServerProvider
{
    JMXConnectorServer newJMXConnectorServer(final JMXServiceURL p0, final Map<String, ?> p1, final MBeanServer p2) throws IOException;
}
