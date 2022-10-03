package org.w3c.tidy;

public final class ParserImpl
{
    public static final Parser HTML;
    public static final Parser HEAD;
    public static final Parser TITLE;
    public static final Parser SCRIPT;
    public static final Parser BODY;
    public static final Parser FRAMESET;
    public static final Parser INLINE;
    public static final Parser LIST;
    public static final Parser DEFLIST;
    public static final Parser PRE;
    public static final Parser BLOCK;
    public static final Parser TABLETAG;
    public static final Parser COLGROUP;
    public static final Parser ROWGROUP;
    public static final Parser ROW;
    public static final Parser NOFRAMES;
    public static final Parser SELECT;
    public static final Parser TEXT;
    public static final Parser EMPTY;
    public static final Parser OPTGROUP;
    
    private ParserImpl() {
    }
    
    protected static void parseTag(final Lexer lexer, final Node node, final short n) {
        if ((node.tag.model & 0x1) != 0x0) {
            lexer.waswhite = false;
        }
        else if ((node.tag.model & 0x10) == 0x0) {
            lexer.insertspace = false;
        }
        if (node.tag.getParser() == null) {
            return;
        }
        if (node.type == 7) {
            Node.trimEmptyElement(lexer, node);
            return;
        }
        node.tag.getParser().parse(lexer, node, n);
    }
    
    protected static void moveToHead(final Lexer lexer, Node parent, final Node node) {
        node.removeNode();
        final TagTable tt = lexer.configuration.tt;
        if (node.type == 5 || node.type == 7) {
            lexer.report.warning(lexer, parent, node, (short)11);
            while (parent.tag != tt.tagHtml) {
                parent = parent.parent;
            }
            for (Node node2 = parent.content; node2 != null; node2 = node2.next) {
                if (node2.tag == tt.tagHead) {
                    node2.insertNodeAtEnd(node);
                    break;
                }
            }
            if (node.tag.getParser() != null) {
                parseTag(lexer, node, (short)0);
            }
        }
        else {
            lexer.report.warning(lexer, parent, node, (short)8);
        }
    }
    
    static void moveNodeToBody(final Lexer lexer, final Node node) {
        node.removeNode();
        lexer.root.findBody(lexer.configuration.tt).insertNodeAtEnd(node);
    }
    
    public static Node parseDocument(final Lexer lexer) {
        Node node = null;
        final TagTable tt = lexer.configuration.tt;
        final Node node2 = lexer.newNode();
        node2.type = 0;
        lexer.root = node2;
        Node token;
        while ((token = lexer.getToken((short)0)) != null) {
            if (Node.insertMisc(node2, token)) {
                continue;
            }
            if (token.type == 1) {
                if (node == null) {
                    node2.insertNodeAtEnd(token);
                    node = token;
                }
                else {
                    lexer.report.warning(lexer, node2, token, (short)8);
                }
            }
            else {
                if (token.type != 6) {
                    Node inferredTag;
                    if (token.type != 5 || token.tag != tt.tagHtml) {
                        lexer.ungetToken();
                        inferredTag = lexer.inferredTag("html");
                    }
                    else {
                        inferredTag = token;
                    }
                    if (node2.findDocType() == null && !lexer.configuration.bodyOnly) {
                        lexer.report.warning(lexer, null, null, (short)44);
                    }
                    node2.insertNodeAtEnd(inferredTag);
                    ParserImpl.HTML.parse(lexer, inferredTag, (short)0);
                    break;
                }
                lexer.report.warning(lexer, node2, token, (short)8);
            }
        }
        if (lexer.root.findHTML(lexer.configuration.tt) == null) {
            final Node inferredTag2 = lexer.inferredTag("html");
            lexer.root.insertNodeAtEnd(inferredTag2);
            ParserImpl.HTML.parse(lexer, inferredTag2, (short)0);
        }
        if (lexer.root.findTITLE(lexer.configuration.tt) == null) {
            final Node head = lexer.root.findHEAD(lexer.configuration.tt);
            lexer.report.warning(lexer, head, null, (short)17);
            head.insertNodeAtEnd(lexer.inferredTag("title"));
        }
        return node2;
    }
    
    public static boolean XMLPreserveWhiteSpace(final Node node, final TagTable tagTable) {
        for (AttVal attVal = node.attributes; attVal != null; attVal = attVal.next) {
            if (attVal.attribute.equals("xml:space")) {
                return attVal.value.equals("preserve");
            }
        }
        return node.element != null && ("pre".equalsIgnoreCase(node.element) || "script".equalsIgnoreCase(node.element) || "style".equalsIgnoreCase(node.element) || (tagTable != null && tagTable.findParser(node) == ParserImpl.PRE) || "xsl:text".equalsIgnoreCase(node.element));
    }
    
    public static void parseXMLElement(final Lexer lexer, final Node node, short n) {
        if (XMLPreserveWhiteSpace(node, lexer.configuration.tt)) {
            n = 2;
        }
        Node token;
        while ((token = lexer.getToken(n)) != null) {
            if (token.type == 6 && token.element.equals(node.element)) {
                node.closed = true;
                break;
            }
            if (token.type == 6) {
                lexer.report.error(lexer, node, token, (short)13);
            }
            else {
                if (token.type == 5) {
                    parseXMLElement(lexer, token, n);
                }
                node.insertNodeAtEnd(token);
            }
        }
        final Node content = node.content;
        if (content != null && content.type == 4 && n != 2 && content.textarray[content.start] == 32) {
            final Node node2 = content;
            ++node2.start;
            if (content.start >= content.end) {
                Node.discardElement(content);
            }
        }
        final Node last = node.last;
        if (last != null && last.type == 4 && n != 2 && last.textarray[last.end - 1] == 32) {
            final Node node3 = last;
            --node3.end;
            if (last.start >= last.end) {
                Node.discardElement(last);
            }
        }
    }
    
    public static Node parseXMLDocument(final Lexer lexer) {
        final Node node = lexer.newNode();
        node.type = 0;
        Node node2 = null;
        lexer.configuration.xmlTags = true;
        Node token;
        while ((token = lexer.getToken((short)0)) != null) {
            if (token.type == 6) {
                lexer.report.warning(lexer, null, token, (short)13);
            }
            else {
                if (Node.insertMisc(node, token)) {
                    continue;
                }
                if (token.type == 1) {
                    if (node2 == null) {
                        node.insertNodeAtEnd(token);
                        node2 = token;
                    }
                    else {
                        lexer.report.warning(lexer, node, token, (short)8);
                    }
                }
                else if (token.type == 7) {
                    node.insertNodeAtEnd(token);
                }
                else {
                    if (token.type != 5) {
                        continue;
                    }
                    node.insertNodeAtEnd(token);
                    parseXMLElement(lexer, token, (short)0);
                }
            }
        }
        if (node2 != null && !lexer.checkDocTypeKeyWords(node2)) {
            lexer.report.warning(lexer, node2, null, (short)37);
        }
        if (lexer.configuration.xmlPi) {
            lexer.fixXmlDecl(node);
        }
        return node;
    }
    
    static void badForm(final Lexer lexer) {
        lexer.badForm = 1;
        ++lexer.errors;
    }
    
    static {
        HTML = new ParseHTML();
        HEAD = new ParseHead();
        TITLE = new ParseTitle();
        SCRIPT = new ParseScript();
        BODY = new ParseBody();
        FRAMESET = new ParseFrameSet();
        INLINE = new ParseInline();
        LIST = new ParseList();
        DEFLIST = new ParseDefList();
        PRE = new ParsePre();
        BLOCK = new ParseBlock();
        TABLETAG = new ParseTableTag();
        COLGROUP = new ParseColGroup();
        ROWGROUP = new ParseRowGroup();
        ROW = new ParseRow();
        NOFRAMES = new ParseNoFrames();
        SELECT = new ParseSelect();
        TEXT = new ParseText();
        EMPTY = new ParseEmpty();
        OPTGROUP = new ParseOptGroup();
    }
    
