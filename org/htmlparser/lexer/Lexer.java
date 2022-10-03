package org.htmlparser.lexer;

import java.net.MalformedURLException;
import org.htmlparser.http.ConnectionManager;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.Tag;
import java.util.Vector;
import org.htmlparser.nodes.RemarkNode;
import org.htmlparser.Remark;
import org.htmlparser.nodes.TextNode;
import org.htmlparser.Text;
import org.htmlparser.Node;
import org.htmlparser.util.ParserException;
import java.net.URLConnection;
import org.htmlparser.NodeFactory;
import java.io.Serializable;

public class Lexer implements Serializable, NodeFactory
{
    public static final double VERSION_NUMBER = 1.6;
    public static final String VERSION_TYPE = "Release Build";
    public static final String VERSION_DATE = "Jun 10, 2006";
    public static final String VERSION_STRING = "1.6 (Release Build Jun 10, 2006)";
    public static boolean STRICT_REMARKS;
    protected Page mPage;
    protected Cursor mCursor;
    protected NodeFactory mFactory;
    protected static int mDebugLineTrigger;
    
    public static String getVersion() {
        return "1.6 (Release Build Jun 10, 2006)";
    }
    
    public Lexer() {
        this(new Page(""));
    }
    
    public Lexer(final Page page) {
        this.setPage(page);
        this.setCursor(new Cursor(page, 0));
        this.setNodeFactory(this);
    }
    
    public Lexer(final String text) {
        this(new Page(text));
    }
    
    public Lexer(final URLConnection connection) throws ParserException {
        this(new Page(connection));
    }
    
    public Page getPage() {
        return this.mPage;
    }
    
    public void setPage(final Page page) {
        if (null == page) {
            throw new IllegalArgumentException("page cannot be null");
        }
        this.mPage = page;
    }
    
    public Cursor getCursor() {
        return this.mCursor;
    }
    
    public void setCursor(final Cursor cursor) {
        if (null == cursor) {
            throw new IllegalArgumentException("cursor cannot be null");
        }
        this.mCursor = cursor;
    }
    
    public NodeFactory getNodeFactory() {
        return this.mFactory;
    }
    
    public void setNodeFactory(final NodeFactory factory) {
        if (null == factory) {
            throw new IllegalArgumentException("node factory cannot be null");
        }
        this.mFactory = factory;
    }
    
    public int getPosition() {
        return this.getCursor().getPosition();
    }
    
    public void setPosition(final int position) {
        this.getCursor().setPosition(position);
    }
    
    public int getCurrentLineNumber() {
        return this.getPage().row(this.getCursor());
    }
    
    public String getCurrentLine() {
        return this.getPage().getLine(this.getCursor());
    }
    
    public void reset() {
        this.getPage().reset();
        this.setCursor(new Cursor(this.getPage(), 0));
    }
    
    public Node nextNode() throws ParserException {
        return this.nextNode(false);
    }
    
    public Node nextNode(final boolean quotesmart) throws ParserException {
        if (-1 != Lexer.mDebugLineTrigger) {
            final Page page = this.getPage();
            final int lineno = page.row(this.mCursor);
            if (Lexer.mDebugLineTrigger < lineno) {
                Lexer.mDebugLineTrigger = lineno + 1;
            }
        }
        final int start = this.mCursor.getPosition();
        char ch = this.mPage.getCharacter(this.mCursor);
        Node ret = null;
        switch (ch) {
            case '\uffff': {
                ret = null;
                break;
            }
            case '<': {
                ch = this.mPage.getCharacter(this.mCursor);
                if ('\uffff' == ch) {
                    ret = this.makeString(start, this.mCursor.getPosition());
                    break;
                }
                if ('%' == ch) {
                    this.mPage.ungetCharacter(this.mCursor);
                    ret = this.parseJsp(start);
                    break;
                }
                if ('?' == ch) {
                    this.mPage.ungetCharacter(this.mCursor);
                    ret = this.parsePI(start);
                    break;
                }
                if ('/' == ch || '%' == ch || Character.isLetter(ch)) {
                    this.mPage.ungetCharacter(this.mCursor);
                    ret = this.parseTag(start);
                    break;
                }
                if ('!' != ch) {
                    ret = this.parseString(start, quotesmart);
                    break;
                }
                ch = this.mPage.getCharacter(this.mCursor);
                if ('\uffff' == ch) {
                    ret = this.makeString(start, this.mCursor.getPosition());
                    break;
                }
                if ('>' == ch) {
                    ret = this.makeRemark(start, this.mCursor.getPosition());
                    break;
                }
                this.mPage.ungetCharacter(this.mCursor);
                if ('-' == ch) {
                    ret = this.parseRemark(start, quotesmart);
                    break;
                }
                this.mPage.ungetCharacter(this.mCursor);
                ret = this.parseTag(start);
                break;
            }
            default: {
                this.mPage.ungetCharacter(this.mCursor);
                ret = this.parseString(start, quotesmart);
                break;
            }
        }
        return ret;
    }
    
