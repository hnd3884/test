package org.apache.xml.security.keys.keyresolver;

import org.apache.commons.logging.LogFactory;
import javax.crypto.SecretKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import org.w3c.dom.Element;
import java.util.ArrayList;
import org.apache.xml.security.keys.storage.StorageResolver;
import java.util.List;
import org.apache.commons.logging.Log;

public class KeyResolver
{
    static Log log;
    static boolean _alreadyInitialized;
    static List _resolverVector;
    protected KeyResolverSpi _resolverSpi;
    protected StorageResolver _storage;
    
    private KeyResolver(final String s) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        this._resolverSpi = null;
        this._storage = null;
        (this._resolverSpi = (KeyResolverSpi)Class.forName(s).newInstance()).setGlobalResolver(true);
    }
    
    public static int length() {
        return KeyResolver._resolverVector.size();
    }
    
    public static KeyResolverSpi item(final int n) throws KeyResolverException {
        final KeyResolver keyResolver = KeyResolver._resolverVector.get(n);
        if (keyResolver == null) {
            throw new KeyResolverException("utils.resolver.noClass");
        }
        return keyResolver._resolverSpi;
    }
    
    public static void hit(final int n) {
        if (n != 0) {
            final List resolverVector = (List)((ArrayList)KeyResolver._resolverVector).clone();
            resolverVector.add(0, resolverVector.remove(n));
            KeyResolver._resolverVector = resolverVector;
        }
    }
    
    public static final X509Certificate getX509Certificate(final Element element, final String s, final StorageResolver storageResolver) throws KeyResolverException {
        for (int i = 0; i < KeyResolver._resolverVector.size(); ++i) {
            final KeyResolver keyResolver = KeyResolver._resolverVector.get(i);
            if (keyResolver == null) {
                throw new KeyResolverException("utils.resolver.noClass", new Object[] { (element != null && element.getNodeType() == 1) ? element.getTagName() : "null" });
            }
            if (KeyResolver.log.isDebugEnabled()) {
                KeyResolver.log.debug((Object)("check resolvability by class " + keyResolver.getClass()));
            }
            final X509Certificate resolveX509Certificate = keyResolver.resolveX509Certificate(element, s, storageResolver);
            if (resolveX509Certificate != null) {
                return resolveX509Certificate;
            }
        }
        throw new KeyResolverException("utils.resolver.noClass", new Object[] { (element != null && element.getNodeType() == 1) ? element.getTagName() : "null" });
    }
    
    public static final PublicKey getPublicKey(final Element element, final String s, final StorageResolver storageResolver) throws KeyResolverException {
        for (int i = 0; i < KeyResolver._resolverVector.size(); ++i) {
            final KeyResolver keyResolver = KeyResolver._resolverVector.get(i);
            if (keyResolver == null) {
                throw new KeyResolverException("utils.resolver.noClass", new Object[] { (element != null && element.getNodeType() == 1) ? element.getTagName() : "null" });
            }
            if (KeyResolver.log.isDebugEnabled()) {
                KeyResolver.log.debug((Object)("check resolvability by class " + keyResolver.getClass()));
            }
            final PublicKey resolvePublicKey = keyResolver.resolvePublicKey(element, s, storageResolver);
            if (resolvePublicKey != null) {
                if (i != 0) {
                    final List resolverVector = (List)((ArrayList)KeyResolver._resolverVector).clone();
                    resolverVector.add(0, resolverVector.remove(i));
                    KeyResolver._resolverVector = resolverVector;
                }
                return resolvePublicKey;
            }
        }
        throw new KeyResolverException("utils.resolver.noClass", new Object[] { (element != null && element.getNodeType() == 1) ? element.getTagName() : "null" });
    }
    
    public static void init() {
        if (!KeyResolver._alreadyInitialized) {
            KeyResolver._resolverVector = new ArrayList(10);
            KeyResolver._alreadyInitialized = true;
        }
    }
    
    public static void register(final String s) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        KeyResolver._resolverVector.add(new KeyResolver(s));
    }
    
    public static void registerAtStart(final String s) {
        KeyResolver._resolverVector.add(0, s);
    }
    
    public PublicKey resolvePublicKey(final Element element, final String s, final StorageResolver storageResolver) throws KeyResolverException {
        return this._resolverSpi.engineLookupAndResolvePublicKey(element, s, storageResolver);
    }
    
    public X509Certificate resolveX509Certificate(final Element element, final String s, final StorageResolver storageResolver) throws KeyResolverException {
        return this._resolverSpi.engineLookupResolveX509Certificate(element, s, storageResolver);
    }
    
    public SecretKey resolveSecretKey(final Element element, final String s, final StorageResolver storageResolver) throws KeyResolverException {
        return this._resolverSpi.engineLookupAndResolveSecretKey(element, s, storageResolver);
    }
    
    public void setProperty(final String s, final String s2) {
        this._resolverSpi.engineSetProperty(s, s2);
    }
    
    public String getProperty(final String s) {
        return this._resolverSpi.engineGetProperty(s);
    }
    
    public boolean understandsProperty(final String s) {
        return this._resolverSpi.understandsProperty(s);
    }
    
    public String resolverClassName() {
        return this._resolverSpi.getClass().getName();
    }
    
    static {
        KeyResolver.log = LogFactory.getLog(KeyResolver.class.getName());
        KeyResolver._alreadyInitialized = false;
        KeyResolver._resolverVector = null;
    }
}
