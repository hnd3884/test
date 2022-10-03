package javax.security.sasl;

import javax.security.auth.callback.CallbackHandler;
import java.util.Map;

public interface SaslServerFactory
{
    SaslServer createSaslServer(final String p0, final String p1, final String p2, final Map<String, ?> p3, final CallbackHandler p4) throws SaslException;
    
    String[] getMechanismNames(final Map<String, ?> p0);
}
