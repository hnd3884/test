package sun.security.util;

import java.io.IOException;
import java.util.Base64;
import java.nio.charset.StandardCharsets;

public class Pem
{
    public static byte[] decode(final String s) throws IOException {
        final byte[] bytes = s.replaceAll("\\s+", "").getBytes(StandardCharsets.ISO_8859_1);
        try {
            return Base64.getDecoder().decode(bytes);
        }
        catch (final IllegalArgumentException ex) {
            throw new IOException(ex);
        }
    }
}
