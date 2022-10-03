package sun.util.cldr;

import java.util.StringTokenizer;
import java.util.HashSet;
import java.util.Collections;
import java.util.ResourceBundle;
import java.util.Iterator;
import java.util.Set;
import java.util.Locale;
import java.text.spi.CollatorProvider;
import java.text.spi.BreakIteratorProvider;
import sun.util.locale.provider.LocaleProviderAdapter;
import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;
import java.io.File;
import sun.util.locale.provider.JRELocaleProviderAdapter;

public class CLDRLocaleProviderAdapter extends JRELocaleProviderAdapter
{
    private static final String LOCALE_DATA_JAR_NAME = "cldrdata.jar";
    
    public CLDRLocaleProviderAdapter() {
        final String separator = File.separator;
        if (!AccessController.doPrivileged((PrivilegedAction<Boolean>)new PrivilegedAction<Boolean>() {
            final /* synthetic */ File val$f = new File(AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("java.home")) + separator + "lib" + separator + "ext" + separator + "cldrdata.jar");
            
            @Override
            public Boolean run() {
                return this.val$f.exists();
            }
        })) {
            throw new UnsupportedOperationException();
        }
    }
    
    @Override
    public Type getAdapterType() {
        return Type.CLDR;
    }
    
    @Override
    public BreakIteratorProvider getBreakIteratorProvider() {
        return null;
    }
    
    @Override
    public CollatorProvider getCollatorProvider() {
        return null;
    }
    
    @Override
    public Locale[] getAvailableLocales() {
        final Set<String> languageTagSet = this.createLanguageTagSet("All");
        final Locale[] array = new Locale[languageTagSet.size()];
        int n = 0;
        final Iterator iterator = languageTagSet.iterator();
        while (iterator.hasNext()) {
            array[n++] = Locale.forLanguageTag((String)iterator.next());
        }
        return array;
    }
    
    @Override
    protected Set<String> createLanguageTagSet(final String s) {
        final String string = ResourceBundle.getBundle("sun.util.cldr.CLDRLocaleDataMetaInfo", Locale.ROOT).getString(s);
        if (string == null) {
            return Collections.emptySet();
        }
        final HashSet set = new HashSet();
        final StringTokenizer stringTokenizer = new StringTokenizer(string);
        while (stringTokenizer.hasMoreTokens()) {
            set.add(stringTokenizer.nextToken());
        }
        return set;
    }
}
