package com.sun.org.apache.xpath.internal.axes;

import com.sun.org.apache.xpath.internal.patterns.FunctionPattern;
import com.sun.org.apache.xpath.internal.objects.XNumber;
import com.sun.org.apache.xpath.internal.patterns.ContextMatchStepPattern;
import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.patterns.StepPattern;
import com.sun.org.apache.xpath.internal.res.XPATHMessages;
import com.sun.org.apache.xpath.internal.compiler.OpMap;
import com.sun.org.apache.xml.internal.dtm.DTMIterator;
import com.sun.org.apache.xpath.internal.ExpressionNode;
import javax.xml.transform.TransformerException;
import com.sun.org.apache.xpath.internal.compiler.Compiler;

public class WalkerFactory
{
    static final boolean DEBUG_PATTERN_CREATION = false;
    static final boolean DEBUG_WALKER_CREATION = false;
    static final boolean DEBUG_ITERATOR_CREATION = false;
    public static final int BITS_COUNT = 255;
    public static final int BITS_RESERVED = 3840;
    public static final int BIT_PREDICATE = 4096;
    public static final int BIT_ANCESTOR = 8192;
    public static final int BIT_ANCESTOR_OR_SELF = 16384;
    public static final int BIT_ATTRIBUTE = 32768;
    public static final int BIT_CHILD = 65536;
    public static final int BIT_DESCENDANT = 131072;
    public static final int BIT_DESCENDANT_OR_SELF = 262144;
    public static final int BIT_FOLLOWING = 524288;
    public static final int BIT_FOLLOWING_SIBLING = 1048576;
    public static final int BIT_NAMESPACE = 2097152;
    public static final int BIT_PARENT = 4194304;
    public static final int BIT_PRECEDING = 8388608;
    public static final int BIT_PRECEDING_SIBLING = 16777216;
    public static final int BIT_SELF = 33554432;
    public static final int BIT_FILTER = 67108864;
    public static final int BIT_ROOT = 134217728;
    public static final int BITMASK_TRAVERSES_OUTSIDE_SUBTREE = 234381312;
    public static final int BIT_BACKWARDS_SELF = 268435456;
    public static final int BIT_ANY_DESCENDANT_FROM_ROOT = 536870912;
    public static final int BIT_NODETEST_ANY = 1073741824;
    public static final int BIT_MATCH_PATTERN = Integer.MIN_VALUE;
    
    static AxesWalker loadOneWalker(final WalkingIterator lpi, final Compiler compiler, final int stepOpCodePos) throws TransformerException {
        AxesWalker firstWalker = null;
        final int stepType = compiler.getOp(stepOpCodePos);
        if (stepType != -1) {
            firstWalker = createDefaultWalker(compiler, stepType, lpi, 0);
            firstWalker.init(compiler, stepOpCodePos, stepType);
        }
        return firstWalker;
    }
    
    static AxesWalker loadWalkers(final WalkingIterator lpi, final Compiler compiler, int stepOpCodePos, final int stepIndex) throws TransformerException {
        AxesWalker firstWalker = null;
        AxesWalker prevWalker = null;
        final int analysis = analyze(compiler, stepOpCodePos, stepIndex);
        int stepType;
        while (-1 != (stepType = compiler.getOp(stepOpCodePos))) {
            final AxesWalker walker = createDefaultWalker(compiler, stepOpCodePos, lpi, analysis);
            walker.init(compiler, stepOpCodePos, stepType);
            walker.exprSetParent(lpi);
            if (null == firstWalker) {
                firstWalker = walker;
            }
            else {
                prevWalker.setNextWalker(walker);
                walker.setPrevWalker(prevWalker);
            }
            prevWalker = walker;
            stepOpCodePos = compiler.getNextStepPos(stepOpCodePos);
            if (stepOpCodePos < 0) {
                break;
            }
        }
        return firstWalker;
    }
    
    public static boolean isSet(final int analysis, final int bits) {
        return 0x0 != (analysis & bits);
    }
    
    public static void diagnoseIterator(final String name, final int analysis, final Compiler compiler) {
        System.out.println(compiler.toString() + ", " + name + ", " + Integer.toBinaryString(analysis) + ", " + getAnalysisString(analysis));
    }
    
