package org.htmlparser;

import org.htmlparser.util.DefaultParserFeedback;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.http.HttpHeader;
import java.net.HttpURLConnection;
import org.htmlparser.visitors.NodeVisitor;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.IteratorImpl;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.ParserException;
import java.net.URLConnection;
import org.htmlparser.lexer.Page;
import org.htmlparser.http.ConnectionManager;
import org.htmlparser.lexer.Lexer;
import org.htmlparser.util.ParserFeedback;
import org.htmlparser.http.ConnectionMonitor;
import java.io.Serializable;

public class Parser implements Serializable, ConnectionMonitor
{
    public static final double VERSION_NUMBER = 1.6;
    public static final String VERSION_TYPE = "Release Build";
    public static final String VERSION_DATE = "Jun 10, 2006";
    public static final String VERSION_STRING = "1.6 (Release Build Jun 10, 2006)";
    protected ParserFeedback mFeedback;
    protected Lexer mLexer;
    public static final ParserFeedback DEVNULL;
    public static final ParserFeedback STDOUT;
    
    public static String getVersion() {
        return "1.6 (Release Build Jun 10, 2006)";
    }
    
    public static double getVersionNumber() {
        return 1.6;
    }
    
    public static ConnectionManager getConnectionManager() {
        return Page.getConnectionManager();
    }
    
    public static void setConnectionManager(final ConnectionManager manager) {
        Page.setConnectionManager(manager);
    }
    
    public static Parser createParser(final String html, final String charset) {
        if (null == html) {
            throw new IllegalArgumentException("html cannot be null");
        }
        final Parser ret = new Parser(new Lexer(new Page(html, charset)));
        return ret;
    }
    
    public Parser() {
        this(new Lexer(new Page("")), Parser.DEVNULL);
    }
    
    public Parser(final Lexer lexer, final ParserFeedback fb) {
        this.setFeedback(fb);
        this.setLexer(lexer);
        this.setNodeFactory(new PrototypicalNodeFactory());
    }
    
    public Parser(final URLConnection connection, final ParserFeedback fb) throws ParserException {
        this(new Lexer(connection), fb);
    }
    
    public Parser(final String resource, final ParserFeedback feedback) throws ParserException {
        this.setFeedback(feedback);
        this.setResource(resource);
        this.setNodeFactory(new PrototypicalNodeFactory());
    }
    
    public Parser(final String resource) throws ParserException {
        this(resource, Parser.STDOUT);
    }
    
    public Parser(final Lexer lexer) {
        this(lexer, Parser.STDOUT);
    }
    
    public Parser(final URLConnection connection) throws ParserException {
        this(connection, Parser.STDOUT);
    }
    
    public void setResource(final String resource) throws ParserException {
        if (null == resource) {
            throw new IllegalArgumentException("resource cannot be null");
        }
        final int length = resource.length();
        boolean html = false;
        int i = 0;
        while (i < length) {
            final char ch = resource.charAt(i);
            if (!Character.isWhitespace(ch)) {
                if ('<' == ch) {
                    html = true;
                    break;
                }
                break;
            }
            else {
                ++i;
            }
        }
        if (html) {
            this.setLexer(new Lexer(new Page(resource)));
        }
        else {
            this.setLexer(new Lexer(getConnectionManager().openConnection(resource)));
        }
    }
    
    public void setConnection(final URLConnection connection) throws ParserException {
        if (null == connection) {
            throw new IllegalArgumentException("connection cannot be null");
        }
        this.setLexer(new Lexer(connection));
    }
    
    public URLConnection getConnection() {
        return this.getLexer().getPage().getConnection();
    }
    
    public void setURL(final String url) throws ParserException {
        if (null != url && !"".equals(url)) {
            this.setConnection(getConnectionManager().openConnection(url));
        }
    }
    
    public String getURL() {
        return this.getLexer().getPage().getUrl();
    }
    
    public void setEncoding(final String encoding) throws ParserException {
        this.getLexer().getPage().setEncoding(encoding);
    }
    
    public String getEncoding() {
        return this.getLexer().getPage().getEncoding();
    }
    
