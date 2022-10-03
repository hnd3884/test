package org.apache.tomcat.util.http.parser;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

public class ContentRange
{
    private final String units;
    private final long start;
    private final long end;
    private final long length;
    
    public ContentRange(final String units, final long start, final long end, final long length) {
        this.units = units;
        this.start = start;
        this.end = end;
        this.length = length;
    }
    
    public String getUnits() {
        return this.units;
    }
    
    public long getStart() {
        return this.start;
    }
    
    public long getEnd() {
        return this.end;
    }
    
    public long getLength() {
        return this.length;
    }
    
    public static ContentRange parse(final StringReader input) throws IOException {
        final String units = HttpParser.readToken(input);
        if (units == null || units.length() == 0) {
            return null;
        }
        final long start = HttpParser.readLong(input);
        if (HttpParser.skipConstant(input, "-") == SkipResult.NOT_FOUND) {
            return null;
        }
        final long end = HttpParser.readLong(input);
        if (HttpParser.skipConstant(input, "/") == SkipResult.NOT_FOUND) {
            return null;
        }
        final long length = HttpParser.readLong(input);
        final SkipResult skipResult = HttpParser.skipConstant(input, "X");
        if (skipResult != SkipResult.EOF) {
            return null;
        }
        return new ContentRange(units, start, end, length);
    }
}
