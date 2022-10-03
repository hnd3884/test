package org.apache.lucene.analysis.charfilter;

import java.io.Reader;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.Set;
import org.apache.lucene.analysis.util.CharFilterFactory;

public class HTMLStripCharFilterFactory extends CharFilterFactory
{
    final Set<String> escapedTags;
    static final Pattern TAG_NAME_PATTERN;
    
    public HTMLStripCharFilterFactory(final Map<String, String> args) {
        super(args);
        this.escapedTags = this.getSet(args, "escapedTags");
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }
    
    public HTMLStripCharFilter create(final Reader input) {
        HTMLStripCharFilter charFilter;
        if (null == this.escapedTags) {
            charFilter = new HTMLStripCharFilter(input);
        }
        else {
            charFilter = new HTMLStripCharFilter(input, this.escapedTags);
        }
        return charFilter;
    }
    
    static {
        TAG_NAME_PATTERN = Pattern.compile("[^\\s,]+");
    }
}