    public Node parseCDATA() throws ParserException {
        return this.parseCDATA(false);
    }
    
    public Node parseCDATA(final boolean quotesmart) throws ParserException {
        final int start = this.mCursor.getPosition();
        int state = 0;
        boolean done = false;
        char quote = '\0';
        boolean comment = false;
        while (!done) {
            char ch = this.mPage.getCharacter(this.mCursor);
            switch (state) {
                case 0: {
                    switch (ch) {
                        case '\uffff': {
                            done = true;
                            continue;
                        }
                        case '\'': {
                            if (!quotesmart || comment) {
                                continue;
                            }
                            if ('\0' == quote) {
                                quote = '\'';
                                continue;
                            }
                            if ('\'' == quote) {
                                quote = '\0';
                                continue;
                            }
                            continue;
                        }
                        case '\"': {
                            if (!quotesmart || comment) {
                                continue;
                            }
                            if ('\0' == quote) {
                                quote = '\"';
                                continue;
                            }
                            if ('\"' == quote) {
                                quote = '\0';
                                continue;
                            }
                            continue;
                        }
                        case '\\': {
                            if (!quotesmart || '\0' == quote) {
                                continue;
                            }
                            ch = this.mPage.getCharacter(this.mCursor);
                            if ('\uffff' == ch) {
                                done = true;
                                continue;
                            }
                            if (ch != '\\' && ch != quote) {
                                this.mPage.ungetCharacter(this.mCursor);
                                continue;
                            }
                            continue;
                        }
                        case '/': {
                            if (!quotesmart || '\0' != quote) {
                                continue;
                            }
                            ch = this.mPage.getCharacter(this.mCursor);
                            if ('\uffff' == ch) {
                                done = true;
                                continue;
                            }
                            if ('/' == ch) {
                                comment = true;
                                continue;
                            }
                            if ('*' == ch) {
                                while (true) {
                                    ch = this.mPage.getCharacter(this.mCursor);
                                    if ('\uffff' == ch || '*' == ch) {
                                        ch = this.mPage.getCharacter(this.mCursor);
                                        if (ch == '*') {
                                            this.mPage.ungetCharacter(this.mCursor);
                                        }
                                        if ('\uffff' == ch) {
                                            break;
                                        }
                                        if ('/' == ch) {
                                            break;
                                        }
                                        continue;
                                    }
                                }
                                continue;
                            }
                            this.mPage.ungetCharacter(this.mCursor);
                            continue;
                        }
                        case '\n': {
                            comment = false;
                            continue;
                        }
                        case '<': {
                            if (!quotesmart) {
                                state = 1;
                                continue;
                            }
                            if ('\0' == quote) {
                                state = 1;
                                continue;
                            }
                            continue;
                        }
                        default: {
                            continue;
                        }
                    }
                    break;
                }
                case 1: {
                    switch (ch) {
                        case '\uffff': {
                            done = true;
                            continue;
                        }
                        case '/': {
                            state = 2;
                            continue;
                        }
                        case '!': {
                            ch = this.mPage.getCharacter(this.mCursor);
                            if ('\uffff' == ch) {
                                done = true;
                                continue;
                            }
                            if ('-' != ch) {
                                state = 0;
                                continue;
                            }
                            ch = this.mPage.getCharacter(this.mCursor);
                            if ('\uffff' == ch) {
                                done = true;
                                continue;
                            }
                            if ('-' == ch) {
                                state = 3;
                                continue;
                            }
                            state = 0;
                            continue;
                        }
                        default: {
                            state = 0;
                            continue;
                        }
                    }
                    break;
                }
                case 2: {
                    comment = false;
                    if ('\uffff' == ch) {
                        done = true;
                        continue;
                    }
                    if (Character.isLetter(ch)) {
                        done = true;
                        this.mPage.ungetCharacter(this.mCursor);
                        this.mPage.ungetCharacter(this.mCursor);
                        this.mPage.ungetCharacter(this.mCursor);
                        continue;
                    }
                    state = 0;
                    continue;
                }
                case 3: {
                    comment = false;
                    if ('\uffff' == ch) {
                        done = true;
                        continue;
                    }
                    if ('-' != ch) {
                        continue;
                    }
                    ch = this.mPage.getCharacter(this.mCursor);
                    if ('\uffff' == ch) {
                        done = true;
                        continue;
                    }
                    if ('-' != ch) {
                        this.mPage.ungetCharacter(this.mCursor);
                        continue;
                    }
                    ch = this.mPage.getCharacter(this.mCursor);
                    if ('\uffff' == ch) {
                        done = true;
                        continue;
                    }
                    if ('>' == ch) {
                        state = 0;
                        continue;
                    }
                    this.mPage.ungetCharacter(this.mCursor);
                    this.mPage.ungetCharacter(this.mCursor);
                    continue;
                }
                default: {
                    throw new IllegalStateException("how the fuck did we get in state " + state);
                }
            }
        }
        final int end = this.mCursor.getPosition();
        return this.makeString(start, end);
    }
    
