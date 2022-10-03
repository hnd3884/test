package org.bouncycastle.jce.provider;

import org.bouncycastle.jcajce.provider.config.ProviderConfigurationPermission;
import java.util.Collections;
import javax.crypto.spec.DHParameterSpec;
import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.bouncycastle.jce.spec.ECParameterSpec;
import java.security.Permission;
import org.bouncycastle.jcajce.provider.config.ProviderConfiguration;

class BouncyCastleProviderConfiguration implements ProviderConfiguration
{
    private static Permission BC_EC_LOCAL_PERMISSION;
    private static Permission BC_EC_PERMISSION;
    private static Permission BC_DH_LOCAL_PERMISSION;
    private static Permission BC_DH_PERMISSION;
    private static Permission BC_EC_CURVE_PERMISSION;
    private static Permission BC_ADDITIONAL_EC_CURVE_PERMISSION;
    private ThreadLocal ecThreadSpec;
    private ThreadLocal dhThreadSpec;
    private volatile ECParameterSpec ecImplicitCaParams;
    private volatile Object dhDefaultParams;
    private volatile Set acceptableNamedCurves;
    private volatile Map additionalECParameters;
    
    BouncyCastleProviderConfiguration() {
        this.ecThreadSpec = new ThreadLocal();
        this.dhThreadSpec = new ThreadLocal();
        this.acceptableNamedCurves = new HashSet();
        this.additionalECParameters = new HashMap();
    }
    
    void setParameter(final String s, final Object dhDefaultParams) {
        final SecurityManager securityManager = System.getSecurityManager();
        if (s.equals("threadLocalEcImplicitlyCa")) {
            if (securityManager != null) {
                securityManager.checkPermission(BouncyCastleProviderConfiguration.BC_EC_LOCAL_PERMISSION);
            }
            ECParameterSpec convertSpec;
            if (dhDefaultParams instanceof ECParameterSpec || dhDefaultParams == null) {
                convertSpec = (ECParameterSpec)dhDefaultParams;
            }
            else {
                convertSpec = EC5Util.convertSpec((java.security.spec.ECParameterSpec)dhDefaultParams, false);
            }
            if (convertSpec == null) {
                this.ecThreadSpec.remove();
            }
            else {
                this.ecThreadSpec.set(convertSpec);
            }
        }
        else if (s.equals("ecImplicitlyCa")) {
            if (securityManager != null) {
                securityManager.checkPermission(BouncyCastleProviderConfiguration.BC_EC_PERMISSION);
            }
            if (dhDefaultParams instanceof ECParameterSpec || dhDefaultParams == null) {
                this.ecImplicitCaParams = (ECParameterSpec)dhDefaultParams;
            }
            else {
                this.ecImplicitCaParams = EC5Util.convertSpec((java.security.spec.ECParameterSpec)dhDefaultParams, false);
            }
        }
        else if (s.equals("threadLocalDhDefaultParams")) {
            if (securityManager != null) {
                securityManager.checkPermission(BouncyCastleProviderConfiguration.BC_DH_LOCAL_PERMISSION);
            }
            if (!(dhDefaultParams instanceof DHParameterSpec) && !(dhDefaultParams instanceof DHParameterSpec[]) && dhDefaultParams != null) {
                throw new IllegalArgumentException("not a valid DHParameterSpec");
            }
            if (dhDefaultParams == null) {
                this.dhThreadSpec.remove();
            }
            else {
                this.dhThreadSpec.set(dhDefaultParams);
            }
        }
        else if (s.equals("DhDefaultParams")) {
            if (securityManager != null) {
                securityManager.checkPermission(BouncyCastleProviderConfiguration.BC_DH_PERMISSION);
            }
            if (!(dhDefaultParams instanceof DHParameterSpec) && !(dhDefaultParams instanceof DHParameterSpec[]) && dhDefaultParams != null) {
                throw new IllegalArgumentException("not a valid DHParameterSpec or DHParameterSpec[]");
            }
            this.dhDefaultParams = dhDefaultParams;
        }
        else if (s.equals("acceptableEcCurves")) {
            if (securityManager != null) {
                securityManager.checkPermission(BouncyCastleProviderConfiguration.BC_EC_CURVE_PERMISSION);
            }
            this.acceptableNamedCurves = (Set)dhDefaultParams;
        }
        else if (s.equals("additionalEcParameters")) {
            if (securityManager != null) {
                securityManager.checkPermission(BouncyCastleProviderConfiguration.BC_ADDITIONAL_EC_CURVE_PERMISSION);
            }
            this.additionalECParameters = (Map)dhDefaultParams;
        }
    }
    
    public ECParameterSpec getEcImplicitlyCa() {
        final ECParameterSpec ecParameterSpec = this.ecThreadSpec.get();
        if (ecParameterSpec != null) {
            return ecParameterSpec;
        }
        return this.ecImplicitCaParams;
    }
    
    public DHParameterSpec getDHDefaultParameters(final int n) {
        Object o = this.dhThreadSpec.get();
        if (o == null) {
            o = this.dhDefaultParams;
        }
        if (o instanceof DHParameterSpec) {
            final DHParameterSpec dhParameterSpec = (DHParameterSpec)o;
            if (dhParameterSpec.getP().bitLength() == n) {
                return dhParameterSpec;
            }
        }
        else if (o instanceof DHParameterSpec[]) {
            final DHParameterSpec[] array = (DHParameterSpec[])o;
            for (int i = 0; i != array.length; ++i) {
                if (array[i].getP().bitLength() == n) {
                    return array[i];
                }
            }
        }
        return null;
    }
    
    public Set getAcceptableNamedCurves() {
        return Collections.unmodifiableSet((Set<?>)this.acceptableNamedCurves);
    }
    
    public Map getAdditionalECParameters() {
        return Collections.unmodifiableMap((Map<?, ?>)this.additionalECParameters);
    }
    
    static {
        BouncyCastleProviderConfiguration.BC_EC_LOCAL_PERMISSION = new ProviderConfigurationPermission("BC", "threadLocalEcImplicitlyCa");
        BouncyCastleProviderConfiguration.BC_EC_PERMISSION = new ProviderConfigurationPermission("BC", "ecImplicitlyCa");
        BouncyCastleProviderConfiguration.BC_DH_LOCAL_PERMISSION = new ProviderConfigurationPermission("BC", "threadLocalDhDefaultParams");
        BouncyCastleProviderConfiguration.BC_DH_PERMISSION = new ProviderConfigurationPermission("BC", "DhDefaultParams");
        BouncyCastleProviderConfiguration.BC_EC_CURVE_PERMISSION = new ProviderConfigurationPermission("BC", "acceptableEcCurves");
        BouncyCastleProviderConfiguration.BC_ADDITIONAL_EC_CURVE_PERMISSION = new ProviderConfigurationPermission("BC", "additionalEcParameters");
    }
}
