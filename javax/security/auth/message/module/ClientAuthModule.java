package javax.security.auth.message.module;

import javax.security.auth.message.AuthException;
import java.util.Map;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.message.MessagePolicy;
import javax.security.auth.message.ClientAuth;

public interface ClientAuthModule extends ClientAuth
{
    void initialize(final MessagePolicy p0, final MessagePolicy p1, final CallbackHandler p2, final Map p3) throws AuthException;
    
    Class[] getSupportedMessageTypes();
}
