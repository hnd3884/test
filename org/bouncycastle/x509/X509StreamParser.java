package org.bouncycastle.x509;

import java.util.Collection;
import org.bouncycastle.x509.util.StreamParsingException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.NoSuchProviderException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import org.bouncycastle.x509.util.StreamParser;

public class X509StreamParser implements StreamParser
{
    private Provider _provider;
    private X509StreamParserSpi _spi;
    
    public static X509StreamParser getInstance(final String s) throws NoSuchParserException {
        try {
            return createParser(X509Util.getImplementation("X509StreamParser", s));
        }
        catch (final NoSuchAlgorithmException ex) {
            throw new NoSuchParserException(ex.getMessage());
        }
    }
    
    public static X509StreamParser getInstance(final String s, final String s2) throws NoSuchParserException, NoSuchProviderException {
        return getInstance(s, X509Util.getProvider(s2));
    }
    
    public static X509StreamParser getInstance(final String s, final Provider provider) throws NoSuchParserException {
        try {
            return createParser(X509Util.getImplementation("X509StreamParser", s, provider));
        }
        catch (final NoSuchAlgorithmException ex) {
            throw new NoSuchParserException(ex.getMessage());
        }
    }
    
    private static X509StreamParser createParser(final X509Util.Implementation implementation) {
        return new X509StreamParser(implementation.getProvider(), (X509StreamParserSpi)implementation.getEngine());
    }
    
    private X509StreamParser(final Provider provider, final X509StreamParserSpi spi) {
        this._provider = provider;
        this._spi = spi;
    }
    
    public Provider getProvider() {
        return this._provider;
    }
    
    public void init(final InputStream inputStream) {
        this._spi.engineInit(inputStream);
    }
    
    public void init(final byte[] array) {
        this._spi.engineInit(new ByteArrayInputStream(array));
    }
    
    public Object read() throws StreamParsingException {
        return this._spi.engineRead();
    }
    
    public Collection readAll() throws StreamParsingException {
        return this._spi.engineReadAll();
    }
}
