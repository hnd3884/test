package org.w3c.tidy;

public class Node
{
    public static final short ROOT_NODE = 0;
    public static final short DOCTYPE_TAG = 1;
    public static final short COMMENT_TAG = 2;
    public static final short PROC_INS_TAG = 3;
    public static final short TEXT_NODE = 4;
    public static final short START_TAG = 5;
    public static final short END_TAG = 6;
    public static final short START_END_TAG = 7;
    public static final short CDATA_TAG = 8;
    public static final short SECTION_TAG = 9;
    public static final short ASP_TAG = 10;
    public static final short JSTE_TAG = 11;
    public static final short PHP_TAG = 12;
    public static final short XML_DECL = 13;
    private static final String[] NODETYPE_STRING;
    protected Node parent;
    protected Node prev;
    protected Node next;
    protected Node last;
    protected int start;
    protected int end;
    protected byte[] textarray;
    protected short type;
    protected boolean closed;
    protected boolean implicit;
    protected boolean linebreak;
    protected Dict was;
    protected Dict tag;
    protected String element;
    protected AttVal attributes;
    protected Node content;
    protected org.w3c.dom.Node adapter;
    
    public Node() {
        this((short)4, null, 0, 0);
    }
    
    public Node(final short type, final byte[] textarray, final int start, final int end) {
        this.parent = null;
        this.prev = null;
        this.next = null;
        this.last = null;
        this.start = start;
        this.end = end;
        this.textarray = textarray;
        this.type = type;
        this.closed = false;
        this.implicit = false;
        this.linebreak = false;
        this.was = null;
        this.tag = null;
        this.element = null;
        this.attributes = null;
        this.content = null;
    }
    
    public Node(final short type, final byte[] textarray, final int start, final int end, final String element, final TagTable tagTable) {
        this.parent = null;
        this.prev = null;
        this.next = null;
        this.last = null;
        this.start = start;
        this.end = end;
        this.textarray = textarray;
        this.type = type;
        this.closed = false;
        this.implicit = false;
        this.linebreak = false;
        this.was = null;
        this.tag = null;
        this.element = element;
        this.attributes = null;
        this.content = null;
        if (type == 5 || type == 7 || type == 6) {
            tagTable.findTag(this);
        }
    }
    
    public AttVal getAttrByName(final String s) {
        AttVal attVal;
        for (attVal = this.attributes; attVal != null && (s == null || attVal.attribute == null || !attVal.attribute.equals(s)); attVal = attVal.next) {}
        return attVal;
    }
    
    public void checkAttributes(final Lexer lexer) {
        for (AttVal attVal = this.attributes; attVal != null; attVal = attVal.next) {
            attVal.checkAttribute(lexer, this);
        }
    }
    
    public void repairDuplicateAttributes(final Lexer lexer) {
        for (AttVal attVal = this.attributes; attVal != null; attVal = attVal.next) {
            if (attVal.asp == null && attVal.php == null) {
                AttVal attVal2 = attVal.next;
                while (attVal2 != null) {
                    if (attVal2.asp == null && attVal2.php == null && attVal.attribute != null && attVal.attribute.equalsIgnoreCase(attVal2.attribute)) {
                        if ("class".equalsIgnoreCase(attVal2.attribute) && lexer.configuration.joinClasses) {
                            attVal2.value = attVal2.value + " " + attVal.value;
                            final AttVal next = attVal.next;
                            if (next.next == null) {
                                attVal2 = null;
                            }
                            else {
                                attVal2 = attVal2.next;
                            }
                            lexer.report.attrError(lexer, this, attVal, (short)68);
                            this.removeAttribute(attVal);
                            attVal = next;
                        }
                        else if ("style".equalsIgnoreCase(attVal2.attribute) && lexer.configuration.joinStyles) {
                            final int n = attVal2.value.length() - 1;
                            if (attVal2.value.charAt(n) == ';') {
                                attVal2.value = attVal2.value + " " + attVal.value;
                            }
                            else if (attVal2.value.charAt(n) == '}') {
                                attVal2.value = attVal2.value + " { " + attVal.value + " }";
                            }
                            else {
                                attVal2.value = attVal2.value + "; " + attVal.value;
                            }
                            final AttVal next2 = attVal.next;
                            if (next2.next == null) {
                                attVal2 = null;
                            }
                            else {
                                attVal2 = attVal2.next;
                            }
                            lexer.report.attrError(lexer, this, attVal, (short)68);
                            this.removeAttribute(attVal);
                            attVal = next2;
                        }
                        else if (lexer.configuration.duplicateAttrs == 0) {
                            final AttVal next3 = attVal2.next;
                            lexer.report.attrError(lexer, this, attVal2, (short)55);
                            this.removeAttribute(attVal2);
                            attVal2 = next3;
                        }
                        else {
                            final AttVal next4 = attVal.next;
                            if (attVal.next == null) {
                                attVal2 = null;
                            }
                            else {
                                attVal2 = attVal2.next;
                            }
                            lexer.report.attrError(lexer, this, attVal, (short)55);
                            this.removeAttribute(attVal);
                            attVal = next4;
                        }
                    }
                    else {
                        attVal2 = attVal2.next;
                    }
                }
            }
            else {}
        }
    }
    
