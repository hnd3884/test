package sun.util.locale;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;

public final class InternalLocaleBuilder
{
    private static final CaseInsensitiveChar PRIVATEUSE_KEY;
    private String language;
    private String script;
    private String region;
    private String variant;
    private Map<CaseInsensitiveChar, String> extensions;
    private Set<CaseInsensitiveString> uattributes;
    private Map<CaseInsensitiveString, String> ukeywords;
    
    public InternalLocaleBuilder() {
        this.language = "";
        this.script = "";
        this.region = "";
        this.variant = "";
    }
    
    public InternalLocaleBuilder setLanguage(final String language) throws LocaleSyntaxException {
        if (LocaleUtils.isEmpty(language)) {
            this.language = "";
        }
        else {
            if (!LanguageTag.isLanguage(language)) {
                throw new LocaleSyntaxException("Ill-formed language: " + language, 0);
            }
            this.language = language;
        }
        return this;
    }
    
    public InternalLocaleBuilder setScript(final String script) throws LocaleSyntaxException {
        if (LocaleUtils.isEmpty(script)) {
            this.script = "";
        }
        else {
            if (!LanguageTag.isScript(script)) {
                throw new LocaleSyntaxException("Ill-formed script: " + script, 0);
            }
            this.script = script;
        }
        return this;
    }
    
    public InternalLocaleBuilder setRegion(final String region) throws LocaleSyntaxException {
        if (LocaleUtils.isEmpty(region)) {
            this.region = "";
        }
        else {
            if (!LanguageTag.isRegion(region)) {
                throw new LocaleSyntaxException("Ill-formed region: " + region, 0);
            }
            this.region = region;
        }
        return this;
    }
    
    public InternalLocaleBuilder setVariant(final String s) throws LocaleSyntaxException {
        if (LocaleUtils.isEmpty(s)) {
            this.variant = "";
        }
        else {
            final String replaceAll = s.replaceAll("-", "_");
            final int checkVariants = this.checkVariants(replaceAll, "_");
            if (checkVariants != -1) {
                throw new LocaleSyntaxException("Ill-formed variant: " + s, checkVariants);
            }
            this.variant = replaceAll;
        }
        return this;
    }
    
    public InternalLocaleBuilder addUnicodeLocaleAttribute(final String s) throws LocaleSyntaxException {
        if (!UnicodeLocaleExtension.isAttribute(s)) {
            throw new LocaleSyntaxException("Ill-formed Unicode locale attribute: " + s);
        }
        if (this.uattributes == null) {
            this.uattributes = new HashSet<CaseInsensitiveString>(4);
        }
        this.uattributes.add(new CaseInsensitiveString(s));
        return this;
    }
    
    public InternalLocaleBuilder removeUnicodeLocaleAttribute(final String s) throws LocaleSyntaxException {
        if (s == null || !UnicodeLocaleExtension.isAttribute(s)) {
            throw new LocaleSyntaxException("Ill-formed Unicode locale attribute: " + s);
        }
        if (this.uattributes != null) {
            this.uattributes.remove(new CaseInsensitiveString(s));
        }
        return this;
    }
    
    public InternalLocaleBuilder setUnicodeLocaleKeyword(final String s, final String s2) throws LocaleSyntaxException {
        if (!UnicodeLocaleExtension.isKey(s)) {
            throw new LocaleSyntaxException("Ill-formed Unicode locale keyword key: " + s);
        }
        final CaseInsensitiveString caseInsensitiveString = new CaseInsensitiveString(s);
        if (s2 == null) {
            if (this.ukeywords != null) {
                this.ukeywords.remove(caseInsensitiveString);
            }
        }
        else {
            if (s2.length() != 0) {
                final StringTokenIterator stringTokenIterator = new StringTokenIterator(s2.replaceAll("_", "-"), "-");
                while (!stringTokenIterator.isDone()) {
                    if (!UnicodeLocaleExtension.isTypeSubtag(stringTokenIterator.current())) {
                        throw new LocaleSyntaxException("Ill-formed Unicode locale keyword type: " + s2, stringTokenIterator.currentStart());
                    }
                    stringTokenIterator.next();
                }
            }
            if (this.ukeywords == null) {
                this.ukeywords = new HashMap<CaseInsensitiveString, String>(4);
            }
            this.ukeywords.put(caseInsensitiveString, s2);
        }
        return this;
    }
    
