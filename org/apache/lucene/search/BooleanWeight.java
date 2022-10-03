package org.apache.lucene.search;

import org.apache.lucene.util.Bits;
import java.util.List;
import java.util.Collection;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.Term;
import java.util.Set;
import java.io.IOException;
import java.util.Iterator;
import java.util.Arrays;
import java.util.ArrayList;
import org.apache.lucene.search.similarities.Similarity;

final class BooleanWeight extends Weight
{
    final Similarity similarity;
    final BooleanQuery query;
    final ArrayList<Weight> weights;
    final int maxCoord;
    final boolean disableCoord;
    final boolean needsScores;
    final float[] coords;
    
    BooleanWeight(final BooleanQuery query, final IndexSearcher searcher, final boolean needsScores, final boolean disableCoord) throws IOException {
        super(query);
        this.query = query;
        this.needsScores = needsScores;
        this.similarity = searcher.getSimilarity(needsScores);
        this.weights = new ArrayList<Weight>();
        int i = 0;
        int maxCoord = 0;
        for (final BooleanClause c : query) {
            final Weight w = searcher.createWeight(c.getQuery(), needsScores && c.isScoring());
            this.weights.add(w);
            if (c.isScoring()) {
                ++maxCoord;
            }
            ++i;
        }
        this.maxCoord = maxCoord;
        Arrays.fill(this.coords = new float[maxCoord + 1], 1.0f);
        this.coords[0] = 0.0f;
        if (maxCoord > 0 && needsScores && !disableCoord) {
            boolean seenActualCoord = false;
            for (i = 1; i < this.coords.length; ++i) {
                this.coords[i] = this.coord(i, maxCoord);
                seenActualCoord |= (this.coords[i] != 1.0f);
            }
            this.disableCoord = !seenActualCoord;
        }
        else {
            this.disableCoord = true;
        }
    }
    
    @Override
    public void extractTerms(final Set<Term> terms) {
        int i = 0;
        for (final BooleanClause clause : this.query) {
            if (clause.isScoring() || (!this.needsScores && !clause.isProhibited())) {
                this.weights.get(i).extractTerms(terms);
            }
            ++i;
        }
    }
    
    @Override
    public float getValueForNormalization() throws IOException {
        float sum = 0.0f;
        int i = 0;
        for (final BooleanClause clause : this.query) {
            final float s = this.weights.get(i).getValueForNormalization();
            if (clause.isScoring()) {
                sum += s;
            }
            ++i;
        }
        return sum;
    }
    
    public float coord(final int overlap, final int maxOverlap) {
        if (overlap == 0) {
            return 0.0f;
        }
        if (maxOverlap == 1) {
            return 1.0f;
        }
        return this.similarity.coord(overlap, maxOverlap);
    }
    
    @Override
    public void normalize(final float norm, final float boost) {
        for (final Weight w : this.weights) {
            w.normalize(norm, boost);
        }
    }
    
    @Override
    public Explanation explain(final LeafReaderContext context, final int doc) throws IOException {
        final int minShouldMatch = this.query.getMinimumNumberShouldMatch();
        final List<Explanation> subs = new ArrayList<Explanation>();
        int coord = 0;
        float sum = 0.0f;
        boolean fail = false;
        int matchCount = 0;
        int shouldMatchCount = 0;
        final Iterator<BooleanClause> cIter = this.query.iterator();
        for (final Weight w : this.weights) {
            final BooleanClause c = cIter.next();
            final Explanation e = w.explain(context, doc);
            if (e.isMatch()) {
                if (c.isScoring()) {
                    subs.add(e);
                    sum += e.getValue();
                    ++coord;
                }
                else if (c.isRequired()) {
                    subs.add(Explanation.match(0.0f, "match on required clause, product of:", Explanation.match(0.0f, BooleanClause.Occur.FILTER + " clause", new Explanation[0]), e));
                }
                else if (c.isProhibited()) {
                    subs.add(Explanation.noMatch("match on prohibited clause (" + c.getQuery().toString() + ")", e));
                    fail = true;
                }
                if (!c.isProhibited()) {
                    ++matchCount;
                }
                if (c.getOccur() != BooleanClause.Occur.SHOULD) {
                    continue;
                }
                ++shouldMatchCount;
            }
            else {
                if (!c.isRequired()) {
                    continue;
                }
                subs.add(Explanation.noMatch("no match on required clause (" + c.getQuery().toString() + ")", e));
                fail = true;
            }
        }
        if (fail) {
            return Explanation.noMatch("Failure to meet condition(s) of required/prohibited clause(s)", subs);
        }
        if (matchCount == 0) {
            return Explanation.noMatch("No matching clauses", subs);
        }
        if (shouldMatchCount < minShouldMatch) {
            return Explanation.noMatch("Failure to match minimum number of optional clauses: " + minShouldMatch, subs);
        }
        Explanation result = Explanation.match(sum, "sum of:", subs);
        final float coordFactor = this.disableCoord ? 1.0f : this.coord(coord, this.maxCoord);
        if (coordFactor != 1.0f) {
            result = Explanation.match(sum * coordFactor, "product of:", result, Explanation.match(coordFactor, "coord(" + coord + "/" + this.maxCoord + ")", new Explanation[0]));
        }
        return result;
    }
    
