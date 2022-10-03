package javax.swing.text.html.parser;

import java.io.InterruptedIOException;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.text.html.HTML;
import javax.swing.text.ChangedCharSetException;
import java.io.IOException;
import java.io.CharArrayReader;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import java.io.Reader;

public class Parser implements DTDConstants
{
    private char[] text;
    private int textpos;
    private TagElement last;
    private boolean space;
    private char[] str;
    private int strpos;
    protected DTD dtd;
    private int ch;
    private int ln;
    private Reader in;
    private Element recent;
    private TagStack stack;
    private boolean skipTag;
    private TagElement lastFormSent;
    private SimpleAttributeSet attributes;
    private boolean seenHtml;
    private boolean seenHead;
    private boolean seenBody;
    private boolean ignoreSpace;
    protected boolean strict;
    private int crlfCount;
    private int crCount;
    private int lfCount;
    private int currentBlockStartPos;
    private int lastBlockStartPos;
    private static final char[] cp1252Map;
    private static final String START_COMMENT = "<!--";
    private static final String END_COMMENT = "-->";
    private static final char[] SCRIPT_END_TAG;
    private static final char[] SCRIPT_END_TAG_UPPER_CASE;
    private char[] buf;
    private int pos;
    private int len;
    private int currentPosition;
    
    public Parser(final DTD dtd) {
        this.text = new char[1024];
        this.textpos = 0;
        this.str = new char[128];
        this.strpos = 0;
        this.dtd = null;
        this.skipTag = false;
        this.lastFormSent = null;
        this.attributes = new SimpleAttributeSet();
        this.seenHtml = false;
        this.seenHead = false;
        this.seenBody = false;
        this.strict = false;
        this.buf = new char[1];
        this.dtd = dtd;
    }
    
    protected int getCurrentLine() {
        return this.ln;
    }
    
    int getBlockStartPosition() {
        return Math.max(0, this.lastBlockStartPos - 1);
    }
    
    protected TagElement makeTag(final Element element, final boolean b) {
        return new TagElement(element, b);
    }
    
    protected TagElement makeTag(final Element element) {
        return this.makeTag(element, false);
    }
    
    protected SimpleAttributeSet getAttributes() {
        return this.attributes;
    }
    
    protected void flushAttributes() {
        this.attributes.removeAttributes(this.attributes);
    }
    
    protected void handleText(final char[] array) {
    }
    
    protected void handleTitle(final char[] array) {
        this.handleText(array);
    }
    
    protected void handleComment(final char[] array) {
    }
    
    protected void handleEOFInComment() {
        final int strIndex = this.strIndexOf('\n');
        if (strIndex >= 0) {
            this.handleComment(this.getChars(0, strIndex));
            try {
                this.in.close();
                this.in = new CharArrayReader(this.getChars(strIndex + 1));
                this.ch = 62;
            }
            catch (final IOException ex) {
                this.error("ioexception");
            }
            this.resetStrBuffer();
        }
        else {
            this.error("eof.comment");
        }
    }
    
    protected void handleEmptyTag(final TagElement tagElement) throws ChangedCharSetException {
    }
    
    protected void handleStartTag(final TagElement tagElement) {
    }
    
    protected void handleEndTag(final TagElement tagElement) {
    }
    
    protected void handleError(final int n, final String s) {
    }
    
    void handleText(final TagElement tagElement) {
        if (tagElement.breaksFlow()) {
            this.space = false;
            if (!this.strict) {
                this.ignoreSpace = true;
            }
        }
        if (this.textpos == 0 && (!this.space || this.stack == null || this.last.breaksFlow() || !this.stack.advance(this.dtd.pcdata))) {
            this.last = tagElement;
            this.space = false;
            this.lastBlockStartPos = this.currentBlockStartPos;
            return;
        }
        if (this.space) {
            if (!this.ignoreSpace) {
                if (this.textpos + 1 > this.text.length) {
                    final char[] text = new char[this.text.length + 200];
                    System.arraycopy(this.text, 0, text, 0, this.text.length);
                    this.text = text;
                }
                this.text[this.textpos++] = ' ';
                if (!this.strict && !tagElement.getElement().isEmpty()) {
                    this.ignoreSpace = true;
                }
            }
            this.space = false;
        }
        final char[] array = new char[this.textpos];
        System.arraycopy(this.text, 0, array, 0, this.textpos);
        if (tagElement.getElement().getName().equals("title")) {
            this.handleTitle(array);
        }
        else {
            this.handleText(array);
        }
        this.lastBlockStartPos = this.currentBlockStartPos;
        this.textpos = 0;
        this.last = tagElement;
        this.space = false;
    }
    
    protected void error(final String s, final String s2, final String s3, final String s4) {
        this.handleError(this.ln, s + " " + s2 + " " + s3 + " " + s4);
    }
    
    protected void error(final String s, final String s2, final String s3) {
        this.error(s, s2, s3, "?");
    }
    
    protected void error(final String s, final String s2) {
        this.error(s, s2, "?", "?");
    }
    
    protected void error(final String s) {
        this.error(s, "?", "?", "?");
    }
    
    protected void startTag(final TagElement last) throws ChangedCharSetException {
        final Element element = last.getElement();
        if (!element.isEmpty() || (this.last != null && !this.last.breaksFlow()) || this.textpos != 0) {
            this.handleText(last);
        }
        else {
            this.last = last;
            this.space = false;
        }
        this.lastBlockStartPos = this.currentBlockStartPos;
        for (AttributeList list = element.atts; list != null; list = list.next) {
            if (list.modifier == 2 && (this.attributes.isEmpty() || (!this.attributes.isDefined(list.name) && !this.attributes.isDefined(HTML.getAttributeKey(list.name))))) {
                this.error("req.att ", list.getName(), element.getName());
            }
        }
        if (element.isEmpty()) {
            this.handleEmptyTag(last);
        }
        else {
            this.recent = element;
            this.stack = new TagStack(last, this.stack);
            this.handleStartTag(last);
        }
    }
    
