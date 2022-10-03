package org.bouncycastle.crypto.tls;

import org.bouncycastle.util.Integers;
import org.bouncycastle.util.BigIntegers;
import java.io.OutputStream;
import java.math.BigInteger;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Hashtable;

public class TlsSRPUtils
{
    public static final Integer EXT_SRP;
    
    public static void addSRPExtension(final Hashtable hashtable, final byte[] array) throws IOException {
        hashtable.put(TlsSRPUtils.EXT_SRP, createSRPExtension(array));
    }
    
    public static byte[] getSRPExtension(final Hashtable hashtable) throws IOException {
        final byte[] extensionData = TlsUtils.getExtensionData(hashtable, TlsSRPUtils.EXT_SRP);
        return (byte[])((extensionData == null) ? null : readSRPExtension(extensionData));
    }
    
    public static byte[] createSRPExtension(final byte[] array) throws IOException {
        if (array == null) {
            throw new TlsFatalAlert((short)80);
        }
        return TlsUtils.encodeOpaque8(array);
    }
    
    public static byte[] readSRPExtension(final byte[] array) throws IOException {
        if (array == null) {
            throw new IllegalArgumentException("'extensionData' cannot be null");
        }
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(array);
        final byte[] opaque8 = TlsUtils.readOpaque8(byteArrayInputStream);
        TlsProtocol.assertEmpty(byteArrayInputStream);
        return opaque8;
    }
    
    public static BigInteger readSRPParameter(final InputStream inputStream) throws IOException {
        return new BigInteger(1, TlsUtils.readOpaque16(inputStream));
    }
    
    public static void writeSRPParameter(final BigInteger bigInteger, final OutputStream outputStream) throws IOException {
        TlsUtils.writeOpaque16(BigIntegers.asUnsignedByteArray(bigInteger), outputStream);
    }
    
    public static boolean isSRPCipherSuite(final int n) {
        switch (n) {
            case 49178:
            case 49179:
            case 49180:
            case 49181:
            case 49182:
            case 49183:
            case 49184:
            case 49185:
            case 49186: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    static {
        EXT_SRP = Integers.valueOf(12);
    }
}
