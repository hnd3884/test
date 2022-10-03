package org.apache.lucene.facet.taxonomy;

import org.apache.lucene.queries.function.docvalues.DoubleDocValues;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.queries.function.FunctionValues;
import java.util.Iterator;
import java.util.Map;
import org.apache.lucene.util.IntsRef;
import org.apache.lucene.search.Scorer;
import java.util.HashMap;
import java.util.List;
import java.io.IOException;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.facet.FacetsCollector;
import org.apache.lucene.facet.FacetsConfig;

public class TaxonomyFacetSumValueSource extends FloatTaxonomyFacets
{
    private final OrdinalsReader ordinalsReader;
    
    public TaxonomyFacetSumValueSource(final TaxonomyReader taxoReader, final FacetsConfig config, final FacetsCollector fc, final ValueSource valueSource) throws IOException {
        this(new DocValuesOrdinalsReader("$facets"), taxoReader, config, fc, valueSource);
    }
    
    public TaxonomyFacetSumValueSource(final OrdinalsReader ordinalsReader, final TaxonomyReader taxoReader, final FacetsConfig config, final FacetsCollector fc, final ValueSource valueSource) throws IOException {
        super(ordinalsReader.getIndexFieldName(), taxoReader, config);
        this.ordinalsReader = ordinalsReader;
        this.sumValues(fc.getMatchingDocs(), fc.getKeepScores(), valueSource);
    }
    
    private final void sumValues(final List<FacetsCollector.MatchingDocs> matchingDocs, final boolean keepScores, final ValueSource valueSource) throws IOException {
        final FakeScorer scorer = new FakeScorer();
        final Map<String, Scorer> context = new HashMap<String, Scorer>();
        if (keepScores) {
            context.put("scorer", scorer);
        }
        final IntsRef scratch = new IntsRef();
        for (final FacetsCollector.MatchingDocs hits : matchingDocs) {
            final OrdinalsReader.OrdinalsSegmentReader ords = this.ordinalsReader.getReader(hits.context);
            int scoresIdx = 0;
            final float[] scores = hits.scores;
            final FunctionValues functionValues = valueSource.getValues((Map)context, hits.context);
            final DocIdSetIterator docs = hits.bits.iterator();
            int doc;
            while ((doc = docs.nextDoc()) != Integer.MAX_VALUE) {
                ords.get(doc, scratch);
                if (keepScores) {
                    scorer.doc = doc;
                    scorer.score = scores[scoresIdx++];
                }
                final float value = (float)functionValues.doubleVal(doc);
                for (int i = 0; i < scratch.length; ++i) {
                    final float[] values = this.values;
                    final int n = scratch.ints[i];
                    values[n] += value;
                }
            }
        }
        this.rollup();
    }
    
    public static class ScoreValueSource extends ValueSource
    {
        public FunctionValues getValues(final Map context, final LeafReaderContext readerContext) throws IOException {
            final Scorer scorer = context.get("scorer");
            if (scorer == null) {
                throw new IllegalStateException("scores are missing; be sure to pass keepScores=true to FacetsCollector");
            }
            return (FunctionValues)new DoubleDocValues(this) {
                public double doubleVal(final int document) {
                    try {
                        return scorer.score();
                    }
                    catch (final IOException exception) {
                        throw new RuntimeException(exception);
                    }
                }
            };
        }
        
        public boolean equals(final Object o) {
            return o == this;
        }
        
        public int hashCode() {
            return System.identityHashCode(this);
        }
        
        public String description() {
            return "score()";
        }
    }
}
