package org.apache.tika.detect;

import org.apache.tika.mime.MediaType;
import org.apache.tika.metadata.Metadata;
import java.io.InputStream;

public class TypeDetector implements Detector
{
    @Override
    public MediaType detect(final InputStream input, final Metadata metadata) {
        final String hint = metadata.get("Content-Type");
        if (hint != null) {
            final MediaType type = MediaType.parse(hint);
            if (type != null) {
                return type;
            }
        }
        return MediaType.OCTET_STREAM;
    }
}
