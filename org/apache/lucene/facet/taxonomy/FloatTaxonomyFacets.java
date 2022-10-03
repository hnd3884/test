package org.apache.lucene.facet.taxonomy;

import org.apache.lucene.facet.LabelAndValue;
import org.apache.lucene.facet.TopOrdAndFloatQueue;
import org.apache.lucene.facet.FacetResult;
import java.util.Iterator;
import java.util.Map;
import java.io.IOException;
import org.apache.lucene.facet.FacetsConfig;

public abstract class FloatTaxonomyFacets extends TaxonomyFacets
{
    protected final float[] values;
    
    protected FloatTaxonomyFacets(final String indexFieldName, final TaxonomyReader taxoReader, final FacetsConfig config) throws IOException {
        super(indexFieldName, taxoReader, config);
        this.values = new float[taxoReader.getSize()];
    }
    
    protected void rollup() throws IOException {
        for (final Map.Entry<String, FacetsConfig.DimConfig> ent : this.config.getDimConfigs().entrySet()) {
            final String dim = ent.getKey();
            final FacetsConfig.DimConfig ft = ent.getValue();
            if (ft.hierarchical && !ft.multiValued) {
                final int dimRootOrd = this.taxoReader.getOrdinal(new FacetLabel(new String[] { dim }));
                assert dimRootOrd > 0;
                final float[] values = this.values;
                final int n = dimRootOrd;
                values[n] += this.rollup(this.children[dimRootOrd]);
            }
        }
    }
    
    private float rollup(int ord) {
        float sum = 0.0f;
        while (ord != -1) {
            final float childValue = this.values[ord] + this.rollup(this.children[ord]);
            this.values[ord] = childValue;
            sum += childValue;
            ord = this.siblings[ord];
        }
        return sum;
    }
    
    @Override
    public Number getSpecificValue(final String dim, final String... path) throws IOException {
        final FacetsConfig.DimConfig dimConfig = this.verifyDim(dim);
        if (path.length == 0) {
            if (!dimConfig.hierarchical || dimConfig.multiValued) {
                if (!dimConfig.requireDimCount || !dimConfig.multiValued) {
                    throw new IllegalArgumentException("cannot return dimension-level value alone; use getTopChildren instead");
                }
            }
        }
        final int ord = this.taxoReader.getOrdinal(new FacetLabel(dim, path));
        if (ord < 0) {
            return -1;
        }
        return this.values[ord];
    }
    
    @Override
    public FacetResult getTopChildren(final int topN, final String dim, final String... path) throws IOException {
        if (topN <= 0) {
            throw new IllegalArgumentException("topN must be > 0 (got: " + topN + ")");
        }
        final FacetsConfig.DimConfig dimConfig = this.verifyDim(dim);
        final FacetLabel cp = new FacetLabel(dim, path);
        final int dimOrd = this.taxoReader.getOrdinal(cp);
        if (dimOrd == -1) {
            return null;
        }
        final TopOrdAndFloatQueue q = new TopOrdAndFloatQueue(Math.min(this.taxoReader.getSize(), topN));
        float bottomValue = 0.0f;
        int ord = this.children[dimOrd];
        float sumValues = 0.0f;
        int childCount = 0;
        TopOrdAndFloatQueue.OrdAndValue reuse = null;
        while (ord != -1) {
            if (this.values[ord] > 0.0f) {
                sumValues += this.values[ord];
                ++childCount;
                if (this.values[ord] > bottomValue) {
                    if (reuse == null) {
                        reuse = new TopOrdAndFloatQueue.OrdAndValue();
                    }
                    reuse.ord = ord;
                    reuse.value = this.values[ord];
                    reuse = (TopOrdAndFloatQueue.OrdAndValue)q.insertWithOverflow((Object)reuse);
                    if (q.size() == topN) {
                        bottomValue = ((TopOrdAndFloatQueue.OrdAndValue)q.top()).value;
                    }
                }
            }
            ord = this.siblings[ord];
        }
        if (sumValues == 0.0f) {
            return null;
        }
        if (dimConfig.multiValued) {
            if (dimConfig.requireDimCount) {
                sumValues = this.values[dimOrd];
            }
            else {
                sumValues = -1.0f;
            }
        }
        final LabelAndValue[] labelValues = new LabelAndValue[q.size()];
        for (int i = labelValues.length - 1; i >= 0; --i) {
            final TopOrdAndFloatQueue.OrdAndValue ordAndValue = (TopOrdAndFloatQueue.OrdAndValue)q.pop();
            final FacetLabel child = this.taxoReader.getPath(ordAndValue.ord);
            labelValues[i] = new LabelAndValue(child.components[cp.length], ordAndValue.value);
        }
        return new FacetResult(dim, path, sumValues, labelValues, childCount);
    }
}
