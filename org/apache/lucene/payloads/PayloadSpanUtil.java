package org.apache.lucene.payloads;

import org.apache.lucene.search.spans.Spans;
import org.apache.lucene.search.spans.SpanCollector;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.spans.SpanWeight;
import org.apache.lucene.search.QueryCache;
import org.apache.lucene.search.IndexSearcher;
import java.util.Iterator;
import org.apache.lucene.search.spans.SpanOrQuery;
import org.apache.lucene.index.Term;
import java.util.List;
import org.apache.lucene.search.MultiPhraseQuery;
import org.apache.lucene.search.DisjunctionMaxQuery;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.search.spans.SpanTermQuery;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.lucene.search.Query;
import org.apache.lucene.index.IndexReaderContext;

public class PayloadSpanUtil
{
    private IndexReaderContext context;
    
    public PayloadSpanUtil(final IndexReaderContext context) {
        this.context = context;
    }
    
    public Collection<byte[]> getPayloadsForQuery(final Query query) throws IOException {
        final Collection<byte[]> payloads = new ArrayList<byte[]>();
        this.queryToSpanQuery(query, payloads);
        return payloads;
    }
    
    private void queryToSpanQuery(final Query query, final Collection<byte[]> payloads) throws IOException {
        if (query instanceof BooleanQuery) {
            for (final BooleanClause clause : (BooleanQuery)query) {
                if (!clause.isProhibited()) {
                    this.queryToSpanQuery(clause.getQuery(), payloads);
                }
            }
        }
        else if (query instanceof PhraseQuery) {
            final Term[] phraseQueryTerms = ((PhraseQuery)query).getTerms();
            final SpanQuery[] clauses = new SpanQuery[phraseQueryTerms.length];
            for (int i = 0; i < phraseQueryTerms.length; ++i) {
                clauses[i] = (SpanQuery)new SpanTermQuery(phraseQueryTerms[i]);
            }
            final int slop = ((PhraseQuery)query).getSlop();
            boolean inorder = false;
            if (slop == 0) {
                inorder = true;
            }
            final SpanNearQuery sp = new SpanNearQuery(clauses, slop, inorder);
            this.getPayloads(payloads, (SpanQuery)sp);
        }
        else if (query instanceof TermQuery) {
            final SpanTermQuery stq = new SpanTermQuery(((TermQuery)query).getTerm());
            this.getPayloads(payloads, (SpanQuery)stq);
        }
        else if (query instanceof SpanQuery) {
            this.getPayloads(payloads, (SpanQuery)query);
        }
        else if (query instanceof DisjunctionMaxQuery) {
            final Iterator<Query> iterator = ((DisjunctionMaxQuery)query).iterator();
            while (iterator.hasNext()) {
                this.queryToSpanQuery(iterator.next(), payloads);
            }
        }
        else if (query instanceof MultiPhraseQuery) {
            final MultiPhraseQuery mpq = (MultiPhraseQuery)query;
            final List<Term[]> termArrays = mpq.getTermArrays();
            final int[] positions = mpq.getPositions();
            if (positions.length > 0) {
                int maxPosition = positions[positions.length - 1];
                for (int j = 0; j < positions.length - 1; ++j) {
                    if (positions[j] > maxPosition) {
                        maxPosition = positions[j];
                    }
                }
                final List<Query>[] disjunctLists = new List[maxPosition + 1];
                int distinctPositions = 0;
                for (int k = 0; k < termArrays.size(); ++k) {
                    final Term[] termArray = termArrays.get(k);
                    List<Query> disjuncts = disjunctLists[positions[k]];
                    if (disjuncts == null) {
                        final List<Query>[] array = disjunctLists;
                        final int n = positions[k];
                        final List<Query> list = new ArrayList<Query>(termArray.length);
                        array[n] = list;
                        disjuncts = list;
                        ++distinctPositions;
                    }
                    for (final Term term : termArray) {
                        disjuncts.add((Query)new SpanTermQuery(term));
                    }
                }
                int positionGaps = 0;
                int position = 0;
                final SpanQuery[] clauses2 = new SpanQuery[distinctPositions];
                for (int l = 0; l < disjunctLists.length; ++l) {
                    final List<Query> disjuncts2 = disjunctLists[l];
                    if (disjuncts2 != null) {
                        clauses2[position++] = (SpanQuery)new SpanOrQuery((SpanQuery[])disjuncts2.toArray(new SpanQuery[disjuncts2.size()]));
                    }
                    else {
                        ++positionGaps;
                    }
                }
                final int slop2 = mpq.getSlop();
                final boolean inorder2 = slop2 == 0;
                final SpanNearQuery sp2 = new SpanNearQuery(clauses2, slop2 + positionGaps, inorder2);
                this.getPayloads(payloads, (SpanQuery)sp2);
            }
        }
    }
    
    private void getPayloads(final Collection<byte[]> payloads, final SpanQuery query) throws IOException {
        final IndexSearcher searcher = new IndexSearcher(this.context);
        searcher.setQueryCache((QueryCache)null);
        final SpanWeight w = (SpanWeight)searcher.createNormalizedWeight((Query)query, false);
        final PayloadSpanCollector collector = new PayloadSpanCollector();
        for (final LeafReaderContext leafReaderContext : this.context.leaves()) {
            final Spans spans = w.getSpans(leafReaderContext, SpanWeight.Postings.PAYLOADS);
            if (spans != null) {
                while (spans.nextDoc() != Integer.MAX_VALUE) {
                    while (spans.nextStartPosition() != Integer.MAX_VALUE) {
                        collector.reset();
                        spans.collect((SpanCollector)collector);
                        payloads.addAll(collector.getPayloads());
                    }
                }
            }
        }
    }
}
