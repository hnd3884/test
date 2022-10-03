package org.apache.xerces.impl.xpath;

import java.io.Reader;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.NamespaceContext;

public class XPath20Parser
{
    public static final int EOF = 0;
    public static final int KEYWORD_AND = 2;
    public static final int KEYWORD_OR = 3;
    public static final int KEYWORD_CAST = 4;
    public static final int KEYWORD_AS = 5;
    public static final int SYMBOL_COLON = 6;
    public static final int SYMBOL_AT = 7;
    public static final int SYMBOL_QUESTION = 8;
    public static final int OPEN_PARAN = 9;
    public static final int CLOSE_PARAN = 10;
    public static final int SYMBOL_EQ = 11;
    public static final int SYMBOL_NE = 12;
    public static final int SYMBOL_LT = 13;
    public static final int SYMBOL_GT = 14;
    public static final int SYMBOL_LE = 15;
    public static final int SYMBOL_GE = 16;
    public static final int NUMERIC_LITERAL = 17;
    public static final int DIGITS = 18;
    public static final int NCNAME = 19;
    public static final int NCNAME_START_CHAR = 20;
    public static final int NCNAME_CHAR = 21;
    public static final int ESCAPE_QUOTE = 22;
    public static final int ESCAPE_APOS = 23;
    public static final int STRING_LITERAL = 24;
    public static final int DEFAULT = 0;
    String[] tokenImage;
    protected final NamespaceContext fNsContext;
    public XPath20ParserTokenManager tokenSource;
    SimpleCharStream inputStream;
    public Token token;
    public Token nextToken;
    private int nextTokenIndex;
    private int gen;
    private final int[] array1;
    
    public XPathSyntaxTreeNode parseExpression() throws XPathException {
        return this.Test();
    }
    
    private XPathSyntaxTreeNode Test() throws XPathException {
        final XPathSyntaxTreeNode orExpr = this.OrExpr();
        this.consumeToken(25);
        return orExpr;
    }
    
    private XPathSyntaxTreeNode OrExpr() throws XPathException {
        final XPathSyntaxTreeNode andExpr = this.AndExpr();
        switch ((this.nextTokenIndex == -1) ? this.nextToken() : this.nextTokenIndex) {
            case 3: {
                this.consumeToken(3);
                return new ConjunctionNode(0, andExpr, this.AndExpr());
            }
            default: {
                this.array1[0] = this.gen;
                return andExpr;
            }
        }
    }
    
    private XPathSyntaxTreeNode AndExpr() throws XPathException {
        final XPathSyntaxTreeNode booleanExpr = this.BooleanExpr();
        switch ((this.nextTokenIndex == -1) ? this.nextToken() : this.nextTokenIndex) {
            case 2: {
                this.consumeToken(2);
                return new ConjunctionNode(1, booleanExpr, this.BooleanExpr());
            }
            default: {
                this.array1[1] = this.gen;
                return booleanExpr;
            }
        }
    }
    
    private XPathSyntaxTreeNode BooleanExpr() throws XPathException {
        switch ((this.nextTokenIndex == -1) ? this.nextToken() : this.nextTokenIndex) {
            case 9: {
                this.consumeToken(9);
                final XPathSyntaxTreeNode orExpr = this.OrExpr();
                this.consumeToken(10);
                return orExpr;
            }
            case 19: {
                final QName qName = this.QName();
                this.consumeToken(9);
                if ("not".equals(qName.localpart) && "http://www.w3.org/2005/xpath-functions".equals(qName.uri)) {
                    final XPathSyntaxTreeNode orExpr2 = this.OrExpr();
                    this.consumeToken(10);
                    return new FunctionNode(qName, orExpr2);
                }
                final XPathSyntaxTreeNode simpleValue = this.SimpleValue();
                this.consumeToken(10);
                final CastNode castNode = new CastNode(simpleValue, qName);
                switch ((this.nextTokenIndex == -1) ? this.nextToken() : this.nextTokenIndex) {
                    case 11:
                    case 12:
                    case 13:
                    case 14:
                    case 15:
                    case 16: {
                        return new CompNode(this.Comparator(), castNode, this.ValueExpr());
                    }
                    default: {
                        this.array1[2] = this.gen;
                        return castNode;
                    }
                }
                break;
            }
            case 7:
            case 17:
            case 24: {
                final XPathSyntaxTreeNode castExpr = this.CastExpr();
                switch ((this.nextTokenIndex == -1) ? this.nextToken() : this.nextTokenIndex) {
                    case 11:
                    case 12:
                    case 13:
                    case 14:
                    case 15:
                    case 16: {
                        return new CompNode(this.Comparator(), castExpr, this.CastExpr());
                    }
                    default: {
                        this.array1[2] = this.gen;
                        return castExpr;
                    }
                }
                break;
            }
            default: {
                this.array1[3] = this.gen;
                this.consumeToken(-1);
                throw new XPathException("c-general-xpath");
            }
        }
    }
    
    private QName QName() throws XPathException {
        final Token consumeToken = this.consumeToken(19);
        QName qName = null;
        switch ((this.nextTokenIndex == -1) ? this.nextToken() : this.nextTokenIndex) {
            case 6: {
                this.consumeToken(6);
                final Token consumeToken2 = this.consumeToken(19);
                final String intern = consumeToken.image.intern();
                final String intern2 = consumeToken2.image.intern();
                qName = new QName(intern, intern2, intern + ':' + intern2, this.fNsContext.getURI(intern));
                break;
            }
            default: {
                final String intern3 = consumeToken.image.intern();
                qName = new QName(null, intern3, intern3, null);
                this.array1[4] = this.gen;
                break;
            }
        }
        return qName;
    }
    
