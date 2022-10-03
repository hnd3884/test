package org.apache.lucene.queryparser.analyzing;

import org.apache.lucene.analysis.TokenStream;
import java.io.IOException;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import java.util.regex.Matcher;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.Query;
import org.apache.lucene.analysis.Analyzer;
import java.util.regex.Pattern;
import org.apache.lucene.queryparser.classic.QueryParser;

public class AnalyzingQueryParser extends QueryParser
{
    private final Pattern wildcardPattern;
    
    public AnalyzingQueryParser(final String field, final Analyzer analyzer) {
        super(field, analyzer);
        this.wildcardPattern = Pattern.compile("(\\.)|([?*]+)");
        this.setAnalyzeRangeTerms(true);
    }
    
    @Override
    protected Query getWildcardQuery(final String field, final String termStr) throws ParseException {
        if (termStr == null) {
            throw new ParseException("Passed null value as term to getWildcardQuery");
        }
        if (!this.getAllowLeadingWildcard() && (termStr.startsWith("*") || termStr.startsWith("?"))) {
            throw new ParseException("'*' or '?' not allowed as first character in WildcardQuery unless getAllowLeadingWildcard() returns true");
        }
        final Matcher wildcardMatcher = this.wildcardPattern.matcher(termStr);
        final StringBuilder sb = new StringBuilder();
        int last = 0;
        while (wildcardMatcher.find()) {
            if (wildcardMatcher.group(1) != null) {
                continue;
            }
            if (wildcardMatcher.start() > 0) {
                final String chunk = termStr.substring(last, wildcardMatcher.start());
                final String analyzed = this.analyzeSingleChunk(field, termStr, chunk);
                sb.append(analyzed);
            }
            sb.append(wildcardMatcher.group(2));
            last = wildcardMatcher.end();
        }
        if (last < termStr.length()) {
            sb.append(this.analyzeSingleChunk(field, termStr, termStr.substring(last)));
        }
        return super.getWildcardQuery(field, sb.toString());
    }
    
    @Override
    protected Query getPrefixQuery(final String field, final String termStr) throws ParseException {
        final String analyzed = this.analyzeSingleChunk(field, termStr, termStr);
        return super.getPrefixQuery(field, analyzed);
    }
    
    @Override
    protected Query getFuzzyQuery(final String field, final String termStr, final float minSimilarity) throws ParseException {
        final String analyzed = this.analyzeSingleChunk(field, termStr, termStr);
        return super.getFuzzyQuery(field, analyzed, minSimilarity);
    }
    
    protected String analyzeSingleChunk(final String field, final String termStr, final String chunk) throws ParseException {
        String analyzed = null;
        try (final TokenStream stream = this.getAnalyzer().tokenStream(field, chunk)) {
            stream.reset();
            final CharTermAttribute termAtt = (CharTermAttribute)stream.getAttribute((Class)CharTermAttribute.class);
            if (!stream.incrementToken()) {
                stream.end();
                throw new ParseException(String.format(this.getLocale(), "Analyzer returned nothing for \"%s\"", chunk));
            }
            analyzed = termAtt.toString();
            StringBuilder multipleOutputs = null;
            while (stream.incrementToken()) {
                if (null == multipleOutputs) {
                    multipleOutputs = new StringBuilder();
                    multipleOutputs.append('\"');
                    multipleOutputs.append(analyzed);
                    multipleOutputs.append('\"');
                }
                multipleOutputs.append(',');
                multipleOutputs.append('\"');
                multipleOutputs.append(termAtt.toString());
                multipleOutputs.append('\"');
            }
            stream.end();
            if (null != multipleOutputs) {
                throw new ParseException(String.format(this.getLocale(), "Analyzer created multiple terms for \"%s\": %s", chunk, multipleOutputs.toString()));
            }
        }
        catch (final IOException e) {
            throw new ParseException(String.format(this.getLocale(), "IO error while trying to analyze single term: \"%s\"", termStr));
        }
        return analyzed;
    }
}
