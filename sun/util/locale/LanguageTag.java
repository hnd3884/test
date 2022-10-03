package sun.util.locale;

import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.List;

public class LanguageTag
{
    public static final String SEP = "-";
    public static final String PRIVATEUSE = "x";
    public static final String UNDETERMINED = "und";
    public static final String PRIVUSE_VARIANT_PREFIX = "lvariant";
    private String language;
    private String script;
    private String region;
    private String privateuse;
    private List<String> extlangs;
    private List<String> variants;
    private List<String> extensions;
    private static final Map<String, String[]> GRANDFATHERED;
    
    private LanguageTag() {
        this.language = "";
        this.script = "";
        this.region = "";
        this.privateuse = "";
        this.extlangs = Collections.emptyList();
        this.variants = Collections.emptyList();
        this.extensions = Collections.emptyList();
    }
    
    public static LanguageTag parse(final String s, ParseStatus parseStatus) {
        if (parseStatus == null) {
            parseStatus = new ParseStatus();
        }
        else {
            parseStatus.reset();
        }
        final String[] array = LanguageTag.GRANDFATHERED.get(LocaleUtils.toLowerString(s));
        StringTokenIterator stringTokenIterator;
        if (array != null) {
            stringTokenIterator = new StringTokenIterator(array[1], "-");
        }
        else {
            stringTokenIterator = new StringTokenIterator(s, "-");
        }
        final LanguageTag languageTag = new LanguageTag();
        if (languageTag.parseLanguage(stringTokenIterator, parseStatus)) {
            languageTag.parseExtlangs(stringTokenIterator, parseStatus);
            languageTag.parseScript(stringTokenIterator, parseStatus);
            languageTag.parseRegion(stringTokenIterator, parseStatus);
            languageTag.parseVariants(stringTokenIterator, parseStatus);
            languageTag.parseExtensions(stringTokenIterator, parseStatus);
        }
        languageTag.parsePrivateuse(stringTokenIterator, parseStatus);
        if (!stringTokenIterator.isDone() && !parseStatus.isError()) {
            final String current = stringTokenIterator.current();
            parseStatus.errorIndex = stringTokenIterator.currentStart();
            if (current.length() == 0) {
                parseStatus.errorMsg = "Empty subtag";
            }
            else {
                parseStatus.errorMsg = "Invalid subtag: " + current;
            }
        }
        return languageTag;
    }
    
    private boolean parseLanguage(final StringTokenIterator stringTokenIterator, final ParseStatus parseStatus) {
        if (stringTokenIterator.isDone() || parseStatus.isError()) {
            return false;
        }
        boolean b = false;
        final String current = stringTokenIterator.current();
        if (isLanguage(current)) {
            b = true;
            this.language = current;
            parseStatus.parseLength = stringTokenIterator.currentEnd();
            stringTokenIterator.next();
        }
        return b;
    }
    
    private boolean parseExtlangs(final StringTokenIterator stringTokenIterator, final ParseStatus parseStatus) {
        if (stringTokenIterator.isDone() || parseStatus.isError()) {
            return false;
        }
        boolean b = false;
        while (!stringTokenIterator.isDone()) {
            final String current = stringTokenIterator.current();
            if (!isExtlang(current)) {
                break;
            }
            b = true;
            if (this.extlangs.isEmpty()) {
                this.extlangs = new ArrayList<String>(3);
            }
            this.extlangs.add(current);
            parseStatus.parseLength = stringTokenIterator.currentEnd();
            stringTokenIterator.next();
            if (this.extlangs.size() == 3) {
                break;
            }
        }
        return b;
    }
    
    private boolean parseScript(final StringTokenIterator stringTokenIterator, final ParseStatus parseStatus) {
        if (stringTokenIterator.isDone() || parseStatus.isError()) {
            return false;
        }
        boolean b = false;
        final String current = stringTokenIterator.current();
        if (isScript(current)) {
            b = true;
            this.script = current;
            parseStatus.parseLength = stringTokenIterator.currentEnd();
            stringTokenIterator.next();
        }
        return b;
    }
    
