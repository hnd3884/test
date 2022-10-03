package org.apache.lucene.search.vectorhighlight;

import java.util.List;
import java.util.HashSet;
import org.apache.lucene.search.MultiTermQuery;
import org.apache.lucene.queries.CustomScoreQuery;
import org.apache.lucene.search.FilteredQuery;
import org.apache.lucene.search.ConstantScoreQuery;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.DisjunctionMaxQuery;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import java.io.IOException;
import org.apache.lucene.index.Term;
import java.util.Iterator;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.BoostQuery;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.HashMap;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.Query;
import java.util.Set;
import java.util.Map;

public class FieldQuery
{
    final boolean fieldMatch;
    Map<String, QueryPhraseMap> rootMaps;
    Map<String, Set<String>> termSetMap;
    int termOrPhraseNumber;
    private static final int MAX_MTQ_TERMS = 1024;
    
    FieldQuery(final Query query, final IndexReader reader, final boolean phraseHighlight, final boolean fieldMatch) throws IOException {
        this.rootMaps = new HashMap<String, QueryPhraseMap>();
        this.termSetMap = new HashMap<String, Set<String>>();
        this.fieldMatch = fieldMatch;
        final Set<Query> flatQueries = new LinkedHashSet<Query>();
        this.flatten(query, reader, flatQueries, 1.0f);
        this.saveTerms(flatQueries, reader);
        final Collection<Query> expandQueries = this.expand(flatQueries);
        for (Query flatQuery : expandQueries) {
            final QueryPhraseMap rootMap = this.getRootMap(flatQuery);
            rootMap.add(flatQuery, reader);
            float boost;
            BoostQuery bq;
            for (boost = 1.0f; flatQuery instanceof BoostQuery; flatQuery = bq.getQuery(), boost *= bq.getBoost()) {
                bq = (BoostQuery)flatQuery;
            }
            if (!phraseHighlight && flatQuery instanceof PhraseQuery) {
                final PhraseQuery pq = (PhraseQuery)flatQuery;
                if (pq.getTerms().length <= 1) {
                    continue;
                }
                for (final Term term : pq.getTerms()) {
                    rootMap.addTerm(term, boost);
                }
            }
        }
    }
    
    FieldQuery(final Query query, final boolean phraseHighlight, final boolean fieldMatch) throws IOException {
        this(query, null, phraseHighlight, fieldMatch);
    }
    
    void flatten(Query sourceQuery, final IndexReader reader, final Collection<Query> flatQueries, float boost) throws IOException {
        while (true) {
            if (sourceQuery.getBoost() != 1.0f) {
                boost *= sourceQuery.getBoost();
                sourceQuery = sourceQuery.clone();
                sourceQuery.setBoost(1.0f);
            }
            else {
                if (!(sourceQuery instanceof BoostQuery)) {
                    break;
                }
                final BoostQuery bq = (BoostQuery)sourceQuery;
                sourceQuery = bq.getQuery();
                boost *= bq.getBoost();
            }
        }
        if (sourceQuery instanceof BooleanQuery) {
            final BooleanQuery bq2 = (BooleanQuery)sourceQuery;
            for (final BooleanClause clause : bq2) {
                if (!clause.isProhibited()) {
                    this.flatten(clause.getQuery(), reader, flatQueries, boost);
                }
            }
        }
        else if (sourceQuery instanceof DisjunctionMaxQuery) {
            final DisjunctionMaxQuery dmq = (DisjunctionMaxQuery)sourceQuery;
            for (final Query query : dmq) {
                this.flatten(query, reader, flatQueries, boost);
            }
        }
        else if (sourceQuery instanceof TermQuery) {
            if (boost != 1.0f) {
                sourceQuery = (Query)new BoostQuery(sourceQuery, boost);
            }
            if (!flatQueries.contains(sourceQuery)) {
                flatQueries.add(sourceQuery);
            }
        }
        else if (sourceQuery instanceof PhraseQuery) {
            final PhraseQuery pq = (PhraseQuery)sourceQuery;
            if (pq.getTerms().length == 1) {
                sourceQuery = (Query)new TermQuery(pq.getTerms()[0]);
            }
            if (boost != 1.0f) {
                sourceQuery = (Query)new BoostQuery(sourceQuery, boost);
            }
            flatQueries.add(sourceQuery);
        }
        else if (sourceQuery instanceof ConstantScoreQuery) {
            final Query q = ((ConstantScoreQuery)sourceQuery).getQuery();
            if (q != null) {
                this.flatten(q, reader, flatQueries, boost);
            }
        }
        else if (sourceQuery instanceof FilteredQuery) {
            final Query q = ((FilteredQuery)sourceQuery).getQuery();
            if (q != null) {
                this.flatten(q, reader, flatQueries, boost);
            }
        }
        else if (sourceQuery instanceof CustomScoreQuery) {
            final Query q = ((CustomScoreQuery)sourceQuery).getSubQuery();
            if (q != null) {
                this.flatten(q, reader, flatQueries, boost);
            }
        }
        else if (reader != null) {
            final Query query2 = sourceQuery;
            Query rewritten;
            if (sourceQuery instanceof MultiTermQuery) {
                rewritten = new MultiTermQuery.TopTermsScoringBooleanQueryRewrite(1024).rewrite(reader, (MultiTermQuery)query2);
            }
            else {
                rewritten = query2.rewrite(reader);
            }
            if (rewritten != query2) {
                this.flatten(rewritten, reader, flatQueries, boost);
            }
        }
    }
    
