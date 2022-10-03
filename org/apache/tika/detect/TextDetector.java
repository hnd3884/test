package org.apache.tika.detect;

import java.util.Arrays;
import java.io.IOException;
import org.apache.tika.mime.MediaType;
import org.apache.tika.metadata.Metadata;
import java.io.InputStream;

public class TextDetector implements Detector
{
    private static final long serialVersionUID = 4774601079503507765L;
    private static final int DEFAULT_NUMBER_OF_BYTES_TO_TEST = 512;
    private static final boolean[] IS_CONTROL_BYTE;
    private final int bytesToTest;
    
    public TextDetector() {
        this(512);
    }
    
    public TextDetector(final int bytesToTest) {
        this.bytesToTest = bytesToTest;
    }
    
    @Override
    public MediaType detect(final InputStream input, final Metadata metadata) throws IOException {
        if (input == null) {
            return MediaType.OCTET_STREAM;
        }
        input.mark(this.bytesToTest);
        try {
            final TextStatistics stats = new TextStatistics();
            final byte[] buffer = new byte[1024];
            for (int n = 0, m = input.read(buffer, 0, Math.min(this.bytesToTest, buffer.length)); m != -1 && n < this.bytesToTest; n += m, m = input.read(buffer, 0, Math.min(this.bytesToTest - n, buffer.length))) {
                stats.addData(buffer, 0, m);
            }
            if (stats.isMostlyAscii() || stats.looksLikeUTF8()) {
                return MediaType.TEXT_PLAIN;
            }
            return MediaType.OCTET_STREAM;
        }
        finally {
            input.reset();
        }
    }
    
    static {
        Arrays.fill(IS_CONTROL_BYTE = new boolean[32], true);
        TextDetector.IS_CONTROL_BYTE[9] = false;
        TextDetector.IS_CONTROL_BYTE[10] = false;
        TextDetector.IS_CONTROL_BYTE[12] = false;
        TextDetector.IS_CONTROL_BYTE[13] = false;
        TextDetector.IS_CONTROL_BYTE[27] = false;
    }
}
