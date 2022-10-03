package org.apache.lucene.search.join;

import org.apache.lucene.index.SortedDocValues;
import org.apache.lucene.index.LeafReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.MatchNoDocsQuery;
import org.apache.lucene.index.MultiDocValues;
import java.util.Locale;
import org.apache.lucene.search.Collector;
import org.apache.lucene.document.FieldType;
import java.io.IOException;
import org.apache.lucene.index.BinaryDocValues;
import org.apache.lucene.index.SortedSetDocValues;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;

public final class JoinUtil
{
    private JoinUtil() {
    }
    
    public static Query createJoinQuery(final String fromField, final boolean multipleValuesPerDocument, final String toField, final Query fromQuery, final IndexSearcher fromSearcher, final ScoreMode scoreMode) throws IOException {
        GenericTermsCollector termsWithScoreCollector;
        if (multipleValuesPerDocument) {
            final DocValuesTermsCollector.Function<SortedSetDocValues> mvFunction = DocValuesTermsCollector.sortedSetDocValues(fromField);
            termsWithScoreCollector = GenericTermsCollectorFactory.createCollectorMV(mvFunction, scoreMode);
        }
        else {
            final DocValuesTermsCollector.Function<BinaryDocValues> svFunction = DocValuesTermsCollector.binaryDocValues(fromField);
            termsWithScoreCollector = GenericTermsCollectorFactory.createCollectorSV(svFunction, scoreMode);
        }
        return createJoinQuery(multipleValuesPerDocument, toField, fromQuery, fromSearcher, scoreMode, termsWithScoreCollector);
    }
    
    public static Query createJoinQuery(final String fromField, final boolean multipleValuesPerDocument, final String toField, final FieldType.NumericType numericType, final Query fromQuery, final IndexSearcher fromSearcher, final ScoreMode scoreMode) throws IOException {
        GenericTermsCollector termsCollector;
        if (multipleValuesPerDocument) {
            final DocValuesTermsCollector.Function<SortedSetDocValues> mvFunction = DocValuesTermsCollector.sortedNumericAsSortedSetDocValues(fromField, numericType);
            termsCollector = GenericTermsCollectorFactory.createCollectorMV(mvFunction, scoreMode);
        }
        else {
            final DocValuesTermsCollector.Function<BinaryDocValues> svFunction = DocValuesTermsCollector.numericAsBinaryDocValues(fromField, numericType);
            termsCollector = GenericTermsCollectorFactory.createCollectorSV(svFunction, scoreMode);
        }
        return createJoinQuery(multipleValuesPerDocument, toField, fromQuery, fromSearcher, scoreMode, termsCollector);
    }
    
    private static Query createJoinQuery(final boolean multipleValuesPerDocument, final String toField, final Query fromQuery, final IndexSearcher fromSearcher, final ScoreMode scoreMode, final GenericTermsCollector collector) throws IOException {
        fromSearcher.search(fromQuery, (Collector)collector);
        switch (scoreMode) {
            case None: {
                return (Query)new TermsQuery(toField, fromQuery, collector.getCollectedTerms());
            }
            case Total:
            case Max:
            case Min:
            case Avg: {
                return new TermsIncludingScoreQuery(toField, multipleValuesPerDocument, collector.getCollectedTerms(), collector.getScoresPerTerm(), fromQuery);
            }
            default: {
                throw new IllegalArgumentException(String.format(Locale.ROOT, "Score mode %s isn't supported.", scoreMode));
            }
        }
    }
    
    public static Query createJoinQuery(final String joinField, final Query fromQuery, final Query toQuery, final IndexSearcher searcher, final ScoreMode scoreMode, final MultiDocValues.OrdinalMap ordinalMap) throws IOException {
        return createJoinQuery(joinField, fromQuery, toQuery, searcher, scoreMode, ordinalMap, 0, Integer.MAX_VALUE);
    }
    
    public static Query createJoinQuery(final String joinField, final Query fromQuery, final Query toQuery, final IndexSearcher searcher, final ScoreMode scoreMode, MultiDocValues.OrdinalMap ordinalMap, final int min, final int max) throws IOException {
        final IndexReader indexReader = searcher.getIndexReader();
        final int numSegments = indexReader.leaves().size();
        if (numSegments == 0) {
            return (Query)new MatchNoDocsQuery();
        }
        long valueCount;
        if (numSegments == 1) {
            ordinalMap = null;
            final LeafReader leafReader = searcher.getIndexReader().leaves().get(0).reader();
            final SortedDocValues joinSortedDocValues = leafReader.getSortedDocValues(joinField);
            if (joinSortedDocValues == null) {
                return (Query)new MatchNoDocsQuery();
            }
            valueCount = joinSortedDocValues.getValueCount();
        }
        else {
            if (ordinalMap == null) {
                throw new IllegalArgumentException("OrdinalMap is required, because there is more than 1 segment");
            }
            valueCount = ordinalMap.getValueCount();
        }
        final Query rewrittenFromQuery = searcher.rewrite(fromQuery);
        final Query rewrittenToQuery = searcher.rewrite(toQuery);
        GlobalOrdinalsWithScoreCollector globalOrdinalsWithScoreCollector = null;
        switch (scoreMode) {
            case Total: {
                globalOrdinalsWithScoreCollector = new GlobalOrdinalsWithScoreCollector.Sum(joinField, ordinalMap, valueCount, min, max);
                break;
            }
            case Min: {
                globalOrdinalsWithScoreCollector = new GlobalOrdinalsWithScoreCollector.Min(joinField, ordinalMap, valueCount, min, max);
                break;
            }
            case Max: {
                globalOrdinalsWithScoreCollector = new GlobalOrdinalsWithScoreCollector.Max(joinField, ordinalMap, valueCount, min, max);
                break;
            }
            case Avg: {
                globalOrdinalsWithScoreCollector = new GlobalOrdinalsWithScoreCollector.Avg(joinField, ordinalMap, valueCount, min, max);
                break;
            }
            case None: {
                if (min <= 0 && max == Integer.MAX_VALUE) {
                    final GlobalOrdinalsCollector globalOrdinalsCollector = new GlobalOrdinalsCollector(joinField, ordinalMap, valueCount);
                    searcher.search(rewrittenFromQuery, (Collector)globalOrdinalsCollector);
                    return new GlobalOrdinalsQuery(globalOrdinalsCollector.getCollectorOrdinals(), joinField, ordinalMap, rewrittenToQuery, rewrittenFromQuery, indexReader);
                }
                globalOrdinalsWithScoreCollector = new GlobalOrdinalsWithScoreCollector.NoScore(joinField, ordinalMap, valueCount, min, max);
                break;
            }
            default: {
                throw new IllegalArgumentException(String.format(Locale.ROOT, "Score mode %s isn't supported.", scoreMode));
            }
        }
        searcher.search(rewrittenFromQuery, (Collector)globalOrdinalsWithScoreCollector);
        return new GlobalOrdinalsWithScoreQuery(globalOrdinalsWithScoreCollector, joinField, ordinalMap, rewrittenToQuery, rewrittenFromQuery, min, max, indexReader);
    }
}
