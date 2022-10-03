package com.sun.org.apache.xerces.internal.impl.xpath;

import com.sun.org.apache.xerces.internal.util.XMLChar;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import java.util.Vector;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.util.SymbolTable;

public class XPath
{
    private static final boolean DEBUG_ALL = false;
    private static final boolean DEBUG_XPATH_PARSE = false;
    private static final boolean DEBUG_ANY = false;
    protected String fExpression;
    protected SymbolTable fSymbolTable;
    protected LocationPath[] fLocationPaths;
    
    public XPath(final String xpath, final SymbolTable symbolTable, final NamespaceContext context) throws XPathException {
        this.fExpression = xpath;
        this.fSymbolTable = symbolTable;
        this.parseExpression(context);
    }
    
    public LocationPath[] getLocationPaths() {
        final LocationPath[] ret = new LocationPath[this.fLocationPaths.length];
        for (int i = 0; i < this.fLocationPaths.length; ++i) {
            ret[i] = (LocationPath)this.fLocationPaths[i].clone();
        }
        return ret;
    }
    
    public LocationPath getLocationPath() {
        return (LocationPath)this.fLocationPaths[0].clone();
    }
    
    @Override
    public String toString() {
        final StringBuffer buf = new StringBuffer();
        for (int i = 0; i < this.fLocationPaths.length; ++i) {
            if (i > 0) {
                buf.append("|");
            }
            buf.append(this.fLocationPaths[i].toString());
        }
        return buf.toString();
    }
    
    private static void check(final boolean b) throws XPathException {
        if (!b) {
            throw new XPathException("c-general-xpath");
        }
    }
    
    private LocationPath buildLocationPath(final Vector stepsVector) throws XPathException {
        final int size = stepsVector.size();
        check(size != 0);
        final Step[] steps = new Step[size];
        stepsVector.copyInto(steps);
        stepsVector.removeAllElements();
        return new LocationPath(steps);
    }
    
    private void parseExpression(final NamespaceContext context) throws XPathException {
        final Tokens xtokens = new Tokens(this.fSymbolTable);
        final Scanner scanner = new Scanner(this.fSymbolTable) {
            @Override
            protected void addToken(final Tokens tokens, final int token) throws XPathException {
                if (token == 6 || token == 35 || token == 11 || token == 21 || token == 4 || token == 9 || token == 10 || token == 22 || token == 23 || token == 36 || token == 8) {
                    super.addToken(tokens, token);
                    return;
                }
                throw new XPathException("c-general-xpath");
            }
        };
        final int length = this.fExpression.length();
        final boolean success = scanner.scanExpr(this.fSymbolTable, xtokens, this.fExpression, 0, length);
        if (!success) {
            throw new XPathException("c-general-xpath");
        }
        final Vector stepsVector = new Vector();
        final Vector locationPathsVector = new Vector();
        boolean expectingStep = true;
        boolean expectingDoubleColon = false;
        while (xtokens.hasMore()) {
            final int token = xtokens.nextToken();
            switch (token) {
                case 23: {
                    check(!expectingStep);
                    locationPathsVector.addElement(this.buildLocationPath(stepsVector));
                    expectingStep = true;
                    continue;
                }
                case 6: {
                    check(expectingStep);
                    final Step step = new Step(new Axis((short)2), this.parseNodeTest(xtokens.nextToken(), xtokens, context));
                    stepsVector.addElement(step);
                    expectingStep = false;
                    continue;
                }
                case 9:
                case 10:
                case 11: {
                    check(expectingStep);
                    final Step step = new Step(new Axis((short)1), this.parseNodeTest(token, xtokens, context));
                    stepsVector.addElement(step);
                    expectingStep = false;
                    continue;
                }
                case 4: {
                    check(expectingStep);
                    expectingStep = false;
                    if (stepsVector.size() == 0) {
                        Axis axis = new Axis((short)3);
                        NodeTest nodeTest = new NodeTest((short)3);
                        Step step2 = new Step(axis, nodeTest);
                        stepsVector.addElement(step2);
                        if (!xtokens.hasMore() || xtokens.peekToken() != 22) {
                            continue;
                        }
                        xtokens.nextToken();
                        axis = new Axis((short)4);
                        nodeTest = new NodeTest((short)3);
                        step2 = new Step(axis, nodeTest);
                        stepsVector.addElement(step2);
                        expectingStep = true;
                        continue;
                    }
                    continue;
                }
                case 22: {
                    throw new XPathException("c-general-xpath");
                }
                case 21: {
                    check(!expectingStep);
                    expectingStep = true;
                    continue;
                }
                case 35: {
                    check(expectingStep);
                    expectingDoubleColon = true;
                    if (xtokens.nextToken() == 8) {
                        final Step step = new Step(new Axis((short)2), this.parseNodeTest(xtokens.nextToken(), xtokens, context));
                        stepsVector.addElement(step);
                        expectingStep = false;
                        expectingDoubleColon = false;
                        continue;
                    }
                    continue;
                }
                case 36: {
                    check(expectingStep);
                    expectingDoubleColon = true;
                    continue;
                }
                case 8: {
                    check(expectingStep);
                    check(expectingDoubleColon);
                    expectingDoubleColon = false;
                    continue;
                }
                default: {
                    throw new XPathException("c-general-xpath");
                }
            }
        }
        check(!expectingStep);
        locationPathsVector.addElement(this.buildLocationPath(stepsVector));
        locationPathsVector.copyInto(this.fLocationPaths = new LocationPath[locationPathsVector.size()]);
    }
    
    private NodeTest parseNodeTest(final int typeToken, final Tokens xtokens, final NamespaceContext context) throws XPathException {
        switch (typeToken) {
            case 9: {
                return new NodeTest((short)2);
            }
            case 10:
            case 11: {
                final String prefix = xtokens.nextTokenAsString();
                String uri = null;
                if (context != null && prefix != XMLSymbols.EMPTY_STRING) {
                    uri = context.getURI(prefix);
                }
                if (prefix != XMLSymbols.EMPTY_STRING && context != null && uri == null) {
                    throw new XPathException("c-general-xpath-ns");
                }
                if (typeToken == 10) {
                    return new NodeTest(prefix, uri);
                }
                final String localpart = xtokens.nextTokenAsString();
                final String rawname = (prefix != XMLSymbols.EMPTY_STRING) ? this.fSymbolTable.addSymbol(prefix + ':' + localpart) : localpart;
                return new NodeTest(new QName(prefix, localpart, rawname, uri));
            }
            default: {
                throw new XPathException("c-general-xpath");
            }
        }
    }
    