    public Text createStringNode(final Page page, final int start, final int end) {
        return new TextNode(page, start, end);
    }
    
    public Remark createRemarkNode(final Page page, final int start, final int end) {
        return new RemarkNode(page, start, end);
    }
    
    public Tag createTagNode(final Page page, final int start, final int end, final Vector attributes) {
        return new TagNode(page, start, end, attributes);
    }
    
    protected void scanJIS(final Cursor cursor) throws ParserException {
        boolean done = false;
        int state = 0;
        while (!done) {
            final char ch = this.mPage.getCharacter(cursor);
            if ('\uffff' == ch) {
                done = true;
            }
            else {
                switch (state) {
                    case 0: {
                        if ('\u001b' == ch) {
                            state = 1;
                            continue;
                        }
                        continue;
                    }
                    case 1: {
                        if ('(' == ch) {
                            state = 2;
                            continue;
                        }
                        state = 0;
                        continue;
                    }
                    case 2: {
                        if ('J' == ch) {
                            done = true;
                            continue;
                        }
                        state = 0;
                        continue;
                    }
                    default: {
                        throw new IllegalStateException("state " + state);
                    }
                }
            }
        }
    }
    
    protected Node parseString(final int start, final boolean quotesmart) throws ParserException {
        boolean done = false;
        char quote = '\0';
        while (!done) {
            char ch = this.mPage.getCharacter(this.mCursor);
            if ('\uffff' == ch) {
                done = true;
            }
            else if ('\u001b' == ch) {
                ch = this.mPage.getCharacter(this.mCursor);
                if ('\uffff' == ch) {
                    done = true;
                }
                else if ('$' == ch) {
                    ch = this.mPage.getCharacter(this.mCursor);
                    if ('\uffff' == ch) {
                        done = true;
                    }
                    else if ('B' == ch) {
                        this.scanJIS(this.mCursor);
                    }
                    else {
                        this.mPage.ungetCharacter(this.mCursor);
                        this.mPage.ungetCharacter(this.mCursor);
                    }
                }
                else {
                    this.mPage.ungetCharacter(this.mCursor);
                }
            }
            else if (quotesmart && '\0' == quote && ('\'' == ch || '\"' == ch)) {
                quote = ch;
            }
            else if (quotesmart && '\0' != quote && '\\' == ch) {
                ch = this.mPage.getCharacter(this.mCursor);
                if ('\uffff' == ch || '\\' == ch || ch == quote) {
                    continue;
                }
                this.mPage.ungetCharacter(this.mCursor);
            }
            else if (quotesmart && ch == quote) {
                quote = '\0';
            }
            else if (quotesmart && '\0' == quote && ch == '/') {
                ch = this.mPage.getCharacter(this.mCursor);
                if ('\uffff' == ch) {
                    done = true;
                }
                else if ('/' == ch) {
                    do {
                        ch = this.mPage.getCharacter(this.mCursor);
                        if ('\uffff' != ch) {
                            continue;
                        }
                        break;
                    } while ('\n' != ch);
                }
                else if ('*' == ch) {
                    while (true) {
                        ch = this.mPage.getCharacter(this.mCursor);
                        if ('\uffff' == ch || '*' == ch) {
                            ch = this.mPage.getCharacter(this.mCursor);
                            if (ch == '*') {
                                this.mPage.ungetCharacter(this.mCursor);
                            }
                            if ('\uffff' == ch) {
                                break;
                            }
                            if ('/' == ch) {
                                break;
                            }
                            continue;
                        }
                    }
                }
                else {
                    this.mPage.ungetCharacter(this.mCursor);
                }
            }
            else {
                if ('\0' != quote || '<' != ch) {
                    continue;
                }
                ch = this.mPage.getCharacter(this.mCursor);
                if ('\uffff' == ch) {
                    done = true;
                }
                else if ('/' == ch || Character.isLetter(ch) || '!' == ch || '%' == ch || '?' == ch) {
                    done = true;
                    this.mPage.ungetCharacter(this.mCursor);
                    this.mPage.ungetCharacter(this.mCursor);
                }
                else {
                    this.mPage.ungetCharacter(this.mCursor);
                }
            }
        }
        return this.makeString(start, this.mCursor.getPosition());
    }
    
