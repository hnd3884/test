package org.apache.axiom.mime.impl.axiom;

import org.apache.axiom.mime.MultipartWriter;
import java.io.OutputStream;
import org.apache.axiom.mime.MultipartWriterFactory;

public class AxiomMultipartWriterFactory implements MultipartWriterFactory
{
    public static final MultipartWriterFactory INSTANCE;
    
    public MultipartWriter createMultipartWriter(final OutputStream out, final String boundary) {
        return new MultipartWriterImpl(out, boundary);
    }
    
    static {
        INSTANCE = new AxiomMultipartWriterFactory();
    }
}
