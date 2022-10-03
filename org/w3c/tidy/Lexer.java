package org.w3c.tidy;

import java.util.Vector;
import java.util.List;
import java.util.Stack;
import java.io.PrintWriter;

public class Lexer
{
    public static final short IGNORE_WHITESPACE = 0;
    public static final short MIXED_CONTENT = 1;
    public static final short PREFORMATTED = 2;
    public static final short IGNORE_MARKUP = 3;
    private static final String VOYAGER_LOOSE = "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd";
    private static final String VOYAGER_STRICT = "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd";
    private static final String VOYAGER_FRAMESET = "http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd";
    private static final String VOYAGER_11 = "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd";
    private static final String XHTML_NAMESPACE = "http://www.w3.org/1999/xhtml";
    private static final W3CVersionInfo[] W3CVERSION;
    private static final short LEX_CONTENT = 0;
    private static final short LEX_GT = 1;
    private static final short LEX_ENDTAG = 2;
    private static final short LEX_STARTTAG = 3;
    private static final short LEX_COMMENT = 4;
    private static final short LEX_DOCTYPE = 5;
    private static final short LEX_PROCINSTR = 6;
    private static final short LEX_CDATA = 8;
    private static final short LEX_SECTION = 9;
    private static final short LEX_ASP = 10;
    private static final short LEX_JSTE = 11;
    private static final short LEX_PHP = 12;
    private static final short LEX_XMLDECL = 13;
    protected StreamIn in;
    protected PrintWriter errout;
    protected short badAccess;
    protected short badLayout;
    protected short badChars;
    protected short badForm;
    protected short warnings;
    protected short errors;
    protected int lines;
    protected int columns;
    protected boolean waswhite;
    protected boolean pushed;
    protected boolean insertspace;
    protected boolean excludeBlocks;
    protected boolean exiled;
    protected boolean isvoyager;
    protected short versions;
    protected int doctype;
    protected boolean badDoctype;
    protected int txtstart;
    protected int txtend;
    protected short state;
    protected Node token;
    protected byte[] lexbuf;
    protected int lexlength;
    protected int lexsize;
    protected Node inode;
    protected int insert;
    protected Stack istack;
    protected int istackbase;
    protected Style styles;
    protected Configuration configuration;
    protected boolean seenEndBody;
    protected boolean seenEndHtml;
    protected Report report;
    protected Node root;
    private List nodeList;
    private static final int CDATA_INTERMEDIATE = 0;
    private static final int CDATA_STARTTAG = 1;
    private static final int CDATA_ENDTAG = 2;
    
    public Lexer(final StreamIn in, final Configuration configuration, final Report report) {
        this.report = report;
        this.in = in;
        this.lines = 1;
        this.columns = 1;
        this.state = 0;
        this.versions = 3551;
        this.doctype = 0;
        this.insert = -1;
        this.istack = new Stack();
        this.configuration = configuration;
        this.nodeList = new Vector();
    }
    
    public Node newNode() {
        final Node node = new Node();
        this.nodeList.add(node);
        return node;
    }
    
    public Node newNode(final short n, final byte[] array, final int n2, final int n3) {
        final Node node = new Node(n, array, n2, n3);
        this.nodeList.add(node);
        return node;
    }
    
    public Node newNode(final short n, final byte[] array, final int n2, final int n3, final String s) {
        final Node node = new Node(n, array, n2, n3, s, this.configuration.tt);
        this.nodeList.add(node);
        return node;
    }
    
    public Node cloneNode(final Node node) {
        final Node cloneNode = node.cloneNode(false);
        this.nodeList.add(cloneNode);
        for (AttVal attVal = cloneNode.attributes; attVal != null; attVal = attVal.next) {
            if (attVal.asp != null) {
                this.nodeList.add(attVal.asp);
            }
            if (attVal.php != null) {
                this.nodeList.add(attVal.php);
            }
        }
        return cloneNode;
    }
    
    public AttVal cloneAttributes(final AttVal attVal) {
        AttVal next;
        AttVal attVal2;
        for (attVal2 = (next = (AttVal)attVal.clone()); next != null; next = next.next) {
            if (next.asp != null) {
                this.nodeList.add(next.asp);
            }
            if (next.php != null) {
                this.nodeList.add(next.php);
            }
        }
        return attVal2;
    }
    
    protected void updateNodeTextArrays(final byte[] array, final byte[] textarray) {
        for (int i = 0; i < this.nodeList.size(); ++i) {
            final Node node = this.nodeList.get(i);
            if (node.textarray == array) {
                node.textarray = textarray;
            }
        }
    }
    
    public Node newLineNode() {
        final Node node = this.newNode();
        node.textarray = this.lexbuf;
        node.start = this.lexsize;
        this.addCharToLexer(10);
        node.end = this.lexsize;
        return node;
    }
    
    public boolean endOfInput() {
        return this.in.isEndOfStream();
    }
    
    public void addByte(final int n) {
        if (this.lexsize + 1 >= this.lexlength) {
            while (this.lexsize + 1 >= this.lexlength) {
                if (this.lexlength == 0) {
                    this.lexlength = 8192;
                }
                else {
                    this.lexlength *= 2;
                }
            }
            final byte[] lexbuf = this.lexbuf;
            this.lexbuf = new byte[this.lexlength];
            if (lexbuf != null) {
                System.arraycopy(lexbuf, 0, this.lexbuf, 0, lexbuf.length);
                this.updateNodeTextArrays(lexbuf, this.lexbuf);
            }
        }
        this.lexbuf[this.lexsize++] = (byte)n;
        this.lexbuf[this.lexsize] = 0;
    }
    
    public void changeChar(final byte b) {
        if (this.lexsize > 0) {
            this.lexbuf[this.lexsize - 1] = b;
        }
    }
    
    public void addCharToLexer(final int n) {
        if ((this.configuration.xmlOut || this.configuration.xHTML) && (n < 32 || n > 55295) && n != 9 && n != 10 && n != 13 && (n < 57344 || n > 65533) && (n < 65536 || n > 1114111)) {
            return;
        }
        final int[] array = { 0 };
        final byte[] array2 = new byte[10];
        if (EncodingUtils.encodeCharToUTF8Bytes(n, array2, null, array)) {
            array2[0] = -17;
            array2[1] = -65;
            array2[2] = -67;
            array[0] = 3;
        }
        for (int i = 0; i < array[0]; ++i) {
            this.addByte(array2[i]);
        }
    }
    
    public void addStringToLexer(final String s) {
        for (int i = 0; i < s.length(); ++i) {
            this.addCharToLexer(s.charAt(i));
        }
    }
    
    public void parseEntity(final short n) {
        int n2 = 1;
        boolean b = false;
        final int lexsize = this.lexsize - 1;
        final int n3 = this.in.getCurcol() - 1;
        int char1;
        while ((char1 = this.in.readChar()) != -1) {
            if (char1 == 59) {
                b = true;
                break;
            }
            if (n2 != 0 && char1 == 35) {
                if (!this.configuration.ncr || "BIG5".equals(this.configuration.getInCharEncodingName()) || "SHIFTJIS".equals(this.configuration.getInCharEncodingName())) {
                    this.in.ungetChar(char1);
                    return;
                }
                this.addCharToLexer(char1);
                n2 = 0;
            }
            else {
                n2 = 0;
                if (!TidyUtils.isNamechar((char)char1)) {
                    this.in.ungetChar(char1);
                    break;
                }
                this.addCharToLexer(char1);
            }
        }
        final String string = TidyUtils.getString(this.lexbuf, lexsize, this.lexsize - lexsize);
        if ("&apos".equals(string) && !this.configuration.xmlOut && !this.isvoyager && !this.configuration.xHTML) {
            this.report.entityError(this, (short)5, string, 39);
        }
        int entityCode = EntityTable.getDefaultEntityTable().entityCode(string);
        if (entityCode <= 0 || (entityCode >= 256 && char1 != 59)) {
            this.lines = this.in.getCurline();
            this.columns = n3;
            if (this.lexsize > lexsize + 1) {
                if (entityCode >= 128 && entityCode <= 159) {
                    int n4 = 0;
                    if ("WIN1252".equals(this.configuration.replacementCharEncoding)) {
                        n4 = EncodingUtils.decodeWin1252(entityCode);
                    }
                    else if ("MACROMAN".equals(this.configuration.replacementCharEncoding)) {
                        n4 = EncodingUtils.decodeMacRoman(entityCode);
                    }
                    final boolean b2 = n4 == 0;
                    if (char1 != 59) {
                        this.report.entityError(this, (short)2, string, char1);
                    }
                    this.report.encodingError(this, (short)(0x52 | (b2 ? 1 : 0)), entityCode);
                    if (n4 != 0) {
                        this.lexsize = lexsize;
                        this.addCharToLexer(n4);
                        b = false;
                    }
                    else {
                        this.lexsize = lexsize;
                        b = false;
                    }
                }
                else {
                    this.report.entityError(this, (short)3, string, entityCode);
                }
                if (b) {
                    this.addCharToLexer(59);
                }
            }
            else {
                this.report.entityError(this, (short)4, string, entityCode);
            }
        }
        else {
            if (char1 != 59) {
                this.lines = this.in.getCurline();
                this.columns = n3;
                this.report.entityError(this, (short)1, string, char1);
            }
            this.lexsize = lexsize;
            if (entityCode == 160 && TidyUtils.toBoolean(n & 0x2)) {
                entityCode = 32;
            }
            this.addCharToLexer(entityCode);
            if (entityCode == 38 && !this.configuration.quoteAmpersand) {
                this.addCharToLexer(97);
                this.addCharToLexer(109);
                this.addCharToLexer(112);
                this.addCharToLexer(59);
            }
        }
    }
    
