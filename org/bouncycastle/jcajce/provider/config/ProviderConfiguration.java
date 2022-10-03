package org.bouncycastle.jcajce.provider.config;

import java.util.Map;
import java.util.Set;
import javax.crypto.spec.DHParameterSpec;
import org.bouncycastle.jce.spec.ECParameterSpec;

public interface ProviderConfiguration
{
    ECParameterSpec getEcImplicitlyCa();
    
    DHParameterSpec getDHDefaultParameters(final int p0);
    
    Set getAcceptableNamedCurves();
    
    Map getAdditionalECParameters();
}
