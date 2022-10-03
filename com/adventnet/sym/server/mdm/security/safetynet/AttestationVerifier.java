package com.adventnet.sym.server.mdm.security.safetynet;

import javax.net.ssl.SSLException;
import java.security.cert.X509Certificate;
import java.security.GeneralSecurityException;
import java.io.IOException;
import java.util.logging.Level;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.webtoken.JsonWebSignature;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import java.util.logging.Logger;

public class AttestationVerifier
{
    private Logger mdmLogger;
    private final DefaultHostnameVerifier hostnameVerifier;
    
    public AttestationVerifier() {
        this.mdmLogger = Logger.getLogger("MDMLogger");
        this.hostnameVerifier = new DefaultHostnameVerifier();
    }
    
    public AttestationResponse parseAndVerify(final String signedAttestationStatment) {
        JsonWebSignature jws;
        try {
            jws = JsonWebSignature.parser((JsonFactory)JacksonFactory.getDefaultInstance()).setPayloadClass((Class)AttestationResponse.class).parse(signedAttestationStatment);
        }
        catch (final IOException e) {
            this.mdmLogger.log(Level.SEVERE, "AttestationResponse: Failure: {0} is not valid JWS format", signedAttestationStatment);
            return null;
        }
        X509Certificate cert;
        try {
            cert = jws.verifySignature();
            if (cert == null) {
                this.mdmLogger.log(Level.SEVERE, "AttestationResponse: Failure: Signature verification failed");
                return null;
            }
            this.mdmLogger.log(Level.INFO, "AttestationResponse: Success: Certificate signature verified");
        }
        catch (final GeneralSecurityException e2) {
            this.mdmLogger.log(Level.SEVERE, "AttestationResponse: Failure: Error during cryptographic verification of the JWS signature.");
            return null;
        }
        if (!this.verifyHostname("attest.android.com", cert)) {
            this.mdmLogger.log(Level.SEVERE, "AttestationResponse: Failure: Certificate isn't issued for the hostname attest.android.com.");
            return null;
        }
        final AttestationResponse stmt = (AttestationResponse)jws.getPayload();
        return stmt;
    }
    
    private boolean verifyHostname(final String hostname, final X509Certificate leafCert) {
        try {
            this.hostnameVerifier.verify(hostname, leafCert);
            this.mdmLogger.log(Level.INFO, "AttestationResponse: Success: Certificate issuer verified");
            return true;
        }
        catch (final SSLException e) {
            this.mdmLogger.log(Level.SEVERE, "AttestationResponse: Failure: Cannot verify the certificate chain", e);
            return false;
        }
    }
}
