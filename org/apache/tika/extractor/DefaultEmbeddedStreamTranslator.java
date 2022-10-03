package org.apache.tika.extractor;

import java.io.IOException;
import java.util.Iterator;
import org.apache.tika.metadata.Metadata;
import java.io.InputStream;
import org.apache.tika.utils.ServiceLoaderUtils;
import org.apache.tika.config.ServiceLoader;
import java.util.List;

public class DefaultEmbeddedStreamTranslator implements EmbeddedStreamTranslator
{
    final List<EmbeddedStreamTranslator> translators;
    
    private static List<EmbeddedStreamTranslator> getDefaultFilters(final ServiceLoader loader) {
        final List<EmbeddedStreamTranslator> embeddedStreamTranslators = loader.loadServiceProviders(EmbeddedStreamTranslator.class);
        ServiceLoaderUtils.sortLoadedClasses(embeddedStreamTranslators);
        return embeddedStreamTranslators;
    }
    
    public DefaultEmbeddedStreamTranslator() {
        this(getDefaultFilters(new ServiceLoader()));
    }
    
    private DefaultEmbeddedStreamTranslator(final List<EmbeddedStreamTranslator> translators) {
        this.translators = translators;
    }
    
    @Override
    public boolean shouldTranslate(final InputStream inputStream, final Metadata metadata) throws IOException {
        for (final EmbeddedStreamTranslator translator : this.translators) {
            if (translator.shouldTranslate(inputStream, metadata)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public InputStream translate(final InputStream inputStream, final Metadata metadata) throws IOException {
        for (final EmbeddedStreamTranslator translator : this.translators) {
            final InputStream translated = translator.translate(inputStream, metadata);
            if (translated != null) {
                return translated;
            }
        }
        return inputStream;
    }
}
