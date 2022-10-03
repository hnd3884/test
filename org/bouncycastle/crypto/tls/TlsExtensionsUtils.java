package org.bouncycastle.crypto.tls;

import org.bouncycastle.util.Integers;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Hashtable;

public class TlsExtensionsUtils
{
    public static final Integer EXT_encrypt_then_mac;
    public static final Integer EXT_extended_master_secret;
    public static final Integer EXT_heartbeat;
    public static final Integer EXT_max_fragment_length;
    public static final Integer EXT_padding;
    public static final Integer EXT_server_name;
    public static final Integer EXT_status_request;
    public static final Integer EXT_truncated_hmac;
    
    public static Hashtable ensureExtensionsInitialised(final Hashtable hashtable) {
        return (hashtable == null) ? new Hashtable() : hashtable;
    }
    
    public static void addEncryptThenMACExtension(final Hashtable hashtable) {
        hashtable.put(TlsExtensionsUtils.EXT_encrypt_then_mac, createEncryptThenMACExtension());
    }
    
    public static void addExtendedMasterSecretExtension(final Hashtable hashtable) {
        hashtable.put(TlsExtensionsUtils.EXT_extended_master_secret, createExtendedMasterSecretExtension());
    }
    
    public static void addHeartbeatExtension(final Hashtable hashtable, final HeartbeatExtension heartbeatExtension) throws IOException {
        hashtable.put(TlsExtensionsUtils.EXT_heartbeat, createHeartbeatExtension(heartbeatExtension));
    }
    
    public static void addMaxFragmentLengthExtension(final Hashtable hashtable, final short n) throws IOException {
        hashtable.put(TlsExtensionsUtils.EXT_max_fragment_length, createMaxFragmentLengthExtension(n));
    }
    
    public static void addPaddingExtension(final Hashtable hashtable, final int n) throws IOException {
        hashtable.put(TlsExtensionsUtils.EXT_padding, createPaddingExtension(n));
    }
    
    public static void addServerNameExtension(final Hashtable hashtable, final ServerNameList list) throws IOException {
        hashtable.put(TlsExtensionsUtils.EXT_server_name, createServerNameExtension(list));
    }
    
    public static void addStatusRequestExtension(final Hashtable hashtable, final CertificateStatusRequest certificateStatusRequest) throws IOException {
        hashtable.put(TlsExtensionsUtils.EXT_status_request, createStatusRequestExtension(certificateStatusRequest));
    }
    
    public static void addTruncatedHMacExtension(final Hashtable hashtable) {
        hashtable.put(TlsExtensionsUtils.EXT_truncated_hmac, createTruncatedHMacExtension());
    }
    
    public static HeartbeatExtension getHeartbeatExtension(final Hashtable hashtable) throws IOException {
        final byte[] extensionData = TlsUtils.getExtensionData(hashtable, TlsExtensionsUtils.EXT_heartbeat);
        return (extensionData == null) ? null : readHeartbeatExtension(extensionData);
    }
    
    public static short getMaxFragmentLengthExtension(final Hashtable hashtable) throws IOException {
        final byte[] extensionData = TlsUtils.getExtensionData(hashtable, TlsExtensionsUtils.EXT_max_fragment_length);
        return (short)((extensionData == null) ? -1 : readMaxFragmentLengthExtension(extensionData));
    }
    
    public static int getPaddingExtension(final Hashtable hashtable) throws IOException {
        final byte[] extensionData = TlsUtils.getExtensionData(hashtable, TlsExtensionsUtils.EXT_padding);
        return (extensionData == null) ? -1 : readPaddingExtension(extensionData);
    }
    
    public static ServerNameList getServerNameExtension(final Hashtable hashtable) throws IOException {
        final byte[] extensionData = TlsUtils.getExtensionData(hashtable, TlsExtensionsUtils.EXT_server_name);
        return (extensionData == null) ? null : readServerNameExtension(extensionData);
    }
    
    public static CertificateStatusRequest getStatusRequestExtension(final Hashtable hashtable) throws IOException {
        final byte[] extensionData = TlsUtils.getExtensionData(hashtable, TlsExtensionsUtils.EXT_status_request);
        return (extensionData == null) ? null : readStatusRequestExtension(extensionData);
    }
    
    public static boolean hasEncryptThenMACExtension(final Hashtable hashtable) throws IOException {
        final byte[] extensionData = TlsUtils.getExtensionData(hashtable, TlsExtensionsUtils.EXT_encrypt_then_mac);
        return extensionData != null && readEncryptThenMACExtension(extensionData);
    }
    
    public static boolean hasExtendedMasterSecretExtension(final Hashtable hashtable) throws IOException {
        final byte[] extensionData = TlsUtils.getExtensionData(hashtable, TlsExtensionsUtils.EXT_extended_master_secret);
        return extensionData != null && readExtendedMasterSecretExtension(extensionData);
    }
    