    public static class ParseBlock implements Parser
    {
        public void parse(final Lexer lexer, final Node node, final short n) {
            int istackbase = 0;
            final TagTable tt = lexer.configuration.tt;
            int n2 = 1;
            if ((node.tag.model & 0x1) != 0x0) {
                return;
            }
            if (node.tag == tt.tagForm && node.isDescendantOf(tt.tagForm)) {
                lexer.report.warning(lexer, node, null, (short)25);
            }
            if ((node.tag.model & 0x800) != 0x0) {
                istackbase = lexer.istackbase;
                lexer.istackbase = lexer.istack.size();
            }
            if ((node.tag.model & 0x20000) == 0x0) {
                lexer.inlineDup(null);
            }
            short n3 = 0;
            Node node2;
            while ((node2 = lexer.getToken(n3)) != null) {
                if (node2.type == 6 && node2.tag != null && (node2.tag == node.tag || node.was == node2.tag)) {
                    if ((node.tag.model & 0x800) != 0x0) {
                        while (lexer.istack.size() > lexer.istackbase) {
                            lexer.popInline(null);
                        }
                        lexer.istackbase = istackbase;
                    }
                    node.closed = true;
                    Node.trimSpaces(lexer, node);
                    Node.trimEmptyElement(lexer, node);
                    return;
                }
                if (node2.tag == tt.tagHtml || node2.tag == tt.tagHead || node2.tag == tt.tagBody) {
                    if (node2.type != 5 && node2.type != 7) {
                        continue;
                    }
                    lexer.report.warning(lexer, node, node2, (short)8);
                }
                else {
                    if (node2.type == 6) {
                        if (node2.tag == null) {
                            lexer.report.warning(lexer, node, node2, (short)8);
                            continue;
                        }
                        if (node2.tag == tt.tagBr) {
                            node2.type = 5;
                        }
                        else if (node2.tag == tt.tagP) {
                            Node.coerceNode(lexer, node2, tt.tagBr);
                            node.insertNodeAtEnd(node2);
                            node2 = lexer.inferredTag("br");
                        }
                        else {
                            for (Node node3 = node.parent; node3 != null; node3 = node3.parent) {
                                if (node2.tag == node3.tag) {
                                    if ((node.tag.model & 0x8000) == 0x0) {
                                        lexer.report.warning(lexer, node, node2, (short)7);
                                    }
                                    lexer.ungetToken();
                                    if ((node.tag.model & 0x800) != 0x0) {
                                        while (lexer.istack.size() > lexer.istackbase) {
                                            lexer.popInline(null);
                                        }
                                        lexer.istackbase = istackbase;
                                    }
                                    Node.trimSpaces(lexer, node);
                                    Node.trimEmptyElement(lexer, node);
                                    return;
                                }
                            }
                            if (lexer.exiled && node2.tag.model != 0 && (node2.tag.model & 0x80) != 0x0) {
                                lexer.ungetToken();
                                Node.trimSpaces(lexer, node);
                                Node.trimEmptyElement(lexer, node);
                                return;
                            }
                        }
                    }
                    if (node2.type == 4) {
                        boolean b = false;
                        if (node2.type == 4 && node2.end <= node2.start + 1 && lexer.lexbuf[node2.start] == 32) {
                            b = true;
                        }
                        if (lexer.configuration.encloseBlockText && !b) {
                            lexer.ungetToken();
                            final Node inferredTag = lexer.inferredTag("p");
                            node.insertNodeAtEnd(inferredTag);
                            ParserImpl.parseTag(lexer, inferredTag, (short)1);
                        }
                        else {
                            if (n2 != 0) {
                                n2 = 0;
                                if ((node.tag.model & 0x20000) == 0x0 && lexer.inlineDup(node2) > 0) {
                                    continue;
                                }
                            }
                            node.insertNodeAtEnd(node2);
                            n3 = 1;
                            if (node.tag != tt.tagBody && node.tag != tt.tagMap && node.tag != tt.tagBlockquote && node.tag != tt.tagForm && node.tag != tt.tagNoscript) {
                                continue;
                            }
                            lexer.constrainVersion(-5);
                        }
                    }
                    else {
                        if (Node.insertMisc(node, node2)) {
                            continue;
                        }
                        if (node2.tag == tt.tagParam) {
                            if ((node.tag.model & 0x1000) != 0x0 && (node2.type == 5 || node2.type == 7)) {
                                node.insertNodeAtEnd(node2);
                            }
                            else {
                                lexer.report.warning(lexer, node, node2, (short)8);
                            }
                        }
                        else if (node2.tag == tt.tagArea) {
                            if (node.tag == tt.tagMap && (node2.type == 5 || node2.type == 7)) {
                                node.insertNodeAtEnd(node2);
                            }
                            else {
                                lexer.report.warning(lexer, node, node2, (short)8);
                            }
                        }
                        else if (node2.tag == null) {
                            lexer.report.warning(lexer, node, node2, (short)8);
                        }
                        else {
                            if ((node2.tag.model & 0x10) == 0x0) {
                                if (node2.type != 5 && node2.type != 7) {
                                    if (node2.tag == tt.tagForm) {
                                        ParserImpl.badForm(lexer);
                                    }
                                    lexer.report.warning(lexer, node, node2, (short)8);
                                    continue;
                                }
                                if (node.tag == tt.tagLi && (node2.tag == tt.tagFrame || node2.tag == tt.tagFrameset || node2.tag == tt.tagOptgroup || node2.tag == tt.tagOption)) {
                                    lexer.report.warning(lexer, node, node2, (short)8);
                                    continue;
                                }
                                if (node.tag == tt.tagTd || node.tag == tt.tagTh) {
                                    if ((node2.tag.model & 0x4) != 0x0) {
                                        ParserImpl.moveToHead(lexer, node, node2);
                                        continue;
                                    }
                                    if ((node2.tag.model & 0x20) != 0x0) {
                                        lexer.ungetToken();
                                        node2 = lexer.inferredTag("ul");
                                        node2.addClass("noindent");
                                        lexer.excludeBlocks = true;
                                    }
                                    else if ((node2.tag.model & 0x40) != 0x0) {
                                        lexer.ungetToken();
                                        node2 = lexer.inferredTag("dl");
                                        lexer.excludeBlocks = true;
                                    }
                                    if ((node2.tag.model & 0x8) == 0x0) {
                                        lexer.ungetToken();
                                        Node.trimSpaces(lexer, node);
                                        Node.trimEmptyElement(lexer, node);
                                        return;
                                    }
                                }
                                else if ((node2.tag.model & 0x8) != 0x0) {
                                    if (lexer.excludeBlocks) {
                                        if ((node.tag.model & 0x8000) == 0x0) {
                                            lexer.report.warning(lexer, node, node2, (short)7);
                                        }
                                        lexer.ungetToken();
                                        if ((node.tag.model & 0x800) != 0x0) {
                                            lexer.istackbase = istackbase;
                                        }
                                        Node.trimSpaces(lexer, node);
                                        Node.trimEmptyElement(lexer, node);
                                        return;
                                    }
                                }
                                else {
                                    if ((node2.tag.model & 0x4) != 0x0) {
                                        ParserImpl.moveToHead(lexer, node, node2);
                                        continue;
                                    }
                                    if (node.tag == tt.tagForm && node.parent.tag == tt.tagTd && node.parent.implicit) {
                                        if (node2.tag == tt.tagTd) {
                                            lexer.report.warning(lexer, node, node2, (short)8);
                                            continue;
                                        }
                                        if (node2.tag == tt.tagTh) {
                                            lexer.report.warning(lexer, node, node2, (short)8);
                                            final Node parent = node.parent;
                                            parent.element = "th";
                                            parent.tag = tt.tagTh;
                                            continue;
                                        }
                                    }
                                    if ((node.tag.model & 0x8000) == 0x0 && !node.implicit) {
                                        lexer.report.warning(lexer, node, node2, (short)7);
                                    }
                                    lexer.ungetToken();
                                    if ((node2.tag.model & 0x20) != 0x0) {
                                        if (node.parent != null && node.parent.tag != null && node.parent.tag.getParser() == ParserImpl.LIST) {
                                            Node.trimSpaces(lexer, node);
                                            Node.trimEmptyElement(lexer, node);
                                            return;
                                        }
                                        node2 = lexer.inferredTag("ul");
                                        node2.addClass("noindent");
                                    }
                                    else if ((node2.tag.model & 0x40) != 0x0) {
                                        if (node.parent.tag == tt.tagDl) {
                                            Node.trimSpaces(lexer, node);
                                            Node.trimEmptyElement(lexer, node);
                                            return;
                                        }
                                        node2 = lexer.inferredTag("dl");
                                    }
                                    else if ((node2.tag.model & 0x80) != 0x0 || (node2.tag.model & 0x200) != 0x0) {
                                        node2 = lexer.inferredTag("table");
                                    }
                                    else {
                                        if ((node.tag.model & 0x800) != 0x0) {
                                            while (lexer.istack.size() > lexer.istackbase) {
                                                lexer.popInline(null);
                                            }
                                            lexer.istackbase = istackbase;
                                            Node.trimSpaces(lexer, node);
                                            Node.trimEmptyElement(lexer, node);
                                            return;
                                        }
                                        Node.trimSpaces(lexer, node);
                                        Node.trimEmptyElement(lexer, node);
                                        return;
                                    }
                                }
                            }
                            if (node2.type == 5 || node2.type == 7) {
                                if (TidyUtils.toBoolean(node2.tag.model & 0x10)) {
                                    if (n2 != 0 && !node2.implicit) {
                                        n2 = 0;
                                        if (!TidyUtils.toBoolean(node.tag.model & 0x20000) && lexer.inlineDup(node2) > 0) {
                                            continue;
                                        }
                                    }
                                    n3 = 1;
                                }
                                else {
                                    n2 = 1;
                                    n3 = 0;
                                }
                                if (node2.tag == tt.tagBr) {
                                    Node.trimSpaces(lexer, node);
                                }
                                node.insertNodeAtEnd(node2);
                                if (node2.implicit) {
                                    lexer.report.warning(lexer, node, node2, (short)15);
                                }
                                ParserImpl.parseTag(lexer, node2, (short)0);
                            }
                            else {
                                if (node2.type == 6) {
                                    lexer.popInline(node2);
                                }
                                lexer.report.warning(lexer, node, node2, (short)8);
                            }
                        }
                    }
                }
            }
            if ((node.tag.model & 0x8000) == 0x0) {
                lexer.report.warning(lexer, node, node2, (short)6);
            }
            if ((node.tag.model & 0x800) != 0x0) {
                while (lexer.istack.size() > lexer.istackbase) {
                    lexer.popInline(null);
                }
                lexer.istackbase = istackbase;
            }
            Node.trimSpaces(lexer, node);
            Node.trimEmptyElement(lexer, node);
        }
    }
    