    public static DTMIterator newDTMIterator(final Compiler compiler, final int opPos, final boolean isTopLevel) throws TransformerException {
        final int firstStepPos = OpMap.getFirstChildPos(opPos);
        final int analysis = analyze(compiler, firstStepPos, 0);
        final boolean isOneStep = isOneStep(analysis);
        DTMIterator iter;
        if (isOneStep && walksSelfOnly(analysis) && isWild(analysis) && !hasPredicate(analysis)) {
            iter = new SelfIteratorNoPredicate(compiler, opPos, analysis);
        }
        else if (walksChildrenOnly(analysis) && isOneStep) {
            if (isWild(analysis) && !hasPredicate(analysis)) {
                iter = new ChildIterator(compiler, opPos, analysis);
            }
            else {
                iter = new ChildTestIterator(compiler, opPos, analysis);
            }
        }
        else if (isOneStep && walksAttributes(analysis)) {
            iter = new AttributeIterator(compiler, opPos, analysis);
        }
        else if (isOneStep && !walksFilteredList(analysis)) {
            if (!walksNamespaces(analysis) && (walksInDocOrder(analysis) || isSet(analysis, 4194304))) {
                iter = new OneStepIteratorForward(compiler, opPos, analysis);
            }
            else {
                iter = new OneStepIterator(compiler, opPos, analysis);
            }
        }
        else if (isOptimizableForDescendantIterator(compiler, firstStepPos, 0)) {
            iter = new DescendantIterator(compiler, opPos, analysis);
        }
        else if (isNaturalDocOrder(compiler, firstStepPos, 0, analysis)) {
            iter = new WalkingIterator(compiler, opPos, analysis, true);
        }
        else {
            iter = new WalkingIteratorSorted(compiler, opPos, analysis, true);
        }
        if (iter instanceof LocPathIterator) {
            ((LocPathIterator)iter).setIsTopLevel(isTopLevel);
        }
        return iter;
    }
    
    public static int getAxisFromStep(final Compiler compiler, final int stepOpCodePos) throws TransformerException {
        final int stepType = compiler.getOp(stepOpCodePos);
        switch (stepType) {
            case 43: {
                return 6;
            }
            case 44: {
                return 7;
            }
            case 46: {
                return 11;
            }
            case 47: {
                return 12;
            }
            case 45: {
                return 10;
            }
            case 49: {
                return 9;
            }
            case 37: {
                return 0;
            }
            case 38: {
                return 1;
            }
            case 39: {
                return 2;
            }
            case 50: {
                return 19;
            }
            case 40: {
                return 3;
            }
            case 42: {
                return 5;
            }
            case 41: {
                return 4;
            }
            case 48: {
                return 13;
            }
            case 22:
            case 23:
            case 24:
            case 25: {
                return 20;
            }
            default: {
                throw new RuntimeException(XPATHMessages.createXPATHMessage("ER_NULL_ERROR_HANDLER", new Object[] { Integer.toString(stepType) }));
            }
        }
    }
    
    public static int getAnalysisBitFromAxes(final int axis) {
        switch (axis) {
            case 0: {
                return 8192;
            }
            case 1: {
                return 16384;
            }
            case 2: {
                return 32768;
            }
            case 3: {
                return 65536;
            }
            case 4: {
                return 131072;
            }
            case 5: {
                return 262144;
            }
            case 6: {
                return 524288;
            }
            case 7: {
                return 1048576;
            }
            case 8:
            case 9: {
                return 2097152;
            }
            case 10: {
                return 4194304;
            }
            case 11: {
                return 8388608;
            }
            case 12: {
                return 16777216;
            }
            case 13: {
                return 33554432;
            }
            case 14: {
                return 262144;
            }
            case 16:
            case 17:
            case 18: {
                return 536870912;
            }
            case 19: {
                return 134217728;
            }
            case 20: {
                return 67108864;
            }
            default: {
                return 67108864;
            }
        }
    }
    
    static boolean functionProximateOrContainsProximate(final Compiler compiler, int opPos) {
        final int endFunc = opPos + compiler.getOp(opPos + 1) - 1;
        opPos = OpMap.getFirstChildPos(opPos);
        final int funcID = compiler.getOp(opPos);
        switch (funcID) {
            case 1:
            case 2: {
                return true;
            }
            default: {
                ++opPos;
                for (int i = 0, p = opPos; p < endFunc; p = compiler.getNextOpPos(p), ++i) {
                    final int innerExprOpPos = p + 2;
                    final int argOp = compiler.getOp(innerExprOpPos);
                    final boolean prox = isProximateInnerExpr(compiler, innerExprOpPos);
                    if (prox) {
                        return true;
                    }
                }
                return false;
            }
        }
    }
    
