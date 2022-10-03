package com.microsoft.sqlserver.jdbc;

import java.util.Iterator;
import com.google.gson.JsonObject;
import java.net.URLConnection;
import com.google.gson.JsonArray;
import java.security.GeneralSecurityException;
import java.io.IOException;
import java.util.Arrays;
import java.security.Signature;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.security.cert.X509Certificate;
import java.security.cert.CertificateFactory;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.net.URL;
import java.util.Base64;
import java.nio.ByteOrder;
import java.nio.ByteBuffer;
import java.util.Hashtable;

class AASAttestationResponse extends BaseAttestationResponse
{
    private byte[] attestationToken;
    private static Hashtable<String, JWTCertificateEntry> certificateCache;
    
    AASAttestationResponse(final byte[] b) throws SQLServerException {
        final ByteBuffer response = ByteBuffer.wrap(b).order(ByteOrder.LITTLE_ENDIAN);
        this.totalSize = response.getInt();
        this.identitySize = response.getInt();
        this.attestationTokenSize = response.getInt();
        this.enclaveType = response.getInt();
        this.enclavePK = new byte[this.identitySize];
        this.attestationToken = new byte[this.attestationTokenSize];
        response.get(this.enclavePK, 0, this.identitySize);
        response.get(this.attestationToken, 0, this.attestationTokenSize);
        this.sessionInfoSize = response.getInt();
        response.get(this.sessionID, 0, 8);
        this.DHPKsize = response.getInt();
        this.DHPKSsize = response.getInt();
        this.DHpublicKey = new byte[this.DHPKsize];
        this.publicKeySig = new byte[this.DHPKSsize];
        response.get(this.DHpublicKey, 0, this.DHPKsize);
        response.get(this.publicKeySig, 0, this.DHPKSsize);
        if (0 != response.remaining()) {
            SQLServerException.makeFromDriverError(null, this, SQLServerResource.getResource("R_EnclaveResponseLengthError"), "0", false);
        }
    }
    
    void validateToken(final String attestationUrl, final byte[] nonce) throws SQLServerException {
        try {
            String jwtToken = new String(this.attestationToken).trim();
            if (jwtToken.startsWith("\"") && jwtToken.endsWith("\"")) {
                jwtToken = jwtToken.substring(1, jwtToken.length() - 1);
            }
            final String[] splitString = jwtToken.split("\\.");
            final Base64.Decoder decoder = Base64.getUrlDecoder();
            final String header = new String(decoder.decode(splitString[0]));
            final String body = new String(decoder.decode(splitString[1]));
            final byte[] stmtSig = decoder.decode(splitString[2]);
            JsonArray keys = null;
            final JWTCertificateEntry cacheEntry = AASAttestationResponse.certificateCache.get(attestationUrl);
            if (null != cacheEntry && !cacheEntry.expired()) {
                keys = cacheEntry.getCertificates();
            }
            else if (null != cacheEntry && cacheEntry.expired()) {
                AASAttestationResponse.certificateCache.remove(attestationUrl);
            }
            if (null == keys) {
                final String authorityUrl = new URL(attestationUrl).getAuthority();
                final URL wellKnownUrl = new URL("https://" + authorityUrl + "/.well-known/openid-configuration");
                final URLConnection con = wellKnownUrl.openConnection();
                final String wellKnownUrlJson = new String(Util.convertInputStreamToString(con.getInputStream()));
                final JsonObject attestationJson = JsonParser.parseString(wellKnownUrlJson).getAsJsonObject();
                final URL jwksUrl = new URL(attestationJson.get("jwks_uri").getAsString());
                final URLConnection jwksCon = jwksUrl.openConnection();
                final String jwksUrlJson = new String(Util.convertInputStreamToString(jwksCon.getInputStream()));
                final JsonObject jwksJson = JsonParser.parseString(jwksUrlJson).getAsJsonObject();
                keys = jwksJson.get("keys").getAsJsonArray();
                AASAttestationResponse.certificateCache.put(attestationUrl, new JWTCertificateEntry(keys));
            }
            final JsonObject headerJsonObject = JsonParser.parseString(header).getAsJsonObject();
            final String keyID = headerJsonObject.get("kid").getAsString();
            for (final JsonElement key : keys) {
                final JsonObject keyObj = key.getAsJsonObject();
                final String kId = keyObj.get("kid").getAsString();
                if (kId.equals(keyID)) {
                    final JsonArray certsFromServer = keyObj.get("x5c").getAsJsonArray();
                    final byte[] signatureBytes = (splitString[0] + "." + splitString[1]).getBytes();
                    for (final JsonElement jsonCert : certsFromServer) {
                        final CertificateFactory cf = CertificateFactory.getInstance("X.509");
                        final X509Certificate cert = (X509Certificate)cf.generateCertificate(new ByteArrayInputStream(Base64.getDecoder().decode(jsonCert.getAsString())));
                        final Signature sig = Signature.getInstance("SHA256withRSA");
                        sig.initVerify(cert.getPublicKey());
                        sig.update(signatureBytes);
                        if (sig.verify(stmtSig)) {
                            final JsonObject bodyJsonObject = JsonParser.parseString(body).getAsJsonObject();
                            final String aasEhd = bodyJsonObject.get("aas-ehd").getAsString();
                            if (!Arrays.equals(Base64.getUrlDecoder().decode(aasEhd), this.enclavePK)) {
                                SQLServerException.makeFromDriverError(null, this, SQLServerResource.getResource("R_AasEhdError"), "0", false);
                            }
                            if (this.enclaveType == 1) {
                                final String rpData = bodyJsonObject.get("rp_data").getAsString();
                                if (!Arrays.equals(Base64.getUrlDecoder().decode(rpData), nonce)) {
                                    SQLServerException.makeFromDriverError(null, this, SQLServerResource.getResource("R_VbsRpDataError"), "0", false);
                                }
                            }
                            return;
                        }
                    }
                }
            }
            SQLServerException.makeFromDriverError(null, this, SQLServerResource.getResource("R_AasJWTError"), "0", false);
        }
        catch (final IOException | GeneralSecurityException e) {
            SQLServerException.makeFromDriverError(null, this, e.getLocalizedMessage(), "", false);
        }
    }
    
    void validateDHPublicKey(final byte[] nonce) throws SQLServerException, GeneralSecurityException {
        if (this.enclaveType == 2) {
            for (int i = 0; i < this.enclavePK.length; ++i) {
                this.enclavePK[i] ^= nonce[i % nonce.length];
            }
        }
        this.validateDHPublicKey();
    }
    
    static {
        AASAttestationResponse.certificateCache = new Hashtable<String, JWTCertificateEntry>();
    }
}
