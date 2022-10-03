package org.w3c.tidy;

public class Clean
{
    private int classNum;
    private TagTable tt;
    
    public Clean(final TagTable tt) {
        this.classNum = 1;
        this.tt = tt;
    }
    
    private StyleProp insertProperty(StyleProp next, final String s, final String s2) {
        StyleProp styleProp = null;
        StyleProp styleProp2 = next;
        while (next != null) {
            final int compareTo = next.name.compareTo(s);
            if (compareTo == 0) {
                return styleProp2;
            }
            if (compareTo > 0) {
                final StyleProp next2 = new StyleProp(s, s2, next);
                if (styleProp != null) {
                    styleProp.next = next2;
                }
                else {
                    styleProp2 = next2;
                }
                return styleProp2;
            }
            styleProp = next;
            next = next.next;
        }
        final StyleProp next3 = new StyleProp(s, s2, null);
        if (styleProp != null) {
            styleProp.next = next3;
        }
        else {
            styleProp2 = next3;
        }
        return styleProp2;
    }
    
    private StyleProp createProps(StyleProp insertProperty, final String s) {
        int n = 0;
        int k;
        for (int i = 0; i < s.length(); i = k + 1) {
            while (i < s.length() && s.charAt(i) == ' ') {
                ++i;
            }
            int j;
            for (j = i; j < s.length(); ++j) {
                if (s.charAt(j) == ':') {
                    n = j + 1;
                    break;
                }
            }
            if (j >= s.length()) {
                break;
            }
            if (s.charAt(j) != ':') {
                break;
            }
            while (n < s.length() && s.charAt(n) == ' ') {
                ++n;
            }
            k = n;
            boolean b = false;
            while (k < s.length()) {
                if (s.charAt(k) == ';') {
                    b = true;
                    break;
                }
                ++k;
            }
            insertProperty = this.insertProperty(insertProperty, s.substring(i, j), s.substring(n, k));
            if (!b) {
                break;
            }
        }
        return insertProperty;
    }
    
    private String createPropString(final StyleProp styleProp) {
        String s = "";
        int n = 0;
        for (StyleProp next = styleProp; next != null; next = next.next) {
            n = n + (next.name.length() + 2) + (next.value.length() + 2);
        }
        for (StyleProp next2 = styleProp; next2 != null; next2 = next2.next) {
            s = s.concat(next2.name).concat(": ").concat(next2.value);
            if (next2.next == null) {
                break;
            }
            s = s.concat("; ");
        }
        return s;
    }
    
    private String addProperty(String propString, final String s) {
        propString = this.createPropString(this.createProps(this.createProps(null, propString), s));
        return propString;
    }
    
    private String gensymClass(final Lexer lexer, final String s) {
        final String s2 = (lexer.configuration.cssPrefix == null) ? (lexer.configuration.cssPrefix + this.classNum) : ("c" + this.classNum);
        ++this.classNum;
        return s2;
    }
    
    private String findStyle(final Lexer lexer, final String s, final String s2) {
        for (Style style = lexer.styles; style != null; style = style.next) {
            if (style.tag.equals(s) && style.properties.equals(s2)) {
                return style.tagClass;
            }
        }
        final Style styles = new Style(s, this.gensymClass(lexer, s), s2, lexer.styles);
        lexer.styles = styles;
        return styles.tagClass;
    }
    
    private void style2Rule(final Lexer lexer, final Node node) {
        final AttVal attrByName = node.getAttrByName("style");
        if (attrByName != null) {
            final String style = this.findStyle(lexer, node.element, attrByName.value);
            final AttVal attrByName2 = node.getAttrByName("class");
            if (attrByName2 != null) {
                attrByName2.value = attrByName2.value + " " + style;
                node.removeAttribute(attrByName);
            }
            else {
                attrByName.attribute = "class";
                attrByName.value = style;
            }
        }
    }
    
    private void addColorRule(final Lexer lexer, final String s, final String s2) {
        if (s2 != null) {
            lexer.addStringLiteral(s);
            lexer.addStringLiteral(" { color: ");
            lexer.addStringLiteral(s2);
            lexer.addStringLiteral(" }\n");
        }
    }
    