    public void addAttribute(final String s, final String s2) {
        final AttVal attVal = new AttVal(null, null, null, null, 34, s, s2);
        attVal.dict = AttributeTable.getDefaultAttributeTable().findAttribute(attVal);
        if (this.attributes == null) {
            this.attributes = attVal;
        }
        else {
            AttVal attVal2;
            for (attVal2 = this.attributes; attVal2.next != null; attVal2 = attVal2.next) {}
            attVal2.next = attVal;
        }
    }
    
    public void removeAttribute(final AttVal attVal) {
        AttVal attVal2 = null;
        AttVal next;
        for (AttVal attributes = this.attributes; attributes != null; attributes = next) {
            next = attributes.next;
            if (attributes == attVal) {
                if (attVal2 != null) {
                    attVal2.next = next;
                }
                else {
                    this.attributes = next;
                }
            }
            else {
                attVal2 = attributes;
            }
        }
    }
    
    public Node findDocType() {
        Node node;
        for (node = this.content; node != null && node.type != 1; node = node.next) {}
        return node;
    }
    
    public void discardDocType() {
        final Node docType = this.findDocType();
        if (docType != null) {
            if (docType.prev != null) {
                docType.prev.next = docType.next;
            }
            else {
                docType.parent.content = docType.next;
            }
            if (docType.next != null) {
                docType.next.prev = docType.prev;
            }
            docType.next = null;
        }
    }
    
    public static Node discardElement(final Node node) {
        Node next = null;
        if (node != null) {
            next = node.next;
            node.removeNode();
        }
        return next;
    }
    
    public void insertNodeAtStart(final Node content) {
        content.parent = this;
        if (this.content == null) {
            this.last = content;
        }
        else {
            this.content.prev = content;
        }
        content.next = this.content;
        content.prev = null;
        this.content = content;
    }
    
    public void insertNodeAtEnd(final Node last) {
        last.parent = this;
        last.prev = this.last;
        if (this.last != null) {
            this.last.next = last;
        }
        else {
            this.content = last;
        }
        this.last = last;
    }
    
    public static void insertNodeAsParent(final Node node, final Node prev) {
        prev.content = node;
        prev.last = node;
        prev.parent = node.parent;
        node.parent = prev;
        if (prev.parent.content == node) {
            prev.parent.content = prev;
        }
        if (prev.parent.last == node) {
            prev.parent.last = prev;
        }
        prev.prev = node.prev;
        node.prev = null;
        if (prev.prev != null) {
            prev.prev.next = prev;
        }
        prev.next = node.next;
        node.next = null;
        if (prev.next != null) {
            prev.next.prev = prev;
        }
    }
    
    public static void insertNodeBeforeElement(final Node next, final Node content) {
        final Node parent = next.parent;
        content.parent = parent;
        content.next = next;
        content.prev = next.prev;
        next.prev = content;
        if (content.prev != null) {
            content.prev.next = content;
        }
        if (parent != null && parent.content == next) {
            parent.content = content;
        }
    }
    
    public void insertNodeAfterElement(final Node next) {
        final Node parent = this.parent;
        next.parent = parent;
        if (parent != null && parent.last == this) {
            parent.last = next;
        }
        else {
            next.next = this.next;
            if (next.next != null) {
                next.next.prev = next;
            }
        }
        this.next = next;
        next.prev = this;
    }
    
