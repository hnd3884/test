package org.apache.lucene.queryparser.surround.parser;

import java.util.Iterator;
import org.apache.lucene.queryparser.surround.query.SrndTruncQuery;
import org.apache.lucene.queryparser.surround.query.SrndPrefixQuery;
import org.apache.lucene.queryparser.surround.query.SrndTermQuery;
import org.apache.lucene.queryparser.surround.query.DistanceQuery;
import org.apache.lucene.queryparser.surround.query.NotQuery;
import org.apache.lucene.queryparser.surround.query.AndQuery;
import org.apache.lucene.queryparser.surround.query.OrQuery;
import org.apache.lucene.queryparser.surround.query.FieldsQuery;
import java.util.ArrayList;
import java.io.Reader;
import java.io.StringReader;
import org.apache.lucene.queryparser.surround.query.SrndQuery;
import java.util.List;

public class QueryParser implements QueryParserConstants
{
    final int minimumPrefixLength = 3;
    final int minimumCharsInTrunc = 3;
    final String truncationErrorMessage = "Too unrestrictive truncation: ";
    final String boostErrorMessage = "Cannot handle boost value: ";
    final char truncator = '*';
    final char anyChar = '?';
    final char quote = '\"';
    final char fieldOperator = ':';
    final char comma = ',';
    final char carat = '^';
    public QueryParserTokenManager token_source;
    public Token token;
    public Token jj_nt;
    private int jj_ntk;
    private Token jj_scanpos;
    private Token jj_lastpos;
    private int jj_la;
    private int jj_gen;
    private final int[] jj_la1;
    private static int[] jj_la1_0;
    private final JJCalls[] jj_2_rtns;
    private boolean jj_rescan;
    private int jj_gc;
    private final LookaheadSuccess jj_ls;
    private List<int[]> jj_expentries;
    private int[] jj_expentry;
    private int jj_kind;
    private int[] jj_lasttokens;
    private int jj_endpos;
    
    public static SrndQuery parse(final String query) throws ParseException {
        final QueryParser parser = new QueryParser();
        return parser.parse2(query);
    }
    
    public QueryParser() {
        this(new FastCharStream(new StringReader("")));
    }
    
    public SrndQuery parse2(final String query) throws ParseException {
        this.ReInit(new FastCharStream(new StringReader(query)));
        try {
            return this.TopSrndQuery();
        }
        catch (final TokenMgrError tme) {
            throw new ParseException(tme.getMessage());
        }
    }
    
    protected SrndQuery getFieldsQuery(final SrndQuery q, final ArrayList<String> fieldNames) {
        return new FieldsQuery(q, fieldNames, ':');
    }
    
    protected SrndQuery getOrQuery(final List<SrndQuery> queries, final boolean infix, final Token orToken) {
        return new OrQuery(queries, infix, orToken.image);
    }
    
    protected SrndQuery getAndQuery(final List<SrndQuery> queries, final boolean infix, final Token andToken) {
        return new AndQuery(queries, infix, andToken.image);
    }
    
    protected SrndQuery getNotQuery(final List<SrndQuery> queries, final Token notToken) {
        return new NotQuery(queries, notToken.image);
    }
    
    protected static int getOpDistance(final String distanceOp) {
        return (distanceOp.length() == 1) ? 1 : Integer.parseInt(distanceOp.substring(0, distanceOp.length() - 1));
    }
    
    protected static void checkDistanceSubQueries(final DistanceQuery distq, final String opName) throws ParseException {
        final String m = distq.distanceSubQueryNotAllowed();
        if (m != null) {
            throw new ParseException("Operator " + opName + ": " + m);
        }
    }
    
    protected SrndQuery getDistanceQuery(final List<SrndQuery> queries, final boolean infix, final Token dToken, final boolean ordered) throws ParseException {
        final DistanceQuery dq = new DistanceQuery(queries, infix, getOpDistance(dToken.image), dToken.image, ordered);
        checkDistanceSubQueries(dq, dToken.image);
        return dq;
    }
    
    protected SrndQuery getTermQuery(final String term, final boolean quoted) {
        return new SrndTermQuery(term, quoted);
    }
    
    protected boolean allowedSuffix(final String suffixed) {
        return suffixed.length() - 1 >= 3;
    }
    
    protected SrndQuery getPrefixQuery(final String prefix, final boolean quoted) {
        return new SrndPrefixQuery(prefix, quoted, '*');
    }
    