    protected void endTag(final boolean b) {
        this.handleText(this.stack.tag);
        if (b && !this.stack.elem.omitEnd()) {
            this.error("end.missing", this.stack.elem.getName());
        }
        else if (!this.stack.terminate()) {
            this.error("end.unexpected", this.stack.elem.getName());
        }
        this.handleEndTag(this.stack.tag);
        this.stack = this.stack.next;
        this.recent = ((this.stack != null) ? this.stack.elem : null);
    }
    
    boolean ignoreElement(final Element element) {
        final String name = this.stack.elem.getName();
        final String name2 = element.getName();
        if ((name2.equals("html") && this.seenHtml) || (name2.equals("head") && this.seenHead) || (name2.equals("body") && this.seenBody)) {
            return true;
        }
        if (name2.equals("dt") || name2.equals("dd")) {
            TagStack tagStack;
            for (tagStack = this.stack; tagStack != null && !tagStack.elem.getName().equals("dl"); tagStack = tagStack.next) {}
            if (tagStack == null) {
                return true;
            }
        }
        return (name.equals("table") && !name2.equals("#pcdata") && !name2.equals("input")) || (name2.equals("font") && (name.equals("ul") || name.equals("ol"))) || (name2.equals("meta") && this.stack != null) || (name2.equals("style") && this.seenBody) || (name.equals("table") && name2.equals("a"));
    }
    
    protected void markFirstTime(final Element element) {
        final String name = element.getName();
        if (name.equals("html")) {
            this.seenHtml = true;
        }
        else if (name.equals("head")) {
            this.seenHead = true;
        }
        else if (name.equals("body")) {
            if (this.buf.length == 1) {
                final char[] buf = new char[256];
                buf[0] = this.buf[0];
                this.buf = buf;
            }
            this.seenBody = true;
        }
    }
    
    boolean legalElementContext(final Element element) throws ChangedCharSetException {
        if (this.stack == null) {
            if (element != this.dtd.html) {
                this.startTag(this.makeTag(this.dtd.html, true));
                return this.legalElementContext(element);
            }
            return true;
        }
        else {
            if (this.stack.advance(element)) {
                this.markFirstTime(element);
                return true;
            }
            boolean b = false;
            final String name = this.stack.elem.getName();
            final String name2 = element.getName();
            if (!this.strict && ((name.equals("table") && name2.equals("td")) || (name.equals("table") && name2.equals("th")) || (name.equals("tr") && !name2.equals("tr")))) {
                b = true;
            }
            if (!this.strict && !b && (this.stack.elem.getName() != element.getName() || element.getName().equals("body")) && (this.skipTag = this.ignoreElement(element))) {
                this.error("tag.ignore", element.getName());
                return this.skipTag;
            }
            if (!this.strict && name.equals("table") && !name2.equals("tr") && !name2.equals("td") && !name2.equals("th") && !name2.equals("caption")) {
                final TagElement tag = this.makeTag(this.dtd.getElement("tr"), true);
                this.legalTagContext(tag);
                this.startTag(tag);
                this.error("start.missing", element.getName());
                return this.legalElementContext(element);
            }
            if (!b && this.stack.terminate() && (!this.strict || this.stack.elem.omitEnd())) {
                for (TagStack tagStack = this.stack.next; tagStack != null; tagStack = tagStack.next) {
                    if (tagStack.advance(element)) {
                        while (this.stack != tagStack) {
                            this.endTag(true);
                        }
                        return true;
                    }
                    if (!tagStack.terminate()) {
                        break;
                    }
                    if (this.strict && !tagStack.elem.omitEnd()) {
                        break;
                    }
                }
            }
            final Element first = this.stack.first();
            if (first != null && (!this.strict || first.omitStart()) && (first != this.dtd.head || element != this.dtd.pcdata)) {
                final TagElement tag2 = this.makeTag(first, true);
                this.legalTagContext(tag2);
                this.startTag(tag2);
                if (!first.omitStart()) {
                    this.error("start.missing", element.getName());
                }
                return this.legalElementContext(element);
            }
            if (!this.strict) {
                final ContentModel contentModel = this.stack.contentModel();
                final Vector<Element> vector = new Vector<Element>();
                if (contentModel != null) {
                    contentModel.getElements(vector);
                    for (final Element element2 : vector) {
                        if (this.stack.excluded(element2.getIndex())) {
                            continue;
                        }
                        boolean b2 = false;
                        for (AttributeList list = element2.getAttributes(); list != null; list = list.next) {
                            if (list.modifier == 2) {
                                b2 = true;
                                break;
                            }
                        }
                        if (b2) {
                            continue;
                        }
                        final ContentModel content = element2.getContent();
                        if (content != null && content.first(element)) {
                            final TagElement tag3 = this.makeTag(element2, true);
                            this.legalTagContext(tag3);
                            this.startTag(tag3);
                            this.error("start.missing", element2.getName());
                            return this.legalElementContext(element);
                        }
                    }
                }
            }
            if (this.stack.terminate() && this.stack.elem != this.dtd.body && (!this.strict || this.stack.elem.omitEnd())) {
                if (!this.stack.elem.omitEnd()) {
                    this.error("end.missing", element.getName());
                }
                this.endTag(true);
                return this.legalElementContext(element);
            }
            return false;
        }
    }
    
    void legalTagContext(final TagElement tagElement) throws ChangedCharSetException {
        if (this.legalElementContext(tagElement.getElement())) {
            this.markFirstTime(tagElement.getElement());
            return;
        }
        if (tagElement.breaksFlow() && this.stack != null && !this.stack.tag.breaksFlow()) {
            this.endTag(true);
            this.legalTagContext(tagElement);
            return;
        }
        for (TagStack tagStack = this.stack; tagStack != null; tagStack = tagStack.next) {
            if (tagStack.tag.getElement() == this.dtd.head) {
                while (this.stack != tagStack) {
                    this.endTag(true);
                }
                this.endTag(true);
                this.legalTagContext(tagElement);
                return;
            }
        }
        this.error("tag.unexpected", tagElement.getElement().getName());
    }
    
    void errorContext() throws ChangedCharSetException {
        while (this.stack != null && this.stack.tag.getElement() != this.dtd.body) {
            this.handleEndTag(this.stack.tag);
            this.stack = this.stack.next;
        }
        if (this.stack == null) {
            this.legalElementContext(this.dtd.body);
            this.startTag(this.makeTag(this.dtd.body, true));
        }
    }
    
