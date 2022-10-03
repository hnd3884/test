package com.sun.org.apache.xpath.internal.axes;

import com.sun.org.apache.xml.internal.dtm.DTMIterator;
import com.sun.org.apache.xpath.internal.VariableStack;
import javax.xml.transform.TransformerException;
import com.sun.org.apache.xpath.internal.compiler.OpMap;
import com.sun.org.apache.xpath.internal.compiler.Compiler;
import com.sun.org.apache.xml.internal.utils.PrefixResolver;

public abstract class BasicTestIterator extends LocPathIterator
{
    static final long serialVersionUID = 3505378079378096623L;
    
    protected BasicTestIterator() {
    }
    
    protected BasicTestIterator(final PrefixResolver nscontext) {
        super(nscontext);
    }
    
    protected BasicTestIterator(final Compiler compiler, final int opPos, final int analysis) throws TransformerException {
        super(compiler, opPos, analysis, false);
        final int firstStepPos = OpMap.getFirstChildPos(opPos);
        final int whatToShow = compiler.getWhatToShow(firstStepPos);
        if (0x0 == (whatToShow & 0x1043) || whatToShow == -1) {
            this.initNodeTest(whatToShow);
        }
        else {
            this.initNodeTest(whatToShow, compiler.getStepNS(firstStepPos), compiler.getStepLocalName(firstStepPos));
        }
        this.initPredicateInfo(compiler, firstStepPos);
    }
    
    protected BasicTestIterator(final Compiler compiler, final int opPos, final int analysis, final boolean shouldLoadWalkers) throws TransformerException {
        super(compiler, opPos, analysis, shouldLoadWalkers);
    }
    
    protected abstract int getNextNode();
    
    @Override
    public int nextNode() {
        if (this.m_foundLast) {
            return this.m_lastFetched = -1;
        }
        if (-1 == this.m_lastFetched) {
            this.resetProximityPositions();
        }
        Label_0062: {
            if (-1 != this.m_stackFrame) {
                final VariableStack vars = this.m_execContext.getVarStack();
                final int savedStart = vars.getStackFrame();
                vars.setStackFrame(this.m_stackFrame);
                break Label_0062;
            }
            final VariableStack vars = null;
            final int savedStart = 0;
            try {
                int next;
                do {
                    next = this.getNextNode();
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
    public DTMIterator cloneWithReset() throws CloneNotSupportedException {
        final ChildTestIterator clone = (ChildTestIterator)super.cloneWithReset();
        clone.resetProximityPositions();
        return clone;
    }
}
