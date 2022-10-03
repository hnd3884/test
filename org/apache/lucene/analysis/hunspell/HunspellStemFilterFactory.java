package org.apache.lucene.analysis.hunspell;

import org.apache.lucene.analysis.TokenStream;
import java.util.List;
import org.apache.lucene.util.IOUtils;
import java.io.Closeable;
import java.text.ParseException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import org.apache.lucene.analysis.util.ResourceLoader;
import java.util.Map;
import org.apache.lucene.analysis.util.ResourceLoaderAware;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class HunspellStemFilterFactory extends TokenFilterFactory implements ResourceLoaderAware
{
    private static final String PARAM_DICTIONARY = "dictionary";
    private static final String PARAM_AFFIX = "affix";
    private static final String PARAM_RECURSION_CAP = "recursionCap";
    private static final String PARAM_IGNORE_CASE = "ignoreCase";
    private static final String PARAM_LONGEST_ONLY = "longestOnly";
    private final String dictionaryFiles;
    private final String affixFile;
    private final boolean ignoreCase;
    private final boolean longestOnly;
    private Dictionary dictionary;
    
    public HunspellStemFilterFactory(final Map<String, String> args) {
        super(args);
        this.dictionaryFiles = this.require(args, "dictionary");
        this.affixFile = this.get(args, "affix");
        this.ignoreCase = this.getBoolean(args, "ignoreCase", false);
        this.longestOnly = this.getBoolean(args, "longestOnly", false);
        this.getBoolean(args, "strictAffixParsing", true);
        this.getInt(args, "recursionCap", 0);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }
    
    @Override
    public void inform(final ResourceLoader loader) throws IOException {
        final String[] dicts = this.dictionaryFiles.split(",");
        InputStream affix = null;
        List<InputStream> dictionaries = new ArrayList<InputStream>();
        try {
            dictionaries = new ArrayList<InputStream>();
            for (final String file : dicts) {
                dictionaries.add(loader.openResource(file));
            }
            affix = loader.openResource(this.affixFile);
            this.dictionary = new Dictionary(affix, dictionaries, this.ignoreCase);
        }
        catch (final ParseException e) {
            throw new IOException("Unable to load hunspell data! [dictionary=" + dictionaries + ",affix=" + this.affixFile + "]", e);
        }
        finally {
            IOUtils.closeWhileHandlingException(new Closeable[] { affix });
            IOUtils.closeWhileHandlingException((Iterable)dictionaries);
        }
    }
    
    @Override
    public TokenStream create(final TokenStream tokenStream) {
        return (TokenStream)new HunspellStemFilter(tokenStream, this.dictionary, true, this.longestOnly);
    }
}
