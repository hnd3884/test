package org.apache.lucene.facet.sortedset;

import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.facet.FacetField;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.Field;

public class SortedSetDocValuesFacetField extends Field
{
    public static final FieldType TYPE;
    public final String dim;
    public final String label;
    
    public SortedSetDocValuesFacetField(final String dim, final String label) {
        super("dummy", SortedSetDocValuesFacetField.TYPE);
        FacetField.verifyLabel(label);
        FacetField.verifyLabel(dim);
        this.dim = dim;
        this.label = label;
    }
    
    public String toString() {
        return "SortedSetDocValuesFacetField(dim=" + this.dim + " label=" + this.label + ")";
    }
    
    static {
        (TYPE = new FieldType()).setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
        SortedSetDocValuesFacetField.TYPE.freeze();
    }
}
