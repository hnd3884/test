package org.bouncycastle.jcajce.provider.symmetric;

import org.bouncycastle.jcajce.provider.symmetric.util.ClassUtil;
import java.security.spec.InvalidParameterSpecException;
import org.bouncycastle.util.Integers;
import org.bouncycastle.asn1.cms.GCMParameters;
import org.bouncycastle.asn1.ASN1Primitive;
import java.security.spec.AlgorithmParameterSpec;

class GcmSpecUtil
{
    static final Class gcmSpecClass;
    
    static boolean gcmSpecExists() {
        return GcmSpecUtil.gcmSpecClass != null;
    }
    
    static boolean isGcmSpec(final AlgorithmParameterSpec algorithmParameterSpec) {
        return GcmSpecUtil.gcmSpecClass != null && GcmSpecUtil.gcmSpecClass.isInstance(algorithmParameterSpec);
    }
    
    static boolean isGcmSpec(final Class clazz) {
        return GcmSpecUtil.gcmSpecClass == clazz;
    }
    
    static AlgorithmParameterSpec extractGcmSpec(final ASN1Primitive asn1Primitive) throws InvalidParameterSpecException {
        try {
            final GCMParameters instance = GCMParameters.getInstance(asn1Primitive);
            return GcmSpecUtil.gcmSpecClass.getConstructor(Integer.TYPE, byte[].class).newInstance(Integers.valueOf(instance.getIcvLen() * 8), instance.getNonce());
        }
        catch (final NoSuchMethodException ex) {
            throw new InvalidParameterSpecException("No constructor found!");
        }
        catch (final Exception ex2) {
            throw new InvalidParameterSpecException("Construction failed: " + ex2.getMessage());
        }
    }
    
    static GCMParameters extractGcmParameters(final AlgorithmParameterSpec algorithmParameterSpec) throws InvalidParameterSpecException {
        try {
            return new GCMParameters((byte[])GcmSpecUtil.gcmSpecClass.getDeclaredMethod("getIV", (Class[])new Class[0]).invoke(algorithmParameterSpec, new Object[0]), (int)GcmSpecUtil.gcmSpecClass.getDeclaredMethod("getTLen", (Class[])new Class[0]).invoke(algorithmParameterSpec, new Object[0]) / 8);
        }
        catch (final Exception ex) {
            throw new InvalidParameterSpecException("Cannot process GCMParameterSpec");
        }
    }
    
    static {
        gcmSpecClass = ClassUtil.loadClass(GcmSpecUtil.class, "javax.crypto.spec.GCMParameterSpec");
    }
}
