package org.apache.lucene.queryparser.flexible.core.builders;

import org.apache.lucene.queryparser.flexible.messages.Message;
import org.apache.lucene.queryparser.flexible.messages.MessageImpl;
import org.apache.lucene.queryparser.flexible.core.parser.EscapeQuerySyntax;
import org.apache.lucene.queryparser.flexible.standard.parser.EscapeQuerySyntaxImpl;
import org.apache.lucene.queryparser.flexible.core.messages.QueryParserMessages;
import org.apache.lucene.queryparser.flexible.core.nodes.FieldableNode;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import java.util.Iterator;
import java.util.List;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;
import java.util.HashMap;

public class QueryTreeBuilder implements QueryBuilder
{
    public static final String QUERY_TREE_BUILDER_TAGID;
    private HashMap<Class<? extends QueryNode>, QueryBuilder> queryNodeBuilders;
    private HashMap<String, QueryBuilder> fieldNameBuilders;
    
    public void setBuilder(final CharSequence fieldName, final QueryBuilder builder) {
        if (this.fieldNameBuilders == null) {
            this.fieldNameBuilders = new HashMap<String, QueryBuilder>();
        }
        this.fieldNameBuilders.put(fieldName.toString(), builder);
    }
    
    public void setBuilder(final Class<? extends QueryNode> queryNodeClass, final QueryBuilder builder) {
        if (this.queryNodeBuilders == null) {
            this.queryNodeBuilders = new HashMap<Class<? extends QueryNode>, QueryBuilder>();
        }
        this.queryNodeBuilders.put(queryNodeClass, builder);
    }
    
    private void process(final QueryNode node) throws QueryNodeException {
        if (node != null) {
            final QueryBuilder builder = this.getBuilder(node);
            if (!(builder instanceof QueryTreeBuilder)) {
                final List<QueryNode> children = node.getChildren();
                if (children != null) {
                    for (final QueryNode child : children) {
                        this.process(child);
                    }
                }
            }
            this.processNode(node, builder);
        }
    }
    
    private QueryBuilder getBuilder(final QueryNode node) {
        QueryBuilder builder = null;
        if (this.fieldNameBuilders != null && node instanceof FieldableNode) {
            CharSequence field = ((FieldableNode)node).getField();
            if (field != null) {
                field = field.toString();
            }
            builder = this.fieldNameBuilders.get(field);
        }
        if (builder == null && this.queryNodeBuilders != null) {
            Class<?> clazz = node.getClass();
            do {
                builder = this.getQueryBuilder(clazz);
                if (builder == null) {
                    final Class[] arr$;
                    final Class<?>[] classes = arr$ = clazz.getInterfaces();
                    for (final Class<?> actualClass : arr$) {
                        builder = this.getQueryBuilder(actualClass);
                        if (builder != null) {
                            break;
                        }
                    }
                }
            } while (builder == null && (clazz = clazz.getSuperclass()) != null);
        }
        return builder;
    }
    
    private void processNode(final QueryNode node, final QueryBuilder builder) throws QueryNodeException {
        if (builder == null) {
            throw new QueryNodeException(new MessageImpl(QueryParserMessages.LUCENE_QUERY_CONVERSION_ERROR, new Object[] { node.toQueryString(new EscapeQuerySyntaxImpl()), node.getClass().getName() }));
        }
        final Object obj = builder.build(node);
        if (obj != null) {
            node.setTag(QueryTreeBuilder.QUERY_TREE_BUILDER_TAGID, obj);
        }
    }
    
    private QueryBuilder getQueryBuilder(final Class<?> clazz) {
        if (QueryNode.class.isAssignableFrom(clazz)) {
            return this.queryNodeBuilders.get(clazz);
        }
        return null;
    }
    
    @Override
    public Object build(final QueryNode queryNode) throws QueryNodeException {
        this.process(queryNode);
        return queryNode.getTag(QueryTreeBuilder.QUERY_TREE_BUILDER_TAGID);
    }
    
    static {
        QUERY_TREE_BUILDER_TAGID = QueryTreeBuilder.class.getName();
    }
}
