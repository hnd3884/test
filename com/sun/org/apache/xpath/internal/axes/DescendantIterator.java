package com.sun.org.apache.xpath.internal.axes;

import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.patterns.NodeTest;
import com.sun.org.apache.xpath.internal.VariableStack;
import com.sun.org.apache.xml.internal.dtm.DTMIterator;
import com.sun.org.apache.xml.internal.utils.PrefixResolver;
import javax.xml.transform.TransformerException;
import com.sun.org.apache.xpath.internal.compiler.OpMap;
import com.sun.org.apache.xpath.internal.compiler.Compiler;
import com.sun.org.apache.xml.internal.dtm.DTMAxisTraverser;

public class DescendantIterator extends LocPathIterator
{
    static final long serialVersionUID = -1190338607743976938L;
    protected transient DTMAxisTraverser m_traverser;
    protected int m_axis;
    protected int m_extendedTypeID;
    
    DescendantIterator(final Compiler compiler, final int opPos, final int analysis) throws TransformerException {
        super(compiler, opPos, analysis, false);
        int firstStepPos = OpMap.getFirstChildPos(opPos);
        final int stepType = compiler.getOp(firstStepPos);
        boolean orSelf = 42 == stepType;
        boolean fromRoot = false;
        if (48 == stepType) {
            orSelf = true;
        }
        else if (50 == stepType) {
            fromRoot = true;
            final int nextStepPos = compiler.getNextStepPos(firstStepPos);
            if (compiler.getOp(nextStepPos) == 42) {
                orSelf = true;
            }
        }
        int nextStepPos = firstStepPos;
        while (true) {
            nextStepPos = compiler.getNextStepPos(nextStepPos);
            if (nextStepPos <= 0) {
                break;
            }
            final int stepOp = compiler.getOp(nextStepPos);
            if (-1 == stepOp) {
                break;
            }
            firstStepPos = nextStepPos;
        }
        if ((analysis & 0x10000) != 0x0) {
            orSelf = false;
        }
        if (fromRoot) {
            if (orSelf) {
                this.m_axis = 18;
            }
            else {
                this.m_axis = 17;
            }
        }
        else if (orSelf) {
            this.m_axis = 5;
        }
        else {
            this.m_axis = 4;
        }
        final int whatToShow = compiler.getWhatToShow(firstStepPos);
        if (0x0 == (whatToShow & 0x43) || whatToShow == -1) {
            this.initNodeTest(whatToShow);
        }
        else {
            this.initNodeTest(whatToShow, compiler.getStepNS(firstStepPos), compiler.getStepLocalName(firstStepPos));
        }
        this.initPredicateInfo(compiler, firstStepPos);
    }
    
    public DescendantIterator() {
        super((PrefixResolver)null);
        this.m_axis = 18;
        final int whatToShow = -1;
        this.initNodeTest(whatToShow);
    }
    
    @Override
    public DTMIterator cloneWithReset() throws CloneNotSupportedException {
        final DescendantIterator clone = (DescendantIterator)super.cloneWithReset();
        clone.m_traverser = this.m_traverser;
        clone.resetProximityPositions();
        return clone;
    }
    
    @Override
    public int nextNode() {
        if (this.m_foundLast) {
            return -1;
        }
        if (-1 == this.m_lastFetched) {
            this.resetProximityPositions();
        }
        Label_0057: {
            if (-1 != this.m_stackFrame) {
                final VariableStack vars = this.m_execContext.getVarStack();
                final int savedStart = vars.getStackFrame();
                vars.setStackFrame(this.m_stackFrame);
                break Label_0057;
            }
            final VariableStack vars = null;
            final int savedStart = 0;
            try {
                int next;
                do {
                    if (0 == this.m_extendedTypeID) {
                        final int lastFetched = (-1 == this.m_lastFetched) ? this.m_traverser.first(this.m_context) : this.m_traverser.next(this.m_context, this.m_lastFetched);
                        this.m_lastFetched = lastFetched;
                        next = lastFetched;
                    }
                    else {
                        final int lastFetched2 = (-1 == this.m_lastFetched) ? this.m_traverser.first(this.m_context, this.m_extendedTypeID) : this.m_traverser.next(this.m_context, this.m_lastFetched, this.m_extendedTypeID);
                        this.m_lastFetched = lastFetched2;
                        next = lastFetched2;
                    }
                    if (-1 == next) {
                        break;
                    }
                    if (1 == this.acceptNode(next)) {
                        break;
                    }
                } while (next != -1);
                if (-1 != next) {
                    ++this.m_pos;
                    return next;
                }
                this.m_foundLast = true;
                return -1;
            }
            finally {
                if (-1 != this.m_stackFrame) {
                    vars.setStackFrame(savedStart);
                }
            }
        }
    }
    
    @Override
    public void setRoot(final int context, final Object environment) {
        super.setRoot(context, environment);
        this.m_traverser = this.m_cdtm.getAxisTraverser(this.m_axis);
        final String localName = this.getLocalName();
        final String namespace = this.getNamespace();
        final int what = this.m_whatToShow;
        if (-1 == what || "*".equals(localName) || "*".equals(namespace)) {
            this.m_extendedTypeID = 0;
        }
        else {
            final int type = NodeTest.getNodeTypeTest(what);
            this.m_extendedTypeID = this.m_cdtm.getExpandedTypeID(namespace, localName, type);
        }
    }
    
    @Override
    public int asNode(final XPathContext xctxt) throws TransformerException {
        if (this.getPredicateCount() > 0) {
            return super.asNode(xctxt);
        }
        final int current = xctxt.getCurrentNode();
        final DTM dtm = xctxt.getDTM(current);
        final DTMAxisTraverser traverser = dtm.getAxisTraverser(this.m_axis);
        final String localName = this.getLocalName();
        final String namespace = this.getNamespace();
        final int what = this.m_whatToShow;
        if (-1 == what || localName == "*" || namespace == "*") {
            return traverser.first(current);
        }
        final int type = NodeTest.getNodeTypeTest(what);
        final int extendedType = dtm.getExpandedTypeID(namespace, localName, type);
        return traverser.first(current, extendedType);
    }
    
    @Override
    public void detach() {
        if (this.m_allowDetach) {
            this.m_traverser = null;
            this.m_extendedTypeID = 0;
            super.detach();
        }
    }
    
    @Override
    public int getAxis() {
        return this.m_axis;
    }
    
    @Override
    public boolean deepEquals(final Expression expr) {
        return super.deepEquals(expr) && this.m_axis == ((DescendantIterator)expr).m_axis;
    }
}
