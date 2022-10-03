package org.apache.tika.language.detect;

import java.util.Map;
import java.util.Set;
import java.io.IOException;
import java.util.Comparator;
import org.apache.tika.utils.CompareUtils;
import java.util.List;
import org.apache.tika.config.ServiceLoader;

public abstract class LanguageDetector
{
    private static final ServiceLoader DEFAULT_SERVICE_LOADER;
    private static final int BUFFER_LENGTH = 4096;
    protected boolean mixedLanguages;
    protected boolean shortText;
    
    public LanguageDetector() {
        this.mixedLanguages = false;
        this.shortText = false;
    }
    
    public static LanguageDetector getDefaultLanguageDetector() {
        final List<LanguageDetector> detectors = getLanguageDetectors();
        if (detectors.isEmpty()) {
            throw new IllegalStateException("No language detectors available");
        }
        return detectors.get(0);
    }
    
    public static List<LanguageDetector> getLanguageDetectors() {
        return getLanguageDetectors(LanguageDetector.DEFAULT_SERVICE_LOADER);
    }
    
    public static List<LanguageDetector> getLanguageDetectors(final ServiceLoader loader) {
        final List<LanguageDetector> detectors = loader.loadStaticServiceProviders(LanguageDetector.class);
        detectors.sort(CompareUtils::compareClassName);
        return detectors;
    }
    
    public boolean isMixedLanguages() {
        return this.mixedLanguages;
    }
    
    public LanguageDetector setMixedLanguages(final boolean mixedLanguages) {
        this.mixedLanguages = mixedLanguages;
        return this;
    }
    
    public boolean isShortText() {
        return this.shortText;
    }
    
    public LanguageDetector setShortText(final boolean shortText) {
        this.shortText = shortText;
        return this;
    }
    
    public abstract LanguageDetector loadModels() throws IOException;
    
    public abstract LanguageDetector loadModels(final Set<String> p0) throws IOException;
    
    public abstract boolean hasModel(final String p0);
    
    public abstract LanguageDetector setPriors(final Map<String, Float> p0) throws IOException;
    
    public abstract void reset();
    
    public abstract void addText(final char[] p0, final int p1, final int p2);
    
    public void addText(final CharSequence text) {
        final int len = text.length();
        if (len < 4096) {
            final char[] chars = text.toString().toCharArray();
            this.addText(chars, 0, chars.length);
            return;
        }
        for (int start = 0; !this.hasEnoughText() && start < len; start += 4096) {
            final int end = Math.min(start + 4096, len);
            final char[] chars2 = text.subSequence(start, end).toString().toCharArray();
            this.addText(chars2, 0, chars2.length);
        }
    }
    
    public boolean hasEnoughText() {
        return false;
    }
    
    public abstract List<LanguageResult> detectAll();
    
    public LanguageResult detect() {
        final List<LanguageResult> results = this.detectAll();
        return results.get(0);
    }
    
    public List<LanguageResult> detectAll(final String text) {
        this.reset();
        this.addText(text);
        return this.detectAll();
    }
    
    public LanguageResult detect(final CharSequence text) {
        this.reset();
        this.addText(text);
        return this.detect();
    }
    
    static {
        DEFAULT_SERVICE_LOADER = new ServiceLoader();
    }
}
