package org.owasp.esapi;

import org.owasp.esapi.errors.IntegrityException;
import javax.crypto.SecretKey;
import org.owasp.esapi.crypto.CipherText;
import org.owasp.esapi.crypto.PlainText;
import org.owasp.esapi.errors.EncryptionException;

public interface Encryptor
{
    String hash(final String p0, final String p1) throws EncryptionException;
    
    String hash(final String p0, final String p1, final int p2) throws EncryptionException;
    
    CipherText encrypt(final PlainText p0) throws EncryptionException;
    
    CipherText encrypt(final SecretKey p0, final PlainText p1) throws EncryptionException;
    
    PlainText decrypt(final CipherText p0) throws EncryptionException;
    
    PlainText decrypt(final SecretKey p0, final CipherText p1) throws EncryptionException;
    
    String sign(final String p0) throws EncryptionException;
    
    boolean verifySignature(final String p0, final String p1);
    
    String seal(final String p0, final long p1) throws IntegrityException;
    
    String unseal(final String p0) throws EncryptionException;
    
    boolean verifySeal(final String p0);
    
    long getRelativeTimeStamp(final long p0);
    
    long getTimeStamp();
}
