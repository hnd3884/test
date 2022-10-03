package sun.net.www.protocol.http.spnego;

import java.io.IOException;
import java.util.Arrays;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.Callback;
import java.net.PasswordAuthentication;
import java.net.Authenticator;
import sun.security.jgss.LoginConfigImpl;
import sun.net.www.protocol.http.HttpCallerInfo;
import javax.security.auth.callback.CallbackHandler;

public class NegotiateCallbackHandler implements CallbackHandler
{
    private String username;
    private char[] password;
    private boolean answered;
    private final HttpCallerInfo hci;
    
    public NegotiateCallbackHandler(final HttpCallerInfo hci) {
        this.hci = hci;
    }
    
    private void getAnswer() {
        if (!this.answered) {
            this.answered = true;
            if (LoginConfigImpl.HTTP_USE_GLOBAL_CREDS) {
                final PasswordAuthentication requestPasswordAuthentication = Authenticator.requestPasswordAuthentication(this.hci.host, this.hci.addr, this.hci.port, this.hci.protocol, this.hci.prompt, this.hci.scheme, this.hci.url, this.hci.authType);
                if (requestPasswordAuthentication != null) {
                    this.username = requestPasswordAuthentication.getUserName();
                    this.password = requestPasswordAuthentication.getPassword();
                }
            }
        }
    }
    
    @Override
    public void handle(final Callback[] array) throws UnsupportedCallbackException, IOException {
        for (int i = 0; i < array.length; ++i) {
            final Callback callback = array[i];
            if (callback instanceof NameCallback) {
                this.getAnswer();
                ((NameCallback)callback).setName(this.username);
            }
            else {
                if (!(callback instanceof PasswordCallback)) {
                    throw new UnsupportedCallbackException(callback, "Call back not supported");
                }
                this.getAnswer();
                ((PasswordCallback)callback).setPassword(this.password);
                if (this.password != null) {
                    Arrays.fill(this.password, ' ');
                }
            }
        }
    }
}
