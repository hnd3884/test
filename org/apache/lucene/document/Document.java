package org.apache.lucene.document;

import org.apache.lucene.util.BytesRef;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.index.IndexableField;

public final class Document implements Iterable<IndexableField>
{
    private final List<IndexableField> fields;
    private static final String[] NO_STRINGS;
    
    public Document() {
        this.fields = new ArrayList<IndexableField>();
    }
    
    @Override
    public Iterator<IndexableField> iterator() {
        return this.fields.iterator();
    }
    
    public final void add(final IndexableField field) {
        this.fields.add(field);
    }
    
    public final void removeField(final String name) {
        final Iterator<IndexableField> it = this.fields.iterator();
        while (it.hasNext()) {
            final IndexableField field = it.next();
            if (field.name().equals(name)) {
                it.remove();
            }
        }
    }
    
    public final void removeFields(final String name) {
        final Iterator<IndexableField> it = this.fields.iterator();
        while (it.hasNext()) {
            final IndexableField field = it.next();
            if (field.name().equals(name)) {
                it.remove();
            }
        }
    }
    
    public final BytesRef[] getBinaryValues(final String name) {
        final List<BytesRef> result = new ArrayList<BytesRef>();
        for (final IndexableField field : this.fields) {
            if (field.name().equals(name)) {
                final BytesRef bytes = field.binaryValue();
                if (bytes == null) {
                    continue;
                }
                result.add(bytes);
            }
        }
        return result.toArray(new BytesRef[result.size()]);
    }
    
    public final BytesRef getBinaryValue(final String name) {
        for (final IndexableField field : this.fields) {
            if (field.name().equals(name)) {
                final BytesRef bytes = field.binaryValue();
                if (bytes != null) {
                    return bytes;
                }
                continue;
            }
        }
        return null;
    }
    
    public final IndexableField getField(final String name) {
        for (final IndexableField field : this.fields) {
            if (field.name().equals(name)) {
                return field;
            }
        }
        return null;
    }
    
    public IndexableField[] getFields(final String name) {
        final List<IndexableField> result = new ArrayList<IndexableField>();
        for (final IndexableField field : this.fields) {
            if (field.name().equals(name)) {
                result.add(field);
            }
        }
        return result.toArray(new IndexableField[result.size()]);
    }
    
    public final List<IndexableField> getFields() {
        return this.fields;
    }
    
    public final String[] getValues(final String name) {
        final List<String> result = new ArrayList<String>();
        for (final IndexableField field : this.fields) {
            if (field.name().equals(name) && field.stringValue() != null) {
                result.add(field.stringValue());
            }
        }
        if (result.size() == 0) {
            return Document.NO_STRINGS;
        }
        return result.toArray(new String[result.size()]);
    }
    
    public final String get(final String name) {
        for (final IndexableField field : this.fields) {
            if (field.name().equals(name) && field.stringValue() != null) {
                return field.stringValue();
            }
        }
        return null;
    }
    
    @Override
    public final String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("Document<");
        for (int i = 0; i < this.fields.size(); ++i) {
            final IndexableField field = this.fields.get(i);
            buffer.append(field.toString());
            if (i != this.fields.size() - 1) {
                buffer.append(" ");
            }
        }
        buffer.append(">");
        return buffer.toString();
    }
    
    static {
        NO_STRINGS = new String[0];
    }
}
