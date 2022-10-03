package org.apache.lucene.queryparser.flexible.standard.processors;

import java.util.Iterator;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.queryparser.flexible.core.nodes.ModifierQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.BooleanQueryNode;
import java.util.Collections;
import org.apache.lucene.queryparser.flexible.core.nodes.GroupQueryNode;
import java.util.List;
import org.apache.lucene.queryparser.flexible.standard.nodes.StandardBooleanQueryNode;
import java.util.LinkedList;
import java.util.ArrayList;
import org.apache.lucene.queryparser.flexible.standard.nodes.MultiPhraseQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.TokenizedPhraseQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.QuotedFieldQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.NoTokenFoundQueryNode;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import java.io.IOException;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.CachingTokenFilter;
import org.apache.lucene.queryparser.flexible.core.nodes.FieldQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.RangeQueryNode;
import org.apache.lucene.queryparser.flexible.standard.nodes.RegexpQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.FuzzyQueryNode;
import org.apache.lucene.queryparser.flexible.standard.nodes.WildcardQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.TextableQueryNode;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;
import org.apache.lucene.queryparser.flexible.standard.config.StandardQueryConfigHandler;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryparser.flexible.core.processors.QueryNodeProcessorImpl;

public class AnalyzerQueryNodeProcessor extends QueryNodeProcessorImpl
{
    private Analyzer analyzer;
    private boolean positionIncrementsEnabled;
    private StandardQueryConfigHandler.Operator defaultOperator;
    
    @Override
    public QueryNode process(final QueryNode queryTree) throws QueryNodeException {
        final Analyzer analyzer = this.getQueryConfigHandler().get(StandardQueryConfigHandler.ConfigurationKeys.ANALYZER);
        if (analyzer != null) {
            this.analyzer = analyzer;
            this.positionIncrementsEnabled = false;
            final Boolean positionIncrementsEnabled = this.getQueryConfigHandler().get(StandardQueryConfigHandler.ConfigurationKeys.ENABLE_POSITION_INCREMENTS);
            final StandardQueryConfigHandler.Operator defaultOperator = this.getQueryConfigHandler().get(StandardQueryConfigHandler.ConfigurationKeys.DEFAULT_OPERATOR);
            this.defaultOperator = ((defaultOperator != null) ? defaultOperator : StandardQueryConfigHandler.Operator.OR);
            if (positionIncrementsEnabled != null) {
                this.positionIncrementsEnabled = positionIncrementsEnabled;
            }
            if (this.analyzer != null) {
                return super.process(queryTree);
            }
        }
        return queryTree;
    }
    