    public char parseTagName() {
        final byte b = this.lexbuf[this.txtstart];
        if (!this.configuration.xmlTags && TidyUtils.isUpper((char)b)) {
            this.lexbuf[this.txtstart] = (byte)TidyUtils.toLower((char)b);
        }
        int n;
        while ((n = this.in.readChar()) != -1 && TidyUtils.isNamechar((char)n)) {
            if (!this.configuration.xmlTags && TidyUtils.isUpper((char)n)) {
                n = TidyUtils.toLower((char)n);
            }
            this.addCharToLexer(n);
        }
        this.txtend = this.lexsize;
        return (char)n;
    }
    
    public void addStringLiteral(final String s) {
        for (int length = s.length(), i = 0; i < length; ++i) {
            this.addCharToLexer(s.charAt(i));
        }
    }
    
    void addStringLiteralLen(final String s, int n) {
        final int length = s.length();
        if (length < n) {
            n = length;
        }
        for (int i = 0; i < n; ++i) {
            this.addCharToLexer(s.charAt(i));
        }
    }
    
    public short htmlVersion() {
        if (TidyUtils.toBoolean(this.versions & 0x1)) {
            return 1;
        }
        if (!(this.configuration.xmlOut | this.configuration.xmlTags | this.isvoyager) && TidyUtils.toBoolean(this.versions & 0x2)) {
            return 2;
        }
        if (TidyUtils.toBoolean(this.versions & 0x400)) {
            return 1024;
        }
        if (TidyUtils.toBoolean(this.versions & 0x4)) {
            return 4;
        }
        if (TidyUtils.toBoolean(this.versions & 0x8)) {
            return 8;
        }
        if (TidyUtils.toBoolean(this.versions & 0x10)) {
            return 16;
        }
        return 0;
    }
    
    public String htmlVersionName() {
        final short apparentVersion = this.apparentVersion();
        int i = 0;
        while (i < Lexer.W3CVERSION.length) {
            if (apparentVersion == Lexer.W3CVERSION[i].code) {
                if (this.isvoyager) {
                    return Lexer.W3CVERSION[i].voyagerName;
                }
                return Lexer.W3CVERSION[i].name;
            }
            else {
                ++i;
            }
        }
        return null;
    }
    
    public boolean addGenerator(final Node node) {
        final Node head = node.findHEAD(this.configuration.tt);
        if (head != null) {
            final String string = "HTML Tidy for Java (vers. " + Report.RELEASE_DATE_STRING + "), see jtidy.sourceforge.net";
            for (Node node2 = head.content; node2 != null; node2 = node2.next) {
                if (node2.tag == this.configuration.tt.tagMeta) {
                    final AttVal attrByName = node2.getAttrByName("name");
                    if (attrByName != null && attrByName.value != null && "generator".equalsIgnoreCase(attrByName.value)) {
                        final AttVal attrByName2 = node2.getAttrByName("content");
                        if (attrByName2 != null && attrByName2.value != null && attrByName2.value.length() >= 9 && "HTML Tidy".equalsIgnoreCase(attrByName2.value.substring(0, 9))) {
                            attrByName2.value = string;
                            return false;
                        }
                    }
                }
            }
            final Node inferredTag = this.inferredTag("meta");
            inferredTag.addAttribute("content", string);
            inferredTag.addAttribute("name", "generator");
            head.insertNodeAtStart(inferredTag);
            return true;
        }
        return false;
    }
    
    public boolean checkDocTypeKeyWords(final Node node) {
        final String string = TidyUtils.getString(this.lexbuf, node.start, node.end - node.start);
        return !TidyUtils.findBadSubString("SYSTEM", string, string.length()) && !TidyUtils.findBadSubString("PUBLIC", string, string.length()) && !TidyUtils.findBadSubString("//DTD", string, string.length()) && !TidyUtils.findBadSubString("//W3C", string, string.length()) && !TidyUtils.findBadSubString("//EN", string, string.length());
    }
    
    public short findGivenVersion(final Node node) {
        if (!"html ".equalsIgnoreCase(TidyUtils.getString(this.lexbuf, node.start, 5))) {
            return 0;
        }
        if (!this.checkDocTypeKeyWords(node)) {
            this.report.warning(this, node, null, (short)37);
        }
        final String string = TidyUtils.getString(this.lexbuf, node.start + 5, 7);
        if ("SYSTEM ".equalsIgnoreCase(string)) {
            if (!string.substring(0, 6).equals("SYSTEM")) {
                System.arraycopy(TidyUtils.getBytes("SYSTEM"), 0, this.lexbuf, node.start + 5, 6);
            }
            return 0;
        }
        if ("PUBLIC ".equalsIgnoreCase(string)) {
            if (!string.substring(0, 6).equals("PUBLIC")) {
                System.arraycopy(TidyUtils.getBytes("PUBLIC "), 0, this.lexbuf, node.start + 5, 6);
            }
        }
        else {
            this.badDoctype = true;
        }
        int i = node.start;
        while (i < node.end) {
            if (this.lexbuf[i] == 34) {
                final String string2 = TidyUtils.getString(this.lexbuf, i + 1, 12);
                final String string3 = TidyUtils.getString(this.lexbuf, i + 1, 13);
                if (string2.equals("-//W3C//DTD ")) {
                    int n;
                    for (n = i + 13; n < node.end && this.lexbuf[n] != 47; ++n) {}
                    final int n2 = n - i - 13;
                    final String string4 = TidyUtils.getString(this.lexbuf, i + 13, n2);
                    for (int j = 1; j < Lexer.W3CVERSION.length; ++j) {
                        final String name = Lexer.W3CVERSION[j].name;
                        if (n2 == name.length() && name.equals(string4)) {
                            return Lexer.W3CVERSION[j].code;
                        }
                    }
                    break;
                }
                if (!string3.equals("-//IETF//DTD ")) {
                    break;
                }
                int n3;
                for (n3 = i + 14; n3 < node.end && this.lexbuf[n3] != 47; ++n3) {}
                final int n4 = n3 - i - 14;
                final String string5 = TidyUtils.getString(this.lexbuf, i + 14, n4);
                final String name2 = Lexer.W3CVERSION[0].name;
                if (n4 == name2.length() && name2.equals(string5)) {
                    return Lexer.W3CVERSION[0].code;
                }
                break;
            }
            else {
                ++i;
            }
        }
        return 0;
    }
    
    public void fixHTMLNameSpace(final Node node, final String value) {
        Node node2;
        for (node2 = node.content; node2 != null && node2.tag != this.configuration.tt.tagHtml; node2 = node2.next) {}
        if (node2 != null) {
            AttVal attVal;
            for (attVal = node2.attributes; attVal != null && !attVal.attribute.equals("xmlns"); attVal = attVal.next) {}
            if (attVal != null) {
                if (!attVal.value.equals(value)) {
                    this.report.warning(this, node2, null, (short)33);
                    attVal.value = value;
                }
            }
            else {
                final AttVal attributes = new AttVal(node2.attributes, null, 34, "xmlns", value);
                attributes.dict = AttributeTable.getDefaultAttributeTable().findAttribute(attributes);
                node2.attributes = attributes;
            }
        }
    }
    
    Node newXhtmlDocTypeNode(final Node parent) {
        final Node html = parent.findHTML(this.configuration.tt);
        if (html == null) {
            return null;
        }
        final Node node = this.newNode();
        node.setType((short)1);
        node.next = html;
        node.parent = parent;
        node.prev = null;
        if (html == parent.content) {
            parent.content.prev = node;
            parent.content = node;
            node.prev = null;
        }
        else {
            node.prev = html.prev;
            node.prev.next = node;
        }
        return html.prev = node;
    }
    
