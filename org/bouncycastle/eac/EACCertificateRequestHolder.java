package org.bouncycastle.eac;

import java.io.OutputStream;
import org.bouncycastle.eac.operator.EACSignatureVerifier;
import org.bouncycastle.asn1.eac.PublicKeyDataObject;
import org.bouncycastle.asn1.ASN1ParsingException;
import java.io.IOException;
import org.bouncycastle.asn1.eac.CVCertificateRequest;

public class EACCertificateRequestHolder
{
    private CVCertificateRequest request;
    
    private static CVCertificateRequest parseBytes(final byte[] array) throws IOException {
        try {
            return CVCertificateRequest.getInstance((Object)array);
        }
        catch (final ClassCastException ex) {
            throw new EACIOException("malformed data: " + ex.getMessage(), ex);
        }
        catch (final IllegalArgumentException ex2) {
            throw new EACIOException("malformed data: " + ex2.getMessage(), ex2);
        }
        catch (final ASN1ParsingException ex3) {
            if (ex3.getCause() instanceof IOException) {
                throw (IOException)ex3.getCause();
            }
            throw new EACIOException("malformed data: " + ex3.getMessage(), (Throwable)ex3);
        }
    }
    
    public EACCertificateRequestHolder(final byte[] array) throws IOException {
        this(parseBytes(array));
    }
    
    public EACCertificateRequestHolder(final CVCertificateRequest request) {
        this.request = request;
    }
    
    public CVCertificateRequest toASN1Structure() {
        return this.request;
    }
    
    public PublicKeyDataObject getPublicKeyDataObject() {
        return this.request.getPublicKey();
    }
    
    public boolean isInnerSignatureValid(final EACSignatureVerifier eacSignatureVerifier) throws EACException {
        try {
            final OutputStream outputStream = eacSignatureVerifier.getOutputStream();
            outputStream.write(this.request.getCertificateBody().getEncoded("DER"));
            outputStream.close();
            return eacSignatureVerifier.verify(this.request.getInnerSignature());
        }
        catch (final Exception ex) {
            throw new EACException("unable to process signature: " + ex.getMessage(), ex);
        }
    }
}
