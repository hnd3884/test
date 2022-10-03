package sun.util;

import java.util.List;
import java.util.Arrays;
import java.util.Locale;
import java.util.Collection;
import java.util.ResourceBundle;

public class CoreResourceBundleControl extends ResourceBundle.Control
{
    private final Collection<Locale> excludedJDKLocales;
    private static CoreResourceBundleControl resourceBundleControlInstance;
    
    protected CoreResourceBundleControl() {
        this.excludedJDKLocales = Arrays.asList(Locale.GERMANY, Locale.ENGLISH, Locale.US, new Locale("es", "ES"), Locale.FRANCE, Locale.ITALY, Locale.JAPAN, Locale.KOREA, new Locale("sv", "SE"), Locale.CHINESE);
    }
    
    public static CoreResourceBundleControl getRBControlInstance() {
        return CoreResourceBundleControl.resourceBundleControlInstance;
    }
    
    public static CoreResourceBundleControl getRBControlInstance(final String s) {
        if (s.startsWith("com.sun.") || s.startsWith("java.") || s.startsWith("javax.") || s.startsWith("sun.")) {
            return CoreResourceBundleControl.resourceBundleControlInstance;
        }
        return null;
    }
    
    @Override
    public List<Locale> getCandidateLocales(final String s, final Locale locale) {
        final List<Locale> candidateLocales = super.getCandidateLocales(s, locale);
        candidateLocales.removeAll(this.excludedJDKLocales);
        return candidateLocales;
    }
    
    @Override
    public long getTimeToLive(final String s, final Locale locale) {
        return -1L;
    }
    
    static {
        CoreResourceBundleControl.resourceBundleControlInstance = new CoreResourceBundleControl();
    }
}
