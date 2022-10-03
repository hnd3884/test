package com.sun.org.apache.xpath.internal.axes;

import com.sun.org.apache.xpath.internal.ExpressionOwner;
import com.sun.org.apache.xpath.internal.XPathVisitor;
import com.sun.org.apache.xml.internal.dtm.DTM;
import java.util.Vector;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xml.internal.utils.PrefixResolver;
import com.sun.org.apache.xml.internal.utils.WrappedRuntimeException;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.ExpressionNode;
import com.sun.org.apache.xpath.internal.compiler.Compiler;
import java.io.IOException;
import javax.xml.transform.TransformerException;
import java.io.ObjectInputStream;
import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.patterns.NodeTest;

public abstract class PredicatedNodeTest extends NodeTest implements SubContextList
{
    static final long serialVersionUID = -6193530757296377351L;
    protected int m_predCount;
    protected transient boolean m_foundLast;
    protected LocPathIterator m_lpi;
    transient int m_predicateIndex;
    private Expression[] m_predicates;
    protected transient int[] m_proximityPositions;
    static final boolean DEBUG_PREDICATECOUNTING = false;
    
    PredicatedNodeTest(final LocPathIterator locPathIterator) {
        this.m_predCount = -1;
        this.m_foundLast = false;
        this.m_predicateIndex = -1;
        this.m_lpi = locPathIterator;
    }
    
    PredicatedNodeTest() {
        this.m_predCount = -1;
        this.m_foundLast = false;
        this.m_predicateIndex = -1;
    }
    
    private void readObject(final ObjectInputStream stream) throws IOException, TransformerException {
        try {
            stream.defaultReadObject();
            this.m_predicateIndex = -1;
            this.m_predCount = -1;
            this.resetProximityPositions();
        }
        catch (final ClassNotFoundException cnfe) {
            throw new TransformerException(cnfe);
        }
    }
    
    public Object clone() throws CloneNotSupportedException {
        final PredicatedNodeTest clone = (PredicatedNodeTest)super.clone();
        if (null != this.m_proximityPositions && this.m_proximityPositions == clone.m_proximityPositions) {
            clone.m_proximityPositions = new int[this.m_proximityPositions.length];
            System.arraycopy(this.m_proximityPositions, 0, clone.m_proximityPositions, 0, this.m_proximityPositions.length);
        }
        if (clone.m_lpi == this) {
            clone.m_lpi = (LocPathIterator)clone;
        }
        return clone;
    }
    
    public int getPredicateCount() {
        if (-1 == this.m_predCount) {
            return (null == this.m_predicates) ? 0 : this.m_predicates.length;
        }
        return this.m_predCount;
    }
    
    public void setPredicateCount(final int count) {
        if (count > 0) {
            final Expression[] newPredicates = new Expression[count];
            for (int i = 0; i < count; ++i) {
                newPredicates[i] = this.m_predicates[i];
            }
            this.m_predicates = newPredicates;
        }
        else {
            this.m_predicates = null;
        }
    }
    
    protected void initPredicateInfo(final Compiler compiler, final int opPos) throws TransformerException {
        final int pos = compiler.getFirstPredicateOpPos(opPos);
        if (pos > 0) {
            this.m_predicates = compiler.getCompiledPredicates(pos);
            if (null != this.m_predicates) {
                for (int i = 0; i < this.m_predicates.length; ++i) {
                    this.m_predicates[i].exprSetParent(this);
                }
            }
        }
    }
    
    public Expression getPredicate(final int index) {
        return this.m_predicates[index];
    }
    
    public int getProximityPosition() {
        return this.getProximityPosition(this.m_predicateIndex);
    }
    
    @Override
    public int getProximityPosition(final XPathContext xctxt) {
        return this.getProximityPosition();
    }
    
    @Override
    public abstract int getLastPos(final XPathContext p0);
    
    protected int getProximityPosition(final int predicateIndex) {
        return (predicateIndex >= 0) ? this.m_proximityPositions[predicateIndex] : 0;
    }
    
    public void resetProximityPositions() {
        final int nPredicates = this.getPredicateCount();
        if (nPredicates > 0) {
            if (null == this.m_proximityPositions) {
                this.m_proximityPositions = new int[nPredicates];
            }
            for (int i = 0; i < nPredicates; ++i) {
                try {
                    this.initProximityPosition(i);
                }
                catch (final Exception e) {
                    throw new WrappedRuntimeException(e);
                }
            }
        }
    }
    
    public void initProximityPosition(final int i) throws TransformerException {
        this.m_proximityPositions[i] = 0;
    }
    
    protected void countProximityPosition(final int i) {
        final int[] pp = this.m_proximityPositions;
        if (null != pp && i < pp.length) {
            final int[] array = pp;
            ++array[i];
        }
    }
    