    public boolean setXHTMLDocType(final Node node) {
        String docTypeStr = " ";
        String s = "";
        final String s2 = "http://www.w3.org/1999/xhtml";
        String substring = null;
        int n = 0;
        Node node2 = node.findDocType();
        this.fixHTMLNameSpace(node, s2);
        if (this.configuration.docTypeMode == 0) {
            if (node2 != null) {
                Node.discardElement(node2);
            }
            return true;
        }
        if (this.configuration.docTypeMode == 1) {
            if (TidyUtils.toBoolean(this.versions & 0x4)) {
                docTypeStr = "-//W3C//DTD XHTML 1.0 Strict//EN";
                s = "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd";
            }
            else if (TidyUtils.toBoolean(this.versions & 0x10)) {
                docTypeStr = "-//W3C//DTD XHTML 1.0 Frameset//EN";
                s = "http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd";
            }
            else if (TidyUtils.toBoolean(this.versions & 0x1A)) {
                docTypeStr = "-//W3C//DTD XHTML 1.0 Transitional//EN";
                s = "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd";
            }
            else if (TidyUtils.toBoolean(this.versions & 0x400)) {
                docTypeStr = "-//W3C//DTD XHTML 1.1//EN";
                s = "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd";
            }
            else {
                docTypeStr = null;
                s = "";
                if (node2 != null) {
                    Node.discardElement(node2);
                }
            }
        }
        else if (this.configuration.docTypeMode == 2) {
            docTypeStr = "-//W3C//DTD XHTML 1.0 Strict//EN";
            s = "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd";
        }
        else if (this.configuration.docTypeMode == 3) {
            docTypeStr = "-//W3C//DTD XHTML 1.0 Transitional//EN";
            s = "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd";
        }
        if (this.configuration.docTypeMode == 4 && this.configuration.docTypeStr != null) {
            docTypeStr = this.configuration.docTypeStr;
            s = "";
        }
        if (docTypeStr == null) {
            return false;
        }
        if (node2 != null) {
            if (this.configuration.xHTML || this.configuration.xmlOut) {
                final String string = TidyUtils.getString(this.lexbuf, node2.start, node2.end - node2.start + 1);
                final int index = string.indexOf(91);
                if (index >= 0) {
                    final int index2 = string.substring(index).indexOf(93);
                    if (index2 >= 0) {
                        n = index2 + 1;
                        substring = string.substring(index);
                    }
                }
            }
        }
        else if ((node2 = this.newXhtmlDocTypeNode(node)) == null) {
            return false;
        }
        this.txtstart = this.lexsize;
        this.txtend = this.lexsize;
        this.addStringLiteral("html PUBLIC ");
        if (docTypeStr.charAt(0) == '\"') {
            this.addStringLiteral(docTypeStr);
        }
        else {
            this.addStringLiteral("\"");
            this.addStringLiteral(docTypeStr);
            this.addStringLiteral("\"");
        }
        if (this.configuration.wraplen != 0 && s.length() + 6 >= this.configuration.wraplen) {
            this.addStringLiteral("\n\"");
        }
        else {
            this.addStringLiteral(" \"");
        }
        this.addStringLiteral(s);
        this.addStringLiteral("\"");
        if (n > 0 && substring != null) {
            this.addCharToLexer(32);
            this.addStringLiteralLen(substring, n);
        }
        this.txtend = this.lexsize;
        final int end = this.txtend - this.txtstart;
        node2.textarray = new byte[end];
        System.arraycopy(this.lexbuf, this.txtstart, node2.textarray, 0, end);
        node2.start = 0;
        node2.end = end;
        return false;
    }
    
    public short apparentVersion() {
        switch (this.doctype) {
            case 0: {
                return this.htmlVersion();
            }
            case 1: {
                if (TidyUtils.toBoolean(this.versions & 0x1)) {
                    return 1;
                }
                break;
            }
            case 2: {
                if (TidyUtils.toBoolean(this.versions & 0x2)) {
                    return 2;
                }
                break;
            }
            case 4: {
                if (TidyUtils.toBoolean(this.versions & 0x4)) {
                    return 4;
                }
                break;
            }
            case 8: {
                if (TidyUtils.toBoolean(this.versions & 0x8)) {
                    return 8;
                }
                break;
            }
            case 16: {
                if (TidyUtils.toBoolean(this.versions & 0x10)) {
                    return 16;
                }
                break;
            }
            case 1024: {
                if (TidyUtils.toBoolean(this.versions & 0x400)) {
                    return 1024;
                }
                break;
            }
        }
        this.lines = 1;
        this.columns = 1;
        this.report.warning(this, null, null, (short)28);
        return this.htmlVersion();
    }
    
    public boolean fixDocType(final Node node) {
        short htmlVersion = 4;
        if (this.badDoctype) {
            this.report.warning(this, null, null, (short)35);
        }
        Node node2 = node.findDocType();
        if (this.configuration.docTypeMode == 0) {
            if (node2 != null) {
                Node.discardElement(node2);
            }
            return true;
        }
        if (this.configuration.xmlOut) {
            return true;
        }
        if (this.configuration.docTypeMode == 2) {
            Node.discardElement(node2);
            node2 = null;
            htmlVersion = 4;
        }
        else if (this.configuration.docTypeMode == 3) {
            Node.discardElement(node2);
            node2 = null;
            htmlVersion = 8;
        }
        else if (this.configuration.docTypeMode == 1) {
            if (node2 != null) {
                if (this.doctype == 0) {
                    return false;
                }
                switch (this.doctype) {
                    case 0: {
                        return false;
                    }
                    case 1: {
                        if (TidyUtils.toBoolean(this.versions & 0x1)) {
                            return true;
                        }
                        break;
                    }
                    case 2: {
                        if (TidyUtils.toBoolean(this.versions & 0x2)) {
                            return true;
                        }
                        break;
                    }
                    case 4: {
                        if (TidyUtils.toBoolean(this.versions & 0x4)) {
                            return true;
                        }
                        break;
                    }
                    case 8: {
                        if (TidyUtils.toBoolean(this.versions & 0x8)) {
                            return true;
                        }
                        break;
                    }
                    case 16: {
                        if (TidyUtils.toBoolean(this.versions & 0x10)) {
                            return true;
                        }
                        break;
                    }
                    case 1024: {
                        if (TidyUtils.toBoolean(this.versions & 0x400)) {
                            return true;
                        }
                        break;
                    }
                }
            }
            htmlVersion = this.htmlVersion();
        }
        if (htmlVersion == 0) {
            return false;
        }
        if (this.configuration.xmlOut || this.configuration.xmlTags || this.isvoyager) {
            if (node2 != null) {
                Node.discardElement(node2);
            }
            this.fixHTMLNameSpace(node, "http://www.w3.org/1999/xhtml");
        }
        if (node2 == null && (node2 = this.newXhtmlDocTypeNode(node)) == null) {
            return false;
        }
        this.txtstart = this.lexsize;
        this.txtend = this.lexsize;
        this.addStringLiteral("html PUBLIC ");
        if (this.configuration.docTypeMode == 4 && this.configuration.docTypeStr != null && this.configuration.docTypeStr.length() > 0) {
            if (this.configuration.docTypeStr.charAt(0) == '\"') {
                this.addStringLiteral(this.configuration.docTypeStr);
            }
            else {
                this.addStringLiteral("\"");
                this.addStringLiteral(this.configuration.docTypeStr);
                this.addStringLiteral("\"");
            }
        }
        else if (htmlVersion == 1) {
            this.addStringLiteral("\"-//IETF//DTD HTML 2.0//EN\"");
        }
        else {
            this.addStringLiteral("\"-//W3C//DTD ");
            for (int i = 0; i < Lexer.W3CVERSION.length; ++i) {
                if (htmlVersion == Lexer.W3CVERSION[i].code) {
                    this.addStringLiteral(Lexer.W3CVERSION[i].name);
                    break;
                }
            }
            this.addStringLiteral("//EN\"");
        }
        this.txtend = this.lexsize;
        final int end = this.txtend - this.txtstart;
        node2.textarray = new byte[end];
        System.arraycopy(this.lexbuf, this.txtstart, node2.textarray, 0, end);
        node2.start = 0;
        node2.end = end;
        return true;
    }
    
