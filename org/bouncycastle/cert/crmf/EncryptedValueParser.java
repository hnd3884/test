package org.bouncycastle.cert.crmf;

import org.bouncycastle.util.Strings;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.cert.X509CertificateHolder;
import java.io.IOException;
import org.bouncycastle.util.io.Streams;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import org.bouncycastle.asn1.crmf.EncryptedValue;

public class EncryptedValueParser
{
    private EncryptedValue value;
    private EncryptedValuePadder padder;
    
    public EncryptedValueParser(final EncryptedValue value) {
        this.value = value;
    }
    
    public EncryptedValueParser(final EncryptedValue value, final EncryptedValuePadder padder) {
        this.value = value;
        this.padder = padder;
    }
    
    private byte[] decryptValue(final ValueDecryptorGenerator valueDecryptorGenerator) throws CRMFException {
        if (this.value.getIntendedAlg() != null) {
            throw new UnsupportedOperationException();
        }
        if (this.value.getValueHint() != null) {
            throw new UnsupportedOperationException();
        }
        final InputStream inputStream = valueDecryptorGenerator.getValueDecryptor(this.value.getKeyAlg(), this.value.getSymmAlg(), this.value.getEncSymmKey().getBytes()).getInputStream(new ByteArrayInputStream(this.value.getEncValue().getBytes()));
        try {
            final byte[] all = Streams.readAll(inputStream);
            if (this.padder != null) {
                return this.padder.getUnpaddedData(all);
            }
            return all;
        }
        catch (final IOException ex) {
            throw new CRMFException("Cannot parse decrypted data: " + ex.getMessage(), ex);
        }
    }
    
    public X509CertificateHolder readCertificateHolder(final ValueDecryptorGenerator valueDecryptorGenerator) throws CRMFException {
        return new X509CertificateHolder(Certificate.getInstance((Object)this.decryptValue(valueDecryptorGenerator)));
    }
    
    public char[] readPassphrase(final ValueDecryptorGenerator valueDecryptorGenerator) throws CRMFException {
        return Strings.fromUTF8ByteArray(this.decryptValue(valueDecryptorGenerator)).toCharArray();
    }
}