    private boolean parseRegion(final StringTokenIterator stringTokenIterator, final ParseStatus parseStatus) {
        if (stringTokenIterator.isDone() || parseStatus.isError()) {
            return false;
        }
        boolean b = false;
        final String current = stringTokenIterator.current();
        if (isRegion(current)) {
            b = true;
            this.region = current;
            parseStatus.parseLength = stringTokenIterator.currentEnd();
            stringTokenIterator.next();
        }
        return b;
    }
    
    private boolean parseVariants(final StringTokenIterator stringTokenIterator, final ParseStatus parseStatus) {
        if (stringTokenIterator.isDone() || parseStatus.isError()) {
            return false;
        }
        boolean b = false;
        while (!stringTokenIterator.isDone()) {
            final String current = stringTokenIterator.current();
            if (!isVariant(current)) {
                break;
            }
            b = true;
            if (this.variants.isEmpty()) {
                this.variants = new ArrayList<String>(3);
            }
            this.variants.add(current);
            parseStatus.parseLength = stringTokenIterator.currentEnd();
            stringTokenIterator.next();
        }
        return b;
    }
    
    private boolean parseExtensions(final StringTokenIterator stringTokenIterator, final ParseStatus parseStatus) {
        if (stringTokenIterator.isDone() || parseStatus.isError()) {
            return false;
        }
        boolean b = false;
        while (!stringTokenIterator.isDone()) {
            final String current = stringTokenIterator.current();
            if (!isExtensionSingleton(current)) {
                break;
            }
            final int currentStart = stringTokenIterator.currentStart();
            final String s = current;
            final StringBuilder sb = new StringBuilder(s);
            stringTokenIterator.next();
            while (!stringTokenIterator.isDone()) {
                final String current2 = stringTokenIterator.current();
                if (!isExtensionSubtag(current2)) {
                    break;
                }
                sb.append("-").append(current2);
                parseStatus.parseLength = stringTokenIterator.currentEnd();
                stringTokenIterator.next();
            }
            if (parseStatus.parseLength <= currentStart) {
                parseStatus.errorIndex = currentStart;
                parseStatus.errorMsg = "Incomplete extension '" + s + "'";
                break;
            }
            if (this.extensions.isEmpty()) {
                this.extensions = new ArrayList<String>(4);
            }
            this.extensions.add(sb.toString());
            b = true;
        }
        return b;
    }
    
    private boolean parsePrivateuse(final StringTokenIterator stringTokenIterator, final ParseStatus parseStatus) {
        if (stringTokenIterator.isDone() || parseStatus.isError()) {
            return false;
        }
        boolean b = false;
        final String current = stringTokenIterator.current();
        if (isPrivateusePrefix(current)) {
            final int currentStart = stringTokenIterator.currentStart();
            final StringBuilder sb = new StringBuilder(current);
            stringTokenIterator.next();
            while (!stringTokenIterator.isDone()) {
                final String current2 = stringTokenIterator.current();
                if (!isPrivateuseSubtag(current2)) {
                    break;
                }
                sb.append("-").append(current2);
                parseStatus.parseLength = stringTokenIterator.currentEnd();
                stringTokenIterator.next();
            }
            if (parseStatus.parseLength <= currentStart) {
                parseStatus.errorIndex = currentStart;
                parseStatus.errorMsg = "Incomplete privateuse";
            }
            else {
                this.privateuse = sb.toString();
                b = true;
            }
        }
        return b;
    }
    
