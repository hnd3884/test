package com.sun.org.apache.xpath.internal.patterns;

import com.sun.org.apache.xml.internal.dtm.DTMAxisTraverser;
import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xpath.internal.axes.WalkerFactory;
import javax.xml.transform.TransformerException;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.XPathContext;

public class ContextMatchStepPattern extends StepPattern
{
    static final long serialVersionUID = -1888092779313211942L;
    
    public ContextMatchStepPattern(final int axis, final int paxis) {
        super(-1, axis, paxis);
    }
    
    @Override
    public XObject execute(final XPathContext xctxt) throws TransformerException {
        if (xctxt.getIteratorRoot() == xctxt.getCurrentNode()) {
            return this.getStaticScore();
        }
        return ContextMatchStepPattern.SCORE_NONE;
    }
    
    public XObject executeRelativePathPattern(final XPathContext xctxt, final StepPattern prevStep) throws TransformerException {
        XObject score = NodeTest.SCORE_NONE;
        final int context = xctxt.getCurrentNode();
        final DTM dtm = xctxt.getDTM(context);
        if (null != dtm) {
            final int predContext = xctxt.getCurrentNode();
            int axis = this.m_axis;
            final boolean needToTraverseAttrs = WalkerFactory.isDownwardAxisOfMany(axis);
            final boolean iterRootIsAttr = dtm.getNodeType(xctxt.getIteratorRoot()) == 2;
            if (11 == axis && iterRootIsAttr) {
                axis = 15;
            }
            final DTMAxisTraverser traverser = dtm.getAxisTraverser(axis);
            for (int relative = traverser.first(context); -1 != relative; relative = traverser.next(context, relative)) {
                try {
                    xctxt.pushCurrentNode(relative);
                    score = this.execute(xctxt);
                    if (score != NodeTest.SCORE_NONE) {
                        if (this.executePredicates(xctxt, dtm, context)) {
                            return score;
                        }
                        score = NodeTest.SCORE_NONE;
                    }
                    if (needToTraverseAttrs && iterRootIsAttr && 1 == dtm.getNodeType(relative)) {
                        int xaxis = 2;
                        for (int i = 0; i < 2; ++i) {
                            final DTMAxisTraverser atraverser = dtm.getAxisTraverser(xaxis);
                            for (int arelative = atraverser.first(relative); -1 != arelative; arelative = atraverser.next(relative, arelative)) {
                                try {
                                    xctxt.pushCurrentNode(arelative);
                                    score = this.execute(xctxt);
                                    if (score != NodeTest.SCORE_NONE && score != NodeTest.SCORE_NONE) {
                                        return score;
                                    }
                                }
                                finally {
                                    xctxt.popCurrentNode();
                                }
                            }
                            xaxis = 9;
                        }
                    }
                }
                finally {
                    xctxt.popCurrentNode();
                }
            }
        }
        return score;
    }
}