    public static class ParseBody implements Parser
    {
        public void parse(final Lexer lexer, final Node node, final short n) {
            short n2 = 0;
            int n3 = 1;
            final TagTable tt = lexer.configuration.tt;
            Clean.bumpObject(lexer, node.parent);
            Node node2;
            while ((node2 = lexer.getToken(n2)) != null) {
                if (node2.tag == tt.tagHtml) {
                    if (node2.type == 5 || node2.type == 7 || lexer.seenEndHtml) {
                        lexer.report.warning(lexer, node, node2, (short)8);
                    }
                    else {
                        lexer.seenEndHtml = true;
                    }
                }
                else {
                    if (lexer.seenEndBody && (node2.type == 5 || node2.type == 6 || node2.type == 7)) {
                        lexer.report.warning(lexer, node, node2, (short)27);
                    }
                    if (node2.tag == node.tag && node2.type == 6) {
                        node.closed = true;
                        Node.trimSpaces(lexer, node);
                        lexer.seenEndBody = true;
                        n2 = 0;
                        if (node.parent.tag == tt.tagNoframes) {
                            break;
                        }
                        continue;
                    }
                    else {
                        if (node2.tag == tt.tagNoframes) {
                            if (node2.type == 5) {
                                node.insertNodeAtEnd(node2);
                                ParserImpl.BLOCK.parse(lexer, node2, n2);
                                continue;
                            }
                            if (node2.type == 6 && node.parent.tag == tt.tagNoframes) {
                                Node.trimSpaces(lexer, node);
                                lexer.ungetToken();
                                break;
                            }
                        }
                        if ((node2.tag == tt.tagFrame || node2.tag == tt.tagFrameset) && node.parent.tag == tt.tagNoframes) {
                            Node.trimSpaces(lexer, node);
                            lexer.ungetToken();
                            break;
                        }
                        boolean b = false;
                        if (node2.type == 4 && node2.end <= node2.start + 1 && node2.textarray[node2.start] == 32) {
                            b = true;
                        }
                        if (Node.insertMisc(node, node2)) {
                            continue;
                        }
                        if (node2.type == 4) {
                            if (b && n2 == 0) {
                                continue;
                            }
                            if (lexer.configuration.encloseBodyText && !b) {
                                lexer.ungetToken();
                                final Node inferredTag = lexer.inferredTag("p");
                                node.insertNodeAtEnd(inferredTag);
                                ParserImpl.parseTag(lexer, inferredTag, n2);
                                n2 = 1;
                            }
                            else {
                                lexer.constrainVersion(-6);
                                if (n3 != 0) {
                                    n3 = 0;
                                    if (lexer.inlineDup(node2) > 0) {
                                        continue;
                                    }
                                }
                                node.insertNodeAtEnd(node2);
                                n2 = 1;
                            }
                        }
                        else if (node2.type == 1) {
                            Node.insertDocType(lexer, node, node2);
                        }
                        else if (node2.tag == null || node2.tag == tt.tagParam) {
                            lexer.report.warning(lexer, node, node2, (short)8);
                        }
                        else {
                            lexer.excludeBlocks = false;
                            if (((node2.tag.model & 0x8) == 0x0 && (node2.tag.model & 0x10) == 0x0) || node2.tag == tt.tagInput) {
                                if ((node2.tag.model & 0x4) == 0x0) {
                                    lexer.report.warning(lexer, node, node2, (short)11);
                                }
                                if ((node2.tag.model & 0x2) != 0x0) {
                                    if (node2.tag == tt.tagBody && node.implicit && node.attributes == null) {
                                        node.attributes = node2.attributes;
                                        node2.attributes = null;
                                        continue;
                                    }
                                    continue;
                                }
                                else {
                                    if ((node2.tag.model & 0x4) != 0x0) {
                                        ParserImpl.moveToHead(lexer, node, node2);
                                        continue;
                                    }
                                    if ((node2.tag.model & 0x20) != 0x0) {
                                        lexer.ungetToken();
                                        node2 = lexer.inferredTag("ul");
                                        node2.addClass("noindent");
                                        lexer.excludeBlocks = true;
                                    }
                                    else if ((node2.tag.model & 0x40) != 0x0) {
                                        lexer.ungetToken();
                                        node2 = lexer.inferredTag("dl");
                                        lexer.excludeBlocks = true;
                                    }
                                    else if ((node2.tag.model & 0x380) != 0x0) {
                                        if (node2.type != 6) {
                                            lexer.ungetToken();
                                            node2 = lexer.inferredTag("table");
                                        }
                                        lexer.excludeBlocks = true;
                                    }
                                    else if (node2.tag == tt.tagInput) {
                                        lexer.ungetToken();
                                        node2 = lexer.inferredTag("form");
                                        lexer.excludeBlocks = true;
                                    }
                                    else {
                                        if ((node2.tag.model & 0x600) == 0x0) {
                                            lexer.ungetToken();
                                            return;
                                        }
                                        continue;
                                    }
                                }
                            }
                            if (node2.type == 6) {
                                if (node2.tag == tt.tagBr) {
                                    node2.type = 5;
                                }
                                else if (node2.tag == tt.tagP) {
                                    Node.coerceNode(lexer, node2, tt.tagBr);
                                    node.insertNodeAtEnd(node2);
                                    node2 = lexer.inferredTag("br");
                                }
                                else if ((node2.tag.model & 0x10) != 0x0) {
                                    lexer.popInline(node2);
                                }
                            }
                            if (node2.type == 5 || node2.type == 7) {
                                if ((node2.tag.model & 0x10) != 0x0 && (node2.tag.model & 0x20000) == 0x0) {
                                    if (node2.tag == tt.tagImg) {
                                        lexer.constrainVersion(-5);
                                    }
                                    else {
                                        lexer.constrainVersion(-6);
                                    }
                                    if (n3 != 0 && !node2.implicit) {
                                        n3 = 0;
                                        if (lexer.inlineDup(node2) > 0) {
                                            continue;
                                        }
                                    }
                                    n2 = 1;
                                }
                                else {
                                    n3 = 1;
                                    n2 = 0;
                                }
                                if (node2.implicit) {
                                    lexer.report.warning(lexer, node, node2, (short)15);
                                }
                                node.insertNodeAtEnd(node2);
                                ParserImpl.parseTag(lexer, node2, n2);
                            }
                            else {
                                lexer.report.warning(lexer, node, node2, (short)8);
                            }
                        }
                    }
                }
            }
        }
    }
    
    public static class ParseColGroup implements Parser
    {
        public void parse(final Lexer lexer, final Node node, final short n) {
            final TagTable tt = lexer.configuration.tt;
            if ((node.tag.model & 0x1) != 0x0) {
                return;
            }
            Node token;
            while ((token = lexer.getToken((short)0)) != null) {
                if (token.tag == node.tag && token.type == 6) {
                    node.closed = true;
                    return;
                }
                if (token.type == 6) {
                    if (token.tag == tt.tagForm) {
                        ParserImpl.badForm(lexer);
                        lexer.report.warning(lexer, node, token, (short)8);
                        continue;
                    }
                    for (Node node2 = node.parent; node2 != null; node2 = node2.parent) {
                        if (token.tag == node2.tag) {
                            lexer.ungetToken();
                            return;
                        }
                    }
                }
                if (token.type == 4) {
                    lexer.ungetToken();
                    return;
                }
                if (Node.insertMisc(node, token)) {
                    continue;
                }
                if (token.tag == null) {
                    lexer.report.warning(lexer, node, token, (short)8);
                }
                else {
                    if (token.tag != tt.tagCol) {
                        lexer.ungetToken();
                        return;
                    }
                    if (token.type == 6) {
                        lexer.report.warning(lexer, node, token, (short)8);
                    }
                    else {
                        node.insertNodeAtEnd(token);
                        ParserImpl.parseTag(lexer, token, (short)0);
                    }
                }
            }
        }
    }
    