    protected boolean allowedTruncation(final String truncated) {
        int nrNormalChars = 0;
        for (int i = 0; i < truncated.length(); ++i) {
            final char c = truncated.charAt(i);
            if (c != '*' && c != '?') {
                ++nrNormalChars;
            }
        }
        return nrNormalChars >= 3;
    }
    
    protected SrndQuery getTruncQuery(final String truncated) {
        return new SrndTruncQuery(truncated, '*', '?');
    }
    
    public final SrndQuery TopSrndQuery() throws ParseException {
        final SrndQuery q = this.FieldsQuery();
        this.jj_consume_token(0);
        return q;
    }
    
    public final SrndQuery FieldsQuery() throws ParseException {
        final ArrayList<String> fieldNames = this.OptionalFields();
        final SrndQuery q = this.OrQuery();
        return (fieldNames == null) ? q : this.getFieldsQuery(q, fieldNames);
    }
    
    public final ArrayList<String> OptionalFields() throws ParseException {
        ArrayList<String> fieldNames = null;
        while (this.jj_2_1(2)) {
            final Token fieldName = this.jj_consume_token(22);
            this.jj_consume_token(16);
            if (fieldNames == null) {
                fieldNames = new ArrayList<String>();
            }
            fieldNames.add(fieldName.image);
        }
        return fieldNames;
    }
    
