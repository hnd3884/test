package org.bouncycastle.pqc.jcajce.spec;

import java.security.spec.AlgorithmParameterSpec;

public class XMSSParameterSpec implements AlgorithmParameterSpec
{
    public static final String SHA256 = "SHA256";
    public static final String SHA512 = "SHA512";
    public static final String SHAKE128 = "SHAKE128";
    public static final String SHAKE256 = "SHAKE256";
    private final int height;
    private final String treeDigest;
    
    public XMSSParameterSpec(final int height, final String treeDigest) {
        this.height = height;
        this.treeDigest = treeDigest;
    }
    
    public String getTreeDigest() {
        return this.treeDigest;
    }
    
    public int getHeight() {
        return this.height;
    }
}
