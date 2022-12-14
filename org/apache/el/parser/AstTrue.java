package org.apache.el.parser;

import javax.el.ELException;
import org.apache.el.lang.EvaluationContext;

public final class AstTrue extends BooleanNode
{
    public AstTrue(final int id) {
        super(id);
    }
    
    @Override
    public Object getValue(final EvaluationContext ctx) throws ELException {
        return Boolean.TRUE;
    }
}