    void addString(final int n) {
        if (this.strpos == this.str.length) {
            final char[] str = new char[this.str.length + 128];
            System.arraycopy(this.str, 0, str, 0, this.str.length);
            this.str = str;
        }
        this.str[this.strpos++] = (char)n;
    }
    
    String getString(final int strpos) {
        final char[] array = new char[this.strpos - strpos];
        System.arraycopy(this.str, strpos, array, 0, this.strpos - strpos);
        this.strpos = strpos;
        return new String(array);
    }
    
    char[] getChars(final int strpos) {
        final char[] array = new char[this.strpos - strpos];
        System.arraycopy(this.str, strpos, array, 0, this.strpos - strpos);
        this.strpos = strpos;
        return array;
    }
    
    char[] getChars(final int n, final int n2) {
        final char[] array = new char[n2 - n];
        System.arraycopy(this.str, n, array, 0, n2 - n);
        return array;
    }
    
    void resetStrBuffer() {
        this.strpos = 0;
    }
    
    int strIndexOf(final char c) {
        for (int i = 0; i < this.strpos; ++i) {
            if (this.str[i] == c) {
                return i;
            }
        }
        return -1;
    }
    
    void skipSpace() throws IOException {
        while (true) {
            switch (this.ch) {
                case 10: {
                    ++this.ln;
                    this.ch = this.readCh();
                    ++this.lfCount;
                    continue;
                }
                case 13: {
                    ++this.ln;
                    final int ch = this.readCh();
                    this.ch = ch;
                    if (ch == 10) {
                        this.ch = this.readCh();
                        ++this.crlfCount;
                        continue;
                    }
                    ++this.crCount;
                    continue;
                }
                case 9:
                case 32: {
                    this.ch = this.readCh();
                    continue;
                }
                default: {}
            }
        }
    }
    
    boolean parseIdentifier(final boolean b) throws IOException {
        switch (this.ch) {
            case 65:
            case 66:
            case 67:
            case 68:
            case 69:
            case 70:
            case 71:
            case 72:
            case 73:
            case 74:
            case 75:
            case 76:
            case 77:
            case 78:
            case 79:
            case 80:
            case 81:
            case 82:
            case 83:
            case 84:
            case 85:
            case 86:
            case 87:
            case 88:
            case 89:
            case 90: {
                if (b) {
                    this.ch = 97 + (this.ch - 65);
                }
            }
            case 97:
            case 98:
            case 99:
            case 100:
            case 101:
            case 102:
            case 103:
            case 104:
            case 105:
            case 106:
            case 107:
            case 108:
            case 109:
            case 110:
            case 111:
            case 112:
            case 113:
            case 114:
            case 115:
            case 116:
            case 117:
            case 118:
            case 119:
            case 120:
            case 121:
            case 122: {
                while (true) {
                    this.addString(this.ch);
                    switch (this.ch = this.readCh()) {
                        case 65:
                        case 66:
                        case 67:
                        case 68:
                        case 69:
                        case 70:
                        case 71:
                        case 72:
                        case 73:
                        case 74:
                        case 75:
                        case 76:
                        case 77:
                        case 78:
                        case 79:
                        case 80:
                        case 81:
                        case 82:
                        case 83:
                        case 84:
                        case 85:
                        case 86:
                        case 87:
                        case 88:
                        case 89:
                        case 90: {
                            if (b) {
                                this.ch = 97 + (this.ch - 65);
                                continue;
                            }
                            continue;
                        }
                        case 45:
                        case 46:
                        case 48:
                        case 49:
                        case 50:
                        case 51:
                        case 52:
                        case 53:
                        case 54:
                        case 55:
                        case 56:
                        case 57:
                        case 95:
                        case 97:
                        case 98:
                        case 99:
                        case 100:
                        case 101:
                        case 102:
                        case 103:
                        case 104:
                        case 105:
                        case 106:
                        case 107:
                        case 108:
                        case 109:
                        case 110:
                        case 111:
                        case 112:
                        case 113:
                        case 114:
                        case 115:
                        case 116:
                        case 117:
                        case 118:
                        case 119:
                        case 120:
                        case 121:
                        case 122: {
                            continue;
                        }
                        default: {
                            return true;
                        }
                    }
                }
                break;
            }
            default: {
                return false;
            }
        }
    }
    
    private char[] parseEntityReference() throws IOException {
        final int strpos = this.strpos;
        final int ch = this.readCh();
        this.ch = ch;
        if (ch == 35) {
            int n = 0;
            this.ch = this.readCh();
            if ((this.ch >= 48 && this.ch <= 57) || this.ch == 120 || this.ch == 88) {
                if (this.ch >= 48 && this.ch <= 57) {
                    while (this.ch >= 48 && this.ch <= 57) {
                        n = n * 10 + this.ch - 48;
                        this.ch = this.readCh();
                    }
                }
                else {
                    this.ch = this.readCh();
                    for (char c = (char)Character.toLowerCase(this.ch); (c >= '0' && c <= '9') || (c >= 'a' && c <= 'f'); c = (char)Character.toLowerCase(this.ch)) {
                        if (c >= '0' && c <= '9') {
                            n = n * 16 + c - 48;
                        }
                        else {
                            n = n * 16 + c - 97 + 10;
                        }
                        this.ch = this.readCh();
                    }
                }
                switch (this.ch) {
                    case 10: {
                        ++this.ln;
                        this.ch = this.readCh();
                        ++this.lfCount;
                        break;
                    }
                    case 13: {
                        ++this.ln;
                        final int ch2 = this.readCh();
                        this.ch = ch2;
                        if (ch2 == 10) {
                            this.ch = this.readCh();
                            ++this.crlfCount;
                            break;
                        }
                        ++this.crCount;
                        break;
                    }
                    case 59: {
                        this.ch = this.readCh();
                        break;
                    }
                }
                return this.mapNumericReference(n);
            }
            this.addString(35);
            if (!this.parseIdentifier(false)) {
                this.error("ident.expected");
                this.strpos = strpos;
                return new char[] { '&', '#' };
            }
        }
        else if (!this.parseIdentifier(false)) {
            return new char[] { '&' };
        }
        boolean b = false;
        switch (this.ch) {
            case 10: {
                ++this.ln;
                this.ch = this.readCh();
                ++this.lfCount;
                break;
            }
            case 13: {
                ++this.ln;
                final int ch3 = this.readCh();
                this.ch = ch3;
                if (ch3 == 10) {
                    this.ch = this.readCh();
                    ++this.crlfCount;
                    break;
                }
                ++this.crCount;
                break;
            }
            case 59: {
                b = true;
                this.ch = this.readCh();
                break;
            }
        }
        final String string = this.getString(strpos);
        Entity entity = this.dtd.getEntity(string);
        if (!this.strict && entity == null) {
            entity = this.dtd.getEntity(string.toLowerCase());
        }
        if (entity != null && entity.isGeneral()) {
            return entity.getData();
        }
        if (string.length() == 0) {
            this.error("invalid.entref", string);
            return new char[0];
        }
        final String string2 = "&" + string + (b ? ";" : "");
        final char[] array = new char[string2.length()];
        string2.getChars(0, array.length, array, 0);
        return array;
    }
    
