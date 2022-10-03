package com.sun.org.apache.xml.internal.security.keys.keyresolver;

import java.util.concurrent.CopyOnWriteArrayList;
import com.sun.org.slf4j.internal.LoggerFactory;
import javax.crypto.SecretKey;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations.ECKeyValueResolver;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations.X509DigestResolver;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations.KeyInfoReferenceResolver;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations.DEREncodedKeyValueResolver;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations.X509IssuerSerialResolver;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations.X509SubjectNameResolver;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations.RetrievalMethodResolver;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations.X509SKIResolver;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations.X509CertificateResolver;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations.DSAKeyValueResolver;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations.RSAKeyValueResolver;
import java.util.Collection;
import java.util.ArrayList;
import com.sun.org.apache.xml.internal.security.utils.ClassLoaderUtils;
import com.sun.org.apache.xml.internal.security.utils.JavaUtils;
import java.security.PublicKey;
import java.util.Iterator;
import java.security.cert.X509Certificate;
import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolver;
import org.w3c.dom.Element;
import java.util.List;
import com.sun.org.slf4j.internal.Logger;

public class KeyResolver
{
    private static final Logger LOG;
    private static List<KeyResolver> resolverVector;
    private final KeyResolverSpi resolverSpi;
    
    private KeyResolver(final KeyResolverSpi resolverSpi) {
        this.resolverSpi = resolverSpi;
    }
    
    public static int length() {
        return KeyResolver.resolverVector.size();
    }
    
    public static final X509Certificate getX509Certificate(final Element element, final String s, final StorageResolver storageResolver) throws KeyResolverException {
        for (final KeyResolver keyResolver : KeyResolver.resolverVector) {
            if (keyResolver == null) {
                throw new KeyResolverException("utils.resolver.noClass", new Object[] { (element != null && element.getNodeType() == 1) ? element.getTagName() : "null" });
            }
            KeyResolver.LOG.debug("check resolvability by class {}", keyResolver.getClass());
            final X509Certificate resolveX509Certificate = keyResolver.resolveX509Certificate(element, s, storageResolver);
            if (resolveX509Certificate != null) {
                return resolveX509Certificate;
            }
        }
        throw new KeyResolverException("utils.resolver.noClass", new Object[] { (element != null && element.getNodeType() == 1) ? element.getTagName() : "null" });
    }
    
    public static final PublicKey getPublicKey(final Element element, final String s, final StorageResolver storageResolver) throws KeyResolverException {
        for (final KeyResolver keyResolver : KeyResolver.resolverVector) {
            if (keyResolver == null) {
                throw new KeyResolverException("utils.resolver.noClass", new Object[] { (element != null && element.getNodeType() == 1) ? element.getTagName() : "null" });
            }
            KeyResolver.LOG.debug("check resolvability by class {}", keyResolver.getClass());
            final PublicKey resolvePublicKey = keyResolver.resolvePublicKey(element, s, storageResolver);
            if (resolvePublicKey != null) {
                return resolvePublicKey;
            }
        }
        throw new KeyResolverException("utils.resolver.noClass", new Object[] { (element != null && element.getNodeType() == 1) ? element.getTagName() : "null" });
    }
    