    public static void main(final String[] argv) throws Exception {
        for (int i = 0; i < argv.length; ++i) {
            final String expression = argv[i];
            System.out.println("# XPath expression: \"" + expression + '\"');
            try {
                final SymbolTable symbolTable = new SymbolTable();
                final XPath xpath = new XPath(expression, symbolTable, null);
                System.out.println("expanded xpath: \"" + xpath.toString() + '\"');
            }
            catch (final XPathException e) {
                System.out.println("error: " + e.getMessage());
            }
        }
    }
    
    public static class LocationPath implements Cloneable
    {
        public Step[] steps;
        
        public LocationPath(final Step[] steps) {
            this.steps = steps;
        }
        
        protected LocationPath(final LocationPath path) {
            this.steps = new Step[path.steps.length];
            for (int i = 0; i < this.steps.length; ++i) {
                this.steps[i] = (Step)path.steps[i].clone();
            }
        }
        
        @Override
        public String toString() {
            final StringBuffer str = new StringBuffer();
            for (int i = 0; i < this.steps.length; ++i) {
                if (i > 0 && this.steps[i - 1].axis.type != 4 && this.steps[i].axis.type != 4) {
                    str.append('/');
                }
                str.append(this.steps[i].toString());
            }
            return str.toString();
        }
        
        public Object clone() {
            return new LocationPath(this);
        }
    }
    
    public static class Step implements Cloneable
    {
        public Axis axis;
        public NodeTest nodeTest;
        
        public Step(final Axis axis, final NodeTest nodeTest) {
            this.axis = axis;
            this.nodeTest = nodeTest;
        }
        
        protected Step(final Step step) {
            this.axis = (Axis)step.axis.clone();
            this.nodeTest = (NodeTest)step.nodeTest.clone();
        }
        
        @Override
        public String toString() {
            if (this.axis.type == 3) {
                return ".";
            }
            if (this.axis.type == 2) {
                return "@" + this.nodeTest.toString();
            }
            if (this.axis.type == 1) {
                return this.nodeTest.toString();
            }
            if (this.axis.type == 4) {
                return "//";
            }
            return "??? (" + this.axis.type + ')';
        }
        
        public Object clone() {
            return new Step(this);
        }
    }
    
    public static class Axis implements Cloneable
    {
        public static final short CHILD = 1;
        public static final short ATTRIBUTE = 2;
        public static final short SELF = 3;
        public static final short DESCENDANT = 4;
        public short type;
        
        public Axis(final short type) {
            this.type = type;
        }
        
        protected Axis(final Axis axis) {
            this.type = axis.type;
        }
        
        @Override
        public String toString() {
            switch (this.type) {
                case 1: {
                    return "child";
                }
                case 2: {
                    return "attribute";
                }
                case 3: {
                    return "self";
                }
                case 4: {
                    return "descendant";
                }
                default: {
                    return "???";
                }
            }
        }
        
        public Object clone() {
            return new Axis(this);
        }
    }
    
    public static class NodeTest implements Cloneable
    {
        public static final short QNAME = 1;
        public static final short WILDCARD = 2;
        public static final short NODE = 3;
        public static final short NAMESPACE = 4;
        public short type;
        public final QName name;
        
        public NodeTest(final short type) {
            this.name = new QName();
            this.type = type;
        }
        
        public NodeTest(final QName name) {
            this.name = new QName();
            this.type = 1;
            this.name.setValues(name);
        }
        
        public NodeTest(final String prefix, final String uri) {
            this.name = new QName();
            this.type = 4;
            this.name.setValues(prefix, null, null, uri);
        }
        
        public NodeTest(final NodeTest nodeTest) {
            this.name = new QName();
            this.type = nodeTest.type;
            this.name.setValues(nodeTest.name);
        }
        
        @Override
        public String toString() {
            switch (this.type) {
                case 1: {
                    if (this.name.prefix.length() == 0) {
                        return this.name.localpart;
                    }
                    if (this.name.uri != null) {
                        return this.name.prefix + ':' + this.name.localpart;
                    }
                    return "{" + this.name.uri + '}' + this.name.prefix + ':' + this.name.localpart;
                }
                case 4: {
                    if (this.name.prefix.length() == 0) {
                        return "???:*";
                    }
                    if (this.name.uri != null) {
                        return this.name.prefix + ":*";
                    }
                    return "{" + this.name.uri + '}' + this.name.prefix + ":*";
                }
                case 2: {
                    return "*";
                }
                case 3: {
                    return "node()";
                }
                default: {
                    return "???";
                }
            }
        }
        
        public Object clone() {
            return new NodeTest(this);
        }
    }
    
    private static final class Tokens
    {
        static final boolean DUMP_TOKENS = false;
        public static final int EXPRTOKEN_OPEN_PAREN = 0;
        public static final int EXPRTOKEN_CLOSE_PAREN = 1;
        public static final int EXPRTOKEN_OPEN_BRACKET = 2;
        public static final int EXPRTOKEN_CLOSE_BRACKET = 3;
        public static final int EXPRTOKEN_PERIOD = 4;
        public static final int EXPRTOKEN_DOUBLE_PERIOD = 5;
        public static final int EXPRTOKEN_ATSIGN = 6;
        public static final int EXPRTOKEN_COMMA = 7;
        public static final int EXPRTOKEN_DOUBLE_COLON = 8;
        public static final int EXPRTOKEN_NAMETEST_ANY = 9;
        public static final int EXPRTOKEN_NAMETEST_NAMESPACE = 10;
        public static final int EXPRTOKEN_NAMETEST_QNAME = 11;
        public static final int EXPRTOKEN_NODETYPE_COMMENT = 12;
        public static final int EXPRTOKEN_NODETYPE_TEXT = 13;
        public static final int EXPRTOKEN_NODETYPE_PI = 14;
        public static final int EXPRTOKEN_NODETYPE_NODE = 15;
        public static final int EXPRTOKEN_OPERATOR_AND = 16;
        public static final int EXPRTOKEN_OPERATOR_OR = 17;
        public static final int EXPRTOKEN_OPERATOR_MOD = 18;
        public static final int EXPRTOKEN_OPERATOR_DIV = 19;
        public static final int EXPRTOKEN_OPERATOR_MULT = 20;
        public static final int EXPRTOKEN_OPERATOR_SLASH = 21;
        public static final int EXPRTOKEN_OPERATOR_DOUBLE_SLASH = 22;
        public static final int EXPRTOKEN_OPERATOR_UNION = 23;
        public static final int EXPRTOKEN_OPERATOR_PLUS = 24;
        public static final int EXPRTOKEN_OPERATOR_MINUS = 25;
        public static final int EXPRTOKEN_OPERATOR_EQUAL = 26;
        public static final int EXPRTOKEN_OPERATOR_NOT_EQUAL = 27;
        public static final int EXPRTOKEN_OPERATOR_LESS = 28;
        public static final int EXPRTOKEN_OPERATOR_LESS_EQUAL = 29;
        public static final int EXPRTOKEN_OPERATOR_GREATER = 30;
        public static final int EXPRTOKEN_OPERATOR_GREATER_EQUAL = 31;
        public static final int EXPRTOKEN_FUNCTION_NAME = 32;
        public static final int EXPRTOKEN_AXISNAME_ANCESTOR = 33;
        public static final int EXPRTOKEN_AXISNAME_ANCESTOR_OR_SELF = 34;
        public static final int EXPRTOKEN_AXISNAME_ATTRIBUTE = 35;
        public static final int EXPRTOKEN_AXISNAME_CHILD = 36;
        public static final int EXPRTOKEN_AXISNAME_DESCENDANT = 37;
        public static final int EXPRTOKEN_AXISNAME_DESCENDANT_OR_SELF = 38;
        public static final int EXPRTOKEN_AXISNAME_FOLLOWING = 39;
        public static final int EXPRTOKEN_AXISNAME_FOLLOWING_SIBLING = 40;
        public static final int EXPRTOKEN_AXISNAME_NAMESPACE = 41;
        public static final int EXPRTOKEN_AXISNAME_PARENT = 42;
        public static final int EXPRTOKEN_AXISNAME_PRECEDING = 43;
        public static final int EXPRTOKEN_AXISNAME_PRECEDING_SIBLING = 44;
        public static final int EXPRTOKEN_AXISNAME_SELF = 45;
        public static final int EXPRTOKEN_LITERAL = 46;
        public static final int EXPRTOKEN_NUMBER = 47;
        public static final int EXPRTOKEN_VARIABLE_REFERENCE = 48;
        private static final String[] fgTokenNames;
        private static final int INITIAL_TOKEN_COUNT = 256;
        private int[] fTokens;
        private int fTokenCount;
        private SymbolTable fSymbolTable;
        private Map<String, Integer> fSymbolMapping;
        private Map<Integer, String> fTokenNames;
        private int fCurrentTokenIndex;
        
