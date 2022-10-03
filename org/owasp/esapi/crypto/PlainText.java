package org.owasp.esapi.crypto;

import org.owasp.esapi.ESAPI;
import java.io.UnsupportedEncodingException;
import org.owasp.esapi.Logger;
import java.io.Serializable;

public final class PlainText implements Serializable
{
    private static final long serialVersionUID = 20090921L;
    private static Logger logger;
    private byte[] rawBytes;
    
    public PlainText(final String str) {
        this.rawBytes = null;
        try {
            assert str != null : "String for plaintext cannot be null.";
            if (str == null) {
                throw new IllegalArgumentException("String for plaintext may not be null!");
            }
            this.rawBytes = str.getBytes("UTF-8");
        }
        catch (final UnsupportedEncodingException e) {
            PlainText.logger.error(Logger.EVENT_FAILURE, "PlainText(String) CTOR failed: Can't find UTF-8 byte-encoding!", e);
            throw new RuntimeException("Can't find UTF-8 byte-encoding!", e);
        }
    }
    
    public PlainText(final byte[] b) {
        this.rawBytes = null;
        assert b != null : "Byte array representing plaintext cannot be null.";
        System.arraycopy(b, 0, this.rawBytes = new byte[b.length], 0, b.length);
    }
    
    @Override
    public String toString() {
        try {
            return new String(this.rawBytes, "UTF-8");
        }
        catch (final UnsupportedEncodingException e) {
            PlainText.logger.error(Logger.EVENT_FAILURE, "PlainText.toString() failed: Can't find UTF-8 byte-encoding!", e);
            throw new RuntimeException("Can't find UTF-8 byte-encoding!", e);
        }
    }
    
    public byte[] asBytes() {
        final byte[] bytes = new byte[this.rawBytes.length];
        System.arraycopy(this.rawBytes, 0, bytes, 0, this.rawBytes.length);
        return bytes;
    }
    
    @Override
    public boolean equals(final Object anObject) {
        if (this == anObject) {
            return true;
        }
        if (anObject == null) {
            return false;
        }
        boolean result = false;
        if (anObject instanceof PlainText) {
            final PlainText that = (PlainText)anObject;
            result = (that.canEqual(this) && this.toString().equals(that.toString()));
        }
        return result;
    }
    
    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }
    
    public int length() {
        return this.rawBytes.length;
    }
    
    public void overwrite() {
        CryptoHelper.overwrite(this.rawBytes);
    }
    
    protected boolean canEqual(final Object other) {
        return other instanceof PlainText;
    }
    
    static {
        PlainText.logger = ESAPI.getLogger("PlainText");
    }
}
