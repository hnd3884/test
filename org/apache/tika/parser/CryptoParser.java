package org.apache.tika.parser;

import org.xml.sax.SAXException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import org.apache.tika.exception.TikaException;
import javax.crypto.CipherInputStream;
import java.security.SecureRandom;
import java.security.AlgorithmParameters;
import org.apache.tika.exception.EncryptedDocumentException;
import java.security.Key;
import javax.crypto.Cipher;
import org.apache.tika.metadata.Metadata;
import org.xml.sax.ContentHandler;
import java.io.InputStream;
import org.apache.tika.mime.MediaType;
import java.util.Set;
import java.security.Provider;

public abstract class CryptoParser extends DelegatingParser
{
    private static final long serialVersionUID = -3507995752666557731L;
    private final String transformation;
    private final Provider provider;
    private final Set<MediaType> types;
    
    public CryptoParser(final String transformation, final Provider provider, final Set<MediaType> types) {
        this.transformation = transformation;
        this.provider = provider;
        this.types = types;
    }
    
    public CryptoParser(final String transformation, final Set<MediaType> types) {
        this(transformation, null, types);
    }
    
    @Override
    public Set<MediaType> getSupportedTypes(final ParseContext context) {
        return this.types;
    }
    
    @Override
    public void parse(final InputStream stream, final ContentHandler handler, final Metadata metadata, final ParseContext context) throws IOException, SAXException, TikaException {
        try {
            Cipher cipher;
            if (this.provider != null) {
                cipher = Cipher.getInstance(this.transformation, this.provider);
            }
            else {
                cipher = Cipher.getInstance(this.transformation);
            }
            final Key key = context.get(Key.class);
            if (key == null) {
                throw new EncryptedDocumentException("No decryption key provided");
            }
            final AlgorithmParameters params = context.get(AlgorithmParameters.class);
            final SecureRandom random = context.get(SecureRandom.class);
            if (params != null && random != null) {
                cipher.init(2, key, params, random);
            }
            else if (params != null) {
                cipher.init(2, key, params);
            }
            else if (random != null) {
                cipher.init(2, key, random);
            }
            else {
                cipher.init(2, key);
            }
            super.parse(new CipherInputStream(stream, cipher), handler, metadata, context);
        }
        catch (final GeneralSecurityException e) {
            throw new TikaException("Unable to decrypt document stream", e);
        }
    }
}
