package net.oauth.signature.pem;

import net.oauth.signature.OAuthSignatureMethod;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class PEMReader
{
    public static final String PRIVATE_PKCS1_MARKER = "-----BEGIN RSA PRIVATE KEY-----";
    public static final String PRIVATE_PKCS8_MARKER = "-----BEGIN PRIVATE KEY-----";
    public static final String CERTIFICATE_X509_MARKER = "-----BEGIN CERTIFICATE-----";
    public static final String PUBLIC_X509_MARKER = "-----BEGIN PUBLIC KEY-----";
    private static final String BEGIN_MARKER = "-----BEGIN ";
    private InputStream stream;
    private byte[] derBytes;
    private String beginMarker;
    
    public PEMReader(final InputStream inStream) throws IOException {
        this.stream = inStream;
        this.readFile();
    }
    
    public PEMReader(final byte[] buffer) throws IOException {
        this(new ByteArrayInputStream(buffer));
    }
    
    public PEMReader(final String fileName) throws IOException {
        this(new FileInputStream(fileName));
    }
    
    public byte[] getDerBytes() {
        return this.derBytes;
    }
    
    public String getBeginMarker() {
        return this.beginMarker;
    }
    
    protected void readFile() throws IOException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(this.stream));
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.indexOf("-----BEGIN ") != -1) {
                    this.beginMarker = line.trim();
                    final String endMarker = this.beginMarker.replace("BEGIN", "END");
                    this.derBytes = this.readBytes(reader, endMarker);
                    return;
                }
            }
            throw new IOException("Invalid PEM file: no begin marker");
        }
        finally {
            reader.close();
        }
    }
    
    private byte[] readBytes(final BufferedReader reader, final String endMarker) throws IOException {
        String line = null;
        final StringBuffer buf = new StringBuffer();
        while ((line = reader.readLine()) != null) {
            if (line.indexOf(endMarker) != -1) {
                return OAuthSignatureMethod.decodeBase64(buf.toString());
            }
            buf.append(line.trim());
        }
        throw new IOException("Invalid PEM file: No end marker");
    }
}
