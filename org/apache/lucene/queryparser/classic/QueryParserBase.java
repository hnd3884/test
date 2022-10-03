package org.apache.lucene.queryparser.classic;

import org.apache.lucene.search.BoostQuery;
import java.util.Iterator;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.analysis.TokenStream;
import java.io.IOException;
import org.apache.lucene.analysis.tokenattributes.TermToBytesRefAttribute;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.RegexpQuery;
import org.apache.lucene.search.PrefixQuery;
import java.util.Date;
import java.util.Calendar;
import java.text.DateFormat;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.MultiPhraseQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.BooleanClause;
import java.util.List;
import java.util.HashMap;
import org.apache.lucene.search.BooleanQuery;
import java.io.Reader;
import java.io.StringReader;
import org.apache.lucene.search.Query;
import org.apache.lucene.analysis.Analyzer;
import java.util.Map;
import org.apache.lucene.document.DateTools;
import java.util.TimeZone;
import java.util.Locale;
import org.apache.lucene.search.MultiTermQuery;
import org.apache.lucene.queryparser.flexible.standard.CommonQueryParserConfiguration;
import org.apache.lucene.util.QueryBuilder;

public abstract class QueryParserBase extends QueryBuilder implements CommonQueryParserConfiguration
{
    static final int CONJ_NONE = 0;
    static final int CONJ_AND = 1;
    static final int CONJ_OR = 2;
    static final int MOD_NONE = 0;
    static final int MOD_NOT = 10;
    static final int MOD_REQ = 11;
    public static final QueryParser.Operator AND_OPERATOR;
    public static final QueryParser.Operator OR_OPERATOR;
    QueryParser.Operator operator;
    boolean lowercaseExpandedTerms;
    MultiTermQuery.RewriteMethod multiTermRewriteMethod;
    boolean allowLeadingWildcard;
    protected String field;
    int phraseSlop;
    float fuzzyMinSim;
    int fuzzyPrefixLength;
    Locale locale;
    TimeZone timeZone;
    DateTools.Resolution dateResolution;
    Map<String, DateTools.Resolution> fieldToDateResolution;
    boolean analyzeRangeTerms;
    boolean autoGeneratePhraseQueries;
    int maxDeterminizedStates;
    
    protected QueryParserBase() {
        super((Analyzer)null);
        this.operator = QueryParserBase.OR_OPERATOR;
        this.lowercaseExpandedTerms = true;
        this.multiTermRewriteMethod = MultiTermQuery.CONSTANT_SCORE_REWRITE;
        this.allowLeadingWildcard = false;
        this.phraseSlop = 0;
        this.fuzzyMinSim = 2.0f;
        this.fuzzyPrefixLength = 0;
        this.locale = Locale.getDefault();
        this.timeZone = TimeZone.getDefault();
        this.dateResolution = null;
        this.fieldToDateResolution = null;
        this.analyzeRangeTerms = false;
        this.maxDeterminizedStates = 10000;
    }
    
    public void init(final String f, final Analyzer a) {
        this.setAnalyzer(a);
        this.field = f;
        this.setAutoGeneratePhraseQueries(false);
    }
    
    public abstract void ReInit(final CharStream p0);
    
    public abstract Query TopLevelQuery(final String p0) throws ParseException;
    
    public Query parse(final String query) throws ParseException {
        this.ReInit(new FastCharStream(new StringReader(query)));
        try {
            final Query res = this.TopLevelQuery(this.field);
            return (Query)((res != null) ? res : this.newBooleanQuery(false).build());
        }
        catch (final ParseException | TokenMgrError tme) {
            final ParseException e = new ParseException("Cannot parse '" + query + "': " + tme.getMessage());
            e.initCause(tme);
            throw e;
        }
        catch (final BooleanQuery.TooManyClauses tmc) {
            final ParseException e = new ParseException("Cannot parse '" + query + "': too many boolean clauses");
            e.initCause((Throwable)tmc);
            throw e;
        }
    }
    
    public String getField() {
        return this.field;
    }
    
    public final boolean getAutoGeneratePhraseQueries() {
        return this.autoGeneratePhraseQueries;
    }
    
    public final void setAutoGeneratePhraseQueries(final boolean value) {
        this.autoGeneratePhraseQueries = value;
    }
    
    public float getFuzzyMinSim() {
        return this.fuzzyMinSim;
    }
    
    public void setFuzzyMinSim(final float fuzzyMinSim) {
        this.fuzzyMinSim = fuzzyMinSim;
    }
    
