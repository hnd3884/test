package org.apache.lucene.document;

import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.analysis.TokenStream;
import java.io.Reader;

public final class TextField extends Field
{
    public static final FieldType TYPE_NOT_STORED;
    public static final FieldType TYPE_STORED;
    
    public TextField(final String name, final Reader reader) {
        super(name, reader, TextField.TYPE_NOT_STORED);
    }
    
    public TextField(final String name, final String value, final Store store) {
        super(name, value, (store == Store.YES) ? TextField.TYPE_STORED : TextField.TYPE_NOT_STORED);
    }
    
    public TextField(final String name, final TokenStream stream) {
        super(name, stream, TextField.TYPE_NOT_STORED);
    }
    
    static {
        TYPE_NOT_STORED = new FieldType();
        TYPE_STORED = new FieldType();
        TextField.TYPE_NOT_STORED.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
        TextField.TYPE_NOT_STORED.setTokenized(true);
        TextField.TYPE_NOT_STORED.freeze();
        TextField.TYPE_STORED.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
        TextField.TYPE_STORED.setTokenized(true);
        TextField.TYPE_STORED.setStored(true);
        TextField.TYPE_STORED.freeze();
    }
}
