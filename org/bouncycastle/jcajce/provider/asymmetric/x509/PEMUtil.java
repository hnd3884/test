package org.bouncycastle.jcajce.provider.asymmetric.x509;

import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.asn1.ASN1Sequence;
import java.io.IOException;
import java.io.InputStream;

class PEMUtil
{
    private final String _header1;
    private final String _header2;
    private final String _header3;
    private final String _footer1;
    private final String _footer2;
    private final String _footer3;
    
    PEMUtil(final String s) {
        this._header1 = "-----BEGIN " + s + "-----";
        this._header2 = "-----BEGIN X509 " + s + "-----";
        this._header3 = "-----BEGIN PKCS7-----";
        this._footer1 = "-----END " + s + "-----";
        this._footer2 = "-----END X509 " + s + "-----";
        this._footer3 = "-----END PKCS7-----";
    }
    
    private String readLine(final InputStream inputStream) throws IOException {
        final StringBuffer sb = new StringBuffer();
        int read;
        while (true) {
            if ((read = inputStream.read()) != 13 && read != 10 && read >= 0) {
                sb.append((char)read);
            }
            else {
                if (read < 0 || sb.length() != 0) {
                    break;
                }
                continue;
            }
        }
        if (read < 0) {
            return null;
        }
        if (read == 13) {
            inputStream.mark(1);
            final int read2;
            if ((read2 = inputStream.read()) == 10) {
                inputStream.mark(1);
            }
            if (read2 > 0) {
                inputStream.reset();
            }
        }
        return sb.toString();
    }
    
    ASN1Sequence readPEMObject(final InputStream inputStream) throws IOException {
        final StringBuffer sb = new StringBuffer();
        String line;
        while ((line = this.readLine(inputStream)) != null && !line.startsWith(this._header1) && !line.startsWith(this._header2) && !line.startsWith(this._header3)) {}
        String line2;
        while ((line2 = this.readLine(inputStream)) != null && !line2.startsWith(this._footer1) && !line2.startsWith(this._footer2) && !line2.startsWith(this._footer3)) {
            sb.append(line2);
        }
        if (sb.length() != 0) {
            try {
                return ASN1Sequence.getInstance(Base64.decode(sb.toString()));
            }
            catch (final Exception ex) {
                throw new IOException("malformed PEM data encountered");
            }
        }
        return null;
    }
}
