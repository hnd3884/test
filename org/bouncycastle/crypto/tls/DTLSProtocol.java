package org.bouncycastle.crypto.tls;

import java.util.Vector;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.util.Hashtable;
import java.io.IOException;
import org.bouncycastle.util.Arrays;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.security.SecureRandom;

public abstract class DTLSProtocol
{
    protected final SecureRandom secureRandom;
    
    protected DTLSProtocol(final SecureRandom secureRandom) {
        if (secureRandom == null) {
            throw new IllegalArgumentException("'secureRandom' cannot be null");
        }
        this.secureRandom = secureRandom;
    }
    
    protected void processFinished(final byte[] array, final byte[] array2) throws IOException {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(array);
        final byte[] fully = TlsUtils.readFully(array2.length, byteArrayInputStream);
        TlsProtocol.assertEmpty(byteArrayInputStream);
        if (!Arrays.constantTimeAreEqual(array2, fully)) {
            throw new TlsFatalAlert((short)40);
        }
    }
    
    protected static void applyMaxFragmentLengthExtension(final DTLSRecordLayer dtlsRecordLayer, final short n) throws IOException {
        if (n >= 0) {
            if (!MaxFragmentLength.isValid(n)) {
                throw new TlsFatalAlert((short)80);
            }
            dtlsRecordLayer.setPlaintextLimit(1 << 8 + n);
        }
    }
    
    protected static short evaluateMaxFragmentLengthExtension(final boolean b, final Hashtable hashtable, final Hashtable hashtable2, final short n) throws IOException {
        final short maxFragmentLengthExtension = TlsExtensionsUtils.getMaxFragmentLengthExtension(hashtable2);
        if (maxFragmentLengthExtension >= 0 && (!MaxFragmentLength.isValid(maxFragmentLengthExtension) || (!b && maxFragmentLengthExtension != TlsExtensionsUtils.getMaxFragmentLengthExtension(hashtable)))) {
            throw new TlsFatalAlert(n);
        }
        return maxFragmentLengthExtension;
    }
    
    protected static byte[] generateCertificate(final Certificate certificate) throws IOException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        certificate.encode(byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
    
    protected static byte[] generateSupplementalData(final Vector vector) throws IOException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        TlsProtocol.writeSupplementalData(byteArrayOutputStream, vector);
        return byteArrayOutputStream.toByteArray();
    }
    
    protected static void validateSelectedCipherSuite(final int n, final short n2) throws IOException {
        switch (TlsUtils.getEncryptionAlgorithm(n)) {
            case 1:
            case 2: {
                throw new TlsFatalAlert(n2);
            }
            default: {}
        }
    }
}