    Collection<Query> expand(final Collection<Query> flatQueries) {
        final Set<Query> expandQueries = new LinkedHashSet<Query>();
        final Iterator<Query> i = flatQueries.iterator();
        while (i.hasNext()) {
            Query query = i.next();
            i.remove();
            expandQueries.add(query);
            float queryBoost = 1.0f;
            while (query instanceof BoostQuery) {
                final BoostQuery bq = (BoostQuery)query;
                queryBoost *= bq.getBoost();
                query = bq.getQuery();
            }
            if (!(query instanceof PhraseQuery)) {
                continue;
            }
            for (Query qj : flatQueries) {
                float qjBoost = 1.0f;
                while (qj instanceof BoostQuery) {
                    final BoostQuery bq2 = (BoostQuery)qj;
                    qjBoost *= bq2.getBoost();
                    qj = bq2.getQuery();
                }
                if (!(qj instanceof PhraseQuery)) {
                    continue;
                }
                this.checkOverlap(expandQueries, (PhraseQuery)query, queryBoost, (PhraseQuery)qj, qjBoost);
            }
        }
        return expandQueries;
    }
    
    private void checkOverlap(final Collection<Query> expandQueries, final PhraseQuery a, final float aBoost, final PhraseQuery b, final float bBoost) {
        if (a.getSlop() != b.getSlop()) {
            return;
        }
        final Term[] ats = a.getTerms();
        final Term[] bts = b.getTerms();
        if (this.fieldMatch && !ats[0].field().equals(bts[0].field())) {
            return;
        }
        this.checkOverlap(expandQueries, ats, bts, a.getSlop(), aBoost);
        this.checkOverlap(expandQueries, bts, ats, b.getSlop(), bBoost);
    }
    
    private void checkOverlap(final Collection<Query> expandQueries, final Term[] src, final Term[] dest, final int slop, final float boost) {
        for (int i = 1; i < src.length; ++i) {
            boolean overlap = true;
            for (int j = i; j < src.length; ++j) {
                if (j - i < dest.length && !src[j].text().equals(dest[j - i].text())) {
                    overlap = false;
                    break;
                }
            }
            if (overlap && src.length - i < dest.length) {
                final PhraseQuery.Builder pqBuilder = new PhraseQuery.Builder();
                for (final Term srcTerm : src) {
                    pqBuilder.add(srcTerm);
                }
                for (int k = src.length - i; k < dest.length; ++k) {
                    pqBuilder.add(new Term(src[0].field(), dest[k].text()));
                }
                pqBuilder.setSlop(slop);
                Query pq = (Query)pqBuilder.build();
                if (boost != 1.0f) {
                    pq = (Query)new BoostQuery(pq, 1.0f);
                }
                if (!expandQueries.contains(pq)) {
                    expandQueries.add(pq);
                }
            }
        }
    }
    
    QueryPhraseMap getRootMap(final Query query) {
        final String key = this.getKey(query);
        QueryPhraseMap map = this.rootMaps.get(key);
        if (map == null) {
            map = new QueryPhraseMap(this);
            this.rootMaps.put(key, map);
        }
        return map;
    }
    
    private String getKey(Query query) {
        if (!this.fieldMatch) {
            return null;
        }
        while (query instanceof BoostQuery) {
            query = ((BoostQuery)query).getQuery();
        }
        if (query instanceof TermQuery) {
            return ((TermQuery)query).getTerm().field();
        }
        if (query instanceof PhraseQuery) {
            final PhraseQuery pq = (PhraseQuery)query;
            final Term[] terms = pq.getTerms();
            return terms[0].field();
        }
        if (query instanceof MultiTermQuery) {
            return ((MultiTermQuery)query).getField();
        }
        throw new RuntimeException("query \"" + query.toString() + "\" must be flatten first.");
    }
    
    void saveTerms(final Collection<Query> flatQueries, final IndexReader reader) throws IOException {
        for (Query query : flatQueries) {
            while (query instanceof BoostQuery) {
                query = ((BoostQuery)query).getQuery();
            }
            final Set<String> termSet = this.getTermSet(query);
            if (query instanceof TermQuery) {
                termSet.add(((TermQuery)query).getTerm().text());
            }
            else if (query instanceof PhraseQuery) {
                for (final Term term : ((PhraseQuery)query).getTerms()) {
                    termSet.add(term.text());
                }
            }
            else {
                if (!(query instanceof MultiTermQuery) || reader == null) {
                    throw new RuntimeException("query \"" + query.toString() + "\" must be flatten first.");
                }
                final BooleanQuery mtqTerms = (BooleanQuery)query.rewrite(reader);
                for (final BooleanClause clause : mtqTerms) {
                    termSet.add(((TermQuery)clause.getQuery()).getTerm().text());
                }
            }
        }
    }
    
