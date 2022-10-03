package org.bouncycastle.crypto.tls;

import org.bouncycastle.util.Integers;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Hashtable;

public class TlsSRTPUtils
{
    public static final Integer EXT_use_srtp;
    
    public static void addUseSRTPExtension(final Hashtable hashtable, final UseSRTPData useSRTPData) throws IOException {
        hashtable.put(TlsSRTPUtils.EXT_use_srtp, createUseSRTPExtension(useSRTPData));
    }
    
    public static UseSRTPData getUseSRTPExtension(final Hashtable hashtable) throws IOException {
        final byte[] extensionData = TlsUtils.getExtensionData(hashtable, TlsSRTPUtils.EXT_use_srtp);
        return (extensionData == null) ? null : readUseSRTPExtension(extensionData);
    }
    
    public static byte[] createUseSRTPExtension(final UseSRTPData useSRTPData) throws IOException {
        if (useSRTPData == null) {
            throw new IllegalArgumentException("'useSRTPData' cannot be null");
        }
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        TlsUtils.writeUint16ArrayWithUint16Length(useSRTPData.getProtectionProfiles(), byteArrayOutputStream);
        TlsUtils.writeOpaque8(useSRTPData.getMki(), byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
    
    public static UseSRTPData readUseSRTPExtension(final byte[] array) throws IOException {
        if (array == null) {
            throw new IllegalArgumentException("'extensionData' cannot be null");
        }
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(array);
        final int uint16 = TlsUtils.readUint16(byteArrayInputStream);
        if (uint16 < 2 || (uint16 & 0x1) != 0x0) {
            throw new TlsFatalAlert((short)50);
        }
        final int[] uint16Array = TlsUtils.readUint16Array(uint16 / 2, byteArrayInputStream);
        final byte[] opaque8 = TlsUtils.readOpaque8(byteArrayInputStream);
        TlsProtocol.assertEmpty(byteArrayInputStream);
        return new UseSRTPData(uint16Array, opaque8);
    }
    
    static {
        EXT_use_srtp = Integers.valueOf(14);
    }
}
