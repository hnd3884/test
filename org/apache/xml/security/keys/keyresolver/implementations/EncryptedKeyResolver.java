package org.apache.xml.security.keys.keyresolver.implementations;

import org.apache.commons.logging.LogFactory;
import org.apache.xml.security.encryption.XMLCipher;
import org.apache.xml.security.utils.XMLUtils;
import javax.crypto.SecretKey;
import java.security.cert.X509Certificate;
import java.security.PublicKey;
import org.apache.xml.security.keys.storage.StorageResolver;
import org.w3c.dom.Element;
import java.security.Key;
import org.apache.commons.logging.Log;
import org.apache.xml.security.keys.keyresolver.KeyResolverSpi;

public class EncryptedKeyResolver extends KeyResolverSpi
{
    static Log log;
    Key _kek;
    String _algorithm;
    
    public EncryptedKeyResolver(final String algorithm) {
        this._kek = null;
        this._algorithm = algorithm;
    }
    
    public EncryptedKeyResolver(final String algorithm, final Key kek) {
        this._algorithm = algorithm;
        this._kek = kek;
    }
    
    public PublicKey engineLookupAndResolvePublicKey(final Element element, final String s, final StorageResolver storageResolver) {
        return null;
    }
    
    public X509Certificate engineLookupResolveX509Certificate(final Element element, final String s, final StorageResolver storageResolver) {
        return null;
    }
    
    public SecretKey engineLookupAndResolveSecretKey(final Element element, final String s, final StorageResolver storageResolver) {
        SecretKey secretKey = null;
        if (EncryptedKeyResolver.log.isDebugEnabled()) {
            EncryptedKeyResolver.log.debug((Object)("EncryptedKeyResolver - Can I resolve " + element.getTagName()));
        }
        if (element == null) {
            return null;
        }
        if (XMLUtils.elementIsInEncryptionSpace(element, "EncryptedKey")) {
            EncryptedKeyResolver.log.debug((Object)"Passed an Encrypted Key");
            try {
                final XMLCipher instance = XMLCipher.getInstance();
                instance.init(4, this._kek);
                secretKey = (SecretKey)instance.decryptKey(instance.loadEncryptedKey(element), this._algorithm);
            }
            catch (final Exception ex) {}
        }
        return secretKey;
    }
    
    static {
        EncryptedKeyResolver.log = LogFactory.getLog(RSAKeyValueResolver.class.getName());
    }
}