        public Tokens(final SymbolTable symbolTable) {
            this.fTokens = new int[256];
            this.fTokenCount = 0;
            this.fSymbolMapping = new HashMap<String, Integer>();
            this.fTokenNames = new HashMap<Integer, String>();
            this.fSymbolTable = symbolTable;
            final String[] symbols = { "ancestor", "ancestor-or-self", "attribute", "child", "descendant", "descendant-or-self", "following", "following-sibling", "namespace", "parent", "preceding", "preceding-sibling", "self" };
            for (int i = 0; i < symbols.length; ++i) {
                this.fSymbolMapping.put(this.fSymbolTable.addSymbol(symbols[i]), i);
            }
            this.fTokenNames.put(0, "EXPRTOKEN_OPEN_PAREN");
            this.fTokenNames.put(1, "EXPRTOKEN_CLOSE_PAREN");
            this.fTokenNames.put(2, "EXPRTOKEN_OPEN_BRACKET");
            this.fTokenNames.put(3, "EXPRTOKEN_CLOSE_BRACKET");
            this.fTokenNames.put(4, "EXPRTOKEN_PERIOD");
            this.fTokenNames.put(5, "EXPRTOKEN_DOUBLE_PERIOD");
            this.fTokenNames.put(6, "EXPRTOKEN_ATSIGN");
            this.fTokenNames.put(7, "EXPRTOKEN_COMMA");
            this.fTokenNames.put(8, "EXPRTOKEN_DOUBLE_COLON");
            this.fTokenNames.put(9, "EXPRTOKEN_NAMETEST_ANY");
            this.fTokenNames.put(10, "EXPRTOKEN_NAMETEST_NAMESPACE");
            this.fTokenNames.put(11, "EXPRTOKEN_NAMETEST_QNAME");
            this.fTokenNames.put(12, "EXPRTOKEN_NODETYPE_COMMENT");
            this.fTokenNames.put(13, "EXPRTOKEN_NODETYPE_TEXT");
            this.fTokenNames.put(14, "EXPRTOKEN_NODETYPE_PI");
            this.fTokenNames.put(15, "EXPRTOKEN_NODETYPE_NODE");
            this.fTokenNames.put(16, "EXPRTOKEN_OPERATOR_AND");
            this.fTokenNames.put(17, "EXPRTOKEN_OPERATOR_OR");
            this.fTokenNames.put(18, "EXPRTOKEN_OPERATOR_MOD");
            this.fTokenNames.put(19, "EXPRTOKEN_OPERATOR_DIV");
            this.fTokenNames.put(20, "EXPRTOKEN_OPERATOR_MULT");
            this.fTokenNames.put(21, "EXPRTOKEN_OPERATOR_SLASH");
            this.fTokenNames.put(22, "EXPRTOKEN_OPERATOR_DOUBLE_SLASH");
            this.fTokenNames.put(23, "EXPRTOKEN_OPERATOR_UNION");
            this.fTokenNames.put(24, "EXPRTOKEN_OPERATOR_PLUS");
            this.fTokenNames.put(25, "EXPRTOKEN_OPERATOR_MINUS");
            this.fTokenNames.put(26, "EXPRTOKEN_OPERATOR_EQUAL");
            this.fTokenNames.put(27, "EXPRTOKEN_OPERATOR_NOT_EQUAL");
            this.fTokenNames.put(28, "EXPRTOKEN_OPERATOR_LESS");
            this.fTokenNames.put(29, "EXPRTOKEN_OPERATOR_LESS_EQUAL");
            this.fTokenNames.put(30, "EXPRTOKEN_OPERATOR_GREATER");
            this.fTokenNames.put(31, "EXPRTOKEN_OPERATOR_GREATER_EQUAL");
            this.fTokenNames.put(32, "EXPRTOKEN_FUNCTION_NAME");
            this.fTokenNames.put(33, "EXPRTOKEN_AXISNAME_ANCESTOR");
            this.fTokenNames.put(34, "EXPRTOKEN_AXISNAME_ANCESTOR_OR_SELF");
            this.fTokenNames.put(35, "EXPRTOKEN_AXISNAME_ATTRIBUTE");
            this.fTokenNames.put(36, "EXPRTOKEN_AXISNAME_CHILD");
            this.fTokenNames.put(37, "EXPRTOKEN_AXISNAME_DESCENDANT");
            this.fTokenNames.put(38, "EXPRTOKEN_AXISNAME_DESCENDANT_OR_SELF");
            this.fTokenNames.put(39, "EXPRTOKEN_AXISNAME_FOLLOWING");
            this.fTokenNames.put(40, "EXPRTOKEN_AXISNAME_FOLLOWING_SIBLING");
            this.fTokenNames.put(41, "EXPRTOKEN_AXISNAME_NAMESPACE");
            this.fTokenNames.put(42, "EXPRTOKEN_AXISNAME_PARENT");
            this.fTokenNames.put(43, "EXPRTOKEN_AXISNAME_PRECEDING");
            this.fTokenNames.put(44, "EXPRTOKEN_AXISNAME_PRECEDING_SIBLING");
            this.fTokenNames.put(45, "EXPRTOKEN_AXISNAME_SELF");
            this.fTokenNames.put(46, "EXPRTOKEN_LITERAL");
            this.fTokenNames.put(47, "EXPRTOKEN_NUMBER");
            this.fTokenNames.put(48, "EXPRTOKEN_VARIABLE_REFERENCE");
        }
        
