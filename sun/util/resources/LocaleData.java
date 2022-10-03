package sun.util.resources;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import sun.util.locale.provider.JRELocaleProviderAdapter;
import java.util.List;
import java.util.MissingResourceException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ResourceBundle;
import java.util.Locale;
import sun.util.locale.provider.LocaleProviderAdapter;

public class LocaleData
{
    private final LocaleProviderAdapter.Type type;
    
    public LocaleData(final LocaleProviderAdapter.Type type) {
        this.type = type;
    }
    
    public ResourceBundle getCalendarData(final Locale locale) {
        return getBundle(this.type.getUtilResourcesPackage() + ".CalendarData", locale);
    }
    
    public OpenListResourceBundle getCurrencyNames(final Locale locale) {
        return (OpenListResourceBundle)getBundle(this.type.getUtilResourcesPackage() + ".CurrencyNames", locale);
    }
    
    public OpenListResourceBundle getLocaleNames(final Locale locale) {
        return (OpenListResourceBundle)getBundle(this.type.getUtilResourcesPackage() + ".LocaleNames", locale);
    }
    
    public TimeZoneNamesBundle getTimeZoneNames(final Locale locale) {
        return (TimeZoneNamesBundle)getBundle(this.type.getUtilResourcesPackage() + ".TimeZoneNames", locale);
    }
    
    public ResourceBundle getBreakIteratorInfo(final Locale locale) {
        return getBundle(this.type.getTextResourcesPackage() + ".BreakIteratorInfo", locale);
    }
    
    public ResourceBundle getCollationData(final Locale locale) {
        return getBundle(this.type.getTextResourcesPackage() + ".CollationData", locale);
    }
    
    public ResourceBundle getDateFormatData(final Locale locale) {
        return getBundle(this.type.getTextResourcesPackage() + ".FormatData", locale);
    }
    
    public void setSupplementary(final ParallelListResourceBundle parallelListResourceBundle) {
        if (!parallelListResourceBundle.areParallelContentsComplete()) {
            this.setSupplementary(this.type.getTextResourcesPackage() + ".JavaTimeSupplementary", parallelListResourceBundle);
        }
    }
    
    private boolean setSupplementary(final String s, final ParallelListResourceBundle parallelListResourceBundle) {
        final ParallelListResourceBundle parallelListResourceBundle2 = (ParallelListResourceBundle)parallelListResourceBundle.getParent();
        boolean setSupplementary = false;
        if (parallelListResourceBundle2 != null) {
            setSupplementary = this.setSupplementary(s, parallelListResourceBundle2);
        }
        final OpenListResourceBundle supplementary = getSupplementary(s, parallelListResourceBundle.getLocale());
        parallelListResourceBundle.setParallelContents(supplementary);
        final boolean b = setSupplementary | supplementary != null;
        if (b) {
            parallelListResourceBundle.resetKeySet();
        }
        return b;
    }
    
    public ResourceBundle getNumberFormatData(final Locale locale) {
        return getBundle(this.type.getTextResourcesPackage() + ".FormatData", locale);
    }
    
    public static ResourceBundle getBundle(final String s, final Locale locale) {
        return AccessController.doPrivileged((PrivilegedAction<ResourceBundle>)new PrivilegedAction<ResourceBundle>() {
            @Override
            public ResourceBundle run() {
                return ResourceBundle.getBundle(s, locale, LocaleDataResourceBundleControl.INSTANCE);
            }
        });
    }
    
    private static OpenListResourceBundle getSupplementary(final String s, final Locale locale) {
        return AccessController.doPrivileged((PrivilegedAction<OpenListResourceBundle>)new PrivilegedAction<OpenListResourceBundle>() {
            @Override
            public OpenListResourceBundle run() {
                OpenListResourceBundle openListResourceBundle = null;
                try {
                    openListResourceBundle = (OpenListResourceBundle)ResourceBundle.getBundle(s, locale, SupplementaryResourceBundleControl.INSTANCE);
                }
                catch (final MissingResourceException ex) {}
                return openListResourceBundle;
            }
        });
    }
    
    private static class LocaleDataResourceBundleControl extends ResourceBundle.Control
    {
        private static final LocaleDataResourceBundleControl INSTANCE;
        private static final String DOTCLDR = ".cldr";
        
        @Override
        public List<Locale> getCandidateLocales(final String s, final Locale locale) {
            final List<Locale> candidateLocales = super.getCandidateLocales(s, locale);
            final int lastIndex = s.lastIndexOf(46);
            final String s2 = (lastIndex >= 0) ? s.substring(lastIndex + 1) : s;
            final LocaleProviderAdapter.Type type = s.contains(".cldr") ? LocaleProviderAdapter.Type.CLDR : LocaleProviderAdapter.Type.JRE;
            final Set<String> languageTagSet = ((JRELocaleProviderAdapter)LocaleProviderAdapter.forType(type)).getLanguageTagSet(s2);
            if (!languageTagSet.isEmpty()) {
                final Iterator iterator = candidateLocales.iterator();
                while (iterator.hasNext()) {
                    if (!LocaleProviderAdapter.isSupportedLocale((Locale)iterator.next(), type, languageTagSet)) {
                        iterator.remove();
                    }
                }
            }
            if (locale.getLanguage() != "en" && type == LocaleProviderAdapter.Type.CLDR && s2.equals("TimeZoneNames")) {
                candidateLocales.add(candidateLocales.size() - 1, Locale.ENGLISH);
            }
            return candidateLocales;
        }
        
        @Override
        public Locale getFallbackLocale(final String s, final Locale locale) {
            if (s == null || locale == null) {
                throw new NullPointerException();
            }
            return null;
        }
        
        @Override
        public String toBundleName(final String s, final Locale locale) {
            String string = s;
            final String language = locale.getLanguage();
            if (language.length() > 0 && (s.startsWith(LocaleProviderAdapter.Type.JRE.getUtilResourcesPackage()) || s.startsWith(LocaleProviderAdapter.Type.JRE.getTextResourcesPackage()))) {
                assert LocaleProviderAdapter.Type.JRE.getUtilResourcesPackage().length() == LocaleProviderAdapter.Type.JRE.getTextResourcesPackage().length();
                int length = LocaleProviderAdapter.Type.JRE.getUtilResourcesPackage().length();
                if (s.indexOf(".cldr", length) > 0) {
                    length += ".cldr".length();
                }
                string = s.substring(0, length + 1) + language + s.substring(length);
            }
            return super.toBundleName(string, locale);
        }
        
        static {
            INSTANCE = new LocaleDataResourceBundleControl();
        }
    }
    
    private static class SupplementaryResourceBundleControl extends LocaleDataResourceBundleControl
    {
        private static final SupplementaryResourceBundleControl INSTANCE;
        
        @Override
        public List<Locale> getCandidateLocales(final String s, final Locale locale) {
            return Arrays.asList(locale);
        }
        
        @Override
        public long getTimeToLive(final String s, final Locale locale) {
            assert s.contains("JavaTimeSupplementary");
            return -1L;
        }
        
        static {
            INSTANCE = new SupplementaryResourceBundleControl();
        }
    }
}