    @Override
    protected QueryNode postProcessNode(final QueryNode node) throws QueryNodeException {
        if (node instanceof TextableQueryNode && !(node instanceof WildcardQueryNode) && !(node instanceof FuzzyQueryNode) && !(node instanceof RegexpQueryNode) && !(node.getParent() instanceof RangeQueryNode)) {
            final FieldQueryNode fieldNode = (FieldQueryNode)node;
            final String text = fieldNode.getTextAsString();
            final String field = fieldNode.getFieldAsString();
            CachingTokenFilter buffer = null;
            PositionIncrementAttribute posIncrAtt = null;
            int numTokens = 0;
            int positionCount = 0;
            boolean severalTokensAtSamePosition = false;
            try {
                try (final TokenStream source = this.analyzer.tokenStream(field, text)) {
                    buffer = new CachingTokenFilter(source);
                    buffer.reset();
                    if (buffer.hasAttribute((Class)PositionIncrementAttribute.class)) {
                        posIncrAtt = (PositionIncrementAttribute)buffer.getAttribute((Class)PositionIncrementAttribute.class);
                    }
                    try {
                        while (buffer.incrementToken()) {
                            ++numTokens;
                            final int positionIncrement = (posIncrAtt != null) ? posIncrAtt.getPositionIncrement() : 1;
                            if (positionIncrement != 0) {
                                positionCount += positionIncrement;
                            }
                            else {
                                severalTokensAtSamePosition = true;
                            }
                        }
                    }
                    catch (final IOException ex) {}
                    buffer.reset();
                }
                catch (final IOException e) {
                    throw new RuntimeException(e);
                }
                if (!buffer.hasAttribute((Class)CharTermAttribute.class)) {
                    return new NoTokenFoundQueryNode();
                }
                final CharTermAttribute termAtt = (CharTermAttribute)buffer.getAttribute((Class)CharTermAttribute.class);
                if (numTokens == 0) {
                    return new NoTokenFoundQueryNode();
                }
                if (numTokens == 1) {
                    String term = null;
                    try {
                        final boolean hasNext = buffer.incrementToken();
                        assert hasNext;
                        term = termAtt.toString();
                    }
                    catch (final IOException ex2) {}
                    fieldNode.setText(term);
                    return fieldNode;
                }
                if (!severalTokensAtSamePosition && node instanceof QuotedFieldQueryNode) {
                    final TokenizedPhraseQueryNode pq = new TokenizedPhraseQueryNode();
                    int position = -1;
                    for (int i = 0; i < numTokens; ++i) {
                        String term2 = null;
                        int positionIncrement2 = 1;
                        try {
                            final boolean hasNext2 = buffer.incrementToken();
                            assert hasNext2;
                            term2 = termAtt.toString();
                            if (posIncrAtt != null) {
                                positionIncrement2 = posIncrAtt.getPositionIncrement();
                            }
                        }
                        catch (final IOException ex3) {}
                        final FieldQueryNode newFieldNode = new FieldQueryNode(field, term2, -1, -1);
                        if (this.positionIncrementsEnabled) {
                            position += positionIncrement2;
                            newFieldNode.setPositionIncrement(position);
                        }
                        else {
                            newFieldNode.setPositionIncrement(i);
                        }
                        pq.add(newFieldNode);
                    }
                    return pq;
                }
                if (positionCount != 1 && node instanceof QuotedFieldQueryNode) {
                    final MultiPhraseQueryNode mpq = new MultiPhraseQueryNode();
                    final List<FieldQueryNode> multiTerms = new ArrayList<FieldQueryNode>();
                    int position2 = -1;
                    int j = 0;
                    int termGroupCount = 0;
                    while (j < numTokens) {
                        String term3 = null;
                        int positionIncrement3 = 1;
                        try {
                            final boolean hasNext3 = buffer.incrementToken();
                            assert hasNext3;
                            term3 = termAtt.toString();
                            if (posIncrAtt != null) {
                                positionIncrement3 = posIncrAtt.getPositionIncrement();
                            }
                        }
                        catch (final IOException ex4) {}
                        if (positionIncrement3 > 0 && multiTerms.size() > 0) {
                            for (final FieldQueryNode termNode : multiTerms) {
                                if (this.positionIncrementsEnabled) {
                                    termNode.setPositionIncrement(position2);
                                }
                                else {
                                    termNode.setPositionIncrement(termGroupCount);
                                }
                                mpq.add(termNode);
                            }
                            ++termGroupCount;
                            multiTerms.clear();
                        }
                        position2 += positionIncrement3;
                        multiTerms.add(new FieldQueryNode(field, term3, -1, -1));
                        ++j;
                    }
                    for (final FieldQueryNode termNode2 : multiTerms) {
                        if (this.positionIncrementsEnabled) {
                            termNode2.setPositionIncrement(position2);
                        }
                        else {
                            termNode2.setPositionIncrement(termGroupCount);
                        }
                        mpq.add(termNode2);
                    }
                    return mpq;
                }
                if (positionCount == 1) {
                    final LinkedList<QueryNode> children = new LinkedList<QueryNode>();
                    for (int k = 0; k < numTokens; ++k) {
                        String term4 = null;
                        try {
                            final boolean hasNext4 = buffer.incrementToken();
                            assert hasNext4;
                            term4 = termAtt.toString();
                        }
                        catch (final IOException ex5) {}
                        children.add(new FieldQueryNode(field, term4, -1, -1));
                    }
                    return new GroupQueryNode(new StandardBooleanQueryNode(children, positionCount == 1));
                }
                QueryNode q = new StandardBooleanQueryNode(Collections.emptyList(), false);
                QueryNode currentQuery = null;
                for (int i = 0; i < numTokens; ++i) {
                    String term2 = null;
                    try {
                        final boolean hasNext5 = buffer.incrementToken();
                        assert hasNext5;
                        term2 = termAtt.toString();
                    }
                    catch (final IOException ex6) {}
                    if (posIncrAtt != null && posIncrAtt.getPositionIncrement() == 0) {
                        if (!(currentQuery instanceof BooleanQueryNode)) {
                            final QueryNode t = currentQuery;
                            currentQuery = new StandardBooleanQueryNode(Collections.emptyList(), true);
                            ((BooleanQueryNode)currentQuery).add(t);
                        }
                        ((BooleanQueryNode)currentQuery).add(new FieldQueryNode(field, term2, -1, -1));
                    }
                    else {
                        if (currentQuery != null) {
                            if (this.defaultOperator == StandardQueryConfigHandler.Operator.OR) {
                                q.add(currentQuery);
                            }
                            else {
                                q.add(new ModifierQueryNode(currentQuery, ModifierQueryNode.Modifier.MOD_REQ));
                            }
                        }
                        currentQuery = new FieldQueryNode(field, term2, -1, -1);
                    }
                }
                if (this.defaultOperator == StandardQueryConfigHandler.Operator.OR) {
                    q.add(currentQuery);
                }
                else {
                    q.add(new ModifierQueryNode(currentQuery, ModifierQueryNode.Modifier.MOD_REQ));
                }
                if (q instanceof BooleanQueryNode) {
                    q = new GroupQueryNode(q);
                }
                return q;
            }
            finally {
                if (buffer != null) {
                    try {
                        buffer.close();
                    }
                    catch (final IOException ex7) {}
                }
            }
        }
        return node;
    }
    
    @Override
    protected QueryNode preProcessNode(final QueryNode node) throws QueryNodeException {
        return node;
    }
    
    @Override
    protected List<QueryNode> setChildrenOrder(final List<QueryNode> children) throws QueryNodeException {
        return children;
    }
}