        public String getTokenString(final int token) {
            return this.fTokenNames.get(token);
        }
        
        public void addToken(final String tokenStr) {
            Integer tokenInt = null;
            for (final Map.Entry<Integer, String> entry : this.fTokenNames.entrySet()) {
                if (entry.getValue().equals(tokenStr)) {
                    tokenInt = entry.getKey();
                }
            }
            if (tokenInt == null) {
                tokenInt = this.fTokenNames.size();
                this.fTokenNames.put(tokenInt, tokenStr);
            }
            this.addToken(tokenInt);
        }
        
        public void addToken(final int token) {
            try {
                this.fTokens[this.fTokenCount] = token;
            }
            catch (final ArrayIndexOutOfBoundsException ex) {
                final int[] oldList = this.fTokens;
                System.arraycopy(oldList, 0, this.fTokens = new int[this.fTokenCount << 1], 0, this.fTokenCount);
                this.fTokens[this.fTokenCount] = token;
            }
            ++this.fTokenCount;
        }
        
        public void rewind() {
            this.fCurrentTokenIndex = 0;
        }
        
        public boolean hasMore() {
            return this.fCurrentTokenIndex < this.fTokenCount;
        }
        
        public int nextToken() throws XPathException {
            if (this.fCurrentTokenIndex == this.fTokenCount) {
                throw new XPathException("c-general-xpath");
            }
            return this.fTokens[this.fCurrentTokenIndex++];
        }
        
        public int peekToken() throws XPathException {
            if (this.fCurrentTokenIndex == this.fTokenCount) {
                throw new XPathException("c-general-xpath");
            }
            return this.fTokens[this.fCurrentTokenIndex];
        }
        
        public String nextTokenAsString() throws XPathException {
            final String s = this.getTokenString(this.nextToken());
            if (s == null) {
                throw new XPathException("c-general-xpath");
            }
            return s;
        }
        
        public void dumpTokens() {
            for (int i = 0; i < this.fTokenCount; ++i) {
                switch (this.fTokens[i]) {
                    case 0: {
                        System.out.print("<OPEN_PAREN/>");
                        break;
                    }
                    case 1: {
                        System.out.print("<CLOSE_PAREN/>");
                        break;
                    }
                    case 2: {
                        System.out.print("<OPEN_BRACKET/>");
                        break;
                    }
                    case 3: {
                        System.out.print("<CLOSE_BRACKET/>");
                        break;
                    }
                    case 4: {
                        System.out.print("<PERIOD/>");
                        break;
                    }
                    case 5: {
                        System.out.print("<DOUBLE_PERIOD/>");
                        break;
                    }
                    case 6: {
                        System.out.print("<ATSIGN/>");
                        break;
                    }
                    case 7: {
                        System.out.print("<COMMA/>");
                        break;
                    }
                    case 8: {
                        System.out.print("<DOUBLE_COLON/>");
                        break;
                    }
                    case 9: {
                        System.out.print("<NAMETEST_ANY/>");
                        break;
                    }
                    case 10: {
                        System.out.print("<NAMETEST_NAMESPACE");
                        System.out.print(" prefix=\"" + this.getTokenString(this.fTokens[++i]) + "\"");
                        System.out.print("/>");
                        break;
                    }
                    case 11: {
                        System.out.print("<NAMETEST_QNAME");
                        if (this.fTokens[++i] != -1) {
                            System.out.print(" prefix=\"" + this.getTokenString(this.fTokens[i]) + "\"");
                        }
                        System.out.print(" localpart=\"" + this.getTokenString(this.fTokens[++i]) + "\"");
                        System.out.print("/>");
                        break;
                    }
                    case 12: {
                        System.out.print("<NODETYPE_COMMENT/>");
                        break;
                    }
                    case 13: {
                        System.out.print("<NODETYPE_TEXT/>");
                        break;
                    }
                    case 14: {
                        System.out.print("<NODETYPE_PI/>");
                        break;
                    }
                    case 15: {
                        System.out.print("<NODETYPE_NODE/>");
                        break;
                    }
                    case 16: {
                        System.out.print("<OPERATOR_AND/>");
                        break;
                    }
                    case 17: {
                        System.out.print("<OPERATOR_OR/>");
                        break;
                    }
                    case 18: {
                        System.out.print("<OPERATOR_MOD/>");
                        break;
                    }
                    case 19: {
                        System.out.print("<OPERATOR_DIV/>");
                        break;
                    }
                    case 20: {
                        System.out.print("<OPERATOR_MULT/>");
                        break;
                    }
                    case 21: {
                        System.out.print("<OPERATOR_SLASH/>");
                        if (i + 1 < this.fTokenCount) {
                            System.out.println();
                            System.out.print("  ");
                            break;
                        }
                        break;
                    }
                    case 22: {
                        System.out.print("<OPERATOR_DOUBLE_SLASH/>");
                        break;
                    }
                    case 23: {
                        System.out.print("<OPERATOR_UNION/>");
                        break;
                    }
                    case 24: {
                        System.out.print("<OPERATOR_PLUS/>");
                        break;
                    }
                    case 25: {
                        System.out.print("<OPERATOR_MINUS/>");
                        break;
                    }
                    case 26: {
                        System.out.print("<OPERATOR_EQUAL/>");
                        break;
                    }
                    case 27: {
                        System.out.print("<OPERATOR_NOT_EQUAL/>");
                        break;
                    }
                    case 28: {
                        System.out.print("<OPERATOR_LESS/>");
                        break;
                    }
                    case 29: {
                        System.out.print("<OPERATOR_LESS_EQUAL/>");
                        break;
                    }
                    case 30: {
                        System.out.print("<OPERATOR_GREATER/>");
                        break;
                    }
                    case 31: {
                        System.out.print("<OPERATOR_GREATER_EQUAL/>");
                        break;
                    }
                    case 32: {
                        System.out.print("<FUNCTION_NAME");
                        if (this.fTokens[++i] != -1) {
                            System.out.print(" prefix=\"" + this.getTokenString(this.fTokens[i]) + "\"");
                        }
                        System.out.print(" localpart=\"" + this.getTokenString(this.fTokens[++i]) + "\"");
                        System.out.print("/>");
                        break;
                    }
                    case 33: {
                        System.out.print("<AXISNAME_ANCESTOR/>");
                        break;
                    }
                    case 34: {
                        System.out.print("<AXISNAME_ANCESTOR_OR_SELF/>");
                        break;
                    }
                    case 35: {
                        System.out.print("<AXISNAME_ATTRIBUTE/>");
                        break;
                    }
                    case 36: {
                        System.out.print("<AXISNAME_CHILD/>");
                        break;
                    }
                    case 37: {
                        System.out.print("<AXISNAME_DESCENDANT/>");
                        break;
                    }
                    case 38: {
                        System.out.print("<AXISNAME_DESCENDANT_OR_SELF/>");
                        break;
                    }
                    case 39: {
                        System.out.print("<AXISNAME_FOLLOWING/>");
                        break;
                    }
                    case 40: {
                        System.out.print("<AXISNAME_FOLLOWING_SIBLING/>");
                        break;
                    }
                    case 41: {
                        System.out.print("<AXISNAME_NAMESPACE/>");
                        break;
                    }
                    case 42: {
                        System.out.print("<AXISNAME_PARENT/>");
                        break;
                    }
                    case 43: {
                        System.out.print("<AXISNAME_PRECEDING/>");
                        break;
                    }
                    case 44: {
                        System.out.print("<AXISNAME_PRECEDING_SIBLING/>");
                        break;
                    }
                    case 45: {
                        System.out.print("<AXISNAME_SELF/>");
                        break;
                    }
                    case 46: {
                        System.out.print("<LITERAL");
                        System.out.print(" value=\"" + this.getTokenString(this.fTokens[++i]) + "\"");
                        System.out.print("/>");
                        break;
                    }
                    case 47: {
                        System.out.print("<NUMBER");
                        System.out.print(" whole=\"" + this.getTokenString(this.fTokens[++i]) + "\"");
                        System.out.print(" part=\"" + this.getTokenString(this.fTokens[++i]) + "\"");
                        System.out.print("/>");
                        break;
                    }
                    case 48: {
                        System.out.print("<VARIABLE_REFERENCE");
                        if (this.fTokens[++i] != -1) {
                            System.out.print(" prefix=\"" + this.getTokenString(this.fTokens[i]) + "\"");
                        }
                        System.out.print(" localpart=\"" + this.getTokenString(this.fTokens[++i]) + "\"");
                        System.out.print("/>");
                        break;
                    }
                    default: {
                        System.out.println("<???/>");
                        break;
                    }
                }
            }
            System.out.println();
        }
        
