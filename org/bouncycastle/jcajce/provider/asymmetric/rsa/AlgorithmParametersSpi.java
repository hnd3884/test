package org.bouncycastle.jcajce.provider.asymmetric.rsa;

import org.bouncycastle.asn1.pkcs.RSASSAPSSparams;
import org.bouncycastle.asn1.ASN1Integer;
import java.security.spec.PSSParameterSpec;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.jcajce.util.MessageDigestUtils;
import java.io.IOException;
import org.bouncycastle.asn1.pkcs.RSAESOAEPparams;
import org.bouncycastle.asn1.DEROctetString;
import javax.crypto.spec.PSource;
import java.security.spec.MGF1ParameterSpec;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.jcajce.provider.util.DigestFactory;
import javax.crypto.spec.OAEPParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.AlgorithmParameterSpec;

public abstract class AlgorithmParametersSpi extends java.security.AlgorithmParametersSpi
{
    protected boolean isASN1FormatString(final String s) {
        return s == null || s.equals("ASN.1");
    }
    
    @Override
    protected AlgorithmParameterSpec engineGetParameterSpec(final Class clazz) throws InvalidParameterSpecException {
        if (clazz == null) {
            throw new NullPointerException("argument to getParameterSpec must not be null");
        }
        return this.localEngineGetParameterSpec(clazz);
    }
    
    protected abstract AlgorithmParameterSpec localEngineGetParameterSpec(final Class p0) throws InvalidParameterSpecException;
    
    public static class OAEP extends AlgorithmParametersSpi
    {
        OAEPParameterSpec currentSpec;
        
        @Override
        protected byte[] engineGetEncoded() {
            final RSAESOAEPparams rsaesoaePparams = new RSAESOAEPparams(new AlgorithmIdentifier(DigestFactory.getOID(this.currentSpec.getDigestAlgorithm()), DERNull.INSTANCE), new AlgorithmIdentifier(PKCSObjectIdentifiers.id_mgf1, new AlgorithmIdentifier(DigestFactory.getOID(((MGF1ParameterSpec)this.currentSpec.getMGFParameters()).getDigestAlgorithm()), DERNull.INSTANCE)), new AlgorithmIdentifier(PKCSObjectIdentifiers.id_pSpecified, new DEROctetString(((PSource.PSpecified)this.currentSpec.getPSource()).getValue())));
            try {
                return rsaesoaePparams.getEncoded("DER");
            }
            catch (final IOException ex) {
                throw new RuntimeException("Error encoding OAEPParameters");
            }
        }
        
        @Override
        protected byte[] engineGetEncoded(final String s) {
            if (this.isASN1FormatString(s) || s.equalsIgnoreCase("X.509")) {
                return this.engineGetEncoded();
            }
            return null;
        }
        
        @Override
        protected AlgorithmParameterSpec localEngineGetParameterSpec(final Class clazz) throws InvalidParameterSpecException {
            if (clazz == OAEPParameterSpec.class || clazz == AlgorithmParameterSpec.class) {
                return this.currentSpec;
            }
            throw new InvalidParameterSpecException("unknown parameter spec passed to OAEP parameters object.");
        }
        
        @Override
        protected void engineInit(final AlgorithmParameterSpec algorithmParameterSpec) throws InvalidParameterSpecException {
            if (!(algorithmParameterSpec instanceof OAEPParameterSpec)) {
                throw new InvalidParameterSpecException("OAEPParameterSpec required to initialise an OAEP algorithm parameters object");
            }
            this.currentSpec = (OAEPParameterSpec)algorithmParameterSpec;
        }
        
        @Override
        protected void engineInit(final byte[] array) throws IOException {
            try {
                final RSAESOAEPparams instance = RSAESOAEPparams.getInstance(array);
                if (!instance.getMaskGenAlgorithm().getAlgorithm().equals(PKCSObjectIdentifiers.id_mgf1)) {
                    throw new IOException("unknown mask generation function: " + instance.getMaskGenAlgorithm().getAlgorithm());
                }
                this.currentSpec = new OAEPParameterSpec(MessageDigestUtils.getDigestName(instance.getHashAlgorithm().getAlgorithm()), OAEPParameterSpec.DEFAULT.getMGFAlgorithm(), new MGF1ParameterSpec(MessageDigestUtils.getDigestName(AlgorithmIdentifier.getInstance(instance.getMaskGenAlgorithm().getParameters()).getAlgorithm())), new PSource.PSpecified(ASN1OctetString.getInstance(instance.getPSourceAlgorithm().getParameters()).getOctets()));
            }
            catch (final ClassCastException ex) {
                throw new IOException("Not a valid OAEP Parameter encoding.");
            }
            catch (final ArrayIndexOutOfBoundsException ex2) {
                throw new IOException("Not a valid OAEP Parameter encoding.");
            }
        }
        