    public static void trimEmptyElement(final Lexer lexer, final Node node) {
        if (lexer.configuration.trimEmpty) {
            final TagTable tt = lexer.configuration.tt;
            if (lexer.canPrune(node)) {
                if (node.type != 4) {
                    lexer.report.warning(lexer, node, null, (short)23);
                }
                discardElement(node);
            }
            else if (node.tag == tt.tagP && node.content == null) {
                final Node inferredTag = lexer.inferredTag("br");
                coerceNode(lexer, node, tt.tagBr);
                node.insertNodeAfterElement(inferredTag);
            }
        }
    }
    
    public static void trimTrailingSpace(final Lexer lexer, final Node node, final Node node2) {
        final TagTable tt = lexer.configuration.tt;
        if (node2 != null && node2.type == 4) {
            if (node2.end > node2.start) {
                final byte b = lexer.lexbuf[node2.end - 1];
                if (b == 160 || b == 32) {
                    if (b == 160 && (node.tag == tt.tagTd || node.tag == tt.tagTh)) {
                        if (node2.end > node2.start + 1) {
                            --node2.end;
                        }
                    }
                    else {
                        --node2.end;
                        if (TidyUtils.toBoolean(node.tag.model & 0x10) && !TidyUtils.toBoolean(node.tag.model & 0x400)) {
                            lexer.insertspace = true;
                        }
                    }
                }
            }
            if (node2.start == node2.end) {
                trimEmptyElement(lexer, node2);
            }
        }
    }
    
    protected static Node escapeTag(final Lexer lexer, final Node node) {
        final Node node2 = lexer.newNode();
        node2.start = lexer.lexsize;
        node2.textarray = node.textarray;
        lexer.addByte(60);
        if (node.type == 6) {
            lexer.addByte(47);
        }
        if (node.element != null) {
            lexer.addStringLiteral(node.element);
        }
        else if (node.type == 1) {
            lexer.addByte(33);
            lexer.addByte(68);
            lexer.addByte(79);
            lexer.addByte(67);
            lexer.addByte(84);
            lexer.addByte(89);
            lexer.addByte(80);
            lexer.addByte(69);
            lexer.addByte(32);
            for (int i = node.start; i < node.end; ++i) {
                lexer.addByte(lexer.lexbuf[i]);
            }
        }
        if (node.type == 7) {
            lexer.addByte(47);
        }
        lexer.addByte(62);
        node2.end = lexer.lexsize;
        return node2;
    }
    
    public boolean isBlank(final Lexer lexer) {
        if (this.type == 4) {
            if (this.end == this.start) {
                return true;
            }
            if (this.end == this.start + 1 && lexer.lexbuf[this.end - 1] == 32) {
                return true;
            }
        }
        return false;
    }
    
    public static void trimInitialSpace(final Lexer lexer, final Node next, final Node node) {
        if (node.type == 4 && node.textarray[node.start] == 32 && node.start < node.end) {
            if (TidyUtils.toBoolean(next.tag.model & 0x10) && !TidyUtils.toBoolean(next.tag.model & 0x400) && next.parent.content != next) {
                final Node prev = next.prev;
                if (prev != null && prev.type == 4) {
                    if (prev.textarray[prev.end - 1] != 32) {
                        prev.textarray[prev.end++] = 32;
                    }
                    ++next.start;
                }
                else {
                    final Node node2 = lexer.newNode();
                    if (next.start >= next.end) {
                        node2.start = 0;
                        node2.end = 1;
                        node2.textarray = new byte[1];
                    }
                    else {
                        node2.start = next.start++;
                        node2.end = next.start;
                        node2.textarray = next.textarray;
                    }
                    node2.textarray[node2.start] = 32;
                    node2.prev = prev;
                    if (prev != null) {
                        prev.next = node2;
                    }
                    node2.next = next;
                    next.prev = node2;
                    node2.parent = next.parent;
                }
            }
            ++node.start;
        }
    }
    
    public static void trimSpaces(final Lexer lexer, final Node node) {
        final Node content = node.content;
        final TagTable tt = lexer.configuration.tt;
        if (content != null && content.type == 4 && node.tag != tt.tagPre) {
            trimInitialSpace(lexer, node, content);
        }
        final Node last = node.last;
        if (last != null && last.type == 4) {
            trimTrailingSpace(lexer, node, last);
        }
    }
    
