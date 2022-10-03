package com.lowagie.text.pdf;

import java.io.IOException;
import java.io.OutputStream;

public class PdfString extends PdfObject
{
    protected String value;
    protected String originalValue;
    protected String encoding;
    protected int objNum;
    protected int objGen;
    protected boolean hexWriting;
    
    public PdfString() {
        super(3);
        this.value = "";
        this.originalValue = null;
        this.encoding = "PDF";
        this.objNum = 0;
        this.objGen = 0;
        this.hexWriting = false;
    }
    
    public PdfString(final String value) {
        super(3);
        this.value = "";
        this.originalValue = null;
        this.encoding = "PDF";
        this.objNum = 0;
        this.objGen = 0;
        this.hexWriting = false;
        this.value = value;
    }
    
    public PdfString(final String value, final String encoding) {
        super(3);
        this.value = "";
        this.originalValue = null;
        this.encoding = "PDF";
        this.objNum = 0;
        this.objGen = 0;
        this.hexWriting = false;
        this.value = value;
        this.encoding = encoding;
    }
    
    public PdfString(final byte[] bytes) {
        super(3);
        this.value = "";
        this.originalValue = null;
        this.encoding = "PDF";
        this.objNum = 0;
        this.objGen = 0;
        this.hexWriting = false;
        this.value = PdfEncodings.convertToString(bytes, null);
        this.encoding = "";
    }
    
    @Override
    public void toPdf(final PdfWriter writer, final OutputStream os) throws IOException {
        byte[] b = this.getBytes();
        PdfEncryption crypto = null;
        if (writer != null) {
            crypto = writer.getEncryption();
        }
        if (crypto != null && !crypto.isEmbeddedFilesOnly()) {
            b = crypto.encryptByteArray(b);
        }
        if (this.hexWriting) {
            final ByteBuffer buf = new ByteBuffer();
            buf.append('<');
            for (int len = b.length, k = 0; k < len; ++k) {
                buf.appendHex(b[k]);
            }
            buf.append('>');
            os.write(buf.toByteArray());
        }
        else {
            os.write(PdfContentByte.escapeString(b));
        }
    }
    
    @Override
    public String toString() {
        return this.value;
    }
    
    @Override
    public byte[] getBytes() {
        if (this.bytes == null) {
            if (this.encoding != null && this.encoding.equals("UnicodeBig") && PdfEncodings.isPdfDocEncoding(this.value)) {
                this.bytes = PdfEncodings.convertToBytes(this.value, "PDF");
            }
            else {
                this.bytes = PdfEncodings.convertToBytes(this.value, this.encoding);
            }
        }
        return this.bytes;
    }
    
    public String toUnicodeString() {
        if (this.encoding != null && this.encoding.length() != 0) {
            return this.value;
        }
        this.getBytes();
        if (this.bytes.length >= 2 && this.bytes[0] == -2 && this.bytes[1] == -1) {
            return PdfEncodings.convertToString(this.bytes, "UnicodeBig");
        }
        return PdfEncodings.convertToString(this.bytes, "PDF");
    }
    
    public String getEncoding() {
        return this.encoding;
    }
    
    void setObjNum(final int objNum, final int objGen) {
        this.objNum = objNum;
        this.objGen = objGen;
    }
    
    void decrypt(final PdfReader reader) {
        final PdfEncryption decrypt = reader.getDecrypt();
        if (decrypt != null) {
            this.originalValue = this.value;
            decrypt.setHashKey(this.objNum, this.objGen);
            this.bytes = PdfEncodings.convertToBytes(this.value, null);
            this.bytes = decrypt.decryptByteArray(this.bytes);
            this.value = PdfEncodings.convertToString(this.bytes, null);
        }
    }
    
    public byte[] getOriginalBytes() {
        if (this.originalValue == null) {
            return this.getBytes();
        }
        return PdfEncodings.convertToBytes(this.originalValue, null);
    }
    
    public char[] getOriginalChars() {
        char[] chars;
        if (this.encoding == null || this.encoding.length() == 0) {
            final byte[] bytes = this.getOriginalBytes();
            chars = new char[bytes.length];
            for (int i = 0; i < bytes.length; ++i) {
                chars[i] = (char)(bytes[i] & 0xFF);
            }
        }
        else {
            chars = new char[0];
        }
        return chars;
    }
    
    public PdfString setHexWriting(final boolean hexWriting) {
        this.hexWriting = hexWriting;
        return this;
    }
    
    public boolean isHexWriting() {
        return this.hexWriting;
    }
}
