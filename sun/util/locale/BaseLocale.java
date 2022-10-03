package sun.util.locale;

import java.lang.ref.SoftReference;

public final class BaseLocale
{
    public static final String SEP = "_";
    private static final Cache CACHE;
    private final String language;
    private final String script;
    private final String region;
    private final String variant;
    private volatile int hash;
    
    private BaseLocale(final String language, final String region) {
        this.hash = 0;
        this.language = language;
        this.script = "";
        this.region = region;
        this.variant = "";
    }
    
    private BaseLocale(final String s, final String s2, final String s3, final String s4) {
        this.hash = 0;
        this.language = ((s != null) ? LocaleUtils.toLowerString(s).intern() : "");
        this.script = ((s2 != null) ? LocaleUtils.toTitleString(s2).intern() : "");
        this.region = ((s3 != null) ? LocaleUtils.toUpperString(s3).intern() : "");
        this.variant = ((s4 != null) ? s4.intern() : "");
    }
    
    public static BaseLocale createInstance(final String s, final String s2) {
        final BaseLocale baseLocale = new BaseLocale(s, s2);
        BaseLocale.CACHE.put(new Key(s, s2), baseLocale);
        return baseLocale;
    }
    
    public static BaseLocale getInstance(String s, final String s2, final String s3, final String s4) {
        if (s != null) {
            if (LocaleUtils.caseIgnoreMatch(s, "he")) {
                s = "iw";
            }
            else if (LocaleUtils.caseIgnoreMatch(s, "yi")) {
                s = "ji";
            }
            else if (LocaleUtils.caseIgnoreMatch(s, "id")) {
                s = "in";
            }
        }
        return BaseLocale.CACHE.get(new Key(s, s2, s3, s4));
    }
    
    public String getLanguage() {
        return this.language;
    }
    
    public String getScript() {
        return this.script;
    }
    
    public String getRegion() {
        return this.region;
    }
    
    public String getVariant() {
        return this.variant;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BaseLocale)) {
            return false;
        }
        final BaseLocale baseLocale = (BaseLocale)o;
        return this.language == baseLocale.language && this.script == baseLocale.script && this.region == baseLocale.region && this.variant == baseLocale.variant;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        if (this.language.length() > 0) {
            sb.append("language=");
            sb.append(this.language);
        }
        if (this.script.length() > 0) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append("script=");
            sb.append(this.script);
        }
        if (this.region.length() > 0) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append("region=");
            sb.append(this.region);
        }
        if (this.variant.length() > 0) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append("variant=");
            sb.append(this.variant);
        }
        return sb.toString();
    }
    
    @Override
    public int hashCode() {
        int hash = this.hash;
        if (hash == 0) {
            hash = 31 * (31 * (31 * this.language.hashCode() + this.script.hashCode()) + this.region.hashCode()) + this.variant.hashCode();
            this.hash = hash;
        }
        return hash;
    }
    
    static {
        CACHE = new Cache();
    }
    
    private static final class Key
    {
        private final SoftReference<String> lang;
        private final SoftReference<String> scrt;
        private final SoftReference<String> regn;
        private final SoftReference<String> vart;
        private final boolean normalized;
        private final int hash;
        
        private Key(final String s, final String s2) {
            assert s.intern() == s && s2.intern() == s2;
            this.lang = new SoftReference<String>(s);
            this.scrt = new SoftReference<String>("");
            this.regn = new SoftReference<String>(s2);
            this.vart = new SoftReference<String>("");
            this.normalized = true;
            int hashCode = s.hashCode();
            if (s2 != "") {
                for (int length = s2.length(), i = 0; i < length; ++i) {
                    hashCode = 31 * hashCode + LocaleUtils.toLower(s2.charAt(i));
                }
            }
            this.hash = hashCode;
        }
        
        public Key(final String s, final String s2, final String s3, final String s4) {
            this(s, s2, s3, s4, false);
        }
        
        private Key(final String s, final String s2, final String s3, final String s4, final boolean normalized) {
            int hash = 0;
            if (s != null) {
                this.lang = new SoftReference<String>(s);
                for (int length = s.length(), i = 0; i < length; ++i) {
                    hash = 31 * hash + LocaleUtils.toLower(s.charAt(i));
                }
            }
            else {
                this.lang = new SoftReference<String>("");
            }
            if (s2 != null) {
                this.scrt = new SoftReference<String>(s2);
                for (int length2 = s2.length(), j = 0; j < length2; ++j) {
                    hash = 31 * hash + LocaleUtils.toLower(s2.charAt(j));
                }
            }
            else {
                this.scrt = new SoftReference<String>("");
            }
            if (s3 != null) {
                this.regn = new SoftReference<String>(s3);
                for (int length3 = s3.length(), k = 0; k < length3; ++k) {
                    hash = 31 * hash + LocaleUtils.toLower(s3.charAt(k));
                }
            }
            else {
                this.regn = new SoftReference<String>("");
            }
            if (s4 != null) {
                this.vart = new SoftReference<String>(s4);
                for (int length4 = s4.length(), l = 0; l < length4; ++l) {
                    hash = 31 * hash + s4.charAt(l);
                }
            }
            else {
                this.vart = new SoftReference<String>("");
            }
            this.hash = hash;
            this.normalized = normalized;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o instanceof Key && this.hash == ((Key)o).hash) {
                final String s = this.lang.get();
                final String s2 = ((Key)o).lang.get();
                if (s != null && s2 != null && LocaleUtils.caseIgnoreMatch(s2, s)) {
                    final String s3 = this.scrt.get();
                    final String s4 = ((Key)o).scrt.get();
                    if (s3 != null && s4 != null && LocaleUtils.caseIgnoreMatch(s4, s3)) {
                        final String s5 = this.regn.get();
                        final String s6 = ((Key)o).regn.get();
                        if (s5 != null && s6 != null && LocaleUtils.caseIgnoreMatch(s6, s5)) {
                            final String s7 = this.vart.get();
                            final String s8 = ((Key)o).vart.get();
                            return s8 != null && s8.equals(s7);
                        }
                    }
                }
            }
            return false;
        }
        
        @Override
        public int hashCode() {
            return this.hash;
        }
        
        public static Key normalize(final Key key) {
            if (key.normalized) {
                return key;
            }
            return new Key(LocaleUtils.toLowerString(key.lang.get()).intern(), LocaleUtils.toTitleString(key.scrt.get()).intern(), LocaleUtils.toUpperString(key.regn.get()).intern(), key.vart.get().intern(), true);
        }
    }
    
    private static class Cache extends LocaleObjectCache<Key, BaseLocale>
    {
        public Cache() {
        }
        
        @Override
        protected Key normalizeKey(final Key key) {
            assert key.lang.get() != null && key.scrt.get() != null && key.regn.get() != null && key.vart.get() != null;
            return Key.normalize(key);
        }
        
        @Override
        protected BaseLocale createObject(final Key key) {
            return new BaseLocale(key.lang.get(), key.scrt.get(), key.regn.get(), key.vart.get(), null);
        }
    }
}