    private void cleanBodyAttrs(final Lexer lexer, final Node node) {
        String value = null;
        String value2 = null;
        String value3 = null;
        final AttVal attrByName = node.getAttrByName("background");
        if (attrByName != null) {
            value = attrByName.value;
            attrByName.value = null;
            node.removeAttribute(attrByName);
        }
        final AttVal attrByName2 = node.getAttrByName("bgcolor");
        if (attrByName2 != null) {
            value2 = attrByName2.value;
            attrByName2.value = null;
            node.removeAttribute(attrByName2);
        }
        final AttVal attrByName3 = node.getAttrByName("text");
        if (attrByName3 != null) {
            value3 = attrByName3.value;
            attrByName3.value = null;
            node.removeAttribute(attrByName3);
        }
        if (value != null || value2 != null || value3 != null) {
            lexer.addStringLiteral(" body {\n");
            if (value != null) {
                lexer.addStringLiteral("  background-image: url(");
                lexer.addStringLiteral(value);
                lexer.addStringLiteral(");\n");
            }
            if (value2 != null) {
                lexer.addStringLiteral("  background-color: ");
                lexer.addStringLiteral(value2);
                lexer.addStringLiteral(";\n");
            }
            if (value3 != null) {
                lexer.addStringLiteral("  color: ");
                lexer.addStringLiteral(value3);
                lexer.addStringLiteral(";\n");
            }
            lexer.addStringLiteral(" }\n");
        }
        final AttVal attrByName4 = node.getAttrByName("link");
        if (attrByName4 != null) {
            this.addColorRule(lexer, " :link", attrByName4.value);
            node.removeAttribute(attrByName4);
        }
        final AttVal attrByName5 = node.getAttrByName("vlink");
        if (attrByName5 != null) {
            this.addColorRule(lexer, " :visited", attrByName5.value);
            node.removeAttribute(attrByName5);
        }
        final AttVal attrByName6 = node.getAttrByName("alink");
        if (attrByName6 != null) {
            this.addColorRule(lexer, " :active", attrByName6.value);
            node.removeAttribute(attrByName6);
        }
    }
    
    private boolean niceBody(final Lexer lexer, final Node node) {
        final Node body = node.findBody(lexer.configuration.tt);
        if (body != null && (body.getAttrByName("background") != null || body.getAttrByName("bgcolor") != null || body.getAttrByName("text") != null || body.getAttrByName("link") != null || body.getAttrByName("vlink") != null || body.getAttrByName("alink") != null)) {
            lexer.badLayout |= 0x10;
            return false;
        }
        return true;
    }
    
    private void createStyleElement(final Lexer lexer, final Node node) {
        if (lexer.styles == null && this.niceBody(lexer, node)) {
            return;
        }
        final Node node2 = lexer.newNode((short)5, null, 0, 0, "style");
        node2.implicit = true;
        final AttVal attributes = new AttVal(null, null, 34, "type", "text/css");
        attributes.dict = AttributeTable.getDefaultAttributeTable().findAttribute(attributes);
        node2.attributes = attributes;
        final Node body = node.findBody(lexer.configuration.tt);
        lexer.txtstart = lexer.lexsize;
        if (body != null) {
            this.cleanBodyAttrs(lexer, body);
        }
        for (Style style = lexer.styles; style != null; style = style.next) {
            lexer.addCharToLexer(32);
            lexer.addStringLiteral(style.tag);
            lexer.addCharToLexer(46);
            lexer.addStringLiteral(style.tagClass);
            lexer.addCharToLexer(32);
            lexer.addCharToLexer(123);
            lexer.addStringLiteral(style.properties);
            lexer.addCharToLexer(125);
            lexer.addCharToLexer(10);
        }
        lexer.txtend = lexer.lexsize;
        node2.insertNodeAtEnd(lexer.newNode((short)4, lexer.lexbuf, lexer.txtstart, lexer.txtend));
        final Node head = node.findHEAD(lexer.configuration.tt);
        if (head != null) {
            head.insertNodeAtEnd(node2);
        }
    }
    
    private void fixNodeLinks(final Node parent) {
        if (parent.prev != null) {
            parent.prev.next = parent;
        }
        else {
            parent.parent.content = parent;
        }
        if (parent.next != null) {
            parent.next.prev = parent;
        }
        else {
            parent.parent.last = parent;
        }
        for (Node node = parent.content; node != null; node = node.next) {
            node.parent = parent;
        }
    }
    
    private void stripOnlyChild(final Node parent) {
        final Node content = parent.content;
        parent.content = content.content;
        parent.last = content.last;
        content.content = null;
        for (Node node = parent.content; node != null; node = node.next) {
            node.parent = parent;
        }
    }
    
