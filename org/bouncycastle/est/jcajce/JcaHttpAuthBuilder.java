package org.bouncycastle.est.jcajce;

import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.est.HttpAuth;
import java.security.Provider;
import java.security.SecureRandom;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;

public class JcaHttpAuthBuilder
{
    private JcaDigestCalculatorProviderBuilder providerBuilder;
    private final String realm;
    private final String username;
    private final char[] password;
    private SecureRandom random;
    
    public JcaHttpAuthBuilder(final String s, final char[] array) {
        this(null, s, array);
    }
    
    public JcaHttpAuthBuilder(final String realm, final String username, final char[] password) {
        this.providerBuilder = new JcaDigestCalculatorProviderBuilder();
        this.random = new SecureRandom();
        this.realm = realm;
        this.username = username;
        this.password = password;
    }
    
    public JcaHttpAuthBuilder setProvider(final Provider provider) {
        this.providerBuilder.setProvider(provider);
        return this;
    }
    
    public JcaHttpAuthBuilder setProvider(final String provider) {
        this.providerBuilder.setProvider(provider);
        return this;
    }
    
    public JcaHttpAuthBuilder setNonceGenerator(final SecureRandom random) {
        this.random = random;
        return this;
    }
    
    public HttpAuth build() throws OperatorCreationException {
        return new HttpAuth(this.realm, this.username, this.password, this.random, this.providerBuilder.build());
    }
}