    private char[] mapNumericReference(final int n) {
        char[] chars;
        if (n >= 65535) {
            try {
                chars = Character.toChars(n);
            }
            catch (final IllegalArgumentException ex) {
                chars = new char[0];
            }
        }
        else {
            chars = new char[] { (n < 130 || n > 159) ? ((char)n) : Parser.cp1252Map[n - 130] };
        }
        return chars;
    }
    
    void parseComment() throws IOException {
        while (true) {
            int ch = this.ch;
            switch (ch) {
                case 45: {
                    if (!this.strict && this.strpos != 0 && this.str[this.strpos - 1] == '-') {
                        if ((this.ch = this.readCh()) == 62) {
                            return;
                        }
                        if (this.ch != 33) {
                            break;
                        }
                        if ((this.ch = this.readCh()) == 62) {
                            return;
                        }
                        this.addString(45);
                        this.addString(33);
                        continue;
                    }
                    else {
                        if ((this.ch = this.readCh()) != 45) {
                            break;
                        }
                        this.ch = this.readCh();
                        if (this.strict || this.ch == 62) {
                            return;
                        }
                        if (this.ch != 33) {
                            this.addString(45);
                            break;
                        }
                        if ((this.ch = this.readCh()) == 62) {
                            return;
                        }
                        this.addString(45);
                        this.addString(33);
                        continue;
                    }
                    break;
                }
                case -1: {
                    this.handleEOFInComment();
                    return;
                }
                case 10: {
                    ++this.ln;
                    this.ch = this.readCh();
                    ++this.lfCount;
                    break;
                }
                case 62: {
                    this.ch = this.readCh();
                    break;
                }
                case 13: {
                    ++this.ln;
                    final int ch2 = this.readCh();
                    this.ch = ch2;
                    if (ch2 == 10) {
                        this.ch = this.readCh();
                        ++this.crlfCount;
                    }
                    else {
                        ++this.crCount;
                    }
                    ch = 10;
                    break;
                }
                default: {
                    this.ch = this.readCh();
                    break;
                }
            }
            this.addString(ch);
        }
    }
    
    void parseLiteral(final boolean b) throws IOException {
        while (true) {
            int ch = this.ch;
            switch (ch) {
                case -1: {
                    this.error("eof.literal", this.stack.elem.getName());
                    this.endTag(true);
                    return;
                }
                case 62: {
                    this.ch = this.readCh();
                    int n = this.textpos - (this.stack.elem.name.length() + 2);
                    int n2 = 0;
                    if (n < 0 || this.text[n++] != '<' || this.text[n] != '/') {
                        break;
                    }
                    while (++n < this.textpos && Character.toLowerCase(this.text[n]) == this.stack.elem.name.charAt(n2++)) {}
                    if (n == this.textpos) {
                        this.textpos -= this.stack.elem.name.length() + 2;
                        if (this.textpos > 0 && this.text[this.textpos - 1] == '\n') {
                            --this.textpos;
                        }
                        this.endTag(false);
                        return;
                    }
                    break;
                }
                case 38: {
                    final char[] entityReference = this.parseEntityReference();
                    if (this.textpos + entityReference.length > this.text.length) {
                        final char[] text = new char[Math.max(this.textpos + entityReference.length + 128, this.text.length * 2)];
                        System.arraycopy(this.text, 0, text, 0, this.text.length);
                        this.text = text;
                    }
                    System.arraycopy(entityReference, 0, this.text, this.textpos, entityReference.length);
                    this.textpos += entityReference.length;
                    continue;
                }
                case 10: {
                    ++this.ln;
                    this.ch = this.readCh();
                    ++this.lfCount;
                    break;
                }
                case 13: {
                    ++this.ln;
                    final int ch2 = this.readCh();
                    this.ch = ch2;
                    if (ch2 == 10) {
                        this.ch = this.readCh();
                        ++this.crlfCount;
                    }
                    else {
                        ++this.crCount;
                    }
                    ch = 10;
                    break;
                }
                default: {
                    this.ch = this.readCh();
                    break;
                }
            }
            if (this.textpos == this.text.length) {
                final char[] text2 = new char[this.text.length + 128];
                System.arraycopy(this.text, 0, text2, 0, this.text.length);
                this.text = text2;
            }
            this.text[this.textpos++] = (char)ch;
        }
    }
    
