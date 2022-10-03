package com.sun.org.apache.xpath.internal.compiler;

import com.sun.org.apache.xpath.internal.functions.FuncExtFunction;
import com.sun.org.apache.xpath.internal.functions.Function;
import com.sun.org.apache.xpath.internal.functions.WrongNumberArgsException;
import com.sun.org.apache.xpath.internal.res.XPATHMessages;
import com.sun.org.apache.xpath.internal.functions.FuncExtFunctionAvailable;
import com.sun.org.apache.xpath.internal.patterns.FunctionPattern;
import com.sun.org.apache.xpath.internal.patterns.StepPattern;
import com.sun.org.apache.xpath.internal.patterns.UnionPattern;
import com.sun.org.apache.xml.internal.dtm.DTMIterator;
import com.sun.org.apache.xpath.internal.axes.WalkerFactory;
import com.sun.org.apache.xpath.internal.axes.UnionPathIterator;
import com.sun.org.apache.xml.internal.utils.QName;
import com.sun.org.apache.xpath.internal.operations.Variable;
import com.sun.org.apache.xpath.internal.objects.XNumber;
import com.sun.org.apache.xpath.internal.objects.XString;
import com.sun.org.apache.xpath.internal.operations.Number;
import com.sun.org.apache.xpath.internal.operations.Bool;
import com.sun.org.apache.xpath.internal.operations.String;
import com.sun.org.apache.xpath.internal.operations.Neg;
import com.sun.org.apache.xpath.internal.operations.Mod;
import com.sun.org.apache.xpath.internal.operations.Div;
import com.sun.org.apache.xpath.internal.operations.Mult;
import com.sun.org.apache.xpath.internal.operations.Minus;
import com.sun.org.apache.xpath.internal.operations.Plus;
import com.sun.org.apache.xpath.internal.operations.Gt;
import com.sun.org.apache.xpath.internal.operations.Gte;
import com.sun.org.apache.xpath.internal.operations.Lt;
import com.sun.org.apache.xpath.internal.operations.Lte;
import com.sun.org.apache.xpath.internal.operations.Equals;
import com.sun.org.apache.xpath.internal.operations.NotEquals;
import com.sun.org.apache.xpath.internal.operations.And;
import com.sun.org.apache.xpath.internal.operations.Or;
import com.sun.org.apache.xpath.internal.operations.UnaryOperation;
import com.sun.org.apache.xpath.internal.operations.Operation;
import javax.xml.transform.TransformerException;
import com.sun.org.apache.xpath.internal.Expression;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.ErrorListener;
import com.sun.org.apache.xml.internal.utils.PrefixResolver;

public class Compiler extends OpMap
{
    int countOp;
    private int locPathDepth;
    private static final boolean DEBUG = false;
    private static long s_nextMethodId;
    private PrefixResolver m_currentPrefixResolver;
    ErrorListener m_errorHandler;
    SourceLocator m_locator;
    private FunctionTable m_functionTable;
    
    public Compiler(final ErrorListener errorHandler, final SourceLocator locator, final FunctionTable fTable) {
        this.locPathDepth = -1;
        this.m_currentPrefixResolver = null;
        this.m_errorHandler = errorHandler;
        this.m_locator = locator;
        this.m_functionTable = fTable;
    }
    
    public Compiler() {
        this.locPathDepth = -1;
        this.m_currentPrefixResolver = null;
        this.m_errorHandler = null;
        this.m_locator = null;
    }
    
    public Expression compileExpression(final int opPos) throws TransformerException {
        try {
            this.countOp = 0;
            return this.compile(opPos);
        }
        catch (final StackOverflowError sof) {
            this.error("ER_COMPILATION_TOO_MANY_OPERATION", new Object[] { this.countOp });
            return null;
        }
    }
    
