package org.bouncycastle.crypto.tls;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.ocsp.OCSPResponse;

public class CertificateStatus
{
    protected short statusType;
    protected Object response;
    
    public CertificateStatus(final short statusType, final Object response) {
        if (!isCorrectType(statusType, response)) {
            throw new IllegalArgumentException("'response' is not an instance of the correct type");
        }
        this.statusType = statusType;
        this.response = response;
    }
    
    public short getStatusType() {
        return this.statusType;
    }
    
    public Object getResponse() {
        return this.response;
    }
    
    public OCSPResponse getOCSPResponse() {
        if (!isCorrectType((short)1, this.response)) {
            throw new IllegalStateException("'response' is not an OCSPResponse");
        }
        return (OCSPResponse)this.response;
    }
    
    public void encode(final OutputStream outputStream) throws IOException {
        TlsUtils.writeUint8(this.statusType, outputStream);
        switch (this.statusType) {
            case 1: {
                TlsUtils.writeOpaque24(((OCSPResponse)this.response).getEncoded("DER"), outputStream);
                return;
            }
            default: {
                throw new TlsFatalAlert((short)80);
            }
        }
    }
    
    public static CertificateStatus parse(final InputStream inputStream) throws IOException {
        final short uint8 = TlsUtils.readUint8(inputStream);
        switch (uint8) {
            case 1: {
                return new CertificateStatus(uint8, OCSPResponse.getInstance(TlsUtils.readDERObject(TlsUtils.readOpaque24(inputStream))));
            }
            default: {
                throw new TlsFatalAlert((short)50);
            }
        }
    }
    
    protected static boolean isCorrectType(final short n, final Object o) {
        switch (n) {
            case 1: {
                return o instanceof OCSPResponse;
            }
            default: {
                throw new IllegalArgumentException("'statusType' is an unsupported CertificateStatusType");
            }
        }
    }
}
