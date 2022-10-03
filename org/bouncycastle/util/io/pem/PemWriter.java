package org.bouncycastle.util.io.pem;

import org.bouncycastle.util.encoders.Base64;
import java.io.IOException;
import java.util.Iterator;
import org.bouncycastle.util.Strings;
import java.io.Writer;
import java.io.BufferedWriter;

public class PemWriter extends BufferedWriter
{
    private static final int LINE_LENGTH = 64;
    private final int nlLength;
    private char[] buf;
    
    public PemWriter(final Writer writer) {
        super(writer);
        this.buf = new char[64];
        final String lineSeparator = Strings.lineSeparator();
        if (lineSeparator != null) {
            this.nlLength = lineSeparator.length();
        }
        else {
            this.nlLength = 2;
        }
    }
    
    public int getOutputSize(final PemObject pemObject) {
        int n = 2 * (pemObject.getType().length() + 10 + this.nlLength) + 6 + 4;
        if (!pemObject.getHeaders().isEmpty()) {
            for (final PemHeader pemHeader : pemObject.getHeaders()) {
                n += pemHeader.getName().length() + ": ".length() + pemHeader.getValue().length() + this.nlLength;
            }
            n += this.nlLength;
        }
        final int n2 = (pemObject.getContent().length + 2) / 3 * 4;
        return n + (n2 + (n2 + 64 - 1) / 64 * this.nlLength);
    }
    
    public void writeObject(final PemObjectGenerator pemObjectGenerator) throws IOException {
        final PemObject generate = pemObjectGenerator.generate();
        this.writePreEncapsulationBoundary(generate.getType());
        if (!generate.getHeaders().isEmpty()) {
            for (final PemHeader pemHeader : generate.getHeaders()) {
                this.write(pemHeader.getName());
                this.write(": ");
                this.write(pemHeader.getValue());
                this.newLine();
            }
            this.newLine();
        }
        this.writeEncoded(generate.getContent());
        this.writePostEncapsulationBoundary(generate.getType());
    }
    
    private void writeEncoded(byte[] encode) throws IOException {
        encode = Base64.encode(encode);
        for (int i = 0; i < encode.length; i += this.buf.length) {
            int n;
            for (n = 0; n != this.buf.length && i + n < encode.length; ++n) {
                this.buf[n] = (char)encode[i + n];
            }
            this.write(this.buf, 0, n);
            this.newLine();
        }
    }
    
    private void writePreEncapsulationBoundary(final String s) throws IOException {
        this.write("-----BEGIN " + s + "-----");
        this.newLine();
    }
    
    private void writePostEncapsulationBoundary(final String s) throws IOException {
        this.write("-----END " + s + "-----");
        this.newLine();
    }
}
