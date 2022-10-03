package org.bouncycastle.jce.netscape;

import java.io.ByteArrayOutputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import java.security.SecureRandom;
import java.security.PrivateKey;
import java.security.SignatureException;
import java.security.InvalidKeyException;
import java.security.Signature;
import java.security.NoSuchProviderException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1EncodableVector;
import java.security.spec.KeySpec;
import java.security.KeyFactory;
import java.security.spec.X509EncodedKeySpec;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERIA5String;
import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.asn1.ASN1InputStream;
import java.io.ByteArrayInputStream;
import org.bouncycastle.asn1.ASN1Sequence;
import java.security.PublicKey;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.ASN1Object;

public class NetscapeCertRequest extends ASN1Object
{
    AlgorithmIdentifier sigAlg;
    AlgorithmIdentifier keyAlg;
    byte[] sigBits;
    String challenge;
    DERBitString content;
    PublicKey pubkey;
    
    private static ASN1Sequence getReq(final byte[] array) throws IOException {
        return ASN1Sequence.getInstance(new ASN1InputStream(new ByteArrayInputStream(array)).readObject());
    }
    
    public NetscapeCertRequest(final byte[] array) throws IOException {
        this(getReq(array));
    }
    
    public NetscapeCertRequest(final ASN1Sequence asn1Sequence) {
        try {
            if (asn1Sequence.size() != 3) {
                throw new IllegalArgumentException("invalid SPKAC (size):" + asn1Sequence.size());
            }
            this.sigAlg = AlgorithmIdentifier.getInstance(asn1Sequence.getObjectAt(1));
            this.sigBits = ((DERBitString)asn1Sequence.getObjectAt(2)).getOctets();
            final ASN1Sequence asn1Sequence2 = (ASN1Sequence)asn1Sequence.getObjectAt(0);
            if (asn1Sequence2.size() != 2) {
                throw new IllegalArgumentException("invalid PKAC (len): " + asn1Sequence2.size());
            }
            this.challenge = ((DERIA5String)asn1Sequence2.getObjectAt(1)).getString();
            this.content = new DERBitString(asn1Sequence2);
            final SubjectPublicKeyInfo instance = SubjectPublicKeyInfo.getInstance(asn1Sequence2.getObjectAt(0));
            final X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(new DERBitString(instance).getBytes());
            this.keyAlg = instance.getAlgorithm();
            this.pubkey = KeyFactory.getInstance(this.keyAlg.getAlgorithm().getId(), "BC").generatePublic(x509EncodedKeySpec);
        }
        catch (final Exception ex) {
            throw new IllegalArgumentException(ex.toString());
        }
    }
    
    public NetscapeCertRequest(final String challenge, final AlgorithmIdentifier sigAlg, final PublicKey pubkey) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
        this.challenge = challenge;
        this.sigAlg = sigAlg;
        this.pubkey = pubkey;
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.getKeySpec());
        asn1EncodableVector.add(new DERIA5String(challenge));
        try {
            this.content = new DERBitString(new DERSequence(asn1EncodableVector));
        }
        catch (final IOException ex) {
            throw new InvalidKeySpecException("exception encoding key: " + ex.toString());
        }
    }
    
    public String getChallenge() {
        return this.challenge;
    }
    
    public void setChallenge(final String challenge) {
        this.challenge = challenge;
    }
    
    public AlgorithmIdentifier getSigningAlgorithm() {
        return this.sigAlg;
    }
    
    public void setSigningAlgorithm(final AlgorithmIdentifier sigAlg) {
        this.sigAlg = sigAlg;
    }
    
    public AlgorithmIdentifier getKeyAlgorithm() {
        return this.keyAlg;
    }
    
    public void setKeyAlgorithm(final AlgorithmIdentifier keyAlg) {
        this.keyAlg = keyAlg;
    }
    
    public PublicKey getPublicKey() {
        return this.pubkey;
    }
    
    public void setPublicKey(final PublicKey pubkey) {
        this.pubkey = pubkey;
    }
    
    public boolean verify(final String s) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, NoSuchProviderException {
        if (!s.equals(this.challenge)) {
            return false;
        }
        final Signature instance = Signature.getInstance(this.sigAlg.getAlgorithm().getId(), "BC");
        instance.initVerify(this.pubkey);
        instance.update(this.content.getBytes());
        return instance.verify(this.sigBits);
    }
    
    public void sign(final PrivateKey privateKey) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, NoSuchProviderException, InvalidKeySpecException {
        this.sign(privateKey, null);
    }
    
    public void sign(final PrivateKey privateKey, final SecureRandom secureRandom) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, NoSuchProviderException, InvalidKeySpecException {
        final Signature instance = Signature.getInstance(this.sigAlg.getAlgorithm().getId(), "BC");
        if (secureRandom != null) {
            instance.initSign(privateKey, secureRandom);
        }
        else {
            instance.initSign(privateKey);
        }
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.getKeySpec());
        asn1EncodableVector.add(new DERIA5String(this.challenge));
        try {
            instance.update(new DERSequence(asn1EncodableVector).getEncoded("DER"));
        }
        catch (final IOException ex) {
            throw new SignatureException(ex.getMessage());
        }
        this.sigBits = instance.sign();
    }
    
    private ASN1Primitive getKeySpec() throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ASN1Primitive object;
        try {
            byteArrayOutputStream.write(this.pubkey.getEncoded());
            byteArrayOutputStream.close();
            object = new ASN1InputStream(new ByteArrayInputStream(byteArrayOutputStream.toByteArray())).readObject();
        }
        catch (final IOException ex) {
            throw new InvalidKeySpecException(ex.getMessage());
        }
        return object;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        final ASN1EncodableVector asn1EncodableVector2 = new ASN1EncodableVector();
        try {
            asn1EncodableVector2.add(this.getKeySpec());
        }
        catch (final Exception ex) {}
        asn1EncodableVector2.add(new DERIA5String(this.challenge));
        asn1EncodableVector.add(new DERSequence(asn1EncodableVector2));
        asn1EncodableVector.add(this.sigAlg);
        asn1EncodableVector.add(new DERBitString(this.sigBits));
        return new DERSequence(asn1EncodableVector);
    }
}