    String parseAttributeValue(final boolean b) throws IOException {
        int ch = -1;
        switch (this.ch) {
            case 34:
            case 39: {
                ch = this.ch;
                this.ch = this.readCh();
                break;
            }
        }
        while (true) {
            int ch2 = this.ch;
            switch (ch2) {
                case 10: {
                    ++this.ln;
                    this.ch = this.readCh();
                    ++this.lfCount;
                    if (ch < 0) {
                        return this.getString(0);
                    }
                    break;
                }
                case 13: {
                    ++this.ln;
                    final int ch3 = this.readCh();
                    this.ch = ch3;
                    if (ch3 == 10) {
                        this.ch = this.readCh();
                        ++this.crlfCount;
                    }
                    else {
                        ++this.crCount;
                    }
                    if (ch < 0) {
                        return this.getString(0);
                    }
                    break;
                }
                case 9: {
                    if (ch < 0) {
                        ch2 = 32;
                    }
                }
                case 32: {
                    this.ch = this.readCh();
                    if (ch < 0) {
                        return this.getString(0);
                    }
                    break;
                }
                case 60:
                case 62: {
                    if (ch < 0) {
                        return this.getString(0);
                    }
                    this.ch = this.readCh();
                    break;
                }
                case 34:
                case 39: {
                    this.ch = this.readCh();
                    if (ch2 == ch) {
                        return this.getString(0);
                    }
                    if (ch != -1) {
                        break;
                    }
                    this.error("attvalerr");
                    if (this.strict || this.ch == 32) {
                        return this.getString(0);
                    }
                    continue;
                }
                case 61: {
                    if (ch < 0) {
                        this.error("attvalerr");
                        if (this.strict) {
                            return this.getString(0);
                        }
                    }
                    this.ch = this.readCh();
                    break;
                }
                case 38: {
                    if (this.strict && ch < 0) {
                        this.ch = this.readCh();
                        break;
                    }
                    final char[] entityReference = this.parseEntityReference();
                    for (int i = 0; i < entityReference.length; ++i) {
                        final char c = entityReference[i];
                        this.addString((b && c >= 'A' && c <= 'Z') ? ('a' + c - 65) : c);
                    }
                    continue;
                }
                case -1: {
                    return this.getString(0);
                }
                default: {
                    if (b && ch2 >= 65 && ch2 <= 90) {
                        ch2 = 97 + ch2 - 65;
                    }
                    this.ch = this.readCh();
                    break;
                }
            }
            this.addString(ch2);
        }
    }
    
    void parseAttributeSpecificationList(final Element element) throws IOException {
        while (true) {
            this.skipSpace();
            switch (this.ch) {
                case -1:
                case 47:
                case 60:
                case 62: {
                    return;
                }
                case 45: {
                    final int ch = this.readCh();
                    this.ch = ch;
                    if (ch == 45) {
                        this.ch = this.readCh();
                        this.parseComment();
                        this.strpos = 0;
                        continue;
                    }
                    this.error("invalid.tagchar", "-", element.getName());
                    this.ch = this.readCh();
                    continue;
                }
                default: {
                    String s;
                    AttributeList list;
                    String s2;
                    if (this.parseIdentifier(true)) {
                        s = this.getString(0);
                        this.skipSpace();
                        if (this.ch == 61) {
                            this.ch = this.readCh();
                            this.skipSpace();
                            list = element.getAttribute(s);
                            s2 = this.parseAttributeValue(list != null && list.type != 1 && list.type != 11 && list.type != 7);
                        }
                        else {
                            s2 = s;
                            list = element.getAttributeByValue(s2);
                            if (list == null) {
                                list = element.getAttribute(s);
                                if (list != null) {
                                    s2 = list.getValue();
                                }
                                else {
                                    s2 = null;
                                }
                            }
                        }
                    }
                    else {
                        if (!this.strict && this.ch == 44) {
                            this.ch = this.readCh();
                            continue;
                        }
                        if (!this.strict && this.ch == 34) {
                            this.ch = this.readCh();
                            this.skipSpace();
                            if (!this.parseIdentifier(true)) {
                                this.error("invalid.tagchar", new String(new char[] { (char)this.ch }), element.getName());
                                this.ch = this.readCh();
                                continue;
                            }
                            s = this.getString(0);
                            if (this.ch == 34) {
                                this.ch = this.readCh();
                            }
                            this.skipSpace();
                            if (this.ch == 61) {
                                this.ch = this.readCh();
                                this.skipSpace();
                                list = element.getAttribute(s);
                                s2 = this.parseAttributeValue(list != null && list.type != 1 && list.type != 11);
                            }
                            else {
                                s2 = s;
                                list = element.getAttributeByValue(s2);
                                if (list == null) {
                                    list = element.getAttribute(s);
                                    if (list != null) {
                                        s2 = list.getValue();
                                    }
                                }
                            }
                        }
                        else if (!this.strict && this.attributes.isEmpty() && this.ch == 61) {
                            this.ch = this.readCh();
                            this.skipSpace();
                            s = element.getName();
                            list = element.getAttribute(s);
                            s2 = this.parseAttributeValue(list != null && list.type != 1 && list.type != 11);
                        }
                        else {
                            if (!this.strict && this.ch == 61) {
                                this.ch = this.readCh();
                                this.skipSpace();
                                this.parseAttributeValue(true);
                                this.error("attvalerr");
                                return;
                            }
                            this.error("invalid.tagchar", new String(new char[] { (char)this.ch }), element.getName());
                            if (!this.strict) {
                                this.ch = this.readCh();
                                continue;
                            }
                            return;
                        }
                    }
                    if (list != null) {
                        s = list.getName();
                    }
                    else {
                        this.error("invalid.tagatt", s, element.getName());
                    }
                    if (this.attributes.isDefined(s)) {
                        this.error("multi.tagatt", s, element.getName());
                    }
                    if (s2 == null) {
                        s2 = ((list != null && list.value != null) ? list.value : "#DEFAULT");
                    }
                    else if (list != null && list.values != null && !list.values.contains(s2)) {
                        this.error("invalid.tagattval", s, element.getName());
                    }
                    final HTML.Attribute attributeKey = HTML.getAttributeKey(s);
                    if (attributeKey == null) {
                        this.attributes.addAttribute(s, s2);
                    }
                    else {
                        this.attributes.addAttribute(attributeKey, s2);
                    }
                    continue;
                }
            }
        }
    }
    
