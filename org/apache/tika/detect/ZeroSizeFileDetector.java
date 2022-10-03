package org.apache.tika.detect;

import java.io.IOException;
import org.apache.tika.mime.MediaType;
import org.apache.tika.metadata.Metadata;
import java.io.InputStream;

public class ZeroSizeFileDetector implements Detector
{
    @Override
    public MediaType detect(final InputStream stream, final Metadata metadata) throws IOException {
        if (stream != null) {
            try {
                stream.mark(1);
                if (stream.read() == -1) {
                    return MediaType.EMPTY;
                }
            }
            finally {
                stream.reset();
            }
        }
        return MediaType.OCTET_STREAM;
    }
}
