package org.apache.el.parser;

import javax.el.ELException;
import org.apache.el.lang.EvaluationContext;

public final class AstBracketSuffix extends SimpleNode
{
    public AstBracketSuffix(final int id) {
        super(id);
    }
    
    @Override
    public Object getValue(final EvaluationContext ctx) throws ELException {
        return this.children[0].getValue(ctx);
    }
}