    static BulkScorer disableScoring(final BulkScorer scorer) {
        return new BulkScorer() {
            @Override
            public int score(final LeafCollector collector, final Bits acceptDocs, final int min, final int max) throws IOException {
                final LeafCollector noScoreCollector = new LeafCollector() {
                    FakeScorer fake = new FakeScorer();
                    
                    @Override
                    public void setScorer(final Scorer scorer) throws IOException {
                        collector.setScorer(this.fake);
                    }
                    
                    @Override
                    public void collect(final int doc) throws IOException {
                        this.fake.doc = doc;
                        collector.collect(doc);
                    }
                };
                return scorer.score(noScoreCollector, acceptDocs, min, max);
            }
            
            @Override
            public long cost() {
                return scorer.cost();
            }
        };
    }
    
    BulkScorer optionalBulkScorer(final LeafReaderContext context) throws IOException {
        final List<BulkScorer> optional = new ArrayList<BulkScorer>();
        final Iterator<BooleanClause> cIter = this.query.iterator();
        for (final Weight w : this.weights) {
            final BooleanClause c = cIter.next();
            if (c.getOccur() != BooleanClause.Occur.SHOULD) {
                continue;
            }
            final BulkScorer subScorer = w.bulkScorer(context);
            if (subScorer == null) {
                continue;
            }
            optional.add(subScorer);
        }
        if (optional.size() == 0) {
            return null;
        }
        if (this.query.getMinimumNumberShouldMatch() > optional.size()) {
            return null;
        }
        if (optional.size() != 1) {
            return new BooleanScorer(this, this.disableCoord, this.maxCoord, optional, Math.max(1, this.query.getMinimumNumberShouldMatch()), this.needsScores);
        }
        final BulkScorer opt = optional.get(0);
        if (!this.disableCoord && this.maxCoord > 1) {
            return new BooleanTopLevelScorers.BoostedBulkScorer(opt, this.coord(1, this.maxCoord));
        }
        return opt;
    }
    
    private BulkScorer requiredBulkScorer(final LeafReaderContext context) throws IOException {
        BulkScorer scorer = null;
        final Iterator<BooleanClause> cIter = this.query.iterator();
        for (final Weight w : this.weights) {
            final BooleanClause c = cIter.next();
            if (!c.isRequired()) {
                continue;
            }
            if (scorer != null) {
                return null;
            }
            scorer = w.bulkScorer(context);
            if (scorer == null) {
                return null;
            }
            if (!c.isScoring()) {
                if (!this.needsScores) {
                    continue;
                }
                scorer = disableScoring(scorer);
            }
            else {
                assert this.maxCoord == 1;
                continue;
            }
        }
        return scorer;
    }
    
    BulkScorer booleanScorer(final LeafReaderContext context) throws IOException {
        final int numOptionalClauses = this.query.getClauses(BooleanClause.Occur.SHOULD).size();
        final int numRequiredClauses = this.query.getClauses(BooleanClause.Occur.MUST).size() + this.query.getClauses(BooleanClause.Occur.FILTER).size();
        BulkScorer positiveScorer;
        if (numRequiredClauses == 0) {
            positiveScorer = this.optionalBulkScorer(context);
            if (positiveScorer == null) {
                return null;
            }
            long costThreshold;
            if (this.query.getMinimumNumberShouldMatch() <= 1) {
                costThreshold = -1L;
            }
            else {
                costThreshold = context.reader().maxDoc() / 3;
            }
            if (positiveScorer.cost() < costThreshold) {
                return null;
            }
        }
        else {
            if (numRequiredClauses != 1 || numOptionalClauses != 0 || this.query.getMinimumNumberShouldMatch() != 0) {
                return null;
            }
            positiveScorer = this.requiredBulkScorer(context);
        }
        if (positiveScorer == null) {
            return null;
        }
        final List<Scorer> prohibited = new ArrayList<Scorer>();
        final Iterator<BooleanClause> cIter = this.query.iterator();
        for (final Weight w : this.weights) {
            final BooleanClause c = cIter.next();
            if (c.isProhibited()) {
                final Scorer scorer = w.scorer(context);
                if (scorer == null) {
                    continue;
                }
                prohibited.add(scorer);
            }
        }
        if (prohibited.isEmpty()) {
            return positiveScorer;
        }
        final Scorer prohibitedScorer = this.opt(prohibited, 1, true);
        if (prohibitedScorer.twoPhaseIterator() != null) {
            return null;
        }
        return new ReqExclBulkScorer(positiveScorer, prohibitedScorer.iterator());
    }
    
    @Override
    public BulkScorer bulkScorer(final LeafReaderContext context) throws IOException {
        final BulkScorer bulkScorer = this.booleanScorer(context);
        if (bulkScorer != null) {
            return bulkScorer;
        }
        return super.bulkScorer(context);
    }
    
