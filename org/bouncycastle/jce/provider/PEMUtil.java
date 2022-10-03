package org.bouncycastle.jce.provider;

import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.asn1.ASN1Sequence;
import java.io.IOException;
import java.io.InputStream;

public class PEMUtil
{
    private final String _header1;
    private final String _header2;
    private final String _footer1;
    private final String _footer2;
    
    PEMUtil(final String s) {
        this._header1 = "-----BEGIN " + s + "-----";
        this._header2 = "-----BEGIN X509 " + s + "-----";
        this._footer1 = "-----END " + s + "-----";
        this._footer2 = "-----END X509 " + s + "-----";
    }
    
    private String readLine(final InputStream inputStream) throws IOException {
        final StringBuffer sb = new StringBuffer();
        int read;
        while (true) {
            if ((read = inputStream.read()) != 13 && read != 10 && read >= 0) {
                if (read == 13) {
                    continue;
                }
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
        return sb.toString();
    }
    
    ASN1Sequence readPEMObject(final InputStream inputStream) throws IOException {
        final StringBuffer sb = new StringBuffer();
        String line;
        while ((line = this.readLine(inputStream)) != null && !line.startsWith(this._header1) && !line.startsWith(this._header2)) {}
        String line2;
        while ((line2 = this.readLine(inputStream)) != null && !line2.startsWith(this._footer1) && !line2.startsWith(this._footer2)) {
            sb.append(line2);
        }
        if (sb.length() == 0) {
            return null;
        }
        final ASN1Primitive object = new ASN1InputStream(Base64.decode(sb.toString())).readObject();
        if (!(object instanceof ASN1Sequence)) {
            throw new IOException("malformed PEM data encountered");
        }
        return (ASN1Sequence)object;
    }
}