    protected Node makeString(final int start, final int end) throws ParserException {
        final int length = end - start;
        Node ret;
        if (0 != length) {
            ret = this.getNodeFactory().createStringNode(this.getPage(), start, end);
        }
        else {
            ret = null;
        }
        return ret;
    }
    
    private void whitespace(final Vector attributes, final int[] bookmarks) {
        if (bookmarks[1] > bookmarks[0]) {
            attributes.addElement(new PageAttribute(this.mPage, -1, -1, bookmarks[0], bookmarks[1], '\0'));
        }
    }
    
    private void standalone(final Vector attributes, final int[] bookmarks) {
        attributes.addElement(new PageAttribute(this.mPage, bookmarks[1], bookmarks[2], -1, -1, '\0'));
    }
    
    private void empty(final Vector attributes, final int[] bookmarks) {
        attributes.addElement(new PageAttribute(this.mPage, bookmarks[1], bookmarks[2], bookmarks[2] + 1, -1, '\0'));
    }
    
    private void naked(final Vector attributes, final int[] bookmarks) {
        attributes.addElement(new PageAttribute(this.mPage, bookmarks[1], bookmarks[2], bookmarks[3], bookmarks[4], '\0'));
    }
    
    private void single_quote(final Vector attributes, final int[] bookmarks) {
        attributes.addElement(new PageAttribute(this.mPage, bookmarks[1], bookmarks[2], bookmarks[4] + 1, bookmarks[5], '\''));
    }
    
    private void double_quote(final Vector attributes, final int[] bookmarks) {
        attributes.addElement(new PageAttribute(this.mPage, bookmarks[1], bookmarks[2], bookmarks[5] + 1, bookmarks[6], '\"'));
    }
    
