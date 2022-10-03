package org.w3c.tidy;

import java.io.Writer;
import java.io.UnsupportedEncodingException;
import java.io.OutputStream;

public final class OutFactory
{
    private OutFactory() {
    }
    
    public static Out getOut(final Configuration configuration, final OutputStream outputStream) {
        try {
            return new OutJavaImpl(configuration, configuration.getOutCharEncodingName(), outputStream);
        }
        catch (final UnsupportedEncodingException ex) {
            throw new RuntimeException("Unsupported encoding: " + ex.getMessage());
        }
    }
    
    public static Out getOut(final Configuration configuration, final Writer writer) {
        return new OutJavaImpl(configuration, writer);
    }
}
