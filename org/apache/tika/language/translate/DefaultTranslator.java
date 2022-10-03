package org.apache.tika.language.translate;

import java.io.IOException;
import org.apache.tika.exception.TikaException;
import java.util.Iterator;
import java.util.Comparator;
import org.apache.tika.utils.CompareUtils;
import java.util.List;
import org.apache.tika.config.ServiceLoader;

public class DefaultTranslator implements Translator
{
    private final transient ServiceLoader loader;
    
    public DefaultTranslator(final ServiceLoader loader) {
        this.loader = loader;
    }
    
    public DefaultTranslator() {
        this(new ServiceLoader());
    }
    
    private static List<Translator> getDefaultTranslators(final ServiceLoader loader) {
        final List<Translator> translators = loader.loadStaticServiceProviders(Translator.class);
        translators.sort(CompareUtils::compareClassName);
        return translators;
    }
    
    private static Translator getFirstAvailable(final ServiceLoader loader) {
        for (final Translator t : getDefaultTranslators(loader)) {
            if (t.isAvailable()) {
                return t;
            }
        }
        return null;
    }
    
    @Override
    public String translate(final String text, final String sourceLanguage, final String targetLanguage) throws TikaException, IOException {
        final Translator t = getFirstAvailable(this.loader);
        if (t != null) {
            return t.translate(text, sourceLanguage, targetLanguage);
        }
        throw new TikaException("No translators currently available");
    }
    
    @Override
    public String translate(final String text, final String targetLanguage) throws TikaException, IOException {
        final Translator t = getFirstAvailable(this.loader);
        if (t != null) {
            return t.translate(text, targetLanguage);
        }
        throw new TikaException("No translators currently available");
    }
    
    public List<Translator> getTranslators() {
        return getDefaultTranslators(this.loader);
    }
    
    public Translator getTranslator() {
        return getFirstAvailable(this.loader);
    }
    
    @Override
    public boolean isAvailable() {
        return getFirstAvailable(this.loader) != null;
    }
}
