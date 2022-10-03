package org.bouncycastle.mozilla;

import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;
import org.bouncycastle.asn1.DERBitString;
import java.security.KeyFactory;
import java.security.PublicKey;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import java.security.Signature;
import java.security.InvalidKeyException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.NoSuchAlgorithmException;
import java.io.IOException;
import org.bouncycastle.operator.OperatorCreationException;
import java.io.OutputStream;
import org.bouncycastle.operator.ContentVerifier;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DEROutputStream;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.asn1.mozilla.PublicKeyAndChallenge;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.util.Encodable;

public class SignedPublicKeyAndChallenge implements Encodable
{
    protected final org.bouncycastle.asn1.mozilla.SignedPublicKeyAndChallenge spkacSeq;
    
    public SignedPublicKeyAndChallenge(final byte[] array) {
        this.spkacSeq = org.bouncycastle.asn1.mozilla.SignedPublicKeyAndChallenge.getInstance((Object)array);
    }
    
    protected SignedPublicKeyAndChallenge(final org.bouncycastle.asn1.mozilla.SignedPublicKeyAndChallenge spkacSeq) {
        this.spkacSeq = spkacSeq;
    }
    
    public org.bouncycastle.asn1.mozilla.SignedPublicKeyAndChallenge toASN1Structure() {
        return this.spkacSeq;
    }
    
    @Deprecated
    public ASN1Primitive toASN1Primitive() {
        return this.spkacSeq.toASN1Primitive();
    }
    
    public PublicKeyAndChallenge getPublicKeyAndChallenge() {
        return this.spkacSeq.getPublicKeyAndChallenge();
    }
    
    public boolean isSignatureValid(final ContentVerifierProvider contentVerifierProvider) throws OperatorCreationException, IOException {
        final ContentVerifier value = contentVerifierProvider.get(this.spkacSeq.getSignatureAlgorithm());
        final OutputStream outputStream = value.getOutputStream();
        new DEROutputStream(outputStream).writeObject((ASN1Encodable)this.spkacSeq.getPublicKeyAndChallenge());
        outputStream.close();
        return value.verify(this.spkacSeq.getSignature().getOctets());
    }
    
    @Deprecated
    public boolean verify() throws NoSuchAlgorithmException, SignatureException, NoSuchProviderException, InvalidKeyException {
        return this.verify(null);
    }
    
    @Deprecated
    public boolean verify(final String s) throws NoSuchAlgorithmException, SignatureException, NoSuchProviderException, InvalidKeyException {
        Signature signature;
        if (s == null) {
            signature = Signature.getInstance(this.spkacSeq.getSignatureAlgorithm().getAlgorithm().getId());
        }
        else {
            signature = Signature.getInstance(this.spkacSeq.getSignatureAlgorithm().getAlgorithm().getId(), s);
        }
        signature.initVerify(this.getPublicKey(s));
        try {
            signature.update(this.spkacSeq.getPublicKeyAndChallenge().getEncoded());
            return signature.verify(this.spkacSeq.getSignature().getBytes());
        }
        catch (final Exception ex) {
            throw new InvalidKeyException("error encoding public key");
        }
    }
    
    public SubjectPublicKeyInfo getSubjectPublicKeyInfo() {
        return this.spkacSeq.getPublicKeyAndChallenge().getSubjectPublicKeyInfo();
    }
    
    public String getChallenge() {
        return this.spkacSeq.getPublicKeyAndChallenge().getChallenge().getString();
    }
    
    @Deprecated
    public PublicKey getPublicKey(final String s) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException {
        final SubjectPublicKeyInfo subjectPublicKeyInfo = this.spkacSeq.getPublicKeyAndChallenge().getSubjectPublicKeyInfo();
        try {
            return KeyFactory.getInstance(subjectPublicKeyInfo.getAlgorithm().getAlgorithm().getId(), s).generatePublic(new X509EncodedKeySpec(new DERBitString((ASN1Encodable)subjectPublicKeyInfo).getOctets()));
        }
        catch (final Exception ex) {
            throw new InvalidKeyException("error encoding public key");
        }
    }
    
    public byte[] getEncoded() throws IOException {
        return this.toASN1Structure().getEncoded();
    }
}
