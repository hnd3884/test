package org.apache.axiom.mime;

import javax.activation.DataHandler;
import java.util.List;
import java.io.IOException;
import java.io.OutputStream;

public interface MultipartWriter
{
    OutputStream writePart(final String p0, final String p1, final String p2) throws IOException;
    
    OutputStream writePart(final String p0, final String p1, final String p2, final List p3) throws IOException;
    
    void writePart(final DataHandler p0, final String p1, final String p2) throws IOException;
    
    void writePart(final DataHandler p0, final String p1, final String p2, final List p3) throws IOException;
    
    void complete() throws IOException;
}