    public static class ParseDefList implements Parser
    {
        public void parse(final Lexer lexer, Node inferredTag, final short n) {
            final TagTable tt = lexer.configuration.tt;
            if ((inferredTag.tag.model & 0x1) != 0x0) {
                return;
            }
            lexer.insert = -1;
            Node node;
            while ((node = lexer.getToken((short)0)) != null) {
                if (node.tag == inferredTag.tag && node.type == 6) {
                    inferredTag.closed = true;
                    Node.trimEmptyElement(lexer, inferredTag);
                    return;
                }
                if (Node.insertMisc(inferredTag, node)) {
                    continue;
                }
                if (node.type == 4) {
                    lexer.ungetToken();
                    node = lexer.inferredTag("dt");
                    lexer.report.warning(lexer, inferredTag, node, (short)12);
                }
                if (node.tag == null) {
                    lexer.report.warning(lexer, inferredTag, node, (short)8);
                }
                else {
                    if (node.type == 6) {
                        if (node.tag == tt.tagForm) {
                            ParserImpl.badForm(lexer);
                            lexer.report.warning(lexer, inferredTag, node, (short)8);
                            continue;
                        }
                        for (Node node2 = inferredTag.parent; node2 != null; node2 = node2.parent) {
                            if (node.tag == node2.tag) {
                                lexer.report.warning(lexer, inferredTag, node, (short)7);
                                lexer.ungetToken();
                                Node.trimEmptyElement(lexer, inferredTag);
                                return;
                            }
                        }
                    }
                    if (node.tag == tt.tagCenter) {
                        if (inferredTag.content != null) {
                            inferredTag.insertNodeAfterElement(node);
                        }
                        else {
                            Node.insertNodeBeforeElement(inferredTag, node);
                            Node.discardElement(inferredTag);
                        }
                        ParserImpl.parseTag(lexer, node, n);
                        inferredTag = lexer.inferredTag("dl");
                        node.insertNodeAfterElement(inferredTag);
                    }
                    else {
                        if (node.tag != tt.tagDt && node.tag != tt.tagDd) {
                            lexer.ungetToken();
                            if ((node.tag.model & 0x18) == 0x0) {
                                lexer.report.warning(lexer, inferredTag, node, (short)11);
                                Node.trimEmptyElement(lexer, inferredTag);
                                return;
                            }
                            if ((node.tag.model & 0x10) == 0x0 && lexer.excludeBlocks) {
                                Node.trimEmptyElement(lexer, inferredTag);
                                return;
                            }
                            node = lexer.inferredTag("dd");
                            lexer.report.warning(lexer, inferredTag, node, (short)12);
                        }
                        if (node.type == 6) {
                            lexer.report.warning(lexer, inferredTag, node, (short)8);
                        }
                        else {
                            inferredTag.insertNodeAtEnd(node);
                            ParserImpl.parseTag(lexer, node, (short)0);
                        }
                    }
                }
            }
            lexer.report.warning(lexer, inferredTag, node, (short)6);
            Node.trimEmptyElement(lexer, inferredTag);
        }
    }
    
    public static class ParseEmpty implements Parser
    {
        public void parse(final Lexer lexer, final Node node, final short n) {
            if (lexer.isvoyager) {
                final Node token = lexer.getToken(n);
                if (token != null && (token.type != 6 || token.tag != node.tag)) {
                    lexer.report.warning(lexer, node, token, (short)41);
                    lexer.ungetToken();
                }
            }
        }
    }
    
    public static class ParseFrameSet implements Parser
    {
        public void parse(final Lexer lexer, final Node node, final short n) {
            final TagTable tt = lexer.configuration.tt;
            lexer.badAccess |= 0x10;
            Node node2;
            while ((node2 = lexer.getToken((short)0)) != null) {
                if (node2.tag == node.tag && node2.type == 6) {
                    node.closed = true;
                    Node.trimSpaces(lexer, node);
                    return;
                }
                if (Node.insertMisc(node, node2)) {
                    continue;
                }
                if (node2.tag == null) {
                    lexer.report.warning(lexer, node, node2, (short)8);
                }
                else if ((node2.type == 5 || node2.type == 7) && node2.tag != null && (node2.tag.model & 0x4) != 0x0) {
                    ParserImpl.moveToHead(lexer, node, node2);
                }
                else {
                    if (node2.tag == tt.tagBody) {
                        lexer.ungetToken();
                        node2 = lexer.inferredTag("noframes");
                        lexer.report.warning(lexer, node, node2, (short)15);
                    }
                    if (node2.type == 5 && (node2.tag.model & 0x2000) != 0x0) {
                        node.insertNodeAtEnd(node2);
                        lexer.excludeBlocks = false;
                        ParserImpl.parseTag(lexer, node2, (short)1);
                    }
                    else if (node2.type == 7 && (node2.tag.model & 0x2000) != 0x0) {
                        node.insertNodeAtEnd(node2);
                    }
                    else {
                        lexer.report.warning(lexer, node, node2, (short)8);
                    }
                }
            }
            lexer.report.warning(lexer, node, node2, (short)6);
        }
    }
    
    public static class ParseHTML implements Parser
    {
        public void parse(final Lexer lexer, final Node node, final short n) {
            Node node2 = null;
            Node node3 = null;
            lexer.configuration.xmlTags = false;
            lexer.seenEndBody = false;
            final TagTable tt = lexer.configuration.tt;
            Node node4;
            while (true) {
                node4 = lexer.getToken((short)0);
                if (node4 == null) {
                    node4 = lexer.inferredTag("head");
                    break;
                }
                if (node4.tag == tt.tagHead) {
                    break;
                }
                if (node4.tag == node.tag && node4.type == 6) {
                    lexer.report.warning(lexer, node, node4, (short)8);
                }
                else {
                    if (Node.insertMisc(node, node4)) {
                        continue;
                    }
                    lexer.ungetToken();
                    node4 = lexer.inferredTag("head");
                    break;
                }
            }
            final Node node5 = node4;
            node.insertNodeAtEnd(node5);
            ParserImpl.HEAD.parse(lexer, node5, n);
            Node node6;
            while (true) {
                node6 = lexer.getToken((short)0);
                if (node6 == null) {
                    if (node2 == null) {
                        final Node inferredTag = lexer.inferredTag("body");
                        node.insertNodeAtEnd(inferredTag);
                        ParserImpl.BODY.parse(lexer, inferredTag, n);
                    }
                    return;
                }
                if (node6.tag == node.tag) {
                    if (node6.type != 5 && node2 == null) {
                        lexer.report.warning(lexer, node, node6, (short)8);
                    }
                    else {
                        if (node6.type != 6) {
                            continue;
                        }
                        lexer.seenEndHtml = true;
                    }
                }
                else {
                    if (Node.insertMisc(node, node6)) {
                        continue;
                    }
                    if (node6.tag == tt.tagBody) {
                        if (node6.type != 5) {
                            lexer.report.warning(lexer, node, node6, (short)8);
                        }
                        else {
                            if (node2 == null) {
                                lexer.constrainVersion(-17);
                                break;
                            }
                            lexer.ungetToken();
                            if (node3 == null) {
                                node3 = lexer.inferredTag("noframes");
                                node2.insertNodeAtEnd(node3);
                                lexer.report.warning(lexer, node, node3, (short)15);
                            }
                            ParserImpl.parseTag(lexer, node3, n);
                        }
                    }
                    else if (node6.tag == tt.tagFrameset) {
                        if (node6.type != 5) {
                            lexer.report.warning(lexer, node, node6, (short)8);
                        }
                        else {
                            if (node2 != null) {
                                lexer.report.error(lexer, node, node6, (short)18);
                            }
                            else {
                                node2 = node6;
                            }
                            node.insertNodeAtEnd(node6);
                            ParserImpl.parseTag(lexer, node6, n);
                            for (Node node7 = node2.content; node7 != null; node7 = node7.next) {
                                if (node7.tag == tt.tagNoframes) {
                                    node3 = node7;
                                }
                            }
                        }
                    }
                    else if (node6.tag == tt.tagNoframes) {
                        if (node6.type != 5) {
                            lexer.report.warning(lexer, node, node6, (short)8);
                        }
                        else {
                            if (node2 == null) {
                                lexer.report.warning(lexer, node, node6, (short)8);
                                node6 = lexer.inferredTag("body");
                                break;
                            }
                            if (node3 == null) {
                                node3 = node6;
                                node2.insertNodeAtEnd(node3);
                            }
                            ParserImpl.parseTag(lexer, node3, n);
                        }
                    }
                    else {
                        if (node6.type == 5 || node6.type == 7) {
                            if (node6.tag != null && (node6.tag.model & 0x4) != 0x0) {
                                ParserImpl.moveToHead(lexer, node, node6);
                                continue;
                            }
                            if (node2 != null && node6.tag == tt.tagFrame) {
                                lexer.report.warning(lexer, node, node6, (short)8);
                                continue;
                            }
                        }
                        lexer.ungetToken();
                        if (node2 == null) {
                            node6 = lexer.inferredTag("body");
                            lexer.constrainVersion(-17);
                            break;
                        }
                        if (node3 == null) {
                            node3 = lexer.inferredTag("noframes");
                            node2.insertNodeAtEnd(node3);
                        }
                        else {
                            lexer.report.warning(lexer, node, node6, (short)26);
                        }
                        lexer.constrainVersion(16);
                        ParserImpl.parseTag(lexer, node3, n);
                    }
                }
            }
            node.insertNodeAtEnd(node6);
            ParserImpl.parseTag(lexer, node6, n);
            lexer.seenEndHtml = true;
        }
    }
    
