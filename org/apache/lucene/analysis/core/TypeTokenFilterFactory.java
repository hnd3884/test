package org.apache.lucene.analysis.core;

import org.apache.lucene.analysis.TokenStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Collection;
import java.util.HashSet;
import org.apache.lucene.analysis.util.ResourceLoader;
import org.apache.lucene.util.Version;
import java.util.Map;
import java.util.Set;
import org.apache.lucene.analysis.util.ResourceLoaderAware;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class TypeTokenFilterFactory extends TokenFilterFactory implements ResourceLoaderAware
{
    private final boolean useWhitelist;
    private final String stopTypesFiles;
    private Set<String> stopTypes;
    private boolean enablePositionIncrements;
    
    public TypeTokenFilterFactory(final Map<String, String> args) {
        super(args);
        this.stopTypesFiles = this.require(args, "types");
        this.useWhitelist = this.getBoolean(args, "useWhitelist", false);
        if (!this.luceneMatchVersion.onOrAfter(Version.LUCENE_5_0_0)) {
            final boolean defaultValue = this.luceneMatchVersion.onOrAfter(Version.LUCENE_4_4_0);
            this.enablePositionIncrements = this.getBoolean(args, "enablePositionIncrements", defaultValue);
            if (!this.enablePositionIncrements && this.luceneMatchVersion.onOrAfter(Version.LUCENE_4_4_0)) {
                throw new IllegalArgumentException("enablePositionIncrements=false is not supported anymore as of Lucene 4.4");
            }
        }
        else if (args.containsKey("enablePositionIncrements")) {
            throw new IllegalArgumentException("enablePositionIncrements is not a valid option as of Lucene 5.0");
        }
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }
    
    @Override
    public void inform(final ResourceLoader loader) throws IOException {
        final List<String> files = this.splitFileNames(this.stopTypesFiles);
        if (files.size() > 0) {
            this.stopTypes = new HashSet<String>();
            for (final String file : files) {
                final List<String> typesLines = this.getLines(loader, file.trim());
                this.stopTypes.addAll(typesLines);
            }
        }
    }
    
    public Set<String> getStopTypes() {
        return this.stopTypes;
    }
    
    @Override
    public TokenStream create(final TokenStream input) {
        if (this.luceneMatchVersion.onOrAfter(Version.LUCENE_4_4_0)) {
            return (TokenStream)new TypeTokenFilter(input, this.stopTypes, this.useWhitelist);
        }
        final TokenStream filter = (TokenStream)new Lucene43TypeTokenFilter(this.enablePositionIncrements, input, this.stopTypes, this.useWhitelist);
        return filter;
    }
}
