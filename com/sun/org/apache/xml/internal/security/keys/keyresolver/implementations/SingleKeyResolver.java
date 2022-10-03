package com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations;

import com.sun.org.slf4j.internal.LoggerFactory;
import java.security.cert.X509Certificate;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverException;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolver;
import org.w3c.dom.Element;
import javax.crypto.SecretKey;
import java.security.PrivateKey;
import java.security.PublicKey;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverSpi;

public class SingleKeyResolver extends KeyResolverSpi
{
    private static final Logger LOG;
    private String keyName;
    private PublicKey publicKey;
    private PrivateKey privateKey;
    private SecretKey secretKey;
    
    public SingleKeyResolver(final String keyName, final PublicKey publicKey) {
        this.keyName = keyName;
        this.publicKey = publicKey;
    }
    
    public SingleKeyResolver(final String keyName, final PrivateKey privateKey) {
        this.keyName = keyName;
        this.privateKey = privateKey;
    }
    
    public SingleKeyResolver(final String keyName, final SecretKey secretKey) {
        this.keyName = keyName;
        this.secretKey = secretKey;
    }
    
    @Override
    public boolean engineCanResolve(final Element element, final String s, final StorageResolver storageResolver) {
        return XMLUtils.elementIsInSignatureSpace(element, "KeyName");
    }
    
    @Override
    public PublicKey engineLookupAndResolvePublicKey(final Element element, final String s, final StorageResolver storageResolver) throws KeyResolverException {
        SingleKeyResolver.LOG.debug("Can I resolve {}?", element.getTagName());
        if (this.publicKey != null && XMLUtils.elementIsInSignatureSpace(element, "KeyName") && this.keyName.equals(element.getFirstChild().getNodeValue())) {
            return this.publicKey;
        }
        SingleKeyResolver.LOG.debug("I can't");
        return null;
    }
    
    @Override
    public X509Certificate engineLookupResolveX509Certificate(final Element element, final String s, final StorageResolver storageResolver) throws KeyResolverException {
        return null;
    }
    
    @Override
    public SecretKey engineResolveSecretKey(final Element element, final String s, final StorageResolver storageResolver) throws KeyResolverException {
        SingleKeyResolver.LOG.debug("Can I resolve {}?", element.getTagName());
        if (this.secretKey != null && XMLUtils.elementIsInSignatureSpace(element, "KeyName") && this.keyName.equals(element.getFirstChild().getNodeValue())) {
            return this.secretKey;
        }
        SingleKeyResolver.LOG.debug("I can't");
        return null;
    }
    
    @Override
    public PrivateKey engineLookupAndResolvePrivateKey(final Element element, final String s, final StorageResolver storageResolver) throws KeyResolverException {
        SingleKeyResolver.LOG.debug("Can I resolve {}?", element.getTagName());
        if (this.privateKey != null && XMLUtils.elementIsInSignatureSpace(element, "KeyName") && this.keyName.equals(element.getFirstChild().getNodeValue())) {
            return this.privateKey;
        }
        SingleKeyResolver.LOG.debug("I can't");
        return null;
    }
    
    static {
        LOG = LoggerFactory.getLogger(SingleKeyResolver.class);
    }
}