        static {
            fgTokenNames = new String[] { "EXPRTOKEN_OPEN_PAREN", "EXPRTOKEN_CLOSE_PAREN", "EXPRTOKEN_OPEN_BRACKET", "EXPRTOKEN_CLOSE_BRACKET", "EXPRTOKEN_PERIOD", "EXPRTOKEN_DOUBLE_PERIOD", "EXPRTOKEN_ATSIGN", "EXPRTOKEN_COMMA", "EXPRTOKEN_DOUBLE_COLON", "EXPRTOKEN_NAMETEST_ANY", "EXPRTOKEN_NAMETEST_NAMESPACE", "EXPRTOKEN_NAMETEST_QNAME", "EXPRTOKEN_NODETYPE_COMMENT", "EXPRTOKEN_NODETYPE_TEXT", "EXPRTOKEN_NODETYPE_PI", "EXPRTOKEN_NODETYPE_NODE", "EXPRTOKEN_OPERATOR_AND", "EXPRTOKEN_OPERATOR_OR", "EXPRTOKEN_OPERATOR_MOD", "EXPRTOKEN_OPERATOR_DIV", "EXPRTOKEN_OPERATOR_MULT", "EXPRTOKEN_OPERATOR_SLASH", "EXPRTOKEN_OPERATOR_DOUBLE_SLASH", "EXPRTOKEN_OPERATOR_UNION", "EXPRTOKEN_OPERATOR_PLUS", "EXPRTOKEN_OPERATOR_MINUS", "EXPRTOKEN_OPERATOR_EQUAL", "EXPRTOKEN_OPERATOR_NOT_EQUAL", "EXPRTOKEN_OPERATOR_LESS", "EXPRTOKEN_OPERATOR_LESS_EQUAL", "EXPRTOKEN_OPERATOR_GREATER", "EXPRTOKEN_OPERATOR_GREATER_EQUAL", "EXPRTOKEN_FUNCTION_NAME", "EXPRTOKEN_AXISNAME_ANCESTOR", "EXPRTOKEN_AXISNAME_ANCESTOR_OR_SELF", "EXPRTOKEN_AXISNAME_ATTRIBUTE", "EXPRTOKEN_AXISNAME_CHILD", "EXPRTOKEN_AXISNAME_DESCENDANT", "EXPRTOKEN_AXISNAME_DESCENDANT_OR_SELF", "EXPRTOKEN_AXISNAME_FOLLOWING", "EXPRTOKEN_AXISNAME_FOLLOWING_SIBLING", "EXPRTOKEN_AXISNAME_NAMESPACE", "EXPRTOKEN_AXISNAME_PARENT", "EXPRTOKEN_AXISNAME_PRECEDING", "EXPRTOKEN_AXISNAME_PRECEDING_SIBLING", "EXPRTOKEN_AXISNAME_SELF", "EXPRTOKEN_LITERAL", "EXPRTOKEN_NUMBER", "EXPRTOKEN_VARIABLE_REFERENCE" };
        }
    }
    
