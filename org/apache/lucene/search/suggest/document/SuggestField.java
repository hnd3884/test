package org.apache.lucene.search.suggest.document;

import org.apache.lucene.index.IndexOptions;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.lucene.store.OutputStreamDataOutput;
import java.io.ByteArrayOutputStream;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.Field;

public class SuggestField extends Field
{
    public static final FieldType FIELD_TYPE;
    static final byte TYPE = 0;
    private final BytesRef surfaceForm;
    private final int weight;
    
    public SuggestField(final String name, final String value, final int weight) {
        super(name, value, SuggestField.FIELD_TYPE);
        if (weight < 0) {
            throw new IllegalArgumentException("weight must be >= 0");
        }
        if (value.length() == 0) {
            throw new IllegalArgumentException("value must have a length > 0");
        }
        for (int i = 0; i < value.length(); ++i) {
            if (this.isReserved(value.charAt(i))) {
                throw new IllegalArgumentException("Illegal input [" + value + "] UTF-16 codepoint [0x" + Integer.toHexString(value.charAt(i)) + "] at position " + i + " is a reserved character");
            }
        }
        this.surfaceForm = new BytesRef((CharSequence)value);
        this.weight = weight;
    }
    
    public TokenStream tokenStream(final Analyzer analyzer, final TokenStream reuse) {
        final CompletionTokenStream completionStream = this.wrapTokenStream(super.tokenStream(analyzer, reuse));
        completionStream.setPayload(this.buildSuggestPayload());
        return completionStream;
    }
    
    protected CompletionTokenStream wrapTokenStream(final TokenStream stream) {
        if (stream instanceof CompletionTokenStream) {
            return (CompletionTokenStream)stream;
        }
        return new CompletionTokenStream(stream);
    }
    
    protected byte type() {
        return 0;
    }
    
    private BytesRef buildSuggestPayload() {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (final OutputStreamDataOutput output = new OutputStreamDataOutput((OutputStream)byteArrayOutputStream)) {
            output.writeVInt(this.surfaceForm.length);
            output.writeBytes(this.surfaceForm.bytes, this.surfaceForm.offset, this.surfaceForm.length);
            output.writeVInt(this.weight + 1);
            output.writeByte(this.type());
        }
        catch (final IOException e) {
            throw new RuntimeException(e);
        }
        return new BytesRef(byteArrayOutputStream.toByteArray());
    }
    
    private boolean isReserved(final char c) {
        switch (c) {
            case '\0':
            case '\u001e':
            case '\u001f': {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    static {
        (FIELD_TYPE = new FieldType()).setTokenized(true);
        SuggestField.FIELD_TYPE.setStored(false);
        SuggestField.FIELD_TYPE.setStoreTermVectors(false);
        SuggestField.FIELD_TYPE.setOmitNorms(false);
        SuggestField.FIELD_TYPE.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
        SuggestField.FIELD_TYPE.freeze();
    }
}
