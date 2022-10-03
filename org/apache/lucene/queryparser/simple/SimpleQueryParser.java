package org.apache.lucene.queryparser.simple;

import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BoostQuery;
import org.apache.lucene.search.MatchAllDocsQuery;
import java.util.Iterator;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.MatchNoDocsQuery;
import org.apache.lucene.search.Query;
import java.util.Collections;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.BooleanClause;
import java.util.Map;
import org.apache.lucene.util.QueryBuilder;

public class SimpleQueryParser extends QueryBuilder
{
    protected final Map<String, Float> weights;
    protected final int flags;
    public static final int AND_OPERATOR = 1;
    public static final int NOT_OPERATOR = 2;
    public static final int OR_OPERATOR = 4;
    public static final int PREFIX_OPERATOR = 8;
    public static final int PHRASE_OPERATOR = 16;
    public static final int PRECEDENCE_OPERATORS = 32;
    public static final int ESCAPE_OPERATOR = 64;
    public static final int WHITESPACE_OPERATOR = 128;
    public static final int FUZZY_OPERATOR = 256;
    public static final int NEAR_OPERATOR = 512;
    private BooleanClause.Occur defaultOperator;
    
    public SimpleQueryParser(final Analyzer analyzer, final String field) {
        this(analyzer, Collections.singletonMap(field, 1.0f));
    }
    
    public SimpleQueryParser(final Analyzer analyzer, final Map<String, Float> weights) {
        this(analyzer, weights, -1);
    }
    
    public SimpleQueryParser(final Analyzer analyzer, final Map<String, Float> weights, final int flags) {
        super(analyzer);
        this.defaultOperator = BooleanClause.Occur.SHOULD;
        this.weights = weights;
        this.flags = flags;
    }
    
    public Query parse(final String queryText) {
        final char[] data = queryText.toCharArray();
        final char[] buffer = new char[data.length];
        final State state = new State(data, buffer, 0, data.length);
        this.parseSubQuery(state);
        if (state.top == null) {
            return (Query)new MatchNoDocsQuery();
        }
        return state.top;
    }
    
    private void parseSubQuery(final State state) {
        while (state.index < state.length) {
            if (state.data[state.index] == '(' && (this.flags & 0x20) != 0x0) {
                this.consumeSubQuery(state);
            }
            else if (state.data[state.index] == ')' && (this.flags & 0x20) != 0x0) {
                ++state.index;
            }
            else if (state.data[state.index] == '\"' && (this.flags & 0x10) != 0x0) {
                this.consumePhrase(state);
            }
            else if (state.data[state.index] == '+' && (this.flags & 0x1) != 0x0) {
                if (state.currentOperation == null && state.top != null) {
                    state.currentOperation = BooleanClause.Occur.MUST;
                }
                ++state.index;
            }
            else if (state.data[state.index] == '|' && (this.flags & 0x4) != 0x0) {
                if (state.currentOperation == null && state.top != null) {
                    state.currentOperation = BooleanClause.Occur.SHOULD;
                }
                ++state.index;
            }
            else {
                if (state.data[state.index] == '-' && (this.flags & 0x2) != 0x0) {
                    ++state.not;
                    ++state.index;
                    continue;
                }
                if ((state.data[state.index] == ' ' || state.data[state.index] == '\t' || state.data[state.index] == '\n' || state.data[state.index] == '\r') && (this.flags & 0x80) != 0x0) {
                    ++state.index;
                }
                else {
                    this.consumeToken(state);
                }
            }
            state.not = 0;
        }
    }
    
    private void consumeSubQuery(final State state) {
        assert (this.flags & 0x20) != 0x0;
        final int start = ++state.index;
        int precedence = 1;
        boolean escaped = false;
        while (state.index < state.length) {
            if (!escaped) {
                if (state.data[state.index] == '\\' && (this.flags & 0x40) != 0x0) {
                    escaped = true;
                    ++state.index;
                    continue;
                }
                if (state.data[state.index] == '(') {
                    ++precedence;
                }
                else if (state.data[state.index] == ')' && --precedence == 0) {
                    break;
                }
            }
            escaped = false;
            ++state.index;
        }
        if (state.index == state.length) {
            state.index = start;
        }
        else if (state.index == start) {
            state.currentOperation = null;
            ++state.index;
        }
        else {
            final State subState = new State(state.data, state.buffer, start, state.index);
            this.parseSubQuery(subState);
            this.buildQueryTree(state, subState.top);
            ++state.index;
        }
    }
    
