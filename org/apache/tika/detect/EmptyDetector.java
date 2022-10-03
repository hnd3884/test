package org.apache.tika.detect;

import java.io.IOException;
import org.apache.tika.mime.MediaType;
import org.apache.tika.metadata.Metadata;
import java.io.InputStream;

public class EmptyDetector implements Detector
{
    public static final EmptyDetector INSTANCE;
    
    @Override
    public MediaType detect(final InputStream input, final Metadata metadata) throws IOException {
        return MediaType.OCTET_STREAM;
    }
    
    static {
        INSTANCE = new EmptyDetector();
    }
}
