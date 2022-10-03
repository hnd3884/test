package sun.security.rsa;

import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import java.io.IOException;
import sun.security.x509.AlgorithmId;
import sun.security.util.DerInputStream;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.PSSParameterSpec;
import java.security.AlgorithmParametersSpi;

public final class PSSParameters extends AlgorithmParametersSpi
{
    private PSSParameterSpec spec;
    
    @Override
    protected void engineInit(final AlgorithmParameterSpec algorithmParameterSpec) throws InvalidParameterSpecException {
        if (!(algorithmParameterSpec instanceof PSSParameterSpec)) {
            throw new InvalidParameterSpecException("Inappropriate parameter specification");
        }
        final PSSParameterSpec spec = (PSSParameterSpec)algorithmParameterSpec;
        final String mgfAlgorithm = spec.getMGFAlgorithm();
        if (!spec.getMGFAlgorithm().equalsIgnoreCase("MGF1")) {
            throw new InvalidParameterSpecException("Unsupported mgf " + mgfAlgorithm + "; MGF1 only");
        }
        if (!(spec.getMGFParameters() instanceof MGF1ParameterSpec)) {
            throw new InvalidParameterSpecException("Inappropriate mgf parameters; non-null MGF1ParameterSpec only");
        }
        this.spec = spec;
    }
    
    @Override
    protected void engineInit(final byte[] array) throws IOException {
        String s = PSSParameterSpec.DEFAULT.getDigestAlgorithm();
        MGF1ParameterSpec mgf1ParameterSpec = (MGF1ParameterSpec)PSSParameterSpec.DEFAULT.getMGFParameters();
        int n = PSSParameterSpec.DEFAULT.getSaltLength();
        int n2 = PSSParameterSpec.DEFAULT.getTrailerField();
        for (final DerValue derValue : new DerInputStream(array).getSequence(4)) {
            if (derValue.isContextSpecific((byte)0)) {
                s = AlgorithmId.parse(derValue.data.getDerValue()).getName();
            }
            else if (derValue.isContextSpecific((byte)1)) {
                final AlgorithmId parse = AlgorithmId.parse(derValue.data.getDerValue());
                if (!parse.getOID().equals(AlgorithmId.mgf1_oid)) {
                    throw new IOException("Only MGF1 mgf is supported");
                }
                final String name;
                final String s2 = name = AlgorithmId.parse(new DerValue(parse.getEncodedParams())).getName();
                switch (name) {
                    case "SHA-1": {
                        mgf1ParameterSpec = MGF1ParameterSpec.SHA1;
                        break;
                    }
                    case "SHA-224": {
                        mgf1ParameterSpec = MGF1ParameterSpec.SHA224;
                        break;
                    }
                    case "SHA-256": {
                        mgf1ParameterSpec = MGF1ParameterSpec.SHA256;
                        break;
                    }
                    case "SHA-384": {
                        mgf1ParameterSpec = MGF1ParameterSpec.SHA384;
                        break;
                    }
                    case "SHA-512": {
                        mgf1ParameterSpec = MGF1ParameterSpec.SHA512;
                        break;
                    }
                    case "SHA-512/224": {
                        mgf1ParameterSpec = MGF1ParameterSpec.SHA512_224;
                        break;
                    }
                    case "SHA-512/256": {
                        mgf1ParameterSpec = MGF1ParameterSpec.SHA512_256;
                        break;
                    }
                    default: {
                        throw new IOException("Unrecognized message digest algorithm " + s2);
                    }
                }
            }
            else if (derValue.isContextSpecific((byte)2)) {
                n = derValue.data.getDerValue().getInteger();
                if (n < 0) {
                    throw new IOException("Negative value for saltLength");
                }
            }
            else {
                if (!derValue.isContextSpecific((byte)3)) {
                    throw new IOException("Invalid encoded PSSParameters");
                }
                n2 = derValue.data.getDerValue().getInteger();
                if (n2 != 1) {
                    throw new IOException("Unsupported trailerField value " + n2);
                }
            }
        }
        this.spec = new PSSParameterSpec(s, "MGF1", mgf1ParameterSpec, n, n2);
    }
    
