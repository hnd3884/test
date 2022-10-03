package org.apache.commons.net.ftp.parser;

import java.util.regex.Matcher;
import java.util.regex.MatchResult;
import java.util.ListIterator;
import java.util.HashMap;
import java.util.List;
import java.util.regex.PatternSyntaxException;
import org.apache.commons.net.ftp.FTPClientConfig;
import java.util.regex.Pattern;

public class VMSVersioningFTPEntryParser extends VMSFTPEntryParser
{
    private final Pattern _preparse_pattern_;
    private static final String PRE_PARSE_REGEX = "(.*?);([0-9]+)\\s*.*";
    
    public VMSVersioningFTPEntryParser() {
        this((FTPClientConfig)null);
    }
    
    public VMSVersioningFTPEntryParser(final FTPClientConfig config) {
        this.configure(config);
        try {
            this._preparse_pattern_ = Pattern.compile("(.*?);([0-9]+)\\s*.*");
        }
        catch (final PatternSyntaxException pse) {
            throw new IllegalArgumentException("Unparseable regex supplied:  (.*?);([0-9]+)\\s*.*");
        }
    }
    
    @Override
    public List<String> preParse(final List<String> original) {
        final HashMap<String, Integer> existingEntries = new HashMap<String, Integer>();
        final ListIterator<String> iter = original.listIterator();
        while (iter.hasNext()) {
            final String entry = iter.next().trim();
            MatchResult result = null;
            final Matcher _preparse_matcher_ = this._preparse_pattern_.matcher(entry);
            if (_preparse_matcher_.matches()) {
                result = _preparse_matcher_.toMatchResult();
                final String name = result.group(1);
                final String version = result.group(2);
                final Integer nv = Integer.valueOf(version);
                final Integer existing = existingEntries.get(name);
                if (null != existing && nv < existing) {
                    iter.remove();
                }
                else {
                    existingEntries.put(name, nv);
                }
            }
        }
        while (iter.hasPrevious()) {
            final String entry = iter.previous().trim();
            MatchResult result = null;
            final Matcher _preparse_matcher_ = this._preparse_pattern_.matcher(entry);
            if (_preparse_matcher_.matches()) {
                result = _preparse_matcher_.toMatchResult();
                final String name = result.group(1);
                final String version = result.group(2);
                final Integer nv = Integer.valueOf(version);
                final Integer existing = existingEntries.get(name);
                if (null == existing || nv >= existing) {
                    continue;
                }
                iter.remove();
            }
        }
        return original;
    }
    
    @Override
    protected boolean isVersioning() {
        return true;
    }
}
