package org.apache.xml.security.encryption;

import org.apache.commons.logging.LogFactory;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.w3c.dom.Attr;
import org.apache.xml.security.exceptions.Base64DecodingException;
import org.apache.xml.security.utils.Base64;
import org.apache.xml.security.c14n.CanonicalizationException;
import java.io.IOException;
import org.apache.xml.security.transforms.TransformationException;
import org.apache.xml.security.utils.resolver.ResourceResolverException;
import org.apache.xml.security.utils.resolver.ResourceResolver;
import org.apache.commons.logging.Log;

public class XMLCipherInput
{
    private static Log logger;
    private CipherData _cipherData;
    private int _mode;
    
    public XMLCipherInput(final CipherData cipherData) throws XMLEncryptionException {
        this._cipherData = cipherData;
        this._mode = 2;
        if (this._cipherData == null) {
            throw new XMLEncryptionException("CipherData is null");
        }
    }
    
    public XMLCipherInput(final EncryptedType encryptedType) throws XMLEncryptionException {
        this._cipherData = ((encryptedType == null) ? null : encryptedType.getCipherData());
        this._mode = 2;
        if (this._cipherData == null) {
            throw new XMLEncryptionException("CipherData is null");
        }
    }
    
    public byte[] getBytes() throws XMLEncryptionException {
        if (this._mode == 2) {
            return this.getDecryptBytes();
        }
        return null;
    }
    
    private byte[] getDecryptBytes() throws XMLEncryptionException {
        if (this._cipherData.getDataType() == 2) {
            XMLCipherInput.logger.debug((Object)"Found a reference type CipherData");
            final CipherReference cipherReference = this._cipherData.getCipherReference();
            final Attr uriAsAttr = cipherReference.getURIAsAttr();
            XMLSignatureInput xmlSignatureInput;
            try {
                xmlSignatureInput = ResourceResolver.getInstance(uriAsAttr, null).resolve(uriAsAttr, null);
            }
            catch (final ResourceResolverException ex) {
                throw new XMLEncryptionException("empty", ex);
            }
            if (xmlSignatureInput != null) {
                XMLCipherInput.logger.debug((Object)("Managed to resolve URI \"" + cipherReference.getURI() + "\""));
            }
            else {
                XMLCipherInput.logger.debug((Object)("Failed to resolve URI \"" + cipherReference.getURI() + "\""));
            }
            final Transforms transforms = cipherReference.getTransforms();
            if (transforms != null) {
                XMLCipherInput.logger.debug((Object)"Have transforms in cipher reference");
                try {
                    xmlSignatureInput = transforms.getDSTransforms().performTransforms(xmlSignatureInput);
                }
                catch (final TransformationException ex2) {
                    throw new XMLEncryptionException("empty", ex2);
                }
            }
            try {
                return xmlSignatureInput.getBytes();
            }
            catch (final IOException ex3) {
                throw new XMLEncryptionException("empty", ex3);
            }
            catch (final CanonicalizationException ex4) {
                throw new XMLEncryptionException("empty", ex4);
            }
        }
        if (this._cipherData.getDataType() == 1) {
            final String value = this._cipherData.getCipherValue().getValue();
            XMLCipherInput.logger.debug((Object)("Encrypted octets:\n" + value));
            byte[] decode;
            try {
                decode = Base64.decode(value);
            }
            catch (final Base64DecodingException ex5) {
                throw new XMLEncryptionException("empty", ex5);
            }
            return decode;
        }
        throw new XMLEncryptionException("CipherData.getDataType() returned unexpected value");
    }
    
    static {
        XMLCipherInput.logger = LogFactory.getLog(XMLCipher.class.getName());
    }
}