    private void discardContainer(final Node node, final Node[] array) {
        final Node parent = node.parent;
        if (node.content != null) {
            node.last.next = node.next;
            if (node.next != null) {
                node.next.prev = node.last;
                node.last.next = node.next;
            }
            else {
                parent.last = node.last;
            }
            if (node.prev != null) {
                node.content.prev = node.prev;
                node.prev.next = node.content;
            }
            else {
                parent.content = node.content;
            }
            for (Node node2 = node.content; node2 != null; node2 = node2.next) {
                node2.parent = parent;
            }
            array[0] = node.content;
        }
        else {
            if (node.next != null) {
                node.next.prev = node.prev;
            }
            else {
                parent.last = node.prev;
            }
            if (node.prev != null) {
                node.prev.next = node.next;
            }
            else {
                parent.content = node.next;
            }
            array[0] = node.next;
        }
        node.next = null;
        node.content = null;
    }
    
    private void addStyleProperty(final Node node, final String s) {
        AttVal attVal;
        for (attVal = node.attributes; attVal != null && !attVal.attribute.equals("style"); attVal = attVal.next) {}
        if (attVal != null) {
            attVal.value = this.addProperty(attVal.value, s);
        }
        else {
            final AttVal attributes = new AttVal(node.attributes, null, 34, "style", s);
            attributes.dict = AttributeTable.getDefaultAttributeTable().findAttribute(attributes);
            node.attributes = attributes;
        }
    }
    
    private String mergeProperties(final String s, final String s2) {
        return this.createPropString(this.createProps(this.createProps(null, s), s2));
    }
    
    private void mergeClasses(final Node node, final Node node2) {
        String value = null;
        for (AttVal attVal = node2.attributes; attVal != null; attVal = attVal.next) {
            if ("class".equals(attVal.attribute)) {
                value = attVal.value;
                break;
            }
        }
        String value2 = null;
        AttVal attVal2;
        for (attVal2 = node.attributes; attVal2 != null; attVal2 = attVal2.next) {
            if ("class".equals(attVal2.attribute)) {
                value2 = attVal2.value;
                break;
            }
        }
        if (value2 != null) {
            if (value != null) {
                attVal2.value = value2 + ' ' + value;
            }
        }
        else if (value != null) {
            final AttVal attributes = new AttVal(node.attributes, null, 34, "class", value);
            attributes.dict = AttributeTable.getDefaultAttributeTable().findAttribute(attributes);
            node.attributes = attributes;
        }
    }
    
    private void mergeStyles(final Node node, final Node node2) {
        this.mergeClasses(node, node2);
        String value = null;
        for (AttVal attVal = node2.attributes; attVal != null; attVal = attVal.next) {
            if (attVal.attribute.equals("style")) {
                value = attVal.value;
                break;
            }
        }
        String value2 = null;
        AttVal attVal2;
        for (attVal2 = node.attributes; attVal2 != null; attVal2 = attVal2.next) {
            if (attVal2.attribute.equals("style")) {
                value2 = attVal2.value;
                break;
            }
        }
        if (value2 != null) {
            if (value != null) {
                attVal2.value = this.mergeProperties(value2, value);
            }
        }
        else if (value != null) {
            final AttVal attributes = new AttVal(node.attributes, null, 34, "style", value);
            attributes.dict = AttributeTable.getDefaultAttributeTable().findAttribute(attributes);
            node.attributes = attributes;
        }
    }
    
    private String fontSize2Name(final String s) {
        final String[] array = { "60%", "70%", "80%", null, "120%", "150%", "200%" };
        if (s.length() > 0 && '0' <= s.charAt(0) && s.charAt(0) <= '6') {
            return array[s.charAt(0) - '0'];
        }
        if (s.length() > 0 && s.charAt(0) == '-') {
            if (s.length() > 1 && '0' <= s.charAt(1) && s.charAt(1) <= '6') {
                int i = s.charAt(1) - '0';
                double n = 1.0;
                while (i > 0) {
                    n *= 0.8;
                    --i;
                }
                return "" + (int)(n * 100.0) + "%";
            }
            return "smaller";
        }
        else {
            if (s.length() > 1 && '0' <= s.charAt(1) && s.charAt(1) <= '6') {
                int j = s.charAt(1) - '0';
                double n2 = 1.0;
                while (j > 0) {
                    n2 *= 1.2;
                    --j;
                }
                return "" + (int)(n2 * 100.0) + "%";
            }
            return "larger";
        }
    }
    
    private void addFontFace(final Node node, final String s) {
        this.addStyleProperty(node, "font-family: " + s);
    }
    