    public static class ParseHead implements Parser
    {
        public void parse(final Lexer lexer, final Node node, final short n) {
            int n2 = 0;
            int n3 = 0;
            final TagTable tt = lexer.configuration.tt;
            Node token;
            while ((token = lexer.getToken((short)0)) != null) {
                if (token.tag == node.tag && token.type == 6) {
                    node.closed = true;
                    break;
                }
                if (token.type == 4) {
                    lexer.report.warning(lexer, node, token, (short)11);
                    lexer.ungetToken();
                    break;
                }
                if (Node.insertMisc(node, token)) {
                    continue;
                }
                if (token.type == 1) {
                    Node.insertDocType(lexer, node, token);
                }
                else if (token.tag == null) {
                    lexer.report.warning(lexer, node, token, (short)8);
                }
                else {
                    if (!TidyUtils.toBoolean(token.tag.model & 0x4)) {
                        if (lexer.isvoyager) {
                            lexer.report.warning(lexer, node, token, (short)11);
                        }
                        lexer.ungetToken();
                        break;
                    }
                    if (token.type == 5 || token.type == 7) {
                        if (token.tag == tt.tagTitle) {
                            if (++n2 > 1) {
                                lexer.report.warning(lexer, node, token, (short)38);
                            }
                        }
                        else if (token.tag == tt.tagBase) {
                            if (++n3 > 1) {
                                lexer.report.warning(lexer, node, token, (short)38);
                            }
                        }
                        else if (token.tag == tt.tagNoscript) {
                            lexer.report.warning(lexer, node, token, (short)11);
                        }
                        node.insertNodeAtEnd(token);
                        ParserImpl.parseTag(lexer, token, (short)0);
                    }
                    else {
                        lexer.report.warning(lexer, node, token, (short)8);
                    }
                }
            }
        }
    }
    
    public static class ParseInline implements Parser
    {
        public void parse(final Lexer lexer, Node parent, short n) {
            final TagTable tt = lexer.configuration.tt;
            if (TidyUtils.toBoolean(parent.tag.model & 0x1)) {
                return;
            }
            if (TidyUtils.toBoolean(parent.tag.model & 0x8) || parent.tag == tt.tagDt) {
                lexer.inlineDup(null);
            }
            else if (TidyUtils.toBoolean(parent.tag.model & 0x10) && parent.tag != tt.tagA && parent.tag != tt.tagSpan) {
                lexer.pushInline(parent);
            }
            if (parent.tag == tt.tagNobr) {
                lexer.badLayout |= 0x4;
            }
            else if (parent.tag == tt.tagFont) {
                lexer.badLayout |= 0x8;
            }
            if (n != 2) {
                n = 1;
            }
            Node token;
            while ((token = lexer.getToken(n)) != null) {
                if (token.tag == parent.tag && token.type == 6) {
                    if (TidyUtils.toBoolean(parent.tag.model & 0x10)) {
                        lexer.popInline(token);
                    }
                    if (!TidyUtils.toBoolean(n & 0x2)) {
                        Node.trimSpaces(lexer, parent);
                    }
                    if (parent.tag == tt.tagFont && parent.content != null && parent.content == parent.last) {
                        final Node content = parent.content;
                        if (content.tag == tt.tagA) {
                            content.parent = parent.parent;
                            content.next = parent.next;
                            content.prev = parent.prev;
                            if (content.prev != null) {
                                content.prev.next = content;
                            }
                            else {
                                content.parent.content = content;
                            }
                            if (content.next != null) {
                                content.next.prev = content;
                            }
                            else {
                                content.parent.last = content;
                            }
                            parent.next = null;
                            parent.prev = null;
                            parent.parent = content;
                            parent.content = content.content;
                            parent.last = content.last;
                            content.content = parent;
                            content.last = parent;
                            for (Node node = parent.content; node != null; node = node.next) {
                                node.parent = parent;
                            }
                        }
                    }
                    parent.closed = true;
                    Node.trimSpaces(lexer, parent);
                    Node.trimEmptyElement(lexer, parent);
                    return;
                }
                if (token.type == 5 && token.tag == parent.tag && lexer.isPushed(token) && !token.implicit && !parent.implicit && token.tag != null && (token.tag.model & 0x10) != 0x0 && token.tag != tt.tagA && token.tag != tt.tagFont && token.tag != tt.tagBig && token.tag != tt.tagSmall && token.tag != tt.tagQ) {
                    if (parent.content != null && token.attributes == null) {
                        lexer.report.warning(lexer, parent, token, (short)24);
                        token.type = 6;
                        lexer.ungetToken();
                        continue;
                    }
                    lexer.report.warning(lexer, parent, token, (short)9);
                }
                else if (lexer.isPushed(token) && token.type == 5 && token.tag == tt.tagQ) {
                    lexer.report.warning(lexer, parent, token, (short)40);
                }
                if (token.type == 4) {
                    if (parent.content == null && !TidyUtils.toBoolean(n & 0x2)) {
                        Node.trimSpaces(lexer, parent);
                    }
                    if (token.start >= token.end) {
                        continue;
                    }
                    parent.insertNodeAtEnd(token);
                }
                else {
                    if (Node.insertMisc(parent, token)) {
                        continue;
                    }
                    if (token.tag == tt.tagHtml) {
                        if (token.type != 5 && token.type != 7) {
                            lexer.ungetToken();
                            if ((n & 0x2) == 0x0) {
                                Node.trimSpaces(lexer, parent);
                            }
                            Node.trimEmptyElement(lexer, parent);
                            return;
                        }
                        lexer.report.warning(lexer, parent, token, (short)8);
                    }
                    else if (token.tag == tt.tagP && token.type == 5 && ((n & 0x2) != 0x0 || parent.tag == tt.tagDt || parent.isDescendantOf(tt.tagDt))) {
                        token.tag = tt.tagBr;
                        token.element = "br";
                        Node.trimSpaces(lexer, parent);
                        parent.insertNodeAtEnd(token);
                    }
                    else if (token.tag == null || token.tag == tt.tagParam) {
                        lexer.report.warning(lexer, parent, token, (short)8);
                    }
                    else {
                        if (token.tag == tt.tagBr && token.type == 6) {
                            token.type = 5;
                        }
                        if (token.type == 6) {
                            if (token.tag == tt.tagBr) {
                                token.type = 5;
                            }
                            else if (token.tag == tt.tagP) {
                                if (!parent.isDescendantOf(tt.tagP)) {
                                    Node.coerceNode(lexer, token, tt.tagBr);
                                    Node.trimSpaces(lexer, parent);
                                    parent.insertNodeAtEnd(token);
                                    lexer.inferredTag("br");
                                    continue;
                                }
                            }
                            else if ((token.tag.model & 0x10) != 0x0 && token.tag != tt.tagA && (token.tag.model & 0x800) == 0x0 && (parent.tag.model & 0x10) != 0x0) {
                                lexer.popInline(parent);
                                if (parent.tag != tt.tagA) {
                                    if (token.tag == tt.tagA && token.tag != parent.tag) {
                                        lexer.report.warning(lexer, parent, token, (short)7);
                                        lexer.ungetToken();
                                    }
                                    else {
                                        lexer.report.warning(lexer, parent, token, (short)10);
                                    }
                                    if ((n & 0x2) == 0x0) {
                                        Node.trimSpaces(lexer, parent);
                                    }
                                    Node.trimEmptyElement(lexer, parent);
                                    return;
                                }
                                lexer.report.warning(lexer, parent, token, (short)8);
                                continue;
                            }
                            else if (lexer.exiled && token.tag.model != 0 && (token.tag.model & 0x80) != 0x0) {
                                lexer.ungetToken();
                                Node.trimSpaces(lexer, parent);
                                Node.trimEmptyElement(lexer, parent);
                                return;
                            }
                        }
                        if ((token.tag.model & 0x4000) != 0x0 && (parent.tag.model & 0x4000) != 0x0) {
                            if (token.tag == parent.tag) {
                                lexer.report.warning(lexer, parent, token, (short)10);
                            }
                            else {
                                lexer.report.warning(lexer, parent, token, (short)7);
                                lexer.ungetToken();
                            }
                            if ((n & 0x2) == 0x0) {
                                Node.trimSpaces(lexer, parent);
                            }
                            Node.trimEmptyElement(lexer, parent);
                            return;
                        }
                        if (token.tag == tt.tagA && !token.implicit && (parent.tag == tt.tagA || parent.isDescendantOf(tt.tagA))) {
                            if (token.type == 6 || token.attributes != null) {
                                lexer.ungetToken();
                                lexer.report.warning(lexer, parent, token, (short)7);
                                if ((n & 0x2) == 0x0) {
                                    Node.trimSpaces(lexer, parent);
                                }
                                Node.trimEmptyElement(lexer, parent);
                                return;
                            }
                            token.type = 6;
                            lexer.report.warning(lexer, parent, token, (short)24);
                            lexer.ungetToken();
                        }
                        else {
                            if ((parent.tag.model & 0x4000) != 0x0) {
                                if (token.tag == tt.tagCenter || token.tag == tt.tagDiv) {
                                    if (token.type != 5 && token.type != 7) {
                                        lexer.report.warning(lexer, parent, token, (short)8);
                                        continue;
                                    }
                                    lexer.report.warning(lexer, parent, token, (short)11);
                                    if (parent.content == null) {
                                        Node.insertNodeAsParent(parent, token);
                                        continue;
                                    }
                                    parent.insertNodeAfterElement(token);
                                    if ((n & 0x2) == 0x0) {
                                        Node.trimSpaces(lexer, parent);
                                    }
                                    parent = lexer.cloneNode(parent);
                                    parent.start = lexer.lexsize;
                                    parent.end = lexer.lexsize;
                                    token.insertNodeAtEnd(parent);
                                    continue;
                                }
                                else if (token.tag == tt.tagHr) {
                                    if (token.type != 5 && token.type != 7) {
                                        lexer.report.warning(lexer, parent, token, (short)8);
                                        continue;
                                    }
                                    lexer.report.warning(lexer, parent, token, (short)11);
                                    if (parent.content == null) {
                                        Node.insertNodeBeforeElement(parent, token);
                                        continue;
                                    }
                                    parent.insertNodeAfterElement(token);
                                    if ((n & 0x2) == 0x0) {
                                        Node.trimSpaces(lexer, parent);
                                    }
                                    parent = lexer.cloneNode(parent);
                                    parent.start = lexer.lexsize;
                                    parent.end = lexer.lexsize;
                                    token.insertNodeAfterElement(parent);
                                    continue;
                                }
                            }
                            if (parent.tag == tt.tagDt && token.tag == tt.tagHr) {
                                if (token.type != 5 && token.type != 7) {
                                    lexer.report.warning(lexer, parent, token, (short)8);
                                }
                                else {
                                    lexer.report.warning(lexer, parent, token, (short)11);
                                    final Node inferredTag = lexer.inferredTag("dd");
                                    if (parent.content == null) {
                                        Node.insertNodeBeforeElement(parent, inferredTag);
                                        inferredTag.insertNodeAtEnd(token);
                                    }
                                    else {
                                        parent.insertNodeAfterElement(inferredTag);
                                        inferredTag.insertNodeAtEnd(token);
                                        if ((n & 0x2) == 0x0) {
                                            Node.trimSpaces(lexer, parent);
                                        }
                                        parent = lexer.cloneNode(parent);
                                        parent.start = lexer.lexsize;
                                        parent.end = lexer.lexsize;
                                        inferredTag.insertNodeAfterElement(parent);
                                    }
                                }
                            }
                            else {
                                if (token.type == 6) {
                                    for (Node node2 = parent.parent; node2 != null; node2 = node2.parent) {
                                        if (token.tag == node2.tag) {
                                            if ((parent.tag.model & 0x8000) == 0x0 && !parent.implicit) {
                                                lexer.report.warning(lexer, parent, token, (short)7);
                                            }
                                            if (parent.tag == tt.tagA) {
                                                lexer.popInline(parent);
                                            }
                                            lexer.ungetToken();
                                            if ((n & 0x2) == 0x0) {
                                                Node.trimSpaces(lexer, parent);
                                            }
                                            Node.trimEmptyElement(lexer, parent);
                                            return;
                                        }
                                    }
                                }
                                if ((token.tag.model & 0x10) == 0x0) {
                                    if (token.type != 5) {
                                        lexer.report.warning(lexer, parent, token, (short)8);
                                    }
                                    else {
                                        if ((parent.tag.model & 0x8000) == 0x0) {
                                            lexer.report.warning(lexer, parent, token, (short)7);
                                        }
                                        if ((token.tag.model & 0x4) == 0x0 || (token.tag.model & 0x8) != 0x0) {
                                            if (parent.tag == tt.tagA) {
                                                if (token.tag != null && (token.tag.model & 0x4000) == 0x0) {
                                                    lexer.popInline(parent);
                                                }
                                                else if (parent.content == null) {
                                                    Node.discardElement(parent);
                                                    lexer.ungetToken();
                                                    return;
                                                }
                                            }
                                            lexer.ungetToken();
                                            if ((n & 0x2) == 0x0) {
                                                Node.trimSpaces(lexer, parent);
                                            }
                                            Node.trimEmptyElement(lexer, parent);
                                            return;
                                        }
                                        ParserImpl.moveToHead(lexer, parent, token);
                                    }
                                }
                                else if (token.type == 5 || token.type == 7) {
                                    if (token.implicit) {
                                        lexer.report.warning(lexer, parent, token, (short)15);
                                    }
                                    if (token.tag == tt.tagBr) {
                                        Node.trimSpaces(lexer, parent);
                                    }
                                    parent.insertNodeAtEnd(token);
                                    ParserImpl.parseTag(lexer, token, n);
                                }
                                else {
                                    lexer.report.warning(lexer, parent, token, (short)8);
                                }
                            }
                        }
                    }
                }
            }
            if ((parent.tag.model & 0x8000) == 0x0) {
                lexer.report.warning(lexer, parent, token, (short)6);
            }
            Node.trimEmptyElement(lexer, parent);
        }
    }
    