    private Expression compile(final int opPos) throws TransformerException {
        final int op = this.getOp(opPos);
        Expression expr = null;
        switch (op) {
            case 1: {
                expr = this.compile(opPos + 2);
                break;
            }
            case 2: {
                expr = this.or(opPos);
                break;
            }
            case 3: {
                expr = this.and(opPos);
                break;
            }
            case 4: {
                expr = this.notequals(opPos);
                break;
            }
            case 5: {
                expr = this.equals(opPos);
                break;
            }
            case 6: {
                expr = this.lte(opPos);
                break;
            }
            case 7: {
                expr = this.lt(opPos);
                break;
            }
            case 8: {
                expr = this.gte(opPos);
                break;
            }
            case 9: {
                expr = this.gt(opPos);
                break;
            }
            case 10: {
                expr = this.plus(opPos);
                break;
            }
            case 11: {
                expr = this.minus(opPos);
                break;
            }
            case 12: {
                expr = this.mult(opPos);
                break;
            }
            case 13: {
                expr = this.div(opPos);
                break;
            }
            case 14: {
                expr = this.mod(opPos);
                break;
            }
            case 16: {
                expr = this.neg(opPos);
                break;
            }
            case 17: {
                expr = this.string(opPos);
                break;
            }
            case 18: {
                expr = this.bool(opPos);
                break;
            }
            case 19: {
                expr = this.number(opPos);
                break;
            }
            case 20: {
                expr = this.union(opPos);
                break;
            }
            case 21: {
                expr = this.literal(opPos);
                break;
            }
            case 22: {
                expr = this.variable(opPos);
                break;
            }
            case 23: {
                expr = this.group(opPos);
                break;
            }
            case 27: {
                expr = this.numberlit(opPos);
                break;
            }
            case 26: {
                expr = this.arg(opPos);
                break;
            }
            case 24: {
                expr = this.compileExtension(opPos);
                break;
            }
            case 25: {
                expr = this.compileFunction(opPos);
                break;
            }
            case 28: {
                expr = this.locationPath(opPos);
                break;
            }
            case 29: {
                expr = null;
                break;
            }
            case 30: {
                expr = this.matchPattern(opPos + 2);
                break;
            }
            case 31: {
                expr = this.locationPathPattern(opPos);
                break;
            }
            case 15: {
                this.error("ER_UNKNOWN_OPCODE", new Object[] { "quo" });
                break;
            }
            default: {
                this.error("ER_UNKNOWN_OPCODE", new Object[] { Integer.toString(this.getOp(opPos)) });
                break;
            }
        }
        return expr;
    }
    
    private Expression compileOperation(final Operation operation, final int opPos) throws TransformerException {
        ++this.countOp;
        final int leftPos = OpMap.getFirstChildPos(opPos);
        final int rightPos = this.getNextOpPos(leftPos);
        operation.setLeftRight(this.compile(leftPos), this.compile(rightPos));
        return operation;
    }
    
    private Expression compileUnary(final UnaryOperation unary, final int opPos) throws TransformerException {
        final int rightPos = OpMap.getFirstChildPos(opPos);
        unary.setRight(this.compile(rightPos));
        return unary;
    }
    
    protected Expression or(final int opPos) throws TransformerException {
        return this.compileOperation(new Or(), opPos);
    }
    
    protected Expression and(final int opPos) throws TransformerException {
        return this.compileOperation(new And(), opPos);
    }
    
    protected Expression notequals(final int opPos) throws TransformerException {
        return this.compileOperation(new NotEquals(), opPos);
    }
    
    protected Expression equals(final int opPos) throws TransformerException {
        return this.compileOperation(new Equals(), opPos);
    }
    
    protected Expression lte(final int opPos) throws TransformerException {
        return this.compileOperation(new Lte(), opPos);
    }
    
    protected Expression lt(final int opPos) throws TransformerException {
        return this.compileOperation(new Lt(), opPos);
    }
    
    protected Expression gte(final int opPos) throws TransformerException {
        return this.compileOperation(new Gte(), opPos);
    }
    
    protected Expression gt(final int opPos) throws TransformerException {
        return this.compileOperation(new Gt(), opPos);
    }
    
    protected Expression plus(final int opPos) throws TransformerException {
        return this.compileOperation(new Plus(), opPos);
    }
    
    protected Expression minus(final int opPos) throws TransformerException {
        return this.compileOperation(new Minus(), opPos);
    }
    
