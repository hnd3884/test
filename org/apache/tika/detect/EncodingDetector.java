package org.apache.tika.detect;

import java.io.IOException;
import java.nio.charset.Charset;
import org.apache.tika.metadata.Metadata;
import java.io.InputStream;
import java.io.Serializable;

public interface EncodingDetector extends Serializable
{
    Charset detect(final InputStream p0, final Metadata p1) throws IOException;
}
