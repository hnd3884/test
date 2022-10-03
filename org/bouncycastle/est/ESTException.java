package org.bouncycastle.est;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;

public class ESTException extends IOException
{
    private Throwable cause;
    private InputStream body;
    private int statusCode;
    private static final long MAX_ERROR_BODY = 8192L;
    
    public ESTException(final String s) {
        this(s, null);
    }
    
    public ESTException(final String s, final Throwable cause) {
        super(s);
        this.cause = cause;
        this.body = null;
        this.statusCode = 0;
    }
    
    public ESTException(final String s, final Throwable cause, final int statusCode, final InputStream inputStream) {
        super(s);
        this.cause = cause;
        this.statusCode = statusCode;
        if (inputStream != null) {
            final byte[] array = new byte[8192];
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            try {
                for (int i = inputStream.read(array); i >= 0; i = inputStream.read(array)) {
                    if (byteArrayOutputStream.size() + i > 8192L) {
                        byteArrayOutputStream.write(array, 0, 8192 - byteArrayOutputStream.size());
                        break;
                    }
                    byteArrayOutputStream.write(array, 0, i);
                }
                byteArrayOutputStream.flush();
                byteArrayOutputStream.close();
                this.body = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
                inputStream.close();
            }
            catch (final Exception ex) {}
        }
        else {
            this.body = null;
        }
    }
    
    @Override
    public Throwable getCause() {
        return this.cause;
    }
    
    @Override
    public String getMessage() {
        return super.getMessage() + " HTTP Status Code: " + this.statusCode;
    }
    
    public InputStream getBody() {
        return this.body;
    }
    
    public int getStatusCode() {
        return this.statusCode;
    }
}
