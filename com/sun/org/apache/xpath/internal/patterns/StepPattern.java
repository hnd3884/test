package com.sun.org.apache.xpath.internal.patterns;

import com.sun.org.apache.xpath.internal.XPathVisitor;
import com.sun.org.apache.xml.internal.dtm.Axis;
import com.sun.org.apache.xml.internal.dtm.DTMAxisTraverser;
import javax.xml.transform.TransformerException;
import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.ExpressionNode;
import java.util.Vector;
import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.ExpressionOwner;
import com.sun.org.apache.xpath.internal.axes.SubContextList;

public class StepPattern extends NodeTest implements SubContextList, ExpressionOwner
{
    static final long serialVersionUID = 9071668960168152644L;
    protected int m_axis;
    String m_targetString;
    StepPattern m_relativePathPattern;
    Expression[] m_predicates;
    private static final boolean DEBUG_MATCHES = false;
    
    public StepPattern(final int whatToShow, final String namespace, final String name, final int axis, final int axisForPredicate) {
        super(whatToShow, namespace, name);
        this.m_axis = axis;
    }
    
    public StepPattern(final int whatToShow, final int axis, final int axisForPredicate) {
        super(whatToShow);
        this.m_axis = axis;
    }
    
    public void calcTargetString() {
        final int whatToShow = this.getWhatToShow();
        switch (whatToShow) {
            case 128: {
                this.m_targetString = "#comment";
                break;
            }
            case 4:
            case 8:
            case 12: {
                this.m_targetString = "#text";
                break;
            }
            case -1: {
                this.m_targetString = "*";
                break;
            }
            case 256:
            case 1280: {
                this.m_targetString = "/";
                break;
            }
            case 1: {
                if ("*" == this.m_name) {
                    this.m_targetString = "*";
                    break;
                }
                this.m_targetString = this.m_name;
                break;
            }
            default: {
                this.m_targetString = "*";
                break;
            }
        }
    }
    
    public String getTargetString() {
        return this.m_targetString;
    }
    
    @Override
    public void fixupVariables(final Vector vars, final int globalsSize) {
        super.fixupVariables(vars, globalsSize);
        if (null != this.m_predicates) {
            for (int i = 0; i < this.m_predicates.length; ++i) {
                this.m_predicates[i].fixupVariables(vars, globalsSize);
            }
        }
        if (null != this.m_relativePathPattern) {
            this.m_relativePathPattern.fixupVariables(vars, globalsSize);
        }
    }
    
    public void setRelativePathPattern(final StepPattern expr) {
        (this.m_relativePathPattern = expr).exprSetParent(this);
        this.calcScore();
    }
    
    public StepPattern getRelativePathPattern() {
        return this.m_relativePathPattern;
    }
    
