package org.apache.xml.security.keys.keyresolver.implementations;

import org.apache.commons.logging.LogFactory;
import javax.crypto.SecretKey;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.keys.content.x509.XMLX509SKI;
import org.apache.xml.security.utils.XMLUtils;
import org.apache.xml.security.keys.keyresolver.KeyResolverException;
import java.security.cert.X509Certificate;
import java.security.PublicKey;
import org.apache.xml.security.keys.storage.StorageResolver;
import org.w3c.dom.Element;
import org.apache.commons.logging.Log;
import org.apache.xml.security.keys.keyresolver.KeyResolverSpi;

public class X509SKIResolver extends KeyResolverSpi
{
    static Log log;
    
    public PublicKey engineLookupAndResolvePublicKey(final Element element, final String s, final StorageResolver storageResolver) throws KeyResolverException {
        final X509Certificate engineLookupResolveX509Certificate = this.engineLookupResolveX509Certificate(element, s, storageResolver);
        if (engineLookupResolveX509Certificate != null) {
            return engineLookupResolveX509Certificate.getPublicKey();
        }
        return null;
    }
    
    public X509Certificate engineLookupResolveX509Certificate(final Element element, final String s, final StorageResolver storageResolver) throws KeyResolverException {
        if (X509SKIResolver.log.isDebugEnabled()) {
            X509SKIResolver.log.debug((Object)("Can I resolve " + element.getTagName() + "?"));
        }
        if (!XMLUtils.elementIsInSignatureSpace(element, "X509Data")) {
            X509SKIResolver.log.debug((Object)"I can't");
            return null;
        }
        final Element[] selectDsNodes = XMLUtils.selectDsNodes(element.getFirstChild(), "X509SKI");
        if (selectDsNodes == null || selectDsNodes.length <= 0) {
            X509SKIResolver.log.debug((Object)"I can't");
            return null;
        }
        try {
            if (storageResolver == null) {
                final KeyResolverException ex = new KeyResolverException("KeyResolver.needStorageResolver", new Object[] { "X509SKI" });
                X509SKIResolver.log.info((Object)"", (Throwable)ex);
                throw ex;
            }
            final XMLX509SKI[] array = new XMLX509SKI[selectDsNodes.length];
            for (int i = 0; i < selectDsNodes.length; ++i) {
                array[i] = new XMLX509SKI(selectDsNodes[i], s);
            }
            while (storageResolver.hasNext()) {
                final X509Certificate next = storageResolver.next();
                final XMLX509SKI xmlx509SKI = new XMLX509SKI(element.getOwnerDocument(), next);
                for (int j = 0; j < array.length; ++j) {
                    if (xmlx509SKI.equals(array[j])) {
                        X509SKIResolver.log.debug((Object)("Return PublicKey from " + next.getSubjectDN().getName()));
                        return next;
                    }
                }
            }
        }
        catch (final XMLSecurityException ex2) {
            throw new KeyResolverException("empty", ex2);
        }
        return null;
    }
    
    public SecretKey engineLookupAndResolveSecretKey(final Element element, final String s, final StorageResolver storageResolver) {
        return null;
    }
    
    static {
        X509SKIResolver.log = LogFactory.getLog(X509SKIResolver.class.getName());
    }
}
