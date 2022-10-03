package org.apache.lucene.facet;

import org.apache.lucene.index.IndexOptions;
import java.util.Arrays;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.Field;

public class FacetField extends Field
{
    static final FieldType TYPE;
    public final String dim;
    public final String[] path;
    
    public FacetField(final String dim, final String... path) {
        super("dummy", FacetField.TYPE);
        verifyLabel(dim);
        for (final String label : path) {
            verifyLabel(label);
        }
        this.dim = dim;
        if (path.length == 0) {
            throw new IllegalArgumentException("path must have at least one element");
        }
        this.path = path;
    }
    
    public String toString() {
        return "FacetField(dim=" + this.dim + " path=" + Arrays.toString(this.path) + ")";
    }
    
    public static void verifyLabel(final String label) {
        if (label == null || label.isEmpty()) {
            throw new IllegalArgumentException("empty or null components not allowed; got: " + label);
        }
    }
    
    static {
        (TYPE = new FieldType()).setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
        FacetField.TYPE.freeze();
    }
}
