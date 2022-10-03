package org.apache.xml.security.algorithms;

import org.apache.xml.security.exceptions.AlgorithmAlreadyRegisteredException;
import org.apache.commons.logging.LogFactory;
import java.security.spec.AlgorithmParameterSpec;
import java.security.SecureRandom;
import java.security.Key;
import org.w3c.dom.Element;
import org.apache.xml.security.algorithms.implementations.IntegrityHmac;
import java.util.Map;
import org.apache.xml.security.signature.XMLSignatureException;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.w3c.dom.Document;
import java.util.HashMap;
import org.apache.commons.logging.Log;

public class SignatureAlgorithm extends Algorithm
{
    static Log log;
    static boolean _alreadyInitialized;
    static HashMap _algorithmHash;
    static ThreadLocal instancesSigning;
    static ThreadLocal instancesVerify;
    static ThreadLocal keysSigning;
    static ThreadLocal keysVerify;
    protected SignatureAlgorithmSpi _signatureAlgorithm;
    private String algorithmURI;
    
    public SignatureAlgorithm(final Document document, final String algorithmURI) throws XMLSecurityException {
        super(document, algorithmURI);
        this._signatureAlgorithm = null;
        this.algorithmURI = algorithmURI;
    }
    
    private void initializeAlgorithm(final boolean b) throws XMLSignatureException {
        if (this._signatureAlgorithm != null) {
            return;
        }
        (this._signatureAlgorithm = (b ? getInstanceForSigning(this.algorithmURI) : getInstanceForVerify(this.algorithmURI))).engineGetContextFromElement(super._constructionElement);
    }
    
    private static SignatureAlgorithmSpi getInstanceForSigning(final String s) throws XMLSignatureException {
        final SignatureAlgorithmSpi signatureAlgorithmSpi = SignatureAlgorithm.instancesSigning.get().get(s);
        if (signatureAlgorithmSpi != null) {
            signatureAlgorithmSpi.reset();
            return signatureAlgorithmSpi;
        }
        final SignatureAlgorithmSpi buildSigner = buildSigner(s, signatureAlgorithmSpi);
        SignatureAlgorithm.instancesSigning.get().put(s, buildSigner);
        return buildSigner;
    }
    
    private static SignatureAlgorithmSpi getInstanceForVerify(final String s) throws XMLSignatureException {
        final SignatureAlgorithmSpi signatureAlgorithmSpi = SignatureAlgorithm.instancesVerify.get().get(s);
        if (signatureAlgorithmSpi != null) {
            signatureAlgorithmSpi.reset();
            return signatureAlgorithmSpi;
        }
        final SignatureAlgorithmSpi buildSigner = buildSigner(s, signatureAlgorithmSpi);
        SignatureAlgorithm.instancesVerify.get().put(s, buildSigner);
        return buildSigner;
    }
    
    private static SignatureAlgorithmSpi buildSigner(final String s, SignatureAlgorithmSpi signatureAlgorithmSpi) throws XMLSignatureException {
        try {
            final Class implementingClass = getImplementingClass(s);
            if (SignatureAlgorithm.log.isDebugEnabled()) {
                SignatureAlgorithm.log.debug((Object)("Create URI \"" + s + "\" class \"" + implementingClass + "\""));
            }
            signatureAlgorithmSpi = (SignatureAlgorithmSpi)implementingClass.newInstance();
            return signatureAlgorithmSpi;
        }
        catch (final IllegalAccessException ex) {
            throw new XMLSignatureException("algorithms.NoSuchAlgorithm", new Object[] { s, ex.getMessage() }, ex);
        }
        catch (final InstantiationException ex2) {
            throw new XMLSignatureException("algorithms.NoSuchAlgorithm", new Object[] { s, ex2.getMessage() }, ex2);
        }
        catch (final NullPointerException ex3) {
            throw new XMLSignatureException("algorithms.NoSuchAlgorithm", new Object[] { s, ex3.getMessage() }, ex3);
        }
    }
    
    public SignatureAlgorithm(final Document document, final String algorithmURI, final int n) throws XMLSecurityException {
        this(document, algorithmURI);
        this.algorithmURI = algorithmURI;
        this.initializeAlgorithm(true);
        this._signatureAlgorithm.engineSetHMACOutputLength(n);
        ((IntegrityHmac)this._signatureAlgorithm).engineAddContextToElement(super._constructionElement);
    }
    
    public SignatureAlgorithm(final Element element, final String s) throws XMLSecurityException {
        super(element, s);
        this._signatureAlgorithm = null;
        this.algorithmURI = this.getURI();
    }
    
    public byte[] sign() throws XMLSignatureException {
        return this._signatureAlgorithm.engineSign();
    }
    
    public String getJCEAlgorithmString() {
        try {
            return getInstanceForVerify(this.algorithmURI).engineGetJCEAlgorithmString();
        }
        catch (final XMLSignatureException ex) {
            return null;
        }
    }
    