    public int getFuzzyPrefixLength() {
        return this.fuzzyPrefixLength;
    }
    
    public void setFuzzyPrefixLength(final int fuzzyPrefixLength) {
        this.fuzzyPrefixLength = fuzzyPrefixLength;
    }
    
    public void setPhraseSlop(final int phraseSlop) {
        this.phraseSlop = phraseSlop;
    }
    
    public int getPhraseSlop() {
        return this.phraseSlop;
    }
    
    public void setAllowLeadingWildcard(final boolean allowLeadingWildcard) {
        this.allowLeadingWildcard = allowLeadingWildcard;
    }
    
    public boolean getAllowLeadingWildcard() {
        return this.allowLeadingWildcard;
    }
    
    public void setDefaultOperator(final QueryParser.Operator op) {
        this.operator = op;
    }
    
    public QueryParser.Operator getDefaultOperator() {
        return this.operator;
    }
    
    public void setLowercaseExpandedTerms(final boolean lowercaseExpandedTerms) {
        this.lowercaseExpandedTerms = lowercaseExpandedTerms;
    }
    
    public boolean getLowercaseExpandedTerms() {
        return this.lowercaseExpandedTerms;
    }
    
    public void setMultiTermRewriteMethod(final MultiTermQuery.RewriteMethod method) {
        this.multiTermRewriteMethod = method;
    }
    
    public MultiTermQuery.RewriteMethod getMultiTermRewriteMethod() {
        return this.multiTermRewriteMethod;
    }
    
    public void setLocale(final Locale locale) {
        this.locale = locale;
    }
    
    public Locale getLocale() {
        return this.locale;
    }
    
    public void setTimeZone(final TimeZone timeZone) {
        this.timeZone = timeZone;
    }
    
    public TimeZone getTimeZone() {
        return this.timeZone;
    }
    
    public void setDateResolution(final DateTools.Resolution dateResolution) {
        this.dateResolution = dateResolution;
    }
    
    public void setDateResolution(final String fieldName, final DateTools.Resolution dateResolution) {
        if (fieldName == null) {
            throw new IllegalArgumentException("Field cannot be null.");
        }
        if (this.fieldToDateResolution == null) {
            this.fieldToDateResolution = new HashMap<String, DateTools.Resolution>();
        }
        this.fieldToDateResolution.put(fieldName, dateResolution);
    }
    
    public DateTools.Resolution getDateResolution(final String fieldName) {
        if (fieldName == null) {
            throw new IllegalArgumentException("Field cannot be null.");
        }
        if (this.fieldToDateResolution == null) {
            return this.dateResolution;
        }
        DateTools.Resolution resolution = this.fieldToDateResolution.get(fieldName);
        if (resolution == null) {
            resolution = this.dateResolution;
        }
        return resolution;
    }
    
    public void setAnalyzeRangeTerms(final boolean analyzeRangeTerms) {
        this.analyzeRangeTerms = analyzeRangeTerms;
    }
    
    public boolean getAnalyzeRangeTerms() {
        return this.analyzeRangeTerms;
    }
    
    public void setMaxDeterminizedStates(final int maxDeterminizedStates) {
        this.maxDeterminizedStates = maxDeterminizedStates;
    }
    
    public int getMaxDeterminizedStates() {
        return this.maxDeterminizedStates;
    }
    
    protected void addClause(final List<BooleanClause> clauses, final int conj, final int mods, final Query q) {
        if (clauses.size() > 0 && conj == 1) {
            final BooleanClause c = clauses.get(clauses.size() - 1);
            if (!c.isProhibited()) {
                clauses.set(clauses.size() - 1, new BooleanClause(c.getQuery(), BooleanClause.Occur.MUST));
            }
        }
        if (clauses.size() > 0 && this.operator == QueryParserBase.AND_OPERATOR && conj == 2) {
            final BooleanClause c = clauses.get(clauses.size() - 1);
            if (!c.isProhibited()) {
                clauses.set(clauses.size() - 1, new BooleanClause(c.getQuery(), BooleanClause.Occur.SHOULD));
            }
        }
        if (q == null) {
            return;
        }
        boolean prohibited;
        boolean required;
        if (this.operator == QueryParserBase.OR_OPERATOR) {
            prohibited = (mods == 10);
            required = (mods == 11);
            if (conj == 1 && !prohibited) {
                required = true;
            }
        }
        else {
            prohibited = (mods == 10);
            required = (!prohibited && conj != 2);
        }
        if (required && !prohibited) {
            clauses.add(this.newBooleanClause(q, BooleanClause.Occur.MUST));
        }
        else if (!required && !prohibited) {
            clauses.add(this.newBooleanClause(q, BooleanClause.Occur.SHOULD));
        }
        else {
            if (required || !prohibited) {
                throw new RuntimeException("Clause cannot be both required and prohibited");
            }
            clauses.add(this.newBooleanClause(q, BooleanClause.Occur.MUST_NOT));
        }
    }
    