    public boolean isDescendantOf(final Dict dict) {
        for (Node node = this.parent; node != null; node = node.parent) {
            if (node.tag == dict) {
                return true;
            }
        }
        return false;
    }
    
    public static void insertDocType(final Lexer lexer, Node parent, final Node node) {
        final TagTable tt = lexer.configuration.tt;
        lexer.report.warning(lexer, parent, node, (short)34);
        while (parent.tag != tt.tagHtml) {
            parent = parent.parent;
        }
        insertNodeBeforeElement(parent, node);
    }
    
    public Node findBody(final TagTable tagTable) {
        Node node;
        for (node = this.content; node != null && node.tag != tagTable.tagHtml; node = node.next) {}
        if (node == null) {
            return null;
        }
        Node node2;
        for (node2 = node.content; node2 != null && node2.tag != tagTable.tagBody && node2.tag != tagTable.tagFrameset; node2 = node2.next) {}
        if (node2.tag == tagTable.tagFrameset) {
            for (node2 = node2.content; node2 != null && node2.tag != tagTable.tagNoframes; node2 = node2.next) {}
            if (node2 != null) {
                for (node2 = node2.content; node2 != null && node2.tag != tagTable.tagBody; node2 = node2.next) {}
            }
        }
        return node2;
    }
    
    public boolean isElement() {
        return this.type == 5 || this.type == 7;
    }
    
    public static void moveBeforeTable(final Node node, final Node next, final TagTable tagTable) {
        Node next2 = node.parent;
        while (next2 != null) {
            if (next2.tag == tagTable.tagTable) {
                if (next2.parent.content == next2) {
                    next2.parent.content = next;
                }
                next.prev = next2.prev;
                next.next = next2;
                next2.prev = next;
                next.parent = next2.parent;
                if (next.prev != null) {
                    next.prev.next = next;
                    break;
                }
                break;
            }
            else {
                next2 = next2.parent;
            }
        }
    }
    
    public static void fixEmptyRow(final Lexer lexer, final Node node) {
        if (node.content == null) {
            final Node inferredTag = lexer.inferredTag("td");
            node.insertNodeAtEnd(inferredTag);
            lexer.report.warning(lexer, node, inferredTag, (short)12);
        }
    }
    
    public static void coerceNode(final Lexer lexer, final Node node, final Dict tag) {
        lexer.report.warning(lexer, node, lexer.inferredTag(tag.name), (short)20);
        node.was = node.tag;
        node.tag = tag;
        node.type = 5;
        node.implicit = true;
        node.element = tag.name;
    }
    
    public void removeNode() {
        if (this.prev != null) {
            this.prev.next = this.next;
        }
        if (this.next != null) {
            this.next.prev = this.prev;
        }
        if (this.parent != null) {
            if (this.parent.content == this) {
                this.parent.content = this.next;
            }
            if (this.parent.last == this) {
                this.parent.last = this.prev;
            }
        }
        this.parent = null;
        this.prev = null;
        this.next = null;
    }
    
    public static boolean insertMisc(final Node node, final Node node2) {
        if (node2.type == 2 || node2.type == 3 || node2.type == 8 || node2.type == 9 || node2.type == 10 || node2.type == 11 || node2.type == 12 || node2.type == 13) {
            node.insertNodeAtEnd(node2);
            return true;
        }
        return false;
    }
    
    public boolean isNewNode() {
        return this.tag == null || TidyUtils.toBoolean(this.tag.model & 0x100000);
    }
    
    public boolean hasOneChild() {
        return this.content != null && this.content.next == null;
    }
    
    public Node findHTML(final TagTable tagTable) {
        Node node;
        for (node = this.content; node != null && node.tag != tagTable.tagHtml; node = node.next) {}
        return node;
    }
    
    public Node findHEAD(final TagTable tagTable) {
        Node node = this.findHTML(tagTable);
        if (node != null) {
            for (node = node.content; node != null && node.tag != tagTable.tagHead; node = node.next) {}
        }
        return node;
    }
    
    public Node findTITLE(final TagTable tagTable) {
        Node node = this.findHEAD(tagTable);
        if (node != null) {
            for (node = node.content; node != null && node.tag != tagTable.tagTitle; node = node.next) {}
        }
        return node;
    }
    
