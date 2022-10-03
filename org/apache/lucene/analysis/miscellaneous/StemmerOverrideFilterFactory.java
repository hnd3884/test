package org.apache.lucene.analysis.miscellaneous;

import org.apache.lucene.analysis.TokenStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import org.apache.lucene.analysis.util.ResourceLoader;
import java.util.Map;
import org.apache.lucene.analysis.util.ResourceLoaderAware;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class StemmerOverrideFilterFactory extends TokenFilterFactory implements ResourceLoaderAware
{
    private StemmerOverrideFilter.StemmerOverrideMap dictionary;
    private final String dictionaryFiles;
    private final boolean ignoreCase;
    
    public StemmerOverrideFilterFactory(final Map<String, String> args) {
        super(args);
        this.dictionaryFiles = this.get(args, "dictionary");
        this.ignoreCase = this.getBoolean(args, "ignoreCase", false);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }
    
    @Override
    public void inform(final ResourceLoader loader) throws IOException {
        if (this.dictionaryFiles != null) {
            final List<String> files = this.splitFileNames(this.dictionaryFiles);
            if (files.size() > 0) {
                final StemmerOverrideFilter.Builder builder = new StemmerOverrideFilter.Builder(this.ignoreCase);
                for (final String file : files) {
                    final List<String> list = this.getLines(loader, file.trim());
                    for (final String line : list) {
                        final String[] mapping = line.split("\t", 2);
                        builder.add(mapping[0], mapping[1]);
                    }
                }
                this.dictionary = builder.build();
            }
        }
    }
    
    public boolean isIgnoreCase() {
        return this.ignoreCase;
    }
    
    @Override
    public TokenStream create(final TokenStream input) {
        return (TokenStream)((this.dictionary == null) ? input : new StemmerOverrideFilter(input, this.dictionary));
    }
}