    protected Expression mult(final int opPos) throws TransformerException {
        return this.compileOperation(new Mult(), opPos);
    }
    
    protected Expression div(final int opPos) throws TransformerException {
        return this.compileOperation(new Div(), opPos);
    }
    
    protected Expression mod(final int opPos) throws TransformerException {
        return this.compileOperation(new Mod(), opPos);
    }
    
    protected Expression neg(final int opPos) throws TransformerException {
        return this.compileUnary(new Neg(), opPos);
    }
    
    protected Expression string(final int opPos) throws TransformerException {
        return this.compileUnary(new String(), opPos);
    }
    
    protected Expression bool(final int opPos) throws TransformerException {
        return this.compileUnary(new Bool(), opPos);
    }
    
    protected Expression number(final int opPos) throws TransformerException {
        return this.compileUnary(new Number(), opPos);
    }
    
    protected Expression literal(int opPos) {
        opPos = OpMap.getFirstChildPos(opPos);
        return (XString)this.getTokenQueue().elementAt(this.getOp(opPos));
    }
    
    protected Expression numberlit(int opPos) {
        opPos = OpMap.getFirstChildPos(opPos);
        return (XNumber)this.getTokenQueue().elementAt(this.getOp(opPos));
    }
    
    protected Expression variable(int opPos) throws TransformerException {
        final Variable var = new Variable();
        opPos = OpMap.getFirstChildPos(opPos);
        final int nsPos = this.getOp(opPos);
        final java.lang.String namespace = (-2 == nsPos) ? null : ((java.lang.String)this.getTokenQueue().elementAt(nsPos));
        final java.lang.String localname = (java.lang.String)this.getTokenQueue().elementAt(this.getOp(opPos + 1));
        final QName qname = new QName(namespace, localname);
        var.setQName(qname);
        return var;
    }
    
    protected Expression group(final int opPos) throws TransformerException {
        return this.compile(opPos + 2);
    }
    
    protected Expression arg(final int opPos) throws TransformerException {
        return this.compile(opPos + 2);
    }
    
    protected Expression union(final int opPos) throws TransformerException {
        ++this.locPathDepth;
        try {
            return UnionPathIterator.createUnionIterator(this, opPos);
        }
        finally {
            --this.locPathDepth;
        }
    }
    
    public int getLocationPathDepth() {
        return this.locPathDepth;
    }
    
    FunctionTable getFunctionTable() {
        return this.m_functionTable;
    }
    
    public Expression locationPath(final int opPos) throws TransformerException {
        ++this.locPathDepth;
        try {
            final DTMIterator iter = WalkerFactory.newDTMIterator(this, opPos, this.locPathDepth == 0);
            return (Expression)iter;
        }
        finally {
            --this.locPathDepth;
        }
    }
    
    public Expression predicate(final int opPos) throws TransformerException {
        return this.compile(opPos + 2);
    }
    
    protected Expression matchPattern(int opPos) throws TransformerException {
        ++this.locPathDepth;
        try {
            int nextOpPos;
            int i;
            for (nextOpPos = opPos, i = 0; this.getOp(nextOpPos) == 31; nextOpPos = this.getNextOpPos(nextOpPos), ++i) {}
            if (i == 1) {
                return this.compile(opPos);
            }
            final UnionPattern up = new UnionPattern();
            final StepPattern[] patterns = new StepPattern[i];
            for (i = 0; this.getOp(opPos) == 31; opPos = nextOpPos, ++i) {
                nextOpPos = this.getNextOpPos(opPos);
                patterns[i] = (StepPattern)this.compile(opPos);
            }
            up.setPatterns(patterns);
            return up;
        }
        finally {
            --this.locPathDepth;
        }
    }
    
    public Expression locationPathPattern(int opPos) throws TransformerException {
        opPos = OpMap.getFirstChildPos(opPos);
        return this.stepPattern(opPos, 0, null);
    }
    