    public static class ParseList implements Parser
    {
        public void parse(final Lexer lexer, final Node node, final short n) {
            final TagTable tt = lexer.configuration.tt;
            if ((node.tag.model & 0x1) != 0x0) {
                return;
            }
            lexer.insert = -1;
            Node node2;
            while ((node2 = lexer.getToken((short)0)) != null) {
                if (node2.tag == node.tag && node2.type == 6) {
                    if ((node.tag.model & 0x80000) != 0x0) {
                        Node.coerceNode(lexer, node, tt.tagUl);
                    }
                    node.closed = true;
                    Node.trimEmptyElement(lexer, node);
                    return;
                }
                if (Node.insertMisc(node, node2)) {
                    continue;
                }
                if (node2.type != 4 && node2.tag == null) {
                    lexer.report.warning(lexer, node, node2, (short)8);
                }
                else if (node2.type == 6) {
                    if (node2.tag == tt.tagForm) {
                        ParserImpl.badForm(lexer);
                        lexer.report.warning(lexer, node, node2, (short)8);
                    }
                    else if (node2.tag != null && (node2.tag.model & 0x10) != 0x0) {
                        lexer.report.warning(lexer, node, node2, (short)8);
                        lexer.popInline(node2);
                    }
                    else {
                        for (Node node3 = node.parent; node3 != null; node3 = node3.parent) {
                            if (node2.tag == node3.tag) {
                                lexer.report.warning(lexer, node, node2, (short)7);
                                lexer.ungetToken();
                                if ((node.tag.model & 0x80000) != 0x0) {
                                    Node.coerceNode(lexer, node, tt.tagUl);
                                }
                                Node.trimEmptyElement(lexer, node);
                                return;
                            }
                        }
                        lexer.report.warning(lexer, node, node2, (short)8);
                    }
                }
                else {
                    if (node2.tag != tt.tagLi) {
                        lexer.ungetToken();
                        if (node2.tag != null && (node2.tag.model & 0x8) != 0x0 && lexer.excludeBlocks) {
                            lexer.report.warning(lexer, node, node2, (short)7);
                            Node.trimEmptyElement(lexer, node);
                            return;
                        }
                        node2 = lexer.inferredTag("li");
                        node2.addAttribute("style", "list-style: none");
                        lexer.report.warning(lexer, node, node2, (short)12);
                    }
                    node.insertNodeAtEnd(node2);
                    ParserImpl.parseTag(lexer, node2, (short)0);
                }
            }
            if ((node.tag.model & 0x80000) != 0x0) {
                Node.coerceNode(lexer, node, tt.tagUl);
            }
            lexer.report.warning(lexer, node, node2, (short)6);
            Node.trimEmptyElement(lexer, node);
        }
    }
    
    public static class ParseNoFrames implements Parser
    {
        public void parse(final Lexer lexer, final Node node, final short n) {
            final TagTable tt = lexer.configuration.tt;
            lexer.badAccess |= 0x20;
            Node node2;
            while ((node2 = lexer.getToken((short)0)) != null) {
                if (node2.tag == node.tag && node2.type == 6) {
                    node.closed = true;
                    Node.trimSpaces(lexer, node);
                    return;
                }
                if (node2.tag == tt.tagFrame || node2.tag == tt.tagFrameset) {
                    Node.trimSpaces(lexer, node);
                    if (node2.type == 6) {
                        lexer.report.warning(lexer, node, node2, (short)8);
                    }
                    else {
                        lexer.report.warning(lexer, node, node2, (short)7);
                        lexer.ungetToken();
                    }
                    return;
                }
                if (node2.tag == tt.tagHtml) {
                    if (node2.type != 5 && node2.type != 7) {
                        continue;
                    }
                    lexer.report.warning(lexer, node, node2, (short)8);
                }
                else {
                    if (Node.insertMisc(node, node2)) {
                        continue;
                    }
                    if (node2.tag == tt.tagBody && node2.type == 5) {
                        final boolean seenEndBody = lexer.seenEndBody;
                        node.insertNodeAtEnd(node2);
                        ParserImpl.parseTag(lexer, node2, (short)0);
                        if (!seenEndBody) {
                            continue;
                        }
                        Node.coerceNode(lexer, node2, tt.tagDiv);
                        ParserImpl.moveNodeToBody(lexer, node2);
                    }
                    else if (node2.type == 4 || (node2.tag != null && node2.type != 6)) {
                        if (lexer.seenEndBody) {
                            final Node body = lexer.root.findBody(tt);
                            if (node2.type == 4) {
                                lexer.ungetToken();
                                node2 = lexer.inferredTag("p");
                                lexer.report.warning(lexer, node, node2, (short)27);
                            }
                            body.insertNodeAtEnd(node2);
                        }
                        else {
                            lexer.ungetToken();
                            node2 = lexer.inferredTag("body");
                            if (lexer.configuration.xmlOut) {
                                lexer.report.warning(lexer, node, node2, (short)15);
                            }
                            node.insertNodeAtEnd(node2);
                        }
                        ParserImpl.parseTag(lexer, node2, (short)0);
                    }
                    else {
                        lexer.report.warning(lexer, node, node2, (short)8);
                    }
                }
            }
            lexer.report.warning(lexer, node, node2, (short)6);
        }
    }
    
