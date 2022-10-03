package sun.util.locale;

import java.util.SortedSet;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeSet;
import java.util.TreeMap;
import java.util.Set;
import java.util.Collections;
import java.util.Map;

public class LocaleExtensions
{
    private final Map<Character, Extension> extensionMap;
    private final String id;
    public static final LocaleExtensions CALENDAR_JAPANESE;
    public static final LocaleExtensions NUMBER_THAI;
    
    private LocaleExtensions(final String id, final Character c, final Extension extension) {
        this.id = id;
        this.extensionMap = Collections.singletonMap(c, extension);
    }
    
    LocaleExtensions(final Map<InternalLocaleBuilder.CaseInsensitiveChar, String> map, final Set<InternalLocaleBuilder.CaseInsensitiveString> set, final Map<InternalLocaleBuilder.CaseInsensitiveString, String> map2) {
        final boolean b = !LocaleUtils.isEmpty(map);
        final boolean b2 = !LocaleUtils.isEmpty(set);
        final boolean b3 = !LocaleUtils.isEmpty(map2);
        if (!b && !b2 && !b3) {
            this.id = "";
            this.extensionMap = Collections.emptyMap();
            return;
        }
        final TreeMap extensionMap = new TreeMap();
        if (b) {
            for (final Map.Entry entry : map.entrySet()) {
                final char lower = LocaleUtils.toLower(((InternalLocaleBuilder.CaseInsensitiveChar)entry.getKey()).value());
                String removePrivateuseVariant = (String)entry.getValue();
                if (LanguageTag.isPrivateusePrefixChar(lower)) {
                    removePrivateuseVariant = InternalLocaleBuilder.removePrivateuseVariant(removePrivateuseVariant);
                    if (removePrivateuseVariant == null) {
                        continue;
                    }
                }
                extensionMap.put(lower, new Extension(lower, LocaleUtils.toLowerString(removePrivateuseVariant)));
            }
        }
        if (b2 || b3) {
            SortedSet<String> set2 = null;
            SortedMap<String, String> sortedMap = null;
            if (b2) {
                set2 = new TreeSet<String>();
                final Iterator<InternalLocaleBuilder.CaseInsensitiveString> iterator2 = set.iterator();
                while (iterator2.hasNext()) {
                    set2.add(LocaleUtils.toLowerString(iterator2.next().value()));
                }
            }
            if (b3) {
                sortedMap = new TreeMap<String, String>();
                for (final Map.Entry entry2 : map2.entrySet()) {
                    sortedMap.put(LocaleUtils.toLowerString(((InternalLocaleBuilder.CaseInsensitiveString)entry2.getKey()).value()), LocaleUtils.toLowerString((String)entry2.getValue()));
                }
            }
            extensionMap.put('u', new UnicodeLocaleExtension(set2, sortedMap));
        }
        if (extensionMap.isEmpty()) {
            this.id = "";
            this.extensionMap = Collections.emptyMap();
        }
        else {
            this.id = toID(extensionMap);
            this.extensionMap = extensionMap;
        }
    }
    
    public Set<Character> getKeys() {
        if (this.extensionMap.isEmpty()) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet((Set<? extends Character>)this.extensionMap.keySet());
    }
    
    public Extension getExtension(final Character c) {
        return this.extensionMap.get(LocaleUtils.toLower(c));
    }
    
    public String getExtensionValue(final Character c) {
        final Extension extension = this.extensionMap.get(LocaleUtils.toLower(c));
        if (extension == null) {
            return null;
        }
        return extension.getValue();
    }
    
    public Set<String> getUnicodeLocaleAttributes() {
        final Extension extension = this.extensionMap.get('u');
        if (extension == null) {
            return Collections.emptySet();
        }
        assert extension instanceof UnicodeLocaleExtension;
        return ((UnicodeLocaleExtension)extension).getUnicodeLocaleAttributes();
    }
    
    public Set<String> getUnicodeLocaleKeys() {
        final Extension extension = this.extensionMap.get('u');
        if (extension == null) {
            return Collections.emptySet();
        }
        assert extension instanceof UnicodeLocaleExtension;
        return ((UnicodeLocaleExtension)extension).getUnicodeLocaleKeys();
    }
    
    public String getUnicodeLocaleType(final String s) {
        final Extension extension = this.extensionMap.get('u');
        if (extension == null) {
            return null;
        }
        assert extension instanceof UnicodeLocaleExtension;
        return ((UnicodeLocaleExtension)extension).getUnicodeLocaleType(LocaleUtils.toLowerString(s));
    }
    
    public boolean isEmpty() {
        return this.extensionMap.isEmpty();
    }
    
    public static boolean isValidKey(final char c) {
        return LanguageTag.isExtensionSingletonChar(c) || LanguageTag.isPrivateusePrefixChar(c);
    }
    
    public static boolean isValidUnicodeLocaleKey(final String s) {
        return UnicodeLocaleExtension.isKey(s);
    }
    
    private static String toID(final SortedMap<Character, Extension> sortedMap) {
        final StringBuilder sb = new StringBuilder();
        Object o = null;
        for (final Map.Entry entry : sortedMap.entrySet()) {
            final char charValue = (char)entry.getKey();
            final Extension extension = (Extension)entry.getValue();
            if (LanguageTag.isPrivateusePrefixChar(charValue)) {
                o = extension;
            }
            else {
                if (sb.length() > 0) {
                    sb.append("-");
                }
                sb.append(extension);
            }
        }
        if (o != null) {
            if (sb.length() > 0) {
                sb.append("-");
            }
            sb.append(o);
        }
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return this.id;
    }
    
    public String getID() {
        return this.id;
    }
    
    @Override
    public int hashCode() {
        return this.id.hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        return this == o || (o instanceof LocaleExtensions && this.id.equals(((LocaleExtensions)o).id));
    }
    
    static {
        CALENDAR_JAPANESE = new LocaleExtensions("u-ca-japanese", 'u', UnicodeLocaleExtension.CA_JAPANESE);
        NUMBER_THAI = new LocaleExtensions("u-nu-thai", 'u', UnicodeLocaleExtension.NU_THAI);
    }
}