    public boolean fixXmlDecl(final Node node) {
        Node node2;
        if (node.content != null && node.content.type == 13) {
            node2 = node.content;
        }
        else {
            node2 = this.newNode((short)13, this.lexbuf, 0, 0);
            node2.next = node.content;
            if (node.content != null) {
                node.content.prev = node2;
                node2.next = node.content;
            }
            node.content = node2;
        }
        final AttVal attrByName = node2.getAttrByName("version");
        if (node2.getAttrByName("encoding") == null && !"UTF8".equals(this.configuration.getOutCharEncodingName())) {
            if ("ISO8859_1".equals(this.configuration.getOutCharEncodingName())) {
                node2.addAttribute("encoding", "iso-8859-1");
            }
            if ("ISO2022".equals(this.configuration.getOutCharEncodingName())) {
                node2.addAttribute("encoding", "iso-2022");
            }
        }
        if (attrByName == null) {
            node2.addAttribute("version", "1.0");
        }
        return true;
    }
    
    public Node inferredTag(final String s) {
        final Node node = this.newNode((short)5, this.lexbuf, this.txtstart, this.txtend, s);
        node.implicit = true;
        return node;
    }
    
    public Node getCDATA(final Node node) {
        int n = 0;
        int n2 = 0;
        int n3 = 0;
        int n4 = 1;
        final boolean b = node.getAttrByName("src") != null;
        this.lines = this.in.getCurline();
        this.columns = this.in.getCurcol();
        this.waswhite = false;
        this.txtstart = this.lexsize;
        this.txtend = this.lexsize;
        int char1;
        while ((char1 = this.in.readChar()) != -1) {
            this.addCharToLexer(char1);
            this.txtend = this.lexsize;
            if (n3 == 0) {
                if (char1 != 60) {
                    if (n4 == 0 || TidyUtils.isWhite((char)char1)) {
                        continue;
                    }
                    n4 = 0;
                }
                else {
                    final int char2 = this.in.readChar();
                    if (TidyUtils.isLetter((char)char2)) {
                        if (b && n4 != 0 && node.tag == this.configuration.tt.tagScript) {
                            this.lexsize = this.txtstart;
                            this.in.ungetChar(char2);
                            this.in.ungetChar(60);
                            return null;
                        }
                        this.addCharToLexer(char2);
                        n = this.lexsize - 1;
                        n3 = 1;
                    }
                    else if (char2 == 47) {
                        this.addCharToLexer(char2);
                        final int char3 = this.in.readChar();
                        if (!TidyUtils.isLetter((char)char3)) {
                            this.in.ungetChar(char3);
                        }
                        else {
                            this.in.ungetChar(char3);
                            n = this.lexsize;
                            n3 = 2;
                        }
                    }
                    else if (char2 == 92) {
                        this.addCharToLexer(char2);
                        final int char4 = this.in.readChar();
                        if (char4 != 47) {
                            this.in.ungetChar(char4);
                        }
                        else {
                            this.addCharToLexer(char4);
                            final int char5 = this.in.readChar();
                            if (!TidyUtils.isLetter((char)char5)) {
                                this.in.ungetChar(char5);
                            }
                            else {
                                this.in.ungetChar(char5);
                                n = this.lexsize;
                                n3 = 2;
                            }
                        }
                    }
                    else {
                        this.in.ungetChar(char2);
                    }
                }
            }
            else if (n3 == 1) {
                if (TidyUtils.isLetter((char)char1)) {
                    continue;
                }
                if (node.element.equalsIgnoreCase(TidyUtils.getString(this.lexbuf, n, node.element.length()))) {
                    ++n2;
                }
                n3 = 0;
            }
            else {
                if (n3 != 2) {
                    continue;
                }
                if (TidyUtils.isLetter((char)char1)) {
                    continue;
                }
                final boolean equalsIgnoreCase = node.element.equalsIgnoreCase(TidyUtils.getString(this.lexbuf, n, node.element.length()));
                if (n4 != 0 && !equalsIgnoreCase) {
                    for (int i = this.lexsize - 1; i >= n; --i) {
                        this.in.ungetChar(this.lexbuf[i]);
                    }
                    this.in.ungetChar(47);
                    this.in.ungetChar(60);
                    break;
                }
                if (equalsIgnoreCase && n2-- <= 0) {
                    for (int j = this.lexsize - 1; j >= n; --j) {
                        this.in.ungetChar(this.lexbuf[j]);
                    }
                    this.in.ungetChar(47);
                    this.in.ungetChar(60);
                    this.lexsize -= this.lexsize - n + 2;
                    break;
                }
                if (this.lexbuf[n - 2] != 92) {
                    this.lines = this.in.getCurline();
                    this.columns = this.in.getCurcol();
                    this.columns -= 3;
                    this.report.error(this, null, null, (short)32);
                    if (node.isJavaScript()) {
                        for (int k = this.lexsize; k > n - 1; --k) {
                            this.lexbuf[k] = this.lexbuf[k - 1];
                        }
                        this.lexbuf[n - 1] = 92;
                        ++this.lexsize;
                    }
                }
                n3 = 0;
            }
        }
        if (n4 != 0) {
            final int txtend = this.txtend;
            this.txtstart = txtend;
            this.lexsize = txtend;
        }
        else {
            this.txtend = this.lexsize;
        }
        if (char1 == -1) {
            this.report.error(this, node, null, (short)6);
        }
        return this.newNode((short)4, this.lexbuf, this.txtstart, this.txtend);
    }
    
    public void ungetToken() {
        this.pushed = true;
    }
    