    private void consumePhrase(final State state) {
        assert (this.flags & 0x10) != 0x0;
        final int start = ++state.index;
        int copied = 0;
        boolean escaped = false;
        boolean hasSlop = false;
        while (state.index < state.length) {
            if (!escaped) {
                if (state.data[state.index] == '\\' && (this.flags & 0x40) != 0x0) {
                    escaped = true;
                    ++state.index;
                    continue;
                }
                if (state.data[state.index] == '\"') {
                    if (state.length <= state.index + 1 || state.data[state.index + 1] != '~' || (this.flags & 0x200) == 0x0) {
                        break;
                    }
                    ++state.index;
                    if (state.length > state.index + 1) {
                        hasSlop = true;
                        break;
                    }
                    break;
                }
            }
            escaped = false;
            state.buffer[copied++] = state.data[state.index++];
        }
        if (state.index == state.length) {
            state.index = start;
        }
        else if (state.index == start) {
            state.currentOperation = null;
            ++state.index;
        }
        else {
            final String phrase = new String(state.buffer, 0, copied);
            Query branch;
            if (hasSlop) {
                branch = this.newPhraseQuery(phrase, this.parseFuzziness(state));
            }
            else {
                branch = this.newPhraseQuery(phrase, 0);
            }
            this.buildQueryTree(state, branch);
            ++state.index;
        }
    }
    
    private void consumeToken(final State state) {
        int copied = 0;
        boolean escaped = false;
        boolean prefix = false;
        boolean fuzzy = false;
        while (state.index < state.length) {
            if (!escaped) {
                if (state.data[state.index] == '\\' && (this.flags & 0x40) != 0x0) {
                    escaped = true;
                    prefix = false;
                    ++state.index;
                    continue;
                }
                if (this.tokenFinished(state)) {
                    break;
                }
                if (copied > 0 && state.data[state.index] == '~' && (this.flags & 0x100) != 0x0) {
                    fuzzy = true;
                    break;
                }
                prefix = (copied > 0 && state.data[state.index] == '*' && (this.flags & 0x8) != 0x0);
            }
            escaped = false;
            state.buffer[copied++] = state.data[state.index++];
        }
        if (copied > 0) {
            Query branch;
            if (fuzzy && (this.flags & 0x100) != 0x0) {
                final String token = new String(state.buffer, 0, copied);
                int fuzziness = this.parseFuzziness(state);
                fuzziness = Math.min(fuzziness, 2);
                if (fuzziness == 0) {
                    branch = this.newDefaultQuery(token);
                }
                else {
                    branch = this.newFuzzyQuery(token, fuzziness);
                }
            }
            else if (prefix) {
                final String token = new String(state.buffer, 0, copied - 1);
                branch = this.newPrefixQuery(token);
            }
            else {
                final String token = new String(state.buffer, 0, copied);
                branch = this.newDefaultQuery(token);
            }
            this.buildQueryTree(state, branch);
        }
    }
    
    private static BooleanQuery addClause(final BooleanQuery bq, final Query query, final BooleanClause.Occur occur) {
        final BooleanQuery.Builder newBq = new BooleanQuery.Builder();
        newBq.setDisableCoord(bq.isCoordDisabled());
        newBq.setMinimumNumberShouldMatch(bq.getMinimumNumberShouldMatch());
        for (final BooleanClause clause : bq) {
            newBq.add(clause);
        }
        newBq.add(query, occur);
        return newBq.build();
    }
    
    private void buildQueryTree(final State state, Query branch) {
        if (branch != null) {
            if (state.not % 2 == 1) {
                final BooleanQuery.Builder nq = new BooleanQuery.Builder();
                nq.add(branch, BooleanClause.Occur.MUST_NOT);
                nq.add((Query)new MatchAllDocsQuery(), BooleanClause.Occur.SHOULD);
                branch = (Query)nq.build();
            }
            if (state.top == null) {
                state.top = branch;
            }
            else {
                if (state.currentOperation == null) {
                    state.currentOperation = this.defaultOperator;
                }
                if (state.previousOperation != state.currentOperation) {
                    final BooleanQuery.Builder bq = new BooleanQuery.Builder();
                    bq.add(state.top, state.currentOperation);
                    state.top = (Query)bq.build();
                }
                state.top = (Query)addClause((BooleanQuery)state.top, branch, state.currentOperation);
                state.previousOperation = state.currentOperation;
            }
            state.currentOperation = null;
        }
    }
    
