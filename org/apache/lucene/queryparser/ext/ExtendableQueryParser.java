package org.apache.lucene.queryparser.ext;

import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.Query;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryparser.classic.QueryParser;

public class ExtendableQueryParser extends QueryParser
{
    private final String defaultField;
    private final Extensions extensions;
    private static final Extensions DEFAULT_EXTENSION;
    
    public ExtendableQueryParser(final String f, final Analyzer a) {
        this(f, a, ExtendableQueryParser.DEFAULT_EXTENSION);
    }
    
    public ExtendableQueryParser(final String f, final Analyzer a, final Extensions ext) {
        super(f, a);
        this.defaultField = f;
        this.extensions = ext;
    }
    
    public char getExtensionFieldDelimiter() {
        return this.extensions.getExtensionFieldDelimiter();
    }
    
    @Override
    protected Query getFieldQuery(final String field, final String queryText, final boolean quoted) throws ParseException {
        final Extensions.Pair<String, String> splitExtensionField = this.extensions.splitExtensionField(this.defaultField, field);
        final ParserExtension extension = this.extensions.getExtension(splitExtensionField.cud);
        if (extension != null) {
            return extension.parse(new ExtensionQuery(this, splitExtensionField.cur, queryText));
        }
        return super.getFieldQuery(field, queryText, quoted);
    }
    
    static {
        DEFAULT_EXTENSION = new Extensions();
    }
}