    public void setLexer(final Lexer lexer) {
        if (null == lexer) {
            throw new IllegalArgumentException("lexer cannot be null");
        }
        NodeFactory factory = null;
        if (null != this.getLexer()) {
            factory = this.getLexer().getNodeFactory();
        }
        if (null != factory) {
            lexer.setNodeFactory(factory);
        }
        this.mLexer = lexer;
        final String type = this.mLexer.getPage().getContentType();
        if (type != null && !type.startsWith("text")) {
            this.getFeedback().warning("URL " + this.mLexer.getPage().getUrl() + " does not contain text");
        }
    }
    
    public Lexer getLexer() {
        return this.mLexer;
    }
    
    public NodeFactory getNodeFactory() {
        return this.getLexer().getNodeFactory();
    }
    
    public void setNodeFactory(final NodeFactory factory) {
        if (null == factory) {
            throw new IllegalArgumentException("node factory cannot be null");
        }
        this.getLexer().setNodeFactory(factory);
    }
    
    public void setFeedback(final ParserFeedback fb) {
        if (null == fb) {
            this.mFeedback = Parser.DEVNULL;
        }
        else {
            this.mFeedback = fb;
        }
    }
    
    public ParserFeedback getFeedback() {
        return this.mFeedback;
    }
    
    public void reset() {
        this.getLexer().reset();
    }
    
    public NodeIterator elements() throws ParserException {
        return new IteratorImpl(this.getLexer(), this.getFeedback());
    }
    
    public NodeList parse(final NodeFilter filter) throws ParserException {
        final NodeList ret = new NodeList();
        final NodeIterator e = this.elements();
        while (e.hasMoreNodes()) {
            final Node node = e.nextNode();
            if (null != filter) {
                node.collectInto(ret, filter);
            }
            else {
                ret.add(node);
            }
        }
        return ret;
    }
    
    public void visitAllNodesWith(final NodeVisitor visitor) throws ParserException {
        visitor.beginParsing();
        final NodeIterator e = this.elements();
        while (e.hasMoreNodes()) {
            final Node node = e.nextNode();
            node.accept(visitor);
        }
        visitor.finishedParsing();
    }
    
    public void setInputHTML(final String inputHTML) throws ParserException {
        if (null == inputHTML) {
            throw new IllegalArgumentException("html cannot be null");
        }
        if (!"".equals(inputHTML)) {
            this.setLexer(new Lexer(new Page(inputHTML)));
        }
    }
    
    public NodeList extractAllNodesThatMatch(final NodeFilter filter) throws ParserException {
        final NodeList ret = new NodeList();
        final NodeIterator e = this.elements();
        while (e.hasMoreNodes()) {
            e.nextNode().collectInto(ret, filter);
        }
        return ret;
    }
    
    public void preConnect(final HttpURLConnection connection) throws ParserException {
        this.getFeedback().info(HttpHeader.getRequestHeader(connection));
    }
    
    public void postConnect(final HttpURLConnection connection) throws ParserException {
        this.getFeedback().info(HttpHeader.getResponseHeader(connection));
    }
    
    public static void main(final String[] args) {
        if (args.length < 1 || args[0].equals("-help")) {
            System.out.println("HTML Parser v" + getVersion() + "\n");
            System.out.println();
            System.out.println("Syntax : java -jar htmlparser.jar <file/page> [type]");
            System.out.println("   <file/page> the URL or file to be parsed");
            System.out.println("   type the node type, for example:");
            System.out.println("     A - Show only the link tags");
            System.out.println("     IMG - Show only the image tags");
            System.out.println("     TITLE - Show only the title tag");
            System.out.println();
            System.out.println("Example : java -jar htmlparser.jar http://www.yahoo.com");
            System.out.println();
        }
        else {
            try {
                final Parser parser = new Parser();
                NodeFilter filter;
                if (1 < args.length) {
                    filter = new TagNameFilter(args[1]);
                }
                else {
                    filter = null;
                    parser.setFeedback(Parser.STDOUT);
                    getConnectionManager().setMonitor(parser);
                }
                getConnectionManager().setRedirectionProcessingEnabled(true);
                getConnectionManager().setCookieProcessingEnabled(true);
                parser.setResource(args[0]);
                System.out.println(parser.parse(filter));
            }
            catch (final ParserException e) {
                e.printStackTrace();
            }
        }
    }
    
    static {
        DEVNULL = new DefaultParserFeedback(0);
        STDOUT = new DefaultParserFeedback();
        getConnectionManager();
        ConnectionManager.getDefaultRequestProperties().put("User-Agent", "HTMLParser/" + getVersionNumber());
    }
}
