package org.apache.lucene.queryparser.ext;

import org.apache.lucene.queryparser.classic.QueryParserBase;
import java.util.HashMap;
import java.util.Map;

public class Extensions
{
    private final Map<String, ParserExtension> extensions;
    private final char extensionFieldDelimiter;
    public static final char DEFAULT_EXTENSION_FIELD_DELIMITER = ':';
    
    public Extensions() {
        this(':');
    }
    
    public Extensions(final char extensionFieldDelimiter) {
        this.extensions = new HashMap<String, ParserExtension>();
        this.extensionFieldDelimiter = extensionFieldDelimiter;
    }
    
    public void add(final String key, final ParserExtension extension) {
        this.extensions.put(key, extension);
    }
    
    public final ParserExtension getExtension(final String key) {
        return this.extensions.get(key);
    }
    
    public char getExtensionFieldDelimiter() {
        return this.extensionFieldDelimiter;
    }
    
    public Pair<String, String> splitExtensionField(final String defaultField, final String field) {
        final int indexOf = field.indexOf(this.extensionFieldDelimiter);
        if (indexOf < 0) {
            return new Pair<String, String>(field, null);
        }
        final String indexField = (indexOf == 0) ? defaultField : field.substring(0, indexOf);
        final String extensionKey = field.substring(indexOf + 1);
        return new Pair<String, String>(indexField, extensionKey);
    }
    
    public String escapeExtensionField(final String extfield) {
        return QueryParserBase.escape(extfield);
    }
    
    public String buildExtensionField(final String extensionKey) {
        return this.buildExtensionField(extensionKey, "");
    }
    
    public String buildExtensionField(final String extensionKey, final String field) {
        final StringBuilder builder = new StringBuilder(field);
        builder.append(this.extensionFieldDelimiter);
        builder.append(extensionKey);
        return this.escapeExtensionField(builder.toString());
    }
    
    public static class Pair<Cur, Cud>
    {
        public final Cur cur;
        public final Cud cud;
        
        public Pair(final Cur cur, final Cud cud) {
            this.cur = cur;
            this.cud = cud;
        }
    }
}
