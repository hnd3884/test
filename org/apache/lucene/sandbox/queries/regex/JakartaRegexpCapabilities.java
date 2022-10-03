package org.apache.lucene.sandbox.queries.regex;

import org.apache.lucene.util.BytesRef;
import org.apache.regexp.CharacterIterator;
import org.apache.lucene.util.CharsRefBuilder;
import org.apache.regexp.RE;
import java.security.AccessController;
import org.apache.lucene.util.SuppressForbidden;
import org.apache.regexp.REProgram;
import java.security.PrivilegedAction;
import java.lang.reflect.Field;

@Deprecated
public class JakartaRegexpCapabilities implements RegexCapabilities
{
    private static Field prefixField;
    private int flags;
    public static final int FLAG_MATCH_NORMAL = 0;
    public static final int FLAG_MATCH_CASEINDEPENDENT = 1;
    
    public JakartaRegexpCapabilities() {
        this.flags = 0;
    }
    
    public JakartaRegexpCapabilities(final int flags) {
        this.flags = 0;
        this.flags = flags;
    }
    
    @Override
    public RegexMatcher compile(final String regex) {
        return new JakartaRegexMatcher(regex, this.flags);
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
        final JakartaRegexpCapabilities other = (JakartaRegexpCapabilities)obj;
        return this.flags == other.flags;
    }
    
    static {
        JakartaRegexpCapabilities.prefixField = AccessController.doPrivileged((PrivilegedAction<Field>)new PrivilegedAction<Field>() {
            @SuppressForbidden(reason = "This class needs to access to the 'prefix' private field in Jakarta's REProgram. This class will be removed in Lucene 6.")
            @Override
            public Field run() {
                try {
                    final Field f = REProgram.class.getDeclaredField("prefix");
                    f.setAccessible(true);
                    return f;
                }
                catch (final Exception e) {
                    return null;
                }
            }
        });
    }
    
    class JakartaRegexMatcher implements RegexMatcher
    {
        private RE regexp;
        private final CharsRefBuilder utf16;
        private final CharacterIterator utf16wrapper;
        
        public JakartaRegexMatcher(final String regex, final int flags) {
            this.utf16 = new CharsRefBuilder();
            this.utf16wrapper = (CharacterIterator)new CharacterIterator() {
                public char charAt(final int pos) {
                    return JakartaRegexMatcher.this.utf16.charAt(pos);
                }
                
                public boolean isEnd(final int pos) {
                    return pos >= JakartaRegexMatcher.this.utf16.length();
                }
                
                public String substring(final int beginIndex) {
                    return this.substring(beginIndex, JakartaRegexMatcher.this.utf16.length());
                }
                
                public String substring(final int beginIndex, final int endIndex) {
                    return new String(JakartaRegexMatcher.this.utf16.chars(), beginIndex, endIndex - beginIndex);
                }
            };
            this.regexp = new RE(regex, flags);
        }
        
        @Override
        public boolean match(final BytesRef term) {
            this.utf16.copyUTF8Bytes(term);
            return this.regexp.match(this.utf16wrapper, 0);
        }
        
        @Override
        public String prefix() {
            try {
                if (JakartaRegexpCapabilities.prefixField != null) {
                    final char[] prefix = (char[])JakartaRegexpCapabilities.prefixField.get(this.regexp.getProgram());
                    return (prefix == null) ? null : new String(prefix);
                }
                return null;
            }
            catch (final Exception e) {
                return null;
            }
        }
    }
}
