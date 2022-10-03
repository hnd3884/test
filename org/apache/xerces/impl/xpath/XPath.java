package org.apache.xerces.impl.xpath;

import java.util.Hashtable;
import org.apache.xerces.util.XMLChar;
import org.apache.xerces.xni.QName;
import org.apache.xerces.util.XMLSymbols;
import java.util.ArrayList;
import java.util.Vector;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.util.SymbolTable;

public class XPath
{
    private static final boolean DEBUG_ALL = false;
    private static final boolean DEBUG_XPATH_PARSE = false;
    private static final boolean DEBUG_ANY = false;
    protected final String fExpression;
    protected final SymbolTable fSymbolTable;
    protected final LocationPath[] fLocationPaths;
    
    public XPath(final String fExpression, final SymbolTable fSymbolTable, final NamespaceContext namespaceContext) throws XPathException {
        this.fExpression = fExpression;
        this.fSymbolTable = fSymbolTable;
        this.fLocationPaths = this.parseExpression(namespaceContext);
    }
    
    public LocationPath[] getLocationPaths() {
        final LocationPath[] array = new LocationPath[this.fLocationPaths.length];
        for (int i = 0; i < this.fLocationPaths.length; ++i) {
            array[i] = (LocationPath)this.fLocationPaths[i].clone();
        }
        return array;
    }
    
