package com.adventnet.sym.server.mdm.security.safetynet;

import com.google.api.client.util.Base64;
import com.google.api.client.util.Key;
import com.google.api.client.json.webtoken.JsonWebToken;

public class AttestationResponse extends JsonWebToken.Payload
{
    @Key
    private String nonce;
    @Key
    private long timestampMs;
    @Key
    private String apkPackageName;
    @Key
    private String[] apkCertificateDigestSha256;
    @Key
    private String apkDigestSha256;
    @Key
    private boolean ctsProfileMatch;
    @Key
    private boolean basicIntegrity;
    
    public byte[] getNonce() {
        return Base64.decodeBase64(this.nonce);
    }
    
    public long getTimestampMs() {
        return this.timestampMs;
    }
    
    public String getApkPackageName() {
        return this.apkPackageName;
    }
    
    public byte[] getApkDigestSha256() {
        return Base64.decodeBase64(this.apkDigestSha256);
    }
    
    public byte[][] getApkCertificateDigestSha256() {
        final byte[][] certs = new byte[this.apkCertificateDigestSha256.length][];
        for (int i = 0; i < this.apkCertificateDigestSha256.length; ++i) {
            certs[i] = Base64.decodeBase64(this.apkCertificateDigestSha256[i]);
        }
        return certs;
    }
    
    public boolean isCtsProfileMatch() {
        return this.ctsProfileMatch;
    }
    
    public boolean hasBasicIntegrity() {
        return this.basicIntegrity;
    }
}