    public Node getToken(short n) {
        int n2 = 0;
        final boolean[] array = { false };
        int n3 = 0;
        AttVal attVal = null;
        if (this.pushed && (this.token.type != 4 || (this.insert == -1 && this.inode == null))) {
            this.pushed = false;
            return this.token;
        }
        if (this.insert != -1 || this.inode != null) {
            return this.insertedToken();
        }
        this.lines = this.in.getCurline();
        this.columns = this.in.getCurcol();
        this.waswhite = false;
        this.txtstart = this.lexsize;
        this.txtend = this.lexsize;
        int n4 = 0;
    Label_0125:
        while (true) {
        Label_1136_Outer:
            while ((n4 = this.in.readChar()) != -1) {
                if (this.insertspace && n != 0) {
                    this.addCharToLexer(32);
                }
                if (this.insertspace && !TidyUtils.toBoolean(n & 0x0)) {
                    this.waswhite = true;
                    this.insertspace = false;
                }
                if (n4 == 13) {
                    final int char1 = this.in.readChar();
                    if (char1 != 10) {
                        this.in.ungetChar(char1);
                    }
                    n4 = 10;
                }
                this.addCharToLexer(n4);
                switch (this.state) {
                    case 0: {
                        if (TidyUtils.isWhite((char)n4) && n == 0 && this.lexsize == this.txtstart + 1) {
                            --this.lexsize;
                            this.waswhite = false;
                            this.lines = this.in.getCurline();
                            this.columns = this.in.getCurcol();
                            continue;
                        }
                        if (n4 == 60) {
                            this.state = 1;
                            continue;
                        }
                        if (!TidyUtils.isWhite((char)n4)) {
                            if (n4 == 38 && n != 3) {
                                this.parseEntity(n);
                            }
                            if (n == 0) {
                                n = 1;
                            }
                            this.waswhite = false;
                            continue;
                        }
                        if (this.waswhite) {
                            if (n != 2 && n != 3) {
                                --this.lexsize;
                                this.lines = this.in.getCurline();
                                this.columns = this.in.getCurcol();
                                continue;
                            }
                            continue;
                        }
                        else {
                            this.waswhite = true;
                            if (n != 2 && n != 3 && n4 != 32) {
                                this.changeChar((byte)32);
                                continue;
                            }
                            continue;
                        }
                        break;
                    }
                    case 1: {
                        if (n4 == 47) {
                            final int char2 = this.in.readChar();
                            if (char2 == -1) {
                                this.in.ungetChar(char2);
                                continue;
                            }
                            this.addCharToLexer(char2);
                            if (!TidyUtils.isLetter((char)char2)) {
                                this.waswhite = false;
                                this.state = 0;
                                continue;
                            }
                            this.lexsize -= 3;
                            this.txtend = this.lexsize;
                            this.in.ungetChar(char2);
                            this.state = 2;
                            this.lexbuf[this.lexsize] = 0;
                            this.columns -= 2;
                            if (this.txtend > this.txtstart) {
                                if (n == 0 && this.lexbuf[this.lexsize - 1] == 32) {
                                    --this.lexsize;
                                    this.txtend = this.lexsize;
                                }
                                return this.token = this.newNode((short)4, this.lexbuf, this.txtstart, this.txtend);
                            }
                            continue;
                        }
                        else {
                            if (n == 3) {
                                this.waswhite = false;
                                this.state = 0;
                                continue;
                            }
                            if (n4 == 33) {
                                final int char3 = this.in.readChar();
                                if (char3 == 45) {
                                    if (this.in.readChar() == 45) {
                                        this.state = 4;
                                        this.lexsize -= 2;
                                        this.txtend = this.lexsize;
                                        if (this.txtend > this.txtstart) {
                                            return this.token = this.newNode((short)4, this.lexbuf, this.txtstart, this.txtend);
                                        }
                                        this.txtstart = this.lexsize;
                                        continue Label_1136_Outer;
                                    }
                                    else {
                                        this.report.warning(this, null, null, (short)29);
                                    }
                                }
                                else if (char3 == 100 || char3 == 68) {
                                    this.state = 5;
                                    this.lexsize -= 2;
                                    this.txtend = this.lexsize;
                                    n = 0;
                                    Label_0973: {
                                        int char4;
                                        do {
                                            char4 = this.in.readChar();
                                            if (char4 == -1 || char4 == 62) {
                                                this.in.ungetChar(char4);
                                                break Label_0973;
                                            }
                                        } while (!TidyUtils.isWhite((char)char4));
                                        while (true) {
                                            final int char5 = this.in.readChar();
                                            if (char5 == -1 || char5 == 62) {
                                                this.in.ungetChar(char5);
                                                break;
                                            }
                                            if (TidyUtils.isWhite((char)char5)) {
                                                continue Label_1136_Outer;
                                            }
                                            this.in.ungetChar(char5);
                                            break;
                                        }
                                    }
                                    if (this.txtend > this.txtstart) {
                                        return this.token = this.newNode((short)4, this.lexbuf, this.txtstart, this.txtend);
                                    }
                                    this.txtstart = this.lexsize;
                                    continue Label_1136_Outer;
                                }
                                else if (char3 == 91) {
                                    this.lexsize -= 2;
                                    this.state = 9;
                                    this.txtend = this.lexsize;
                                    if (this.txtend > this.txtstart) {
                                        return this.token = this.newNode((short)4, this.lexbuf, this.txtstart, this.txtend);
                                    }
                                    this.txtstart = this.lexsize;
                                    continue Label_1136_Outer;
                                }
                                while (true) {
                                    int i;
                                    do {
                                        i = this.in.readChar();
                                        if (i == 62) {
                                            this.lexsize -= 2;
                                            this.lexbuf[this.lexsize] = 0;
                                            this.state = 0;
                                            continue Label_0125;
                                        }
                                    } while (i != -1);
                                    this.in.ungetChar(i);
                                    continue;
                                }
                            }
                            if (n4 == 63) {
                                this.lexsize -= 2;
                                this.state = 6;
                                this.txtend = this.lexsize;
                                if (this.txtend > this.txtstart) {
                                    return this.token = this.newNode((short)4, this.lexbuf, this.txtstart, this.txtend);
                                }
                                this.txtstart = this.lexsize;
                                continue;
                            }
                            else if (n4 == 37) {
                                this.lexsize -= 2;
                                this.state = 10;
                                this.txtend = this.lexsize;
                                if (this.txtend > this.txtstart) {
                                    return this.token = this.newNode((short)4, this.lexbuf, this.txtstart, this.txtend);
                                }
                                this.txtstart = this.lexsize;
                                continue;
                            }
                            else if (n4 == 35) {
                                this.lexsize -= 2;
                                this.state = 11;
                                this.txtend = this.lexsize;
                                if (this.txtend > this.txtstart) {
                                    return this.token = this.newNode((short)4, this.lexbuf, this.txtstart, this.txtend);
                                }
                                this.txtstart = this.lexsize;
                                continue;
                            }
                            else {
                                if (!TidyUtils.isLetter((char)n4)) {
                                    this.state = 0;
                                    this.waswhite = false;
                                    continue;
                                }
                                this.in.ungetChar(n4);
                                this.lexsize -= 2;
                                this.txtend = this.lexsize;
                                this.state = 3;
                                if (this.txtend > this.txtstart) {
                                    return this.token = this.newNode((short)4, this.lexbuf, this.txtstart, this.txtend);
                                }
                                continue;
                            }
                        }
                        break;
                    }
                    case 2: {
                        this.txtstart = this.lexsize - 1;
                        this.columns -= 2;
                        int n5 = this.parseTagName();
                        this.token = this.newNode((short)6, this.lexbuf, this.txtstart, this.txtend, TidyUtils.getString(this.lexbuf, this.txtstart, this.txtend - this.txtstart));
                        this.lexsize = this.txtstart;
                        this.txtend = this.txtstart;
                        while (TidyUtils.isWhite((char)n5)) {
                            n5 = this.in.readChar();
                        }
                        if (n5 == -1) {
                            this.in.ungetChar(n5);
                            this.report.attrError(this, this.token, null, (short)52);
                            continue;
                        }
                        if (n5 != 62) {
                            this.in.ungetChar(n5);
                            this.report.attrError(this, this.token, null, (short)52);
                        }
                        this.state = 0;
                        this.waswhite = false;
                        return this.token;
                    }
                    case 3: {
                        this.txtstart = this.lexsize - 1;
                        final char tagName = this.parseTagName();
                        array[0] = false;
                        AttVal attrs = null;
                        this.token = this.newNode((short)(array[0] ? 7 : 5), this.lexbuf, this.txtstart, this.txtend, TidyUtils.getString(this.lexbuf, this.txtstart, this.txtend - this.txtstart));
                        if (tagName != '>') {
                            if (tagName == '/') {
                                this.in.ungetChar(tagName);
                            }
                            attrs = this.parseAttrs(array);
                        }
                        if (array[0]) {
                            this.token.type = 7;
                        }
                        this.token.attributes = attrs;
                        this.lexsize = this.txtstart;
                        this.txtend = this.txtstart;
                        if ((n != 2 || this.preContent(this.token)) && (this.token.expectsContent() || this.token.tag == this.configuration.tt.tagBr)) {
                            final int char6 = this.in.readChar();
                            if (char6 == 13) {
                                final int char7 = this.in.readChar();
                                if (char7 != 10) {
                                    this.in.ungetChar(char7);
                                }
                            }
                            else if (char6 != 10 && char6 != 12) {
                                this.in.ungetChar(char6);
                            }
                            this.waswhite = true;
                        }
                        else {
                            this.waswhite = false;
                        }
                        this.state = 0;
                        if (this.token.tag == null) {
                            this.report.error(this, null, this.token, (short)22);
                        }
                        else if (!this.configuration.xmlTags) {
                            this.constrainVersion(this.token.tag.versions);
                            if (TidyUtils.toBoolean(this.token.tag.versions & 0x1C0)) {
                                if (this.configuration.makeClean && this.token.tag != this.configuration.tt.tagNobr && this.token.tag != this.configuration.tt.tagWbr) {
                                    this.report.warning(this, null, this.token, (short)21);
                                }
                                else if (!this.configuration.makeClean) {
                                    this.report.warning(this, null, this.token, (short)21);
                                }
                            }
                            if (this.token.tag.getChkattrs() != null) {
                                this.token.tag.getChkattrs().check(this, this.token);
                            }
                            else {
                                this.token.checkAttributes(this);
                            }
                            this.token.repairDuplicateAttributes(this);
                        }
                        return this.token;
                    }
                    case 4: {
                        if (n4 != 45) {
                            continue;
                        }
                        final int char8 = this.in.readChar();
                        this.addCharToLexer(char8);
                        if (char8 != 45) {
                            continue;
                        }
                        int j;
                        do {
                            j = this.in.readChar();
                            if (j == 62) {
                                if (n2 != 0) {
                                    this.report.warning(this, null, null, (short)29);
                                }
                                this.txtend = this.lexsize - 2;
                                this.lexbuf[this.lexsize] = 0;
                                this.state = 0;
                                this.waswhite = false;
                                this.token = this.newNode((short)2, this.lexbuf, this.txtstart, this.txtend);
                                int n6 = this.in.readChar();
                                if (n6 == 13) {
                                    n6 = this.in.readChar();
                                    if (n6 != 10) {
                                        this.token.linebreak = true;
                                    }
                                }
                                if (n6 == 10) {
                                    this.token.linebreak = true;
                                }
                                else {
                                    this.in.ungetChar(n6);
                                }
                                return this.token;
                            }
                            if (n2 == 0) {
                                this.lines = this.in.getCurline();
                                this.columns = this.in.getCurcol() - 3;
                            }
                            ++n2;
                            if (this.configuration.fixComments) {
                                this.lexbuf[this.lexsize - 2] = 61;
                            }
                            this.addCharToLexer(j);
                        } while (j == 45);
                        this.lexbuf[this.lexsize - 2] = 61;
                        continue;
                    }
                    case 5: {
                        if (TidyUtils.isWhite((char)n4)) {
                            if (this.waswhite) {
                                --this.lexsize;
                            }
                            this.waswhite = true;
                        }
                        else {
                            this.waswhite = false;
                        }
                        if (n3 != 0) {
                            if (n4 == 93) {
                                n3 = 0;
                            }
                        }
                        else if (n4 == 91) {
                            n3 = 1;
                        }
                        if (n3 != 0) {
                            continue;
                        }
                        if (n4 != 62) {
                            continue;
                        }
                        --this.lexsize;
                        this.txtend = this.lexsize;
                        this.lexbuf[this.lexsize] = 0;
                        this.state = 0;
                        this.waswhite = false;
                        this.token = this.newNode((short)1, this.lexbuf, this.txtstart, this.txtend);
                        this.doctype = this.findGivenVersion(this.token);
                        return this.token;
                    }
                    case 6: {
                        if (this.lexsize - this.txtstart == 3 && TidyUtils.getString(this.lexbuf, this.txtstart, 3).equals("php")) {
                            this.state = 12;
                            continue;
                        }
                        if (this.lexsize - this.txtstart == 4 && TidyUtils.getString(this.lexbuf, this.txtstart, 3).equals("xml") && TidyUtils.isWhite((char)this.lexbuf[this.txtstart + 3])) {
                            this.state = 13;
                            attVal = null;
                            continue;
                        }
                        if (this.configuration.xmlPIs) {
                            if (n4 != 63) {
                                continue;
                            }
                            n4 = this.in.readChar();
                            if (n4 == -1) {
                                this.report.warning(this, null, null, (short)36);
                                this.in.ungetChar(n4);
                                continue;
                            }
                            this.addCharToLexer(n4);
                        }
                        if (n4 != 62) {
                            continue;
                        }
                        --this.lexsize;
                        this.txtend = this.lexsize;
                        this.lexbuf[this.lexsize] = 0;
                        this.state = 0;
                        this.waswhite = false;
                        return this.token = this.newNode((short)3, this.lexbuf, this.txtstart, this.txtend);
                    }
                    case 10: {
                        if (n4 != 37) {
                            continue;
                        }
                        final int char9 = this.in.readChar();
                        if (char9 != 62) {
                            this.in.ungetChar(char9);
                            continue;
                        }
                        --this.lexsize;
                        this.txtend = this.lexsize;
                        this.lexbuf[this.lexsize] = 0;
                        this.state = 0;
                        this.waswhite = false;
                        return this.token = this.newNode((short)10, this.lexbuf, this.txtstart, this.txtend);
                    }
                    case 11: {
                        if (n4 != 35) {
                            continue;
                        }
                        final int char10 = this.in.readChar();
                        if (char10 != 62) {
                            this.in.ungetChar(char10);
                            continue;
                        }
                        --this.lexsize;
                        this.txtend = this.lexsize;
                        this.lexbuf[this.lexsize] = 0;
                        this.state = 0;
                        this.waswhite = false;
                        return this.token = this.newNode((short)11, this.lexbuf, this.txtstart, this.txtend);
                    }
                    case 12: {
                        if (n4 != 63) {
                            continue;
                        }
                        final int char11 = this.in.readChar();
                        if (char11 != 62) {
                            this.in.ungetChar(char11);
                            continue;
                        }
                        --this.lexsize;
                        this.txtend = this.lexsize;
                        this.lexbuf[this.lexsize] = 0;
                        this.state = 0;
                        this.waswhite = false;
                        return this.token = this.newNode((short)12, this.lexbuf, this.txtstart, this.txtend);
                    }
                    case 13: {
                        if (TidyUtils.isWhite((char)n4) && n4 != 63) {
                            continue;
                        }
                        if (n4 != 63) {
                            final Node[] array2 = { null };
                            final Node[] array3 = { null };
                            final AttVal attVal2 = new AttVal();
                            final int[] array4 = { 0 };
                            array[0] = false;
                            this.in.ungetChar(n4);
                            final String attribute = this.parseAttribute(array, array2, array3);
                            attVal2.attribute = attribute;
                            attVal2.value = this.parseValue(attribute, true, array, array4);
                            attVal2.delim = array4[0];
                            attVal2.next = attVal;
                            attVal = attVal2;
                        }
                        final int char12 = this.in.readChar();
                        if (char12 != 62) {
                            this.in.ungetChar(char12);
                            continue;
                        }
                        --this.lexsize;
                        this.txtend = this.txtstart;
                        this.lexbuf[this.txtend] = 0;
                        this.state = 0;
                        this.waswhite = false;
                        this.token = this.newNode((short)13, this.lexbuf, this.txtstart, this.txtend);
                        this.token.attributes = attVal;
                        return this.token;
                    }
                    case 9: {
                        if (n4 == 91 && this.lexsize == this.txtstart + 6 && TidyUtils.getString(this.lexbuf, this.txtstart, 6).equals("CDATA[")) {
                            this.state = 8;
                            this.lexsize -= 6;
                            continue;
                        }
                        if (n4 != 93) {
                            continue;
                        }
                        final int char13 = this.in.readChar();
                        if (char13 != 62) {
                            this.in.ungetChar(char13);
                            continue;
                        }
                        --this.lexsize;
                        this.txtend = this.lexsize;
                        this.lexbuf[this.lexsize] = 0;
                        this.state = 0;
                        this.waswhite = false;
                        return this.token = this.newNode((short)9, this.lexbuf, this.txtstart, this.txtend);
                    }
                    case 8: {
                        if (n4 != 93) {
                            continue;
                        }
                        final int char14 = this.in.readChar();
                        if (char14 != 93) {
                            this.in.ungetChar(char14);
                            continue;
                        }
                        final int char15 = this.in.readChar();
                        if (char15 != 62) {
                            this.in.ungetChar(char15);
                            continue;
                        }
                        --this.lexsize;
                        this.txtend = this.lexsize;
                        this.lexbuf[this.lexsize] = 0;
                        this.state = 0;
                        this.waswhite = false;
                        return this.token = this.newNode((short)8, this.lexbuf, this.txtstart, this.txtend);
                    }
                    default: {
                        continue;
                    }
                }
            }
            break;
        }
        if (this.state == 0) {
            this.txtend = this.lexsize;
            if (this.txtend > this.txtstart) {
                this.in.ungetChar(n4);
                if (this.lexbuf[this.lexsize - 1] == 32) {
                    --this.lexsize;
                    this.txtend = this.lexsize;
                }
                return this.token = this.newNode((short)4, this.lexbuf, this.txtstart, this.txtend);
            }
        }
        else if (this.state == 4) {
            if (n4 == -1) {
                this.report.warning(this, null, null, (short)29);
            }
            this.txtend = this.lexsize;
            this.lexbuf[this.lexsize] = 0;
            this.state = 0;
            this.waswhite = false;
            return this.token = this.newNode((short)2, this.lexbuf, this.txtstart, this.txtend);
        }
        return null;
    }
    
