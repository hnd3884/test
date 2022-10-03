package org.apache.tika.detect;

import java.io.IOException;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.mime.MediaType;
import org.apache.tika.metadata.Metadata;
import java.io.InputStream;

public class OverrideDetector implements Detector
{
    @Override
    public MediaType detect(final InputStream input, final Metadata metadata) throws IOException {
        String type = metadata.get(TikaCoreProperties.CONTENT_TYPE_PARSER_OVERRIDE);
        if (type != null) {
            return MediaType.parse(type);
        }
        type = metadata.get(TikaCoreProperties.CONTENT_TYPE_USER_OVERRIDE);
        if (type != null) {
            return MediaType.parse(type);
        }
        return MediaType.OCTET_STREAM;
    }
}
