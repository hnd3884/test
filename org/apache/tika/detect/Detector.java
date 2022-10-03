package org.apache.tika.detect;

import java.io.IOException;
import org.apache.tika.mime.MediaType;
import org.apache.tika.metadata.Metadata;
import java.io.InputStream;
import java.io.Serializable;

public interface Detector extends Serializable
{
    MediaType detect(final InputStream p0, final Metadata p1) throws IOException;
}
