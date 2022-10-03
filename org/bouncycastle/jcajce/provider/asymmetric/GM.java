package org.bouncycastle.jcajce.provider.asymmetric;

import org.bouncycastle.asn1.gm.GMObjectIdentifiers;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;
import java.util.HashMap;
import java.util.Map;

public class GM
{
    private static final String PREFIX = "org.bouncycastle.jcajce.provider.asymmetric.ec.";
    private static final Map<String, String> generalSm2Attributes;
    
    static {
        (generalSm2Attributes = new HashMap<String, String>()).put("SupportedKeyClasses", "java.security.interfaces.ECPublicKey|java.security.interfaces.ECPrivateKey");
        GM.generalSm2Attributes.put("SupportedKeyFormats", "PKCS#8|X.509");
    }
    
    public static class Mappings extends AsymmetricAlgorithmProvider
    {
        @Override
        public void configure(final ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("Signature.SM3WITHSM2", "org.bouncycastle.jcajce.provider.asymmetric.ec.GMSignatureSpi$sm3WithSM2");
            configurableProvider.addAlgorithm("Alg.Alias.Signature." + GMObjectIdentifiers.sm2sign_with_sm3, "SM3WITHSM2");
        }
    }
}
