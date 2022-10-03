package mdm.integrations.certificateauthority.digicert.v1;

import javax.net.ssl.SSLContext;
import java.util.Map;

public class DigicertClient
{
    static DigicertClient digicertClient;
    
    public static DigicertClient getInstance() {
        if (DigicertClient.digicertClient == null) {
            return new DigicertClient();
        }
        return DigicertClient.digicertClient;
    }
    
    public Map<Long, Map<String, String>> createUserPasscodes(final Map<String, Object> proxyDetails, final SSLContext sslContext, final Map<Long, Map<String, String>> userDetailsList, final String certificateOID) {
        try {
            final DigicertWSHandler digicertWSHandler = new DigicertWSHandler(proxyDetails, sslContext, userDetailsList, certificateOID);
            return digicertWSHandler.createUserAndGetPasscodes();
        }
        catch (final Exception e) {
            return null;
        }
    }
    
    static {
        DigicertClient.digicertClient = null;
    }
}
