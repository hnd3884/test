package com.sun.org.apache.xpath.internal.patterns;

import com.sun.org.apache.xpath.internal.ExpressionNode;
import com.sun.org.apache.xpath.internal.ExpressionOwner;
import com.sun.org.apache.xpath.internal.XPathVisitor;
import com.sun.org.apache.xml.internal.dtm.DTM;
import javax.xml.transform.TransformerException;
import com.sun.org.apache.xpath.internal.objects.XNumber;
import com.sun.org.apache.xml.internal.dtm.DTMIterator;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.XPathContext;
import java.util.Vector;
import com.sun.org.apache.xpath.internal.Expression;

public class FunctionPattern extends StepPattern
{
    static final long serialVersionUID = -5426793413091209944L;
    Expression m_functionExpr;
    
    public FunctionPattern(final Expression expr, final int axis, final int predaxis) {
        super(0, null, null, axis, predaxis);
        this.m_functionExpr = expr;
    }
    
    @Override
    public final void calcScore() {
        this.m_score = FunctionPattern.SCORE_OTHER;
        if (null == this.m_targetString) {
            this.calcTargetString();
        }
    }
    
    @Override
    public void fixupVariables(final Vector vars, final int globalsSize) {
        super.fixupVariables(vars, globalsSize);
        this.m_functionExpr.fixupVariables(vars, globalsSize);
    }
    
    @Override
    public XObject execute(final XPathContext xctxt, int context) throws TransformerException {
        final DTMIterator nl = this.m_functionExpr.asIterator(xctxt, context);
        XNumber score = FunctionPattern.SCORE_NONE;
        if (null != nl) {
            int n;
            while (-1 != (n = nl.nextNode())) {
                score = ((n == context) ? FunctionPattern.SCORE_OTHER : FunctionPattern.SCORE_NONE);
                if (score == FunctionPattern.SCORE_OTHER) {
                    context = n;
                    break;
                }
            }
        }
        nl.detach();
        return score;
    }
    
    @Override
    public XObject execute(final XPathContext xctxt, int context, final DTM dtm, final int expType) throws TransformerException {
        final DTMIterator nl = this.m_functionExpr.asIterator(xctxt, context);
        XNumber score = FunctionPattern.SCORE_NONE;
        if (null != nl) {
            int n;
            while (-1 != (n = nl.nextNode())) {
                score = ((n == context) ? FunctionPattern.SCORE_OTHER : FunctionPattern.SCORE_NONE);
                if (score == FunctionPattern.SCORE_OTHER) {
                    context = n;
                    break;
                }
            }
            nl.detach();
        }
        return score;
    }
    
    @Override
    public XObject execute(final XPathContext xctxt) throws TransformerException {
        int context = xctxt.getCurrentNode();
        final DTMIterator nl = this.m_functionExpr.asIterator(xctxt, context);
        XNumber score = FunctionPattern.SCORE_NONE;
        if (null != nl) {
            int n;
            while (-1 != (n = nl.nextNode())) {
                score = ((n == context) ? FunctionPattern.SCORE_OTHER : FunctionPattern.SCORE_NONE);
                if (score == FunctionPattern.SCORE_OTHER) {
                    context = n;
                    break;
                }
            }
            nl.detach();
        }
        return score;
    }
    
    @Override
    protected void callSubtreeVisitors(final XPathVisitor visitor) {
        this.m_functionExpr.callVisitors(new FunctionOwner(), visitor);
        super.callSubtreeVisitors(visitor);
    }
    
    class FunctionOwner implements ExpressionOwner
    {
        @Override
        public Expression getExpression() {
            return FunctionPattern.this.m_functionExpr;
        }
        
        @Override
        public void setExpression(final Expression exp) {
            exp.exprSetParent(FunctionPattern.this);
            FunctionPattern.this.m_functionExpr = exp;
        }
    }
}
