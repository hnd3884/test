package com.sun.crypto.provider;

import sun.security.util.Debug;
import java.math.BigInteger;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import java.io.IOException;
import sun.security.x509.AlgorithmId;
import sun.security.util.DerInputStream;
import javax.crypto.spec.PSource;
import java.security.spec.InvalidParameterSpecException;
import javax.crypto.spec.OAEPParameterSpec;
import java.security.spec.AlgorithmParameterSpec;
import sun.security.util.ObjectIdentifier;
import java.security.spec.MGF1ParameterSpec;
import java.security.AlgorithmParametersSpi;

public final class OAEPParameters extends AlgorithmParametersSpi
{
    private String mdName;
    private MGF1ParameterSpec mgfSpec;
    private byte[] p;
    private static ObjectIdentifier OID_MGF1;
    private static ObjectIdentifier OID_PSpecified;
    
    @Override
    protected void engineInit(final AlgorithmParameterSpec algorithmParameterSpec) throws InvalidParameterSpecException {
        if (!(algorithmParameterSpec instanceof OAEPParameterSpec)) {
            throw new InvalidParameterSpecException("Inappropriate parameter specification");
        }
        final OAEPParameterSpec oaepParameterSpec = (OAEPParameterSpec)algorithmParameterSpec;
        this.mdName = oaepParameterSpec.getDigestAlgorithm();
        final String mgfAlgorithm = oaepParameterSpec.getMGFAlgorithm();
        if (!mgfAlgorithm.equalsIgnoreCase("MGF1")) {
            throw new InvalidParameterSpecException("Unsupported mgf " + mgfAlgorithm + "; MGF1 only");
        }
        final AlgorithmParameterSpec mgfParameters = oaepParameterSpec.getMGFParameters();
        if (!(mgfParameters instanceof MGF1ParameterSpec)) {
            throw new InvalidParameterSpecException("Inappropriate mgf parameters; non-null MGF1ParameterSpec only");
        }
        this.mgfSpec = (MGF1ParameterSpec)mgfParameters;
        final PSource pSource = oaepParameterSpec.getPSource();
        if (pSource.getAlgorithm().equals("PSpecified")) {
            this.p = ((PSource.PSpecified)pSource).getValue();
            return;
        }
        throw new InvalidParameterSpecException("Unsupported pSource " + pSource.getAlgorithm() + "; PSpecified only");
    }
    
    @Override
    protected void engineInit(final byte[] array) throws IOException {
        final DerInputStream derInputStream = new DerInputStream(array);
        this.mdName = "SHA-1";
        this.mgfSpec = MGF1ParameterSpec.SHA1;
        this.p = new byte[0];
        final DerValue[] sequence = derInputStream.getSequence(3);
        for (int i = 0; i < sequence.length; ++i) {
            final DerValue derValue = sequence[i];
            if (derValue.isContextSpecific((byte)0)) {
                this.mdName = AlgorithmId.parse(derValue.data.getDerValue()).getName();
            }
            else if (derValue.isContextSpecific((byte)1)) {
                final AlgorithmId parse = AlgorithmId.parse(derValue.data.getDerValue());
                if (!parse.getOID().equals((Object)OAEPParameters.OID_MGF1)) {
                    throw new IOException("Only MGF1 mgf is supported");
                }
                final String name = AlgorithmId.parse(new DerValue(parse.getEncodedParams())).getName();
                if (name.equals("SHA-1")) {
                    this.mgfSpec = MGF1ParameterSpec.SHA1;
                }
                else if (name.equals("SHA-224")) {
                    this.mgfSpec = MGF1ParameterSpec.SHA224;
                }
                else if (name.equals("SHA-256")) {
                    this.mgfSpec = MGF1ParameterSpec.SHA256;
                }
                else if (name.equals("SHA-384")) {
                    this.mgfSpec = MGF1ParameterSpec.SHA384;
                }
                else if (name.equals("SHA-512")) {
                    this.mgfSpec = MGF1ParameterSpec.SHA512;
                }
                else if (name.equals("SHA-512/224")) {
                    this.mgfSpec = MGF1ParameterSpec.SHA512_224;
                }
                else {
                    if (!name.equals("SHA-512/256")) {
                        throw new IOException("Unrecognized message digest algorithm");
                    }
                    this.mgfSpec = MGF1ParameterSpec.SHA512_256;
                }
            }
            else {
                if (!derValue.isContextSpecific((byte)2)) {
                    throw new IOException("Invalid encoded OAEPParameters");
                }
                final AlgorithmId parse2 = AlgorithmId.parse(derValue.data.getDerValue());
                if (!parse2.getOID().equals((Object)OAEPParameters.OID_PSpecified)) {
                    throw new IOException("Wrong OID for pSpecified");
                }
                final DerInputStream derInputStream2 = new DerInputStream(parse2.getEncodedParams());
                this.p = derInputStream2.getOctetString();
                if (derInputStream2.available() != 0) {
                    throw new IOException("Extra data for pSpecified");
                }
            }
        }
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
        if (OAEPParameterSpec.class.isAssignableFrom(clazz)) {
            return clazz.cast(new OAEPParameterSpec(this.mdName, "MGF1", this.mgfSpec, new PSource.PSpecified(this.p)));
        }
        throw new InvalidParameterSpecException("Inappropriate parameter specification");
    }
    