    public String getJCEProviderName() {
        try {
            return getInstanceForVerify(this.algorithmURI).engineGetJCEProviderName();
        }
        catch (final XMLSignatureException ex) {
            return null;
        }
    }
    
    public void update(final byte[] array) throws XMLSignatureException {
        this._signatureAlgorithm.engineUpdate(array);
    }
    
    public void update(final byte b) throws XMLSignatureException {
        this._signatureAlgorithm.engineUpdate(b);
    }
    
    public void update(final byte[] array, final int n, final int n2) throws XMLSignatureException {
        this._signatureAlgorithm.engineUpdate(array, n, n2);
    }
    
    public void initSign(final Key key) throws XMLSignatureException {
        this.initializeAlgorithm(true);
        final Map map = SignatureAlgorithm.keysSigning.get();
        if (map.get(this.algorithmURI) == key) {
            return;
        }
        map.put(this.algorithmURI, key);
        this._signatureAlgorithm.engineInitSign(key);
    }
    
    public void initSign(final Key key, final SecureRandom secureRandom) throws XMLSignatureException {
        this.initializeAlgorithm(true);
        this._signatureAlgorithm.engineInitSign(key, secureRandom);
    }
    
    public void initSign(final Key key, final AlgorithmParameterSpec algorithmParameterSpec) throws XMLSignatureException {
        this.initializeAlgorithm(true);
        this._signatureAlgorithm.engineInitSign(key, algorithmParameterSpec);
    }
    
    public void setParameter(final AlgorithmParameterSpec algorithmParameterSpec) throws XMLSignatureException {
        this._signatureAlgorithm.engineSetParameter(algorithmParameterSpec);
    }
    
    public void initVerify(final Key key) throws XMLSignatureException {
        this.initializeAlgorithm(false);
        final Map map = SignatureAlgorithm.keysVerify.get();
        if (map.get(this.algorithmURI) == key) {
            return;
        }
        map.put(this.algorithmURI, key);
        this._signatureAlgorithm.engineInitVerify(key);
    }
    
    public boolean verify(final byte[] array) throws XMLSignatureException {
        return this._signatureAlgorithm.engineVerify(array);
    }
    
    public final String getURI() {
        return super._constructionElement.getAttributeNS(null, "Algorithm");
    }
    
    public static void providerInit() {
        if (SignatureAlgorithm.log == null) {
            SignatureAlgorithm.log = LogFactory.getLog(SignatureAlgorithm.class.getName());
        }
        SignatureAlgorithm.log.debug((Object)"Init() called");
        if (!SignatureAlgorithm._alreadyInitialized) {
            SignatureAlgorithm._algorithmHash = new HashMap(10);
            SignatureAlgorithm._alreadyInitialized = true;
        }
    }
    
    public static void register(final String s, final String s2) throws AlgorithmAlreadyRegisteredException, XMLSignatureException {
        if (SignatureAlgorithm.log.isDebugEnabled()) {
            SignatureAlgorithm.log.debug((Object)("Try to register " + s + " " + s2));
        }
        final Class implementingClass = getImplementingClass(s);
        if (implementingClass != null) {
            final String name = implementingClass.getName();
            if (name != null && name.length() != 0) {
                throw new AlgorithmAlreadyRegisteredException("algorithm.alreadyRegistered", new Object[] { s, name });
            }
        }
        try {
            SignatureAlgorithm._algorithmHash.put(s, Class.forName(s2));
        }
        catch (final ClassNotFoundException ex) {
            throw new XMLSignatureException("algorithms.NoSuchAlgorithm", new Object[] { s, ex.getMessage() }, ex);
        }
        catch (final NullPointerException ex2) {
            throw new XMLSignatureException("algorithms.NoSuchAlgorithm", new Object[] { s, ex2.getMessage() }, ex2);
        }
    }
    
    private static Class getImplementingClass(final String s) {
        if (SignatureAlgorithm._algorithmHash == null) {
            return null;
        }
        return SignatureAlgorithm._algorithmHash.get(s);
    }
    
    public String getBaseNamespace() {
        return "http://www.w3.org/2000/09/xmldsig#";
    }
    
    public String getBaseLocalName() {
        return "SignatureMethod";
    }
    
    static {
        SignatureAlgorithm.log = LogFactory.getLog(SignatureAlgorithm.class.getName());
        SignatureAlgorithm._alreadyInitialized = false;
        SignatureAlgorithm._algorithmHash = null;
        SignatureAlgorithm.instancesSigning = new ThreadLocal() {
            protected Object initialValue() {
                return new HashMap();
            }
        };
        SignatureAlgorithm.instancesVerify = new ThreadLocal() {
            protected Object initialValue() {
                return new HashMap();
            }
        };
        SignatureAlgorithm.keysSigning = new ThreadLocal() {
            protected Object initialValue() {
                return new HashMap();
            }
        };
        SignatureAlgorithm.keysVerify = new ThreadLocal() {
            protected Object initialValue() {
                return new HashMap();
            }
        };
    }
}