    static boolean isProximateInnerExpr(final Compiler compiler, final int opPos) {
        final int op = compiler.getOp(opPos);
        final int innerExprOpPos = opPos + 2;
        switch (op) {
            case 26: {
                if (isProximateInnerExpr(compiler, innerExprOpPos)) {
                    return true;
                }
                break;
            }
            case 21:
            case 22:
            case 27:
            case 28: {
                break;
            }
            case 25: {
                final boolean isProx = functionProximateOrContainsProximate(compiler, opPos);
                if (isProx) {
                    return true;
                }
                break;
            }
            case 5:
            case 6:
            case 7:
            case 8:
            case 9: {
                final int leftPos = OpMap.getFirstChildPos(op);
                final int rightPos = compiler.getNextOpPos(leftPos);
                boolean isProx = isProximateInnerExpr(compiler, leftPos);
                if (isProx) {
                    return true;
                }
                isProx = isProximateInnerExpr(compiler, rightPos);
                if (isProx) {
                    return true;
                }
                break;
            }
            default: {
                return true;
            }
        }
        return false;
    }
    
    public static boolean mightBeProximate(final Compiler compiler, final int opPos, final int stepType) throws TransformerException {
        final boolean mightBeProximate = false;
        switch (stepType) {
            case 22:
            case 23:
            case 24:
            case 25: {
                final int argLen = compiler.getArgLength(opPos);
                break;
            }
            default: {
                final int argLen = compiler.getArgLengthOfStep(opPos);
                break;
            }
        }
        int predPos = compiler.getFirstPredicateOpPos(opPos);
        int count = 0;
        while (29 == compiler.getOp(predPos)) {
            ++count;
            final int innerExprOpPos = predPos + 2;
            final int predOp = compiler.getOp(innerExprOpPos);
            switch (predOp) {
                case 22: {
                    return true;
                }
                case 28: {
                    break;
                }
                case 19:
                case 27: {
                    return true;
                }
                case 25: {
                    final boolean isProx = functionProximateOrContainsProximate(compiler, innerExprOpPos);
                    if (isProx) {
                        return true;
                    }
                    break;
                }
                case 5:
                case 6:
                case 7:
                case 8:
                case 9: {
                    final int leftPos = OpMap.getFirstChildPos(innerExprOpPos);
                    final int rightPos = compiler.getNextOpPos(leftPos);
                    boolean isProx = isProximateInnerExpr(compiler, leftPos);
                    if (isProx) {
                        return true;
                    }
                    isProx = isProximateInnerExpr(compiler, rightPos);
                    if (isProx) {
                        return true;
                    }
                    break;
                }
                default: {
                    return true;
                }
            }
            predPos = compiler.getNextOpPos(predPos);
        }
        return mightBeProximate;
    }
    
    private static boolean isOptimizableForDescendantIterator(final Compiler compiler, int stepOpCodePos, final int stepIndex) throws TransformerException {
        int stepCount = 0;
        boolean foundDorDS = false;
        boolean foundSelf = false;
        boolean foundDS = false;
        int nodeTestType = 1033;
        int stepType;
        while (-1 != (stepType = compiler.getOp(stepOpCodePos))) {
            if (nodeTestType != 1033 && nodeTestType != 35) {
                return false;
            }
            if (++stepCount > 3) {
                return false;
            }
            final boolean mightBeProximate = mightBeProximate(compiler, stepOpCodePos, stepType);
            if (mightBeProximate) {
                return false;
            }
            switch (stepType) {
                case 22:
                case 23:
                case 24:
                case 25:
                case 37:
                case 38:
                case 39:
                case 43:
                case 44:
                case 45:
                case 46:
                case 47:
                case 49:
                case 51:
                case 52:
                case 53: {
                    return false;
                }
                case 50: {
                    if (1 != stepCount) {
                        return false;
                    }
                    break;
                }
                case 40: {
                    if (!foundDS && (!foundDorDS || !foundSelf)) {
                        return false;
                    }
                    break;
                }
                case 42: {
                    foundDS = true;
                }
                case 41: {
                    if (3 == stepCount) {
                        return false;
                    }
                    foundDorDS = true;
                    break;
                }
                case 48: {
                    if (1 != stepCount) {
                        return false;
                    }
                    foundSelf = true;
                    break;
                }
                default: {
                    throw new RuntimeException(XPATHMessages.createXPATHMessage("ER_NULL_ERROR_HANDLER", new Object[] { Integer.toString(stepType) }));
                }
            }
            nodeTestType = compiler.getStepTestType(stepOpCodePos);
            final int nextStepOpCodePos = compiler.getNextStepPos(stepOpCodePos);
            if (nextStepOpCodePos < 0) {
                break;
            }
            if (-1 != compiler.getOp(nextStepOpCodePos) && compiler.countPredicates(stepOpCodePos) > 0) {
                return false;
            }
            stepOpCodePos = nextStepOpCodePos;
        }
        return true;
    }
    
