package org.bouncycastle.jcajce.provider.symmetric;

import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;
import org.bouncycastle.asn1.ASN1Primitive;
import java.security.spec.InvalidParameterSpecException;
import javax.crypto.spec.PBEParameterSpec;
import java.security.spec.AlgorithmParameterSpec;
import java.io.IOException;
import org.bouncycastle.asn1.pkcs.PKCS12PBEParams;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseAlgorithmParameters;

public class PBEPKCS12
{
    private PBEPKCS12() {
    }
    
    public static class AlgParams extends BaseAlgorithmParameters
    {
        PKCS12PBEParams params;
        
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
                return new PBEParameterSpec(this.params.getIV(), this.params.getIterations().intValue());
            }
            throw new InvalidParameterSpecException("unknown parameter spec passed to PKCS12 PBE parameters object.");
        }
        
        @Override
        protected void engineInit(final AlgorithmParameterSpec algorithmParameterSpec) throws InvalidParameterSpecException {
            if (!(algorithmParameterSpec instanceof PBEParameterSpec)) {
                throw new InvalidParameterSpecException("PBEParameterSpec required to initialise a PKCS12 PBE parameters algorithm parameters object");
            }
            final PBEParameterSpec pbeParameterSpec = (PBEParameterSpec)algorithmParameterSpec;
            this.params = new PKCS12PBEParams(pbeParameterSpec.getSalt(), pbeParameterSpec.getIterationCount());
        }
        
        @Override
        protected void engineInit(final byte[] array) throws IOException {
            this.params = PKCS12PBEParams.getInstance(ASN1Primitive.fromByteArray(array));
        }
        
        @Override
        protected void engineInit(final byte[] array, final String s) throws IOException {
            if (this.isASN1FormatString(s)) {
                this.engineInit(array);
                return;
            }
            throw new IOException("Unknown parameters format in PKCS12 PBE parameters object");
        }
        
        @Override
        protected String engineToString() {
            return "PKCS12 PBE Parameters";
        }
    }
    
    public static class Mappings extends AlgorithmProvider
    {
        private static final String PREFIX;
        
        @Override
        public void configure(final ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("AlgorithmParameters.PKCS12PBE", Mappings.PREFIX + "$AlgParams");
        }
        
        static {
            PREFIX = PBEPKCS12.class.getName();
        }
    }
}