    public InternalLocaleBuilder setExtension(final char c, final String s) throws LocaleSyntaxException {
        final boolean privateusePrefixChar = LanguageTag.isPrivateusePrefixChar(c);
        if (!privateusePrefixChar && !LanguageTag.isExtensionSingletonChar(c)) {
            throw new LocaleSyntaxException("Ill-formed extension key: " + c);
        }
        final boolean empty = LocaleUtils.isEmpty(s);
        final CaseInsensitiveChar caseInsensitiveChar = new CaseInsensitiveChar(c);
        if (empty) {
            if (UnicodeLocaleExtension.isSingletonChar(caseInsensitiveChar.value())) {
                if (this.uattributes != null) {
                    this.uattributes.clear();
                }
                if (this.ukeywords != null) {
                    this.ukeywords.clear();
                }
            }
            else if (this.extensions != null && this.extensions.containsKey(caseInsensitiveChar)) {
                this.extensions.remove(caseInsensitiveChar);
            }
        }
        else {
            final String replaceAll = s.replaceAll("_", "-");
            final StringTokenIterator stringTokenIterator = new StringTokenIterator(replaceAll, "-");
            while (!stringTokenIterator.isDone()) {
                final String current = stringTokenIterator.current();
                boolean b;
                if (privateusePrefixChar) {
                    b = LanguageTag.isPrivateuseSubtag(current);
                }
                else {
                    b = LanguageTag.isExtensionSubtag(current);
                }
                if (!b) {
                    throw new LocaleSyntaxException("Ill-formed extension value: " + current, stringTokenIterator.currentStart());
                }
                stringTokenIterator.next();
            }
            if (UnicodeLocaleExtension.isSingletonChar(caseInsensitiveChar.value())) {
                this.setUnicodeLocaleExtension(replaceAll);
            }
            else {
                if (this.extensions == null) {
                    this.extensions = new HashMap<CaseInsensitiveChar, String>(4);
                }
                this.extensions.put(caseInsensitiveChar, replaceAll);
            }
        }
        return this;
    }
    
    public InternalLocaleBuilder setExtensions(String replaceAll) throws LocaleSyntaxException {
        if (LocaleUtils.isEmpty(replaceAll)) {
            this.clearExtensions();
            return this;
        }
        replaceAll = replaceAll.replaceAll("_", "-");
        final StringTokenIterator stringTokenIterator = new StringTokenIterator(replaceAll, "-");
        List<String> list = null;
        String string = null;
        int n = 0;
        while (!stringTokenIterator.isDone()) {
            final String current = stringTokenIterator.current();
            if (!LanguageTag.isExtensionSingleton(current)) {
                break;
            }
            final int currentStart = stringTokenIterator.currentStart();
            final String s = current;
            final StringBuilder sb = new StringBuilder(s);
            stringTokenIterator.next();
            while (!stringTokenIterator.isDone()) {
                final String current2 = stringTokenIterator.current();
                if (!LanguageTag.isExtensionSubtag(current2)) {
                    break;
                }
                sb.append("-").append(current2);
                n = stringTokenIterator.currentEnd();
                stringTokenIterator.next();
            }
            if (n < currentStart) {
                throw new LocaleSyntaxException("Incomplete extension '" + s + "'", currentStart);
            }
            if (list == null) {
                list = new ArrayList<String>(4);
            }
            list.add(sb.toString());
        }
        if (!stringTokenIterator.isDone()) {
            final String current3 = stringTokenIterator.current();
            if (LanguageTag.isPrivateusePrefix(current3)) {
                final int currentStart2 = stringTokenIterator.currentStart();
                final StringBuilder sb2 = new StringBuilder(current3);
                stringTokenIterator.next();
                while (!stringTokenIterator.isDone()) {
                    final String current4 = stringTokenIterator.current();
                    if (!LanguageTag.isPrivateuseSubtag(current4)) {
                        break;
                    }
                    sb2.append("-").append(current4);
                    n = stringTokenIterator.currentEnd();
                    stringTokenIterator.next();
                }
                if (n <= currentStart2) {
                    throw new LocaleSyntaxException("Incomplete privateuse:" + replaceAll.substring(currentStart2), currentStart2);
                }
                string = sb2.toString();
            }
        }
        if (!stringTokenIterator.isDone()) {
            throw new LocaleSyntaxException("Ill-formed extension subtags:" + replaceAll.substring(stringTokenIterator.currentStart()), stringTokenIterator.currentStart());
        }
        return this.setExtensions(list, string);
    }
    
