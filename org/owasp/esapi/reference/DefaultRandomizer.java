package org.owasp.esapi.reference;

import java.util.UUID;
import org.owasp.esapi.EncoderConstants;
import java.security.NoSuchAlgorithmException;
import org.owasp.esapi.errors.EncryptionException;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Logger;
import java.security.SecureRandom;
import org.owasp.esapi.Randomizer;

public class DefaultRandomizer implements Randomizer
{
    private static volatile Randomizer singletonInstance;
    private SecureRandom secureRandom;
    private final Logger logger;
    
    public static Randomizer getInstance() {
        if (DefaultRandomizer.singletonInstance == null) {
            synchronized (DefaultRandomizer.class) {
                if (DefaultRandomizer.singletonInstance == null) {
                    DefaultRandomizer.singletonInstance = new DefaultRandomizer();
                }
            }
        }
        return DefaultRandomizer.singletonInstance;
    }
    
    private DefaultRandomizer() {
        this.secureRandom = null;
        this.logger = ESAPI.getLogger("Randomizer");
        final String algorithm = ESAPI.securityConfiguration().getRandomAlgorithm();
        try {
            this.secureRandom = SecureRandom.getInstance(algorithm);
        }
        catch (final NoSuchAlgorithmException e) {
            final EncryptionException ex = new EncryptionException("Error creating randomizer", "Can't find random algorithm " + algorithm, e);
        }
    }
    
    @Override
    public String getRandomString(final int length, final char[] characterSet) {
        final StringBuilder sb = new StringBuilder();
        for (int loop = 0; loop < length; ++loop) {
            final int index = this.secureRandom.nextInt(characterSet.length);
            sb.append(characterSet[index]);
        }
        final String nonce = sb.toString();
        return nonce;
    }
    
    @Override
    public boolean getRandomBoolean() {
        return this.secureRandom.nextBoolean();
    }
    
    @Override
    public int getRandomInteger(final int min, final int max) {
        return this.secureRandom.nextInt(max - min) + min;
    }
    
    @Override
    public long getRandomLong() {
        return this.secureRandom.nextLong();
    }
    
    @Override
    public float getRandomReal(final float min, final float max) {
        final float factor = max - min;
        return this.secureRandom.nextFloat() * factor + min;
    }
    
    @Override
    public String getRandomFilename(final String extension) {
        final String fn = this.getRandomString(12, EncoderConstants.CHAR_ALPHANUMERICS) + "." + extension;
        this.logger.debug(Logger.SECURITY_SUCCESS, "Generated new random filename: " + fn);
        return fn;
    }
    
    @Override
    public String getRandomGUID() throws EncryptionException {
        return UUID.randomUUID().toString();
    }
    
    @Override
    public byte[] getRandomBytes(final int n) {
        final byte[] result = new byte[n];
        this.secureRandom.nextBytes(result);
        return result;
    }
}
