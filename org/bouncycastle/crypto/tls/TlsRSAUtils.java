package org.bouncycastle.crypto.tls;

import org.bouncycastle.util.Arrays;
import java.io.IOException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.encodings.PKCS1Encoding;
import org.bouncycastle.crypto.engines.RSABlindedEngine;
import java.io.OutputStream;
import org.bouncycastle.crypto.params.RSAKeyParameters;

public class TlsRSAUtils
{
    public static byte[] generateEncryptedPreMasterSecret(final TlsContext tlsContext, final RSAKeyParameters rsaKeyParameters, final OutputStream outputStream) throws IOException {
        final byte[] array = new byte[48];
        tlsContext.getSecureRandom().nextBytes(array);
        TlsUtils.writeVersion(tlsContext.getClientVersion(), array, 0);
        final PKCS1Encoding pkcs1Encoding = new PKCS1Encoding(new RSABlindedEngine());
        pkcs1Encoding.init(true, new ParametersWithRandom(rsaKeyParameters, tlsContext.getSecureRandom()));
        try {
            final byte[] processBlock = pkcs1Encoding.processBlock(array, 0, array.length);
            if (TlsUtils.isSSL(tlsContext)) {
                outputStream.write(processBlock);
            }
            else {
                TlsUtils.writeOpaque16(processBlock, outputStream);
            }
        }
        catch (final InvalidCipherTextException ex) {
            throw new TlsFatalAlert((short)80, ex);
        }
        return array;
    }
    
    public static byte[] safeDecryptPreMasterSecret(final TlsContext tlsContext, final RSAKeyParameters rsaKeyParameters, final byte[] array) {
        final ProtocolVersion clientVersion = tlsContext.getClientVersion();
        final boolean b = false;
        final byte[] array2 = new byte[48];
        tlsContext.getSecureRandom().nextBytes(array2);
        byte[] array3 = Arrays.clone(array2);
        try {
            final PKCS1Encoding pkcs1Encoding = new PKCS1Encoding(new RSABlindedEngine(), array2);
            pkcs1Encoding.init(false, new ParametersWithRandom(rsaKeyParameters, tlsContext.getSecureRandom()));
            array3 = pkcs1Encoding.processBlock(array, 0, array.length);
        }
        catch (final Exception ex) {}
        if (!b || !clientVersion.isEqualOrEarlierVersionOf(ProtocolVersion.TLSv10)) {
            final int n = (clientVersion.getMajorVersion() ^ (array3[0] & 0xFF)) | (clientVersion.getMinorVersion() ^ (array3[1] & 0xFF));
            final int n2 = n | n >> 1;
            final int n3 = n2 | n2 >> 2;
            final int n4 = ~(((n3 | n3 >> 4) & 0x1) - 1);
            for (int i = 0; i < 48; ++i) {
                array3[i] = (byte)((array3[i] & ~n4) | (array2[i] & n4));
            }
        }
        return array3;
    }
}
