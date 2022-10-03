package sun.management;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.nio.ByteBuffer;
import sun.management.counter.Counter;
import sun.management.counter.perf.PerfInstrumentation;
import java.io.IOException;
import sun.management.counter.Units;
import sun.misc.Perf;
import java.util.concurrent.atomic.AtomicInteger;

public class ConnectorAddressLink
{
    private static final String CONNECTOR_ADDRESS_COUNTER = "sun.management.JMXConnectorServer.address";
    private static final String REMOTE_CONNECTOR_COUNTER_PREFIX = "sun.management.JMXConnectorServer.";
    private static AtomicInteger counter;
    
    public static void export(final String s) {
        if (s == null || s.length() == 0) {
            throw new IllegalArgumentException("address not specified");
        }
        Perf.getPerf().createString("sun.management.JMXConnectorServer.address", 1, Units.STRING.intValue(), s);
    }
    
    public static String importFrom(final int n) throws IOException {
        final Perf perf = Perf.getPerf();
        ByteBuffer attach;
        try {
            attach = perf.attach(n, "r");
        }
        catch (final IllegalArgumentException ex) {
            throw new IOException(ex.getMessage());
        }
        final Iterator<Counter> iterator = new PerfInstrumentation(attach).findByPattern("sun.management.JMXConnectorServer.address").iterator();
        if (iterator.hasNext()) {
            return (String)iterator.next().getValue();
        }
        return null;
    }
    
    public static void exportRemote(final Map<String, String> map) {
        final int andIncrement = ConnectorAddressLink.counter.getAndIncrement();
        final Perf perf = Perf.getPerf();
        for (final Map.Entry entry : map.entrySet()) {
            perf.createString("sun.management.JMXConnectorServer." + andIncrement + "." + (String)entry.getKey(), 1, Units.STRING.intValue(), (String)entry.getValue());
        }
    }
    
    public static Map<String, String> importRemoteFrom(final int n) throws IOException {
        final Perf perf = Perf.getPerf();
        ByteBuffer attach;
        try {
            attach = perf.attach(n, "r");
        }
        catch (final IllegalArgumentException ex) {
            throw new IOException(ex.getMessage());
        }
        final List<Counter> allCounters = new PerfInstrumentation(attach).getAllCounters();
        final HashMap hashMap = new HashMap();
        for (final Counter counter : allCounters) {
            final String name = counter.getName();
            if (name.startsWith("sun.management.JMXConnectorServer.") && !name.equals("sun.management.JMXConnectorServer.address")) {
                hashMap.put(name, counter.getValue().toString());
            }
        }
        return hashMap;
    }
    
    static {
        ConnectorAddressLink.counter = new AtomicInteger();
    }
}