    public String parseDTDMarkup() throws IOException {
        final StringBuilder sb = new StringBuilder();
        this.ch = this.readCh();
        while (true) {
            switch (this.ch) {
                case 62: {
                    this.ch = this.readCh();
                    return sb.toString();
                }
                case -1: {
                    this.error("invalid.markup");
                    return sb.toString();
                }
                case 10: {
                    ++this.ln;
                    this.ch = this.readCh();
                    ++this.lfCount;
                    continue;
                }
                case 34: {
                    this.ch = this.readCh();
                    continue;
                }
                case 13: {
                    ++this.ln;
                    final int ch = this.readCh();
                    this.ch = ch;
                    if (ch == 10) {
                        this.ch = this.readCh();
                        ++this.crlfCount;
                        continue;
                    }
                    ++this.crCount;
                    continue;
                }
                default: {
                    sb.append((char)(this.ch & 0xFF));
                    this.ch = this.readCh();
                    continue;
                }
            }
        }
    }
    
    protected boolean parseMarkupDeclarations(final StringBuffer sb) throws IOException {
        if (sb.length() == "DOCTYPE".length() && sb.toString().toUpperCase().equals("DOCTYPE")) {
            this.parseDTDMarkup();
            return true;
        }
        return false;
    }
    
    void parseInvalidTag() throws IOException {
        while (true) {
            this.skipSpace();
            switch (this.ch) {
                case -1:
                case 62: {
                    this.ch = this.readCh();
                    return;
                }
                case 60: {
                    return;
                }
                default: {
                    this.ch = this.readCh();
                    continue;
                }
            }
        }
    }
    
    void parseTag() throws IOException {
        boolean net = false;
        int n = 0;
        int n2 = 0;
        switch (this.ch = this.readCh()) {
            case 33: {
                switch (this.ch = this.readCh()) {
                    case 45: {
                    Label_0291:
                        while (true) {
                            if (this.ch == 45) {
                                if (!this.strict || (this.ch = this.readCh()) == 45) {
                                    this.ch = this.readCh();
                                    if (!this.strict && this.ch == 45) {
                                        this.ch = this.readCh();
                                    }
                                    if (this.textpos != 0) {
                                        final char[] array = new char[this.textpos];
                                        System.arraycopy(this.text, 0, array, 0, this.textpos);
                                        this.handleText(array);
                                        this.lastBlockStartPos = this.currentBlockStartPos;
                                        this.textpos = 0;
                                    }
                                    this.parseComment();
                                    this.last = this.makeTag(this.dtd.getElement("comment"), true);
                                    this.handleComment(this.getChars(0));
                                    continue;
                                }
                                if (n == 0) {
                                    n = 1;
                                    this.error("invalid.commentchar", "-");
                                }
                            }
                            this.skipSpace();
                            switch (this.ch) {
                                case 45: {
                                    continue;
                                }
                                case 62: {
                                    this.ch = this.readCh();
                                }
                                case -1: {
                                    break Label_0291;
                                }
                                default: {
                                    this.ch = this.readCh();
                                    if (n == 0) {
                                        n = 1;
                                        this.error("invalid.commentchar", String.valueOf((char)this.ch));
                                        continue;
                                    }
                                    continue;
                                }
                            }
                        }
                        return;
                    }
                    default: {
                        final StringBuffer sb = new StringBuffer();
                    Label_0408:
                        while (true) {
                            sb.append((char)this.ch);
                            if (this.parseMarkupDeclarations(sb)) {
                                return;
                            }
                            switch (this.ch) {
                                case 62: {
                                    this.ch = this.readCh();
                                }
                                case -1: {
                                    break Label_0408;
                                }
                                case 10: {
                                    ++this.ln;
                                    this.ch = this.readCh();
                                    ++this.lfCount;
                                    continue;
                                }
                                case 13: {
                                    ++this.ln;
                                    final int ch = this.readCh();
                                    this.ch = ch;
                                    if (ch == 10) {
                                        this.ch = this.readCh();
                                        ++this.crlfCount;
                                        continue;
                                    }
                                    ++this.crCount;
                                    continue;
                                }
                                default: {
                                    this.ch = this.readCh();
                                    continue;
                                }
                            }
                        }
                        this.error("invalid.markup");
                        return;
                    }
                }
                break;
            }
            case 47: {
                Element element = null;
                switch (this.ch = this.readCh()) {
                    case 62: {
                        this.ch = this.readCh();
                    }
                    case 60: {
                        if (this.recent == null) {
                            this.error("invalid.shortend");
                            return;
                        }
                        element = this.recent;
                        break;
                    }
                    default: {
                        if (!this.parseIdentifier(true)) {
                            this.error("expected.endtagname");
                            return;
                        }
                        this.skipSpace();
                        switch (this.ch) {
                            case 62: {
                                this.ch = this.readCh();
                            }
                            case 60: {
                                break;
                            }
                            default: {
                                this.error("expected", "'>'");
                                while (this.ch != -1 && this.ch != 10 && this.ch != 62) {
                                    this.ch = this.readCh();
                                }
                                if (this.ch == 62) {
                                    this.ch = this.readCh();
                                    break;
                                }
                                break;
                            }
                        }
                        final String string = this.getString(0);
                        if (!this.dtd.elementExists(string)) {
                            this.error("end.unrecognized", string);
                            if (this.textpos > 0 && this.text[this.textpos - 1] == '\n') {
                                --this.textpos;
                            }
                            element = this.dtd.getElement("unknown");
                            element.name = string;
                            n2 = 1;
                            break;
                        }
                        element = this.dtd.getElement(string);
                        break;
                    }
                }
                if (this.stack == null) {
                    this.error("end.extra.tag", element.getName());
                    return;
                }
                if (this.textpos > 0 && this.text[this.textpos - 1] == '\n') {
                    if (this.stack.pre) {
                        if (this.textpos > 1 && this.text[this.textpos - 2] != '\n') {
                            --this.textpos;
                        }
                    }
                    else {
                        --this.textpos;
                    }
                }
                if (n2 != 0) {
                    this.handleText(this.makeTag(element));
                    this.attributes.addAttribute(HTML.Attribute.ENDTAG, "true");
                    this.handleEmptyTag(this.makeTag(element));
                    return;
                }
                if (!this.strict) {
                    final String name = this.stack.elem.getName();
                    if (name.equals("table") && !element.getName().equals(name)) {
                        this.error("tag.ignore", element.getName());
                        return;
                    }
                    if ((name.equals("tr") || name.equals("td")) && !element.getName().equals("table") && !element.getName().equals(name)) {
                        this.error("tag.ignore", element.getName());
                        return;
                    }
                }
                TagStack tagStack;
                for (tagStack = this.stack; tagStack != null && element != tagStack.elem; tagStack = tagStack.next) {}
                if (tagStack == null) {
                    this.error("unmatched.endtag", element.getName());
                    return;
                }
                final String name2 = element.getName();
                if (this.stack != tagStack && (name2.equals("font") || name2.equals("center"))) {
                    if (name2.equals("center")) {
                        while (this.stack.elem.omitEnd() && this.stack != tagStack) {
                            this.endTag(true);
                        }
                        if (this.stack.elem == element) {
                            this.endTag(false);
                        }
                    }
                    return;
                }
                while (this.stack != tagStack) {
                    this.endTag(true);
                }
                this.endTag(false);
                return;
            }
            case -1: {
                this.error("eof");
                return;
            }
            default: {
                Element element2;
                if (!this.parseIdentifier(true)) {
                    element2 = this.recent;
                    if (this.ch != 62 || element2 == null) {
                        this.error("expected.tagname");
                        return;
                    }
                }
                else {
                    String string2 = this.getString(0);
                    if (string2.equals("image")) {
                        string2 = "img";
                    }
                    if (!this.dtd.elementExists(string2)) {
                        this.error("tag.unrecognized ", string2);
                        element2 = this.dtd.getElement("unknown");
                        element2.name = string2;
                        n2 = 1;
                    }
                    else {
                        element2 = this.dtd.getElement(string2);
                    }
                }
                this.parseAttributeSpecificationList(element2);
                switch (this.ch) {
                    case 47: {
                        net = true;
                    }
                    case 62: {
                        this.ch = this.readCh();
                        if (this.ch == 62 && net) {
                            this.ch = this.readCh();
                            break;
                        }
                        break;
                    }
                    case 60: {
                        break;
                    }
                    default: {
                        this.error("expected", "'>'");
                        break;
                    }
                }
                if (!this.strict && element2.getName().equals("script")) {
                    this.error("javascript.unsupported");
                }
                if (!element2.isEmpty()) {
                    if (this.ch == 10) {
                        ++this.ln;
                        ++this.lfCount;
                        this.ch = this.readCh();
                    }
                    else if (this.ch == 13) {
                        ++this.ln;
                        if ((this.ch = this.readCh()) == 10) {
                            this.ch = this.readCh();
                            ++this.crlfCount;
                        }
                        else {
                            ++this.crCount;
                        }
                    }
                }
                final TagElement tag = this.makeTag(element2, false);
                if (n2 == 0) {
                    this.legalTagContext(tag);
                    if (!this.strict && this.skipTag) {
                        this.skipTag = false;
                        return;
                    }
                }
                this.startTag(tag);
                if (!element2.isEmpty()) {
                    switch (element2.getType()) {
                        case 1: {
                            this.parseLiteral(false);
                            break;
                        }
                        case 16: {
                            this.parseLiteral(true);
                            break;
                        }
                        default: {
                            if (this.stack != null) {
                                this.stack.net = net;
                                break;
                            }
                            break;
                        }
                    }
                }
            }
        }
    }
    
