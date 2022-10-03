package org.apache.lucene.analysis.custom;

import org.apache.lucene.analysis.util.ResourceLoaderAware;
import java.util.HashMap;
import org.apache.lucene.analysis.util.AnalysisSPILoader;
import java.util.Map;
import java.io.IOException;
import java.util.Objects;
import java.util.ArrayList;
import org.apache.lucene.util.SetOnce;
import java.util.Collections;
import java.util.Arrays;
import java.util.List;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.TokenStream;
import java.io.Reader;
import org.apache.lucene.util.Version;
import org.apache.lucene.analysis.util.FilesystemResourceLoader;
import java.nio.file.Path;
import org.apache.lucene.analysis.util.ResourceLoader;
import org.apache.lucene.analysis.util.ClasspathResourceLoader;
import org.apache.lucene.analysis.util.TokenFilterFactory;
import org.apache.lucene.analysis.util.TokenizerFactory;
import org.apache.lucene.analysis.util.CharFilterFactory;
import org.apache.lucene.analysis.Analyzer;

public final class CustomAnalyzer extends Analyzer
{
    private final CharFilterFactory[] charFilters;
    private final TokenizerFactory tokenizer;
    private final TokenFilterFactory[] tokenFilters;
    private final Integer posIncGap;
    private final Integer offsetGap;
    
    public static Builder builder() {
        return builder(new ClasspathResourceLoader());
    }
    
    public static Builder builder(final Path configDir) {
        return builder(new FilesystemResourceLoader(configDir));
    }
    
    public static Builder builder(final ResourceLoader loader) {
        return new Builder(loader);
    }
    
    CustomAnalyzer(final Version defaultMatchVersion, final CharFilterFactory[] charFilters, final TokenizerFactory tokenizer, final TokenFilterFactory[] tokenFilters, final Integer posIncGap, final Integer offsetGap) {
        this.charFilters = charFilters;
        this.tokenizer = tokenizer;
        this.tokenFilters = tokenFilters;
        this.posIncGap = posIncGap;
        this.offsetGap = offsetGap;
        if (defaultMatchVersion != null) {
            this.setVersion(defaultMatchVersion);
        }
    }
    
    protected Reader initReader(final String fieldName, Reader reader) {
        for (final CharFilterFactory charFilter : this.charFilters) {
            reader = charFilter.create(reader);
        }
        return reader;
    }
    
    protected Analyzer.TokenStreamComponents createComponents(final String fieldName) {
        TokenStream ts;
        final Tokenizer tk = (Tokenizer)(ts = (TokenStream)this.tokenizer.create());
        for (final TokenFilterFactory filter : this.tokenFilters) {
            ts = filter.create(ts);
        }
        return new Analyzer.TokenStreamComponents(tk, ts);
    }
    
    public int getPositionIncrementGap(final String fieldName) {
        return (this.posIncGap == null) ? super.getPositionIncrementGap(fieldName) : this.posIncGap;
    }
    
    public int getOffsetGap(final String fieldName) {
        return (this.offsetGap == null) ? super.getOffsetGap(fieldName) : this.offsetGap;
    }
    
    public List<CharFilterFactory> getCharFilterFactories() {
        return Collections.unmodifiableList((List<? extends CharFilterFactory>)Arrays.asList((T[])this.charFilters));
    }
    
    public TokenizerFactory getTokenizerFactory() {
        return this.tokenizer;
    }
    
    public List<TokenFilterFactory> getTokenFilterFactories() {
        return Collections.unmodifiableList((List<? extends TokenFilterFactory>)Arrays.asList((T[])this.tokenFilters));
    }
    
    public String toString() {
        final StringBuilder sb = new StringBuilder(this.getClass().getSimpleName()).append('(');
        for (final CharFilterFactory filter : this.charFilters) {
            sb.append(filter).append(',');
        }
        sb.append(this.tokenizer);
        for (final TokenFilterFactory filter2 : this.tokenFilters) {
            sb.append(',').append(filter2);
        }
        return sb.append(')').toString();
    }
    
    public static final class Builder
    {
        private final ResourceLoader loader;
        private final SetOnce<Version> defaultMatchVersion;
        private final List<CharFilterFactory> charFilters;
        private final SetOnce<TokenizerFactory> tokenizer;
        private final List<TokenFilterFactory> tokenFilters;
        private final SetOnce<Integer> posIncGap;
        private final SetOnce<Integer> offsetGap;
        private boolean componentsAdded;
        
        Builder(final ResourceLoader loader) {
            this.defaultMatchVersion = (SetOnce<Version>)new SetOnce();
            this.charFilters = new ArrayList<CharFilterFactory>();
            this.tokenizer = (SetOnce<TokenizerFactory>)new SetOnce();
            this.tokenFilters = new ArrayList<TokenFilterFactory>();
            this.posIncGap = (SetOnce<Integer>)new SetOnce();
            this.offsetGap = (SetOnce<Integer>)new SetOnce();
            this.componentsAdded = false;
            this.loader = loader;
        }
        
        public Builder withDefaultMatchVersion(final Version version) {
            Objects.requireNonNull(version, "version may not be null");
            if (this.componentsAdded) {
                throw new IllegalStateException("You may only set the default match version before adding tokenizers, token filters, or char filters.");
            }
            this.defaultMatchVersion.set((Object)version);
            return this;
        }
        
