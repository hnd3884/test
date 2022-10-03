package org.apache.lucene.queryparser.flexible.precedence.processors;

import org.apache.lucene.queryparser.flexible.core.processors.QueryNodeProcessor;
import org.apache.lucene.queryparser.flexible.standard.processors.BooleanQuery2ModifierNodeProcessor;
import org.apache.lucene.queryparser.flexible.core.config.QueryConfigHandler;
import org.apache.lucene.queryparser.flexible.standard.processors.StandardQueryNodeProcessorPipeline;

public class PrecedenceQueryNodeProcessorPipeline extends StandardQueryNodeProcessorPipeline
{
    public PrecedenceQueryNodeProcessorPipeline(final QueryConfigHandler queryConfig) {
        super(queryConfig);
        for (int i = 0; i < this.size(); ++i) {
            if (this.get(i).getClass().equals(BooleanQuery2ModifierNodeProcessor.class)) {
                this.remove(i--);
            }
        }
        this.add((QueryNodeProcessor)new BooleanModifiersQueryNodeProcessor());
    }
}