    public Node parseAsp() {
        Node node = null;
        this.txtstart = this.lexsize;
        int char1;
        while ((char1 = this.in.readChar()) != -1) {
            this.addCharToLexer(char1);
            if (char1 != 37) {
                continue;
            }
            final int char2;
            if ((char2 = this.in.readChar()) == -1) {
                break;
            }
            this.addCharToLexer(char2);
            if (char2 == 62) {
                break;
            }
        }
        this.lexsize -= 2;
        this.txtend = this.lexsize;
        if (this.txtend > this.txtstart) {
            node = this.newNode((short)10, this.lexbuf, this.txtstart, this.txtend);
        }
        this.txtstart = this.txtend;
        return node;
    }
    
    public Node parsePhp() {
        Node node = null;
        this.txtstart = this.lexsize;
        int char1;
        while ((char1 = this.in.readChar()) != -1) {
            this.addCharToLexer(char1);
            if (char1 != 63) {
                continue;
            }
            final int char2;
            if ((char2 = this.in.readChar()) == -1) {
                break;
            }
            this.addCharToLexer(char2);
            if (char2 == 62) {
                break;
            }
        }
        this.lexsize -= 2;
        this.txtend = this.lexsize;
        if (this.txtend > this.txtstart) {
            node = this.newNode((short)12, this.lexbuf, this.txtstart, this.txtend);
        }
        this.txtstart = this.txtend;
        return node;
    }
    
