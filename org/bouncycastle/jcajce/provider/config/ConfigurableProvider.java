package org.bouncycastle.jcajce.provider.config;

import java.util.Map;
import org.bouncycastle.jcajce.provider.util.AsymmetricKeyInfoConverter;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;

public interface ConfigurableProvider
{
    public static final String THREAD_LOCAL_EC_IMPLICITLY_CA = "threadLocalEcImplicitlyCa";
    public static final String EC_IMPLICITLY_CA = "ecImplicitlyCa";
    public static final String THREAD_LOCAL_DH_DEFAULT_PARAMS = "threadLocalDhDefaultParams";
    public static final String DH_DEFAULT_PARAMS = "DhDefaultParams";
    public static final String ACCEPTABLE_EC_CURVES = "acceptableEcCurves";
    public static final String ADDITIONAL_EC_PARAMETERS = "additionalEcParameters";
    
    void setParameter(final String p0, final Object p1);
    
    void addAlgorithm(final String p0, final String p1);
    
    void addAlgorithm(final String p0, final ASN1ObjectIdentifier p1, final String p2);
    
    boolean hasAlgorithm(final String p0, final String p1);
    
    void addKeyInfoConverter(final ASN1ObjectIdentifier p0, final AsymmetricKeyInfoConverter p1);
    
    void addAttributes(final String p0, final Map<String, String> p1);
}