    private static int analyze(final Compiler compiler, int stepOpCodePos, final int stepIndex) throws TransformerException {
        int stepCount = 0;
        int analysisResult = 0;
        int stepType;
        while (-1 != (stepType = compiler.getOp(stepOpCodePos))) {
            ++stepCount;
            final boolean predAnalysis = analyzePredicate(compiler, stepOpCodePos, stepType);
            if (predAnalysis) {
                analysisResult |= 0x1000;
            }
            switch (stepType) {
                case 22:
                case 23:
                case 24:
                case 25: {
                    analysisResult |= 0x4000000;
                    break;
                }
                case 50: {
                    analysisResult |= 0x8000000;
                    break;
                }
                case 37: {
                    analysisResult |= 0x2000;
                    break;
                }
                case 38: {
                    analysisResult |= 0x4000;
                    break;
                }
                case 39: {
                    analysisResult |= 0x8000;
                    break;
                }
                case 49: {
                    analysisResult |= 0x200000;
                    break;
                }
                case 40: {
                    analysisResult |= 0x10000;
                    break;
                }
                case 41: {
                    analysisResult |= 0x20000;
                    break;
                }
                case 42: {
                    if (2 == stepCount && 134217728 == analysisResult) {
                        analysisResult |= 0x20000000;
                    }
                    analysisResult |= 0x40000;
                    break;
                }
                case 43: {
                    analysisResult |= 0x80000;
                    break;
                }
                case 44: {
                    analysisResult |= 0x100000;
                    break;
                }
                case 46: {
                    analysisResult |= 0x800000;
                    break;
                }
                case 47: {
                    analysisResult |= 0x1000000;
                    break;
                }
                case 45: {
                    analysisResult |= 0x400000;
                    break;
                }
                case 48: {
                    analysisResult |= 0x2000000;
                    break;
                }
                case 51: {
                    analysisResult |= 0x80008000;
                    break;
                }
                case 52: {
                    analysisResult |= 0x80002000;
                    break;
                }
                case 53: {
                    analysisResult |= 0x80400000;
                    break;
                }
                default: {
                    throw new RuntimeException(XPATHMessages.createXPATHMessage("ER_NULL_ERROR_HANDLER", new Object[] { Integer.toString(stepType) }));
                }
            }
            if (1033 == compiler.getOp(stepOpCodePos + 3)) {
                analysisResult |= 0x40000000;
            }
            stepOpCodePos = compiler.getNextStepPos(stepOpCodePos);
            if (stepOpCodePos < 0) {
                break;
            }
        }
        analysisResult |= (stepCount & 0xFF);
        return analysisResult;
    }
    
    public static boolean isDownwardAxisOfMany(final int axis) {
        return 5 == axis || 4 == axis || 6 == axis || 11 == axis;
    }
    
