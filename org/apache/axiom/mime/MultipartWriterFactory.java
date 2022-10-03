package org.apache.axiom.mime;

import java.io.OutputStream;

public interface MultipartWriterFactory
{
    MultipartWriter createMultipartWriter(final OutputStream p0, final String p1);
}