    protected Query getFieldQuery(final String field, final String queryText, final boolean quoted) throws ParseException {
        return this.newFieldQuery(this.getAnalyzer(), field, queryText, quoted);
    }
    
    protected Query newFieldQuery(final Analyzer analyzer, final String field, final String queryText, final boolean quoted) throws ParseException {
        final BooleanClause.Occur occur = (this.operator == QueryParser.Operator.AND) ? BooleanClause.Occur.MUST : BooleanClause.Occur.SHOULD;
        return this.createFieldQuery(analyzer, occur, field, queryText, quoted || this.autoGeneratePhraseQueries, this.phraseSlop);
    }
    
    protected Query getFieldQuery(final String field, final String queryText, final int slop) throws ParseException {
        Query query = this.getFieldQuery(field, queryText, true);
        if (query instanceof PhraseQuery) {
            final PhraseQuery.Builder builder = new PhraseQuery.Builder();
            builder.setSlop(slop);
            final PhraseQuery pq = (PhraseQuery)query;
            final Term[] terms = pq.getTerms();
            final int[] positions = pq.getPositions();
            for (int i = 0; i < terms.length; ++i) {
                builder.add(terms[i], positions[i]);
            }
            query = (Query)builder.build();
        }
        if (query instanceof MultiPhraseQuery) {
            ((MultiPhraseQuery)query).setSlop(slop);
        }
        return query;
    }
    
    protected Query getRangeQuery(final String field, String part1, String part2, final boolean startInclusive, final boolean endInclusive) throws ParseException {
        if (this.lowercaseExpandedTerms) {
            part1 = ((part1 == null) ? null : part1.toLowerCase(this.locale));
            part2 = ((part2 == null) ? null : part2.toLowerCase(this.locale));
        }
        final DateFormat df = DateFormat.getDateInstance(3, this.locale);
        df.setLenient(true);
        final DateTools.Resolution resolution = this.getDateResolution(field);
        try {
            part1 = DateTools.dateToString(df.parse(part1), resolution);
        }
        catch (final Exception ex) {}
        try {
            Date d2 = df.parse(part2);
            if (endInclusive) {
                final Calendar cal = Calendar.getInstance(this.timeZone, this.locale);
                cal.setTime(d2);
                cal.set(11, 23);
                cal.set(12, 59);
                cal.set(13, 59);
                cal.set(14, 999);
                d2 = cal.getTime();
            }
            part2 = DateTools.dateToString(d2, resolution);
        }
        catch (final Exception ex2) {}
        return this.newRangeQuery(field, part1, part2, startInclusive, endInclusive);
    }
    
    protected BooleanClause newBooleanClause(final Query q, final BooleanClause.Occur occur) {
        return new BooleanClause(q, occur);
    }
    
    protected Query newPrefixQuery(final Term prefix) {
        final PrefixQuery query = new PrefixQuery(prefix);
        query.setRewriteMethod(this.multiTermRewriteMethod);
        return (Query)query;
    }
    
    protected Query newRegexpQuery(final Term regexp) {
        final RegexpQuery query = new RegexpQuery(regexp, 65535, this.maxDeterminizedStates);
        query.setRewriteMethod(this.multiTermRewriteMethod);
        return (Query)query;
    }
    
    protected Query newFuzzyQuery(final Term term, final float minimumSimilarity, final int prefixLength) {
        final String text = term.text();
        final int numEdits = FuzzyQuery.floatToEdits(minimumSimilarity, text.codePointCount(0, text.length()));
        return (Query)new FuzzyQuery(term, numEdits, prefixLength);
    }
    
    private BytesRef analyzeMultitermTerm(final String field, final String part) {
        return this.analyzeMultitermTerm(field, part, this.getAnalyzer());
    }
    
