package org.owasp.esapi;

import org.owasp.esapi.errors.EncryptionException;

public interface Randomizer
{
    String getRandomString(final int p0, final char[] p1);
    
    boolean getRandomBoolean();
    
    int getRandomInteger(final int p0, final int p1);
    
    long getRandomLong();
    
    String getRandomFilename(final String p0);
    
    float getRandomReal(final float p0, final float p1);
    
    String getRandomGUID() throws EncryptionException;
    
    byte[] getRandomBytes(final int p0);
}