    public boolean checkNodeIntegrity() {
        if (this.prev != null && this.prev.next != this) {
            return false;
        }
        if (this.next != null && (this.next == this || this.next.prev != this)) {
            return false;
        }
        if (this.parent != null) {
            if (this.prev == null && this.parent.content != this) {
                return false;
            }
            if (this.next == null && this.parent.last != this) {
                return false;
            }
        }
        for (Node node = this.content; node != null; node = node.next) {
            if (node.parent != this || !node.checkNodeIntegrity()) {
                return false;
            }
        }
        return true;
    }
    
    public void addClass(final String s) {
        final AttVal attrByName = this.getAttrByName("class");
        if (attrByName != null) {
            attrByName.value = attrByName.value + " " + s;
        }
        else {
            this.addAttribute("class", s);
        }
    }
    
    public String toString() {
        String s = "";
        for (Node next = this; next != null; next = next.next) {
            final String string = s + "[Node type=" + Node.NODETYPE_STRING[next.type] + ",element=";
            String s2;
            if (next.element != null) {
                s2 = string + next.element;
            }
            else {
                s2 = string + "null";
            }
            if (next.type == 4 || next.type == 2 || next.type == 3) {
                final String string2 = s2 + ",text=";
                if (next.textarray != null && next.start <= next.end) {
                    s2 = string2 + "\"" + TidyUtils.getString(next.textarray, next.start, next.end - next.start) + "\"";
                }
                else {
                    s2 = string2 + "null";
                }
            }
            final String string3 = s2 + ",content=";
            String s3;
            if (next.content != null) {
                s3 = string3 + next.content.toString();
            }
            else {
                s3 = string3 + "null";
            }
            s = s3 + "]";
            if (next.next != null) {
                s += ",";
            }
        }
        return s;
    }
    
    protected org.w3c.dom.Node getAdapter() {
        if (this.adapter == null) {
            switch (this.type) {
                case 0: {
                    this.adapter = new DOMDocumentImpl(this);
                    break;
                }
                case 5:
                case 7: {
                    this.adapter = new DOMElementImpl(this);
                    break;
                }
                case 1: {
                    this.adapter = new DOMDocumentTypeImpl(this);
                    break;
                }
                case 2: {
                    this.adapter = new DOMCommentImpl(this);
                    break;
                }
                case 4: {
                    this.adapter = new DOMTextImpl(this);
                    break;
                }
                case 8: {
                    this.adapter = new DOMCDATASectionImpl(this);
                    break;
                }
                case 3: {
                    this.adapter = new DOMProcessingInstructionImpl(this);
                    break;
                }
                default: {
                    this.adapter = new DOMNodeImpl(this);
                    break;
                }
            }
        }
        return this.adapter;
    }
    
    protected Node cloneNode(final boolean b) {
        final Node node = new Node(this.type, this.textarray, this.start, this.end);
        node.parent = this.parent;
        node.closed = this.closed;
        node.implicit = this.implicit;
        node.tag = this.tag;
        node.element = this.element;
        if (this.attributes != null) {
            node.attributes = (AttVal)this.attributes.clone();
        }
        if (b) {
            for (Node node2 = this.content; node2 != null; node2 = node2.next) {
                node.insertNodeAtEnd(node2.cloneNode(b));
            }
        }
        return node;
    }
    
    protected void setType(final short type) {
        this.type = type;
    }
    
    public boolean isJavaScript() {
        boolean b = false;
        if (this.attributes == null) {
            return true;
        }
        for (AttVal attVal = this.attributes; attVal != null; attVal = attVal.next) {
            if (("language".equalsIgnoreCase(attVal.attribute) || "type".equalsIgnoreCase(attVal.attribute)) && "javascript".equalsIgnoreCase(attVal.value)) {
                b = true;
            }
        }
        return b;
    }
    
    public boolean expectsContent() {
        return this.type == 5 && (this.tag == null || !TidyUtils.toBoolean(this.tag.model & 0x1));
    }
    
    static {
        NODETYPE_STRING = new String[] { "RootNode", "DocTypeTag", "CommentTag", "ProcInsTag", "TextNode", "StartTag", "EndTag", "StartEndTag", "SectionTag", "AspTag", "PhpTag", "XmlDecl" };
    }
}