    private int parseFuzziness(final State state) {
        final char[] slopText = new char[state.length];
        int slopLength = 0;
        if (state.data[state.index] == '~') {
            while (state.index < state.length) {
                ++state.index;
                if (state.index < state.length) {
                    if (this.tokenFinished(state)) {
                        break;
                    }
                    slopText[slopLength] = state.data[state.index];
                    ++slopLength;
                }
            }
            int fuzziness = 0;
            try {
                fuzziness = Integer.parseInt(new String(slopText, 0, slopLength));
            }
            catch (final NumberFormatException ex) {}
            if (fuzziness < 0) {
                fuzziness = 0;
            }
            return fuzziness;
        }
        return 0;
    }
    
    private boolean tokenFinished(final State state) {
        return (state.data[state.index] == '\"' && (this.flags & 0x10) != 0x0) || (state.data[state.index] == '|' && (this.flags & 0x4) != 0x0) || (state.data[state.index] == '+' && (this.flags & 0x1) != 0x0) || (state.data[state.index] == '(' && (this.flags & 0x20) != 0x0) || (state.data[state.index] == ')' && (this.flags & 0x20) != 0x0) || ((state.data[state.index] == ' ' || state.data[state.index] == '\t' || state.data[state.index] == '\n' || state.data[state.index] == '\r') && (this.flags & 0x80) != 0x0);
    }
    
    protected Query newDefaultQuery(final String text) {
        final BooleanQuery.Builder bq = new BooleanQuery.Builder();
        bq.setDisableCoord(true);
        for (final Map.Entry<String, Float> entry : this.weights.entrySet()) {
            Query q = this.createBooleanQuery((String)entry.getKey(), text, this.defaultOperator);
            if (q != null) {
                final float boost = entry.getValue();
                if (boost != 1.0f) {
                    q = (Query)new BoostQuery(q, boost);
                }
                bq.add(q, BooleanClause.Occur.SHOULD);
            }
        }
        return this.simplify(bq.build());
    }
    
    protected Query newFuzzyQuery(final String text, final int fuzziness) {
        final BooleanQuery.Builder bq = new BooleanQuery.Builder();
        bq.setDisableCoord(true);
        for (final Map.Entry<String, Float> entry : this.weights.entrySet()) {
            Query q = (Query)new FuzzyQuery(new Term((String)entry.getKey(), text), fuzziness);
            final float boost = entry.getValue();
            if (boost != 1.0f) {
                q = (Query)new BoostQuery(q, boost);
            }
            bq.add(q, BooleanClause.Occur.SHOULD);
        }
        return this.simplify(bq.build());
    }
    
    protected Query newPhraseQuery(final String text, final int slop) {
        final BooleanQuery.Builder bq = new BooleanQuery.Builder();
        bq.setDisableCoord(true);
        for (final Map.Entry<String, Float> entry : this.weights.entrySet()) {
            Query q = this.createPhraseQuery((String)entry.getKey(), text, slop);
            if (q != null) {
                final float boost = entry.getValue();
                if (boost != 1.0f) {
                    q = (Query)new BoostQuery(q, boost);
                }
                bq.add(q, BooleanClause.Occur.SHOULD);
            }
        }
        return this.simplify(bq.build());
    }
    
    protected Query newPrefixQuery(final String text) {
        final BooleanQuery.Builder bq = new BooleanQuery.Builder();
        bq.setDisableCoord(true);
        for (final Map.Entry<String, Float> entry : this.weights.entrySet()) {
            Query q = (Query)new PrefixQuery(new Term((String)entry.getKey(), text));
            final float boost = entry.getValue();
            if (boost != 1.0f) {
                q = (Query)new BoostQuery(q, boost);
            }
            bq.add(q, BooleanClause.Occur.SHOULD);
        }
        return this.simplify(bq.build());
    }
    
    protected Query simplify(final BooleanQuery bq) {
        if (bq.clauses().isEmpty()) {
            return null;
        }
        if (bq.clauses().size() == 1) {
            return bq.clauses().iterator().next().getQuery();
        }
        return (Query)bq;
    }
    
    public BooleanClause.Occur getDefaultOperator() {
        return this.defaultOperator;
    }
    
    public void setDefaultOperator(final BooleanClause.Occur operator) {
        if (operator != BooleanClause.Occur.SHOULD && operator != BooleanClause.Occur.MUST) {
            throw new IllegalArgumentException("invalid operator: only SHOULD or MUST are allowed");
        }
        this.defaultOperator = operator;
    }
    
    static class State
    {
        final char[] data;
        final char[] buffer;
        int index;
        int length;
        BooleanClause.Occur currentOperation;
        BooleanClause.Occur previousOperation;
        int not;
        Query top;
        
        State(final char[] data, final char[] buffer, final int index, final int length) {
            this.data = data;
            this.buffer = buffer;
            this.index = index;
            this.length = length;
        }
    }
}
