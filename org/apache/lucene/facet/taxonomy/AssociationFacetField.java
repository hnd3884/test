package org.apache.lucene.facet.taxonomy;

import org.apache.lucene.index.IndexOptions;
import java.util.Arrays;
import org.apache.lucene.facet.FacetField;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.Field;

public class AssociationFacetField extends Field
{
    public static final FieldType TYPE;
    public final String dim;
    public final String[] path;
    public final BytesRef assoc;
    
    public AssociationFacetField(final BytesRef assoc, final String dim, final String... path) {
        super("dummy", AssociationFacetField.TYPE);
        FacetField.verifyLabel(dim);
        for (final String label : path) {
            FacetField.verifyLabel(label);
        }
        this.dim = dim;
        this.assoc = assoc;
        if (path.length == 0) {
            throw new IllegalArgumentException("path must have at least one element");
        }
        this.path = path;
    }
    
    public String toString() {
        return "AssociationFacetField(dim=" + this.dim + " path=" + Arrays.toString(this.path) + " bytes=" + this.assoc + ")";
    }
    
    static {
        (TYPE = new FieldType()).setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
        AssociationFacetField.TYPE.freeze();
    }
}