    private InternalLocaleBuilder setExtensions(final List<String> list, final String s) {
        this.clearExtensions();
        if (!LocaleUtils.isEmpty(list)) {
            final HashSet set = new HashSet(list.size());
            for (final String s2 : list) {
                final CaseInsensitiveChar caseInsensitiveChar = new CaseInsensitiveChar(s2);
                if (!set.contains(caseInsensitiveChar)) {
                    if (UnicodeLocaleExtension.isSingletonChar(caseInsensitiveChar.value())) {
                        this.setUnicodeLocaleExtension(s2.substring(2));
                    }
                    else {
                        if (this.extensions == null) {
                            this.extensions = new HashMap<CaseInsensitiveChar, String>(4);
                        }
                        this.extensions.put(caseInsensitiveChar, s2.substring(2));
                    }
                }
                set.add(caseInsensitiveChar);
            }
        }
        if (s != null && s.length() > 0) {
            if (this.extensions == null) {
                this.extensions = new HashMap<CaseInsensitiveChar, String>(1);
            }
            this.extensions.put(new CaseInsensitiveChar(s), s.substring(2));
        }
        return this;
    }
    
    public InternalLocaleBuilder setLanguageTag(final LanguageTag languageTag) {
        this.clear();
        if (!languageTag.getExtlangs().isEmpty()) {
            this.language = languageTag.getExtlangs().get(0);
        }
        else {
            final String language = languageTag.getLanguage();
            if (!language.equals("und")) {
                this.language = language;
            }
        }
        this.script = languageTag.getScript();
        this.region = languageTag.getRegion();
        final List<String> variants = languageTag.getVariants();
        if (!variants.isEmpty()) {
            final StringBuilder sb = new StringBuilder(variants.get(0));
            for (int size = variants.size(), i = 1; i < size; ++i) {
                sb.append("_").append((String)variants.get(i));
            }
            this.variant = sb.toString();
        }
        this.setExtensions(languageTag.getExtensions(), languageTag.getPrivateuse());
        return this;
    }
    
    public InternalLocaleBuilder setLocale(final BaseLocale baseLocale, final LocaleExtensions localeExtensions) throws LocaleSyntaxException {
        String language = baseLocale.getLanguage();
        final String script = baseLocale.getScript();
        final String region = baseLocale.getRegion();
        String variant = baseLocale.getVariant();
        if (language.equals("ja") && region.equals("JP") && variant.equals("JP")) {
            assert "japanese".equals(localeExtensions.getUnicodeLocaleType("ca"));
            variant = "";
        }
        else if (language.equals("th") && region.equals("TH") && variant.equals("TH")) {
            assert "thai".equals(localeExtensions.getUnicodeLocaleType("nu"));
            variant = "";
        }
        else if (language.equals("no") && region.equals("NO") && variant.equals("NY")) {
            language = "nn";
            variant = "";
        }
        if (language.length() > 0 && !LanguageTag.isLanguage(language)) {
            throw new LocaleSyntaxException("Ill-formed language: " + language);
        }
        if (script.length() > 0 && !LanguageTag.isScript(script)) {
            throw new LocaleSyntaxException("Ill-formed script: " + script);
        }
        if (region.length() > 0 && !LanguageTag.isRegion(region)) {
            throw new LocaleSyntaxException("Ill-formed region: " + region);
        }
        if (variant.length() > 0) {
            final int checkVariants = this.checkVariants(variant, "_");
            if (checkVariants != -1) {
                throw new LocaleSyntaxException("Ill-formed variant: " + variant, checkVariants);
            }
        }
        this.language = language;
        this.script = script;
        this.region = region;
        this.variant = variant;
        this.clearExtensions();
        final Set<Character> set = (localeExtensions == null) ? null : localeExtensions.getKeys();
        if (set != null) {
            for (final Character c : set) {
                final Extension extension = localeExtensions.getExtension(c);
                if (extension instanceof UnicodeLocaleExtension) {
                    final UnicodeLocaleExtension unicodeLocaleExtension = (UnicodeLocaleExtension)extension;
                    for (final String s : unicodeLocaleExtension.getUnicodeLocaleAttributes()) {
                        if (this.uattributes == null) {
                            this.uattributes = new HashSet<CaseInsensitiveString>(4);
                        }
                        this.uattributes.add(new CaseInsensitiveString(s));
                    }
                    for (final String s2 : unicodeLocaleExtension.getUnicodeLocaleKeys()) {
                        if (this.ukeywords == null) {
                            this.ukeywords = new HashMap<CaseInsensitiveString, String>(4);
                        }
                        this.ukeywords.put(new CaseInsensitiveString(s2), unicodeLocaleExtension.getUnicodeLocaleType(s2));
                    }
                }
                else {
                    if (this.extensions == null) {
                        this.extensions = new HashMap<CaseInsensitiveChar, String>(4);
                    }
                    this.extensions.put(new CaseInsensitiveChar(c), extension.getValue());
                }
            }
        }
        return this;
    }
    
