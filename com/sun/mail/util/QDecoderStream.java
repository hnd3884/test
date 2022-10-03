package com.sun.mail.util;

import java.io.IOException;
import java.io.InputStream;

public class QDecoderStream extends QPDecoderStream
{
    public QDecoderStream(final InputStream in) {
        super(in);
    }
    
    @Override
    public int read() throws IOException {
        final int c = this.in.read();
        if (c == 95) {
            return 32;
        }
        if (c == 61) {
            this.ba[0] = (byte)this.in.read();
            this.ba[1] = (byte)this.in.read();
            try {
                return ASCIIUtility.parseInt(this.ba, 0, 2, 16);
            }
            catch (final NumberFormatException nex) {
                throw new DecodingException("QDecoder: Error in QP stream " + nex.getMessage());
            }
        }
        return c;
    }
}