    public final SrndQuery OrQuery() throws ParseException {
        ArrayList<SrndQuery> queries = null;
        Token oprt = null;
        SrndQuery q = this.AndQuery();
        while (true) {
            switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                case 8: {
                    oprt = this.jj_consume_token(8);
                    if (queries == null) {
                        queries = new ArrayList<SrndQuery>();
                        queries.add(q);
                    }
                    q = this.AndQuery();
                    queries.add(q);
                    continue;
                }
                default: {
                    this.jj_la1[0] = this.jj_gen;
                    return (queries == null) ? q : this.getOrQuery(queries, true, oprt);
                }
            }
        }
    }
    
    public final SrndQuery AndQuery() throws ParseException {
        ArrayList<SrndQuery> queries = null;
        Token oprt = null;
        SrndQuery q = this.NotQuery();
        while (true) {
            switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                case 9: {
                    oprt = this.jj_consume_token(9);
                    if (queries == null) {
                        queries = new ArrayList<SrndQuery>();
                        queries.add(q);
                    }
                    q = this.NotQuery();
                    queries.add(q);
                    continue;
                }
                default: {
                    this.jj_la1[1] = this.jj_gen;
                    return (queries == null) ? q : this.getAndQuery(queries, true, oprt);
                }
            }
        }
    }
    
    public final SrndQuery NotQuery() throws ParseException {
        ArrayList<SrndQuery> queries = null;
        Token oprt = null;
        SrndQuery q = this.NQuery();
        while (true) {
            switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                case 10: {
                    oprt = this.jj_consume_token(10);
                    if (queries == null) {
                        queries = new ArrayList<SrndQuery>();
                        queries.add(q);
                    }
                    q = this.NQuery();
                    queries.add(q);
                    continue;
                }
                default: {
                    this.jj_la1[2] = this.jj_gen;
                    return (queries == null) ? q : this.getNotQuery(queries, oprt);
                }
            }
        }
    }
    
    public final SrndQuery NQuery() throws ParseException {
        SrndQuery q = this.WQuery();
        while (true) {
            switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                case 12: {
                    final Token dt = this.jj_consume_token(12);
                    final ArrayList<SrndQuery> queries = new ArrayList<SrndQuery>();
                    queries.add(q);
                    q = this.WQuery();
                    queries.add(q);
                    q = this.getDistanceQuery(queries, true, dt, false);
                    continue;
                }
                default: {
                    this.jj_la1[3] = this.jj_gen;
                    return q;
                }
            }
        }
    }
    
    public final SrndQuery WQuery() throws ParseException {
        SrndQuery q = this.PrimaryQuery();
        while (true) {
            switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                case 11: {
                    final Token wt = this.jj_consume_token(11);
                    final ArrayList<SrndQuery> queries = new ArrayList<SrndQuery>();
                    queries.add(q);
                    q = this.PrimaryQuery();
                    queries.add(q);
                    q = this.getDistanceQuery(queries, true, wt, true);
                    continue;
                }
                default: {
                    this.jj_la1[4] = this.jj_gen;
                    return q;
                }
            }
        }
    }
    
    public final SrndQuery PrimaryQuery() throws ParseException {
        SrndQuery q = null;
        switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
            case 13: {
                this.jj_consume_token(13);
                q = this.FieldsQuery();
                this.jj_consume_token(14);
                break;
            }
            case 8:
            case 9:
            case 11:
            case 12: {
                q = this.PrefixOperatorQuery();
                break;
            }
            case 18:
            case 19:
            case 20:
            case 21:
            case 22: {
                q = this.SimpleTerm();
                break;
            }
            default: {
                this.jj_la1[5] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
        this.OptionalWeights(q);
        return q;
    }
    
    public final SrndQuery PrefixOperatorQuery() throws ParseException {
        switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
            case 8: {
                final Token oprt = this.jj_consume_token(8);
                final List<SrndQuery> queries = this.FieldsQueryList();
                return this.getOrQuery(queries, false, oprt);
            }
            case 9: {
                final Token oprt = this.jj_consume_token(9);
                final List<SrndQuery> queries = this.FieldsQueryList();
                return this.getAndQuery(queries, false, oprt);
            }
            case 12: {
                final Token oprt = this.jj_consume_token(12);
                final List<SrndQuery> queries = this.FieldsQueryList();
                return this.getDistanceQuery(queries, false, oprt, false);
            }
            case 11: {
                final Token oprt = this.jj_consume_token(11);
                final List<SrndQuery> queries = this.FieldsQueryList();
                return this.getDistanceQuery(queries, false, oprt, true);
            }
            default: {
                this.jj_la1[6] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
    }
    
    public final List<SrndQuery> FieldsQueryList() throws ParseException {
        final ArrayList<SrndQuery> queries = new ArrayList<SrndQuery>();
        this.jj_consume_token(13);
        SrndQuery q = this.FieldsQuery();
        queries.add(q);
        while (true) {
            this.jj_consume_token(15);
            q = this.FieldsQuery();
            queries.add(q);
            switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                case 15: {
                    continue;
                }
                default: {
                    this.jj_la1[7] = this.jj_gen;
                    this.jj_consume_token(14);
                    return queries;
                }
            }
        }
    }
    
    public final SrndQuery SimpleTerm() throws ParseException {
        switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
            case 22: {
                final Token term = this.jj_consume_token(22);
                return this.getTermQuery(term.image, false);
            }
            case 19: {
                final Token term = this.jj_consume_token(19);
                return this.getTermQuery(term.image.substring(1, term.image.length() - 1), true);
            }
            case 20: {
                final Token term = this.jj_consume_token(20);
                if (!this.allowedSuffix(term.image)) {
                    throw new ParseException("Too unrestrictive truncation: " + term.image);
                }
                return this.getPrefixQuery(term.image.substring(0, term.image.length() - 1), false);
            }
            case 21: {
                final Token term = this.jj_consume_token(21);
                if (!this.allowedTruncation(term.image)) {
                    throw new ParseException("Too unrestrictive truncation: " + term.image);
                }
                return this.getTruncQuery(term.image);
            }
            case 18: {
                final Token term = this.jj_consume_token(18);
                if (term.image.length() - 3 < 3) {
                    throw new ParseException("Too unrestrictive truncation: " + term.image);
                }
                return this.getPrefixQuery(term.image.substring(1, term.image.length() - 2), true);
            }
            default: {
                this.jj_la1[8] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
    }
    
    public final void OptionalWeights(final SrndQuery q) throws ParseException {
        Token weight = null;
        while (true) {
            switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                case 17: {
                    this.jj_consume_token(17);
                    weight = this.jj_consume_token(23);
                    float f;
                    try {
                        f = Float.valueOf(weight.image);
                    }
                    catch (final Exception floatExc) {
                        throw new ParseException("Cannot handle boost value: " + weight.image + " (" + floatExc + ")");
                    }
                    if (f <= 0.0) {
                        throw new ParseException("Cannot handle boost value: " + weight.image);
                    }
                    q.setWeight(f * q.getWeight());
                    continue;
                }
                default: {
                    this.jj_la1[9] = this.jj_gen;
                }
            }
        }
    }
    
    private boolean jj_2_1(final int xla) {
        this.jj_la = xla;
        final Token token = this.token;
        this.jj_scanpos = token;
        this.jj_lastpos = token;
        try {
            return !this.jj_3_1();
        }
        catch (final LookaheadSuccess ls) {
            return true;
        }
        finally {
            this.jj_save(0, xla);
        }
    }
    
    private boolean jj_3_1() {
        return this.jj_scan_token(22) || this.jj_scan_token(16);
    }
    
    private static void jj_la1_init_0() {
        QueryParser.jj_la1_0 = new int[] { 256, 512, 1024, 4096, 2048, 8141568, 6912, 32768, 8126464, 131072 };
    }
    
    public QueryParser(final CharStream stream) {
        this.jj_la1 = new int[10];
        this.jj_2_rtns = new JJCalls[1];
        this.jj_rescan = false;
        this.jj_gc = 0;
        this.jj_ls = new LookaheadSuccess();
        this.jj_expentries = new ArrayList<int[]>();
        this.jj_kind = -1;
        this.jj_lasttokens = new int[100];
        this.token_source = new QueryParserTokenManager(stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 10; ++i) {
            this.jj_la1[i] = -1;
        }
        for (int i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }
    
    public void ReInit(final CharStream stream) {
        this.token_source.ReInit(stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 10; ++i) {
            this.jj_la1[i] = -1;
        }
        for (int i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }
    
    public QueryParser(final QueryParserTokenManager tm) {
        this.jj_la1 = new int[10];
        this.jj_2_rtns = new JJCalls[1];
        this.jj_rescan = false;
        this.jj_gc = 0;
        this.jj_ls = new LookaheadSuccess();
        this.jj_expentries = new ArrayList<int[]>();
        this.jj_kind = -1;
        this.jj_lasttokens = new int[100];
        this.token_source = tm;
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 10; ++i) {
            this.jj_la1[i] = -1;
        }
        for (int i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }
    
    public void ReInit(final QueryParserTokenManager tm) {
        this.token_source = tm;
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 10; ++i) {
            this.jj_la1[i] = -1;
        }
        for (int i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }
    
    private Token jj_consume_token(final int kind) throws ParseException {
        final Token oldToken;
        if ((oldToken = this.token).next != null) {
            this.token = this.token.next;
        }
        else {
            final Token token = this.token;
            final Token nextToken = this.token_source.getNextToken();
            token.next = nextToken;
            this.token = nextToken;
        }
        this.jj_ntk = -1;
        if (this.token.kind == kind) {
            ++this.jj_gen;
            if (++this.jj_gc > 100) {
                this.jj_gc = 0;
                for (int i = 0; i < this.jj_2_rtns.length; ++i) {
                    for (JJCalls c = this.jj_2_rtns[i]; c != null; c = c.next) {
                        if (c.gen < this.jj_gen) {
                            c.first = null;
                        }
                    }
                }
            }
            return this.token;
        }
        this.token = oldToken;
        this.jj_kind = kind;
        throw this.generateParseException();
    }
    
    private boolean jj_scan_token(final int kind) {
        if (this.jj_scanpos == this.jj_lastpos) {
            --this.jj_la;
            if (this.jj_scanpos.next == null) {
                final Token jj_scanpos = this.jj_scanpos;
                final Token nextToken = this.token_source.getNextToken();
                jj_scanpos.next = nextToken;
                this.jj_scanpos = nextToken;
                this.jj_lastpos = nextToken;
            }
            else {
                final Token next = this.jj_scanpos.next;
                this.jj_scanpos = next;
                this.jj_lastpos = next;
            }
        }
        else {
            this.jj_scanpos = this.jj_scanpos.next;
        }
        if (this.jj_rescan) {
            int i = 0;
            Token tok;
            for (tok = this.token; tok != null && tok != this.jj_scanpos; tok = tok.next) {
                ++i;
            }
            if (tok != null) {
                this.jj_add_error_token(kind, i);
            }
        }
        if (this.jj_scanpos.kind != kind) {
            return true;
        }
        if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
            throw this.jj_ls;
        }
        return false;
    }
    
    public final Token getNextToken() {
        if (this.token.next != null) {
            this.token = this.token.next;
        }
        else {
            final Token token = this.token;
            final Token nextToken = this.token_source.getNextToken();
            token.next = nextToken;
            this.token = nextToken;
        }
        this.jj_ntk = -1;
        ++this.jj_gen;
        return this.token;
    }
    
    public final Token getToken(final int index) {
        Token t = this.token;
        for (int i = 0; i < index; ++i) {
            if (t.next != null) {
                t = t.next;
            }
            else {
                final Token token = t;
                final Token nextToken = this.token_source.getNextToken();
                token.next = nextToken;
                t = nextToken;
            }
        }
        return t;
    }
    
    private int jj_ntk() {
        final Token next = this.token.next;
        this.jj_nt = next;
        if (next == null) {
            final Token token = this.token;
            final Token nextToken = this.token_source.getNextToken();
            token.next = nextToken;
            return this.jj_ntk = nextToken.kind;
        }
        return this.jj_ntk = this.jj_nt.kind;
    }
    
    private void jj_add_error_token(final int kind, final int pos) {
        if (pos >= 100) {
            return;
        }
        if (pos == this.jj_endpos + 1) {
            this.jj_lasttokens[this.jj_endpos++] = kind;
        }
        else if (this.jj_endpos != 0) {
            this.jj_expentry = new int[this.jj_endpos];
            for (int i = 0; i < this.jj_endpos; ++i) {
                this.jj_expentry[i] = this.jj_lasttokens[i];
            }
        Label_0092:
            for (final int[] oldentry : this.jj_expentries) {
                if (oldentry.length == this.jj_expentry.length) {
                    for (int j = 0; j < this.jj_expentry.length; ++j) {
                        if (oldentry[j] != this.jj_expentry[j]) {
                            continue Label_0092;
                        }
                    }
                    this.jj_expentries.add(this.jj_expentry);
                    break;
                }
            }
            if (pos != 0) {
                this.jj_lasttokens[(this.jj_endpos = pos) - 1] = kind;
            }
        }
    }
    
    public ParseException generateParseException() {
        this.jj_expentries.clear();
        final boolean[] la1tokens = new boolean[24];
        if (this.jj_kind >= 0) {
            la1tokens[this.jj_kind] = true;
            this.jj_kind = -1;
        }
        for (int i = 0; i < 10; ++i) {
            if (this.jj_la1[i] == this.jj_gen) {
                for (int j = 0; j < 32; ++j) {
                    if ((QueryParser.jj_la1_0[i] & 1 << j) != 0x0) {
                        la1tokens[j] = true;
                    }
                }
            }
        }
        for (int i = 0; i < 24; ++i) {
            if (la1tokens[i]) {
                (this.jj_expentry = new int[1])[0] = i;
                this.jj_expentries.add(this.jj_expentry);
            }
        }
        this.jj_endpos = 0;
        this.jj_rescan_token();
        this.jj_add_error_token(0, 0);
        final int[][] exptokseq = new int[this.jj_expentries.size()][];
        for (int k = 0; k < this.jj_expentries.size(); ++k) {
            exptokseq[k] = this.jj_expentries.get(k);
        }
        return new ParseException(this.token, exptokseq, QueryParser.tokenImage);
    }
    
    public final void enable_tracing() {
    }
    
    public final void disable_tracing() {
    }
    
    private void jj_rescan_token() {
        this.jj_rescan = true;
        for (int i = 0; i < 1; ++i) {
            try {
                JJCalls p = this.jj_2_rtns[i];
                do {
                    if (p.gen > this.jj_gen) {
                        this.jj_la = p.arg;
                        final Token first = p.first;
                        this.jj_scanpos = first;
                        this.jj_lastpos = first;
                        switch (i) {
                            case 0: {
                                this.jj_3_1();
                                break;
                            }
                        }
                    }
                    p = p.next;
                } while (p != null);
            }
            catch (final LookaheadSuccess lookaheadSuccess) {}
        }
        this.jj_rescan = false;
    }
    
    private void jj_save(final int index, final int xla) {
        JJCalls p;
        for (p = this.jj_2_rtns[index]; p.gen > this.jj_gen; p = p.next) {
            if (p.next == null) {
                final JJCalls jjCalls = p;
                final JJCalls next = new JJCalls();
                jjCalls.next = next;
                p = next;
                break;
            }
        }
        p.gen = this.jj_gen + xla - this.jj_la;
        p.first = this.token;
        p.arg = xla;
    }
    
    static {
        jj_la1_init_0();
    }
    
    private static final class LookaheadSuccess extends Error
    {
    }
    
    static final class JJCalls
    {
        int gen;
        Token first;
        int arg;
        JJCalls next;
    }
}
