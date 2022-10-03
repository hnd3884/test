package org.apache.lucene.analysis.synonym;

import java.io.Reader;
import java.util.List;
import java.nio.charset.CharsetDecoder;
import java.io.InputStreamReader;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.io.IOException;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.util.TokenizerFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.util.ResourceLoader;
import org.apache.lucene.analysis.TokenStream;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import org.apache.lucene.analysis.util.ResourceLoaderAware;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class SynonymFilterFactory extends TokenFilterFactory implements ResourceLoaderAware
{
    private final boolean ignoreCase;
    private final String tokenizerFactory;
    private final String synonyms;
    private final String format;
    private final boolean expand;
    private final String analyzerName;
    private final Map<String, String> tokArgs;
    private SynonymMap map;
    
    public SynonymFilterFactory(final Map<String, String> args) {
        super(args);
        this.tokArgs = new HashMap<String, String>();
        this.ignoreCase = this.getBoolean(args, "ignoreCase", false);
        this.synonyms = this.require(args, "synonyms");
        this.format = this.get(args, "format");
        this.expand = this.getBoolean(args, "expand", true);
        this.analyzerName = this.get(args, "analyzer");
        this.tokenizerFactory = this.get(args, "tokenizerFactory");
        if (this.analyzerName != null && this.tokenizerFactory != null) {
            throw new IllegalArgumentException("Analyzer and TokenizerFactory can't be specified both: " + this.analyzerName + " and " + this.tokenizerFactory);
        }
        if (this.tokenizerFactory != null) {
            this.tokArgs.put("luceneMatchVersion", this.getLuceneMatchVersion().toString());
            final Iterator<String> itr = args.keySet().iterator();
            while (itr.hasNext()) {
                final String key = itr.next();
                this.tokArgs.put(key.replaceAll("^tokenizerFactory\\.", ""), args.get(key));
                itr.remove();
            }
        }
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }
    
    @Override
    public TokenStream create(final TokenStream input) {
        return (TokenStream)((this.map.fst == null) ? input : new SynonymFilter(input, this.map, this.ignoreCase));
    }
    
    @Override
    public void inform(final ResourceLoader loader) throws IOException {
        final TokenizerFactory factory = (this.tokenizerFactory == null) ? null : this.loadTokenizerFactory(loader, this.tokenizerFactory);
        Analyzer analyzer;
        if (this.analyzerName != null) {
            analyzer = this.loadAnalyzer(loader, this.analyzerName);
        }
        else {
            analyzer = new Analyzer() {
                protected Analyzer.TokenStreamComponents createComponents(final String fieldName) {
                    final Tokenizer tokenizer = (factory == null) ? new WhitespaceTokenizer() : factory.create();
                    final TokenStream stream = (TokenStream)(SynonymFilterFactory.this.ignoreCase ? new LowerCaseFilter((TokenStream)tokenizer) : tokenizer);
                    return new Analyzer.TokenStreamComponents(tokenizer, stream);
                }
            };
        }
        try (final Analyzer a = analyzer) {
            String formatClass = this.format;
            if (this.format == null || this.format.equals("solr")) {
                formatClass = SolrSynonymParser.class.getName();
            }
            else if (this.format.equals("wordnet")) {
                formatClass = WordnetSynonymParser.class.getName();
            }
            this.map = this.loadSynonyms(loader, formatClass, true, a);
        }
        catch (final ParseException e) {
            throw new IOException("Error parsing synonyms file:", e);
        }
    }
    
    protected SynonymMap loadSynonyms(final ResourceLoader loader, final String cname, final boolean dedup, final Analyzer analyzer) throws IOException, ParseException {
        final CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT);
        final Class<? extends SynonymMap.Parser> clazz = loader.findClass(cname, SynonymMap.Parser.class);
        SynonymMap.Parser parser;
        try {
            parser = (SynonymMap.Parser)clazz.getConstructor(Boolean.TYPE, Boolean.TYPE, Analyzer.class).newInstance(dedup, this.expand, analyzer);
        }
        catch (final Exception e) {
            throw new RuntimeException(e);
        }
        final List<String> files = this.splitFileNames(this.synonyms);
        for (final String file : files) {
            decoder.reset();
            try (final Reader isr = new InputStreamReader(loader.openResource(file), decoder)) {
                parser.parse(isr);
            }
        }
        return parser.build();
    }
    
    private TokenizerFactory loadTokenizerFactory(final ResourceLoader loader, final String cname) throws IOException {
        final Class<? extends TokenizerFactory> clazz = loader.findClass(cname, TokenizerFactory.class);
        try {
            final TokenizerFactory tokFactory = (TokenizerFactory)clazz.getConstructor(Map.class).newInstance(this.tokArgs);
            if (tokFactory instanceof ResourceLoaderAware) {
                ((ResourceLoaderAware)tokFactory).inform(loader);
            }
            return tokFactory;
        }
        catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private Analyzer loadAnalyzer(final ResourceLoader loader, final String cname) throws IOException {
        final Class<? extends Analyzer> clazz = loader.findClass(cname, Analyzer.class);
        try {
            final Analyzer analyzer = (Analyzer)clazz.getConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]);
            if (analyzer instanceof ResourceLoaderAware) {
                ((ResourceLoaderAware)analyzer).inform(loader);
            }
            return analyzer;
        }
        catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
}
