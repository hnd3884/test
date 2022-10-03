package org.apache.lucene.sandbox.queries.regex;

import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.CharsRefBuilder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Deprecated
public class JavaUtilRegexCapabilities implements RegexCapabilities
{
    private int flags;
    public static final int FLAG_CANON_EQ = 128;
    public static final int FLAG_CASE_INSENSITIVE = 2;
    public static final int FLAG_COMMENTS = 4;
    public static final int FLAG_DOTALL = 32;
    public static final int FLAG_LITERAL = 16;
    public static final int FLAG_MULTILINE = 8;
    public static final int FLAG_UNICODE_CASE = 64;
    public static final int FLAG_UNIX_LINES = 1;
    
    public JavaUtilRegexCapabilities() {
        this.flags = 0;
        this.flags = 0;
    }
    
    public JavaUtilRegexCapabilities(final int flags) {
        this.flags = 0;
        this.flags = flags;
    }
    
    @Override
    public RegexMatcher compile(final String regex) {
        return new JavaUtilRegexMatcher(regex, this.flags);
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + this.flags;
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final JavaUtilRegexCapabilities other = (JavaUtilRegexCapabilities)obj;
        return this.flags == other.flags;
    }
    
    class JavaUtilRegexMatcher implements RegexMatcher
    {
        private final Pattern pattern;
        private final Matcher matcher;
        private final CharsRefBuilder utf16;
        
        public JavaUtilRegexMatcher(final String regex, final int flags) {
            this.utf16 = new CharsRefBuilder();
            this.pattern = Pattern.compile(regex, flags);
            this.matcher = this.pattern.matcher((CharSequence)this.utf16.get());
        }
        
        @Override
        public boolean match(final BytesRef term) {
            this.utf16.copyUTF8Bytes(term);
            this.utf16.get();
            return this.matcher.reset().matches();
        }
        
        @Override
        public String prefix() {
            return null;
        }
    }
}
