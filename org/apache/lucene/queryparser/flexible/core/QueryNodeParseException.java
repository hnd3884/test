package org.apache.lucene.queryparser.flexible.core;

import org.apache.lucene.queryparser.flexible.messages.MessageImpl;
import org.apache.lucene.queryparser.flexible.core.messages.QueryParserMessages;
import org.apache.lucene.queryparser.flexible.messages.Message;

public class QueryNodeParseException extends QueryNodeException
{
    private CharSequence query;
    private int beginColumn;
    private int beginLine;
    private String errorToken;
    
    public QueryNodeParseException(final Message message) {
        super(message);
        this.beginColumn = -1;
        this.beginLine = -1;
        this.errorToken = "";
    }
    
    public QueryNodeParseException(final Throwable throwable) {
        super(throwable);
        this.beginColumn = -1;
        this.beginLine = -1;
        this.errorToken = "";
    }
    
    public QueryNodeParseException(final Message message, final Throwable throwable) {
        super(message, throwable);
        this.beginColumn = -1;
        this.beginLine = -1;
        this.errorToken = "";
    }
    
    public void setQuery(final CharSequence query) {
        this.query = query;
        this.message = new MessageImpl(QueryParserMessages.INVALID_SYNTAX_CANNOT_PARSE, new Object[] { query, "" });
    }
    
    public CharSequence getQuery() {
        return this.query;
    }
    
    protected void setErrorToken(final String errorToken) {
        this.errorToken = errorToken;
    }
    
    public String getErrorToken() {
        return this.errorToken;
    }
    
    public void setNonLocalizedMessage(final Message message) {
        this.message = message;
    }
    
    public int getBeginLine() {
        return this.beginLine;
    }
    
    public int getBeginColumn() {
        return this.beginColumn;
    }
    
    protected void setBeginLine(final int beginLine) {
        this.beginLine = beginLine;
    }
    
    protected void setBeginColumn(final int beginColumn) {
        this.beginColumn = beginColumn;
    }
}