    public static LanguageTag parseLocale(final BaseLocale baseLocale, final LocaleExtensions localeExtensions) {
        final LanguageTag languageTag = new LanguageTag();
        String language = baseLocale.getLanguage();
        final String script = baseLocale.getScript();
        final String region = baseLocale.getRegion();
        String variant = baseLocale.getVariant();
        boolean b = false;
        String string = null;
        if (isLanguage(language)) {
            if (language.equals("iw")) {
                language = "he";
            }
            else if (language.equals("ji")) {
                language = "yi";
            }
            else if (language.equals("in")) {
                language = "id";
            }
            languageTag.language = language;
        }
        if (isScript(script)) {
            languageTag.script = canonicalizeScript(script);
            b = true;
        }
        if (isRegion(region)) {
            languageTag.region = canonicalizeRegion(region);
            b = true;
        }
        if (languageTag.language.equals("no") && languageTag.region.equals("NO") && variant.equals("NY")) {
            languageTag.language = "nn";
            variant = "";
        }
        if (variant.length() > 0) {
            List<String> variants = null;
            final StringTokenIterator stringTokenIterator = new StringTokenIterator(variant, "_");
            while (!stringTokenIterator.isDone()) {
                final String current = stringTokenIterator.current();
                if (!isVariant(current)) {
                    break;
                }
                if (variants == null) {
                    variants = new ArrayList<String>();
                }
                variants.add(current);
                stringTokenIterator.next();
            }
            if (variants != null) {
                languageTag.variants = variants;
                b = true;
            }
            if (!stringTokenIterator.isDone()) {
                final StringBuilder sb = new StringBuilder();
                while (!stringTokenIterator.isDone()) {
                    final String current2 = stringTokenIterator.current();
                    if (!isPrivateuseSubtag(current2)) {
                        break;
                    }
                    if (sb.length() > 0) {
                        sb.append("-");
                    }
                    sb.append(current2);
                    stringTokenIterator.next();
                }
                if (sb.length() > 0) {
                    string = sb.toString();
                }
            }
        }
        List<String> extensions = null;
        String privateuse = null;
        if (localeExtensions != null) {
            for (final Character c : localeExtensions.getKeys()) {
                final Extension extension = localeExtensions.getExtension(c);
                if (isPrivateusePrefixChar(c)) {
                    privateuse = extension.getValue();
                }
                else {
                    if (extensions == null) {
                        extensions = new ArrayList<String>();
                    }
                    extensions.add(c.toString() + "-" + extension.getValue());
                }
            }
        }
        if (extensions != null) {
            languageTag.extensions = extensions;
            b = true;
        }
        if (string != null) {
            if (privateuse == null) {
                privateuse = "lvariant-" + string;
            }
            else {
                privateuse = privateuse + "-" + "lvariant" + "-" + string.replace("_", "-");
            }
        }
        if (privateuse != null) {
            languageTag.privateuse = privateuse;
        }
        if (languageTag.language.length() == 0 && (b || privateuse == null)) {
            languageTag.language = "und";
        }
        return languageTag;
    }
    
    public String getLanguage() {
        return this.language;
    }
    
    public List<String> getExtlangs() {
        if (this.extlangs.isEmpty()) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList((List<? extends String>)this.extlangs);
    }
    
    public String getScript() {
        return this.script;
    }
    
    public String getRegion() {
        return this.region;
    }
    
    public List<String> getVariants() {
        if (this.variants.isEmpty()) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList((List<? extends String>)this.variants);
    }
    
    public List<String> getExtensions() {
        if (this.extensions.isEmpty()) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList((List<? extends String>)this.extensions);
    }
    
    public String getPrivateuse() {
        return this.privateuse;
    }
    
    public static boolean isLanguage(final String s) {
        final int length = s.length();
        return length >= 2 && length <= 8 && LocaleUtils.isAlphaString(s);
    }
    
    public static boolean isExtlang(final String s) {
        return s.length() == 3 && LocaleUtils.isAlphaString(s);
    }
    
    public static boolean isScript(final String s) {
        return s.length() == 4 && LocaleUtils.isAlphaString(s);
    }
    
    public static boolean isRegion(final String s) {
        return (s.length() == 2 && LocaleUtils.isAlphaString(s)) || (s.length() == 3 && LocaleUtils.isNumericString(s));
    }
    
    public static boolean isVariant(final String s) {
        final int length = s.length();
        if (length >= 5 && length <= 8) {
            return LocaleUtils.isAlphaNumericString(s);
        }
        return length == 4 && LocaleUtils.isNumeric(s.charAt(0)) && LocaleUtils.isAlphaNumeric(s.charAt(1)) && LocaleUtils.isAlphaNumeric(s.charAt(2)) && LocaleUtils.isAlphaNumeric(s.charAt(3));
    }
    
    public static boolean isExtensionSingleton(final String s) {
        return s.length() == 1 && LocaleUtils.isAlphaString(s) && !LocaleUtils.caseIgnoreMatch("x", s);
    }
    
