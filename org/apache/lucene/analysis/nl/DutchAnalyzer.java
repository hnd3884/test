package org.apache.lucene.analysis.nl;

import org.apache.lucene.analysis.util.WordlistLoader;
import org.apache.lucene.util.IOUtils;
import java.nio.charset.StandardCharsets;
import org.apache.lucene.analysis.Tokenizer;
import org.tartarus.snowball.SnowballProgram;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.tartarus.snowball.ext.DutchStemmer;
import org.apache.lucene.analysis.miscellaneous.SetKeywordMarkerFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.std40.StandardTokenizer40;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.util.Version;
import java.io.IOException;
import org.apache.lucene.util.CharsRefBuilder;
import java.util.Set;
import org.apache.lucene.analysis.util.CharArrayMap;
import org.apache.lucene.analysis.miscellaneous.StemmerOverrideFilter;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.Analyzer;

public final class DutchAnalyzer extends Analyzer
{
    public static final String DEFAULT_STOPWORD_FILE = "dutch_stop.txt";
    private final CharArraySet stoptable;
    private CharArraySet excltable;
    private final StemmerOverrideFilter.StemmerOverrideMap stemdict;
    
    public static CharArraySet getDefaultStopSet() {
        return DefaultSetHolder.DEFAULT_STOP_SET;
    }
    
    public DutchAnalyzer() {
        this(DefaultSetHolder.DEFAULT_STOP_SET, CharArraySet.EMPTY_SET, DefaultSetHolder.DEFAULT_STEM_DICT);
    }
    
    public DutchAnalyzer(final CharArraySet stopwords) {
        this(stopwords, CharArraySet.EMPTY_SET, DefaultSetHolder.DEFAULT_STEM_DICT);
    }
    
    public DutchAnalyzer(final CharArraySet stopwords, final CharArraySet stemExclusionTable) {
        this(stopwords, stemExclusionTable, DefaultSetHolder.DEFAULT_STEM_DICT);
    }
    
    public DutchAnalyzer(final CharArraySet stopwords, final CharArraySet stemExclusionTable, final CharArrayMap<String> stemOverrideDict) {
        this.excltable = CharArraySet.EMPTY_SET;
        this.stoptable = CharArraySet.unmodifiableSet(CharArraySet.copy(stopwords));
        this.excltable = CharArraySet.unmodifiableSet(CharArraySet.copy(stemExclusionTable));
        if (stemOverrideDict.isEmpty()) {
            this.stemdict = null;
        }
        else {
            final StemmerOverrideFilter.Builder builder = new StemmerOverrideFilter.Builder(false);
            final CharArrayMap.EntryIterator iter = stemOverrideDict.entrySet().iterator();
            final CharsRefBuilder spare = new CharsRefBuilder();
            while (iter.hasNext()) {
                final char[] nextKey = iter.nextKey();
                spare.copyChars(nextKey, 0, nextKey.length);
                builder.add((CharSequence)spare.get(), iter.currentValue());
            }
            try {
                this.stemdict = builder.build();
            }
            catch (final IOException ex) {
                throw new RuntimeException("can not build stem dict", ex);
            }
        }
    }
    
    protected Analyzer.TokenStreamComponents createComponents(final String fieldName) {
        Tokenizer source;
        if (this.getVersion().onOrAfter(Version.LUCENE_4_7_0)) {
            source = new StandardTokenizer();
        }
        else {
            source = new StandardTokenizer40();
        }
        TokenStream result = (TokenStream)new StandardFilter((TokenStream)source);
        result = (TokenStream)new LowerCaseFilter(result);
        result = (TokenStream)new StopFilter(result, this.stoptable);
        if (!this.excltable.isEmpty()) {
            result = (TokenStream)new SetKeywordMarkerFilter(result, this.excltable);
        }
        if (this.stemdict != null) {
            result = (TokenStream)new StemmerOverrideFilter(result, this.stemdict);
        }
        result = (TokenStream)new SnowballFilter(result, new DutchStemmer());
        return new Analyzer.TokenStreamComponents(source, result);
    }
    
    private static class DefaultSetHolder
    {
        static final CharArraySet DEFAULT_STOP_SET;
        static final CharArrayMap<String> DEFAULT_STEM_DICT;
        
        static {
            try {
                DEFAULT_STOP_SET = WordlistLoader.getSnowballWordSet(IOUtils.getDecodingReader((Class)SnowballFilter.class, "dutch_stop.txt", StandardCharsets.UTF_8));
            }
            catch (final IOException ex) {
                throw new RuntimeException("Unable to load default stopword set");
            }
            (DEFAULT_STEM_DICT = new CharArrayMap<String>(4, false)).put("fiets", "fiets");
            DefaultSetHolder.DEFAULT_STEM_DICT.put("bromfiets", "bromfiets");
            DefaultSetHolder.DEFAULT_STEM_DICT.put("ei", "eier");
            DefaultSetHolder.DEFAULT_STEM_DICT.put("kind", "kinder");
        }
    }
}
