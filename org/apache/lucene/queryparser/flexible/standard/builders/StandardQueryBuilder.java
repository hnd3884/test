package org.apache.lucene.queryparser.flexible.standard.builders;

import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.search.Query;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;
import org.apache.lucene.queryparser.flexible.core.builders.QueryBuilder;

public interface StandardQueryBuilder extends QueryBuilder
{
    Query build(final QueryNode p0) throws QueryNodeException;
}