    @Override
    protected byte[] engineGetEncoded() throws IOException {
        final DerOutputStream derOutputStream = new DerOutputStream();
        AlgorithmId value;
        try {
            value = AlgorithmId.get(this.mdName);
        }
        catch (final NoSuchAlgorithmException ex) {
            throw new IOException("AlgorithmId " + this.mdName + " impl not found");
        }
        final DerOutputStream derOutputStream2 = new DerOutputStream();
        value.derEncode(derOutputStream2);
        derOutputStream.write(DerValue.createTag((byte)(-128), true, (byte)0), derOutputStream2);
        final DerOutputStream derOutputStream3 = new DerOutputStream();
        derOutputStream3.putOID(OAEPParameters.OID_MGF1);
        AlgorithmId value2;
        try {
            value2 = AlgorithmId.get(this.mgfSpec.getDigestAlgorithm());
        }
        catch (final NoSuchAlgorithmException ex2) {
            throw new IOException("AlgorithmId " + this.mgfSpec.getDigestAlgorithm() + " impl not found");
        }
        value2.encode(derOutputStream3);
        final DerOutputStream derOutputStream4 = new DerOutputStream();
        derOutputStream4.write((byte)48, derOutputStream3);
        derOutputStream.write(DerValue.createTag((byte)(-128), true, (byte)1), derOutputStream4);
        final DerOutputStream derOutputStream5 = new DerOutputStream();
        derOutputStream5.putOID(OAEPParameters.OID_PSpecified);
        derOutputStream5.putOctetString(this.p);
        final DerOutputStream derOutputStream6 = new DerOutputStream();
        derOutputStream6.write((byte)48, derOutputStream5);
        derOutputStream.write(DerValue.createTag((byte)(-128), true, (byte)2), derOutputStream6);
        final DerOutputStream derOutputStream7 = new DerOutputStream();
        derOutputStream7.write((byte)48, derOutputStream);
        return derOutputStream7.toByteArray();
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
        final StringBuffer sb = new StringBuffer();
        sb.append("MD: " + this.mdName + "\n");
        sb.append("MGF: MGF1" + this.mgfSpec.getDigestAlgorithm() + "\n");
        sb.append("PSource: PSpecified " + ((this.p.length == 0) ? "" : Debug.toHexString(new BigInteger(this.p))) + "\n");
        return sb.toString();
    }
    
    static {
        try {
            OAEPParameters.OID_MGF1 = new ObjectIdentifier(new int[] { 1, 2, 840, 113549, 1, 1, 8 });
        }
        catch (final IOException ex) {
            OAEPParameters.OID_MGF1 = null;
        }
        try {
            OAEPParameters.OID_PSpecified = new ObjectIdentifier(new int[] { 1, 2, 840, 113549, 1, 1, 9 });
        }
        catch (final IOException ex2) {
            OAEPParameters.OID_PSpecified = null;
        }
    }
}
