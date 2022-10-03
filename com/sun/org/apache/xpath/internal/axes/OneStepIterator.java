package com.sun.org.apache.xpath.internal.axes;

import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xml.internal.dtm.DTMIterator;
import com.sun.org.apache.xml.internal.dtm.DTMAxisTraverser;
import javax.xml.transform.TransformerException;
import com.sun.org.apache.xpath.internal.compiler.OpMap;
import com.sun.org.apache.xpath.internal.compiler.Compiler;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;

public class OneStepIterator extends ChildTestIterator
{
    static final long serialVersionUID = 4623710779664998283L;
    protected int m_axis;
    protected DTMAxisIterator m_iterator;
    
    OneStepIterator(final Compiler compiler, final int opPos, final int analysis) throws TransformerException {
        super(compiler, opPos, analysis);
        this.m_axis = -1;
        final int firstStepPos = OpMap.getFirstChildPos(opPos);
        this.m_axis = WalkerFactory.getAxisFromStep(compiler, firstStepPos);
    }
    
    public OneStepIterator(final DTMAxisIterator iterator, final int axis) throws TransformerException {
        super((DTMAxisTraverser)null);
        this.m_axis = -1;
        this.m_iterator = iterator;
        this.m_axis = axis;
        final int whatToShow = -1;
        this.initNodeTest(whatToShow);
    }
    
    @Override
    public void setRoot(final int context, final Object environment) {
        super.setRoot(context, environment);
        if (this.m_axis > -1) {
            this.m_iterator = this.m_cdtm.getAxisIterator(this.m_axis);
        }
        this.m_iterator.setStartNode(this.m_context);
    }
    
    @Override
    public void detach() {
        if (this.m_allowDetach) {
            if (this.m_axis > -1) {
                this.m_iterator = null;
            }
            super.detach();
        }
    }
    
    @Override
    protected int getNextNode() {
        return this.m_lastFetched = this.m_iterator.next();
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        final OneStepIterator clone = (OneStepIterator)super.clone();
        if (this.m_iterator != null) {
            clone.m_iterator = this.m_iterator.cloneIterator();
        }
        return clone;
    }
    
    @Override
    public DTMIterator cloneWithReset() throws CloneNotSupportedException {
        final OneStepIterator clone = (OneStepIterator)super.cloneWithReset();
        clone.m_iterator = this.m_iterator;
        return clone;
    }
    
    @Override
    public boolean isReverseAxes() {
        return this.m_iterator.isReverse();
    }
    
    @Override
    protected int getProximityPosition(final int predicateIndex) {
        if (!this.isReverseAxes()) {
            return super.getProximityPosition(predicateIndex);
        }
        if (predicateIndex < 0) {
            return -1;
        }
        if (this.m_proximityPositions[predicateIndex] <= 0) {
            final XPathContext xctxt = this.getXPathContext();
            try {
                final OneStepIterator clone = (OneStepIterator)this.clone();
                final int root = this.getRoot();
                xctxt.pushCurrentNode(root);
                clone.setRoot(root, xctxt);
                clone.m_predCount = predicateIndex;
                int count = 1;
                int next;
                while (-1 != (next = clone.nextNode())) {
                    ++count;
                }
                final int[] proximityPositions = this.m_proximityPositions;
                proximityPositions[predicateIndex] += count;
            }
            catch (final CloneNotSupportedException ex) {}
            finally {
                xctxt.popCurrentNode();
            }
        }
        return this.m_proximityPositions[predicateIndex];
    }
    
    @Override
    public int getLength() {
        if (!this.isReverseAxes()) {
            return super.getLength();
        }
        final boolean isPredicateTest = this == this.m_execContext.getSubContextList();
        final int predCount = this.getPredicateCount();
        if (-1 != this.m_length && isPredicateTest && this.m_predicateIndex < 1) {
            return this.m_length;
        }
        int count = 0;
        final XPathContext xctxt = this.getXPathContext();
        try {
            final OneStepIterator clone = (OneStepIterator)this.cloneWithReset();
            final int root = this.getRoot();
            xctxt.pushCurrentNode(root);
            clone.setRoot(root, xctxt);
            clone.m_predCount = this.m_predicateIndex;
            int next;
            while (-1 != (next = clone.nextNode())) {
                ++count;
            }
        }
        catch (final CloneNotSupportedException ex) {}
        finally {
            xctxt.popCurrentNode();
        }
        if (isPredicateTest && this.m_predicateIndex < 1) {
            this.m_length = count;
        }
        return count;
    }
    
    @Override
    protected void countProximityPosition(final int i) {
        if (!this.isReverseAxes()) {
            super.countProximityPosition(i);
        }
        else if (i < this.m_proximityPositions.length) {
            final int[] proximityPositions = this.m_proximityPositions;
            --proximityPositions[i];
        }
    }
    
    @Override
    public void reset() {
        super.reset();
        if (null != this.m_iterator) {
            this.m_iterator.reset();
        }
    }
    
    @Override
    public int getAxis() {
        return this.m_axis;
    }
    
    @Override
    public boolean deepEquals(final Expression expr) {
        return super.deepEquals(expr) && this.m_axis == ((OneStepIterator)expr).m_axis;
    }
}
