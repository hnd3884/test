package com.sun.org.apache.xpath.internal.axes;

import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xml.internal.dtm.DTMAxisTraverser;
import javax.xml.transform.TransformerException;
import com.sun.org.apache.xpath.internal.compiler.OpMap;
import com.sun.org.apache.xpath.internal.compiler.Compiler;

public class OneStepIteratorForward extends ChildTestIterator
{
    static final long serialVersionUID = -1576936606178190566L;
    protected int m_axis;
    
    OneStepIteratorForward(final Compiler compiler, final int opPos, final int analysis) throws TransformerException {
        super(compiler, opPos, analysis);
        this.m_axis = -1;
        final int firstStepPos = OpMap.getFirstChildPos(opPos);
        this.m_axis = WalkerFactory.getAxisFromStep(compiler, firstStepPos);
    }
    
    public OneStepIteratorForward(final int axis) {
        super((DTMAxisTraverser)null);
        this.m_axis = -1;
        this.m_axis = axis;
        final int whatToShow = -1;
        this.initNodeTest(whatToShow);
    }
    
    @Override
    public void setRoot(final int context, final Object environment) {
        super.setRoot(context, environment);
        this.m_traverser = this.m_cdtm.getAxisTraverser(this.m_axis);
    }
    
    @Override
    protected int getNextNode() {
        return this.m_lastFetched = ((-1 == this.m_lastFetched) ? this.m_traverser.first(this.m_context) : this.m_traverser.next(this.m_context, this.m_lastFetched));
    }
    
    @Override
    public int getAxis() {
        return this.m_axis;
    }
    
    @Override
    public boolean deepEquals(final Expression expr) {
        return super.deepEquals(expr) && this.m_axis == ((OneStepIteratorForward)expr).m_axis;
    }
}
