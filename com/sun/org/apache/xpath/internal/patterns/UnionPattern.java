package com.sun.org.apache.xpath.internal.patterns;

import com.sun.org.apache.xpath.internal.XPathVisitor;
import com.sun.org.apache.xpath.internal.ExpressionOwner;
import javax.xml.transform.TransformerException;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.ExpressionNode;
import java.util.Vector;
import com.sun.org.apache.xpath.internal.Expression;

public class UnionPattern extends Expression
{
    static final long serialVersionUID = -6670449967116905820L;
    private StepPattern[] m_patterns;
    
    @Override
    public void fixupVariables(final Vector vars, final int globalsSize) {
        for (int i = 0; i < this.m_patterns.length; ++i) {
            this.m_patterns[i].fixupVariables(vars, globalsSize);
        }
    }
    
    @Override
    public boolean canTraverseOutsideSubtree() {
        if (null != this.m_patterns) {
            for (int n = this.m_patterns.length, i = 0; i < n; ++i) {
                if (this.m_patterns[i].canTraverseOutsideSubtree()) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public void setPatterns(final StepPattern[] patterns) {
        this.m_patterns = patterns;
        if (null != patterns) {
            for (int i = 0; i < patterns.length; ++i) {
                patterns[i].exprSetParent(this);
            }
        }
    }
    
    public StepPattern[] getPatterns() {
        return this.m_patterns;
    }
    
    @Override
    public XObject execute(final XPathContext xctxt) throws TransformerException {
        XObject bestScore = null;
        for (int n = this.m_patterns.length, i = 0; i < n; ++i) {
            final XObject score = this.m_patterns[i].execute(xctxt);
            if (score != NodeTest.SCORE_NONE) {
                if (null == bestScore) {
                    bestScore = score;
                }
                else if (score.num() > bestScore.num()) {
                    bestScore = score;
                }
            }
        }
        if (null == bestScore) {
            bestScore = NodeTest.SCORE_NONE;
        }
        return bestScore;
    }
    
    @Override
    public void callVisitors(final ExpressionOwner owner, final XPathVisitor visitor) {
        visitor.visitUnionPattern(owner, this);
        if (null != this.m_patterns) {
            for (int n = this.m_patterns.length, i = 0; i < n; ++i) {
                this.m_patterns[i].callVisitors(new UnionPathPartOwner(i), visitor);
            }
        }
    }
    
    @Override
    public boolean deepEquals(final Expression expr) {
        if (!this.isSameClass(expr)) {
            return false;
        }
        final UnionPattern up = (UnionPattern)expr;
        if (null != this.m_patterns) {
            final int n = this.m_patterns.length;
            if (null == up.m_patterns || up.m_patterns.length != n) {
                return false;
            }
            for (int i = 0; i < n; ++i) {
                if (!this.m_patterns[i].deepEquals(up.m_patterns[i])) {
                    return false;
                }
            }
        }
        else if (up.m_patterns != null) {
            return false;
        }
        return true;
    }
    
    class UnionPathPartOwner implements ExpressionOwner
    {
        int m_index;
        
        UnionPathPartOwner(final int index) {
            this.m_index = index;
        }
        
        @Override
        public Expression getExpression() {
            return UnionPattern.this.m_patterns[this.m_index];
        }
        
        @Override
        public void setExpression(final Expression exp) {
            exp.exprSetParent(UnionPattern.this);
            UnionPattern.this.m_patterns[this.m_index] = (StepPattern)exp;
        }
    }
}
