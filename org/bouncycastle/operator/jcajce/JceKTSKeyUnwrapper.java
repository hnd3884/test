package org.bouncycastle.operator.jcajce;

import javax.crypto.Cipher;
import org.bouncycastle.operator.OperatorException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.Key;
import org.bouncycastle.jcajce.spec.KTSParameterSpec;
import org.bouncycastle.crypto.util.DEROtherInfo;
import org.bouncycastle.asn1.cms.RsaKemParameters;
import org.bouncycastle.asn1.cms.GenericHybridParameters;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import java.security.Provider;
import org.bouncycastle.util.Arrays;
import java.util.HashMap;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import java.security.PrivateKey;
import java.util.Map;
import org.bouncycastle.operator.AsymmetricKeyUnwrapper;

public class JceKTSKeyUnwrapper extends AsymmetricKeyUnwrapper
{
    private OperatorHelper helper;
    private Map extraMappings;
    private PrivateKey privKey;
    private byte[] partyUInfo;
    private byte[] partyVInfo;
    
    public JceKTSKeyUnwrapper(final AlgorithmIdentifier algorithmIdentifier, final PrivateKey privKey, final byte[] array, final byte[] array2) {
        super(algorithmIdentifier);
        this.helper = new OperatorHelper((JcaJceHelper)new DefaultJcaJceHelper());
        this.extraMappings = new HashMap();
        this.privKey = privKey;
        this.partyUInfo = Arrays.clone(array);
        this.partyVInfo = Arrays.clone(array2);
    }
    
    public JceKTSKeyUnwrapper setProvider(final Provider provider) {
        this.helper = new OperatorHelper((JcaJceHelper)new ProviderJcaJceHelper(provider));
        return this;
    }
    
    public JceKTSKeyUnwrapper setProvider(final String s) {
        this.helper = new OperatorHelper((JcaJceHelper)new NamedJcaJceHelper(s));
        return this;
    }
    
    public GenericKey generateUnwrappedKey(final AlgorithmIdentifier algorithmIdentifier, final byte[] array) throws OperatorException {
        final GenericHybridParameters instance = GenericHybridParameters.getInstance((Object)this.getAlgorithmIdentifier().getParameters());
        final Cipher asymmetricWrapper = this.helper.createAsymmetricWrapper(this.getAlgorithmIdentifier().getAlgorithm(), this.extraMappings);
        final String wrappingAlgorithmName = this.helper.getWrappingAlgorithmName(instance.getDem().getAlgorithm());
        final RsaKemParameters instance2 = RsaKemParameters.getInstance((Object)instance.getKem().getParameters());
        final int n = instance2.getKeyLength().intValue() * 8;
        Key unwrap;
        try {
            asymmetricWrapper.init(4, this.privKey, (AlgorithmParameterSpec)new KTSParameterSpec.Builder(wrappingAlgorithmName, n, new DEROtherInfo.Builder(instance.getDem(), this.partyUInfo, this.partyVInfo).build().getEncoded()).withKdfAlgorithm(instance2.getKeyDerivationFunction()).build());
            unwrap = asymmetricWrapper.unwrap(array, this.helper.getKeyAlgorithmName(algorithmIdentifier.getAlgorithm()), 3);
        }
        catch (final Exception ex) {
            throw new OperatorException("Unable to unwrap contents key: " + ex.getMessage(), ex);
        }
        return new JceGenericKey(algorithmIdentifier, unwrap);
    }
}
