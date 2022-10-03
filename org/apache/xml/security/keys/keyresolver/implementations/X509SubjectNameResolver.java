package org.apache.xml.security.keys.keyresolver.implementations;

import org.apache.commons.logging.LogFactory;
import javax.crypto.SecretKey;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.keys.content.x509.XMLX509SubjectName;
import org.apache.xml.security.utils.XMLUtils;
import org.apache.xml.security.keys.keyresolver.KeyResolverException;
import java.security.cert.X509Certificate;
import java.security.PublicKey;
import org.apache.xml.security.keys.storage.StorageResolver;
import org.w3c.dom.Element;
import org.apache.commons.logging.Log;
import org.apache.xml.security.keys.keyresolver.KeyResolverSpi;

public class X509SubjectNameResolver extends KeyResolverSpi
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
        if (X509SubjectNameResolver.log.isDebugEnabled()) {
            X509SubjectNameResolver.log.debug((Object)("Can I resolve " + element.getTagName() + "?"));
        }
        if (!XMLUtils.elementIsInSignatureSpace(element, "X509Data")) {
            X509SubjectNameResolver.log.debug((Object)"I can't");
            return null;
        }
        final Element[] selectDsNodes = XMLUtils.selectDsNodes(element.getFirstChild(), "X509SubjectName");
        if (selectDsNodes == null || selectDsNodes.length <= 0) {
            X509SubjectNameResolver.log.debug((Object)"I can't");
            return null;
        }
        try {
            if (storageResolver == null) {
                final KeyResolverException ex = new KeyResolverException("KeyResolver.needStorageResolver", new Object[] { "X509SubjectName" });
                X509SubjectNameResolver.log.info((Object)"", (Throwable)ex);
                throw ex;
            }
            final XMLX509SubjectName[] array = new XMLX509SubjectName[selectDsNodes.length];
            for (int i = 0; i < selectDsNodes.length; ++i) {
                array[i] = new XMLX509SubjectName(selectDsNodes[i], s);
            }
            while (storageResolver.hasNext()) {
                final X509Certificate next = storageResolver.next();
                final XMLX509SubjectName xmlx509SubjectName = new XMLX509SubjectName(element.getOwnerDocument(), next);
                X509SubjectNameResolver.log.debug((Object)("Found Certificate SN: " + xmlx509SubjectName.getSubjectName()));
                for (int j = 0; j < array.length; ++j) {
                    X509SubjectNameResolver.log.debug((Object)("Found Element SN:     " + array[j].getSubjectName()));
                    if (xmlx509SubjectName.equals(array[j])) {
                        X509SubjectNameResolver.log.debug((Object)"match !!! ");
                        return next;
                    }
                    X509SubjectNameResolver.log.debug((Object)"no match...");
                }
            }
            return null;
        }
        catch (final XMLSecurityException ex2) {
            X509SubjectNameResolver.log.debug((Object)"XMLSecurityException", (Throwable)ex2);
            throw new KeyResolverException("generic.EmptyMessage", ex2);
        }
    }
    
    public SecretKey engineLookupAndResolveSecretKey(final Element element, final String s, final StorageResolver storageResolver) {
        return null;
    }
    
    static {
        X509SubjectNameResolver.log = LogFactory.getLog(X509SubjectNameResolver.class.getName());
    }
}