    public int getWhatToShow(final int opPos) {
        final int axesType = this.getOp(opPos);
        final int testType = this.getOp(opPos + 3);
        switch (testType) {
            case 1030: {
                return 128;
            }
            case 1031: {
                return 12;
            }
            case 1032: {
                return 64;
            }
            case 1033: {
                switch (axesType) {
                    case 49: {
                        return 4096;
                    }
                    case 39:
                    case 51: {
                        return 2;
                    }
                    case 38:
                    case 42:
                    case 48: {
                        return -1;
                    }
                    default: {
                        if (this.getOp(0) == 30) {
                            return -1283;
                        }
                        return -3;
                    }
                }
                break;
            }
            case 35: {
                return 1280;
            }
            case 1034: {
                return 65536;
            }
            case 34: {
                switch (axesType) {
                    case 49: {
                        return 4096;
                    }
                    case 39:
                    case 51: {
                        return 2;
                    }
                    case 52:
                    case 53: {
                        return 1;
                    }
                    default: {
                        return 1;
                    }
                }
                break;
            }
            default: {
                return -1;
            }
        }
    }
    
    protected StepPattern stepPattern(int opPos, final int stepCount, final StepPattern ancestorPattern) throws TransformerException {
        final int startOpPos = opPos;
        final int stepType = this.getOp(opPos);
        if (-1 == stepType) {
            return null;
        }
        boolean addMagicSelf = true;
        final int endStep = this.getNextOpPos(opPos);
        int argLen = 0;
        StepPattern pattern = null;
        switch (stepType) {
            case 25: {
                addMagicSelf = false;
                argLen = this.getOp(opPos + 1);
                pattern = new FunctionPattern(this.compileFunction(opPos), 10, 3);
                break;
            }
            case 50: {
                addMagicSelf = false;
                argLen = this.getArgLengthOfStep(opPos);
                opPos = OpMap.getFirstChildPosOfStep(opPos);
                pattern = new StepPattern(1280, 10, 3);
                break;
            }
            case 51: {
                argLen = this.getArgLengthOfStep(opPos);
                opPos = OpMap.getFirstChildPosOfStep(opPos);
                pattern = new StepPattern(2, this.getStepNS(startOpPos), this.getStepLocalName(startOpPos), 10, 2);
                break;
            }
            case 52: {
                argLen = this.getArgLengthOfStep(opPos);
                opPos = OpMap.getFirstChildPosOfStep(opPos);
                final int what = this.getWhatToShow(startOpPos);
                if (1280 == what) {
                    addMagicSelf = false;
                }
                pattern = new StepPattern(this.getWhatToShow(startOpPos), this.getStepNS(startOpPos), this.getStepLocalName(startOpPos), 0, 3);
                break;
            }
            case 53: {
                argLen = this.getArgLengthOfStep(opPos);
                opPos = OpMap.getFirstChildPosOfStep(opPos);
                pattern = new StepPattern(this.getWhatToShow(startOpPos), this.getStepNS(startOpPos), this.getStepLocalName(startOpPos), 10, 3);
                break;
            }
            default: {
                this.error("ER_UNKNOWN_MATCH_OPERATION", null);
                return null;
            }
        }
        pattern.setPredicates(this.getCompiledPredicates(opPos + argLen));
        if (null != ancestorPattern) {
            pattern.setRelativePathPattern(ancestorPattern);
        }
        final StepPattern relativePathPattern = this.stepPattern(endStep, stepCount + 1, pattern);
        return (null != relativePathPattern) ? relativePathPattern : pattern;
    }
    
    public Expression[] getCompiledPredicates(final int opPos) throws TransformerException {
        final int count = this.countPredicates(opPos);
        if (count > 0) {
            final Expression[] predicates = new Expression[count];
            this.compilePredicates(opPos, predicates);
            return predicates;
        }
        return null;
    }
    
    public int countPredicates(int opPos) throws TransformerException {
        int count = 0;
        while (29 == this.getOp(opPos)) {
            ++count;
            opPos = this.getNextOpPos(opPos);
        }
        return count;
    }
    
    private void compilePredicates(int opPos, final Expression[] predicates) throws TransformerException {
        for (int i = 0; 29 == this.getOp(opPos); opPos = this.getNextOpPos(opPos), ++i) {
            predicates[i] = this.predicate(opPos);
        }
    }
    