    static StepPattern loadSteps(final MatchPatternIterator mpi, final Compiler compiler, int stepOpCodePos, final int stepIndex) throws TransformerException {
        StepPattern step = null;
        StepPattern firstStep = null;
        StepPattern prevStep = null;
        final int analysis = analyze(compiler, stepOpCodePos, stepIndex);
        int stepType;
        while (-1 != (stepType = compiler.getOp(stepOpCodePos))) {
            step = createDefaultStepPattern(compiler, stepOpCodePos, mpi, analysis, firstStep, prevStep);
            if (null == firstStep) {
                firstStep = step;
            }
            else {
                step.setRelativePathPattern(prevStep);
            }
            prevStep = step;
            stepOpCodePos = compiler.getNextStepPos(stepOpCodePos);
            if (stepOpCodePos < 0) {
                break;
            }
        }
        int axis = 13;
        final int paxis = 13;
        StepPattern tail = step;
        for (StepPattern pat = step; null != pat; pat = pat.getRelativePathPattern()) {
            final int nextAxis = pat.getAxis();
            pat.setAxis(axis);
            final int whatToShow = pat.getWhatToShow();
            if (whatToShow == 2 || whatToShow == 4096) {
                final int newAxis = (whatToShow == 2) ? 2 : 9;
                if (isDownwardAxisOfMany(axis)) {
                    final StepPattern attrPat = new StepPattern(whatToShow, pat.getNamespace(), pat.getLocalName(), newAxis, 0);
                    final XNumber score = pat.getStaticScore();
                    pat.setNamespace(null);
                    pat.setLocalName("*");
                    attrPat.setPredicates(pat.getPredicates());
                    pat.setPredicates(null);
                    pat.setWhatToShow(1);
                    final StepPattern rel = pat.getRelativePathPattern();
                    pat.setRelativePathPattern(attrPat);
                    attrPat.setRelativePathPattern(rel);
                    attrPat.setStaticScore(score);
                    if (11 == pat.getAxis()) {
                        pat.setAxis(15);
                    }
                    else if (4 == pat.getAxis()) {
                        pat.setAxis(5);
                    }
                    pat = attrPat;
                }
                else if (3 == pat.getAxis()) {
                    pat.setAxis(2);
                }
            }
            axis = nextAxis;
            tail = pat;
        }
        if (axis < 16) {
            final StepPattern selfPattern = new ContextMatchStepPattern(axis, paxis);
            final XNumber score2 = tail.getStaticScore();
            tail.setRelativePathPattern(selfPattern);
            tail.setStaticScore(score2);
            selfPattern.setStaticScore(score2);
        }
        return step;
    }
    
    private static StepPattern createDefaultStepPattern(final Compiler compiler, final int opPos, final MatchPatternIterator mpi, final int analysis, final StepPattern tail, final StepPattern head) throws TransformerException {
        final int stepType = compiler.getOp(opPos);
        boolean simpleInit = false;
        boolean prevIsOneStepDown = true;
        int whatToShow = compiler.getWhatToShow(opPos);
        StepPattern ai = null;
        int axis = 0;
        int predicateAxis = 0;
        switch (stepType) {
            case 22:
            case 23:
            case 24:
            case 25: {
                prevIsOneStepDown = false;
                Expression expr = null;
                switch (stepType) {
                    case 22:
                    case 23:
                    case 24:
                    case 25: {
                        expr = compiler.compileExpression(opPos);
                        break;
                    }
                    default: {
                        expr = compiler.compileExpression(opPos + 2);
                        break;
                    }
                }
                axis = 20;
                predicateAxis = 20;
                ai = new FunctionPattern(expr, axis, predicateAxis);
                simpleInit = true;
                break;
            }
            case 50: {
                whatToShow = 1280;
                axis = 19;
                predicateAxis = 19;
                ai = new StepPattern(1280, axis, predicateAxis);
                break;
            }
            case 39: {
                whatToShow = 2;
                axis = 10;
                predicateAxis = 2;
                break;
            }
            case 49: {
                whatToShow = 4096;
                axis = 10;
                predicateAxis = 9;
                break;
            }
            case 37: {
                axis = 4;
                predicateAxis = 0;
                break;
            }
            case 40: {
                axis = 10;
                predicateAxis = 3;
                break;
            }
            case 38: {
                axis = 5;
                predicateAxis = 1;
                break;
            }
            case 48: {
                axis = 13;
                predicateAxis = 13;
                break;
            }
            case 45: {
                axis = 3;
                predicateAxis = 10;
                break;
            }
            case 47: {
                axis = 7;
                predicateAxis = 12;
                break;
            }
            case 46: {
                axis = 6;
                predicateAxis = 11;
                break;
            }
            case 44: {
                axis = 12;
                predicateAxis = 7;
                break;
            }
            case 43: {
                axis = 11;
                predicateAxis = 6;
                break;
            }
            case 42: {
                axis = 1;
                predicateAxis = 5;
                break;
            }
            case 41: {
                axis = 0;
                predicateAxis = 4;
                break;
            }
            default: {
                throw new RuntimeException(XPATHMessages.createXPATHMessage("ER_NULL_ERROR_HANDLER", new Object[] { Integer.toString(stepType) }));
            }
        }
        if (null == ai) {
            whatToShow = compiler.getWhatToShow(opPos);
            ai = new StepPattern(whatToShow, compiler.getStepNS(opPos), compiler.getStepLocalName(opPos), axis, predicateAxis);
        }
        final int argLen = compiler.getFirstPredicateOpPos(opPos);
        ai.setPredicates(compiler.getCompiledPredicates(argLen));
        return ai;
    }
    
