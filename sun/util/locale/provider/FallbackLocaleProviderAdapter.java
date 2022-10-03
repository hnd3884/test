package sun.util.locale.provider;

import java.util.Collections;
import java.util.Locale;
import java.util.Set;

public class FallbackLocaleProviderAdapter extends JRELocaleProviderAdapter
{
    private static final Set<String> rootTagSet;
    private final LocaleResources rootLocaleResources;
    
    public FallbackLocaleProviderAdapter() {
        this.rootLocaleResources = new LocaleResources(this, Locale.ROOT);
    }
    
    @Override
    public Type getAdapterType() {
        return Type.FALLBACK;
    }
    
    @Override
    public LocaleResources getLocaleResources(final Locale locale) {
        return this.rootLocaleResources;
    }
    
    @Override
    protected Set<String> createLanguageTagSet(final String s) {
        return FallbackLocaleProviderAdapter.rootTagSet;
    }
    
    static {
        rootTagSet = Collections.singleton(Locale.ROOT.toLanguageTag());
    }
}
