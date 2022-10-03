package org.bouncycastle.cert.dane;

import java.io.OutputStream;
import org.bouncycastle.util.encoders.Hex;
import java.io.IOException;
import org.bouncycastle.util.Strings;
import org.bouncycastle.operator.DigestCalculator;

public class DANEEntrySelectorFactory
{
    private final DigestCalculator digestCalculator;
    
    public DANEEntrySelectorFactory(final DigestCalculator digestCalculator) {
        this.digestCalculator = digestCalculator;
    }
    
    public DANEEntrySelector createSelector(final String s) throws DANEException {
        final byte[] utf8ByteArray = Strings.toUTF8ByteArray(s.substring(0, s.indexOf(64)));
        try {
            final OutputStream outputStream = this.digestCalculator.getOutputStream();
            outputStream.write(utf8ByteArray);
            outputStream.close();
        }
        catch (final IOException ex) {
            throw new DANEException("Unable to calculate digest string: " + ex.getMessage(), ex);
        }
        return new DANEEntrySelector(Strings.fromByteArray(Hex.encode(this.digestCalculator.getDigest())) + "._smimecert." + s.substring(s.indexOf(64) + 1));
    }
}