    public static boolean isExtensionSingletonChar(final char c) {
        return isExtensionSingleton(String.valueOf(c));
    }
    
    public static boolean isExtensionSubtag(final String s) {
        final int length = s.length();
        return length >= 2 && length <= 8 && LocaleUtils.isAlphaNumericString(s);
    }
    
    public static boolean isPrivateusePrefix(final String s) {
        return s.length() == 1 && LocaleUtils.caseIgnoreMatch("x", s);
    }
    
    public static boolean isPrivateusePrefixChar(final char c) {
        return LocaleUtils.caseIgnoreMatch("x", String.valueOf(c));
    }
    
    public static boolean isPrivateuseSubtag(final String s) {
        final int length = s.length();
        return length >= 1 && length <= 8 && LocaleUtils.isAlphaNumericString(s);
    }
    
    public static String canonicalizeLanguage(final String s) {
        return LocaleUtils.toLowerString(s);
    }
    
    public static String canonicalizeExtlang(final String s) {
        return LocaleUtils.toLowerString(s);
    }
    
    public static String canonicalizeScript(final String s) {
        return LocaleUtils.toTitleString(s);
    }
    
    public static String canonicalizeRegion(final String s) {
        return LocaleUtils.toUpperString(s);
    }
    
    public static String canonicalizeVariant(final String s) {
        return LocaleUtils.toLowerString(s);
    }
    
    public static String canonicalizeExtension(final String s) {
        return LocaleUtils.toLowerString(s);
    }
    
    public static String canonicalizeExtensionSingleton(final String s) {
        return LocaleUtils.toLowerString(s);
    }
    
    public static String canonicalizeExtensionSubtag(final String s) {
        return LocaleUtils.toLowerString(s);
    }
    
    public static String canonicalizePrivateuse(final String s) {
        return LocaleUtils.toLowerString(s);
    }
    
    public static String canonicalizePrivateuseSubtag(final String s) {
        return LocaleUtils.toLowerString(s);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        if (this.language.length() > 0) {
            sb.append(this.language);
            final Iterator<String> iterator = this.extlangs.iterator();
            while (iterator.hasNext()) {
                sb.append("-").append(iterator.next());
            }
            if (this.script.length() > 0) {
                sb.append("-").append(this.script);
            }
            if (this.region.length() > 0) {
                sb.append("-").append(this.region);
            }
            final Iterator<String> iterator2 = this.variants.iterator();
            while (iterator2.hasNext()) {
                sb.append("-").append(iterator2.next());
            }
            final Iterator<String> iterator3 = this.extensions.iterator();
            while (iterator3.hasNext()) {
                sb.append("-").append(iterator3.next());
            }
        }
        if (this.privateuse.length() > 0) {
            if (sb.length() > 0) {
                sb.append("-");
            }
            sb.append(this.privateuse);
        }
        return sb.toString();
    }
    
    static {
        GRANDFATHERED = new HashMap<String, String[]>();
        for (final String[] array2 : new String[][] { { "art-lojban", "jbo" }, { "cel-gaulish", "xtg-x-cel-gaulish" }, { "en-GB-oed", "en-GB-x-oed" }, { "i-ami", "ami" }, { "i-bnn", "bnn" }, { "i-default", "en-x-i-default" }, { "i-enochian", "und-x-i-enochian" }, { "i-hak", "hak" }, { "i-klingon", "tlh" }, { "i-lux", "lb" }, { "i-mingo", "see-x-i-mingo" }, { "i-navajo", "nv" }, { "i-pwn", "pwn" }, { "i-tao", "tao" }, { "i-tay", "tay" }, { "i-tsu", "tsu" }, { "no-bok", "nb" }, { "no-nyn", "nn" }, { "sgn-BE-FR", "sfb" }, { "sgn-BE-NL", "vgt" }, { "sgn-CH-DE", "sgg" }, { "zh-guoyu", "cmn" }, { "zh-hakka", "hak" }, { "zh-min", "nan-x-zh-min" }, { "zh-min-nan", "nan" }, { "zh-xiang", "hsn" } }) {
            LanguageTag.GRANDFATHERED.put(LocaleUtils.toLowerString(array2[0]), array2);
        }
    }
}
