package org.bouncycastle.jcajce.provider.symmetric;

import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;
import java.security.spec.InvalidParameterSpecException;
import javax.crypto.spec.PBEParameterSpec;
import java.security.spec.AlgorithmParameterSpec;
import java.io.IOException;
import org.bouncycastle.asn1.pkcs.PBEParameter;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseAlgorithmParameters;

public class PBEPBKDF1
{
    private PBEPBKDF1() {
    }
    
    public static class AlgParams extends BaseAlgorithmParameters
    {
        PBEParameter params;
        
        @Override
        protected byte[] engineGetEncoded() {
            try {
                return this.params.getEncoded("DER");
            }
            catch (final IOException ex) {
                throw new RuntimeException("Oooops! " + ex.toString());
            }
        }
        
        @Override
        protected byte[] engineGetEncoded(final String s) {
            if (this.isASN1FormatString(s)) {
                return this.engineGetEncoded();
            }
            return null;
        }
        
        @Override
        protected AlgorithmParameterSpec localEngineGetParameterSpec(final Class clazz) throws InvalidParameterSpecException {
            if (clazz == PBEParameterSpec.class) {
                return new PBEParameterSpec(this.params.getSalt(), this.params.getIterationCount().intValue());
            }
            throw new InvalidParameterSpecException("unknown parameter spec passed to PBKDF1 PBE parameters object.");
        }
        
        @Override
        protected void engineInit(final AlgorithmParameterSpec algorithmParameterSpec) throws InvalidParameterSpecException {
            if (!(algorithmParameterSpec instanceof PBEParameterSpec)) {
                throw new InvalidParameterSpecException("PBEParameterSpec required to initialise a PBKDF1 PBE parameters algorithm parameters object");
            }
            final PBEParameterSpec pbeParameterSpec = (PBEParameterSpec)algorithmParameterSpec;
            this.params = new PBEParameter(pbeParameterSpec.getSalt(), pbeParameterSpec.getIterationCount());
        }
        
        @Override
        protected void engineInit(final byte[] array) throws IOException {
            this.params = PBEParameter.getInstance(array);
        }
        
        @Override
        protected void engineInit(final byte[] array, final String s) throws IOException {
            if (this.isASN1FormatString(s)) {
                this.engineInit(array);
                return;
            }
            throw new IOException("Unknown parameters format in PBKDF2 parameters object");
        }
        
        @Override
        protected String engineToString() {
            return "PBKDF1 Parameters";
        }
    }
    
    public static class Mappings extends AlgorithmProvider
    {
        private static final String PREFIX;
        
        @Override
        public void configure(final ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("AlgorithmParameters.PBKDF1", Mappings.PREFIX + "$AlgParams");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters." + PKCSObjectIdentifiers.pbeWithMD2AndDES_CBC, "PBKDF1");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters." + PKCSObjectIdentifiers.pbeWithMD5AndDES_CBC, "PBKDF1");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters." + PKCSObjectIdentifiers.pbeWithMD5AndRC2_CBC, "PBKDF1");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters." + PKCSObjectIdentifiers.pbeWithSHA1AndDES_CBC, "PBKDF1");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters." + PKCSObjectIdentifiers.pbeWithSHA1AndRC2_CBC, "PBKDF1");
        }
        
        static {
            PREFIX = PBEPBKDF1.class.getName();
        }
    }
}