    void parseScript() throws IOException {
        final char[] array = new char[Parser.SCRIPT_END_TAG.length];
        int n = 0;
        while (true) {
            int n2;
            for (n2 = 0; n == 0 && n2 < Parser.SCRIPT_END_TAG.length && (Parser.SCRIPT_END_TAG[n2] == this.ch || Parser.SCRIPT_END_TAG_UPPER_CASE[n2] == this.ch); ++n2) {
                array[n2] = (char)this.ch;
                this.ch = this.readCh();
            }
            if (n2 == Parser.SCRIPT_END_TAG.length) {
                return;
            }
            if (n == 0 && n2 == 1 && array[0] == "<!--".charAt(0)) {
                while (n2 < "<!--".length() && "<!--".charAt(n2) == this.ch) {
                    array[n2] = (char)this.ch;
                    this.ch = this.readCh();
                    ++n2;
                }
                if (n2 == "<!--".length()) {
                    n = 1;
                }
            }
            if (n != 0) {
                while (n2 < "-->".length() && "-->".charAt(n2) == this.ch) {
                    array[n2] = (char)this.ch;
                    this.ch = this.readCh();
                    ++n2;
                }
                if (n2 == "-->".length()) {
                    n = 0;
                }
            }
            if (n2 > 0) {
                for (int i = 0; i < n2; ++i) {
                    this.addString(array[i]);
                }
            }
            else {
                switch (this.ch) {
                    case -1: {
                        this.error("eof.script");
                        return;
                    }
                    case 10: {
                        ++this.ln;
                        this.ch = this.readCh();
                        ++this.lfCount;
                        this.addString(10);
                        continue;
                    }
                    case 13: {
                        ++this.ln;
                        final int ch = this.readCh();
                        this.ch = ch;
                        if (ch == 10) {
                            this.ch = this.readCh();
                            ++this.crlfCount;
                        }
                        else {
                            ++this.crCount;
                        }
                        this.addString(10);
                        continue;
                    }
                    default: {
                        this.addString(this.ch);
                        this.ch = this.readCh();
                        continue;
                    }
                }
            }
        }
    }
    