    protected Node parseTag(final int start) throws ParserException {
        boolean done = false;
        final Vector attributes = new Vector();
        int state = 0;
        final int[] bookmarks = new int[8];
        bookmarks[0] = this.mCursor.getPosition();
        while (!done) {
            bookmarks[state + 1] = this.mCursor.getPosition();
            final char ch = this.mPage.getCharacter(this.mCursor);
            switch (state) {
                case 0: {
                    if ('\uffff' == ch || '>' == ch || '<' == ch) {
                        if ('<' == ch) {
                            this.mPage.ungetCharacter(this.mCursor);
                            bookmarks[state + 1] = this.mCursor.getPosition();
                        }
                        this.whitespace(attributes, bookmarks);
                        done = true;
                        continue;
                    }
                    if (!Character.isWhitespace(ch)) {
                        this.whitespace(attributes, bookmarks);
                        state = 1;
                        continue;
                    }
                    continue;
                }
                case 1: {
                    if ('\uffff' == ch || '>' == ch || '<' == ch) {
                        if ('<' == ch) {
                            this.mPage.ungetCharacter(this.mCursor);
                            bookmarks[state + 1] = this.mCursor.getPosition();
                        }
                        this.standalone(attributes, bookmarks);
                        done = true;
                        continue;
                    }
                    if (Character.isWhitespace(ch)) {
                        bookmarks[6] = bookmarks[2];
                        state = 6;
                        continue;
                    }
                    if ('=' == ch) {
                        state = 2;
                        continue;
                    }
                    continue;
                }
                case 2: {
                    if ('\uffff' == ch || '>' == ch) {
                        this.empty(attributes, bookmarks);
                        done = true;
                        continue;
                    }
                    if ('\'' == ch) {
                        state = 4;
                        bookmarks[4] = bookmarks[3];
                        continue;
                    }
                    if ('\"' == ch) {
                        state = 5;
                        bookmarks[5] = bookmarks[3];
                        continue;
                    }
                    if (Character.isWhitespace(ch)) {
                        continue;
                    }
                    state = 3;
                    continue;
                }
                case 3: {
                    if ('\uffff' == ch || '>' == ch) {
                        this.naked(attributes, bookmarks);
                        done = true;
                        continue;
                    }
                    if (Character.isWhitespace(ch)) {
                        this.naked(attributes, bookmarks);
                        bookmarks[0] = bookmarks[4];
                        state = 0;
                        continue;
                    }
                    continue;
                }
                case 4: {
                    if ('\uffff' == ch) {
                        this.single_quote(attributes, bookmarks);
                        done = true;
                        continue;
                    }
                    if ('\'' == ch) {
                        this.single_quote(attributes, bookmarks);
                        bookmarks[0] = bookmarks[5] + 1;
                        state = 0;
                        continue;
                    }
                    continue;
                }
                case 5: {
                    if ('\uffff' == ch) {
                        this.double_quote(attributes, bookmarks);
                        done = true;
                        continue;
                    }
                    if ('\"' == ch) {
                        this.double_quote(attributes, bookmarks);
                        bookmarks[0] = bookmarks[6] + 1;
                        state = 0;
                        continue;
                    }
                    continue;
                }
                case 6: {
                    if ('\uffff' == ch) {
                        this.standalone(attributes, bookmarks);
                        bookmarks[0] = bookmarks[6];
                        this.mPage.ungetCharacter(this.mCursor);
                        state = 0;
                        continue;
                    }
                    if (Character.isWhitespace(ch)) {
                        continue;
                    }
                    if ('=' == ch) {
                        bookmarks[2] = bookmarks[6];
                        bookmarks[3] = bookmarks[7];
                        state = 2;
                        continue;
                    }
                    this.standalone(attributes, bookmarks);
                    bookmarks[0] = bookmarks[6];
                    this.mPage.ungetCharacter(this.mCursor);
                    state = 0;
                    continue;
                }
                default: {
                    throw new IllegalStateException("how the fuck did we get in state " + state);
                }
            }
        }
        return this.makeTag(start, this.mCursor.getPosition(), attributes);
    }
    
    protected Node makeTag(final int start, final int end, final Vector attributes) throws ParserException {
        final int length = end - start;
        Node ret;
        if (0 != length) {
            if (2 > length) {
                return this.makeString(start, end);
            }
            ret = this.getNodeFactory().createTagNode(this.getPage(), start, end, attributes);
        }
        else {
            ret = null;
        }
        return ret;
    }
    