    public Expression[] getPredicates() {
        return this.m_predicates;
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
    
    public Expression getPredicate(final int i) {
        return this.m_predicates[i];
    }
    
    public final int getPredicateCount() {
        return (null == this.m_predicates) ? 0 : this.m_predicates.length;
    }
    
    public void setPredicates(final Expression[] predicates) {
        this.m_predicates = predicates;
        if (null != predicates) {
            for (int i = 0; i < predicates.length; ++i) {
                predicates[i].exprSetParent(this);
            }
        }
        this.calcScore();
    }
    
    public void calcScore() {
        if (this.getPredicateCount() > 0 || null != this.m_relativePathPattern) {
            this.m_score = StepPattern.SCORE_OTHER;
        }
        else {
            super.calcScore();
        }
        if (null == this.m_targetString) {
            this.calcTargetString();
        }
    }
    
    @Override
    public XObject execute(final XPathContext xctxt, final int currentNode) throws TransformerException {
        final DTM dtm = xctxt.getDTM(currentNode);
        if (dtm != null) {
            final int expType = dtm.getExpandedTypeID(currentNode);
            return this.execute(xctxt, currentNode, dtm, expType);
        }
        return NodeTest.SCORE_NONE;
    }
    
    @Override
    public XObject execute(final XPathContext xctxt) throws TransformerException {
        return this.execute(xctxt, xctxt.getCurrentNode());
    }
    
    @Override
    public XObject execute(final XPathContext xctxt, final int currentNode, final DTM dtm, final int expType) throws TransformerException {
        if (this.m_whatToShow == 65536) {
            if (null != this.m_relativePathPattern) {
                return this.m_relativePathPattern.execute(xctxt);
            }
            return NodeTest.SCORE_NONE;
        }
        else {
            final XObject score = super.execute(xctxt, currentNode, dtm, expType);
            if (score == NodeTest.SCORE_NONE) {
                return NodeTest.SCORE_NONE;
            }
            if (this.getPredicateCount() != 0 && !this.executePredicates(xctxt, dtm, currentNode)) {
                return NodeTest.SCORE_NONE;
            }
            if (null != this.m_relativePathPattern) {
                return this.m_relativePathPattern.executeRelativePathPattern(xctxt, dtm, currentNode);
            }
            return score;
        }
    }
    
    private final boolean checkProximityPosition(final XPathContext xctxt, final int predPos, final DTM dtm, final int context, int pos) {
        try {
            final DTMAxisTraverser traverser = dtm.getAxisTraverser(12);
            for (int child = traverser.first(context); -1 != child; child = traverser.next(context, child)) {
                try {
                    xctxt.pushCurrentNode(child);
                    if (NodeTest.SCORE_NONE != super.execute(xctxt, child)) {
                        boolean pass = true;
                        try {
                            xctxt.pushSubContextList(this);
                            for (int i = 0; i < predPos; ++i) {
                                xctxt.pushPredicatePos(i);
                                try {
                                    final XObject pred = this.m_predicates[i].execute(xctxt);
                                    try {
                                        if (2 == pred.getType()) {
                                            throw new Error("Why: Should never have been called");
                                        }
                                        if (!pred.boolWithSideEffects()) {
                                            pass = false;
                                            break;
                                        }
                                    }
                                    finally {
                                        pred.detach();
                                    }
                                }
                                finally {
                                    xctxt.popPredicatePos();
                                }
                            }
                        }
                        finally {
                            xctxt.popSubContextList();
                        }
                        if (pass) {
                            --pos;
                        }
                        if (pos < 1) {
                            return false;
                        }
                    }
                }
                finally {
                    xctxt.popCurrentNode();
                }
            }
        }
        catch (final TransformerException se) {
            throw new RuntimeException(se.getMessage());
        }
        return pos == 1;
    }
    
    private final int getProximityPosition(final XPathContext xctxt, final int predPos, final boolean findLast) {
        int pos = 0;
        final int context = xctxt.getCurrentNode();
        final DTM dtm = xctxt.getDTM(context);
        final int parent = dtm.getParent(context);
        try {
            final DTMAxisTraverser traverser = dtm.getAxisTraverser(3);
            for (int child = traverser.first(parent); -1 != child; child = traverser.next(parent, child)) {
                try {
                    xctxt.pushCurrentNode(child);
                    if (NodeTest.SCORE_NONE != super.execute(xctxt, child)) {
                        boolean pass = true;
                        try {
                            xctxt.pushSubContextList(this);
                            for (int i = 0; i < predPos; ++i) {
                                xctxt.pushPredicatePos(i);
                                try {
                                    final XObject pred = this.m_predicates[i].execute(xctxt);
                                    try {
                                        if (2 == pred.getType()) {
                                            if (pos + 1 != (int)pred.numWithSideEffects()) {
                                                pass = false;
                                                break;
                                            }
                                        }
                                        else if (!pred.boolWithSideEffects()) {
                                            pass = false;
                                            break;
                                        }
                                    }
                                    finally {
                                        pred.detach();
                                    }
                                }
                                finally {
                                    xctxt.popPredicatePos();
                                }
                            }
                        }
                        finally {
                            xctxt.popSubContextList();
                        }
                        if (pass) {
                            ++pos;
                        }
                        if (!findLast && child == context) {
                            return pos;
                        }
                    }
                }
                finally {
                    xctxt.popCurrentNode();
                }
            }
        }
        catch (final TransformerException se) {
            throw new RuntimeException(se.getMessage());
        }
        return pos;
    }
    
    @Override
    public int getProximityPosition(final XPathContext xctxt) {
        return this.getProximityPosition(xctxt, xctxt.getPredicatePos(), false);
    }
    
    @Override
    public int getLastPos(final XPathContext xctxt) {
        return this.getProximityPosition(xctxt, xctxt.getPredicatePos(), true);
    }
    
    protected final XObject executeRelativePathPattern(final XPathContext xctxt, final DTM dtm, final int currentNode) throws TransformerException {
        XObject score = NodeTest.SCORE_NONE;
        final int context = currentNode;
        final DTMAxisTraverser traverser = dtm.getAxisTraverser(this.m_axis);
        for (int relative = traverser.first(context); -1 != relative; relative = traverser.next(context, relative)) {
            try {
                xctxt.pushCurrentNode(relative);
                score = this.execute(xctxt);
                if (score != NodeTest.SCORE_NONE) {
                    break;
                }
            }
            finally {
                xctxt.popCurrentNode();
            }
        }
        return score;
    }
    
    protected final boolean executePredicates(final XPathContext xctxt, final DTM dtm, final int currentNode) throws TransformerException {
        boolean result = true;
        boolean positionAlreadySeen = false;
        final int n = this.getPredicateCount();
        try {
            xctxt.pushSubContextList(this);
            for (int i = 0; i < n; ++i) {
                xctxt.pushPredicatePos(i);
                try {
                    final XObject pred = this.m_predicates[i].execute(xctxt);
                    try {
                        if (2 == pred.getType()) {
                            final int pos = (int)pred.num();
                            if (positionAlreadySeen) {
                                result = (pos == 1);
                                break;
                            }
                            positionAlreadySeen = true;
                            if (!this.checkProximityPosition(xctxt, i, dtm, currentNode, pos)) {
                                result = false;
                                break;
                            }
                        }
                        else if (!pred.boolWithSideEffects()) {
                            result = false;
                            break;
                        }
                    }
                    finally {
                        pred.detach();
                    }
                }
                finally {
                    xctxt.popPredicatePos();
                }
            }
        }
        finally {
            xctxt.popSubContextList();
        }
        return result;
    }
    
    @Override
    public String toString() {
        final StringBuffer buf = new StringBuffer();
        for (StepPattern pat = this; pat != null; pat = pat.m_relativePathPattern) {
            if (pat != this) {
                buf.append("/");
            }
            buf.append(Axis.getNames(pat.m_axis));
            buf.append("::");
            if (20480 == pat.m_whatToShow) {
                buf.append("doc()");
            }
            else if (65536 == pat.m_whatToShow) {
                buf.append("function()");
            }
            else if (-1 == pat.m_whatToShow) {
                buf.append("node()");
            }
            else if (4 == pat.m_whatToShow) {
                buf.append("text()");
            }
            else if (64 == pat.m_whatToShow) {
                buf.append("processing-instruction(");
                if (null != pat.m_name) {
                    buf.append(pat.m_name);
                }
                buf.append(")");
            }
            else if (128 == pat.m_whatToShow) {
                buf.append("comment()");
            }
            else if (null != pat.m_name) {
                if (2 == pat.m_whatToShow) {
                    buf.append("@");
                }
                if (null != pat.m_namespace) {
                    buf.append("{");
                    buf.append(pat.m_namespace);
                    buf.append("}");
                }
                buf.append(pat.m_name);
            }
            else if (2 == pat.m_whatToShow) {
                buf.append("@");
            }
            else if (1280 == pat.m_whatToShow) {
                buf.append("doc-root()");
            }
            else {
                buf.append('?').append(Integer.toHexString(pat.m_whatToShow));
            }
            if (null != pat.m_predicates) {
                for (int i = 0; i < pat.m_predicates.length; ++i) {
                    buf.append("[");
                    buf.append(pat.m_predicates[i]);
                    buf.append("]");
                }
            }
        }
        return buf.toString();
    }
    
    public double getMatchScore(final XPathContext xctxt, final int context) throws TransformerException {
        xctxt.pushCurrentNode(context);
        xctxt.pushCurrentExpressionNode(context);
        try {
            final XObject score = this.execute(xctxt);
            return score.num();
        }
        finally {
            xctxt.popCurrentNode();
            xctxt.popCurrentExpressionNode();
        }
    }
    
    public void setAxis(final int axis) {
        this.m_axis = axis;
    }
    
    public int getAxis() {
        return this.m_axis;
    }
    
    @Override
    public void callVisitors(final ExpressionOwner owner, final XPathVisitor visitor) {
        if (visitor.visitMatchPattern(owner, this)) {
            this.callSubtreeVisitors(visitor);
        }
    }
    
    protected void callSubtreeVisitors(final XPathVisitor visitor) {
        if (null != this.m_predicates) {
            for (int n = this.m_predicates.length, i = 0; i < n; ++i) {
                final ExpressionOwner predOwner = new PredOwner(i);
                if (visitor.visitPredicate(predOwner, this.m_predicates[i])) {
                    this.m_predicates[i].callVisitors(predOwner, visitor);
                }
            }
        }
        if (null != this.m_relativePathPattern) {
            this.m_relativePathPattern.callVisitors(this, visitor);
        }
    }
    
    @Override
    public Expression getExpression() {
        return this.m_relativePathPattern;
    }
    
    @Override
    public void setExpression(final Expression exp) {
        exp.exprSetParent(this);
        this.m_relativePathPattern = (StepPattern)exp;
    }
    
    @Override
    public boolean deepEquals(final Expression expr) {
        if (!super.deepEquals(expr)) {
            return false;
        }
        final StepPattern sp = (StepPattern)expr;
        if (null != this.m_predicates) {
            final int n = this.m_predicates.length;
            if (null == sp.m_predicates || sp.m_predicates.length != n) {
                return false;
            }
            for (int i = 0; i < n; ++i) {
                if (!this.m_predicates[i].deepEquals(sp.m_predicates[i])) {
                    return false;
                }
            }
        }
        else if (null != sp.m_predicates) {
            return false;
        }
        if (null != this.m_relativePathPattern) {
            if (!this.m_relativePathPattern.deepEquals(sp.m_relativePathPattern)) {
                return false;
            }
        }
        else if (sp.m_relativePathPattern != null) {
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
            return StepPattern.this.m_predicates[this.m_index];
        }
        
        @Override
        public void setExpression(final Expression exp) {
            exp.exprSetParent(StepPattern.this);
            StepPattern.this.m_predicates[this.m_index] = exp;
        }
    }
}