    public String parseAttribute(final boolean[] array, final Node[] array2, final Node[] array3) {
        array3[0] = (array2[0] = null);
        int n;
        while (true) {
            n = this.in.readChar();
            if (n == 47) {
                final int char1 = this.in.readChar();
                if (char1 == 62) {
                    array[0] = true;
                    return null;
                }
                this.in.ungetChar(char1);
                n = 47;
                break;
            }
            else {
                if (n == 62) {
                    return null;
                }
                if (n == 60) {
                    final int char2 = this.in.readChar();
                    if (char2 == 37) {
                        array2[0] = this.parseAsp();
                        return null;
                    }
                    if (char2 == 63) {
                        array3[0] = this.parsePhp();
                        return null;
                    }
                    this.in.ungetChar(char2);
                    if (this.state != 13) {
                        this.in.ungetChar(60);
                    }
                    this.report.attrError(this, this.token, null, (short)52);
                    return null;
                }
                else if (n == 61) {
                    this.report.attrError(this, this.token, null, (short)69);
                }
                else if (n == 34 || n == 39) {
                    this.report.attrError(this, this.token, null, (short)59);
                }
                else {
                    if (n == -1) {
                        this.report.attrError(this, this.token, null, (short)36);
                        this.in.ungetChar(n);
                        return null;
                    }
                    if (!TidyUtils.isWhite((char)n)) {
                        break;
                    }
                    continue;
                }
            }
        }
        final int lexsize = this.lexsize;
        int n2 = n;
        while (true) {
            while (n != 61 && n != 62) {
                if (n == 60 || n == -1) {
                    this.in.ungetChar(n);
                }
                else if (n2 == 45 && (n == 34 || n == 39)) {
                    --this.lexsize;
                    this.in.ungetChar(n);
                }
                else if (!TidyUtils.isWhite((char)n)) {
                    if (!this.configuration.xmlTags && TidyUtils.isUpper((char)n)) {
                        n = TidyUtils.toLower((char)n);
                    }
                    this.addCharToLexer(n);
                    n2 = n;
                    n = this.in.readChar();
                    continue;
                }
                final int n3 = this.lexsize - lexsize;
                final String s = (n3 > 0) ? TidyUtils.getString(this.lexbuf, lexsize, n3) : null;
                this.lexsize = lexsize;
                return s;
            }
            this.in.ungetChar(n);
            continue;
        }
    }
    
    public int parseServerInstruction() {
        int n = 34;
        boolean b = false;
        final int char1 = this.in.readChar();
        this.addCharToLexer(char1);
        if (char1 == 37 || char1 == 63 || char1 == 64) {
            b = true;
        }
        while (true) {
            final int char2 = this.in.readChar();
            if (char2 == -1) {
                break;
            }
            if (char2 == 62) {
                if (b) {
                    this.addCharToLexer(char2);
                    break;
                }
                this.in.ungetChar(char2);
                break;
            }
            else {
                if (!b && TidyUtils.isWhite((char)char2)) {
                    break;
                }
                this.addCharToLexer(char2);
                if (char2 == 34) {
                    int i;
                    do {
                        i = this.in.readChar();
                        if (this.endOfInput()) {
                            this.report.attrError(this, this.token, null, (short)36);
                            this.in.ungetChar(i);
                            return 0;
                        }
                        if (i == 62) {
                            this.in.ungetChar(i);
                            this.report.attrError(this, this.token, null, (short)52);
                            return 0;
                        }
                        this.addCharToLexer(i);
                    } while (i != 34);
                    n = 39;
                }
                else {
                    if (char2 != 39) {
                        continue;
                    }
                    int j;
                    do {
                        j = this.in.readChar();
                        if (this.endOfInput()) {
                            this.report.attrError(this, this.token, null, (short)36);
                            this.in.ungetChar(j);
                            return 0;
                        }
                        if (j == 62) {
                            this.in.ungetChar(j);
                            this.report.attrError(this, this.token, null, (short)52);
                            return 0;
                        }
                        this.addCharToLexer(j);
                    } while (j != 39);
                }
            }
        }
        return n;
    }
    
    public String parseValue(final String s, final boolean b, final boolean[] array, final int[] array2) {
        boolean b2 = false;
        boolean b3 = true;
        int n = 0;
        array2[0] = 34;
        if (this.configuration.literalAttribs) {
            b3 = false;
        }
        int char1;
        do {
            char1 = this.in.readChar();
            if (char1 == -1) {
                this.in.ungetChar(char1);
                break;
            }
        } while (TidyUtils.isWhite((char)char1));
        if (char1 != 61 && char1 != 34 && char1 != 39) {
            this.in.ungetChar(char1);
            return null;
        }
        int char2;
        do {
            char2 = this.in.readChar();
            if (char2 == -1) {
                this.in.ungetChar(char2);
                break;
            }
        } while (TidyUtils.isWhite((char)char2));
        if (char2 == 34 || char2 == 39) {
            n = char2;
        }
        else {
            if (char2 == 60) {
                final int lexsize = this.lexsize;
                this.addCharToLexer(char2);
                array2[0] = this.parseServerInstruction();
                final int n2 = this.lexsize - lexsize;
                this.lexsize = lexsize;
                return (n2 > 0) ? TidyUtils.getString(this.lexbuf, lexsize, n2) : null;
            }
            this.in.ungetChar(char2);
        }
        int n3 = 0;
        int lexsize2 = this.lexsize;
        int n4 = 0;
        while (true) {
            final int n5 = n4;
            n4 = this.in.readChar();
            if (n4 == -1) {
                this.report.attrError(this, this.token, null, (short)36);
                this.in.ungetChar(n4);
                break;
            }
            if (n == 0) {
                if (n4 == 62) {
                    this.in.ungetChar(n4);
                    break;
                }
                if (n4 == 34 || n4 == 39) {
                    final int n6 = n4;
                    this.report.attrError(this, this.token, null, (short)59);
                    final int char3 = this.in.readChar();
                    if (char3 == 62) {
                        this.addCharToLexer(n6);
                        this.in.ungetChar(char3);
                        break;
                    }
                    this.in.ungetChar(char3);
                    n4 = n6;
                }
                if (n4 == 60) {
                    this.in.ungetChar(n4);
                    this.in.ungetChar(62);
                    this.report.attrError(this, this.token, null, (short)52);
                    break;
                }
                if (n4 == 47) {
                    final int char4 = this.in.readChar();
                    if (char4 == 62 && !AttributeTable.getDefaultAttributeTable().isUrl(s)) {
                        array[0] = true;
                        this.in.ungetChar(char4);
                        break;
                    }
                    this.in.ungetChar(char4);
                    n4 = 47;
                }
            }
            else {
                if (n4 == n) {
                    break;
                }
                if (n4 == 13) {
                    final int char5 = this.in.readChar();
                    if (char5 != 10) {
                        this.in.ungetChar(char5);
                    }
                    n4 = 10;
                }
                if (n4 == 10 || n4 == 60 || n4 == 62) {
                    ++n3;
                }
                if (n4 == 62) {
                    b2 = true;
                }
            }
            if (n4 == 38) {
                if ("id".equalsIgnoreCase(s)) {
                    this.report.attrError(this, null, null, (short)67);
                }
                else {
                    this.addCharToLexer(n4);
                    this.parseEntity((short)0);
                }
            }
            else {
                if (n4 == 92) {
                    n4 = this.in.readChar();
                    if (n4 != 10) {
                        this.in.ungetChar(n4);
                        n4 = 92;
                    }
                }
                if (TidyUtils.isWhite((char)n4)) {
                    if (n == 0) {
                        break;
                    }
                    if (b3) {
                        if (n4 == 10 && AttributeTable.getDefaultAttributeTable().isUrl(s)) {
                            this.report.attrError(this, this.token, null, (short)65);
                            continue;
                        }
                        n4 = 32;
                        if (n5 == 32) {
                            continue;
                        }
                    }
                }
                else if (b && TidyUtils.isUpper((char)n4)) {
                    n4 = TidyUtils.toLower((char)n4);
                }
                this.addCharToLexer(n4);
            }
        }
        if (n3 > 10 && b2 && b3 && !AttributeTable.getDefaultAttributeTable().isScript(s) && (!AttributeTable.getDefaultAttributeTable().isUrl(s) || !"javascript:".equals(TidyUtils.getString(this.lexbuf, lexsize2, 11))) && !"<xml ".equals(TidyUtils.getString(this.lexbuf, lexsize2, 5))) {
            this.report.error(this, null, null, (short)16);
        }
        int n7 = this.lexsize - lexsize2;
        this.lexsize = lexsize2;
        String string;
        if (n7 > 0 || n != 0) {
            if (b3 && !TidyUtils.isInValuesIgnoreCase(new String[] { "alt", "title", "value", "prompt" }, s)) {
                while (TidyUtils.isWhite((char)this.lexbuf[lexsize2 + n7 - 1])) {
                    --n7;
                }
                while (TidyUtils.isWhite((char)this.lexbuf[lexsize2]) && lexsize2 < n7) {
                    ++lexsize2;
                    --n7;
                }
            }
            string = TidyUtils.getString(this.lexbuf, lexsize2, n7);
        }
        else {
            string = null;
        }
        if (n != 0) {
            array2[0] = n;
        }
        else {
            array2[0] = 34;
        }
        return string;
    }
    