    Expression compileFunction(int opPos) throws TransformerException {
        final int endFunc = opPos + this.getOp(opPos + 1) - 1;
        opPos = OpMap.getFirstChildPos(opPos);
        final int funcID = this.getOp(opPos);
        ++opPos;
        if (-1 != funcID) {
            final Function func = this.m_functionTable.getFunction(funcID);
            if (func instanceof FuncExtFunctionAvailable) {
                ((FuncExtFunctionAvailable)func).setFunctionTable(this.m_functionTable);
            }
            func.postCompileStep(this);
            try {
                int i = 0;
                for (int p = opPos; p < endFunc; p = this.getNextOpPos(p), ++i) {
                    func.setArg(this.compile(p), i);
                }
                func.checkNumberArgs(i);
            }
            catch (final WrongNumberArgsException wnae) {
                final java.lang.String name = this.m_functionTable.getFunctionName(funcID);
                this.m_errorHandler.fatalError(new TransformerException(XPATHMessages.createXPATHMessage("ER_ONLY_ALLOWS", new Object[] { name, wnae.getMessage() }), this.m_locator));
            }
            return func;
        }
        this.error("ER_FUNCTION_TOKEN_NOT_FOUND", null);
        return null;
    }
    
    private synchronized long getNextMethodId() {
        if (Compiler.s_nextMethodId == Long.MAX_VALUE) {
            Compiler.s_nextMethodId = 0L;
        }
        return Compiler.s_nextMethodId++;
    }
    
    private Expression compileExtension(int opPos) throws TransformerException {
        final int endExtFunc = opPos + this.getOp(opPos + 1) - 1;
        opPos = OpMap.getFirstChildPos(opPos);
        final java.lang.String ns = (java.lang.String)this.getTokenQueue().elementAt(this.getOp(opPos));
        ++opPos;
        final java.lang.String funcName = (java.lang.String)this.getTokenQueue().elementAt(this.getOp(opPos));
        ++opPos;
        final Function extension = new FuncExtFunction(ns, funcName, java.lang.String.valueOf(this.getNextMethodId()));
        try {
            int nextOpPos;
            for (int i = 0; opPos < endExtFunc; opPos = nextOpPos, ++i) {
                nextOpPos = this.getNextOpPos(opPos);
                extension.setArg(this.compile(opPos), i);
            }
        }
        catch (final WrongNumberArgsException ex) {}
        return extension;
    }
    
    public void warn(final java.lang.String msg, final Object[] args) throws TransformerException {
        final java.lang.String fmsg = XPATHMessages.createXPATHWarning(msg, args);
        if (null != this.m_errorHandler) {
            this.m_errorHandler.warning(new TransformerException(fmsg, this.m_locator));
        }
        else {
            System.out.println(fmsg + "; file " + this.m_locator.getSystemId() + "; line " + this.m_locator.getLineNumber() + "; column " + this.m_locator.getColumnNumber());
        }
    }
    
    public void assertion(final boolean b, final java.lang.String msg) {
        if (!b) {
            final java.lang.String fMsg = XPATHMessages.createXPATHMessage("ER_INCORRECT_PROGRAMMER_ASSERTION", new Object[] { msg });
            throw new RuntimeException(fMsg);
        }
    }
    
    @Override
    public void error(final java.lang.String msg, final Object[] args) throws TransformerException {
        final java.lang.String fmsg = XPATHMessages.createXPATHMessage(msg, args);
        if (null != this.m_errorHandler) {
            this.m_errorHandler.fatalError(new TransformerException(fmsg, this.m_locator));
            return;
        }
        throw new TransformerException(fmsg, this.m_locator);
    }
    
    public PrefixResolver getNamespaceContext() {
        return this.m_currentPrefixResolver;
    }
    
    public void setNamespaceContext(final PrefixResolver pr) {
        this.m_currentPrefixResolver = pr;
    }
    
    static {
        Compiler.s_nextMethodId = 0L;
    }
}
