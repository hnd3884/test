package org.apache.lucene.queryparser.flexible.core;

import org.apache.lucene.queryparser.flexible.messages.Message;
import org.apache.lucene.queryparser.flexible.messages.NLSException;

public class QueryNodeError extends Error implements NLSException
{
    private Message message;
    
    public QueryNodeError(final Message message) {
        super(message.getKey());
        this.message = message;
    }
    
    public QueryNodeError(final Throwable throwable) {
        super(throwable);
    }
    
    public QueryNodeError(final Message message, final Throwable throwable) {
        super(message.getKey(), throwable);
        this.message = message;
    }
    
    @Override
    public Message getMessageObject() {
        return this.message;
    }
}