    public static boolean hasTruncatedHMacExtension(final Hashtable hashtable) throws IOException {
        final byte[] extensionData = TlsUtils.getExtensionData(hashtable, TlsExtensionsUtils.EXT_truncated_hmac);
        return extensionData != null && readTruncatedHMacExtension(extensionData);
    }
    
    public static byte[] createEmptyExtensionData() {
        return TlsUtils.EMPTY_BYTES;
    }
    
    public static byte[] createEncryptThenMACExtension() {
        return createEmptyExtensionData();
    }
    
    public static byte[] createExtendedMasterSecretExtension() {
        return createEmptyExtensionData();
    }
    
    public static byte[] createHeartbeatExtension(final HeartbeatExtension heartbeatExtension) throws IOException {
        if (heartbeatExtension == null) {
            throw new TlsFatalAlert((short)80);
        }
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        heartbeatExtension.encode(byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
    
    public static byte[] createMaxFragmentLengthExtension(final short n) throws IOException {
        TlsUtils.checkUint8(n);
        final byte[] array = { 0 };
        TlsUtils.writeUint8(n, array, 0);
        return array;
    }
    
    public static byte[] createPaddingExtension(final int n) throws IOException {
        TlsUtils.checkUint16(n);
        return new byte[n];
    }
    
    public static byte[] createServerNameExtension(final ServerNameList list) throws IOException {
        if (list == null) {
            throw new TlsFatalAlert((short)80);
        }
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        list.encode(byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
    
    public static byte[] createStatusRequestExtension(final CertificateStatusRequest certificateStatusRequest) throws IOException {
        if (certificateStatusRequest == null) {
            throw new TlsFatalAlert((short)80);
        }
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        certificateStatusRequest.encode(byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
    
    public static byte[] createTruncatedHMacExtension() {
        return createEmptyExtensionData();
    }
    
    private static boolean readEmptyExtensionData(final byte[] array) throws IOException {
        if (array == null) {
            throw new IllegalArgumentException("'extensionData' cannot be null");
        }
        if (array.length != 0) {
            throw new TlsFatalAlert((short)47);
        }
        return true;
    }
    
    public static boolean readEncryptThenMACExtension(final byte[] array) throws IOException {
        return readEmptyExtensionData(array);
    }
    
    public static boolean readExtendedMasterSecretExtension(final byte[] array) throws IOException {
        return readEmptyExtensionData(array);
    }
    
    public static HeartbeatExtension readHeartbeatExtension(final byte[] array) throws IOException {
        if (array == null) {
            throw new IllegalArgumentException("'extensionData' cannot be null");
        }
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(array);
        final HeartbeatExtension parse = HeartbeatExtension.parse(byteArrayInputStream);
        TlsProtocol.assertEmpty(byteArrayInputStream);
        return parse;
    }
    
    public static short readMaxFragmentLengthExtension(final byte[] array) throws IOException {
        if (array == null) {
            throw new IllegalArgumentException("'extensionData' cannot be null");
        }
        if (array.length != 1) {
            throw new TlsFatalAlert((short)50);
        }
        return TlsUtils.readUint8(array, 0);
    }
    
    public static int readPaddingExtension(final byte[] array) throws IOException {
        if (array == null) {
            throw new IllegalArgumentException("'extensionData' cannot be null");
        }
        for (int i = 0; i < array.length; ++i) {
            if (array[i] != 0) {
                throw new TlsFatalAlert((short)47);
            }
        }
        return array.length;
    }
    
    public static ServerNameList readServerNameExtension(final byte[] array) throws IOException {
        if (array == null) {
            throw new IllegalArgumentException("'extensionData' cannot be null");
        }
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(array);
        final ServerNameList parse = ServerNameList.parse(byteArrayInputStream);
        TlsProtocol.assertEmpty(byteArrayInputStream);
        return parse;
    }
    
    public static CertificateStatusRequest readStatusRequestExtension(final byte[] array) throws IOException {
        if (array == null) {
            throw new IllegalArgumentException("'extensionData' cannot be null");
        }
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(array);
        final CertificateStatusRequest parse = CertificateStatusRequest.parse(byteArrayInputStream);
        TlsProtocol.assertEmpty(byteArrayInputStream);
        return parse;
    }
    
    public static boolean readTruncatedHMacExtension(final byte[] array) throws IOException {
        return readEmptyExtensionData(array);
    }
    
    static {
        EXT_encrypt_then_mac = Integers.valueOf(22);
        EXT_extended_master_secret = Integers.valueOf(23);
        EXT_heartbeat = Integers.valueOf(15);
        EXT_max_fragment_length = Integers.valueOf(1);
        EXT_padding = Integers.valueOf(21);
        EXT_server_name = Integers.valueOf(0);
        EXT_status_request = Integers.valueOf(5);
        EXT_truncated_hmac = Integers.valueOf(4);
    }
}
