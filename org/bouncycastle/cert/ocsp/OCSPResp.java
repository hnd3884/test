package org.bouncycastle.cert.ocsp;

import org.bouncycastle.asn1.ocsp.ResponseBytes;
import org.bouncycastle.asn1.ocsp.BasicOCSPResponse;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ocsp.OCSPObjectIdentifiers;
import org.bouncycastle.asn1.ASN1Exception;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.asn1.ASN1InputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import org.bouncycastle.asn1.ocsp.OCSPResponse;

public class OCSPResp
{
    public static final int SUCCESSFUL = 0;
    public static final int MALFORMED_REQUEST = 1;
    public static final int INTERNAL_ERROR = 2;
    public static final int TRY_LATER = 3;
    public static final int SIG_REQUIRED = 5;
    public static final int UNAUTHORIZED = 6;
    private OCSPResponse resp;
    
    public OCSPResp(final OCSPResponse resp) {
        this.resp = resp;
    }
    
    public OCSPResp(final byte[] array) throws IOException {
        this(new ByteArrayInputStream(array));
    }
    
    public OCSPResp(final InputStream inputStream) throws IOException {
        this(new ASN1InputStream(inputStream));
    }
    
    private OCSPResp(final ASN1InputStream asn1InputStream) throws IOException {
        try {
            this.resp = OCSPResponse.getInstance((Object)asn1InputStream.readObject());
        }
        catch (final IllegalArgumentException ex) {
            throw new CertIOException("malformed response: " + ex.getMessage(), ex);
        }
        catch (final ClassCastException ex2) {
            throw new CertIOException("malformed response: " + ex2.getMessage(), ex2);
        }
        catch (final ASN1Exception ex3) {
            throw new CertIOException("malformed response: " + ex3.getMessage(), (Throwable)ex3);
        }
        if (this.resp == null) {
            throw new CertIOException("malformed response: no response data found");
        }
    }
    
    public int getStatus() {
        return this.resp.getResponseStatus().getValue().intValue();
    }
    
    public Object getResponseObject() throws OCSPException {
        final ResponseBytes responseBytes = this.resp.getResponseBytes();
        if (responseBytes == null) {
            return null;
        }
        if (responseBytes.getResponseType().equals((Object)OCSPObjectIdentifiers.id_pkix_ocsp_basic)) {
            try {
                return new BasicOCSPResp(BasicOCSPResponse.getInstance((Object)ASN1Primitive.fromByteArray(responseBytes.getResponse().getOctets())));
            }
            catch (final Exception ex) {
                throw new OCSPException("problem decoding object: " + ex, ex);
            }
        }
        return responseBytes.getResponse();
    }
    
    public byte[] getEncoded() throws IOException {
        return this.resp.getEncoded();
    }
    
    @Override
    public boolean equals(final Object o) {
        return o == this || (o instanceof OCSPResp && this.resp.equals((Object)((OCSPResp)o).resp));
    }
    
    @Override
    public int hashCode() {
        return this.resp.hashCode();
    }
    
    public OCSPResponse toASN1Structure() {
        return this.resp;
    }
}