        public Builder withPositionIncrementGap(final int posIncGap) {
            if (posIncGap < 0) {
                throw new IllegalArgumentException("posIncGap must be >= 0");
            }
            this.posIncGap.set((Object)posIncGap);
            return this;
        }
        
        public Builder withOffsetGap(final int offsetGap) {
            if (offsetGap < 0) {
                throw new IllegalArgumentException("offsetGap must be >= 0");
            }
            this.offsetGap.set((Object)offsetGap);
            return this;
        }
        
        public Builder withTokenizer(final Class<? extends TokenizerFactory> factory, final String... params) throws IOException {
            return this.withTokenizer(factory, this.paramsToMap(params));
        }
        
        public Builder withTokenizer(final Class<? extends TokenizerFactory> factory, final Map<String, String> params) throws IOException {
            Objects.requireNonNull(factory, "Tokenizer factory may not be null");
            this.tokenizer.set(this.applyResourceLoader((Object)AnalysisSPILoader.newFactoryClassInstance((Class<T>)factory, this.applyDefaultParams(params))));
            this.componentsAdded = true;
            return this;
        }
        
        public Builder withTokenizer(final String name, final String... params) throws IOException {
            return this.withTokenizer(name, this.paramsToMap(params));
        }
        
        public Builder withTokenizer(final String name, final Map<String, String> params) throws IOException {
            Objects.requireNonNull(name, "Tokenizer name may not be null");
            this.tokenizer.set((Object)this.applyResourceLoader(TokenizerFactory.forName(name, this.applyDefaultParams(params))));
            this.componentsAdded = true;
            return this;
        }
        
        public Builder addTokenFilter(final Class<? extends TokenFilterFactory> factory, final String... params) throws IOException {
            return this.addTokenFilter(factory, this.paramsToMap(params));
        }
        
        public Builder addTokenFilter(final Class<? extends TokenFilterFactory> factory, final Map<String, String> params) throws IOException {
            Objects.requireNonNull(factory, "TokenFilter name may not be null");
            this.tokenFilters.add(this.applyResourceLoader((TokenFilterFactory)AnalysisSPILoader.newFactoryClassInstance((Class<T>)factory, this.applyDefaultParams(params))));
            this.componentsAdded = true;
            return this;
        }
        
        public Builder addTokenFilter(final String name, final String... params) throws IOException {
            return this.addTokenFilter(name, this.paramsToMap(params));
        }
        
        public Builder addTokenFilter(final String name, final Map<String, String> params) throws IOException {
            Objects.requireNonNull(name, "TokenFilter name may not be null");
            this.tokenFilters.add(this.applyResourceLoader(TokenFilterFactory.forName(name, this.applyDefaultParams(params))));
            this.componentsAdded = true;
            return this;
        }
        
        public Builder addCharFilter(final Class<? extends CharFilterFactory> factory, final String... params) throws IOException {
            return this.addCharFilter(factory, this.paramsToMap(params));
        }
        
        public Builder addCharFilter(final Class<? extends CharFilterFactory> factory, final Map<String, String> params) throws IOException {
            Objects.requireNonNull(factory, "CharFilter name may not be null");
            this.charFilters.add(this.applyResourceLoader((CharFilterFactory)AnalysisSPILoader.newFactoryClassInstance((Class<T>)factory, this.applyDefaultParams(params))));
            this.componentsAdded = true;
            return this;
        }
        
        public Builder addCharFilter(final String name, final String... params) throws IOException {
            return this.addCharFilter(name, this.paramsToMap(params));
        }
        
        public Builder addCharFilter(final String name, final Map<String, String> params) throws IOException {
            Objects.requireNonNull(name, "CharFilter name may not be null");
            this.charFilters.add(this.applyResourceLoader(CharFilterFactory.forName(name, this.applyDefaultParams(params))));
            this.componentsAdded = true;
            return this;
        }
        
        public CustomAnalyzer build() {
            if (this.tokenizer.get() == null) {
                throw new IllegalStateException("You have to set at least a tokenizer.");
            }
            return new CustomAnalyzer((Version)this.defaultMatchVersion.get(), this.charFilters.toArray(new CharFilterFactory[this.charFilters.size()]), (TokenizerFactory)this.tokenizer.get(), this.tokenFilters.toArray(new TokenFilterFactory[this.tokenFilters.size()]), (Integer)this.posIncGap.get(), (Integer)this.offsetGap.get());
        }
        
        private Map<String, String> applyDefaultParams(final Map<String, String> map) {
            if (this.defaultMatchVersion.get() != null && !map.containsKey("luceneMatchVersion")) {
                map.put("luceneMatchVersion", ((Version)this.defaultMatchVersion.get()).toString());
            }
            return map;
        }
        
        private Map<String, String> paramsToMap(final String... params) {
            if (params.length % 2 != 0) {
                throw new IllegalArgumentException("Key-value pairs expected, so the number of params must be even.");
            }
            final Map<String, String> map = new HashMap<String, String>();
            for (int i = 0; i < params.length; i += 2) {
                Objects.requireNonNull(params[i], "Key of param may not be null.");
                map.put(params[i], params[i + 1]);
            }
            return map;
        }
        
        private <T> T applyResourceLoader(final T factory) throws IOException {
            if (factory instanceof ResourceLoaderAware) {
                ((ResourceLoaderAware)factory).inform(this.loader);
            }
            return factory;
        }
    }
}
