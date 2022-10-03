package org.bouncycastle.eac;

import java.io.OutputStream;
import org.bouncycastle.eac.operator.EACSignatureVerifier;
import org.bouncycastle.asn1.eac.PublicKeyDataObject;
import org.bouncycastle.asn1.ASN1ParsingException;
import java.io.IOException;
import org.bouncycastle.asn1.eac.CVCertificate;

public class EACCertificateHolder
{
    private CVCertificate cvCertificate;
    
    private static CVCertificate parseBytes(final byte[] array) throws IOException {
        try {
            return CVCertificate.getInstance((Object)array);
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
    
    public EACCertificateHolder(final byte[] array) throws IOException {
        this(parseBytes(array));
    }
    
    public EACCertificateHolder(final CVCertificate cvCertificate) {
        this.cvCertificate = cvCertificate;
    }
    
    public CVCertificate toASN1Structure() {
        return this.cvCertificate;
    }
    
    public PublicKeyDataObject getPublicKeyDataObject() {
        return this.cvCertificate.getBody().getPublicKey();
    }
    
    public boolean isSignatureValid(final EACSignatureVerifier eacSignatureVerifier) throws EACException {
        try {
            final OutputStream outputStream = eacSignatureVerifier.getOutputStream();
            outputStream.write(this.cvCertificate.getBody().getEncoded("DER"));
            outputStream.close();
            return eacSignatureVerifier.verify(this.cvCertificate.getSignature());
        }
        catch (final Exception ex) {
            throw new EACException("unable to process signature: " + ex.getMessage(), ex);
        }
    }
}
