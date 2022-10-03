package org.apache.lucene.analysis.synonym;

import java.io.IOException;
import java.text.ParseException;
import org.apache.lucene.util.CharsRefBuilder;
import java.util.Arrays;
import org.apache.lucene.util.CharsRef;
import java.io.LineNumberReader;
import java.io.Reader;
import org.apache.lucene.analysis.Analyzer;

public class WordnetSynonymParser extends SynonymMap.Parser
{
    private final boolean expand;
    
    public WordnetSynonymParser(final boolean dedup, final boolean expand, final Analyzer analyzer) {
        super(dedup, analyzer);
        this.expand = expand;
    }
    
    @Override
    public void parse(final Reader in) throws IOException, ParseException {
        final LineNumberReader br = new LineNumberReader(in);
        try {
            String line = null;
            String lastSynSetID = "";
            CharsRef[] synset = new CharsRef[8];
            int synsetSize = 0;
            while ((line = br.readLine()) != null) {
                final String synSetID = line.substring(2, 11);
                if (!synSetID.equals(lastSynSetID)) {
                    this.addInternal(synset, synsetSize);
                    synsetSize = 0;
                }
                if (synset.length <= synsetSize + 1) {
                    synset = Arrays.copyOf(synset, synset.length * 2);
                }
                synset[synsetSize] = this.parseSynonym(line, new CharsRefBuilder());
                ++synsetSize;
                lastSynSetID = synSetID;
            }
            this.addInternal(synset, synsetSize);
        }
        catch (final IllegalArgumentException e) {
            final ParseException ex = new ParseException("Invalid synonym rule at line " + br.getLineNumber(), 0);
            ex.initCause(e);
            throw ex;
        }
        finally {
            br.close();
        }
    }
    
    private CharsRef parseSynonym(final String line, CharsRefBuilder reuse) throws IOException {
        if (reuse == null) {
            reuse = new CharsRefBuilder();
        }
        final int start = line.indexOf(39) + 1;
        final int end = line.lastIndexOf(39);
        final String text = line.substring(start, end).replace("''", "'");
        return this.analyze(text, reuse);
    }
    
    private void addInternal(final CharsRef[] synset, final int size) {
        if (size <= 1) {
            return;
        }
        if (this.expand) {
            for (int i = 0; i < size; ++i) {
                for (int j = 0; j < size; ++j) {
                    this.add(synset[i], synset[j], false);
                }
            }
        }
        else {
            for (int i = 0; i < size; ++i) {
                this.add(synset[i], synset[0], false);
            }
        }
    }
}