    public static void register(final String s, final boolean globalResolver) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        JavaUtils.checkRegisterPermission();
        final KeyResolverSpi keyResolverSpi = (KeyResolverSpi)ClassLoaderUtils.loadClass(s, KeyResolver.class).newInstance();
        keyResolverSpi.setGlobalResolver(globalResolver);
        register(keyResolverSpi, false);
    }
    
    public static void registerAtStart(final String s, final boolean globalResolver) {
        JavaUtils.checkRegisterPermission();
        Throwable t = null;
        try {
            final KeyResolverSpi keyResolverSpi = (KeyResolverSpi)ClassLoaderUtils.loadClass(s, KeyResolver.class).newInstance();
            keyResolverSpi.setGlobalResolver(globalResolver);
            register(keyResolverSpi, true);
        }
        catch (final ClassNotFoundException ex) {
            t = ex;
        }
        catch (final IllegalAccessException ex2) {
            t = ex2;
        }
        catch (final InstantiationException ex3) {
            t = ex3;
        }
        if (t != null) {
            throw (IllegalArgumentException)new IllegalArgumentException("Invalid KeyResolver class name").initCause(t);
        }
    }
    
    public static void register(final KeyResolverSpi keyResolverSpi, final boolean b) {
        JavaUtils.checkRegisterPermission();
        final KeyResolver keyResolver = new KeyResolver(keyResolverSpi);
        if (b) {
            KeyResolver.resolverVector.add(0, keyResolver);
        }
        else {
            KeyResolver.resolverVector.add(keyResolver);
        }
    }
    
    public static void registerClassNames(final List<String> list) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        JavaUtils.checkRegisterPermission();
        final ArrayList list2 = new ArrayList(list.size());
        final Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            final KeyResolverSpi keyResolverSpi = (KeyResolverSpi)ClassLoaderUtils.loadClass((String)iterator.next(), KeyResolver.class).newInstance();
            keyResolverSpi.setGlobalResolver(false);
            list2.add(new KeyResolver(keyResolverSpi));
        }
        KeyResolver.resolverVector.addAll(list2);
    }
    
    public static void registerDefaultResolvers() {
        final ArrayList list = new ArrayList();
        list.add(new KeyResolver(new RSAKeyValueResolver()));
        list.add(new KeyResolver(new DSAKeyValueResolver()));
        list.add(new KeyResolver(new X509CertificateResolver()));
        list.add(new KeyResolver(new X509SKIResolver()));
        list.add(new KeyResolver(new RetrievalMethodResolver()));
        list.add(new KeyResolver(new X509SubjectNameResolver()));
        list.add(new KeyResolver(new X509IssuerSerialResolver()));
        list.add(new KeyResolver(new DEREncodedKeyValueResolver()));
        list.add(new KeyResolver(new KeyInfoReferenceResolver()));
        list.add(new KeyResolver(new X509DigestResolver()));
        list.add(new KeyResolver(new ECKeyValueResolver()));
        KeyResolver.resolverVector.addAll(list);
    }
    
    public PublicKey resolvePublicKey(final Element element, final String s, final StorageResolver storageResolver) throws KeyResolverException {
        return this.resolverSpi.engineLookupAndResolvePublicKey(element, s, storageResolver);
    }
    
    public X509Certificate resolveX509Certificate(final Element element, final String s, final StorageResolver storageResolver) throws KeyResolverException {
        return this.resolverSpi.engineLookupResolveX509Certificate(element, s, storageResolver);
    }
    
    public SecretKey resolveSecretKey(final Element element, final String s, final StorageResolver storageResolver) throws KeyResolverException {
        return this.resolverSpi.engineLookupAndResolveSecretKey(element, s, storageResolver);
    }
    
    public void setProperty(final String s, final String s2) {
        this.resolverSpi.engineSetProperty(s, s2);
    }
    
    public String getProperty(final String s) {
        return this.resolverSpi.engineGetProperty(s);
    }
    
    public boolean understandsProperty(final String s) {
        return this.resolverSpi.understandsProperty(s);
    }
    
    public String resolverClassName() {
        return this.resolverSpi.getClass().getName();
    }
    
    public static Iterator<KeyResolverSpi> iterator() {
        return new ResolverIterator(KeyResolver.resolverVector);
    }
    
    static {
        LOG = LoggerFactory.getLogger(KeyResolver.class);
        KeyResolver.resolverVector = new CopyOnWriteArrayList<KeyResolver>();
    }
    
    static class ResolverIterator implements Iterator<KeyResolverSpi>
    {
        List<KeyResolver> res;
        Iterator<KeyResolver> it;
        
        public ResolverIterator(final List<KeyResolver> res) {
            this.res = res;
            this.it = this.res.iterator();
        }
        
        @Override
        public boolean hasNext() {
            return this.it.hasNext();
        }
        
        @Override
        public KeyResolverSpi next() {
            final KeyResolver keyResolver = this.it.next();
            if (keyResolver == null) {
                throw new RuntimeException("utils.resolver.noClass");
            }
            return keyResolver.resolverSpi;
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException("Can't remove resolvers using the iterator");
        }
    }
}