    public InternalLocaleBuilder clear() {
        this.language = "";
        this.script = "";
        this.region = "";
        this.variant = "";
        this.clearExtensions();
        return this;
    }
    
    public InternalLocaleBuilder clearExtensions() {
        if (this.extensions != null) {
            this.extensions.clear();
        }
        if (this.uattributes != null) {
            this.uattributes.clear();
        }
        if (this.ukeywords != null) {
            this.ukeywords.clear();
        }
        return this;
    }
    
    public BaseLocale getBaseLocale() {
        final String language = this.language;
        final String script = this.script;
        final String region = this.region;
        String s = this.variant;
        if (this.extensions != null) {
            final String s2 = this.extensions.get(InternalLocaleBuilder.PRIVATEUSE_KEY);
            if (s2 != null) {
                final StringTokenIterator stringTokenIterator = new StringTokenIterator(s2, "-");
                int n = 0;
                int currentStart = -1;
                while (!stringTokenIterator.isDone()) {
                    if (n != 0) {
                        currentStart = stringTokenIterator.currentStart();
                        break;
                    }
                    if (LocaleUtils.caseIgnoreMatch(stringTokenIterator.current(), "lvariant")) {
                        n = 1;
                    }
                    stringTokenIterator.next();
                }
                if (currentStart != -1) {
                    final StringBuilder sb = new StringBuilder(s);
                    if (sb.length() != 0) {
                        sb.append("_");
                    }
                    sb.append(s2.substring(currentStart).replaceAll("-", "_"));
                    s = sb.toString();
                }
            }
        }
        return BaseLocale.getInstance(language, script, region, s);
    }
    
    public LocaleExtensions getLocaleExtensions() {
        if (LocaleUtils.isEmpty(this.extensions) && LocaleUtils.isEmpty(this.uattributes) && LocaleUtils.isEmpty(this.ukeywords)) {
            return null;
        }
        final LocaleExtensions localeExtensions = new LocaleExtensions(this.extensions, this.uattributes, this.ukeywords);
        return localeExtensions.isEmpty() ? null : localeExtensions;
    }
    
    static String removePrivateuseVariant(final String s) {
        final StringTokenIterator stringTokenIterator = new StringTokenIterator(s, "-");
        int currentStart = -1;
        boolean b = false;
        while (!stringTokenIterator.isDone()) {
            if (currentStart != -1) {
                b = true;
                break;
            }
            if (LocaleUtils.caseIgnoreMatch(stringTokenIterator.current(), "lvariant")) {
                currentStart = stringTokenIterator.currentStart();
            }
            stringTokenIterator.next();
        }
        if (!b) {
            return s;
        }
        assert currentStart > 1;
        return (currentStart == 0) ? null : s.substring(0, currentStart - 1);
    }
    