    protected Node parseRemark(final int start, final boolean quotesmart) throws ParserException {
        boolean done = false;
        int state = 0;
        while (!done) {
            char ch = this.mPage.getCharacter(this.mCursor);
            if ('\uffff' == ch) {
                done = true;
            }
            else {
                switch (state) {
                    case 0: {
                        if ('>' == ch) {
                            done = true;
                        }
                        if ('-' == ch) {
                            state = 1;
                            continue;
                        }
                        return this.parseString(start, quotesmart);
                    }
                    case 1: {
                        if ('-' != ch) {
                            return this.parseString(start, quotesmart);
                        }
                        ch = this.mPage.getCharacter(this.mCursor);
                        if ('\uffff' == ch) {
                            done = true;
                            continue;
                        }
                        if ('>' == ch) {
                            done = true;
                            continue;
                        }
                        this.mPage.ungetCharacter(this.mCursor);
                        state = 2;
                        continue;
                    }
                    case 2: {
                        if ('-' == ch) {
                            state = 3;
                            continue;
                        }
                        if ('\uffff' == ch) {
                            return this.parseString(start, quotesmart);
                        }
                        continue;
                    }
                    case 3: {
                        if ('-' == ch) {
                            state = 4;
                            continue;
                        }
                        state = 2;
                        continue;
                    }
                    case 4: {
                        if ('>' == ch) {
                            done = true;
                            continue;
                        }
                        if (Character.isWhitespace(ch)) {
                            continue;
                        }
                        if (!Lexer.STRICT_REMARKS) {
                            if ('-' == ch) {
                                continue;
                            }
                            if ('!' == ch) {
                                continue;
                            }
                        }
                        state = 2;
                        continue;
                    }
                    default: {
                        throw new IllegalStateException("how the fuck did we get in state " + state);
                    }
                }
            }
        }
        return this.makeRemark(start, this.mCursor.getPosition());
    }
    
    protected Node makeRemark(final int start, final int end) throws ParserException {
        final int length = end - start;
        Node ret;
        if (0 != length) {
            if (2 > length) {
                return this.makeString(start, end);
            }
            ret = this.getNodeFactory().createRemarkNode(this.getPage(), start, end);
        }
        else {
            ret = null;
        }
        return ret;
    }
    
    protected Node parseJsp(final int start) throws ParserException {
        boolean done = false;
        int state = 0;
        int code = 0;
        final Vector attributes = new Vector();
        while (!done) {
            char ch = this.mPage.getCharacter(this.mCursor);
            switch (state) {
                case 0: {
                    switch (ch) {
                        case '%': {
                            state = 1;
                            continue;
                        }
                        default: {
                            done = true;
                            continue;
                        }
                    }
                    break;
                }
                case 1: {
                    switch (ch) {
                        case '>':
                        case '\uffff': {
                            done = true;
                            continue;
                        }
                        case '=':
                        case '@': {
                            code = this.mCursor.getPosition();
                            attributes.addElement(new PageAttribute(this.mPage, start + 1, code, -1, -1, '\0'));
                            state = 2;
                            continue;
                        }
                        default: {
                            code = this.mCursor.getPosition() - 1;
                            attributes.addElement(new PageAttribute(this.mPage, start + 1, code, -1, -1, '\0'));
                            state = 2;
                            continue;
                        }
                    }
                    break;
                }
                case 2: {
                    switch (ch) {
                        case '>':
                        case '\uffff': {
                            done = true;
                            continue;
                        }
                        case '\"':
                        case '\'': {
                            state = ch;
                            continue;
                        }
                        case '%': {
                            state = 3;
                            continue;
                        }
                        case '/': {
                            ch = this.mPage.getCharacter(this.mCursor);
                            if (ch == '/') {
                                do {
                                    ch = this.mPage.getCharacter(this.mCursor);
                                    if (ch == '\uffff') {
                                        done = true;
                                        break;
                                    }
                                    if (ch != '\n') {
                                        continue;
                                    }
                                    break;
                                } while (ch != '\r');
                                continue;
                            }
                            if (ch == '*') {
                                while (true) {
                                    ch = this.mPage.getCharacter(this.mCursor);
                                    if ('\uffff' == ch || '*' == ch) {
                                        ch = this.mPage.getCharacter(this.mCursor);
                                        if (ch == '*') {
                                            this.mPage.ungetCharacter(this.mCursor);
                                        }
                                        if ('\uffff' == ch) {
                                            break;
                                        }
                                        if ('/' == ch) {
                                            break;
                                        }
                                        continue;
                                    }
                                }
                                continue;
                            }
                            this.mPage.ungetCharacter(this.mCursor);
                            continue;
                        }
                        default: {
                            continue;
                        }
                    }
                    break;
                }
                case 3: {
                    switch (ch) {
                        case '\uffff': {
                            done = true;
                            continue;
                        }
                        case '>': {
                            state = 4;
                            done = true;
                            continue;
                        }
                        default: {
                            state = 2;
                            continue;
                        }
                    }
                    break;
                }
                case 34: {
                    switch (ch) {
                        case '\uffff': {
                            done = true;
                            continue;
                        }
                        case '\"': {
                            state = 2;
                            continue;
                        }
                        default: {
                            continue;
                        }
                    }
                    break;
                }
                case 39: {
                    switch (ch) {
                        case '\uffff': {
                            done = true;
                            continue;
                        }
                        case '\'': {
                            state = 2;
                            continue;
                        }
                        default: {
                            continue;
                        }
                    }
                    break;
                }
                default: {
                    throw new IllegalStateException("how the fuck did we get in state " + state);
                }
            }
        }
        if (4 != state) {
            return this.parseString(start, true);
        }
        if (0 != code) {
            state = this.mCursor.getPosition() - 2;
            attributes.addElement(new PageAttribute(this.mPage, code, state, -1, -1, '\0'));
            attributes.addElement(new PageAttribute(this.mPage, state, state + 1, -1, -1, '\0'));
            return this.makeTag(start, this.mCursor.getPosition(), attributes);
        }
        throw new IllegalStateException("jsp with no code!");
    }
    
