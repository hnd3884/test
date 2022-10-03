package org.apache.http.entity;

import java.io.IOException;
import java.io.OutputStream;

public interface ContentProducer
{
    void writeTo(final OutputStream p0) throws IOException;
}
