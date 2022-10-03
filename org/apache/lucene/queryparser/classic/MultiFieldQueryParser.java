package org.apache.lucene.queryparser.classic;

import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.MultiPhraseQuery;
import org.apache.lucene.search.PhraseQuery;
import java.util.List;
import org.apache.lucene.search.BoostQuery;
import org.apache.lucene.search.BooleanClause;
import java.util.ArrayList;
import org.apache.lucene.search.Query;
import org.apache.lucene.analysis.Analyzer;
import java.util.Map;

public class MultiFieldQueryParser extends QueryParser
{
    protected String[] fields;
    protected Map<String, Float> boosts;
    
    public MultiFieldQueryParser(final String[] fields, final Analyzer analyzer, final Map<String, Float> boosts) {
        this(fields, analyzer);
        this.boosts = boosts;
    }
    
    public MultiFieldQueryParser(final String[] fields, final Analyzer analyzer) {
        super(null, analyzer);
        this.fields = fields;
    }
    
    @Override
    protected Query getFieldQuery(final String field, final String queryText, final int slop) throws ParseException {
        if (field != null) {
            Query q = super.getFieldQuery(field, queryText, true);
            q = this.applySlop(q, slop);
            return q;
        }
        final List<BooleanClause> clauses = new ArrayList<BooleanClause>();
        for (int i = 0; i < this.fields.length; ++i) {
            Query q2 = super.getFieldQuery(this.fields[i], queryText, true);
            if (q2 != null) {
                if (this.boosts != null) {
                    final Float boost = this.boosts.get(this.fields[i]);
                    if (boost != null) {
                        q2 = (Query)new BoostQuery(q2, (float)boost);
                    }
                }
                q2 = this.applySlop(q2, slop);
                clauses.add(new BooleanClause(q2, BooleanClause.Occur.SHOULD));
            }
        }
        if (clauses.size() == 0) {
            return null;
        }
        return this.getBooleanQuery(clauses, true);
    }
    
    private Query applySlop(Query q, final int slop) {
        if (q instanceof PhraseQuery) {
            final PhraseQuery.Builder builder = new PhraseQuery.Builder();
            builder.setSlop(slop);
            final PhraseQuery pq = (PhraseQuery)q;
            final Term[] terms = pq.getTerms();
            final int[] positions = pq.getPositions();
            for (int i = 0; i < terms.length; ++i) {
                builder.add(terms[i], positions[i]);
            }
            q = (Query)builder.build();
        }
        else if (q instanceof MultiPhraseQuery) {
            ((MultiPhraseQuery)q).setSlop(slop);
        }
        return q;
    }
    
    @Override
    protected Query getFieldQuery(final String field, final String queryText, final boolean quoted) throws ParseException {
        if (field != null) {
            final Query q = super.getFieldQuery(field, queryText, quoted);
            return q;
        }
        final List<BooleanClause> clauses = new ArrayList<BooleanClause>();
        for (int i = 0; i < this.fields.length; ++i) {
            Query q2 = super.getFieldQuery(this.fields[i], queryText, quoted);
            if (q2 != null) {
                if (this.boosts != null) {
                    final Float boost = this.boosts.get(this.fields[i]);
                    if (boost != null) {
                        q2 = (Query)new BoostQuery(q2, (float)boost);
                    }
                }
                clauses.add(new BooleanClause(q2, BooleanClause.Occur.SHOULD));
            }
        }
        if (clauses.size() == 0) {
            return null;
        }
        return this.getBooleanQuery(clauses, true);
    }
    
    @Override
    protected Query getFuzzyQuery(final String field, final String termStr, final float minSimilarity) throws ParseException {
        if (field == null) {
            final List<BooleanClause> clauses = new ArrayList<BooleanClause>();
            for (int i = 0; i < this.fields.length; ++i) {
                clauses.add(new BooleanClause(this.getFuzzyQuery(this.fields[i], termStr, minSimilarity), BooleanClause.Occur.SHOULD));
            }
            return this.getBooleanQuery(clauses, true);
        }
        return super.getFuzzyQuery(field, termStr, minSimilarity);
    }
    