    @Override
    public Scorer scorer(final LeafReaderContext context) throws IOException {
        int minShouldMatch = this.query.getMinimumNumberShouldMatch();
        final List<Scorer> required = new ArrayList<Scorer>();
        final List<Scorer> requiredScoring = new ArrayList<Scorer>();
        final List<Scorer> prohibited = new ArrayList<Scorer>();
        final List<Scorer> optional = new ArrayList<Scorer>();
        final Iterator<BooleanClause> cIter = this.query.iterator();
        for (final Weight w : this.weights) {
            final BooleanClause c = cIter.next();
            final Scorer subScorer = w.scorer(context);
            if (subScorer == null) {
                if (c.isRequired()) {
                    return null;
                }
                continue;
            }
            else if (c.isRequired()) {
                required.add(subScorer);
                if (!c.isScoring()) {
                    continue;
                }
                requiredScoring.add(subScorer);
            }
            else if (c.isProhibited()) {
                prohibited.add(subScorer);
            }
            else {
                optional.add(subScorer);
            }
        }
        if (optional.size() == minShouldMatch) {
            required.addAll(optional);
            requiredScoring.addAll(optional);
            optional.clear();
            minShouldMatch = 0;
        }
        if (required.isEmpty() && optional.isEmpty()) {
            return null;
        }
        if (optional.size() < minShouldMatch) {
            return null;
        }
        if (!this.needsScores && minShouldMatch == 0 && required.size() > 0) {
            optional.clear();
        }
        if (optional.isEmpty()) {
            return this.excl(this.req(required, requiredScoring, this.disableCoord), prohibited);
        }
        if (required.isEmpty()) {
            return this.excl(this.opt(optional, minShouldMatch, this.disableCoord), prohibited);
        }
        final Scorer req = this.excl(this.req(required, requiredScoring, true), prohibited);
        final Scorer opt = this.opt(optional, minShouldMatch, true);
        if (this.disableCoord) {
            if (minShouldMatch > 0) {
                return new ConjunctionScorer(this, Arrays.asList(req, opt), Arrays.asList(req, opt), 1.0f);
            }
            return new ReqOptSumScorer(req, opt);
        }
        else if (optional.size() == 1) {
            if (minShouldMatch > 0) {
                return new ConjunctionScorer(this, Arrays.asList(req, opt), Arrays.asList(req, opt), this.coord(requiredScoring.size() + 1, this.maxCoord));
            }
            final float coordReq = this.coord(requiredScoring.size(), this.maxCoord);
            final float coordBoth = this.coord(requiredScoring.size() + 1, this.maxCoord);
            return new BooleanTopLevelScorers.ReqSingleOptScorer(req, opt, coordReq, coordBoth);
        }
        else {
            if (minShouldMatch > 0) {
                return new BooleanTopLevelScorers.CoordinatingConjunctionScorer(this, this.coords, req, requiredScoring.size(), opt);
            }
            return new BooleanTopLevelScorers.ReqMultiOptScorer(req, opt, requiredScoring.size(), this.coords);
        }
    }
    
    private Scorer req(final List<Scorer> required, final List<Scorer> requiredScoring, final boolean disableCoord) {
        if (required.size() != 1) {
            return new ConjunctionScorer(this, required, requiredScoring, disableCoord ? 1.0f : this.coord(requiredScoring.size(), this.maxCoord));
        }
        final Scorer req = required.get(0);
        if (!this.needsScores) {
            return req;
        }
        if (requiredScoring.isEmpty()) {
            return new FilterScorer(req) {
                @Override
                public float score() throws IOException {
                    return 0.0f;
                }
                
                @Override
                public int freq() throws IOException {
                    return 0;
                }
            };
        }
        float boost = 1.0f;
        if (!disableCoord) {
            boost = this.coord(1, this.maxCoord);
        }
        if (boost == 1.0f) {
            return req;
        }
        return new BooleanTopLevelScorers.BoostedScorer(req, boost);
    }
    
    private Scorer excl(final Scorer main, final List<Scorer> prohibited) throws IOException {
        if (prohibited.isEmpty()) {
            return main;
        }
        if (prohibited.size() == 1) {
            return new ReqExclScorer(main, prohibited.get(0));
        }
        final float[] coords = new float[prohibited.size() + 1];
        Arrays.fill(coords, 1.0f);
        return new ReqExclScorer(main, new DisjunctionSumScorer(this, prohibited, coords, false));
    }
    
    private Scorer opt(final List<Scorer> optional, final int minShouldMatch, final boolean disableCoord) throws IOException {
        if (optional.size() == 1) {
            final Scorer opt = optional.get(0);
            if (!disableCoord && this.maxCoord > 1) {
                return new BooleanTopLevelScorers.BoostedScorer(opt, this.coord(1, this.maxCoord));
            }
            return opt;
        }
        else {
            float[] coords;
            if (disableCoord) {
                coords = new float[optional.size() + 1];
                Arrays.fill(coords, 1.0f);
            }
            else {
                coords = this.coords;
            }
            if (minShouldMatch > 1) {
                return new MinShouldMatchSumScorer(this, optional, minShouldMatch, coords);
            }
            return new DisjunctionSumScorer(this, optional, coords, this.needsScores);
        }
    }
}