    private Set<String> getTermSet(final Query query) {
        final String key = this.getKey(query);
        Set<String> set = this.termSetMap.get(key);
        if (set == null) {
            set = new HashSet<String>();
            this.termSetMap.put(key, set);
        }
        return set;
    }
    
    Set<String> getTermSet(final String field) {
        return this.termSetMap.get(this.fieldMatch ? field : null);
    }
    
    public QueryPhraseMap getFieldTermMap(final String fieldName, final String term) {
        final QueryPhraseMap rootMap = this.getRootMap(fieldName);
        return (rootMap == null) ? null : rootMap.subMap.get(term);
    }
    
    public QueryPhraseMap searchPhrase(final String fieldName, final List<FieldTermStack.TermInfo> phraseCandidate) {
        final QueryPhraseMap root = this.getRootMap(fieldName);
        if (root == null) {
            return null;
        }
        return root.searchPhrase(phraseCandidate);
    }
    
    private QueryPhraseMap getRootMap(final String fieldName) {
        return this.rootMaps.get(this.fieldMatch ? fieldName : null);
    }
    
    int nextTermOrPhraseNumber() {
        return this.termOrPhraseNumber++;
    }
    
    public static class QueryPhraseMap
    {
        boolean terminal;
        int slop;
        float boost;
        int termOrPhraseNumber;
        FieldQuery fieldQuery;
        Map<String, QueryPhraseMap> subMap;
        
        public QueryPhraseMap(final FieldQuery fieldQuery) {
            this.subMap = new HashMap<String, QueryPhraseMap>();
            this.fieldQuery = fieldQuery;
        }
        
        void addTerm(final Term term, final float boost) {
            final QueryPhraseMap map = this.getOrNewMap(this.subMap, term.text());
            map.markTerminal(boost);
        }
        
        private QueryPhraseMap getOrNewMap(final Map<String, QueryPhraseMap> subMap, final String term) {
            QueryPhraseMap map = subMap.get(term);
            if (map == null) {
                map = new QueryPhraseMap(this.fieldQuery);
                subMap.put(term, map);
            }
            return map;
        }
        
        void add(Query query, final IndexReader reader) {
            float boost = 1.0f;
            while (query instanceof BoostQuery) {
                final BoostQuery bq = (BoostQuery)query;
                query = bq.getQuery();
                boost = bq.getBoost();
            }
            if (query instanceof TermQuery) {
                this.addTerm(((TermQuery)query).getTerm(), boost);
            }
            else {
                if (!(query instanceof PhraseQuery)) {
                    throw new RuntimeException("query \"" + query.toString() + "\" must be flatten first.");
                }
                final PhraseQuery pq = (PhraseQuery)query;
                final Term[] terms = pq.getTerms();
                Map<String, QueryPhraseMap> map = this.subMap;
                QueryPhraseMap qpm = null;
                for (final Term term : terms) {
                    qpm = this.getOrNewMap(map, term.text());
                    map = qpm.subMap;
                }
                qpm.markTerminal(pq.getSlop(), boost);
            }
        }
        
        public QueryPhraseMap getTermMap(final String term) {
            return this.subMap.get(term);
        }
        
        private void markTerminal(final float boost) {
            this.markTerminal(0, boost);
        }
        
        private void markTerminal(final int slop, final float boost) {
            this.terminal = true;
            this.slop = slop;
            this.boost = boost;
            this.termOrPhraseNumber = this.fieldQuery.nextTermOrPhraseNumber();
        }
        
        public boolean isTerminal() {
            return this.terminal;
        }
        
        public int getSlop() {
            return this.slop;
        }
        
        public float getBoost() {
            return this.boost;
        }
        
        public int getTermOrPhraseNumber() {
            return this.termOrPhraseNumber;
        }
        
        public QueryPhraseMap searchPhrase(final List<FieldTermStack.TermInfo> phraseCandidate) {
            QueryPhraseMap currMap = this;
            for (final FieldTermStack.TermInfo ti : phraseCandidate) {
                currMap = currMap.subMap.get(ti.getText());
                if (currMap == null) {
                    return null;
                }
            }
            return currMap.isValidTermOrPhrase(phraseCandidate) ? currMap : null;
        }
        
        public boolean isValidTermOrPhrase(final List<FieldTermStack.TermInfo> phraseCandidate) {
            if (!this.terminal) {
                return false;
            }
            if (phraseCandidate.size() == 1) {
                return true;
            }
            int pos = phraseCandidate.get(0).getPosition();
            for (int i = 1; i < phraseCandidate.size(); ++i) {
                final int nextPos = phraseCandidate.get(i).getPosition();
                if (Math.abs(nextPos - pos - 1) > this.slop) {
                    return false;
                }
                pos = nextPos;
            }
            return true;
        }
    }
}