    private int Comparator() throws XPathException {
        switch ((this.nextTokenIndex == -1) ? this.nextToken() : this.nextTokenIndex) {
            case 11: {
                this.consumeToken(11);
                return 0;
            }
            case 12: {
                this.consumeToken(12);
                return 1;
            }
            case 14: {
                this.consumeToken(14);
                return 3;
            }
            case 13: {
                this.consumeToken(13);
                return 2;
            }
            case 16: {
                this.consumeToken(16);
                return 5;
            }
            case 15: {
                this.consumeToken(15);
                return 4;
            }
            default: {
                this.array1[5] = this.gen;
                this.consumeToken(-1);
                throw new XPathException("c-general-xpath");
            }
        }
    }
    
    private XPathSyntaxTreeNode ValueExpr() throws XPathException {
        switch ((this.nextTokenIndex == -1) ? this.nextToken() : this.nextTokenIndex) {
            case 19: {
                final QName qName = this.QName();
                this.consumeToken(9);
                final XPathSyntaxTreeNode simpleValue = this.SimpleValue();
                this.consumeToken(10);
                return new CastNode(simpleValue, qName);
            }
            case 7:
            case 17:
            case 24: {
                return this.CastExpr();
            }
            default: {
                this.array1[3] = this.gen;
                this.consumeToken(-1);
                throw new XPathException("c-general-xpath");
            }
        }
    }
    
    private XPathSyntaxTreeNode CastExpr() throws XPathException {
        final XPathSyntaxTreeNode simpleValue = this.SimpleValue();
        switch ((this.nextTokenIndex == -1) ? this.nextToken() : this.nextTokenIndex) {
            case 4: {
                this.consumeToken(4);
                this.consumeToken(5);
                final QName qName = this.QName();
                switch ((this.nextTokenIndex == -1) ? this.nextToken() : this.nextTokenIndex) {
                    case 8: {
                        this.consumeToken(8);
                        break;
                    }
                    default: {
                        this.array1[6] = this.gen;
                        break;
                    }
                }
                return new CastNode(simpleValue, qName);
            }
            default: {
                this.array1[7] = this.gen;
                return simpleValue;
            }
        }
    }
    
    private XPathSyntaxTreeNode SimpleValue() throws XPathException {
        switch ((this.nextTokenIndex == -1) ? this.nextToken() : this.nextTokenIndex) {
            case 7: {
                return this.AttrName();
            }
            case 17:
            case 24: {
                return this.Literal();
            }
            default: {
                this.array1[8] = this.gen;
                this.consumeToken(-1);
                throw new XPathException("c-general-xpath");
            }
        }
    }
    
    private XPathSyntaxTreeNode AttrName() throws XPathException {
        this.consumeToken(7);
        return new AttrNode(this.NameTest());
    }
    
    private XPathSyntaxTreeNode Literal() throws XPathException {
        switch ((this.nextTokenIndex == -1) ? this.nextToken() : this.nextTokenIndex) {
            case 17: {
                return new LiteralNode(this.consumeToken(17).image, true);
            }
            case 24: {
                final Token consumeToken = this.consumeToken(24);
                return new LiteralNode(consumeToken.image.substring(1, consumeToken.image.length() - 1), false);
            }
            default: {
                this.array1[9] = this.gen;
                this.consumeToken(-1);
                throw new XPathException("c-general-xpath");
            }
        }
    }
    
    private QName NameTest() throws XPathException {
        return this.QName();
    }
    
    public XPath20Parser(final Reader reader, final NamespaceContext fNsContext) {
        this.tokenImage = new String[] { "<EOF>", "\" \"", "\"and\"", "\"or\"", "\"cast\"", "\"as\"", "\":\"", "\"@\"", "\"?\"", "\"(\"", "\")\"", "\"=\"", "\"!=\"", "\"<\"", "\">\"", "\"<=\"", "\">=\"", "<NUMERIC_LITERAL>", "<DIGITS>", "<NCNAME>", "<NCNAME_START_CHAR>", "<NCNAME_CHAR>", "\"\\\"\\\"\"", "\"\\'\\'\"", "<STRING_LITERAL>", "\"\\n\"" };
        this.array1 = new int[10];
        this.fNsContext = fNsContext;
        this.inputStream = new SimpleCharStream(reader, 1, 1);
        this.tokenSource = new XPath20ParserTokenManager(this.inputStream);
        this.token = new Token();
        this.nextTokenIndex = -1;
        this.gen = 0;
        for (int i = 0; i < 10; ++i) {
            this.array1[i] = -1;
        }
    }
    
    private Token consumeToken(final int n) throws XPathException {
        final Token token;
        if ((token = this.token).next != null) {
            this.token = this.token.next;
        }
        else {
            final Token token2 = this.token;
            final Token nextToken = this.tokenSource.getNextToken();
            token2.next = nextToken;
            this.token = nextToken;
        }
        this.nextTokenIndex = -1;
        if (this.token.kind == n) {
            ++this.gen;
            return this.token;
        }
        this.token = token;
        throw new XPathException("c-general-xpath");
    }
    
    private int nextToken() throws XPathException {
        final Token next = this.token.next;
        this.nextToken = next;
        if (next == null) {
            final Token token = this.token;
            final Token nextToken = this.tokenSource.getNextToken();
            token.next = nextToken;
            return this.nextTokenIndex = nextToken.kind;
        }
        return this.nextTokenIndex = this.nextToken.kind;
    }
}
