package org.apache.tika.detect;

import java.util.Iterator;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import org.apache.tika.metadata.Metadata;
import java.io.InputStream;
import org.apache.tika.mime.MediaType;
import java.util.regex.Pattern;
import java.util.Map;

public class NameDetector implements Detector
{
    private final Map<Pattern, MediaType> patterns;
    
    public NameDetector(final Map<Pattern, MediaType> patterns) {
        this.patterns = patterns;
    }
    
    @Override
    public MediaType detect(final InputStream input, final Metadata metadata) {
        String name = metadata.get("resourceName");
        if (name != null) {
            final int question = name.indexOf(63);
            if (question != -1) {
                name = name.substring(0, question);
            }
            final int slash = name.lastIndexOf(47);
            if (slash != -1) {
                name = name.substring(slash + 1);
            }
            final int backslash = name.lastIndexOf(92);
            if (backslash != -1) {
                name = name.substring(backslash + 1);
            }
            final int hash = name.lastIndexOf(35);
            final int dot = name.indexOf(46);
            if (hash != -1 && (dot == -1 || hash > dot)) {
                name = name.substring(0, hash);
            }
            final int percent = name.indexOf(37);
            if (percent != -1) {
                try {
                    name = URLDecoder.decode(name, StandardCharsets.UTF_8.name());
                }
                catch (final UnsupportedEncodingException e) {
                    throw new IllegalStateException("UTF-8 not supported", e);
                }
            }
            name = name.trim();
            if (name.length() > 0) {
                for (final Pattern pattern : this.patterns.keySet()) {
                    if (pattern.matcher(name).matches()) {
                        return this.patterns.get(pattern);
                    }
                }
            }
        }
        return MediaType.OCTET_STREAM;
    }
}