    public static boolean isValidAttrName(final String s) {
        if (!TidyUtils.isLetter(s.charAt(0))) {
            return false;
        }
        for (int i = 1; i < s.length(); ++i) {
            if (!TidyUtils.isNamechar(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean isCSS1Selector(final String s) {
        if (s == null) {
            return false;
        }
        boolean b = true;
        int n = 0;
        for (int n2 = 0; b && n2 < s.length(); ++n2) {
            final char char1 = s.charAt(n2);
            if (char1 == '\\') {
                n = 1;
            }
            else if (Character.isDigit(char1)) {
                if (n > 0) {
                    b = (++n < 6);
                }
                if (b) {
                    b = (n2 > 0 || n > 0);
                }
            }
            else {
                b = (n > 0 || (n2 > 0 && char1 == '-') || Character.isLetter(char1) || (char1 >= '¡' && char1 <= '\u00ff'));
                n = 0;
            }
        }
        return b;
    }
    
    public AttVal parseAttrs(final boolean[] array) {
        final int[] array2 = { 0 };
        final Node[] array3 = { null };
        final Node[] array4 = { null };
        AttVal attVal = null;
        while (!this.endOfInput()) {
            final String attribute = this.parseAttribute(array, array3, array4);
            if (attribute == null) {
                if (array3[0] != null) {
                    attVal = new AttVal(attVal, null, array3[0], null, 0, null, null);
                }
                else {
                    if (array4[0] == null) {
                        break;
                    }
                    attVal = new AttVal(attVal, null, null, array4[0], 0, null, null);
                }
            }
            else {
                final String value = this.parseValue(attribute, false, array, array2);
                if (attribute != null && isValidAttrName(attribute)) {
                    final AttVal attVal2 = new AttVal(attVal, null, null, null, array2[0], attribute, value);
                    attVal2.dict = AttributeTable.getDefaultAttributeTable().findAttribute(attVal2);
                    attVal = attVal2;
                }
                else {
                    final AttVal attVal3 = new AttVal(null, null, null, null, 0, attribute, value);
                    if (value != null) {
                        this.report.attrError(this, this.token, attVal3, (short)51);
                    }
                    else if (TidyUtils.lastChar(attribute) == 34) {
                        this.report.attrError(this, this.token, attVal3, (short)58);
                    }
                    else {
                        this.report.attrError(this, this.token, attVal3, (short)48);
                    }
                }
            }
        }
        return attVal;
    }
    
    public void pushInline(final Node node) {
        if (node.implicit) {
            return;
        }
        if (node.tag == null) {
            return;
        }
        if (!TidyUtils.toBoolean(node.tag.model & 0x10)) {
            return;
        }
        if (TidyUtils.toBoolean(node.tag.model & 0x800)) {
            return;
        }
        if (node.tag != this.configuration.tt.tagFont && this.isPushed(node)) {
            return;
        }
        final IStack stack = new IStack();
        stack.tag = node.tag;
        stack.element = node.element;
        if (node.attributes != null) {
            stack.attributes = this.cloneAttributes(node.attributes);
        }
        this.istack.push(stack);
    }
    
    public void popInline(final Node node) {
        if (node != null) {
            if (node.tag == null) {
                return;
            }
            if (!TidyUtils.toBoolean(node.tag.model & 0x10)) {
                return;
            }
            if (TidyUtils.toBoolean(node.tag.model & 0x800)) {
                return;
            }
            if (node.tag == this.configuration.tt.tagA) {
                while (this.istack.size() > 0 && this.istack.pop().tag != this.configuration.tt.tagA) {}
                if (this.insert >= this.istack.size()) {
                    this.insert = -1;
                }
                return;
            }
        }
        if (this.istack.size() > 0) {
            final IStack stack = this.istack.pop();
            if (this.insert >= this.istack.size()) {
                this.insert = -1;
            }
        }
    }
    
    public boolean isPushed(final Node node) {
        for (int i = this.istack.size() - 1; i >= 0; --i) {
            if (((IStack)this.istack.elementAt(i)).tag == node.tag) {
                return true;
            }
        }
        return false;
    }
    
    public int inlineDup(final Node inode) {
        final int n = this.istack.size() - this.istackbase;
        if (n > 0) {
            this.insert = this.istackbase;
            this.inode = inode;
        }
        return n;
    }
    
    public Node insertedToken() {
        if (this.insert == -1) {
            final Node inode = this.inode;
            this.inode = null;
            return inode;
        }
        if (this.inode == null) {
            this.lines = this.in.getCurline();
            this.columns = this.in.getCurcol();
        }
        final Node node = this.newNode((short)5, this.lexbuf, this.txtstart, this.txtend);
        node.implicit = true;
        final IStack stack = (IStack)this.istack.elementAt(this.insert);
        node.element = stack.element;
        node.tag = stack.tag;
        if (stack.attributes != null) {
            node.attributes = this.cloneAttributes(stack.attributes);
        }
        int insert = this.insert;
        if (++insert < this.istack.size()) {
            this.insert = insert;
        }
        else {
            this.insert = -1;
        }
        return node;
    }
    
    public boolean canPrune(final Node node) {
        return node.type == 4 || (node.content == null && (node.tag != this.configuration.tt.tagA || node.attributes == null) && (node.tag != this.configuration.tt.tagP || this.configuration.dropEmptyParas) && node.tag != null && !TidyUtils.toBoolean(node.tag.model & 0x200) && !TidyUtils.toBoolean(node.tag.model & 0x1) && node.tag != this.configuration.tt.tagApplet && node.tag != this.configuration.tt.tagObject && (node.tag != this.configuration.tt.tagScript || node.getAttrByName("src") == null) && node.tag != this.configuration.tt.tagTitle && node.tag != this.configuration.tt.tagIframe && node.getAttrByName("id") == null && node.getAttrByName("name") == null);
    }
    
    public void fixId(final Node node) {
        final AttVal attrByName = node.getAttrByName("name");
        final AttVal attrByName2 = node.getAttrByName("id");
        if (attrByName != null) {
            if (attrByName2 != null) {
                if (attrByName2.value != null && !attrByName2.value.equals(attrByName.value)) {
                    this.report.attrError(this, node, attrByName, (short)60);
                }
            }
            else if (this.configuration.xmlOut) {
                node.addAttribute("id", attrByName.value);
            }
        }
    }
    
    public void deferDup() {
        this.insert = -1;
        this.inode = null;
    }
    
    void constrainVersion(final int n) {
        this.versions &= (short)(n | 0x1C0);
    }
    
    protected boolean preContent(final Node node) {
        return node.tag == this.configuration.tt.tagP || (node.tag != null && node.tag != this.configuration.tt.tagP && TidyUtils.toBoolean(node.tag.model & 0x100010));
    }
    
    static {
        W3CVERSION = new W3CVersionInfo[] { new W3CVersionInfo("HTML 4.01", "XHTML 1.0 Strict", "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd", (short)4), new W3CVersionInfo("HTML 4.01 Transitional", "XHTML 1.0 Transitional", "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd", (short)8), new W3CVersionInfo("HTML 4.01 Frameset", "XHTML 1.0 Frameset", "http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd", (short)16), new W3CVersionInfo("HTML 4.0", "XHTML 1.0 Strict", "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd", (short)4), new W3CVersionInfo("HTML 4.0 Transitional", "XHTML 1.0 Transitional", "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd", (short)8), new W3CVersionInfo("HTML 4.0 Frameset", "XHTML 1.0 Frameset", "http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd", (short)16), new W3CVersionInfo("HTML 3.2", "XHTML 1.0 Transitional", "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd", (short)2), new W3CVersionInfo("HTML 3.2 Final", "XHTML 1.0 Transitional", "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd", (short)2), new W3CVersionInfo("HTML 3.2 Draft", "XHTML 1.0 Transitional", "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd", (short)2), new W3CVersionInfo("HTML 2.0", "XHTML 1.0 Strict", "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd", (short)1), new W3CVersionInfo("HTML 4.01", "XHTML 1.1", "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd", (short)1024) };
    }
    
    private static class W3CVersionInfo
    {
        String name;
        String voyagerName;
        String profile;
        short code;
        
        public W3CVersionInfo(final String name, final String voyagerName, final String profile, final short code) {
            this.name = name;
            this.voyagerName = voyagerName;
            this.profile = profile;
            this.code = code;
        }
    }
}
