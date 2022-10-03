package org.apache.tomcat.util.http.parser;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.io.StringReader;

public class AcceptEncoding
{
    private final String encoding;
    private final double quality;
    
    protected AcceptEncoding(final String encoding, final double quality) {
        this.encoding = encoding;
        this.quality = quality;
    }
    
    public String getEncoding() {
        return this.encoding;
    }
    
    public double getQuality() {
        return this.quality;
    }
    
    public static List<AcceptEncoding> parse(final StringReader input) throws IOException {
        final List<AcceptEncoding> result = new ArrayList<AcceptEncoding>();
        while (true) {
            final String encoding = HttpParser.readToken(input);
            if (encoding == null) {
                HttpParser.skipUntil(input, 0, ',');
            }
            else {
                if (encoding.length() == 0) {
                    break;
                }
                double quality = 1.0;
                final SkipResult lookForSemiColon = HttpParser.skipConstant(input, ";");
                if (lookForSemiColon == SkipResult.FOUND) {
                    quality = HttpParser.readWeight(input, ',');
                }
                if (quality <= 0.0) {
                    continue;
                }
                result.add(new AcceptEncoding(encoding, quality));
            }
        }
        return result;
    }
}