    protected Node parsePI(final int start) throws ParserException {
        boolean done = false;
        int state = 0;
        int code = 0;
        final Vector attributes = new Vector();
        while (!done) {
            final char ch = this.mPage.getCharacter(this.mCursor);
            switch (state) {
                case 0: {
                    switch (ch) {
                        case '?': {
                            code = this.mCursor.getPosition();
                            attributes.addElement(new PageAttribute(this.mPage, start + 1, code, -1, -1, '\0'));
                            state = 1;
                            continue;
                        }
                        default: {
                            done = true;
                            continue;
                        }
                    }
                    break;
                }
                case 1: {
                    switch (ch) {
                        case '>':
                        case '\uffff': {
                            done = true;
                            continue;
                        }
                        case '\"':
                        case '\'': {
                            state = ch;
                            continue;
                        }
                        case '?': {
                            state = 2;
                            continue;
                        }
                        default: {
                            continue;
                        }
                    }
                    break;
                }
                case 2: {
                    switch (ch) {
                        case '\uffff': {
                            done = true;
                            continue;
                        }
                        case '>': {
                            state = 3;
                            done = true;
                            continue;
                        }
                        default: {
                            state = 1;
                            continue;
                        }
                    }
                    break;
                }
                case 34: {
                    switch (ch) {
                        case '\uffff': {
                            done = true;
                            continue;
                        }
                        case '\"': {
                            state = 1;
                            continue;
                        }
                        default: {
                            continue;
                        }
                    }
                    break;
                }
                case 39: {
                    switch (ch) {
                        case '\uffff': {
                            done = true;
                            continue;
                        }
                        case '\'': {
                            state = 1;
                            continue;
                        }
                        default: {
                            continue;
                        }
                    }
                    break;
                }
                default: {
                    throw new IllegalStateException("how the fuck did we get in state " + state);
                }
            }
        }
        if (3 != state) {
            return this.parseString(start, true);
        }
        if (0 != code) {
            state = this.mCursor.getPosition() - 2;
            attributes.addElement(new PageAttribute(this.mPage, code, state, -1, -1, '\0'));
            attributes.addElement(new PageAttribute(this.mPage, state, state + 1, -1, -1, '\0'));
            return this.makeTag(start, this.mCursor.getPosition(), attributes);
        }
        throw new IllegalStateException("processing instruction with no content");
    }
    
    public static void main(final String[] args) throws MalformedURLException, ParserException {
        if (0 >= args.length) {
            System.out.println("HTML Lexer v" + getVersion() + "\n");
            System.out.println();
            System.out.println("usage: java -jar htmllexer.jar <url>");
        }
        else {
            try {
                final ConnectionManager manager = Page.getConnectionManager();
                final Lexer lexer = new Lexer(manager.openConnection(args[0]));
                Node node;
                while (null != (node = lexer.nextNode(false))) {
                    System.out.println(node.toString());
                }
            }
            catch (final ParserException pe) {
                System.out.println(pe.getMessage());
                if (null != pe.getThrowable()) {
                    System.out.println(pe.getThrowable().getMessage());
                }
            }
        }
    }
    
    static {
        Lexer.STRICT_REMARKS = true;
        Lexer.mDebugLineTrigger = -1;
    }
}