    private int checkVariants(final String s, final String s2) {
        final StringTokenIterator stringTokenIterator = new StringTokenIterator(s, s2);
        while (!stringTokenIterator.isDone()) {
            if (!LanguageTag.isVariant(stringTokenIterator.current())) {
                return stringTokenIterator.currentStart();
            }
            stringTokenIterator.next();
        }
        return -1;
    }
    
    private void setUnicodeLocaleExtension(final String s) {
        if (this.uattributes != null) {
            this.uattributes.clear();
        }
        if (this.ukeywords != null) {
            this.ukeywords.clear();
        }
        final StringTokenIterator stringTokenIterator = new StringTokenIterator(s, "-");
        while (!stringTokenIterator.isDone() && UnicodeLocaleExtension.isAttribute(stringTokenIterator.current())) {
            if (this.uattributes == null) {
                this.uattributes = new HashSet<CaseInsensitiveString>(4);
            }
            this.uattributes.add(new CaseInsensitiveString(stringTokenIterator.current()));
            stringTokenIterator.next();
        }
        CaseInsensitiveString caseInsensitiveString = null;
        int currentStart = -1;
        int currentEnd = -1;
        while (!stringTokenIterator.isDone()) {
            if (caseInsensitiveString != null) {
                if (UnicodeLocaleExtension.isKey(stringTokenIterator.current())) {
                    assert currentEnd != -1;
                    final String s2 = (currentStart == -1) ? "" : s.substring(currentStart, currentEnd);
                    if (this.ukeywords == null) {
                        this.ukeywords = new HashMap<CaseInsensitiveString, String>(4);
                    }
                    this.ukeywords.put(caseInsensitiveString, s2);
                    final CaseInsensitiveString caseInsensitiveString2 = new CaseInsensitiveString(stringTokenIterator.current());
                    caseInsensitiveString = (this.ukeywords.containsKey(caseInsensitiveString2) ? null : caseInsensitiveString2);
                    currentEnd = (currentStart = -1);
                }
                else {
                    if (currentStart == -1) {
                        currentStart = stringTokenIterator.currentStart();
                    }
                    currentEnd = stringTokenIterator.currentEnd();
                }
            }
            else if (UnicodeLocaleExtension.isKey(stringTokenIterator.current())) {
                caseInsensitiveString = new CaseInsensitiveString(stringTokenIterator.current());
                if (this.ukeywords != null && this.ukeywords.containsKey(caseInsensitiveString)) {
                    caseInsensitiveString = null;
                }
            }
            if (!stringTokenIterator.hasNext()) {
                if (caseInsensitiveString == null) {
                    break;
                }
                assert currentEnd != -1;
                final String s3 = (currentStart == -1) ? "" : s.substring(currentStart, currentEnd);
                if (this.ukeywords == null) {
                    this.ukeywords = new HashMap<CaseInsensitiveString, String>(4);
                }
                this.ukeywords.put(caseInsensitiveString, s3);
                break;
            }
            else {
                stringTokenIterator.next();
            }
        }
    }
    
    static {
        PRIVATEUSE_KEY = new CaseInsensitiveChar("x");
    }
    
    static final class CaseInsensitiveString
    {
        private final String str;
        private final String lowerStr;
        
        CaseInsensitiveString(final String str) {
            this.str = str;
            this.lowerStr = LocaleUtils.toLowerString(str);
        }
        
        public String value() {
            return this.str;
        }
        
        @Override
        public int hashCode() {
            return this.lowerStr.hashCode();
        }
        
        @Override
        public boolean equals(final Object o) {
            return this == o || (o instanceof CaseInsensitiveString && this.lowerStr.equals(((CaseInsensitiveString)o).lowerStr));
        }
    }
    
    static final class CaseInsensitiveChar
    {
        private final char ch;
        private final char lowerCh;
        
        private CaseInsensitiveChar(final String s) {
            this(s.charAt(0));
        }
        
        CaseInsensitiveChar(final char ch) {
            this.ch = ch;
            this.lowerCh = LocaleUtils.toLower(this.ch);
        }
        
        public char value() {
            return this.ch;
        }
        
        @Override
        public int hashCode() {
            return this.lowerCh;
        }
        
        @Override
        public boolean equals(final Object o) {
            return this == o || (o instanceof CaseInsensitiveChar && this.lowerCh == ((CaseInsensitiveChar)o).lowerCh);
        }
    }
}
