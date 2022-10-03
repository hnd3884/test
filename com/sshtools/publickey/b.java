package com.sshtools.publickey;

import com.maverick.ssh.SshException;
import com.maverick.ssh.SshIOException;
import com.maverick.ssh.components.ComponentManager;
import com.maverick.ssh.components.Digest;
import java.io.UnsupportedEncodingException;
import java.io.IOException;

class b
{
    protected static final char[] b;
    
    protected static byte[] b(final String s, final byte[] array, final int n) throws IOException {
        try {
            byte[] array2;
            try {
                array2 = ((s == null) ? new byte[0] : s.getBytes("UTF-8"));
            }
            catch (final UnsupportedEncodingException ex) {
                throw new IOException("Mandatory US-ASCII character encoding is not supported by the VM");
            }
            final Digest digest = (Digest)ComponentManager.getInstance().supportedDigests().getInstance("MD5");
            final byte[] array3 = new byte[n];
            int n2 = n & 0xFFFFFFF0;
            if ((n & 0xF) != 0x0) {
                n2 += 16;
            }
            final byte[] array4 = new byte[n2];
            int n3 = 0;
            while (n3 + 16 <= array4.length) {
                digest.putBytes(array2, 0, array2.length);
                digest.putBytes(array, 0, 8);
                final byte[] doFinal = digest.doFinal();
                System.arraycopy(doFinal, 0, array4, n3, doFinal.length);
                n3 += doFinal.length;
                digest.putBytes(doFinal, 0, doFinal.length);
            }
            System.arraycopy(array4, 0, array3, 0, array3.length);
            return array3;
        }
        catch (final SshException ex2) {
            throw new SshIOException(ex2);
        }
    }
    
    static {
        b = "0123456789ABCDEF".toCharArray();
    }
}
