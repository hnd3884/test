package org.apache.lucene.analysis.util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.io.Reader;
import java.io.Closeable;
import org.apache.lucene.util.IOUtils;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import org.apache.lucene.analysis.Analyzer;

public abstract class StopwordAnalyzerBase extends Analyzer
{
    protected final CharArraySet stopwords;
    
    public CharArraySet getStopwordSet() {
        return this.stopwords;
    }
    
    protected StopwordAnalyzerBase(final CharArraySet stopwords) {
        this.stopwords = ((stopwords == null) ? CharArraySet.EMPTY_SET : CharArraySet.unmodifiableSet(CharArraySet.copy(stopwords)));
    }
    
    protected StopwordAnalyzerBase() {
        this(null);
    }
    
    protected static CharArraySet loadStopwordSet(final boolean ignoreCase, final Class<? extends Analyzer> aClass, final String resource, final String comment) throws IOException {
        Reader reader = null;
        try {
            reader = IOUtils.getDecodingReader(aClass.getResourceAsStream(resource), StandardCharsets.UTF_8);
            return WordlistLoader.getWordSet(reader, comment, new CharArraySet(16, ignoreCase));
        }
        finally {
            IOUtils.close(new Closeable[] { reader });
        }
    }
    
    protected static CharArraySet loadStopwordSet(final Path stopwords) throws IOException {
        Reader reader = null;
        try {
            reader = Files.newBufferedReader(stopwords, StandardCharsets.UTF_8);
            return WordlistLoader.getWordSet(reader);
        }
        finally {
            IOUtils.close(new Closeable[] { reader });
        }
    }
    
    protected static CharArraySet loadStopwordSet(final Reader stopwords) throws IOException {
        try {
            return WordlistLoader.getWordSet(stopwords);
        }
        finally {
            IOUtils.close(new Closeable[] { stopwords });
        }
    }
}
