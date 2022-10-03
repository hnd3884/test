package com.sun.org.apache.xpath.internal.axes;

import java.util.Vector;
import javax.xml.transform.TransformerException;
import com.sun.org.apache.xpath.internal.compiler.Compiler;
import com.sun.org.apache.xml.internal.utils.PrefixResolver;

public class WalkingIteratorSorted extends WalkingIterator
{
    static final long serialVersionUID = -4512512007542368213L;
    protected boolean m_inNaturalOrderStatic;
    
    public WalkingIteratorSorted(final PrefixResolver nscontext) {
        super(nscontext);
        this.m_inNaturalOrderStatic = false;
    }
    
    WalkingIteratorSorted(final Compiler compiler, final int opPos, final int analysis, final boolean shouldLoadWalkers) throws TransformerException {
        super(compiler, opPos, analysis, shouldLoadWalkers);
        this.m_inNaturalOrderStatic = false;
    }
    
    @Override
    public boolean isDocOrdered() {
        return this.m_inNaturalOrderStatic;
    }
    
    boolean canBeWalkedInNaturalDocOrderStatic() {
        if (null != this.m_firstWalker) {
            AxesWalker walker = this.m_firstWalker;
            final int prevAxis = -1;
            final boolean prevIsSimpleDownAxis = true;
            for (int i = 0; null != walker; walker = walker.getNextWalker(), ++i) {
                final int axis = walker.getAxis();
                if (!walker.isDocOrdered()) {
                    return false;
                }
                final boolean isSimpleDownAxis = axis == 3 || axis == 13 || axis == 19;
                if (!isSimpleDownAxis && axis != -1) {
                    final boolean isLastWalker = null == walker.getNextWalker();
                    return isLastWalker && ((walker.isDocOrdered() && (axis == 4 || axis == 5 || axis == 17 || axis == 18)) || axis == 2);
                }
            }
            return true;
        }
        return false;
    }
    
    @Override
    public void fixupVariables(final Vector vars, final int globalsSize) {
        super.fixupVariables(vars, globalsSize);
        final int analysis = this.getAnalysisBits();
        if (WalkerFactory.isNaturalDocOrder(analysis)) {
            this.m_inNaturalOrderStatic = true;
        }
        else {
            this.m_inNaturalOrderStatic = false;
        }
    }
}
