package org.glassfish.jersey.internal.util;

import java.io.IOException;
import java.io.InputStream;

public class Closing
{
    private final InputStream in;
    
    public static Closing with(final InputStream in) {
        return new Closing(in);
    }
    
    public Closing(final InputStream in) {
        this.in = in;
    }
    
    public void invoke(final Closure<InputStream> c) throws IOException {
        if (this.in == null) {
            return;
        }
        try {
            c.invoke(this.in);
        }
        finally {
            try {
                this.in.close();
            }
            catch (final IOException ex) {
                throw ex;
            }
        }
    }
}