    public static class ParseOptGroup implements Parser
    {
        public void parse(final Lexer lexer, final Node node, final short n) {
            final TagTable tt = lexer.configuration.tt;
            lexer.insert = -1;
            Node token;
            while ((token = lexer.getToken((short)0)) != null) {
                if (token.tag == node.tag && token.type == 6) {
                    node.closed = true;
                    Node.trimSpaces(lexer, node);
                    return;
                }
                if (Node.insertMisc(node, token)) {
                    continue;
                }
                if (token.type == 5 && (token.tag == tt.tagOption || token.tag == tt.tagOptgroup)) {
                    if (token.tag == tt.tagOptgroup) {
                        lexer.report.warning(lexer, node, token, (short)19);
                    }
                    node.insertNodeAtEnd(token);
                    ParserImpl.parseTag(lexer, token, (short)1);
                }
                else {
                    lexer.report.warning(lexer, node, token, (short)8);
                }
            }
        }
    }
    
    public static class ParsePre implements Parser
    {
        public void parse(final Lexer lexer, final Node node, final short n) {
            final TagTable tt = lexer.configuration.tt;
            if ((node.tag.model & 0x1) != 0x0) {
                return;
            }
            if ((node.tag.model & 0x80000) != 0x0) {
                Node.coerceNode(lexer, node, tt.tagPre);
            }
            lexer.inlineDup(null);
            Node token;
            while ((token = lexer.getToken((short)2)) != null) {
                if (token.tag == node.tag && token.type == 6) {
                    Node.trimSpaces(lexer, node);
                    node.closed = true;
                    Node.trimEmptyElement(lexer, node);
                    return;
                }
                if (token.tag == tt.tagHtml) {
                    if (token.type != 5 && token.type != 7) {
                        continue;
                    }
                    lexer.report.warning(lexer, node, token, (short)8);
                }
                else if (token.type == 4) {
                    if (node.content == null) {
                        if (token.textarray[token.start] == 10) {
                            final Node node2 = token;
                            ++node2.start;
                        }
                        if (token.start >= token.end) {
                            continue;
                        }
                    }
                    node.insertNodeAtEnd(token);
                }
                else {
                    if (Node.insertMisc(node, token)) {
                        continue;
                    }
                    if (!lexer.preContent(token)) {
                        lexer.report.warning(lexer, node, token, (short)39);
                        node.insertNodeAtEnd(Node.escapeTag(lexer, token));
                    }
                    else if (token.tag == tt.tagP) {
                        if (token.type == 5) {
                            lexer.report.warning(lexer, node, token, (short)14);
                            Node.trimSpaces(lexer, node);
                            Node.coerceNode(lexer, token, tt.tagBr);
                            node.insertNodeAtEnd(token);
                        }
                        else {
                            lexer.report.warning(lexer, node, token, (short)8);
                        }
                    }
                    else if (token.type == 5 || token.type == 7) {
                        if (token.tag == tt.tagBr) {
                            Node.trimSpaces(lexer, node);
                        }
                        node.insertNodeAtEnd(token);
                        ParserImpl.parseTag(lexer, token, (short)2);
                    }
                    else {
                        lexer.report.warning(lexer, node, token, (short)8);
                    }
                }
            }
            lexer.report.warning(lexer, node, token, (short)6);
            Node.trimEmptyElement(lexer, node);
        }
    }
    
    public static class ParseRow implements Parser
    {
        public void parse(final Lexer lexer, final Node node, final short n) {
            final TagTable tt = lexer.configuration.tt;
            if ((node.tag.model & 0x1) != 0x0) {
                return;
            }
            Node node2;
            while ((node2 = lexer.getToken((short)0)) != null) {
                if (node2.tag == node.tag) {
                    if (node2.type == 6) {
                        node.closed = true;
                        Node.fixEmptyRow(lexer, node);
                        return;
                    }
                    lexer.ungetToken();
                    Node.fixEmptyRow(lexer, node);
                    return;
                }
                else {
                    if (node2.type == 6) {
                        if (((node2.tag != null && (node2.tag.model & 0x82) != 0x0) || node2.tag == tt.tagTable) && node.isDescendantOf(node2.tag)) {
                            lexer.ungetToken();
                            return;
                        }
                        if (node2.tag == tt.tagForm || (node2.tag != null && (node2.tag.model & 0x18) != 0x0)) {
                            if (node2.tag == tt.tagForm) {
                                ParserImpl.badForm(lexer);
                            }
                            lexer.report.warning(lexer, node, node2, (short)8);
                            continue;
                        }
                        if (node2.tag == tt.tagTd || node2.tag == tt.tagTh) {
                            lexer.report.warning(lexer, node, node2, (short)8);
                            continue;
                        }
                        for (Node node3 = node.parent; node3 != null; node3 = node3.parent) {
                            if (node2.tag == node3.tag) {
                                lexer.ungetToken();
                                Node.trimEmptyElement(lexer, node);
                                return;
                            }
                        }
                    }
                    if (Node.insertMisc(node, node2)) {
                        continue;
                    }
                    if (node2.tag == null && node2.type != 4) {
                        lexer.report.warning(lexer, node, node2, (short)8);
                    }
                    else if (node2.tag == tt.tagTable) {
                        lexer.report.warning(lexer, node, node2, (short)8);
                    }
                    else {
                        if (node2.tag != null && (node2.tag.model & 0x100) != 0x0) {
                            lexer.ungetToken();
                            Node.trimEmptyElement(lexer, node);
                            return;
                        }
                        if (node2.type == 6) {
                            lexer.report.warning(lexer, node, node2, (short)8);
                        }
                        else {
                            if (node2.type != 6) {
                                if (node2.tag == tt.tagForm) {
                                    lexer.ungetToken();
                                    node2 = lexer.inferredTag("td");
                                    lexer.report.warning(lexer, node, node2, (short)12);
                                }
                                else {
                                    if (node2.type == 4 || (node2.tag.model & 0x18) != 0x0) {
                                        Node.moveBeforeTable(node, node2, tt);
                                        lexer.report.warning(lexer, node, node2, (short)11);
                                        lexer.exiled = true;
                                        if (node2.type != 4) {
                                            ParserImpl.parseTag(lexer, node2, (short)0);
                                        }
                                        lexer.exiled = false;
                                        continue;
                                    }
                                    if ((node2.tag.model & 0x4) != 0x0) {
                                        lexer.report.warning(lexer, node, node2, (short)11);
                                        ParserImpl.moveToHead(lexer, node, node2);
                                        continue;
                                    }
                                }
                            }
                            if (node2.tag != tt.tagTd && node2.tag != tt.tagTh) {
                                lexer.report.warning(lexer, node, node2, (short)11);
                            }
                            else {
                                node.insertNodeAtEnd(node2);
                                final boolean excludeBlocks = lexer.excludeBlocks;
                                lexer.excludeBlocks = false;
                                ParserImpl.parseTag(lexer, node2, (short)0);
                                lexer.excludeBlocks = excludeBlocks;
                                while (lexer.istack.size() > lexer.istackbase) {
                                    lexer.popInline(null);
                                }
                            }
                        }
                    }
                }
            }
            Node.trimEmptyElement(lexer, node);
        }
    }
    
    public static class ParseRowGroup implements Parser
    {
        public void parse(final Lexer lexer, final Node node, final short n) {
            final TagTable tt = lexer.configuration.tt;
            if ((node.tag.model & 0x1) != 0x0) {
                return;
            }
            Node node2;
            while ((node2 = lexer.getToken((short)0)) != null) {
                if (node2.tag == node.tag) {
                    if (node2.type == 6) {
                        node.closed = true;
                        Node.trimEmptyElement(lexer, node);
                        return;
                    }
                    lexer.ungetToken();
                    return;
                }
                else {
                    if (node2.tag == tt.tagTable && node2.type == 6) {
                        lexer.ungetToken();
                        Node.trimEmptyElement(lexer, node);
                        return;
                    }
                    if (Node.insertMisc(node, node2)) {
                        continue;
                    }
                    if (node2.tag == null && node2.type != 4) {
                        lexer.report.warning(lexer, node, node2, (short)8);
                    }
                    else {
                        if (node2.type != 6) {
                            if (node2.tag == tt.tagTd || node2.tag == tt.tagTh) {
                                lexer.ungetToken();
                                node2 = lexer.inferredTag("tr");
                                lexer.report.warning(lexer, node, node2, (short)12);
                            }
                            else {
                                if (node2.type == 4 || (node2.tag.model & 0x18) != 0x0) {
                                    Node.moveBeforeTable(node, node2, tt);
                                    lexer.report.warning(lexer, node, node2, (short)11);
                                    lexer.exiled = true;
                                    if (node2.type != 4) {
                                        ParserImpl.parseTag(lexer, node2, (short)0);
                                    }
                                    lexer.exiled = false;
                                    continue;
                                }
                                if ((node2.tag.model & 0x4) != 0x0) {
                                    lexer.report.warning(lexer, node, node2, (short)11);
                                    ParserImpl.moveToHead(lexer, node, node2);
                                    continue;
                                }
                            }
                        }
                        if (node2.type == 6) {
                            if (node2.tag == tt.tagForm || (node2.tag != null && (node2.tag.model & 0x18) != 0x0)) {
                                if (node2.tag == tt.tagForm) {
                                    ParserImpl.badForm(lexer);
                                }
                                lexer.report.warning(lexer, node, node2, (short)8);
                                continue;
                            }
                            if (node2.tag == tt.tagTr || node2.tag == tt.tagTd || node2.tag == tt.tagTh) {
                                lexer.report.warning(lexer, node, node2, (short)8);
                                continue;
                            }
                            for (Node node3 = node.parent; node3 != null; node3 = node3.parent) {
                                if (node2.tag == node3.tag) {
                                    lexer.ungetToken();
                                    Node.trimEmptyElement(lexer, node);
                                    return;
                                }
                            }
                        }
                        if ((node2.tag.model & 0x100) != 0x0) {
                            if (node2.type != 6) {
                                lexer.ungetToken();
                            }
                            Node.trimEmptyElement(lexer, node);
                            return;
                        }
                        if (node2.type == 6) {
                            lexer.report.warning(lexer, node, node2, (short)8);
                        }
                        else {
                            if (node2.tag != tt.tagTr) {
                                node2 = lexer.inferredTag("tr");
                                lexer.report.warning(lexer, node, node2, (short)12);
                                lexer.ungetToken();
                            }
                            node.insertNodeAtEnd(node2);
                            ParserImpl.parseTag(lexer, node2, (short)0);
                        }
                    }
                }
            }
            Node.trimEmptyElement(lexer, node);
        }
    }
    