    static boolean analyzePredicate(final Compiler compiler, final int opPos, final int stepType) throws TransformerException {
        switch (stepType) {
            case 22:
            case 23:
            case 24:
            case 25: {
                final int argLen = compiler.getArgLength(opPos);
                break;
            }
            default: {
                final int argLen = compiler.getArgLengthOfStep(opPos);
                break;
            }
        }
        final int pos = compiler.getFirstPredicateOpPos(opPos);
        final int nPredicates = compiler.countPredicates(pos);
        return nPredicates > 0;
    }
    
    private static AxesWalker createDefaultWalker(final Compiler compiler, final int opPos, final WalkingIterator lpi, final int analysis) {
        AxesWalker ai = null;
        final int stepType = compiler.getOp(opPos);
        boolean simpleInit = false;
        final int totalNumberWalkers = analysis & 0xFF;
        boolean prevIsOneStepDown = true;
        switch (stepType) {
            case 22:
            case 23:
            case 24:
            case 25: {
                prevIsOneStepDown = false;
                ai = new FilterExprWalker(lpi);
                simpleInit = true;
                break;
            }
            case 50: {
                ai = new AxesWalker(lpi, 19);
                break;
            }
            case 37: {
                prevIsOneStepDown = false;
                ai = new ReverseAxesWalker(lpi, 0);
                break;
            }
            case 38: {
                prevIsOneStepDown = false;
                ai = new ReverseAxesWalker(lpi, 1);
                break;
            }
            case 39: {
                ai = new AxesWalker(lpi, 2);
                break;
            }
            case 49: {
                ai = new AxesWalker(lpi, 9);
                break;
            }
            case 40: {
                ai = new AxesWalker(lpi, 3);
                break;
            }
            case 41: {
                prevIsOneStepDown = false;
                ai = new AxesWalker(lpi, 4);
                break;
            }
            case 42: {
                prevIsOneStepDown = false;
                ai = new AxesWalker(lpi, 5);
                break;
            }
            case 43: {
                prevIsOneStepDown = false;
                ai = new AxesWalker(lpi, 6);
                break;
            }
            case 44: {
                prevIsOneStepDown = false;
                ai = new AxesWalker(lpi, 7);
                break;
            }
            case 46: {
                prevIsOneStepDown = false;
                ai = new ReverseAxesWalker(lpi, 11);
                break;
            }
            case 47: {
                prevIsOneStepDown = false;
                ai = new ReverseAxesWalker(lpi, 12);
                break;
            }
            case 45: {
                prevIsOneStepDown = false;
                ai = new ReverseAxesWalker(lpi, 10);
                break;
            }
            case 48: {
                ai = new AxesWalker(lpi, 13);
                break;
            }
            default: {
                throw new RuntimeException(XPATHMessages.createXPATHMessage("ER_NULL_ERROR_HANDLER", new Object[] { Integer.toString(stepType) }));
            }
        }
        if (simpleInit) {
            ai.initNodeTest(-1);
        }
        else {
            final int whatToShow = compiler.getWhatToShow(opPos);
            if (0x0 == (whatToShow & 0x1043) || whatToShow == -1) {
                ai.initNodeTest(whatToShow);
            }
            else {
                ai.initNodeTest(whatToShow, compiler.getStepNS(opPos), compiler.getStepLocalName(opPos));
            }
        }
        return ai;
    }
    
