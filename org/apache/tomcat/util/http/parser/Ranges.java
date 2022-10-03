package org.apache.tomcat.util.http.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collections;
import java.util.List;

public class Ranges
{
    private final String units;
    private final List<Entry> entries;
    
    private Ranges(final String units, final List<Entry> entries) {
        this.units = units;
        this.entries = Collections.unmodifiableList((List<? extends Entry>)entries);
    }
    
    public List<Entry> getEntries() {
        return this.entries;
    }
    
    public String getUnits() {
        return this.units;
    }
    
    public static Ranges parse(final StringReader input) throws IOException {
        final String units = HttpParser.readToken(input);
        if (units == null || units.length() == 0) {
            return null;
        }
        if (HttpParser.skipConstant(input, "=") == SkipResult.NOT_FOUND) {
            return null;
        }
        final List<Entry> entries = new ArrayList<Entry>();
        SkipResult skipResult;
        do {
            final long start = HttpParser.readLong(input);
            if (HttpParser.skipConstant(input, "-") == SkipResult.NOT_FOUND) {
                return null;
            }
            final long end = HttpParser.readLong(input);
            if (start == -1L && end == -1L) {
                return null;
            }
            entries.add(new Entry(start, end));
            skipResult = HttpParser.skipConstant(input, ",");
            if (skipResult == SkipResult.NOT_FOUND) {
                return null;
            }
        } while (skipResult == SkipResult.FOUND);
        if (entries.size() == 0) {
            return null;
        }
        return new Ranges(units, entries);
    }
    
    public static class Entry
    {
        private final long start;
        private final long end;
        
        public Entry(final long start, final long end) {
            this.start = start;
            this.end = end;
        }
        
        public long getStart() {
            return this.start;
        }
        
        public long getEnd() {
            return this.end;
        }
    }
}
