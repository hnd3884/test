package org.bouncycastle.crypto.tls;

import java.io.InputStream;
import java.io.IOException;
import org.bouncycastle.util.Strings;
import java.io.OutputStream;

public class URLAndHash
{
    protected String url;
    protected byte[] sha1Hash;
    
    public URLAndHash(final String url, final byte[] sha1Hash) {
        if (url == null || url.length() < 1 || url.length() >= 65536) {
            throw new IllegalArgumentException("'url' must have length from 1 to (2^16 - 1)");
        }
        if (sha1Hash != null && sha1Hash.length != 20) {
            throw new IllegalArgumentException("'sha1Hash' must have length == 20, if present");
        }
        this.url = url;
        this.sha1Hash = sha1Hash;
    }
    
    public String getURL() {
        return this.url;
    }
    
    public byte[] getSHA1Hash() {
        return this.sha1Hash;
    }
    
    public void encode(final OutputStream outputStream) throws IOException {
        TlsUtils.writeOpaque16(Strings.toByteArray(this.url), outputStream);
        if (this.sha1Hash == null) {
            TlsUtils.writeUint8(0, outputStream);
        }
        else {
            TlsUtils.writeUint8(1, outputStream);
            outputStream.write(this.sha1Hash);
        }
    }
    
    public static URLAndHash parse(final TlsContext tlsContext, final InputStream inputStream) throws IOException {
        final byte[] opaque16 = TlsUtils.readOpaque16(inputStream);
        if (opaque16.length < 1) {
            throw new TlsFatalAlert((short)47);
        }
        final String fromByteArray = Strings.fromByteArray(opaque16);
        byte[] fully = null;
        switch (TlsUtils.readUint8(inputStream)) {
            case 0: {
                if (TlsUtils.isTLSv12(tlsContext)) {
                    throw new TlsFatalAlert((short)47);
                }
                break;
            }
            case 1: {
                fully = TlsUtils.readFully(20, inputStream);
                break;
            }
            default: {
                throw new TlsFatalAlert((short)47);
            }
        }
        return new URLAndHash(fromByteArray, fully);
    }
}
