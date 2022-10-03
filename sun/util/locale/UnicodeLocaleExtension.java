package sun.util.locale;

import java.util.Iterator;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class UnicodeLocaleExtension extends Extension
{
    public static final char SINGLETON = 'u';
    private final Set<String> attributes;
    private final Map<String, String> keywords;
    public static final UnicodeLocaleExtension CA_JAPANESE;
    public static final UnicodeLocaleExtension NU_THAI;
    
    private UnicodeLocaleExtension(final String s, final String s2) {
        super('u', s + "-" + s2);
        this.attributes = Collections.emptySet();
        this.keywords = Collections.singletonMap(s, s2);
    }
    
    UnicodeLocaleExtension(final SortedSet<String> attributes, final SortedMap<String, String> keywords) {
        super('u');
        if (attributes != null) {
            this.attributes = attributes;
        }
        else {
            this.attributes = Collections.emptySet();
        }
        if (keywords != null) {
            this.keywords = keywords;
        }
        else {
            this.keywords = Collections.emptyMap();
        }
        if (!this.attributes.isEmpty() || !this.keywords.isEmpty()) {
            final StringBuilder sb = new StringBuilder();
            final Iterator<String> iterator = this.attributes.iterator();
            while (iterator.hasNext()) {
                sb.append("-").append(iterator.next());
            }
            for (final Map.Entry entry : this.keywords.entrySet()) {
                final String s = (String)entry.getKey();
                final String s2 = (String)entry.getValue();
                sb.append("-").append(s);
                if (s2.length() > 0) {
                    sb.append("-").append(s2);
                }
            }
            this.setValue(sb.substring(1));
        }
    }
    
    public Set<String> getUnicodeLocaleAttributes() {
        if (this.attributes == Collections.EMPTY_SET) {
            return this.attributes;
        }
        return Collections.unmodifiableSet((Set<? extends String>)this.attributes);
    }
    
    public Set<String> getUnicodeLocaleKeys() {
        if (this.keywords == Collections.EMPTY_MAP) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet((Set<? extends String>)this.keywords.keySet());
    }
    
    public String getUnicodeLocaleType(final String s) {
        return this.keywords.get(s);
    }
    
    public static boolean isSingletonChar(final char c) {
        return 'u' == LocaleUtils.toLower(c);
    }
    
    public static boolean isAttribute(final String s) {
        final int length = s.length();
        return length >= 3 && length <= 8 && LocaleUtils.isAlphaNumericString(s);
    }
    
    public static boolean isKey(final String s) {
        return s.length() == 2 && LocaleUtils.isAlphaNumericString(s);
    }
    
    public static boolean isTypeSubtag(final String s) {
        final int length = s.length();
        return length >= 3 && length <= 8 && LocaleUtils.isAlphaNumericString(s);
    }
    
    static {
        CA_JAPANESE = new UnicodeLocaleExtension("ca", "japanese");
        NU_THAI = new UnicodeLocaleExtension("nu", "thai");
    }
}