    protected BytesRef analyzeMultitermTerm(final String field, final String part, Analyzer analyzerIn) {
        if (analyzerIn == null) {
            analyzerIn = this.getAnalyzer();
        }
        try (final TokenStream source = analyzerIn.tokenStream(field, part)) {
            source.reset();
            final TermToBytesRefAttribute termAtt = (TermToBytesRefAttribute)source.getAttribute((Class)TermToBytesRefAttribute.class);
            if (!source.incrementToken()) {
                throw new IllegalArgumentException("analyzer returned no terms for multiTerm term: " + part);
            }
            final BytesRef bytes = BytesRef.deepCopyOf(termAtt.getBytesRef());
            if (source.incrementToken()) {
                throw new IllegalArgumentException("analyzer returned too many terms for multiTerm term: " + part);
            }
            source.end();
            return bytes;
        }
        catch (final IOException e) {
            throw new RuntimeException("Error analyzing multiTerm term: " + part, e);
        }
    }
    
    protected Query newRangeQuery(final String field, final String part1, final String part2, final boolean startInclusive, final boolean endInclusive) {
        BytesRef start;
        if (part1 == null) {
            start = null;
        }
        else {
            start = (this.analyzeRangeTerms ? this.analyzeMultitermTerm(field, part1) : new BytesRef((CharSequence)part1));
        }
        BytesRef end;
        if (part2 == null) {
            end = null;
        }
        else {
            end = (this.analyzeRangeTerms ? this.analyzeMultitermTerm(field, part2) : new BytesRef((CharSequence)part2));
        }
        final TermRangeQuery query = new TermRangeQuery(field, start, end, startInclusive, endInclusive);
        query.setRewriteMethod(this.multiTermRewriteMethod);
        return (Query)query;
    }
    
    protected Query newMatchAllDocsQuery() {
        return (Query)new MatchAllDocsQuery();
    }
    
    protected Query newWildcardQuery(final Term t) {
        final WildcardQuery query = new WildcardQuery(t, this.maxDeterminizedStates);
        query.setRewriteMethod(this.multiTermRewriteMethod);
        return (Query)query;
    }
    
    protected Query getBooleanQuery(final List<BooleanClause> clauses) throws ParseException {
        return this.getBooleanQuery(clauses, false);
    }
    
    protected Query getBooleanQuery(final List<BooleanClause> clauses, final boolean disableCoord) throws ParseException {
        if (clauses.size() == 0) {
            return null;
        }
        final BooleanQuery.Builder query = this.newBooleanQuery(disableCoord);
        for (final BooleanClause clause : clauses) {
            query.add(clause);
        }
        return (Query)query.build();
    }
    
    protected Query getWildcardQuery(final String field, String termStr) throws ParseException {
        if ("*".equals(field) && "*".equals(termStr)) {
            return this.newMatchAllDocsQuery();
        }
        if (!this.allowLeadingWildcard && (termStr.startsWith("*") || termStr.startsWith("?"))) {
            throw new ParseException("'*' or '?' not allowed as first character in WildcardQuery");
        }
        if (this.lowercaseExpandedTerms) {
            termStr = termStr.toLowerCase(this.locale);
        }
        final Term t = new Term(field, termStr);
        return this.newWildcardQuery(t);
    }
    
    protected Query getRegexpQuery(final String field, String termStr) throws ParseException {
        if (this.lowercaseExpandedTerms) {
            termStr = termStr.toLowerCase(this.locale);
        }
        final Term t = new Term(field, termStr);
        return this.newRegexpQuery(t);
    }
    
    protected Query getPrefixQuery(final String field, String termStr) throws ParseException {
        if (!this.allowLeadingWildcard && termStr.startsWith("*")) {
            throw new ParseException("'*' not allowed as first character in PrefixQuery");
        }
        if (this.lowercaseExpandedTerms) {
            termStr = termStr.toLowerCase(this.locale);
        }
        final Term t = new Term(field, termStr);
        return this.newPrefixQuery(t);
    }
    
    protected Query getFuzzyQuery(final String field, String termStr, final float minSimilarity) throws ParseException {
        if (this.lowercaseExpandedTerms) {
            termStr = termStr.toLowerCase(this.locale);
        }
        final Term t = new Term(field, termStr);
        return this.newFuzzyQuery(t, minSimilarity, this.fuzzyPrefixLength);
    }
    