    public boolean isReverseAxes() {
        return false;
    }
    
    public int getPredicateIndex() {
        return this.m_predicateIndex;
    }
    
    boolean executePredicates(final int context, final XPathContext xctxt) throws TransformerException {
        final int nPredicates = this.getPredicateCount();
        if (nPredicates == 0) {
            return true;
        }
        final PrefixResolver savedResolver = xctxt.getNamespaceContext();
        try {
            this.m_predicateIndex = 0;
            xctxt.pushSubContextList(this);
            xctxt.pushNamespaceContext(this.m_lpi.getPrefixResolver());
            xctxt.pushCurrentNode(context);
            for (int i = 0; i < nPredicates; ++i) {
                final XObject pred = this.m_predicates[i].execute(xctxt);
                if (2 == pred.getType()) {
                    final int proxPos = this.getProximityPosition(this.m_predicateIndex);
                    final int predIndex = (int)pred.num();
                    if (proxPos != predIndex) {
                        return false;
                    }
                    if (this.m_predicates[i].isStableNumber() && i == nPredicates - 1) {
                        this.m_foundLast = true;
                    }
                }
                else if (!pred.bool()) {
                    return false;
                }
                this.countProximityPosition(++this.m_predicateIndex);
            }
        }
        finally {
            xctxt.popCurrentNode();
            xctxt.popNamespaceContext();
            xctxt.popSubContextList();
            this.m_predicateIndex = -1;
        }
        return true;
    }
    
    @Override
    public void fixupVariables(final Vector vars, final int globalsSize) {
        super.fixupVariables(vars, globalsSize);
        for (int nPredicates = this.getPredicateCount(), i = 0; i < nPredicates; ++i) {
            this.m_predicates[i].fixupVariables(vars, globalsSize);
        }
    }
    
    protected String nodeToString(final int n) {
        if (-1 != n) {
            final DTM dtm = this.m_lpi.getXPathContext().getDTM(n);
            return dtm.getNodeName(n) + "{" + (n + 1) + "}";
        }
        return "null";
    }
    
    public short acceptNode(final int n) {
        final XPathContext xctxt = this.m_lpi.getXPathContext();
        try {
            xctxt.pushCurrentNode(n);
            final XObject score = this.execute(xctxt, n);
            if (score != NodeTest.SCORE_NONE) {
                if (this.getPredicateCount() > 0) {
                    this.countProximityPosition(0);
                    if (!this.executePredicates(n, xctxt)) {
                        return 3;
                    }
                }
                return 1;
            }
        }
        catch (final TransformerException se) {
            throw new RuntimeException(se.getMessage());
        }
        finally {
            xctxt.popCurrentNode();
        }
        return 3;
    }
    
    public LocPathIterator getLocPathIterator() {
        return this.m_lpi;
    }
    
    public void setLocPathIterator(final LocPathIterator li) {
        this.m_lpi = li;
        if (this != li) {
            li.exprSetParent(this);
        }
    }
    
    @Override
    public boolean canTraverseOutsideSubtree() {
        for (int n = this.getPredicateCount(), i = 0; i < n; ++i) {
            if (this.getPredicate(i).canTraverseOutsideSubtree()) {
                return true;
            }
        }
        return false;
    }
    
    public void callPredicateVisitors(final XPathVisitor visitor) {
        if (null != this.m_predicates) {
            for (int n = this.m_predicates.length, i = 0; i < n; ++i) {
                final ExpressionOwner predOwner = new PredOwner(i);
                if (visitor.visitPredicate(predOwner, this.m_predicates[i])) {
                    this.m_predicates[i].callVisitors(predOwner, visitor);
                }
            }
        }
    }
    
    @Override
    public boolean deepEquals(final Expression expr) {
        if (!super.deepEquals(expr)) {
            return false;
        }
        final PredicatedNodeTest pnt = (PredicatedNodeTest)expr;
        if (null != this.m_predicates) {
            final int n = this.m_predicates.length;
            if (null == pnt.m_predicates || pnt.m_predicates.length != n) {
                return false;
            }
            for (int i = 0; i < n; ++i) {
                if (!this.m_predicates[i].deepEquals(pnt.m_predicates[i])) {
                    return false;
                }
            }
        }
        else if (null != pnt.m_predicates) {
            return false;
        }
        return true;
    }
    
    class PredOwner implements ExpressionOwner
    {
        int m_index;
        
        PredOwner(final int index) {
            this.m_index = index;
        }
        
        @Override
        public Expression getExpression() {
            return PredicatedNodeTest.this.m_predicates[this.m_index];
        }
        
        @Override
        public void setExpression(final Expression exp) {
            exp.exprSetParent(PredicatedNodeTest.this);
            PredicatedNodeTest.this.m_predicates[this.m_index] = exp;
        }
    }
}