    private void addFontSize(final Node node, final String s) {
        if (s == null) {
            return;
        }
        if ("6".equals(s) && node.tag == this.tt.tagP) {
            node.element = "h1";
            this.tt.findTag(node);
            return;
        }
        if ("5".equals(s) && node.tag == this.tt.tagP) {
            node.element = "h2";
            this.tt.findTag(node);
            return;
        }
        if ("4".equals(s) && node.tag == this.tt.tagP) {
            node.element = "h3";
            this.tt.findTag(node);
            return;
        }
        final String fontSize2Name = this.fontSize2Name(s);
        if (fontSize2Name != null) {
            this.addStyleProperty(node, "font-size: " + fontSize2Name);
        }
    }
    
    private void addFontColor(final Node node, final String s) {
        this.addStyleProperty(node, "color: " + s);
    }
    
    private void addAlign(final Node node, final String s) {
        this.addStyleProperty(node, "text-align: " + s.toLowerCase());
    }
    
    private void addFontStyles(final Node node, AttVal next) {
        while (next != null) {
            if (next.attribute.equals("face")) {
                this.addFontFace(node, next.value);
            }
            else if (next.attribute.equals("size")) {
                this.addFontSize(node, next.value);
            }
            else if (next.attribute.equals("color")) {
                this.addFontColor(node, next.value);
            }
            next = next.next;
        }
    }
    
    private void textAlign(final Lexer lexer, final Node node) {
        AttVal attVal = null;
        AttVal attVal2 = node.attributes;
        while (attVal2 != null) {
            if (attVal2.attribute.equals("align")) {
                if (attVal != null) {
                    attVal.next = attVal2.next;
                }
                else {
                    node.attributes = attVal2.next;
                }
                if (attVal2.value != null) {
                    this.addAlign(node, attVal2.value);
                    break;
                }
                break;
            }
            else {
                attVal = attVal2;
                attVal2 = attVal2.next;
            }
        }
    }
    
    private boolean dir2Div(final Lexer lexer, final Node node) {
        if (node.tag != this.tt.tagDir && node.tag != this.tt.tagUl && node.tag != this.tt.tagOl) {
            return false;
        }
        final Node content = node.content;
        if (content == null) {
            return false;
        }
        if (content.next != null) {
            return false;
        }
        if (content.tag != this.tt.tagLi) {
            return false;
        }
        if (!content.implicit) {
            return false;
        }
        node.tag = this.tt.tagDiv;
        node.element = "div";
        this.addStyleProperty(node, "margin-left: 2em");
        this.stripOnlyChild(node);
        return true;
    }
    
    private boolean center2Div(final Lexer lexer, Node content, final Node[] array) {
        if (content.tag != this.tt.tagCenter) {
            return false;
        }
        if (lexer.configuration.dropFontTags) {
            if (content.content != null) {
                final Node last = content.last;
                final Node parent = content.parent;
                this.discardContainer(content, array);
                content = lexer.inferredTag("br");
                if (last.next != null) {
                    last.next.prev = content;
                }
                content.next = last.next;
                last.next = content;
                if (parent.last == (content.prev = last)) {
                    parent.last = content;
                }
                content.parent = parent;
            }
            else {
                final Node prev = content.prev;
                final Node next = content.next;
                final Node parent2 = content.parent;
                this.discardContainer(content, array);
                content = lexer.inferredTag("br");
                content.next = next;
                content.prev = prev;
                content.parent = parent2;
                if (next != null) {
                    next.prev = content;
                }
                else {
                    parent2.last = content;
                }
                if (prev != null) {
                    prev.next = content;
                }
                else {
                    parent2.content = content;
                }
            }
            return true;
        }
        content.tag = this.tt.tagDiv;
        content.element = "div";
        this.addStyleProperty(content, "text-align: center");
        return true;
    }
    
    private boolean mergeDivs(final Lexer lexer, final Node node) {
        if (node.tag != this.tt.tagDiv) {
            return false;
        }
        final Node content = node.content;
        if (content == null) {
            return false;
        }
        if (content.tag != this.tt.tagDiv) {
            return false;
        }
        if (content.next != null) {
            return false;
        }
        this.mergeStyles(node, content);
        this.stripOnlyChild(node);
        return true;
    }
    
    private boolean nestedList(final Lexer lexer, Node node, final Node[] array) {
        if (node.tag != this.tt.tagUl && node.tag != this.tt.tagOl) {
            return false;
        }
        final Node content = node.content;
        if (content == null) {
            return false;
        }
        if (content.next != null) {
            return false;
        }
        final Node content2 = content.content;
        if (content2 == null) {
            return false;
        }
        if (content2.tag != node.tag) {
            return false;
        }
        array[0] = content2;
        content2.prev = node.prev;
        content2.next = node.next;
        content2.parent = node.parent;
        this.fixNodeLinks(content2);
        content.content = null;
        node.content = null;
        node.next = null;
        if (content2.prev != null && (content2.prev.tag == this.tt.tagUl || content2.prev.tag == this.tt.tagOl)) {
            node = content2;
            final Node prev = node.prev;
            prev.next = node.next;
            if (prev.next != null) {
                prev.next.prev = prev;
            }
            final Node last = prev.last;
            node.parent = last;
            node.next = null;
            node.prev = last.last;
            this.fixNodeLinks(node);
            this.cleanNode(lexer, node);
        }
        return true;
    }
    
