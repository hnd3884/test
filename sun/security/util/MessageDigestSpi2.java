package sun.security.util;

import java.security.InvalidKeyException;
import javax.crypto.SecretKey;

public interface MessageDigestSpi2
{
    void engineUpdate(final SecretKey p0) throws InvalidKeyException;
}
