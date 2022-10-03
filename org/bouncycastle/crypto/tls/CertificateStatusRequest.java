package org.bouncycastle.crypto.tls;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

public class CertificateStatusRequest
{
    protected short statusType;
    protected Object request;
    
    public CertificateStatusRequest(final short statusType, final Object request) {
        if (!isCorrectType(statusType, request)) {
            throw new IllegalArgumentException("'request' is not an instance of the correct type");
        }
        this.statusType = statusType;
        this.request = request;
    }
    
    public short getStatusType() {
        return this.statusType;
    }
    
    public Object getRequest() {
        return this.request;
    }
    
    public OCSPStatusRequest getOCSPStatusRequest() {
        if (!isCorrectType((short)1, this.request)) {
            throw new IllegalStateException("'request' is not an OCSPStatusRequest");
        }
        return (OCSPStatusRequest)this.request;
    }
    
    public void encode(final OutputStream outputStream) throws IOException {
        TlsUtils.writeUint8(this.statusType, outputStream);
        switch (this.statusType) {
            case 1: {
                ((OCSPStatusRequest)this.request).encode(outputStream);
                return;
            }
            default: {
                throw new TlsFatalAlert((short)80);
            }
        }
    }
    
    public static CertificateStatusRequest parse(final InputStream inputStream) throws IOException {
        final short uint8 = TlsUtils.readUint8(inputStream);
        switch (uint8) {
            case 1: {
                return new CertificateStatusRequest(uint8, OCSPStatusRequest.parse(inputStream));
            }
            default: {
                throw new TlsFatalAlert((short)50);
            }
        }
    }
    
    protected static boolean isCorrectType(final short n, final Object o) {
        switch (n) {
            case 1: {
                return o instanceof OCSPStatusRequest;
            }
            default: {
                throw new IllegalArgumentException("'statusType' is an unsupported CertificateStatusType");
            }
        }
    }
}
