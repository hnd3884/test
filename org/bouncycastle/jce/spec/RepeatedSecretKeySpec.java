package org.bouncycastle.jce.spec;

public class RepeatedSecretKeySpec extends org.bouncycastle.jcajce.spec.RepeatedSecretKeySpec
{
    private String algorithm;
    
    public RepeatedSecretKeySpec(final String s) {
        super(s);
    }
}