    public static class ParseScript implements Parser
    {
        public void parse(final Lexer lexer, final Node node, final short n) {
            final Node cdata = lexer.getCDATA(node);
            if (cdata != null) {
                node.insertNodeAtEnd(cdata);
                final Node token = lexer.getToken((short)0);
                if (token == null || token.type != 6 || token.tag == null || !token.tag.name.equalsIgnoreCase(node.tag.name)) {
                    lexer.report.error(lexer, node, token, (short)6);
                    if (token != null) {
                        lexer.ungetToken();
                    }
                }
                return;
            }
            lexer.report.error(lexer, node, null, (short)6);
        }
    }
    
    public static class ParseSelect implements Parser
    {
        public void parse(final Lexer lexer, final Node node, final short n) {
            final TagTable tt = lexer.configuration.tt;
            lexer.insert = -1;
            Node token;
            while ((token = lexer.getToken((short)0)) != null) {
                if (token.tag == node.tag && token.type == 6) {
                    node.closed = true;
                    Node.trimSpaces(lexer, node);
                    return;
                }
                if (Node.insertMisc(node, token)) {
                    continue;
                }
                if (token.type == 5 && (token.tag == tt.tagOption || token.tag == tt.tagOptgroup || token.tag == tt.tagScript)) {
                    node.insertNodeAtEnd(token);
                    ParserImpl.parseTag(lexer, token, (short)0);
                }
                else {
                    lexer.report.warning(lexer, node, token, (short)8);
                }
            }
            lexer.report.warning(lexer, node, token, (short)6);
        }
    }
    
    public static class ParseTableTag implements Parser
    {
        public void parse(final Lexer lexer, final Node node, final short n) {
            final TagTable tt = lexer.configuration.tt;
            lexer.deferDup();
            final int istackbase = lexer.istackbase;
            lexer.istackbase = lexer.istack.size();
            Node node2;
            while ((node2 = lexer.getToken((short)0)) != null) {
                if (node2.tag == node.tag && node2.type == 6) {
                    lexer.istackbase = istackbase;
                    node.closed = true;
                    Node.trimEmptyElement(lexer, node);
                    return;
                }
                if (Node.insertMisc(node, node2)) {
                    continue;
                }
                if (node2.tag == null && node2.type != 4) {
                    lexer.report.warning(lexer, node, node2, (short)8);
                }
                else {
                    if (node2.type != 6) {
                        if (node2.tag == tt.tagTd || node2.tag == tt.tagTh || node2.tag == tt.tagTable) {
                            lexer.ungetToken();
                            node2 = lexer.inferredTag("tr");
                            lexer.report.warning(lexer, node, node2, (short)12);
                        }
                        else {
                            if (node2.type == 4 || (node2.tag.model & 0x18) != 0x0) {
                                Node.insertNodeBeforeElement(node, node2);
                                lexer.report.warning(lexer, node, node2, (short)11);
                                lexer.exiled = true;
                                if (node2.type != 4) {
                                    ParserImpl.parseTag(lexer, node2, (short)0);
                                }
                                lexer.exiled = false;
                                continue;
                            }
                            if ((node2.tag.model & 0x4) != 0x0) {
                                ParserImpl.moveToHead(lexer, node, node2);
                                continue;
                            }
                        }
                    }
                    if (node2.type == 6) {
                        if (node2.tag == tt.tagForm || (node2.tag != null && (node2.tag.model & 0x18) != 0x0)) {
                            ParserImpl.badForm(lexer);
                            lexer.report.warning(lexer, node, node2, (short)8);
                            continue;
                        }
                        if ((node2.tag != null && (node2.tag.model & 0x280) != 0x0) || (node2.tag != null && (node2.tag.model & 0x18) != 0x0)) {
                            lexer.report.warning(lexer, node, node2, (short)8);
                            continue;
                        }
                        for (Node node3 = node.parent; node3 != null; node3 = node3.parent) {
                            if (node2.tag == node3.tag) {
                                lexer.report.warning(lexer, node, node2, (short)7);
                                lexer.ungetToken();
                                lexer.istackbase = istackbase;
                                Node.trimEmptyElement(lexer, node);
                                return;
                            }
                        }
                    }
                    if ((node2.tag.model & 0x80) == 0x0) {
                        lexer.ungetToken();
                        lexer.report.warning(lexer, node, node2, (short)11);
                        lexer.istackbase = istackbase;
                        Node.trimEmptyElement(lexer, node);
                        return;
                    }
                    if (node2.type == 5 || node2.type == 7) {
                        node.insertNodeAtEnd(node2);
                        ParserImpl.parseTag(lexer, node2, (short)0);
                    }
                    else {
                        lexer.report.warning(lexer, node, node2, (short)8);
                    }
                }
            }
            lexer.report.warning(lexer, node, node2, (short)6);
            Node.trimEmptyElement(lexer, node);
            lexer.istackbase = istackbase;
        }
    }
    
    public static class ParseText implements Parser
    {
        public void parse(final Lexer lexer, final Node node, final short n) {
            final TagTable tt = lexer.configuration.tt;
            lexer.insert = -1;
            short n2;
            if (node.tag == tt.tagTextarea) {
                n2 = 2;
            }
            else {
                n2 = 1;
            }
            Node token;
            while ((token = lexer.getToken(n2)) != null) {
                if (token.tag == node.tag && token.type == 6) {
                    node.closed = true;
                    Node.trimSpaces(lexer, node);
                    return;
                }
                if (Node.insertMisc(node, token)) {
                    continue;
                }
                if (token.type == 4) {
                    if (node.content == null && (n2 & 0x2) == 0x0) {
                        Node.trimSpaces(lexer, node);
                    }
                    if (token.start >= token.end) {
                        continue;
                    }
                    node.insertNodeAtEnd(token);
                }
                else {
                    if (token.tag == null || (token.tag.model & 0x10) == 0x0 || (token.tag.model & 0x400) != 0x0) {
                        if ((node.tag.model & 0x8000) == 0x0) {
                            lexer.report.warning(lexer, node, token, (short)7);
                        }
                        lexer.ungetToken();
                        Node.trimSpaces(lexer, node);
                        return;
                    }
                    lexer.report.warning(lexer, node, token, (short)8);
                }
            }
            if ((node.tag.model & 0x8000) == 0x0) {
                lexer.report.warning(lexer, node, token, (short)6);
            }
        }
    }
    
    public static class ParseTitle implements Parser
    {
        public void parse(final Lexer lexer, final Node node, final short n) {
            Node token;
            while ((token = lexer.getToken((short)1)) != null) {
                if (token.tag == node.tag && token.type == 5) {
                    lexer.report.warning(lexer, node, token, (short)24);
                    token.type = 6;
                }
                else {
                    if (token.tag == node.tag && token.type == 6) {
                        node.closed = true;
                        Node.trimSpaces(lexer, node);
                        return;
                    }
                    if (token.type == 4) {
                        if (node.content == null) {
                            Node.trimInitialSpace(lexer, node, token);
                        }
                        if (token.start >= token.end) {
                            continue;
                        }
                        node.insertNodeAtEnd(token);
                    }
                    else {
                        if (Node.insertMisc(node, token)) {
                            continue;
                        }
                        if (token.tag != null) {
                            lexer.report.warning(lexer, node, token, (short)7);
                            lexer.ungetToken();
                            Node.trimSpaces(lexer, node);
                            return;
                        }
                        lexer.report.warning(lexer, node, token, (short)8);
                    }
                }
            }
            lexer.report.warning(lexer, node, token, (short)6);
        }
    }
}