    private static class Scanner
    {
        private static final byte CHARTYPE_INVALID = 0;
        private static final byte CHARTYPE_OTHER = 1;
        private static final byte CHARTYPE_WHITESPACE = 2;
        private static final byte CHARTYPE_EXCLAMATION = 3;
        private static final byte CHARTYPE_QUOTE = 4;
        private static final byte CHARTYPE_DOLLAR = 5;
        private static final byte CHARTYPE_OPEN_PAREN = 6;
        private static final byte CHARTYPE_CLOSE_PAREN = 7;
        private static final byte CHARTYPE_STAR = 8;
        private static final byte CHARTYPE_PLUS = 9;
        private static final byte CHARTYPE_COMMA = 10;
        private static final byte CHARTYPE_MINUS = 11;
        private static final byte CHARTYPE_PERIOD = 12;
        private static final byte CHARTYPE_SLASH = 13;
        private static final byte CHARTYPE_DIGIT = 14;
        private static final byte CHARTYPE_COLON = 15;
        private static final byte CHARTYPE_LESS = 16;
        private static final byte CHARTYPE_EQUAL = 17;
        private static final byte CHARTYPE_GREATER = 18;
        private static final byte CHARTYPE_ATSIGN = 19;
        private static final byte CHARTYPE_LETTER = 20;
        private static final byte CHARTYPE_OPEN_BRACKET = 21;
        private static final byte CHARTYPE_CLOSE_BRACKET = 22;
        private static final byte CHARTYPE_UNDERSCORE = 23;
        private static final byte CHARTYPE_UNION = 24;
        private static final byte CHARTYPE_NONASCII = 25;
        private static final byte[] fASCIICharMap;
        private SymbolTable fSymbolTable;
        private static final String fAndSymbol;
        private static final String fOrSymbol;
        private static final String fModSymbol;
        private static final String fDivSymbol;
        private static final String fCommentSymbol;
        private static final String fTextSymbol;
        private static final String fPISymbol;
        private static final String fNodeSymbol;
        private static final String fAncestorSymbol;
        private static final String fAncestorOrSelfSymbol;
        private static final String fAttributeSymbol;
        private static final String fChildSymbol;
        private static final String fDescendantSymbol;
        private static final String fDescendantOrSelfSymbol;
        private static final String fFollowingSymbol;
        private static final String fFollowingSiblingSymbol;
        private static final String fNamespaceSymbol;
        private static final String fParentSymbol;
        private static final String fPrecedingSymbol;
        private static final String fPrecedingSiblingSymbol;
        private static final String fSelfSymbol;
        
        public Scanner(final SymbolTable symbolTable) {
            this.fSymbolTable = symbolTable;
        }
        
