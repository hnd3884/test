package javax.security.sasl;

import javax.security.auth.callback.CallbackHandler;
import java.util.Map;

public interface SaslClientFactory
{
    SaslClient createSaslClient(final String[] p0, final String p1, final String p2, final String p3, final Map<String, ?> p4, final CallbackHandler p5) throws SaslException;
    
    String[] getMechanismNames(final Map<String, ?> p0);
}