    public static String getAnalysisString(final int analysis) {
        final StringBuffer buf = new StringBuffer();
        buf.append("count: ").append(getStepCount(analysis)).append(' ');
        if ((analysis & 0x40000000) != 0x0) {
            buf.append("NTANY|");
        }
        if ((analysis & 0x1000) != 0x0) {
            buf.append("PRED|");
        }
        if ((analysis & 0x2000) != 0x0) {
            buf.append("ANC|");
        }
        if ((analysis & 0x4000) != 0x0) {
            buf.append("ANCOS|");
        }
        if ((analysis & 0x8000) != 0x0) {
            buf.append("ATTR|");
        }
        if ((analysis & 0x10000) != 0x0) {
            buf.append("CH|");
        }
        if ((analysis & 0x20000) != 0x0) {
            buf.append("DESC|");
        }
        if ((analysis & 0x40000) != 0x0) {
            buf.append("DESCOS|");
        }
        if ((analysis & 0x80000) != 0x0) {
            buf.append("FOL|");
        }
        if ((analysis & 0x100000) != 0x0) {
            buf.append("FOLS|");
        }
        if ((analysis & 0x200000) != 0x0) {
            buf.append("NS|");
        }
        if ((analysis & 0x400000) != 0x0) {
            buf.append("P|");
        }
        if ((analysis & 0x800000) != 0x0) {
            buf.append("PREC|");
        }
        if ((analysis & 0x1000000) != 0x0) {
            buf.append("PRECS|");
        }
        if ((analysis & 0x2000000) != 0x0) {
            buf.append(".|");
        }
        if ((analysis & 0x4000000) != 0x0) {
            buf.append("FLT|");
        }
        if ((analysis & 0x8000000) != 0x0) {
            buf.append("R|");
        }
        return buf.toString();
    }
    
    public static boolean hasPredicate(final int analysis) {
        return 0x0 != (analysis & 0x1000);
    }
    
    public static boolean isWild(final int analysis) {
        return 0x0 != (analysis & 0x40000000);
    }
    
    public static boolean walksAncestors(final int analysis) {
        return isSet(analysis, 24576);
    }
    
    public static boolean walksAttributes(final int analysis) {
        return 0x0 != (analysis & 0x8000);
    }
    
    public static boolean walksNamespaces(final int analysis) {
        return 0x0 != (analysis & 0x200000);
    }
    
    public static boolean walksChildren(final int analysis) {
        return 0x0 != (analysis & 0x10000);
    }
    
    public static boolean walksDescendants(final int analysis) {
        return isSet(analysis, 393216);
    }
    
    public static boolean walksSubtree(final int analysis) {
        return isSet(analysis, 458752);
    }
    
    public static boolean walksSubtreeOnlyMaybeAbsolute(final int analysis) {
        return walksSubtree(analysis) && !walksExtraNodes(analysis) && !walksUp(analysis) && !walksSideways(analysis);
    }
    
    public static boolean walksSubtreeOnly(final int analysis) {
        return walksSubtreeOnlyMaybeAbsolute(analysis) && !isAbsolute(analysis);
    }
    
    public static boolean walksFilteredList(final int analysis) {
        return isSet(analysis, 67108864);
    }
    
    public static boolean walksSubtreeOnlyFromRootOrContext(final int analysis) {
        return walksSubtree(analysis) && !walksExtraNodes(analysis) && !walksUp(analysis) && !walksSideways(analysis) && !isSet(analysis, 67108864);
    }
    
    public static boolean walksInDocOrder(final int analysis) {
        return (walksSubtreeOnlyMaybeAbsolute(analysis) || walksExtraNodesOnly(analysis) || walksFollowingOnlyMaybeAbsolute(analysis)) && !isSet(analysis, 67108864);
    }
    
    public static boolean walksFollowingOnlyMaybeAbsolute(final int analysis) {
        return isSet(analysis, 35127296) && !walksSubtree(analysis) && !walksUp(analysis) && !walksSideways(analysis);
    }
    
    public static boolean walksUp(final int analysis) {
        return isSet(analysis, 4218880);
    }
    
    public static boolean walksSideways(final int analysis) {
        return isSet(analysis, 26738688);
    }
    
    public static boolean walksExtraNodes(final int analysis) {
        return isSet(analysis, 2129920);
    }
    
    public static boolean walksExtraNodesOnly(final int analysis) {
        return walksExtraNodes(analysis) && !isSet(analysis, 33554432) && !walksSubtree(analysis) && !walksUp(analysis) && !walksSideways(analysis) && !isAbsolute(analysis);
    }
    
    public static boolean isAbsolute(final int analysis) {
        return isSet(analysis, 201326592);
    }
    
    public static boolean walksChildrenOnly(final int analysis) {
        return walksChildren(analysis) && !isSet(analysis, 33554432) && !walksExtraNodes(analysis) && !walksDescendants(analysis) && !walksUp(analysis) && !walksSideways(analysis) && (!isAbsolute(analysis) || isSet(analysis, 134217728));
    }
    
