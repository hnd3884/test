package javax.management.remote;

import java.io.IOException;
import java.util.Map;

public interface JMXConnectorProvider
{
    JMXConnector newJMXConnector(final JMXServiceURL p0, final Map<String, ?> p1) throws IOException;
}
