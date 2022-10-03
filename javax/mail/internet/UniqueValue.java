package javax.mail.internet;

import javax.mail.Session;
import java.util.concurrent.atomic.AtomicInteger;

class UniqueValue
{
    private static AtomicInteger id;
    
    public static String getUniqueBoundaryValue() {
        final StringBuilder s = new StringBuilder();
        final long hash = s.hashCode();
        s.append("----=_Part_").append(UniqueValue.id.getAndIncrement()).append("_").append(hash).append('.').append(System.currentTimeMillis());
        return s.toString();
    }
    
    public static String getUniqueMessageIDValue(final Session ssn) {
        String suffix = null;
        final InternetAddress addr = InternetAddress.getLocalAddress(ssn);
        if (addr != null) {
            suffix = addr.getAddress();
        }
        else {
            suffix = "javamailuser@localhost";
        }
        final int at = suffix.lastIndexOf(64);
        if (at >= 0) {
            suffix = suffix.substring(at);
        }
        final StringBuilder s = new StringBuilder();
        s.append(s.hashCode()).append('.').append(UniqueValue.id.getAndIncrement()).append('.').append(System.currentTimeMillis()).append(suffix);
        return s.toString();
    }
    
    static {
        UniqueValue.id = new AtomicInteger();
    }
}