        @Override
        protected void engineInit(final byte[] array, final String s) throws IOException {
            if (s.equalsIgnoreCase("X.509") || s.equalsIgnoreCase("ASN.1")) {
                this.engineInit(array);
                return;
            }
            throw new IOException("Unknown parameter format " + s);
        }
        
        @Override
        protected String engineToString() {
            return "OAEP Parameters";
        }
    }
    
    public static class PSS extends AlgorithmParametersSpi
    {
        PSSParameterSpec currentSpec;
        
        @Override
        protected byte[] engineGetEncoded() throws IOException {
            final PSSParameterSpec currentSpec = this.currentSpec;
            return new RSASSAPSSparams(new AlgorithmIdentifier(DigestFactory.getOID(currentSpec.getDigestAlgorithm()), DERNull.INSTANCE), new AlgorithmIdentifier(PKCSObjectIdentifiers.id_mgf1, new AlgorithmIdentifier(DigestFactory.getOID(((MGF1ParameterSpec)currentSpec.getMGFParameters()).getDigestAlgorithm()), DERNull.INSTANCE)), new ASN1Integer(currentSpec.getSaltLength()), new ASN1Integer(currentSpec.getTrailerField())).getEncoded("DER");
        }
        
        @Override
        protected byte[] engineGetEncoded(final String s) throws IOException {
            if (s.equalsIgnoreCase("X.509") || s.equalsIgnoreCase("ASN.1")) {
                return this.engineGetEncoded();
            }
            return null;
        }
        
        @Override
        protected AlgorithmParameterSpec localEngineGetParameterSpec(final Class clazz) throws InvalidParameterSpecException {
            if (clazz == PSSParameterSpec.class && this.currentSpec != null) {
                return this.currentSpec;
            }
            throw new InvalidParameterSpecException("unknown parameter spec passed to PSS parameters object.");
        }
        
        @Override
        protected void engineInit(final AlgorithmParameterSpec algorithmParameterSpec) throws InvalidParameterSpecException {
            if (!(algorithmParameterSpec instanceof PSSParameterSpec)) {
                throw new InvalidParameterSpecException("PSSParameterSpec required to initialise an PSS algorithm parameters object");
            }
            this.currentSpec = (PSSParameterSpec)algorithmParameterSpec;
        }
        
        @Override
        protected void engineInit(final byte[] array) throws IOException {
            try {
                final RSASSAPSSparams instance = RSASSAPSSparams.getInstance(array);
                if (!instance.getMaskGenAlgorithm().getAlgorithm().equals(PKCSObjectIdentifiers.id_mgf1)) {
                    throw new IOException("unknown mask generation function: " + instance.getMaskGenAlgorithm().getAlgorithm());
                }
                this.currentSpec = new PSSParameterSpec(MessageDigestUtils.getDigestName(instance.getHashAlgorithm().getAlgorithm()), PSSParameterSpec.DEFAULT.getMGFAlgorithm(), new MGF1ParameterSpec(MessageDigestUtils.getDigestName(AlgorithmIdentifier.getInstance(instance.getMaskGenAlgorithm().getParameters()).getAlgorithm())), instance.getSaltLength().intValue(), instance.getTrailerField().intValue());
            }
            catch (final ClassCastException ex) {
                throw new IOException("Not a valid PSS Parameter encoding.");
            }
            catch (final ArrayIndexOutOfBoundsException ex2) {
                throw new IOException("Not a valid PSS Parameter encoding.");
            }
        }
        
        @Override
        protected void engineInit(final byte[] array, final String s) throws IOException {
            if (this.isASN1FormatString(s) || s.equalsIgnoreCase("X.509")) {
                this.engineInit(array);
                return;
            }
            throw new IOException("Unknown parameter format " + s);
        }
        
        @Override
        protected String engineToString() {
            return "PSS Parameters";
        }
    }
}