        public boolean scanExpr(final SymbolTable symbolTable, final Tokens tokens, final String data, int currentOffset, final int endOffset) throws XPathException {
            boolean starIsMultiplyOperator = false;
        Label_2265:
            while (currentOffset != endOffset) {
                int ch;
                for (ch = data.charAt(currentOffset); (ch == 32 || ch == 10 || ch == 9 || ch == 13) && ++currentOffset != endOffset; ch = data.charAt(currentOffset)) {}
                if (currentOffset == endOffset) {
                    return true;
                }
                final byte chartype = (byte)((ch >= 128) ? 25 : Scanner.fASCIICharMap[ch]);
                switch (chartype) {
                    case 6: {
                        this.addToken(tokens, 0);
                        starIsMultiplyOperator = false;
                        if (++currentOffset == endOffset) {
                            continue;
                        }
                        continue;
                    }
                    case 7: {
                        this.addToken(tokens, 1);
                        starIsMultiplyOperator = true;
                        if (++currentOffset == endOffset) {
                            continue;
                        }
                        continue;
                    }
                    case 21: {
                        this.addToken(tokens, 2);
                        starIsMultiplyOperator = false;
                        if (++currentOffset == endOffset) {
                            continue;
                        }
                        continue;
                    }
                    case 22: {
                        this.addToken(tokens, 3);
                        starIsMultiplyOperator = true;
                        if (++currentOffset == endOffset) {
                            continue;
                        }
                        continue;
                    }
                    case 12: {
                        if (currentOffset + 1 == endOffset) {
                            this.addToken(tokens, 4);
                            starIsMultiplyOperator = true;
                            ++currentOffset;
                            continue;
                        }
                        ch = data.charAt(currentOffset + 1);
                        if (ch == 46) {
                            this.addToken(tokens, 5);
                            starIsMultiplyOperator = true;
                            currentOffset += 2;
                        }
                        else if (ch >= 48 && ch <= 57) {
                            this.addToken(tokens, 47);
                            starIsMultiplyOperator = true;
                            currentOffset = this.scanNumber(tokens, data, endOffset, currentOffset);
                        }
                        else if (ch == 47) {
                            this.addToken(tokens, 4);
                            starIsMultiplyOperator = true;
                            ++currentOffset;
                        }
                        else {
                            if (ch == 124) {
                                this.addToken(tokens, 4);
                                starIsMultiplyOperator = true;
                                ++currentOffset;
                                continue;
                            }
                            if (ch == 32 || ch == 10 || ch == 9 || ch == 13) {
                                while (true) {
                                    while (++currentOffset != endOffset) {
                                        ch = data.charAt(currentOffset);
                                        if (ch != 32 && ch != 10 && ch != 9 && ch != 13) {
                                            if (currentOffset == endOffset || ch == 124 || ch == 47) {
                                                this.addToken(tokens, 4);
                                                starIsMultiplyOperator = true;
                                                continue Label_2265;
                                            }
                                            throw new XPathException("c-general-xpath");
                                        }
                                    }
                                    continue;
                                }
                            }
                            throw new XPathException("c-general-xpath");
                        }
                        if (currentOffset == endOffset) {
                            continue;
                        }
                        continue;
                    }
                    case 19: {
                        this.addToken(tokens, 6);
                        starIsMultiplyOperator = false;
                        if (++currentOffset == endOffset) {
                            continue;
                        }
                        continue;
                    }
                    case 10: {
                        this.addToken(tokens, 7);
                        starIsMultiplyOperator = false;
                        if (++currentOffset == endOffset) {
                            continue;
                        }
                        continue;
                    }
                    case 15: {
                        if (++currentOffset == endOffset) {
                            return false;
                        }
                        ch = data.charAt(currentOffset);
                        if (ch != 58) {
                            return false;
                        }
                        this.addToken(tokens, 8);
                        starIsMultiplyOperator = false;
                        if (++currentOffset == endOffset) {
                            continue;
                        }
                        continue;
                    }
                    case 13: {
                        if (++currentOffset == endOffset) {
                            this.addToken(tokens, 21);
                            starIsMultiplyOperator = false;
                            continue;
                        }
                        ch = data.charAt(currentOffset);
                        if (ch != 47) {
                            this.addToken(tokens, 21);
                            starIsMultiplyOperator = false;
                            continue;
                        }
                        this.addToken(tokens, 22);
                        starIsMultiplyOperator = false;
                        if (++currentOffset == endOffset) {
                            continue;
                        }
                        continue;
                    }
                    case 24: {
                        this.addToken(tokens, 23);
                        starIsMultiplyOperator = false;
                        if (++currentOffset == endOffset) {
                            continue;
                        }
                        continue;
                    }
                    case 9: {
                        this.addToken(tokens, 24);
                        starIsMultiplyOperator = false;
                        if (++currentOffset == endOffset) {
                            continue;
                        }
                        continue;
                    }
                    case 11: {
                        this.addToken(tokens, 25);
                        starIsMultiplyOperator = false;
                        if (++currentOffset == endOffset) {
                            continue;
                        }
                        continue;
                    }
                    case 17: {
                        this.addToken(tokens, 26);
                        starIsMultiplyOperator = false;
                        if (++currentOffset == endOffset) {
                            continue;
                        }
                        continue;
                    }
                    case 3: {
                        if (++currentOffset == endOffset) {
                            return false;
                        }
                        ch = data.charAt(currentOffset);
                        if (ch != 61) {
                            return false;
                        }
                        this.addToken(tokens, 27);
                        starIsMultiplyOperator = false;
                        if (++currentOffset == endOffset) {
                            continue;
                        }
                        continue;
                    }
                    case 16: {
                        if (++currentOffset == endOffset) {
                            this.addToken(tokens, 28);
                            starIsMultiplyOperator = false;
                            continue;
                        }
                        ch = data.charAt(currentOffset);
                        if (ch != 61) {
                            this.addToken(tokens, 28);
                            starIsMultiplyOperator = false;
                            continue;
                        }
                        this.addToken(tokens, 29);
                        starIsMultiplyOperator = false;
                        if (++currentOffset == endOffset) {
                            continue;
                        }
                        continue;
                    }
                    case 18: {
                        if (++currentOffset == endOffset) {
                            this.addToken(tokens, 30);
                            starIsMultiplyOperator = false;
                            continue;
                        }
                        ch = data.charAt(currentOffset);
                        if (ch != 61) {
                            this.addToken(tokens, 30);
                            starIsMultiplyOperator = false;
                            continue;
                        }
                        this.addToken(tokens, 31);
                        starIsMultiplyOperator = false;
                        if (++currentOffset == endOffset) {
                            continue;
                        }
                        continue;
                    }
                    case 4: {
                        final int qchar = ch;
                        if (++currentOffset == endOffset) {
                            return false;
                        }
                        ch = data.charAt(currentOffset);
                        final int litOffset = currentOffset;
                        while (ch != qchar) {
                            if (++currentOffset == endOffset) {
                                return false;
                            }
                            ch = data.charAt(currentOffset);
                        }
                        final int litLength = currentOffset - litOffset;
                        this.addToken(tokens, 46);
                        starIsMultiplyOperator = true;
                        tokens.addToken(symbolTable.addSymbol(data.substring(litOffset, litOffset + litLength)));
                        if (++currentOffset == endOffset) {
                            continue;
                        }
                        continue;
                    }
                    case 14: {
                        this.addToken(tokens, 47);
                        starIsMultiplyOperator = true;
                        currentOffset = this.scanNumber(tokens, data, endOffset, currentOffset);
                        continue;
                    }
                    case 5: {
                        if (++currentOffset == endOffset) {
                            return false;
                        }
                        int nameOffset = currentOffset;
                        currentOffset = this.scanNCName(data, endOffset, currentOffset);
                        if (currentOffset == nameOffset) {
                            return false;
                        }
                        if (currentOffset < endOffset) {
                            ch = data.charAt(currentOffset);
                        }
                        else {
                            ch = -1;
                        }
                        String nameHandle = symbolTable.addSymbol(data.substring(nameOffset, currentOffset));
                        String prefixHandle;
                        if (ch != 58) {
                            prefixHandle = XMLSymbols.EMPTY_STRING;
                        }
                        else {
                            prefixHandle = nameHandle;
                            if (++currentOffset == endOffset) {
                                return false;
                            }
                            nameOffset = currentOffset;
                            currentOffset = this.scanNCName(data, endOffset, currentOffset);
                            if (currentOffset == nameOffset) {
                                return false;
                            }
                            if (currentOffset < endOffset) {
                                ch = data.charAt(currentOffset);
                            }
                            else {
                                ch = -1;
                            }
                            nameHandle = symbolTable.addSymbol(data.substring(nameOffset, currentOffset));
                        }
                        this.addToken(tokens, 48);
                        starIsMultiplyOperator = true;
                        tokens.addToken(prefixHandle);
                        tokens.addToken(nameHandle);
                        continue;
                    }
                    case 8: {
                        if (starIsMultiplyOperator) {
                            this.addToken(tokens, 20);
                            starIsMultiplyOperator = false;
                        }
                        else {
                            this.addToken(tokens, 9);
                            starIsMultiplyOperator = true;
                        }
                        if (++currentOffset == endOffset) {
                            continue;
                        }
                        continue;
                    }
                    case 20:
                    case 23:
                    case 25: {
                        int nameOffset = currentOffset;
                        currentOffset = this.scanNCName(data, endOffset, currentOffset);
                        if (currentOffset == nameOffset) {
                            return false;
                        }
                        if (currentOffset < endOffset) {
                            ch = data.charAt(currentOffset);
                        }
                        else {
                            ch = -1;
                        }
                        String nameHandle = symbolTable.addSymbol(data.substring(nameOffset, currentOffset));
                        boolean isNameTestNCName = false;
                        boolean isAxisName = false;
                        String prefixHandle = XMLSymbols.EMPTY_STRING;
                        if (ch == 58) {
                            if (++currentOffset == endOffset) {
                                return false;
                            }
                            ch = data.charAt(currentOffset);
                            if (ch == 42) {
                                if (++currentOffset < endOffset) {
                                    ch = data.charAt(currentOffset);
                                }
                                isNameTestNCName = true;
                            }
                            else if (ch == 58) {
                                if (++currentOffset < endOffset) {
                                    ch = data.charAt(currentOffset);
                                }
                                isAxisName = true;
                            }
                            else {
                                prefixHandle = nameHandle;
                                nameOffset = currentOffset;
                                currentOffset = this.scanNCName(data, endOffset, currentOffset);
                                if (currentOffset == nameOffset) {
                                    return false;
                                }
                                if (currentOffset < endOffset) {
                                    ch = data.charAt(currentOffset);
                                }
                                else {
                                    ch = -1;
                                }
                                nameHandle = symbolTable.addSymbol(data.substring(nameOffset, currentOffset));
                            }
                        }
                        while ((ch == 32 || ch == 10 || ch == 9 || ch == 13) && ++currentOffset != endOffset) {
                            ch = data.charAt(currentOffset);
                        }
                        if (starIsMultiplyOperator) {
                            if (nameHandle == Scanner.fAndSymbol) {
                                this.addToken(tokens, 16);
                                starIsMultiplyOperator = false;
                            }
                            else if (nameHandle == Scanner.fOrSymbol) {
                                this.addToken(tokens, 17);
                                starIsMultiplyOperator = false;
                            }
                            else if (nameHandle == Scanner.fModSymbol) {
                                this.addToken(tokens, 18);
                                starIsMultiplyOperator = false;
                            }
                            else {
                                if (nameHandle != Scanner.fDivSymbol) {
                                    return false;
                                }
                                this.addToken(tokens, 19);
                                starIsMultiplyOperator = false;
                            }
                            if (isNameTestNCName) {
                                return false;
                            }
                            if (isAxisName) {
                                return false;
                            }
                            continue;
                        }
                        else if (ch == 40 && !isNameTestNCName && !isAxisName) {
                            if (nameHandle == Scanner.fCommentSymbol) {
                                this.addToken(tokens, 12);
                            }
                            else if (nameHandle == Scanner.fTextSymbol) {
                                this.addToken(tokens, 13);
                            }
                            else if (nameHandle == Scanner.fPISymbol) {
                                this.addToken(tokens, 14);
                            }
                            else if (nameHandle == Scanner.fNodeSymbol) {
                                this.addToken(tokens, 15);
                            }
                            else {
                                this.addToken(tokens, 32);
                                tokens.addToken(prefixHandle);
                                tokens.addToken(nameHandle);
                            }
                            this.addToken(tokens, 0);
                            starIsMultiplyOperator = false;
                            if (++currentOffset == endOffset) {
                                continue;
                            }
                            continue;
                        }
                        else if (isAxisName || (ch == 58 && currentOffset + 1 < endOffset && data.charAt(currentOffset + 1) == ':')) {
                            if (nameHandle == Scanner.fAncestorSymbol) {
                                this.addToken(tokens, 33);
                            }
                            else if (nameHandle == Scanner.fAncestorOrSelfSymbol) {
                                this.addToken(tokens, 34);
                            }
                            else if (nameHandle == Scanner.fAttributeSymbol) {
                                this.addToken(tokens, 35);
                            }
                            else if (nameHandle == Scanner.fChildSymbol) {
                                this.addToken(tokens, 36);
                            }
                            else if (nameHandle == Scanner.fDescendantSymbol) {
                                this.addToken(tokens, 37);
                            }
                            else if (nameHandle == Scanner.fDescendantOrSelfSymbol) {
                                this.addToken(tokens, 38);
                            }
                            else if (nameHandle == Scanner.fFollowingSymbol) {
                                this.addToken(tokens, 39);
                            }
                            else if (nameHandle == Scanner.fFollowingSiblingSymbol) {
                                this.addToken(tokens, 40);
                            }
                            else if (nameHandle == Scanner.fNamespaceSymbol) {
                                this.addToken(tokens, 41);
                            }
                            else if (nameHandle == Scanner.fParentSymbol) {
                                this.addToken(tokens, 42);
                            }
                            else if (nameHandle == Scanner.fPrecedingSymbol) {
                                this.addToken(tokens, 43);
                            }
                            else if (nameHandle == Scanner.fPrecedingSiblingSymbol) {
                                this.addToken(tokens, 44);
                            }
                            else {
                                if (nameHandle != Scanner.fSelfSymbol) {
                                    return false;
                                }
                                this.addToken(tokens, 45);
                            }
                            if (isNameTestNCName) {
                                return false;
                            }
                            this.addToken(tokens, 8);
                            starIsMultiplyOperator = false;
                            if (isAxisName) {
                                continue;
                            }
                            ++currentOffset;
                            if (++currentOffset == endOffset) {
                                continue;
                            }
                            continue;
                        }
                        else {
                            if (isNameTestNCName) {
                                this.addToken(tokens, 10);
                                starIsMultiplyOperator = true;
                                tokens.addToken(nameHandle);
                                continue;
                            }
                            this.addToken(tokens, 11);
                            starIsMultiplyOperator = true;
                            tokens.addToken(prefixHandle);
                            tokens.addToken(nameHandle);
                            continue;
                        }
                        break;
                    }
                }
            }
            return true;
        }
        
