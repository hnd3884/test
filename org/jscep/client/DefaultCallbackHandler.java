package org.jscep.client;

import java.io.IOException;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.callback.Callback;
import org.jscep.client.verification.CertificateVerifier;
import javax.security.auth.callback.CallbackHandler;

public final class DefaultCallbackHandler implements CallbackHandler
{
    private final CertificateVerifier verifier;
    
    public DefaultCallbackHandler(final CertificateVerifier verifier) {
        this.verifier = verifier;
    }
    
    @Override
    public void handle(final Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        for (final Callback callback : callbacks) {
            if (!(callback instanceof CertificateVerificationCallback)) {
                throw new UnsupportedCallbackException(callback);
            }
            this.verify(CertificateVerificationCallback.class.cast(callback));
        }
    }
    
    private void verify(final CertificateVerificationCallback callback) {
        callback.setVerified(this.verifier.verify(callback.getCertificate()));
    }
}