    private boolean blockStyle(final Lexer lexer, final Node node) {
        if ((node.tag.model & 0xE8) != 0x0 && node.tag != this.tt.tagTable && node.tag != this.tt.tagTr && node.tag != this.tt.tagLi) {
            if (node.tag != this.tt.tagCaption) {
                this.textAlign(lexer, node);
            }
            final Node content = node.content;
            if (content == null) {
                return false;
            }
            if (content.next != null) {
                return false;
            }
            if (content.tag == this.tt.tagB) {
                this.mergeStyles(node, content);
                this.addStyleProperty(node, "font-weight: bold");
                this.stripOnlyChild(node);
                return true;
            }
            if (content.tag == this.tt.tagI) {
                this.mergeStyles(node, content);
                this.addStyleProperty(node, "font-style: italic");
                this.stripOnlyChild(node);
                return true;
            }
            if (content.tag == this.tt.tagFont) {
                this.mergeStyles(node, content);
                this.addFontStyles(node, content.attributes);
                this.stripOnlyChild(node);
                return true;
            }
        }
        return false;
    }
    
    private boolean inlineStyle(final Lexer lexer, final Node node, final Node[] array) {
        if (node.tag != this.tt.tagFont && (node.tag.model & 0x210) != 0x0) {
            final Node content = node.content;
            if (content == null) {
                return false;
            }
            if (content.next != null) {
                return false;
            }
            if (content.tag == this.tt.tagB && lexer.configuration.logicalEmphasis) {
                this.mergeStyles(node, content);
                this.addStyleProperty(node, "font-weight: bold");
                this.stripOnlyChild(node);
                return true;
            }
            if (content.tag == this.tt.tagI && lexer.configuration.logicalEmphasis) {
                this.mergeStyles(node, content);
                this.addStyleProperty(node, "font-style: italic");
                this.stripOnlyChild(node);
                return true;
            }
            if (content.tag == this.tt.tagFont) {
                this.mergeStyles(node, content);
                this.addFontStyles(node, content.attributes);
                this.stripOnlyChild(node);
                return true;
            }
        }
        return false;
    }
    
    private boolean font2Span(final Lexer lexer, final Node node, final Node[] array) {
        if (node.tag != this.tt.tagFont) {
            return false;
        }
        if (lexer.configuration.dropFontTags) {
            this.discardContainer(node, array);
            return false;
        }
        if (node.parent.content == node && node.next == null) {
            return false;
        }
        this.addFontStyles(node, node.attributes);
        AttVal attributes = node.attributes;
        AttVal attributes2 = null;
        while (attributes != null) {
            final AttVal next = attributes.next;
            if (attributes.attribute.equals("style")) {
                attributes.next = null;
                attributes2 = attributes;
            }
            attributes = next;
        }
        node.attributes = attributes2;
        node.tag = this.tt.tagSpan;
        node.element = "span";
        return true;
    }
    
