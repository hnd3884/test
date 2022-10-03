package com.sun.org.apache.xpath.internal.axes;

import com.sun.org.apache.xpath.internal.ExpressionNode;
import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.XPathVisitor;
import com.sun.org.apache.xpath.internal.VariableStack;
import java.util.Vector;
import com.sun.org.apache.xml.internal.utils.PrefixResolver;
import javax.xml.transform.TransformerException;
import com.sun.org.apache.xpath.internal.compiler.OpMap;
import com.sun.org.apache.xpath.internal.compiler.Compiler;
import com.sun.org.apache.xpath.internal.ExpressionOwner;

public class WalkingIterator extends LocPathIterator implements ExpressionOwner
{
    static final long serialVersionUID = 9110225941815665906L;
    protected AxesWalker m_lastUsedWalker;
    protected AxesWalker m_firstWalker;
    
    WalkingIterator(final Compiler compiler, final int opPos, final int analysis, final boolean shouldLoadWalkers) throws TransformerException {
        super(compiler, opPos, analysis, shouldLoadWalkers);
        final int firstStepPos = OpMap.getFirstChildPos(opPos);
        if (shouldLoadWalkers) {
            this.m_firstWalker = WalkerFactory.loadWalkers(this, compiler, firstStepPos, 0);
            this.m_lastUsedWalker = this.m_firstWalker;
        }
    }
    
    public WalkingIterator(final PrefixResolver nscontext) {
        super(nscontext);
    }
    
    @Override
    public int getAnalysisBits() {
        int bits = 0;
        if (null != this.m_firstWalker) {
            for (AxesWalker walker = this.m_firstWalker; null != walker; walker = walker.getNextWalker()) {
                final int bit = walker.getAnalysisBits();
                bits |= bit;
            }
        }
        return bits;
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        final WalkingIterator clone = (WalkingIterator)super.clone();
        if (null != this.m_firstWalker) {
            clone.m_firstWalker = this.m_firstWalker.cloneDeep(clone, null);
        }
        return clone;
    }
    
    @Override
    public void reset() {
        super.reset();
        if (null != this.m_firstWalker) {
            this.m_lastUsedWalker = this.m_firstWalker;
            this.m_firstWalker.setRoot(this.m_context);
        }
    }
    
    @Override
    public void setRoot(final int context, final Object environment) {
        super.setRoot(context, environment);
        if (null != this.m_firstWalker) {
            this.m_firstWalker.setRoot(context);
            this.m_lastUsedWalker = this.m_firstWalker;
        }
    }
    
    @Override
    public int nextNode() {
        if (this.m_foundLast) {
            return -1;
        }
        if (-1 == this.m_stackFrame) {
            return this.returnNextNode(this.m_firstWalker.nextNode());
        }
        final VariableStack vars = this.m_execContext.getVarStack();
        final int savedStart = vars.getStackFrame();
        vars.setStackFrame(this.m_stackFrame);
        final int n = this.returnNextNode(this.m_firstWalker.nextNode());
        vars.setStackFrame(savedStart);
        return n;
    }
    
    public final AxesWalker getFirstWalker() {
        return this.m_firstWalker;
    }
    
    public final void setFirstWalker(final AxesWalker walker) {
        this.m_firstWalker = walker;
    }
    
    public final void setLastUsedWalker(final AxesWalker walker) {
        this.m_lastUsedWalker = walker;
    }
    
    public final AxesWalker getLastUsedWalker() {
        return this.m_lastUsedWalker;
    }
    
    @Override
    public void detach() {
        if (this.m_allowDetach) {
            for (AxesWalker walker = this.m_firstWalker; null != walker; walker = walker.getNextWalker()) {
                walker.detach();
            }
            this.m_lastUsedWalker = null;
            super.detach();
        }
    }
    
    @Override
    public void fixupVariables(final Vector vars, final int globalsSize) {
        this.m_predicateIndex = -1;
        for (AxesWalker walker = this.m_firstWalker; null != walker; walker = walker.getNextWalker()) {
            walker.fixupVariables(vars, globalsSize);
        }
    }
    
    @Override
    public void callVisitors(final ExpressionOwner owner, final XPathVisitor visitor) {
        if (visitor.visitLocationPath(owner, this) && null != this.m_firstWalker) {
            this.m_firstWalker.callVisitors(this, visitor);
        }
    }
    
    @Override
    public Expression getExpression() {
        return this.m_firstWalker;
    }
    
    @Override
    public void setExpression(final Expression exp) {
        exp.exprSetParent(this);
        this.m_firstWalker = (AxesWalker)exp;
    }
    
    @Override
    public boolean deepEquals(final Expression expr) {
        if (!super.deepEquals(expr)) {
            return false;
        }
        AxesWalker walker1;
        AxesWalker walker2;
        for (walker1 = this.m_firstWalker, walker2 = ((WalkingIterator)expr).m_firstWalker; null != walker1 && null != walker2; walker1 = walker1.getNextWalker(), walker2 = walker2.getNextWalker()) {
            if (!walker1.deepEquals(walker2)) {
                return false;
            }
        }
        return null == walker1 && null == walker2;
    }
}