    public static boolean walksChildrenAndExtraAndSelfOnly(final int analysis) {
        return walksChildren(analysis) && !walksDescendants(analysis) && !walksUp(analysis) && !walksSideways(analysis) && (!isAbsolute(analysis) || isSet(analysis, 134217728));
    }
    
    public static boolean walksDescendantsAndExtraAndSelfOnly(final int analysis) {
        return !walksChildren(analysis) && walksDescendants(analysis) && !walksUp(analysis) && !walksSideways(analysis) && (!isAbsolute(analysis) || isSet(analysis, 134217728));
    }
    
    public static boolean walksSelfOnly(final int analysis) {
        return isSet(analysis, 33554432) && !walksSubtree(analysis) && !walksUp(analysis) && !walksSideways(analysis) && !isAbsolute(analysis);
    }
    
    public static boolean walksUpOnly(final int analysis) {
        return !walksSubtree(analysis) && walksUp(analysis) && !walksSideways(analysis) && !isAbsolute(analysis);
    }
    
    public static boolean walksDownOnly(final int analysis) {
        return walksSubtree(analysis) && !walksUp(analysis) && !walksSideways(analysis) && !isAbsolute(analysis);
    }
    
    public static boolean walksDownExtraOnly(final int analysis) {
        return walksSubtree(analysis) && walksExtraNodes(analysis) && !walksUp(analysis) && !walksSideways(analysis) && !isAbsolute(analysis);
    }
    
    public static boolean canSkipSubtrees(final int analysis) {
        return isSet(analysis, 65536) | walksSideways(analysis);
    }
    
    public static boolean canCrissCross(final int analysis) {
        return !walksSelfOnly(analysis) && (!walksDownOnly(analysis) || canSkipSubtrees(analysis)) && !walksChildrenAndExtraAndSelfOnly(analysis) && !walksDescendantsAndExtraAndSelfOnly(analysis) && !walksUpOnly(analysis) && !walksExtraNodesOnly(analysis) && (walksSubtree(analysis) && (walksSideways(analysis) || walksUp(analysis) || canSkipSubtrees(analysis)));
    }
    
    public static boolean isNaturalDocOrder(final int analysis) {
        return !canCrissCross(analysis) && !isSet(analysis, 2097152) && !walksFilteredList(analysis) && walksInDocOrder(analysis);
    }
    
    private static boolean isNaturalDocOrder(final Compiler compiler, int stepOpCodePos, final int stepIndex, final int analysis) throws TransformerException {
        if (canCrissCross(analysis)) {
            return false;
        }
        if (isSet(analysis, 2097152)) {
            return false;
        }
        if (isSet(analysis, 1572864) && isSet(analysis, 25165824)) {
            return false;
        }
        int stepCount = 0;
        boolean foundWildAttribute = false;
        int potentialDuplicateMakingStepCount = 0;
        int stepType;
        while (-1 != (stepType = compiler.getOp(stepOpCodePos))) {
            ++stepCount;
            switch (stepType) {
                case 39:
                case 51: {
                    if (foundWildAttribute) {
                        return false;
                    }
                    final String localName = compiler.getStepLocalName(stepOpCodePos);
                    if (localName.equals("*")) {
                        foundWildAttribute = true;
                        break;
                    }
                    break;
                }
                case 22:
                case 23:
                case 24:
                case 25:
                case 37:
                case 38:
                case 41:
                case 42:
                case 43:
                case 44:
                case 45:
                case 46:
                case 47:
                case 49:
                case 52:
                case 53: {
                    if (potentialDuplicateMakingStepCount > 0) {
                        return false;
                    }
                    ++potentialDuplicateMakingStepCount;
                }
                case 40:
                case 48:
                case 50: {
                    if (foundWildAttribute) {
                        return false;
                    }
                    break;
                }
                default: {
                    throw new RuntimeException(XPATHMessages.createXPATHMessage("ER_NULL_ERROR_HANDLER", new Object[] { Integer.toString(stepType) }));
                }
            }
            final int nextStepOpCodePos = compiler.getNextStepPos(stepOpCodePos);
            if (nextStepOpCodePos < 0) {
                break;
            }
            stepOpCodePos = nextStepOpCodePos;
        }
        return true;
    }
    
    public static boolean isOneStep(final int analysis) {
        return (analysis & 0xFF) == 0x1;
    }
    
    public static int getStepCount(final int analysis) {
        return analysis & 0xFF;
    }
}