    private Node cleanNode(final Lexer lexer, Node node) {
        final Node[] array = { null };
        Node node2;
        for (node2 = node; node != null && node.isElement(); node = node2) {
            array[0] = node2;
            final boolean dir2Div = this.dir2Div(lexer, node);
            node2 = array[0];
            if (!dir2Div) {
                final boolean nestedList = this.nestedList(lexer, node, array);
                final Node node3 = array[0];
                if (nestedList) {
                    return node3;
                }
                final boolean center2Div = this.center2Div(lexer, node, array);
                node2 = array[0];
                if (!center2Div) {
                    final boolean mergeDivs = this.mergeDivs(lexer, node);
                    node2 = array[0];
                    if (!mergeDivs) {
                        final boolean blockStyle = this.blockStyle(lexer, node);
                        node2 = array[0];
                        if (!blockStyle) {
                            final boolean inlineStyle = this.inlineStyle(lexer, node, array);
                            node2 = array[0];
                            if (!inlineStyle) {
                                final boolean font2Span = this.font2Span(lexer, node, array);
                                node2 = array[0];
                                if (!font2Span) {
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        return node2;
    }
    
    private Node createStyleProperties(final Lexer lexer, final Node node, final Node[] array) {
        Node node2 = node.content;
        if (node2 != null) {
            final Node[] array2 = { node };
            while (node2 != null) {
                node2 = this.createStyleProperties(lexer, node2, array2);
                if (array2[0] != node) {
                    return array2[0];
                }
                if (node2 == null) {
                    continue;
                }
                node2 = node2.next;
            }
        }
        return this.cleanNode(lexer, node);
    }
    
    private void defineStyleRules(final Lexer lexer, final Node node) {
        if (node.content != null) {
            for (Node node2 = node.content; node2 != null; node2 = node2.next) {
                this.defineStyleRules(lexer, node2);
            }
        }
        this.style2Rule(lexer, node);
    }
    
    public void cleanTree(final Lexer lexer, Node styleProperties) {
        styleProperties = this.createStyleProperties(lexer, styleProperties, new Node[] { styleProperties });
        if (!lexer.configuration.makeClean) {
            this.defineStyleRules(lexer, styleProperties);
            this.createStyleElement(lexer, styleProperties);
        }
    }
    
    public void nestedEmphasis(Node node) {
        final Node[] array = { null };
        while (node != null) {
            final Node next = node.next;
            if ((node.tag == this.tt.tagB || node.tag == this.tt.tagI) && node.parent != null && node.parent.tag == node.tag) {
                array[0] = next;
                this.discardContainer(node, array);
                node = array[0];
            }
            else {
                if (node.content != null) {
                    this.nestedEmphasis(node.content);
                }
                node = next;
            }
        }
    }
    
    public void emFromI(Node next) {
        while (next != null) {
            if (next.tag == this.tt.tagI) {
                next.element = this.tt.tagEm.name;
                next.tag = this.tt.tagEm;
            }
            else if (next.tag == this.tt.tagB) {
                next.element = this.tt.tagStrong.name;
                next.tag = this.tt.tagStrong;
            }
            if (next.content != null) {
                this.emFromI(next.content);
            }
            next = next.next;
        }
    }
    
    public void list2BQ(Node next) {
        while (next != null) {
            if (next.content != null) {
                this.list2BQ(next.content);
            }
            if (next.tag != null && next.tag.getParser() == ParserImpl.LIST && next.hasOneChild() && next.content.implicit) {
                this.stripOnlyChild(next);
                next.element = this.tt.tagBlockquote.name;
                next.tag = this.tt.tagBlockquote;
                next.implicit = true;
            }
            next = next.next;
        }
    }
    
    public void bQ2Div(Node next) {
        while (next != null) {
            if (next.tag == this.tt.tagBlockquote && next.implicit) {
                int n = 1;
                while (next.hasOneChild() && next.content.tag == this.tt.tagBlockquote && next.implicit) {
                    ++n;
                    this.stripOnlyChild(next);
                }
                if (next.content != null) {
                    this.bQ2Div(next.content);
                }
                final String string = "margin-left: " + new Integer(2 * n).toString() + "em";
                next.element = this.tt.tagDiv.name;
                next.tag = this.tt.tagDiv;
                final AttVal attrByName = next.getAttrByName("style");
                if (attrByName != null && attrByName.value != null) {
                    attrByName.value = string + "; " + attrByName.value;
                }
                else {
                    next.addAttribute("style", string);
                }
            }
            else if (next.content != null) {
                this.bQ2Div(next.content);
            }
            next = next.next;
        }
    }
    
    Node findEnclosingCell(final Node node) {
        for (Node parent = node; parent != null; parent = parent.parent) {
            if (parent.tag == this.tt.tagTd) {
                return parent;
            }
        }
        return null;
    }
    
    public Node pruneSection(final Lexer lexer, Node node) {
        while (true) {
            node = Node.discardElement(node);
            if (node == null) {
                return null;
            }
            if (node.type != 9) {
                continue;
            }
            if (TidyUtils.getString(node.textarray, node.start, 2).equals("if")) {
                node = this.pruneSection(lexer, node);
            }
            else {
                if (TidyUtils.getString(node.textarray, node.start, 5).equals("endif")) {
                    node = Node.discardElement(node);
                    return node;
                }
                continue;
            }
        }
    }
    
    public void dropSections(final Lexer lexer, Node node) {
        while (node != null) {
            if (node.type == 9) {
                if (TidyUtils.getString(node.textarray, node.start, 2).equals("if") && !TidyUtils.getString(node.textarray, node.start, 7).equals("if !vml")) {
                    node = this.pruneSection(lexer, node);
                }
                else {
                    node = Node.discardElement(node);
                }
            }
            else {
                if (node.content != null) {
                    this.dropSections(lexer, node.content);
                }
                node = node.next;
            }
        }
    }
    
    public void purgeWord2000Attributes(final Node node) {
        AttVal attVal = null;
        AttVal next;
        for (AttVal attributes = node.attributes; attributes != null; attributes = next) {
            next = attributes.next;
            if (attributes.attribute != null && attributes.value != null && attributes.attribute.equals("class") && (attributes.value.equals("Code") || !attributes.value.startsWith("Mso"))) {
                attVal = attributes;
            }
            else if (attributes.attribute != null && (attributes.attribute.equals("class") || attributes.attribute.equals("style") || attributes.attribute.equals("lang") || attributes.attribute.startsWith("x:") || ((attributes.attribute.equals("height") || attributes.attribute.equals("width")) && (node.tag == this.tt.tagTd || node.tag == this.tt.tagTr || node.tag == this.tt.tagTh)))) {
                if (attVal != null) {
                    attVal.next = next;
                }
                else {
                    node.attributes = next;
                }
            }
            else {
                attVal = attributes;
            }
        }
    }
    
    public Node stripSpan(final Lexer lexer, final Node node) {
        Node prev = null;
        this.cleanWord2000(lexer, node.content);
        Node node2 = node.content;
        if (node.prev != null) {
            prev = node.prev;
        }
        else if (node2 != null) {
            final Node node3 = node2;
            node2 = node2.next;
            node3.removeNode();
            Node.insertNodeBeforeElement(node, node3);
            prev = node3;
        }
        while (node2 != null) {
            final Node node4 = node2;
            node2 = node2.next;
            node4.removeNode();
            prev.insertNodeAfterElement(node4);
            prev = node4;
        }
        if (node.next == null) {
            node.parent.last = prev;
        }
        final Node next = node.next;
        node.content = null;
        Node.discardElement(node);
        return next;
    }
    
    private void normalizeSpaces(final Lexer lexer, Node next) {
        while (next != null) {
            if (next.content != null) {
                this.normalizeSpaces(lexer, next.content);
            }
            if (next.type == 4) {
                final int[] array = { 0 };
                int n = next.start;
                for (int i = next.start; i < next.end; ++i) {
                    array[0] = next.textarray[i];
                    if (array[0] > 127) {
                        i += PPrint.getUTF8(next.textarray, i, array);
                    }
                    if (array[0] == 160) {
                        array[0] = 32;
                    }
                    n = PPrint.putUTF8(next.textarray, n, array[0]);
                }
            }
            next = next.next;
        }
    }
    
    boolean noMargins(final Node node) {
        final AttVal attrByName = node.getAttrByName("style");
        return attrByName != null && attrByName.value != null && attrByName.value.indexOf("margin-top: 0") != -1 && attrByName.value.indexOf("margin-bottom: 0") != -1;
    }
    
    boolean singleSpace(final Lexer lexer, Node content) {
        if (content.content != null) {
            content = content.content;
            if (content.next != null) {
                return false;
            }
            if (content.type != 4) {
                return false;
            }
            if (content.end - content.start == 1 && lexer.lexbuf[content.start] == 32) {
                return true;
            }
            if (content.end - content.start == 2) {
                final int[] array = { 0 };
                PPrint.getUTF8(lexer.lexbuf, content.start, array);
                if (array[0] == 160) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public void cleanWord2000(final Lexer lexer, Node node) {
        Node node2 = null;
        while (node != null) {
            if (node.tag == this.tt.tagHtml) {
                if (node.getAttrByName("xmlns:o") == null) {
                    return;
                }
                lexer.configuration.tt.freeAttrs(node);
            }
            if (node.tag == this.tt.tagP && this.noMargins(node)) {
                Node.coerceNode(lexer, node, this.tt.tagPre);
                this.purgeWord2000Attributes(node);
                if (node.content != null) {
                    this.cleanWord2000(lexer, node.content);
                }
                final Node node3 = node;
                Node next;
                for (node = node.next; node.tag == this.tt.tagP && this.noMargins(node); node = next) {
                    next = node.next;
                    node.removeNode();
                    node3.insertNodeAtEnd(lexer.newLineNode());
                    node3.insertNodeAtEnd(node);
                    this.stripSpan(lexer, node);
                }
                if (node == null) {
                    break;
                }
            }
            if (node.tag != null && TidyUtils.toBoolean(node.tag.model & 0x8) && this.singleSpace(lexer, node)) {
                node = this.stripSpan(lexer, node);
            }
            else if (node.tag == this.tt.tagStyle || node.tag == this.tt.tagMeta || node.type == 2) {
                node = Node.discardElement(node);
            }
            else if (node.tag == this.tt.tagSpan || node.tag == this.tt.tagFont) {
                node = this.stripSpan(lexer, node);
            }
            else {
                if (node.tag == this.tt.tagLink) {
                    final AttVal attrByName = node.getAttrByName("rel");
                    if (attrByName != null && attrByName.value != null && attrByName.value.equals("File-List")) {
                        node = Node.discardElement(node);
                        continue;
                    }
                }
                if (node.content == null && node.tag == this.tt.tagP) {
                    node = Node.discardElement(node);
                }
                else {
                    if (node.tag == this.tt.tagP) {
                        final AttVal attrByName2 = node.getAttrByName("class");
                        final AttVal attrByName3 = node.getAttrByName("style");
                        if (attrByName2 != null && attrByName2.value != null && (attrByName2.value.equals("MsoListBullet") || attrByName2.value.equals("MsoListNumber") || (attrByName3 != null && attrByName3.value.indexOf("mso-list:") != -1))) {
                            Dict dict = this.tt.tagUl;
                            if (attrByName2.value.equals("MsoListNumber")) {
                                dict = this.tt.tagOl;
                            }
                            Node.coerceNode(lexer, node, this.tt.tagLi);
                            if (node2 == null || node2.tag != dict) {
                                node2 = lexer.inferredTag(dict.name);
                                Node.insertNodeBeforeElement(node, node2);
                            }
                            this.purgeWord2000Attributes(node);
                            if (node.content != null) {
                                this.cleanWord2000(lexer, node.content);
                            }
                            node.removeNode();
                            node2.insertNodeAtEnd(node);
                            node = node2;
                        }
                        else if (attrByName2 != null && attrByName2.value != null && attrByName2.value.equals("Code")) {
                            final Node lineNode = lexer.newLineNode();
                            this.normalizeSpaces(lexer, node);
                            if (node2 == null || node2.tag != this.tt.tagPre) {
                                node2 = lexer.inferredTag("pre");
                                Node.insertNodeBeforeElement(node, node2);
                            }
                            node.removeNode();
                            node2.insertNodeAtEnd(node);
                            this.stripSpan(lexer, node);
                            node2.insertNodeAtEnd(lineNode);
                            node = node2.next;
                        }
                        else {
                            node2 = null;
                        }
                    }
                    else {
                        node2 = null;
                    }
                    if (node.type == 5 || node.type == 7) {
                        this.purgeWord2000Attributes(node);
                    }
                    if (node.content != null) {
                        this.cleanWord2000(lexer, node.content);
                    }
                    node = node.next;
                }
            }
        }
    }
    
    public boolean isWord2000(final Node node) {
        final Node html = node.findHTML(this.tt);
        if (html != null && html.getAttrByName("xmlns:o") != null) {
            return true;
        }
        final Node head = node.findHEAD(this.tt);
        if (head != null) {
            for (Node node2 = head.content; node2 != null; node2 = node2.next) {
                if (node2.tag == this.tt.tagMeta) {
                    final AttVal attrByName = node2.getAttrByName("name");
                    if (attrByName != null) {
                        if (attrByName.value != null) {
                            if ("generator".equals(attrByName.value)) {
                                final AttVal attrByName2 = node2.getAttrByName("content");
                                if (attrByName2 != null) {
                                    if (attrByName2.value != null) {
                                        if (attrByName2.value.indexOf("Microsoft") != -1) {
                                            return true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
    
    static void bumpObject(final Lexer lexer, final Node node) {
        if (node == null) {
            return;
        }
        Node node2 = null;
        Node node3 = null;
        final TagTable tt = lexer.configuration.tt;
        for (Node node4 = node.content; node4 != null; node4 = node4.next) {
            if (node4.tag == tt.tagHead) {
                node2 = node4;
            }
            if (node4.tag == tt.tagBody) {
                node3 = node4;
            }
        }
        if (node2 != null && node3 != null) {
            Node next;
            for (Node content = node2.content; content != null; content = next) {
                next = content.next;
                if (content.tag == tt.tagObject) {
                    boolean b = false;
                    for (Node node5 = content.content; node5 != null; node5 = node5.next) {
                        if ((node5.type == 4 && !content.isBlank(lexer)) || node5.tag != tt.tagParam) {
                            b = true;
                            break;
                        }
                    }
                    if (b) {
                        content.removeNode();
                        node3.insertNodeAtStart(content);
                    }
                }
            }
        }
    }
}
