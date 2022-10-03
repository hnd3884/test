package javax.xml.crypto.dsig;

import javax.xml.crypto.URIDereferencer;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.crypto.dsig.spec.SignatureMethodParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import javax.xml.crypto.dsig.spec.DigestMethodParameterSpec;
import javax.xml.crypto.Data;
import java.util.List;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.NoSuchAlgorithmException;
import javax.xml.crypto.NoSuchMechanismException;
import java.security.Provider;

public abstract class XMLSignatureFactory
{
    private String mechanismType;
    private Provider provider;
    
    protected XMLSignatureFactory() {
    }
    
    public static XMLSignatureFactory getInstance(final String s) {
        if (s == null) {
            throw new NullPointerException("mechanismType cannot be null");
        }
        return findInstance(s, null);
    }
    
    private static XMLSignatureFactory findInstance(final String mechanismType, final Provider provider) {
        Object[] array;
        try {
            array = XMLDSigSecurity.getImpl(mechanismType, "XMLSignatureFactory", provider);
        }
        catch (final NoSuchAlgorithmException ex) {
            throw new NoSuchMechanismException("Cannot find " + mechanismType + " mechanism type", ex);
        }
        final XMLSignatureFactory xmlSignatureFactory = (XMLSignatureFactory)array[0];
        xmlSignatureFactory.mechanismType = mechanismType;
        xmlSignatureFactory.provider = (Provider)array[1];
        return xmlSignatureFactory;
    }
    
    public static XMLSignatureFactory getInstance(final String s, final Provider provider) {
        if (s == null) {
            throw new NullPointerException("mechanismType cannot be null");
        }
        if (provider == null) {
            throw new NullPointerException("provider cannot be null");
        }
        return findInstance(s, provider);
    }
    
    public static XMLSignatureFactory getInstance(final String s, final String s2) throws NoSuchProviderException {
        if (s == null) {
            throw new NullPointerException("mechanismType cannot be null");
        }
        if (s2 == null) {
            throw new NullPointerException("provider cannot be null");
        }
        final Provider provider = Security.getProvider(s2);
        if (provider == null) {
            throw new NoSuchProviderException("cannot find provider named " + s2);
        }
        return findInstance(s, provider);
    }
    
    public static XMLSignatureFactory getInstance() {
        return getInstance("DOM");
    }
    
    public final String getMechanismType() {
        return this.mechanismType;
    }
    
    public final Provider getProvider() {
        return this.provider;
    }
    
    public abstract XMLSignature newXMLSignature(final SignedInfo p0, final KeyInfo p1);
    
    public abstract XMLSignature newXMLSignature(final SignedInfo p0, final KeyInfo p1, final List p2, final String p3, final String p4);
    
    public abstract Reference newReference(final String p0, final DigestMethod p1);
    
    public abstract Reference newReference(final String p0, final DigestMethod p1, final List p2, final String p3, final String p4);
    
    public abstract Reference newReference(final String p0, final DigestMethod p1, final List p2, final String p3, final String p4, final byte[] p5);
    
    public abstract Reference newReference(final String p0, final DigestMethod p1, final List p2, final Data p3, final List p4, final String p5, final String p6);
    
    public abstract SignedInfo newSignedInfo(final CanonicalizationMethod p0, final SignatureMethod p1, final List p2);
    
    public abstract SignedInfo newSignedInfo(final CanonicalizationMethod p0, final SignatureMethod p1, final List p2, final String p3);
    
    public abstract XMLObject newXMLObject(final List p0, final String p1, final String p2, final String p3);
    
    public abstract Manifest newManifest(final List p0);
    
    public abstract Manifest newManifest(final List p0, final String p1);
    
    public abstract SignatureProperty newSignatureProperty(final List p0, final String p1, final String p2);
    
    public abstract SignatureProperties newSignatureProperties(final List p0, final String p1);
    
    public abstract DigestMethod newDigestMethod(final String p0, final DigestMethodParameterSpec p1) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException;
    
    public abstract SignatureMethod newSignatureMethod(final String p0, final SignatureMethodParameterSpec p1) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException;
    
    public abstract Transform newTransform(final String p0, final TransformParameterSpec p1) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException;
    
    public abstract Transform newTransform(final String p0, final XMLStructure p1) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException;
    
    public abstract CanonicalizationMethod newCanonicalizationMethod(final String p0, final C14NMethodParameterSpec p1) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException;
    
    public abstract CanonicalizationMethod newCanonicalizationMethod(final String p0, final XMLStructure p1) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException;
    
    public final KeyInfoFactory getKeyInfoFactory() {
        return KeyInfoFactory.getInstance(this.getMechanismType(), this.getProvider());
    }
    
    public abstract XMLSignature unmarshalXMLSignature(final XMLValidateContext p0) throws MarshalException;
    
    public abstract XMLSignature unmarshalXMLSignature(final XMLStructure p0) throws MarshalException;
    
    public abstract boolean isFeatureSupported(final String p0);
    
    public abstract URIDereferencer getURIDereferencer();
}