    @Override
    protected Query getPrefixQuery(final String field, final String termStr) throws ParseException {
        if (field == null) {
            final List<BooleanClause> clauses = new ArrayList<BooleanClause>();
            for (int i = 0; i < this.fields.length; ++i) {
                clauses.add(new BooleanClause(this.getPrefixQuery(this.fields[i], termStr), BooleanClause.Occur.SHOULD));
            }
            return this.getBooleanQuery(clauses, true);
        }
        return super.getPrefixQuery(field, termStr);
    }
    
    @Override
    protected Query getWildcardQuery(final String field, final String termStr) throws ParseException {
        if (field == null) {
            final List<BooleanClause> clauses = new ArrayList<BooleanClause>();
            for (int i = 0; i < this.fields.length; ++i) {
                clauses.add(new BooleanClause(this.getWildcardQuery(this.fields[i], termStr), BooleanClause.Occur.SHOULD));
            }
            return this.getBooleanQuery(clauses, true);
        }
        return super.getWildcardQuery(field, termStr);
    }
    
    @Override
    protected Query getRangeQuery(final String field, final String part1, final String part2, final boolean startInclusive, final boolean endInclusive) throws ParseException {
        if (field == null) {
            final List<BooleanClause> clauses = new ArrayList<BooleanClause>();
            for (int i = 0; i < this.fields.length; ++i) {
                clauses.add(new BooleanClause(this.getRangeQuery(this.fields[i], part1, part2, startInclusive, endInclusive), BooleanClause.Occur.SHOULD));
            }
            return this.getBooleanQuery(clauses, true);
        }
        return super.getRangeQuery(field, part1, part2, startInclusive, endInclusive);
    }
    
    @Override
    protected Query getRegexpQuery(final String field, final String termStr) throws ParseException {
        if (field == null) {
            final List<BooleanClause> clauses = new ArrayList<BooleanClause>();
            for (int i = 0; i < this.fields.length; ++i) {
                clauses.add(new BooleanClause(this.getRegexpQuery(this.fields[i], termStr), BooleanClause.Occur.SHOULD));
            }
            return this.getBooleanQuery(clauses, true);
        }
        return super.getRegexpQuery(field, termStr);
    }
    
    public static Query parse(final String[] queries, final String[] fields, final Analyzer analyzer) throws ParseException {
        if (queries.length != fields.length) {
            throw new IllegalArgumentException("queries.length != fields.length");
        }
        final BooleanQuery.Builder bQuery = new BooleanQuery.Builder();
        for (int i = 0; i < fields.length; ++i) {
            final QueryParser qp = new QueryParser(fields[i], analyzer);
            final Query q = qp.parse(queries[i]);
            if (q != null && (!(q instanceof BooleanQuery) || ((BooleanQuery)q).clauses().size() > 0)) {
                bQuery.add(q, BooleanClause.Occur.SHOULD);
            }
        }
        return (Query)bQuery.build();
    }
    
    public static Query parse(final String query, final String[] fields, final BooleanClause.Occur[] flags, final Analyzer analyzer) throws ParseException {
        if (fields.length != flags.length) {
            throw new IllegalArgumentException("fields.length != flags.length");
        }
        final BooleanQuery.Builder bQuery = new BooleanQuery.Builder();
        for (int i = 0; i < fields.length; ++i) {
            final QueryParser qp = new QueryParser(fields[i], analyzer);
            final Query q = qp.parse(query);
            if (q != null && (!(q instanceof BooleanQuery) || ((BooleanQuery)q).clauses().size() > 0)) {
                bQuery.add(q, flags[i]);
            }
        }
        return (Query)bQuery.build();
    }
    
    public static Query parse(final String[] queries, final String[] fields, final BooleanClause.Occur[] flags, final Analyzer analyzer) throws ParseException {
        if (queries.length != fields.length || queries.length != flags.length) {
            throw new IllegalArgumentException("queries, fields, and flags array have have different length");
        }
        final BooleanQuery.Builder bQuery = new BooleanQuery.Builder();
        for (int i = 0; i < fields.length; ++i) {
            final QueryParser qp = new QueryParser(fields[i], analyzer);
            final Query q = qp.parse(queries[i]);
            if (q != null && (!(q instanceof BooleanQuery) || ((BooleanQuery)q).clauses().size() > 0)) {
                bQuery.add(q, flags[i]);
            }
        }
        return (Query)bQuery.build();
    }
}