    Query handleBareTokenQuery(final String qfield, final Token term, final Token fuzzySlop, final boolean prefix, final boolean wildcard, final boolean fuzzy, final boolean regexp) throws ParseException {
        final String termImage = this.discardEscapeChar(term.image);
        Query q;
        if (wildcard) {
            q = this.getWildcardQuery(qfield, term.image);
        }
        else if (prefix) {
            q = this.getPrefixQuery(qfield, this.discardEscapeChar(term.image.substring(0, term.image.length() - 1)));
        }
        else if (regexp) {
            q = this.getRegexpQuery(qfield, term.image.substring(1, term.image.length() - 1));
        }
        else if (fuzzy) {
            q = this.handleBareFuzzy(qfield, fuzzySlop, termImage);
        }
        else {
            q = this.getFieldQuery(qfield, termImage, false);
        }
        return q;
    }
    
    Query handleBareFuzzy(final String qfield, final Token fuzzySlop, final String termImage) throws ParseException {
        float fms = this.fuzzyMinSim;
        try {
            fms = Float.valueOf(fuzzySlop.image.substring(1));
        }
        catch (final Exception ex) {}
        if (fms < 0.0f) {
            throw new ParseException("Minimum similarity for a FuzzyQuery has to be between 0.0f and 1.0f !");
        }
        if (fms >= 1.0f && fms != (int)fms) {
            throw new ParseException("Fractional edit distances are not allowed!");
        }
        final Query q = this.getFuzzyQuery(qfield, termImage, fms);
        return q;
    }
    
    Query handleQuotedTerm(final String qfield, final Token term, final Token fuzzySlop) throws ParseException {
        int s = this.phraseSlop;
        if (fuzzySlop != null) {
            try {
                s = Float.valueOf(fuzzySlop.image.substring(1)).intValue();
            }
            catch (final Exception ex) {}
        }
        return this.getFieldQuery(qfield, this.discardEscapeChar(term.image.substring(1, term.image.length() - 1)), s);
    }
    
    Query handleBoost(Query q, final Token boost) {
        if (boost != null) {
            float f = 1.0f;
            try {
                f = Float.valueOf(boost.image);
            }
            catch (final Exception ex) {}
            if (q != null) {
                q = (Query)new BoostQuery(q, f);
            }
        }
        return q;
    }
    
    String discardEscapeChar(final String input) throws ParseException {
        final char[] output = new char[input.length()];
        int length = 0;
        boolean lastCharWasEscapeChar = false;
        int codePointMultiplier = 0;
        int codePoint = 0;
        for (int i = 0; i < input.length(); ++i) {
            final char curChar = input.charAt(i);
            if (codePointMultiplier > 0) {
                codePoint += hexToInt(curChar) * codePointMultiplier;
                codePointMultiplier >>>= 4;
                if (codePointMultiplier == 0) {
                    output[length++] = (char)codePoint;
                    codePoint = 0;
                }
            }
            else if (lastCharWasEscapeChar) {
                if (curChar == 'u') {
                    codePointMultiplier = 4096;
                }
                else {
                    output[length] = curChar;
                    ++length;
                }
                lastCharWasEscapeChar = false;
            }
            else if (curChar == '\\') {
                lastCharWasEscapeChar = true;
            }
            else {
                output[length] = curChar;
                ++length;
            }
        }
        if (codePointMultiplier > 0) {
            throw new ParseException("Truncated unicode escape sequence.");
        }
        if (lastCharWasEscapeChar) {
            throw new ParseException("Term can not end with escape character.");
        }
        return new String(output, 0, length);
    }
    
    static final int hexToInt(final char c) throws ParseException {
        if ('0' <= c && c <= '9') {
            return c - '0';
        }
        if ('a' <= c && c <= 'f') {
            return c - 'a' + 10;
        }
        if ('A' <= c && c <= 'F') {
            return c - 'A' + 10;
        }
        throw new ParseException("Non-hex character in Unicode escape sequence: " + c);
    }
    
    public static String escape(final String s) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); ++i) {
            final char c = s.charAt(i);
            if (c == '\\' || c == '+' || c == '-' || c == '!' || c == '(' || c == ')' || c == ':' || c == '^' || c == '[' || c == ']' || c == '\"' || c == '{' || c == '}' || c == '~' || c == '*' || c == '?' || c == '|' || c == '&' || c == '/') {
                sb.append('\\');
            }
            sb.append(c);
        }
        return sb.toString();
    }
    
    static {
        AND_OPERATOR = QueryParser.Operator.AND;
        OR_OPERATOR = QueryParser.Operator.OR;
    }
    
    public static class MethodRemovedUseAnother extends Throwable
    {
    }
}
