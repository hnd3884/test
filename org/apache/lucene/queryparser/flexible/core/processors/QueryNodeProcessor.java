package org.apache.lucene.queryparser.flexible.core.processors;

import org.apache.lucene.queryparser.flexible.core.config.QueryConfigHandler;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;

public interface QueryNodeProcessor
{
    QueryNode process(final QueryNode p0) throws QueryNodeException;
    
    void setQueryConfigHandler(final QueryConfigHandler p0);
    
    QueryConfigHandler getQueryConfigHandler();
}