    public LocationPath getLocationPath() {
        return (LocationPath)this.fLocationPaths[0].clone();
    }
    
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < this.fLocationPaths.length; ++i) {
            if (i > 0) {
                sb.append('|');
            }
            sb.append(this.fLocationPaths[i].toString());
        }
        return sb.toString();
    }
    
    private static void check(final boolean b) throws XPathException {
        if (!b) {
            throw new XPathException("c-general-xpath");
        }
    }
    
    private LocationPath buildLocationPath(final Vector vector) throws XPathException {
        final int size = vector.size();
        check(size != 0);
        final Step[] array = new Step[size];
        vector.copyInto(array);
        vector.removeAllElements();
        return new LocationPath(array);
    }
    
    private LocationPath[] parseExpression(final NamespaceContext namespaceContext) throws XPathException {
        final Tokens tokens = new Tokens(this.fSymbolTable);
        if (!new Scanner(this.fSymbolTable) {
            protected void addToken(final Tokens tokens, final int n) throws XPathException {
                if (n == 6 || n == 11 || n == 21 || n == 4 || n == 9 || n == 10 || n == 22 || n == 23 || n == 36 || n == 35 || n == 8) {
                    super.addToken(tokens, n);
                    return;
                }
                throw new XPathException("c-general-xpath");
            }
        }.scanExpr(this.fSymbolTable, tokens, this.fExpression, 0, this.fExpression.length())) {
            throw new XPathException("c-general-xpath");
        }
        final Vector vector = new Vector();
        final ArrayList list = new ArrayList();
        boolean b = true;
        while (tokens.hasMore()) {
            final int nextToken = tokens.nextToken();
            switch (nextToken) {
                case 23: {
                    check(!b);
                    list.add(this.buildLocationPath(vector));
                    b = true;
                    continue;
                }
                case 6: {
                    check(b);
                    vector.addElement(new Step(new Axis((short)2), this.parseNodeTest(tokens.nextToken(), tokens, namespaceContext)));
                    b = false;
                    continue;
                }
                case 35: {
                    check(b);
                    if (tokens.nextToken() != 8) {
                        throw new XPathException("c-general-xpath");
                    }
                    vector.addElement(new Step(new Axis((short)2), this.parseNodeTest(tokens.nextToken(), tokens, namespaceContext)));
                    b = false;
                    continue;
                }
                case 9:
                case 10:
                case 11: {
                    check(b);
                    vector.addElement(new Step(new Axis((short)1), this.parseNodeTest(nextToken, tokens, namespaceContext)));
                    b = false;
                    continue;
                }
                case 36: {
                    check(b);
                    if (tokens.nextToken() != 8) {
                        throw new XPathException("c-general-xpath");
                    }
                    vector.addElement(new Step(new Axis((short)1), this.parseNodeTest(tokens.nextToken(), tokens, namespaceContext)));
                    b = false;
                    continue;
                }
                case 4: {
                    check(b);
                    b = false;
                    if (vector.size() != 0) {
                        continue;
                    }
                    vector.addElement(new Step(new Axis((short)3), new NodeTest((short)3)));
                    if (tokens.hasMore() && tokens.peekToken() == 22) {
                        tokens.nextToken();
                        vector.addElement(new Step(new Axis((short)4), new NodeTest((short)3)));
                        b = true;
                        continue;
                    }
                    continue;
                }
                case 22: {
                    throw new XPathException("c-general-xpath");
                }
                case 8: {
                    throw new XPathException("c-general-xpath");
                }
                case 21: {
                    check(!b);
                    b = true;
                    continue;
                }
                default: {
                    throw new InternalError();
                }
            }
        }
        check(!b);
        list.add(this.buildLocationPath(vector));
        return list.toArray(new LocationPath[list.size()]);
    }
    
    private NodeTest parseNodeTest(final int n, final Tokens tokens, final NamespaceContext namespaceContext) throws XPathException {
        switch (n) {
            case 9: {
                return new NodeTest((short)2);
            }
            case 10:
            case 11: {
                final String nextTokenAsString = tokens.nextTokenAsString();
                String uri = null;
                if (namespaceContext != null && nextTokenAsString != XMLSymbols.EMPTY_STRING) {
                    uri = namespaceContext.getURI(nextTokenAsString);
                }
                if (nextTokenAsString != XMLSymbols.EMPTY_STRING && namespaceContext != null && uri == null) {
                    throw new XPathException("c-general-xpath-ns");
                }
                if (n == 10) {
                    return new NodeTest(nextTokenAsString, uri);
                }
                final String nextTokenAsString2 = tokens.nextTokenAsString();
                return new NodeTest(new QName(nextTokenAsString, nextTokenAsString2, (nextTokenAsString != XMLSymbols.EMPTY_STRING) ? this.fSymbolTable.addSymbol(nextTokenAsString + ':' + nextTokenAsString2) : nextTokenAsString2, uri));
            }
            default: {
                throw new XPathException("c-general-xpath");
            }
        }
    }
    
    public static void main(final String[] array) throws Exception {
        for (int i = 0; i < array.length; ++i) {
            final String s = array[i];
            System.out.println("# XPath expression: \"" + s + '\"');
            try {
                System.out.println("expanded xpath: \"" + new XPath(s, new SymbolTable(), null).toString() + '\"');
            }
            catch (final XPathException ex) {
                System.out.println("error: " + ex.getMessage());
            }
        }
    }
    
    public static class Axis implements Cloneable
    {
        public static final short CHILD = 1;
        public static final short ATTRIBUTE = 2;
        public static final short SELF = 3;
        public static final short DESCENDANT = 4;
        public final short type;
        
        public Axis(final short type) {
            this.type = type;
        }
        
        protected Axis(final Axis axis) {
            this.type = axis.type;
        }
        
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
    
    public static class LocationPath implements Cloneable
    {
        public final Step[] steps;
        
        public LocationPath(final Step[] steps) {
            this.steps = steps;
        }
        
        protected LocationPath(final LocationPath locationPath) {
            this.steps = new Step[locationPath.steps.length];
            for (int i = 0; i < this.steps.length; ++i) {
                this.steps[i] = (Step)locationPath.steps[i].clone();
            }
        }
        
        public String toString() {
            final StringBuffer sb = new StringBuffer();
            for (int i = 0; i < this.steps.length; ++i) {
                if (i > 0 && this.steps[i - 1].axis.type != 4 && this.steps[i].axis.type != 4) {
                    sb.append('/');
                }
                sb.append(this.steps[i].toString());
            }
            return sb.toString();
        }
        
        public Object clone() {
            return new LocationPath(this);
        }
    }
    
    public static class Step implements Cloneable
    {
        public final Axis axis;
        public final NodeTest nodeTest;
        
        public Step(final Axis axis, final NodeTest nodeTest) {
            this.axis = axis;
            this.nodeTest = nodeTest;
        }
        
        protected Step(final Step step) {
            this.axis = (Axis)step.axis.clone();
            this.nodeTest = (NodeTest)step.nodeTest.clone();
        }
        
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
    
    public static class NodeTest implements Cloneable
    {
        public static final short QNAME = 1;
        public static final short WILDCARD = 2;
        public static final short NODE = 3;
        public static final short NAMESPACE = 4;
        public final short type;
        public final QName name;
        
        public NodeTest(final short type) {
            this.name = new QName();
            this.type = type;
        }
        
        public NodeTest(final QName values) {
            this.name = new QName();
            this.type = 1;
            this.name.setValues(values);
        }
        
        public NodeTest(final String s, final String s2) {
            this.name = new QName();
            this.type = 4;
            this.name.setValues(s, null, null, s2);
        }
        
        public NodeTest(final NodeTest nodeTest) {
            this.name = new QName();
            this.type = nodeTest.type;
            this.name.setValues(nodeTest.name);
        }
        
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
        
        public Scanner(final SymbolTable fSymbolTable) {
            this.fSymbolTable = fSymbolTable;
        }
        
        public boolean scanExpr(final SymbolTable symbolTable, final Tokens tokens, final String s, int i, final int n) throws XPathException {
            int n2 = 0;
        Label_0003:
            while (true) {
                while (i != n) {
                    char c;
                    for (c = s.charAt(i); (c == ' ' || c == '\n' || c == '\t' || c == '\r') && ++i != n; c = s.charAt(i)) {}
                    if (i == n) {
                        return true;
                    }
                    switch ((c >= '\u0080') ? 25 : Scanner.fASCIICharMap[c]) {
                        case 6: {
                            this.addToken(tokens, 0);
                            n2 = 0;
                            if (++i == n) {
                                continue;
                            }
                            continue;
                        }
                        case 7: {
                            this.addToken(tokens, 1);
                            n2 = 1;
                            if (++i == n) {
                                continue;
                            }
                            continue;
                        }
                        case 21: {
                            this.addToken(tokens, 2);
                            n2 = 0;
                            if (++i == n) {
                                continue;
                            }
                            continue;
                        }
                        case 22: {
                            this.addToken(tokens, 3);
                            n2 = 1;
                            if (++i == n) {
                                continue;
                            }
                            continue;
                        }
                        case 12: {
                            if (i + 1 == n) {
                                this.addToken(tokens, 4);
                                n2 = 1;
                                ++i;
                                continue;
                            }
                            char c2 = s.charAt(i + 1);
                            if (c2 == '.') {
                                this.addToken(tokens, 5);
                                n2 = 1;
                                i += 2;
                            }
                            else if (c2 >= '0' && c2 <= '9') {
                                this.addToken(tokens, 47);
                                n2 = 1;
                                i = this.scanNumber(tokens, s, n, i);
                            }
                            else if (c2 == '/') {
                                this.addToken(tokens, 4);
                                n2 = 1;
                                ++i;
                            }
                            else {
                                if (c2 == '|') {
                                    this.addToken(tokens, 4);
                                    n2 = 1;
                                    ++i;
                                    continue;
                                }
                                if (c2 == ' ' || c2 == '\n' || c2 == '\t' || c2 == '\r') {
                                    while (true) {
                                        while (++i != n) {
                                            c2 = s.charAt(i);
                                            if (c2 != ' ' && c2 != '\n' && c2 != '\t' && c2 != '\r') {
                                                if (i == n || c2 == '|') {
                                                    this.addToken(tokens, 4);
                                                    n2 = 1;
                                                    continue Label_0003;
                                                }
                                                throw new XPathException("c-general-xpath");
                                            }
                                        }
                                        continue;
                                    }
                                }
                                throw new XPathException("c-general-xpath");
                            }
                            if (i == n) {
                                continue;
                            }
                            continue;
                        }
                        case 19: {
                            this.addToken(tokens, 6);
                            n2 = 0;
                            if (++i == n) {
                                continue;
                            }
                            continue;
                        }
                        case 10: {
                            this.addToken(tokens, 7);
                            n2 = 0;
                            if (++i == n) {
                                continue;
                            }
                            continue;
                        }
                        case 15: {
                            if (++i == n) {
                                return false;
                            }
                            if (s.charAt(i) != ':') {
                                return false;
                            }
                            this.addToken(tokens, 8);
                            n2 = 0;
                            if (++i == n) {
                                continue;
                            }
                            continue;
                        }
                        case 13: {
                            if (++i == n) {
                                this.addToken(tokens, 21);
                                n2 = 0;
                                continue;
                            }
                            if (s.charAt(i) != '/') {
                                this.addToken(tokens, 21);
                                n2 = 0;
                                continue;
                            }
                            this.addToken(tokens, 22);
                            n2 = 0;
                            if (++i == n) {
                                continue;
                            }
                            continue;
                        }
                        case 24: {
                            this.addToken(tokens, 23);
                            n2 = 0;
                            if (++i == n) {
                                continue;
                            }
                            continue;
                        }
                        case 9: {
                            this.addToken(tokens, 24);
                            n2 = 0;
                            if (++i == n) {
                                continue;
                            }
                            continue;
                        }
                        case 11: {
                            this.addToken(tokens, 25);
                            n2 = 0;
                            if (++i == n) {
                                continue;
                            }
                            continue;
                        }
                        case 17: {
                            this.addToken(tokens, 26);
                            n2 = 0;
                            if (++i == n) {
                                continue;
                            }
                            continue;
                        }
                        case 3: {
                            if (++i == n) {
                                return false;
                            }
                            if (s.charAt(i) != '=') {
                                return false;
                            }
                            this.addToken(tokens, 27);
                            n2 = 0;
                            if (++i == n) {
                                continue;
                            }
                            continue;
                        }
                        case 16: {
                            if (++i == n) {
                                this.addToken(tokens, 28);
                                n2 = 0;
                                continue;
                            }
                            if (s.charAt(i) != '=') {
                                this.addToken(tokens, 28);
                                n2 = 0;
                                continue;
                            }
                            this.addToken(tokens, 29);
                            n2 = 0;
                            if (++i == n) {
                                continue;
                            }
                            continue;
                        }
                        case 18: {
                            if (++i == n) {
                                this.addToken(tokens, 30);
                                n2 = 0;
                                continue;
                            }
                            if (s.charAt(i) != '=') {
                                this.addToken(tokens, 30);
                                n2 = 0;
                                continue;
                            }
                            this.addToken(tokens, 31);
                            n2 = 0;
                            if (++i == n) {
                                continue;
                            }
                            continue;
                        }
                        case 4: {
                            final char c3 = c;
                            if (++i == n) {
                                return false;
                            }
                            char c4 = s.charAt(i);
                            final int n3 = i;
                            while (c4 != c3) {
                                if (++i == n) {
                                    return false;
                                }
                                c4 = s.charAt(i);
                            }
                            final int n4 = i - n3;
                            this.addToken(tokens, 46);
                            n2 = 1;
                            tokens.addToken(symbolTable.addSymbol(s.substring(n3, n3 + n4)));
                            if (++i == n) {
                                continue;
                            }
                            continue;
                        }
                        case 14: {
                            this.addToken(tokens, 47);
                            n2 = 1;
                            i = this.scanNumber(tokens, s, n, i);
                            continue;
                        }
                        case 5: {
                            if (++i == n) {
                                return false;
                            }
                            final int n5 = i;
                            i = this.scanNCName(s, n, i);
                            if (i == n5) {
                                return false;
                            }
                            int char1;
                            if (i < n) {
                                char1 = s.charAt(i);
                            }
                            else {
                                char1 = -1;
                            }
                            String s2 = symbolTable.addSymbol(s.substring(n5, i));
                            String empty_STRING;
                            if (char1 != 58) {
                                empty_STRING = XMLSymbols.EMPTY_STRING;
                            }
                            else {
                                empty_STRING = s2;
                                if (++i == n) {
                                    return false;
                                }
                                final int n6 = i;
                                i = this.scanNCName(s, n, i);
                                if (i == n6) {
                                    return false;
                                }
                                if (i < n) {
                                    s.charAt(i);
                                }
                                s2 = symbolTable.addSymbol(s.substring(n6, i));
                            }
                            this.addToken(tokens, 48);
                            n2 = 1;
                            tokens.addToken(empty_STRING);
                            tokens.addToken(s2);
                            continue;
                        }
                        case 8: {
                            if (n2 != 0) {
                                this.addToken(tokens, 20);
                                n2 = 0;
                            }
                            else {
                                this.addToken(tokens, 9);
                                n2 = 1;
                            }
                            if (++i == n) {
                                continue;
                            }
                            continue;
                        }
                        case 20:
                        case 23:
                        case 25: {
                            final int n7 = i;
                            i = this.scanNCName(s, n, i);
                            if (i == n7) {
                                return false;
                            }
                            int n8;
                            if (i < n) {
                                n8 = s.charAt(i);
                            }
                            else {
                                n8 = -1;
                            }
                            String s3 = symbolTable.addSymbol(s.substring(n7, i));
                            boolean b = false;
                            boolean b2 = false;
                            String empty_STRING2 = XMLSymbols.EMPTY_STRING;
                            if (n8 == 58) {
                                if (++i == n) {
                                    return false;
                                }
                                n8 = s.charAt(i);
                                if (n8 == 42) {
                                    if (++i < n) {
                                        n8 = s.charAt(i);
                                    }
                                    b = true;
                                }
                                else if (n8 == 58) {
                                    if (++i < n) {
                                        n8 = s.charAt(i);
                                    }
                                    b2 = true;
                                }
                                else {
                                    empty_STRING2 = s3;
                                    final int n9 = i;
                                    i = this.scanNCName(s, n, i);
                                    if (i == n9) {
                                        return false;
                                    }
                                    if (i < n) {
                                        n8 = s.charAt(i);
                                    }
                                    else {
                                        n8 = -1;
                                    }
                                    s3 = symbolTable.addSymbol(s.substring(n9, i));
                                }
                            }
                            while ((n8 == 32 || n8 == 10 || n8 == 9 || n8 == 13) && ++i != n) {
                                n8 = s.charAt(i);
                            }
                            if (n2 != 0) {
                                if (s3 == Scanner.fAndSymbol) {
                                    this.addToken(tokens, 16);
                                    n2 = 0;
                                }
                                else if (s3 == Scanner.fOrSymbol) {
                                    this.addToken(tokens, 17);
                                    n2 = 0;
                                }
                                else if (s3 == Scanner.fModSymbol) {
                                    this.addToken(tokens, 18);
                                    n2 = 0;
                                }
                                else {
                                    if (s3 != Scanner.fDivSymbol) {
                                        return false;
                                    }
                                    this.addToken(tokens, 19);
                                    n2 = 0;
                                }
                                if (b) {
                                    return false;
                                }
                                if (b2) {
                                    return false;
                                }
                                continue;
                            }
                            else if (n8 == 40 && !b && !b2) {
                                if (s3 == Scanner.fCommentSymbol) {
                                    this.addToken(tokens, 12);
                                }
                                else if (s3 == Scanner.fTextSymbol) {
                                    this.addToken(tokens, 13);
                                }
                                else if (s3 == Scanner.fPISymbol) {
                                    this.addToken(tokens, 14);
                                }
                                else if (s3 == Scanner.fNodeSymbol) {
                                    this.addToken(tokens, 15);
                                }
                                else {
                                    this.addToken(tokens, 32);
                                    tokens.addToken(empty_STRING2);
                                    tokens.addToken(s3);
                                }
                                this.addToken(tokens, 0);
                                n2 = 0;
                                if (++i == n) {
                                    continue;
                                }
                                continue;
                            }
                            else if (b2 || (n8 == 58 && i + 1 < n && s.charAt(i + 1) == ':')) {
                                if (s3 == Scanner.fAncestorSymbol) {
                                    this.addToken(tokens, 33);
                                }
                                else if (s3 == Scanner.fAncestorOrSelfSymbol) {
                                    this.addToken(tokens, 34);
                                }
                                else if (s3 == Scanner.fAttributeSymbol) {
                                    this.addToken(tokens, 35);
                                }
                                else if (s3 == Scanner.fChildSymbol) {
                                    this.addToken(tokens, 36);
                                }
                                else if (s3 == Scanner.fDescendantSymbol) {
                                    this.addToken(tokens, 37);
                                }
                                else if (s3 == Scanner.fDescendantOrSelfSymbol) {
                                    this.addToken(tokens, 38);
                                }
                                else if (s3 == Scanner.fFollowingSymbol) {
                                    this.addToken(tokens, 39);
                                }
                                else if (s3 == Scanner.fFollowingSiblingSymbol) {
                                    this.addToken(tokens, 40);
                                }
                                else if (s3 == Scanner.fNamespaceSymbol) {
                                    this.addToken(tokens, 41);
                                }
                                else if (s3 == Scanner.fParentSymbol) {
                                    this.addToken(tokens, 42);
                                }
                                else if (s3 == Scanner.fPrecedingSymbol) {
                                    this.addToken(tokens, 43);
                                }
                                else if (s3 == Scanner.fPrecedingSiblingSymbol) {
                                    this.addToken(tokens, 44);
                                }
                                else {
                                    if (s3 != Scanner.fSelfSymbol) {
                                        return false;
                                    }
                                    this.addToken(tokens, 45);
                                }
                                if (b) {
                                    return false;
                                }
                                this.addToken(tokens, 8);
                                n2 = 0;
                                if (b2) {
                                    continue;
                                }
                                ++i;
                                if (++i == n) {
                                    continue;
                                }
                                continue;
                            }
                            else {
                                if (b) {
                                    this.addToken(tokens, 10);
                                    n2 = 1;
                                    tokens.addToken(s3);
                                    continue;
                                }
                                this.addToken(tokens, 11);
                                n2 = 1;
                                tokens.addToken(empty_STRING2);
                                tokens.addToken(s3);
                                continue;
                            }
                            break;
                        }
                        default: {
                            return false;
                        }
                    }
                }
                break;
            }
            return true;
        }
        
        int scanNCName(final String s, final int n, int n2) {
            final char char1 = s.charAt(n2);
            if (char1 >= '\u0080') {
                if (!XMLChar.isNameStart(char1)) {
                    return n2;
                }
            }
            else {
                final byte b = Scanner.fASCIICharMap[char1];
                if (b != 20 && b != 23) {
                    return n2;
                }
            }
            while (++n2 < n) {
                final char char2 = s.charAt(n2);
                if (char2 >= '\u0080') {
                    if (!XMLChar.isName(char2)) {
                        break;
                    }
                    continue;
                }
                else {
                    final byte b2 = Scanner.fASCIICharMap[char2];
                    if (b2 != 20 && b2 != 14 && b2 != 12 && b2 != 11 && b2 != 23) {
                        break;
                    }
                    continue;
                }
            }
            return n2;
        }
        
        private int scanNumber(final Tokens tokens, final String s, final int n, int n2) {
            char c = s.charAt(n2);
            int n3 = 0;
            int n4 = 0;
            while (c >= '0' && c <= '9') {
                n3 = n3 * 10 + (c - '0');
                if (++n2 == n) {
                    break;
                }
                c = s.charAt(n2);
            }
            if (c == '.' && ++n2 < n) {
                for (char c2 = s.charAt(n2); c2 >= '0' && c2 <= '9'; c2 = s.charAt(n2)) {
                    n4 = n4 * 10 + (c2 - '0');
                    if (++n2 == n) {
                        break;
                    }
                }
                if (n4 != 0) {
                    throw new RuntimeException("find a solution!");
                }
            }
            tokens.addToken(n3);
            tokens.addToken(n4);
            return n2;
        }
        
        protected void addToken(final Tokens tokens, final int n) throws XPathException {
            tokens.addToken(n);
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
        private Hashtable fSymbolMapping;
        private Hashtable fTokenNames;
        private int fCurrentTokenIndex;
        
        public Tokens(final SymbolTable fSymbolTable) {
            this.fTokens = new int[256];
            this.fTokenCount = 0;
            this.fSymbolMapping = new Hashtable();
            this.fTokenNames = new Hashtable();
            this.fSymbolTable = fSymbolTable;
            final String[] array = { "ancestor", "ancestor-or-self", "attribute", "child", "descendant", "descendant-or-self", "following", "following-sibling", "namespace", "parent", "preceding", "preceding-sibling", "self" };
            for (int i = 0; i < array.length; ++i) {
                this.fSymbolMapping.put(this.fSymbolTable.addSymbol(array[i]), new Integer(i));
            }
            this.fTokenNames.put(new Integer(0), "EXPRTOKEN_OPEN_PAREN");
            this.fTokenNames.put(new Integer(1), "EXPRTOKEN_CLOSE_PAREN");
            this.fTokenNames.put(new Integer(2), "EXPRTOKEN_OPEN_BRACKET");
            this.fTokenNames.put(new Integer(3), "EXPRTOKEN_CLOSE_BRACKET");
            this.fTokenNames.put(new Integer(4), "EXPRTOKEN_PERIOD");
            this.fTokenNames.put(new Integer(5), "EXPRTOKEN_DOUBLE_PERIOD");
            this.fTokenNames.put(new Integer(6), "EXPRTOKEN_ATSIGN");
            this.fTokenNames.put(new Integer(7), "EXPRTOKEN_COMMA");
            this.fTokenNames.put(new Integer(8), "EXPRTOKEN_DOUBLE_COLON");
            this.fTokenNames.put(new Integer(9), "EXPRTOKEN_NAMETEST_ANY");
            this.fTokenNames.put(new Integer(10), "EXPRTOKEN_NAMETEST_NAMESPACE");
            this.fTokenNames.put(new Integer(11), "EXPRTOKEN_NAMETEST_QNAME");
            this.fTokenNames.put(new Integer(12), "EXPRTOKEN_NODETYPE_COMMENT");
            this.fTokenNames.put(new Integer(13), "EXPRTOKEN_NODETYPE_TEXT");
            this.fTokenNames.put(new Integer(14), "EXPRTOKEN_NODETYPE_PI");
            this.fTokenNames.put(new Integer(15), "EXPRTOKEN_NODETYPE_NODE");
            this.fTokenNames.put(new Integer(16), "EXPRTOKEN_OPERATOR_AND");
            this.fTokenNames.put(new Integer(17), "EXPRTOKEN_OPERATOR_OR");
            this.fTokenNames.put(new Integer(18), "EXPRTOKEN_OPERATOR_MOD");
            this.fTokenNames.put(new Integer(19), "EXPRTOKEN_OPERATOR_DIV");
            this.fTokenNames.put(new Integer(20), "EXPRTOKEN_OPERATOR_MULT");
            this.fTokenNames.put(new Integer(21), "EXPRTOKEN_OPERATOR_SLASH");
            this.fTokenNames.put(new Integer(22), "EXPRTOKEN_OPERATOR_DOUBLE_SLASH");
            this.fTokenNames.put(new Integer(23), "EXPRTOKEN_OPERATOR_UNION");
            this.fTokenNames.put(new Integer(24), "EXPRTOKEN_OPERATOR_PLUS");
            this.fTokenNames.put(new Integer(25), "EXPRTOKEN_OPERATOR_MINUS");
            this.fTokenNames.put(new Integer(26), "EXPRTOKEN_OPERATOR_EQUAL");
            this.fTokenNames.put(new Integer(27), "EXPRTOKEN_OPERATOR_NOT_EQUAL");
            this.fTokenNames.put(new Integer(28), "EXPRTOKEN_OPERATOR_LESS");
            this.fTokenNames.put(new Integer(29), "EXPRTOKEN_OPERATOR_LESS_EQUAL");
            this.fTokenNames.put(new Integer(30), "EXPRTOKEN_OPERATOR_GREATER");
            this.fTokenNames.put(new Integer(31), "EXPRTOKEN_OPERATOR_GREATER_EQUAL");
            this.fTokenNames.put(new Integer(32), "EXPRTOKEN_FUNCTION_NAME");
            this.fTokenNames.put(new Integer(33), "EXPRTOKEN_AXISNAME_ANCESTOR");
            this.fTokenNames.put(new Integer(34), "EXPRTOKEN_AXISNAME_ANCESTOR_OR_SELF");
            this.fTokenNames.put(new Integer(35), "EXPRTOKEN_AXISNAME_ATTRIBUTE");
            this.fTokenNames.put(new Integer(36), "EXPRTOKEN_AXISNAME_CHILD");
            this.fTokenNames.put(new Integer(37), "EXPRTOKEN_AXISNAME_DESCENDANT");
            this.fTokenNames.put(new Integer(38), "EXPRTOKEN_AXISNAME_DESCENDANT_OR_SELF");
            this.fTokenNames.put(new Integer(39), "EXPRTOKEN_AXISNAME_FOLLOWING");
            this.fTokenNames.put(new Integer(40), "EXPRTOKEN_AXISNAME_FOLLOWING_SIBLING");
            this.fTokenNames.put(new Integer(41), "EXPRTOKEN_AXISNAME_NAMESPACE");
            this.fTokenNames.put(new Integer(42), "EXPRTOKEN_AXISNAME_PARENT");
            this.fTokenNames.put(new Integer(43), "EXPRTOKEN_AXISNAME_PRECEDING");
            this.fTokenNames.put(new Integer(44), "EXPRTOKEN_AXISNAME_PRECEDING_SIBLING");
            this.fTokenNames.put(new Integer(45), "EXPRTOKEN_AXISNAME_SELF");
            this.fTokenNames.put(new Integer(46), "EXPRTOKEN_LITERAL");
            this.fTokenNames.put(new Integer(47), "EXPRTOKEN_NUMBER");
            this.fTokenNames.put(new Integer(48), "EXPRTOKEN_VARIABLE_REFERENCE");
        }
        
        public String getTokenString(final int n) {
            return this.fTokenNames.get(new Integer(n));
        }
        
        public void addToken(final String s) {
            Integer n = this.fTokenNames.get(s);
            if (n == null) {
                n = new Integer(this.fTokenNames.size());
                this.fTokenNames.put(n, s);
            }
            this.addToken(n);
        }
        
        public void addToken(final int n) {
            try {
                this.fTokens[this.fTokenCount] = n;
            }
            catch (final ArrayIndexOutOfBoundsException ex) {
                System.arraycopy(this.fTokens, 0, this.fTokens = new int[this.fTokenCount << 1], 0, this.fTokenCount);
                this.fTokens[this.fTokenCount] = n;
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
            final String tokenString = this.getTokenString(this.nextToken());
            if (tokenString == null) {
                throw new XPathException("c-general-xpath");
            }
            return tokenString;
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
}
