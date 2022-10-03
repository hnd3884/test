package com.google.api.client.auth.oauth;

import java.security.GeneralSecurityException;
import java.security.Signature;
import com.google.api.client.util.Base64;
import com.google.api.client.util.StringUtils;
import com.google.api.client.util.SecurityUtils;
import java.security.PrivateKey;
import com.google.api.client.util.Beta;

@Beta
public final class OAuthRsaSigner implements OAuthSigner
{
    public PrivateKey privateKey;
    
    @Override
    public String getSignatureMethod() {
        return "RSA-SHA1";
    }
    
    @Override
    public String computeSignature(final String signatureBaseString) throws GeneralSecurityException {
        final Signature signer = SecurityUtils.getSha1WithRsaSignatureAlgorithm();
        final byte[] data = StringUtils.getBytesUtf8(signatureBaseString);
        return Base64.encodeBase64String(SecurityUtils.sign(signer, this.privateKey, data));
    }
}
