package org.apache.lucene.analysis.synonym;

import java.util.ArrayList;
import org.apache.lucene.util.CharsRefBuilder;
import org.apache.lucene.util.CharsRef;
import java.io.IOException;
import java.text.ParseException;
import java.io.BufferedReader;
import java.io.LineNumberReader;
import java.io.Reader;
import org.apache.lucene.analysis.Analyzer;

public class SolrSynonymParser extends SynonymMap.Parser
{
    private final boolean expand;
    
    public SolrSynonymParser(final boolean dedup, final boolean expand, final Analyzer analyzer) {
        super(dedup, analyzer);
        this.expand = expand;
    }
    
    @Override
    public void parse(final Reader in) throws IOException, ParseException {
        final LineNumberReader br = new LineNumberReader(in);
        try {
            this.addInternal(br);
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
    
    private void addInternal(final BufferedReader in) throws IOException {
        String line = null;
        while ((line = in.readLine()) != null) {
            if (line.length() != 0) {
                if (line.charAt(0) == '#') {
                    continue;
                }
                final String[] sides = split(line, "=>");
                if (sides.length > 1) {
                    if (sides.length != 2) {
                        throw new IllegalArgumentException("more than one explicit mapping specified on the same line");
                    }
                    final String[] inputStrings = split(sides[0], ",");
                    final CharsRef[] inputs = new CharsRef[inputStrings.length];
                    for (int i = 0; i < inputs.length; ++i) {
                        inputs[i] = this.analyze(this.unescape(inputStrings[i]).trim(), new CharsRefBuilder());
                    }
                    final String[] outputStrings = split(sides[1], ",");
                    final CharsRef[] outputs = new CharsRef[outputStrings.length];
                    for (int j = 0; j < outputs.length; ++j) {
                        outputs[j] = this.analyze(this.unescape(outputStrings[j]).trim(), new CharsRefBuilder());
                    }
                    for (int j = 0; j < inputs.length; ++j) {
                        for (int k = 0; k < outputs.length; ++k) {
                            this.add(inputs[j], outputs[k], false);
                        }
                    }
                }
                else {
                    final String[] inputStrings = split(line, ",");
                    final CharsRef[] inputs = new CharsRef[inputStrings.length];
                    for (int i = 0; i < inputs.length; ++i) {
                        inputs[i] = this.analyze(this.unescape(inputStrings[i]).trim(), new CharsRefBuilder());
                    }
                    if (this.expand) {
                        for (int i = 0; i < inputs.length; ++i) {
                            for (int l = 0; l < inputs.length; ++l) {
                                if (i != l) {
                                    this.add(inputs[i], inputs[l], true);
                                }
                            }
                        }
                    }
                    else {
                        for (int i = 0; i < inputs.length; ++i) {
                            this.add(inputs[i], inputs[0], false);
                        }
                    }
                }
            }
        }
    }
    
    private static String[] split(final String s, final String separator) {
        final ArrayList<String> list = new ArrayList<String>(2);
        StringBuilder sb = new StringBuilder();
        int pos = 0;
        final int end = s.length();
        while (pos < end) {
            if (s.startsWith(separator, pos)) {
                if (sb.length() > 0) {
                    list.add(sb.toString());
                    sb = new StringBuilder();
                }
                pos += separator.length();
            }
            else {
                char ch = s.charAt(pos++);
                if (ch == '\\') {
                    sb.append(ch);
                    if (pos >= end) {
                        break;
                    }
                    ch = s.charAt(pos++);
                }
                sb.append(ch);
            }
        }
        if (sb.length() > 0) {
            list.add(sb.toString());
        }
        return list.toArray(new String[list.size()]);
    }
    
    private String unescape(final String s) {
        if (s.indexOf("\\") >= 0) {
            final StringBuilder sb = new StringBuilder();
            for (int i = 0; i < s.length(); ++i) {
                final char ch = s.charAt(i);
                if (ch == '\\' && i < s.length() - 1) {
                    sb.append(s.charAt(++i));
                }
                else {
                    sb.append(ch);
                }
            }
            return sb.toString();
        }
        return s;
    }
}