    void parseContent() throws IOException {
        final Thread currentThread = Thread.currentThread();
        while (!currentThread.isInterrupted()) {
            int ch = this.ch;
            this.currentBlockStartPos = this.currentPosition;
            if (this.recent == this.dtd.script) {
                this.parseScript();
                this.last = this.makeTag(this.dtd.getElement("comment"), true);
                String s = new String(this.getChars(0)).trim();
                final int n = "<!--".length() + "-->".length();
                if (s.startsWith("<!--") && s.endsWith("-->") && s.length() >= n) {
                    s = s.substring("<!--".length(), s.length() - "-->".length());
                }
                this.handleComment(s.toCharArray());
                this.endTag(false);
                this.lastBlockStartPos = this.currentPosition;
            }
            else {
                switch (ch) {
                    case 60: {
                        this.parseTag();
                        this.lastBlockStartPos = this.currentPosition;
                        continue;
                    }
                    case 47: {
                        this.ch = this.readCh();
                        if (this.stack != null && this.stack.net) {
                            this.endTag(false);
                            continue;
                        }
                        if (this.textpos != 0) {
                            break;
                        }
                        if (!this.legalElementContext(this.dtd.pcdata)) {
                            this.error("unexpected.pcdata");
                        }
                        if (this.last.breaksFlow()) {
                            this.space = false;
                            break;
                        }
                        break;
                    }
                    case -1: {
                        return;
                    }
                    case 38: {
                        if (this.textpos == 0) {
                            if (!this.legalElementContext(this.dtd.pcdata)) {
                                this.error("unexpected.pcdata");
                            }
                            if (this.last.breaksFlow()) {
                                this.space = false;
                            }
                        }
                        final char[] entityReference = this.parseEntityReference();
                        if (this.textpos + entityReference.length + 1 > this.text.length) {
                            final char[] text = new char[Math.max(this.textpos + entityReference.length + 128, this.text.length * 2)];
                            System.arraycopy(this.text, 0, text, 0, this.text.length);
                            this.text = text;
                        }
                        if (this.space) {
                            this.space = false;
                            this.text[this.textpos++] = ' ';
                        }
                        System.arraycopy(entityReference, 0, this.text, this.textpos, entityReference.length);
                        this.textpos += entityReference.length;
                        this.ignoreSpace = false;
                        continue;
                    }
                    case 10: {
                        ++this.ln;
                        ++this.lfCount;
                        this.ch = this.readCh();
                        if (this.stack != null && this.stack.pre) {
                            break;
                        }
                        if (this.textpos == 0) {
                            this.lastBlockStartPos = this.currentPosition;
                        }
                        if (!this.ignoreSpace) {
                            this.space = true;
                            continue;
                        }
                        continue;
                    }
                    case 13: {
                        ++this.ln;
                        ch = 10;
                        final int ch2 = this.readCh();
                        this.ch = ch2;
                        if (ch2 == 10) {
                            this.ch = this.readCh();
                            ++this.crlfCount;
                        }
                        else {
                            ++this.crCount;
                        }
                        if (this.stack != null && this.stack.pre) {
                            break;
                        }
                        if (this.textpos == 0) {
                            this.lastBlockStartPos = this.currentPosition;
                        }
                        if (!this.ignoreSpace) {
                            this.space = true;
                            continue;
                        }
                        continue;
                    }
                    case 9:
                    case 32: {
                        this.ch = this.readCh();
                        if (this.stack != null && this.stack.pre) {
                            break;
                        }
                        if (this.textpos == 0) {
                            this.lastBlockStartPos = this.currentPosition;
                        }
                        if (!this.ignoreSpace) {
                            this.space = true;
                            continue;
                        }
                        continue;
                    }
                    default: {
                        if (this.textpos == 0) {
                            if (!this.legalElementContext(this.dtd.pcdata)) {
                                this.error("unexpected.pcdata");
                            }
                            if (this.last.breaksFlow()) {
                                this.space = false;
                            }
                        }
                        this.ch = this.readCh();
                        break;
                    }
                }
                if (this.textpos + 2 > this.text.length) {
                    final char[] text2 = new char[this.text.length + 128];
                    System.arraycopy(this.text, 0, text2, 0, this.text.length);
                    this.text = text2;
                }
                if (this.space) {
                    if (this.textpos == 0) {
                        --this.lastBlockStartPos;
                    }
                    this.text[this.textpos++] = ' ';
                    this.space = false;
                }
                this.text[this.textpos++] = (char)ch;
                this.ignoreSpace = false;
            }
        }
        currentThread.interrupt();
    }
    
    String getEndOfLineString() {
        if (this.crlfCount >= this.crCount) {
            if (this.lfCount >= this.crlfCount) {
                return "\n";
            }
            return "\r\n";
        }
        else {
            if (this.crCount > this.lfCount) {
                return "\r";
            }
            return "\n";
        }
    }
    
    public synchronized void parse(final Reader in) throws IOException {
        this.in = in;
        this.ln = 1;
        this.seenHtml = false;
        this.seenHead = false;
        this.seenBody = false;
        final int crCount = 0;
        this.crlfCount = crCount;
        this.lfCount = crCount;
        this.crCount = crCount;
        try {
            this.ch = this.readCh();
            this.text = new char[1024];
            this.str = new char[128];
            this.parseContent();
            while (this.stack != null) {
                this.endTag(true);
            }
            in.close();
        }
        catch (final IOException ex) {
            this.errorContext();
            this.error("ioexception");
            throw ex;
        }
        catch (final Exception ex2) {
            this.errorContext();
            this.error("exception", ex2.getClass().getName(), ex2.getMessage());
            ex2.printStackTrace();
        }
        catch (final ThreadDeath threadDeath) {
            this.errorContext();
            this.error("terminated");
            threadDeath.printStackTrace();
            throw threadDeath;
        }
        finally {
            while (this.stack != null) {
                this.handleEndTag(this.stack.tag);
                this.stack = this.stack.next;
            }
            this.text = null;
            this.str = null;
        }
    }
    
    private final int readCh() throws IOException {
        if (this.pos >= this.len) {
            try {
                this.len = this.in.read(this.buf);
            }
            catch (final InterruptedIOException ex) {
                throw ex;
            }
            if (this.len <= 0) {
                return -1;
            }
            this.pos = 0;
        }
        ++this.currentPosition;
        return this.buf[this.pos++];
    }
    
    protected int getCurrentPos() {
        return this.currentPosition;
    }
    
    static {
        cp1252Map = new char[] { '\u201a', '\u0192', '\u201e', '\u2026', '\u2020', '\u2021', '\u02c6', '\u2030', '\u0160', '\u2039', '\u0152', '\u008d', '\u008e', '\u008f', '\u0090', '\u2018', '\u2019', '\u201c', '\u201d', '\u2022', '\u2013', '\u2014', '\u02dc', '\u2122', '\u0161', '\u203a', '\u0153', '\u009d', '\u009e', '\u0178' };
        SCRIPT_END_TAG = "</script>".toCharArray();
        SCRIPT_END_TAG_UPPER_CASE = "</SCRIPT>".toCharArray();
    }
}