        int scanNCName(final String data, final int endOffset, int currentOffset) {
            int ch = data.charAt(currentOffset);
            if (ch >= 128) {
                if (!XMLChar.isNameStart(ch)) {
                    return currentOffset;
                }
            }
            else {
                final byte chartype = Scanner.fASCIICharMap[ch];
                if (chartype != 20 && chartype != 23) {
                    return currentOffset;
                }
            }
            while (++currentOffset < endOffset) {
                ch = data.charAt(currentOffset);
                if (ch >= 128) {
                    if (!XMLChar.isName(ch)) {
                        break;
                    }
                    continue;
                }
                else {
                    final byte chartype = Scanner.fASCIICharMap[ch];
                    if (chartype != 20 && chartype != 14 && chartype != 12 && chartype != 11 && chartype != 23) {
                        break;
                    }
                    continue;
                }
            }
            return currentOffset;
        }
        
        private int scanNumber(final Tokens tokens, final String data, final int endOffset, int currentOffset) {
            int ch = data.charAt(currentOffset);
            int whole = 0;
            int part = 0;
            while (ch >= 48 && ch <= 57) {
                whole = whole * 10 + (ch - 48);
                if (++currentOffset == endOffset) {
                    break;
                }
                ch = data.charAt(currentOffset);
            }
            if (ch == 46 && ++currentOffset < endOffset) {
                for (ch = data.charAt(currentOffset); ch >= 48 && ch <= 57; ch = data.charAt(currentOffset)) {
                    part = part * 10 + (ch - 48);
                    if (++currentOffset == endOffset) {
                        break;
                    }
                }
                if (part != 0) {
                    throw new RuntimeException("find a solution!");
                }
            }
            tokens.addToken(whole);
            tokens.addToken(part);
            return currentOffset;
        }
        
        protected void addToken(final Tokens tokens, final int token) throws XPathException {
            tokens.addToken(token);
        }
        
        static {
            fASCIICharMap = new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 3, 4, 1, 5, 1, 1, 4, 6, 7, 8, 9, 10, 11, 12, 13, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 15, 1, 16, 17, 18, 1, 19, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 21, 1, 22, 1, 23, 1, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 1, 24, 1, 1, 1 };
            fAndSymbol = "and".intern();
            fOrSymbol = "or".intern();
            fModSymbol = "mod".intern();
            fDivSymbol = "div".intern();
            fCommentSymbol = "comment".intern();
            fTextSymbol = "text".intern();
            fPISymbol = "processing-instruction".intern();
            fNodeSymbol = "node".intern();
            fAncestorSymbol = "ancestor".intern();
            fAncestorOrSelfSymbol = "ancestor-or-self".intern();
            fAttributeSymbol = "attribute".intern();
            fChildSymbol = "child".intern();
            fDescendantSymbol = "descendant".intern();
            fDescendantOrSelfSymbol = "descendant-or-self".intern();
            fFollowingSymbol = "following".intern();
            fFollowingSiblingSymbol = "following-sibling".intern();
            fNamespaceSymbol = "namespace".intern();
            fParentSymbol = "parent".intern();
            fPrecedingSymbol = "preceding".intern();
            fPrecedingSiblingSymbol = "preceding-sibling".intern();
            fSelfSymbol = "self".intern();
        }
    }
}