    @Override
    protected void engineInit(final byte[] array, final String s) throws IOException {
        if (s != null && !s.equalsIgnoreCase("ASN.1")) {
            throw new IllegalArgumentException("Only support ASN.1 format");
        }
        this.engineInit(array);
    }
    
    @Override
    protected <T extends AlgorithmParameterSpec> T engineGetParameterSpec(final Class<T> clazz) throws InvalidParameterSpecException {
        if (PSSParameterSpec.class.isAssignableFrom(clazz)) {
            return clazz.cast(this.spec);
        }
        throw new InvalidParameterSpecException("Inappropriate parameter specification");
    }
    
    @Override
    protected byte[] engineGetEncoded() throws IOException {
        return getEncoded(this.spec);
    }
    
    @Override
    protected byte[] engineGetEncoded(final String s) throws IOException {
        if (s != null && !s.equalsIgnoreCase("ASN.1")) {
            throw new IllegalArgumentException("Only support ASN.1 format");
        }
        return this.engineGetEncoded();
    }
    
    @Override
    protected String engineToString() {
        return this.spec.toString();
    }
    
    public static byte[] getEncoded(final PSSParameterSpec pssParameterSpec) throws IOException {
        final AlgorithmParameterSpec mgfParameters = pssParameterSpec.getMGFParameters();
        if (!(mgfParameters instanceof MGF1ParameterSpec)) {
            throw new IOException("Cannot encode " + mgfParameters);
        }
        final MGF1ParameterSpec mgf1ParameterSpec = (MGF1ParameterSpec)mgfParameters;
        final DerOutputStream derOutputStream = new DerOutputStream();
        AlgorithmId value;
        try {
            value = AlgorithmId.get(pssParameterSpec.getDigestAlgorithm());
        }
        catch (final NoSuchAlgorithmException ex) {
            throw new IOException("AlgorithmId " + pssParameterSpec.getDigestAlgorithm() + " impl not found");
        }
        if (!value.getOID().equals(AlgorithmId.SHA_oid)) {
            final DerOutputStream derOutputStream2 = new DerOutputStream();
            value.derEncode(derOutputStream2);
            derOutputStream.write(DerValue.createTag((byte)(-128), true, (byte)0), derOutputStream2);
        }
        AlgorithmId value2;
        try {
            value2 = AlgorithmId.get(mgf1ParameterSpec.getDigestAlgorithm());
        }
        catch (final NoSuchAlgorithmException ex2) {
            throw new IOException("AlgorithmId " + mgf1ParameterSpec.getDigestAlgorithm() + " impl not found");
        }
        if (!value2.getOID().equals(AlgorithmId.SHA_oid)) {
            final DerOutputStream derOutputStream3 = new DerOutputStream();
            derOutputStream3.putOID(AlgorithmId.mgf1_oid);
            value2.encode(derOutputStream3);
            final DerOutputStream derOutputStream4 = new DerOutputStream();
            derOutputStream4.write((byte)48, derOutputStream3);
            derOutputStream.write(DerValue.createTag((byte)(-128), true, (byte)1), derOutputStream4);
        }
        if (pssParameterSpec.getSaltLength() != 20) {
            final DerOutputStream derOutputStream5 = new DerOutputStream();
            derOutputStream5.putInteger(pssParameterSpec.getSaltLength());
            derOutputStream.write(DerValue.createTag((byte)(-128), true, (byte)2), derOutputStream5);
        }
        if (pssParameterSpec.getTrailerField() != 1) {
            final DerOutputStream derOutputStream6 = new DerOutputStream();
            derOutputStream6.putInteger(pssParameterSpec.getTrailerField());
            derOutputStream.write(DerValue.createTag((byte)(-128), true, (byte)3), derOutputStream6);
        }
        final DerOutputStream derOutputStream7 = new DerOutputStream();
        derOutputStream7.write((byte)48, derOutputStream);
        return derOutputStream7.toByteArray();
    }
}
