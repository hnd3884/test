package org.w3c.tidy;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.text.NumberFormat;

public class PPrint
{
    private static final short NORMAL = 0;
    private static final short PREFORMATTED = 1;
    private static final short COMMENT = 2;
    private static final short ATTRIBVALUE = 4;
    private static final short NOWRAP = 8;
    private static final short CDATA = 16;
    private static final String CDATA_START = "<![CDATA[";
    private static final String CDATA_END = "]]>";
    private static final String JS_COMMENT_START = "//";
    private static final String JS_COMMENT_END = "";
    private static final String VB_COMMENT_START = "'";
    private static final String VB_COMMENT_END = "";
    private static final String CSS_COMMENT_START = "/*";
    private static final String CSS_COMMENT_END = "*/";
    private static final String DEFAULT_COMMENT_START = "";
    private static final String DEFAULT_COMMENT_END = "";
    private int[] linebuf;
    private int lbufsize;
    private int linelen;
    private int wraphere;
    private boolean inAttVal;
    private boolean inString;
    private int slide;
    private int count;
    private Node slidecontent;
    private Configuration configuration;
    
    public PPrint(final Configuration configuration) {
        this.configuration = configuration;
    }
    
    int cWrapLen(final int n) {
        if ("zh".equals(this.configuration.language)) {
            return n + (this.configuration.wraplen - n) / 2;
        }
        if ("ja".equals(this.configuration.language)) {
            return n + (this.configuration.wraplen - n) * 7 / 10;
        }
        return this.configuration.wraplen;
    }
    
    public static int getUTF8(final byte[] array, final int n, final int[] array2) {
        final int[] array3 = { 0 };
        final int[] array4 = { 0 };
        if (EncodingUtils.decodeUTF8BytesToChar(array3, TidyUtils.toUnsigned(array[n]), array, null, array4, n + 1)) {
            array3[0] = 65533;
        }
        array2[0] = array3[0];
        return array4[0] - 1;
    }
    
    public static int putUTF8(final byte[] array, int n, final int n2) {
        final int[] array2 = { 0 };
        if (EncodingUtils.encodeCharToUTF8Bytes(n2, array, null, array2)) {
            array[0] = -17;
            array[1] = -65;
            array[2] = -67;
            array2[0] = 3;
        }
        n += array2[0];
        return n;
    }
    
    private void addC(final int n, final int n2) {
        if (n2 + 1 >= this.lbufsize) {
            while (n2 + 1 >= this.lbufsize) {
                if (this.lbufsize == 0) {
                    this.lbufsize = 256;
                }
                else {
                    this.lbufsize *= 2;
                }
            }
            final int[] linebuf = new int[this.lbufsize];
            if (this.linebuf != null) {
                System.arraycopy(this.linebuf, 0, linebuf, 0, n2);
            }
            this.linebuf = linebuf;
        }
        this.linebuf[n2] = n;
    }
    
    private int addAsciiString(final String s, final int n) {
        final int length = s.length();
        if (n + length >= this.lbufsize) {
            while (n + length >= this.lbufsize) {
                if (this.lbufsize == 0) {
                    this.lbufsize = 256;
                }
                else {
                    this.lbufsize *= 2;
                }
            }
            final int[] linebuf = new int[this.lbufsize];
            if (this.linebuf != null) {
                System.arraycopy(this.linebuf, 0, linebuf, 0, n);
            }
            this.linebuf = linebuf;
        }
        for (int i = 0; i < length; ++i) {
            this.linebuf[n + i] = s.charAt(i);
        }
        return n + length;
    }
    
    private void wrapLine(final Out out, final int n) {
        if (this.wraphere == 0) {
            return;
        }
        for (int i = 0; i < n; ++i) {
            out.outc(32);
        }
        for (int j = 0; j < this.wraphere; ++j) {
            out.outc(this.linebuf[j]);
        }
        if (this.inString) {
            out.outc(32);
            out.outc(92);
        }
        out.newline();
        if (this.linelen > this.wraphere) {
            int n2 = 0;
            if (this.linebuf[this.wraphere] == 32) {
                ++this.wraphere;
            }
            int wraphere = this.wraphere;
            this.addC(0, this.linelen);
            while (true) {
                this.linebuf[n2] = this.linebuf[wraphere];
                if (this.linebuf[wraphere] == 0) {
                    break;
                }
                ++n2;
                ++wraphere;
            }
            this.linelen -= this.wraphere;
        }
        else {
            this.linelen = 0;
        }
        this.wraphere = 0;
    }
    
    private void wrapAttrVal(final Out out, final int n, final boolean b) {
        for (int i = 0; i < n; ++i) {
            out.outc(32);
        }
        for (int j = 0; j < this.wraphere; ++j) {
            out.outc(this.linebuf[j]);
        }
        out.outc(32);
        if (b) {
            out.outc(92);
        }
        out.newline();
        if (this.linelen > this.wraphere) {
            int n2 = 0;
            if (this.linebuf[this.wraphere] == 32) {
                ++this.wraphere;
            }
            int wraphere = this.wraphere;
            this.addC(0, this.linelen);
            while (true) {
                this.linebuf[n2] = this.linebuf[wraphere];
                if (this.linebuf[wraphere] == 0) {
                    break;
                }
                ++n2;
                ++wraphere;
            }
            this.linelen -= this.wraphere;
        }
        else {
            this.linelen = 0;
        }
        this.wraphere = 0;
    }
    
    public void flushLine(final Out out, final int n) {
        if (this.linelen > 0) {
            if (n + this.linelen >= this.configuration.wraplen) {
                this.wrapLine(out, n);
            }
            if (!this.inAttVal || this.configuration.indentAttributes) {
                for (int i = 0; i < n; ++i) {
                    out.outc(32);
                }
            }
            for (int j = 0; j < this.linelen; ++j) {
                out.outc(this.linebuf[j]);
            }
        }
        out.newline();
        this.linelen = 0;
        this.wraphere = 0;
        this.inAttVal = false;
    }
    
    public void condFlushLine(final Out out, final int n) {
        if (this.linelen > 0) {
            if (n + this.linelen >= this.configuration.wraplen) {
                this.wrapLine(out, n);
            }
            if (!this.inAttVal || this.configuration.indentAttributes) {
                for (int i = 0; i < n; ++i) {
                    out.outc(32);
                }
            }
            for (int j = 0; j < this.linelen; ++j) {
                out.outc(this.linebuf[j]);
            }
            out.newline();
            this.linelen = 0;
            this.wraphere = 0;
            this.inAttVal = false;
        }
    }
    
    private void printChar(int n, final short n2) {
        boolean b = false;
        if (n == 32 && !TidyUtils.toBoolean(n2 & 0x17)) {
            if (TidyUtils.toBoolean(n2 & 0x8)) {
                if (this.configuration.numEntities || this.configuration.xmlTags) {
                    this.addC(38, this.linelen++);
                    this.addC(35, this.linelen++);
                    this.addC(49, this.linelen++);
                    this.addC(54, this.linelen++);
                    this.addC(48, this.linelen++);
                    this.addC(59, this.linelen++);
                }
                else {
                    this.addC(38, this.linelen++);
                    this.addC(110, this.linelen++);
                    this.addC(98, this.linelen++);
                    this.addC(115, this.linelen++);
                    this.addC(112, this.linelen++);
                    this.addC(59, this.linelen++);
                }
                return;
            }
            this.wraphere = this.linelen;
        }
        if (TidyUtils.toBoolean(n2 & 0x12)) {
            this.addC(n, this.linelen++);
            return;
        }
        if (!TidyUtils.toBoolean(n2 & 0x10)) {
            if (n == 60) {
                this.addC(38, this.linelen++);
                this.addC(108, this.linelen++);
                this.addC(116, this.linelen++);
                this.addC(59, this.linelen++);
                return;
            }
            if (n == 62) {
                this.addC(38, this.linelen++);
                this.addC(103, this.linelen++);
                this.addC(116, this.linelen++);
                this.addC(59, this.linelen++);
                return;
            }
            if (n == 38 && this.configuration.quoteAmpersand) {
                this.addC(38, this.linelen++);
                this.addC(97, this.linelen++);
                this.addC(109, this.linelen++);
                this.addC(112, this.linelen++);
                this.addC(59, this.linelen++);
                return;
            }
            if (n == 34 && this.configuration.quoteMarks) {
                this.addC(38, this.linelen++);
                this.addC(113, this.linelen++);
                this.addC(117, this.linelen++);
                this.addC(111, this.linelen++);
                this.addC(116, this.linelen++);
                this.addC(59, this.linelen++);
                return;
            }
            if (n == 39 && this.configuration.quoteMarks) {
                this.addC(38, this.linelen++);
                this.addC(35, this.linelen++);
                this.addC(51, this.linelen++);
                this.addC(57, this.linelen++);
                this.addC(59, this.linelen++);
                return;
            }
            if (n == 160 && !this.configuration.rawOut) {
                if (this.configuration.makeBare) {
                    this.addC(32, this.linelen++);
                }
                else if (this.configuration.quoteNbsp) {
                    this.addC(38, this.linelen++);
                    if (this.configuration.numEntities || this.configuration.xmlTags) {
                        this.addC(35, this.linelen++);
                        this.addC(49, this.linelen++);
                        this.addC(54, this.linelen++);
                        this.addC(48, this.linelen++);
                    }
                    else {
                        this.addC(110, this.linelen++);
                        this.addC(98, this.linelen++);
                        this.addC(115, this.linelen++);
                        this.addC(112, this.linelen++);
                    }
                    this.addC(59, this.linelen++);
                }
                else {
                    this.addC(n, this.linelen++);
                }
                return;
            }
        }
        if ("UTF8".equals(this.configuration.getOutCharEncodingName())) {
            if (n >= 8192 && !TidyUtils.toBoolean(n2 & 0x1)) {
                if ((n >= 8192 && n <= 8198) || (n >= 8200 && n <= 8208) || (n >= 8209 && n <= 8262) || (n >= 8317 && n <= 8318) || (n >= 8333 && n <= 8334) || (n >= 9001 && n <= 9002) || (n >= 12289 && n <= 12291) || (n >= 12296 && n <= 12305) || (n >= 12308 && n <= 12319) || (n >= 64830 && n <= 64831) || (n >= 65072 && n <= 65092) || (n >= 65097 && n <= 65106) || (n >= 65108 && n <= 65121) || (n >= 65130 && n <= 65131) || (n >= 65281 && n <= 65283) || (n >= 65285 && n <= 65290) || (n >= 65292 && n <= 65295) || (n >= 65306 && n <= 65307) || (n >= 65311 && n <= 65312) || (n >= 65339 && n <= 65341) || (n >= 65377 && n <= 65381)) {
                    this.wraphere = this.linelen + 2;
                    b = true;
                }
                else {
                    switch (n) {
                        case 12336:
                        case 12539:
                        case 65123:
                        case 65128:
                        case 65343:
                        case 65371:
                        case 65373: {
                            this.wraphere = this.linelen + 2;
                            b = true;
                            break;
                        }
                    }
                }
                if (b) {
                    if ((n >= 8218 && n <= 8220) || (n >= 8222 && n <= 8223)) {
                        --this.wraphere;
                    }
                    else {
                        switch (n) {
                            case 8216:
                            case 8249:
                            case 8261:
                            case 8317:
                            case 8333:
                            case 9001:
                            case 12296:
                            case 12298:
                            case 12300:
                            case 12302:
                            case 12304:
                            case 12308:
                            case 12310:
                            case 12312:
                            case 12314:
                            case 12317:
                            case 64830:
                            case 65077:
                            case 65079:
                            case 65081:
                            case 65083:
                            case 65085:
                            case 65087:
                            case 65089:
                            case 65091:
                            case 65113:
                            case 65115:
                            case 65117:
                            case 65288:
                            case 65339:
                            case 65371:
                            case 65378: {
                                --this.wraphere;
                                break;
                            }
                        }
                    }
                }
            }
            else {
                if ("BIG5".equals(this.configuration.getOutCharEncodingName())) {
                    this.addC(n, this.linelen++);
                    if ((n & 0xFF00) == 0xA100 && !TidyUtils.toBoolean(n2 & 0x1)) {
                        this.wraphere = this.linelen;
                        if (n > 92 && n < 173 && (n & 0x1) == 0x1) {
                            --this.wraphere;
                        }
                    }
                    return;
                }
                if ("SHIFTJIS".equals(this.configuration.getOutCharEncodingName()) || "ISO2022".equals(this.configuration.getOutCharEncodingName())) {
                    this.addC(n, this.linelen++);
                    return;
                }
                if (this.configuration.rawOut) {
                    this.addC(n, this.linelen++);
                    return;
                }
            }
        }
        if (n == 160 && TidyUtils.toBoolean(n2 & 0x1)) {
            this.addC(32, this.linelen++);
            return;
        }
        if (((this.configuration.makeClean && this.configuration.asciiChars) || this.configuration.makeBare) && n >= 8211 && n <= 8222) {
            switch (n) {
                case 8211:
                case 8212: {
                    n = 45;
                    break;
                }
                case 8216:
                case 8217:
                case 8218: {
                    n = 39;
                    break;
                }
                case 8220:
                case 8221:
                case 8222: {
                    n = 34;
                    break;
                }
            }
        }
        if ("ISO8859_1".equals(this.configuration.getOutCharEncodingName())) {
            if (n > 255) {
                String s;
                if (!this.configuration.numEntities) {
                    final String entityName = EntityTable.getDefaultEntityTable().entityName((short)n);
                    if (entityName != null) {
                        s = "&" + entityName + ";";
                    }
                    else {
                        s = "&#" + n + ";";
                    }
                }
                else {
                    s = "&#" + n + ";";
                }
                for (int i = 0; i < s.length(); ++i) {
                    this.addC(s.charAt(i), this.linelen++);
                }
                return;
            }
            if (n > 126 && n < 160) {
                final String string = "&#" + n + ";";
                for (int j = 0; j < string.length(); ++j) {
                    this.addC(string.charAt(j), this.linelen++);
                }
                return;
            }
            this.addC(n, this.linelen++);
        }
        else {
            if (this.configuration.getOutCharEncodingName().startsWith("UTF")) {
                this.addC(n, this.linelen++);
                return;
            }
            if (this.configuration.xmlTags) {
                if (n > 127 && "ASCII".equals(this.configuration.getOutCharEncodingName())) {
                    final String string2 = "&#" + n + ";";
                    for (int k = 0; k < string2.length(); ++k) {
                        this.addC(string2.charAt(k), this.linelen++);
                    }
                    return;
                }
                this.addC(n, this.linelen++);
            }
            else {
                if ("ASCII".equals(this.configuration.getOutCharEncodingName()) && (n > 126 || (n < 32 && n != 9))) {
                    String s2;
                    if (!this.configuration.numEntities) {
                        final String entityName2 = EntityTable.getDefaultEntityTable().entityName((short)n);
                        if (entityName2 != null) {
                            s2 = "&" + entityName2 + ";";
                        }
                        else {
                            s2 = "&#" + n + ";";
                        }
                    }
                    else {
                        s2 = "&#" + n + ";";
                    }
                    for (int l = 0; l < s2.length(); ++l) {
                        this.addC(s2.charAt(l), this.linelen++);
                    }
                    return;
                }
                this.addC(n, this.linelen++);
            }
        }
    }
    
    private void printText(final Out out, final short n, final int n2, final byte[] array, final int n3, final int n4) {
        final int[] array2 = { 0 };
        for (int i = n3; i < n4; ++i) {
            if (n2 + this.linelen >= this.configuration.wraplen) {
                this.wrapLine(out, n2);
            }
            int n5 = array[i] & 0xFF;
            if (n5 > 127) {
                i += getUTF8(array, i, array2);
                n5 = array2[0];
            }
            if (n5 == 10) {
                this.flushLine(out, n2);
            }
            else {
                this.printChar(n5, n);
            }
        }
    }
    
    private void printString(final String s) {
        for (int i = 0; i < s.length(); ++i) {
            this.addC(s.charAt(i), this.linelen++);
        }
    }
    
    private void printAttrValue(final Out out, final int n, final String s, int n2, final boolean b) {
        final int[] array = { 0 };
        boolean inString = false;
        byte[] bytes = null;
        short n3 = (short)(b ? 4 : 5);
        if (s != null) {
            bytes = TidyUtils.getBytes(s);
        }
        if (bytes != null && bytes.length >= 5 && bytes[0] == 60 && (bytes[1] == 37 || bytes[1] == 64 || new String(bytes, 0, 5).equals("<?php"))) {
            n3 |= 0x10;
        }
        if (n2 == 0) {
            n2 = 34;
        }
        this.addC(61, this.linelen++);
        if (!this.configuration.xmlOut) {
            if (n + this.linelen < this.configuration.wraplen) {
                this.wraphere = this.linelen;
            }
            if (n + this.linelen >= this.configuration.wraplen) {
                this.wrapLine(out, n);
            }
            if (n + this.linelen < this.configuration.wraplen) {
                this.wraphere = this.linelen;
            }
            else {
                this.condFlushLine(out, n);
            }
        }
        this.addC(n2, this.linelen++);
        if (s != null) {
            this.inString = false;
            int i = 0;
            while (i < bytes.length) {
                int n4 = bytes[i] & 0xFF;
                if (b && n4 == 32 && n + this.linelen < this.configuration.wraplen) {
                    this.wraphere = this.linelen;
                    inString = this.inString;
                }
                if (b && this.wraphere > 0 && n + this.linelen >= this.configuration.wraplen) {
                    this.wrapAttrVal(out, n, inString);
                }
                if (n4 == n2) {
                    final String s2 = (n4 == 34) ? "&quot;" : "&#39;";
                    for (int j = 0; j < s2.length(); ++j) {
                        this.addC(s2.charAt(j), this.linelen++);
                    }
                    ++i;
                }
                else if (n4 == 34) {
                    if (this.configuration.quoteMarks) {
                        this.addC(38, this.linelen++);
                        this.addC(113, this.linelen++);
                        this.addC(117, this.linelen++);
                        this.addC(111, this.linelen++);
                        this.addC(116, this.linelen++);
                        this.addC(59, this.linelen++);
                    }
                    else {
                        this.addC(34, this.linelen++);
                    }
                    if (n2 == 39) {
                        this.inString = !this.inString;
                    }
                    ++i;
                }
                else if (n4 == 39) {
                    if (this.configuration.quoteMarks) {
                        this.addC(38, this.linelen++);
                        this.addC(35, this.linelen++);
                        this.addC(51, this.linelen++);
                        this.addC(57, this.linelen++);
                        this.addC(59, this.linelen++);
                    }
                    else {
                        this.addC(39, this.linelen++);
                    }
                    if (n2 == 34) {
                        this.inString = !this.inString;
                    }
                    ++i;
                }
                else {
                    if (n4 > 127) {
                        i += getUTF8(bytes, i, array);
                        n4 = array[0];
                    }
                    ++i;
                    if (n4 == 10) {
                        this.flushLine(out, n);
                    }
                    else {
                        this.printChar(n4, n3);
                    }
                }
            }
        }
        this.inString = false;
        this.addC(n2, this.linelen++);
    }
    
    private void printAttribute(final Out out, int n, final Node node, final AttVal attVal) {
        boolean wrapScriptlets = false;
        if (this.configuration.indentAttributes) {
            this.flushLine(out, n);
            n += this.configuration.spaces;
        }
        final String attribute = attVal.attribute;
        if (n + this.linelen >= this.configuration.wraplen) {
            this.wrapLine(out, n);
        }
        if (!this.configuration.xmlTags && !this.configuration.xmlOut && attVal.dict != null) {
            if (AttributeTable.getDefaultAttributeTable().isScript(attribute)) {
                wrapScriptlets = this.configuration.wrapScriptlets;
            }
            else if (!attVal.dict.isNowrap() && this.configuration.wrapAttVals) {
                wrapScriptlets = true;
            }
        }
        if (n + this.linelen < this.configuration.wraplen) {
            this.wraphere = this.linelen;
            this.addC(32, this.linelen++);
        }
        else {
            this.condFlushLine(out, n);
            this.addC(32, this.linelen++);
        }
        for (int i = 0; i < attribute.length(); ++i) {
            this.addC(TidyUtils.foldCase(attribute.charAt(i), this.configuration.upperCaseAttrs, this.configuration.xmlTags), this.linelen++);
        }
        if (n + this.linelen >= this.configuration.wraplen) {
            this.wrapLine(out, n);
        }
        if (attVal.value == null) {
            if (this.configuration.xmlTags || this.configuration.xmlOut) {
                this.printAttrValue(out, n, attVal.isBoolAttribute() ? attVal.attribute : "", attVal.delim, true);
            }
            else if (!attVal.isBoolAttribute() && node != null && !node.isNewNode()) {
                this.printAttrValue(out, n, "", attVal.delim, true);
            }
            else if (n + this.linelen < this.configuration.wraplen) {
                this.wraphere = this.linelen;
            }
        }
        else {
            this.printAttrValue(out, n, attVal.value, attVal.delim, wrapScriptlets);
        }
    }
    
    private void printAttrs(final Out out, final int n, final Node node, AttVal attributes) {
        if (this.configuration.xmlOut && this.configuration.xmlSpace && ParserImpl.XMLPreserveWhiteSpace(node, this.configuration.tt) && node.getAttrByName("xml:space") == null) {
            node.addAttribute("xml:space", "preserve");
            if (attributes != null) {
                attributes = node.attributes;
            }
        }
        if (attributes != null) {
            if (attributes.next != null) {
                this.printAttrs(out, n, node, attributes.next);
            }
            if (attributes.attribute != null) {
                final Attribute dict = attributes.dict;
                if (!this.configuration.dropProprietaryAttributes || (dict != null && !TidyUtils.toBoolean(dict.getVersions() & 0x1C0))) {
                    this.printAttribute(out, n, node, attributes);
                }
            }
            else if (attributes.asp != null) {
                this.addC(32, this.linelen++);
                this.printAsp(out, n, attributes.asp);
            }
            else if (attributes.php != null) {
                this.addC(32, this.linelen++);
                this.printPhp(out, n, attributes.php);
            }
        }
    }
    
    private static boolean afterSpace(final Node node) {
        if (node == null || node.tag == null || !TidyUtils.toBoolean(node.tag.model & 0x10)) {
            return true;
        }
        final Node prev = node.prev;
        if (prev != null) {
            if (prev.type == 4 && prev.end > prev.start) {
                final int n = prev.textarray[prev.end - 1] & 0xFF;
                if (n == 160 || n == 32 || n == 10) {
                    return true;
                }
            }
            return false;
        }
        return afterSpace(node.parent);
    }
    
    private void printTag(final Lexer lexer, final Out out, final short n, final int n2, final Node node) {
        final TagTable tt = this.configuration.tt;
        this.addC(60, this.linelen++);
        if (node.type == 6) {
            this.addC(47, this.linelen++);
        }
        final String element = node.element;
        for (int i = 0; i < element.length(); ++i) {
            this.addC(TidyUtils.foldCase(element.charAt(i), this.configuration.upperCaseTags, this.configuration.xmlTags), this.linelen++);
        }
        this.printAttrs(out, n2, node, node.attributes);
        if ((this.configuration.xmlOut || this.configuration.xHTML) && (node.type == 7 || TidyUtils.toBoolean(node.tag.model & 0x1))) {
            this.addC(32, this.linelen++);
            this.addC(47, this.linelen++);
        }
        this.addC(62, this.linelen++);
        if ((node.type != 7 || this.configuration.xHTML) && !TidyUtils.toBoolean(n & 0x1)) {
            if (n2 + this.linelen >= this.configuration.wraplen) {
                this.wrapLine(out, n2);
            }
            if (n2 + this.linelen < this.configuration.wraplen && !TidyUtils.toBoolean(n & 0x8) && (!TidyUtils.toBoolean(node.tag.model & 0x10) || node.tag == tt.tagBr) && afterSpace(node)) {
                this.wraphere = this.linelen;
            }
        }
        else {
            this.condFlushLine(out, n2);
        }
    }
    
    private void printEndTag(final short n, final int n2, final Node node) {
        this.addC(60, this.linelen++);
        this.addC(47, this.linelen++);
        final String element = node.element;
        for (int i = 0; i < element.length(); ++i) {
            this.addC(TidyUtils.foldCase(element.charAt(i), this.configuration.upperCaseTags, this.configuration.xmlTags), this.linelen++);
        }
        this.addC(62, this.linelen++);
    }
    
    private void printComment(final Out out, final int n, final Node node) {
        if (this.configuration.hideComments) {
            return;
        }
        if (n + this.linelen < this.configuration.wraplen) {
            this.wraphere = this.linelen;
        }
        this.addC(60, this.linelen++);
        this.addC(33, this.linelen++);
        this.addC(45, this.linelen++);
        this.addC(45, this.linelen++);
        this.printText(out, (short)2, n, node.textarray, node.start, node.end);
        this.addC(45, this.linelen++);
        this.addC(45, this.linelen++);
        this.addC(62, this.linelen++);
        if (node.linebreak) {
            this.flushLine(out, n);
        }
    }
    
    private void printDocType(final Out out, final int n, final Lexer lexer, final Node node) {
        short n2 = 0;
        final boolean quoteMarks = this.configuration.quoteMarks;
        this.configuration.quoteMarks = false;
        if (n + this.linelen < this.configuration.wraplen) {
            this.wraphere = this.linelen;
        }
        this.condFlushLine(out, n);
        this.addC(60, this.linelen++);
        this.addC(33, this.linelen++);
        this.addC(68, this.linelen++);
        this.addC(79, this.linelen++);
        this.addC(67, this.linelen++);
        this.addC(84, this.linelen++);
        this.addC(89, this.linelen++);
        this.addC(80, this.linelen++);
        this.addC(69, this.linelen++);
        this.addC(32, this.linelen++);
        if (n + this.linelen < this.configuration.wraplen) {
            this.wraphere = this.linelen;
        }
        for (int i = node.start; i < node.end; ++i) {
            if (n + this.linelen >= this.configuration.wraplen) {
                this.wrapLine(out, n);
            }
            int n3 = node.textarray[i] & 0xFF;
            if (TidyUtils.toBoolean(n2 & 0x10)) {
                if (n3 == 93) {
                    n2 &= 0xFFFFFFEF;
                }
            }
            else if (n3 == 91) {
                n2 |= 0x10;
            }
            final int[] array = { 0 };
            if (n3 > 127) {
                i += getUTF8(node.textarray, i, array);
                n3 = array[0];
            }
            if (n3 == 10) {
                this.flushLine(out, n);
            }
            else {
                this.printChar(n3, n2);
            }
        }
        if (this.linelen < this.configuration.wraplen) {
            this.wraphere = this.linelen;
        }
        this.addC(62, this.linelen++);
        this.configuration.quoteMarks = quoteMarks;
        this.condFlushLine(out, n);
    }
    
    private void printPI(final Out out, final int n, final Node node) {
        if (n + this.linelen < this.configuration.wraplen) {
            this.wraphere = this.linelen;
        }
        this.addC(60, this.linelen++);
        this.addC(63, this.linelen++);
        this.printText(out, (short)16, n, node.textarray, node.start, node.end);
        if (node.end <= 0 || node.textarray[node.end - 1] != 63) {
            this.addC(63, this.linelen++);
        }
        this.addC(62, this.linelen++);
        this.condFlushLine(out, n);
    }
    
    private void printXmlDecl(final Out out, final int n, final Node node) {
        if (n + this.linelen < this.configuration.wraplen) {
            this.wraphere = this.linelen;
        }
        this.addC(60, this.linelen++);
        this.addC(63, this.linelen++);
        this.addC(120, this.linelen++);
        this.addC(109, this.linelen++);
        this.addC(108, this.linelen++);
        this.printAttrs(out, n, node, node.attributes);
        if (node.end <= 0 || node.textarray[node.end - 1] != 63) {
            this.addC(63, this.linelen++);
        }
        this.addC(62, this.linelen++);
        this.condFlushLine(out, n);
    }
    
    private void printAsp(final Out out, final int n, final Node node) {
        final int wraplen = this.configuration.wraplen;
        if (!this.configuration.wrapAsp || !this.configuration.wrapJste) {
            this.configuration.wraplen = 16777215;
        }
        this.addC(60, this.linelen++);
        this.addC(37, this.linelen++);
        this.printText(out, (short)(this.configuration.wrapAsp ? 16 : 2), n, node.textarray, node.start, node.end);
        this.addC(37, this.linelen++);
        this.addC(62, this.linelen++);
        this.configuration.wraplen = wraplen;
    }
    
    private void printJste(final Out out, final int n, final Node node) {
        final int wraplen = this.configuration.wraplen;
        if (!this.configuration.wrapJste) {
            this.configuration.wraplen = 16777215;
        }
        this.addC(60, this.linelen++);
        this.addC(35, this.linelen++);
        this.printText(out, (short)(this.configuration.wrapJste ? 16 : 2), n, node.textarray, node.start, node.end);
        this.addC(35, this.linelen++);
        this.addC(62, this.linelen++);
        this.configuration.wraplen = wraplen;
    }
    
    private void printPhp(final Out out, final int n, final Node node) {
        final int wraplen = this.configuration.wraplen;
        if (!this.configuration.wrapPhp) {
            this.configuration.wraplen = 16777215;
        }
        this.addC(60, this.linelen++);
        this.addC(63, this.linelen++);
        this.printText(out, (short)(this.configuration.wrapPhp ? 16 : 2), n, node.textarray, node.start, node.end);
        this.addC(63, this.linelen++);
        this.addC(62, this.linelen++);
        this.configuration.wraplen = wraplen;
    }
    
    private void printCDATA(final Out out, int n, final Node node) {
        final int wraplen = this.configuration.wraplen;
        if (!this.configuration.indentCdata) {
            n = 0;
        }
        this.condFlushLine(out, n);
        this.configuration.wraplen = 16777215;
        this.addC(60, this.linelen++);
        this.addC(33, this.linelen++);
        this.addC(91, this.linelen++);
        this.addC(67, this.linelen++);
        this.addC(68, this.linelen++);
        this.addC(65, this.linelen++);
        this.addC(84, this.linelen++);
        this.addC(65, this.linelen++);
        this.addC(91, this.linelen++);
        this.printText(out, (short)2, n, node.textarray, node.start, node.end);
        this.addC(93, this.linelen++);
        this.addC(93, this.linelen++);
        this.addC(62, this.linelen++);
        this.condFlushLine(out, n);
        this.configuration.wraplen = wraplen;
    }
    
    private void printSection(final Out out, final int n, final Node node) {
        final int wraplen = this.configuration.wraplen;
        if (!this.configuration.wrapSection) {
            this.configuration.wraplen = 16777215;
        }
        this.addC(60, this.linelen++);
        this.addC(33, this.linelen++);
        this.addC(91, this.linelen++);
        this.printText(out, (short)(this.configuration.wrapSection ? 16 : 2), n, node.textarray, node.start, node.end);
        this.addC(93, this.linelen++);
        this.addC(62, this.linelen++);
        this.configuration.wraplen = wraplen;
    }
    
    private boolean insideHead(final Node node) {
        return node.tag == this.configuration.tt.tagHead || (node.parent != null && this.insideHead(node.parent));
    }
    
    private int textEndsWithNewline(final Lexer lexer, final Node node) {
        if (node.type == 4 && node.end > node.start) {
            int n;
            int n2;
            for (n = node.end - 1; n >= node.start && TidyUtils.toBoolean(n2 = (node.textarray[n] & 0xFF)) && (n2 == 32 || n2 == 9 || n2 == 13); --n) {}
            if (n >= 0 && node.textarray[n] == 10) {
                return node.end - n - 1;
            }
        }
        return -1;
    }
    
    static boolean hasCDATA(final Lexer lexer, final Node node) {
        if (node.type != 4) {
            return false;
        }
        final int n = node.end - node.start + 1;
        final int index = TidyUtils.getString(node.textarray, node.start, n).indexOf("<![CDATA[");
        return index > -1 && index <= n;
    }
    
    private void printScriptStyle(final Out out, final short n, int n2, final Lexer lexer, final Node node) {
        String s = "";
        String s2 = "";
        boolean hasCDATA = false;
        int textEndsWithNewline = -1;
        if (this.insideHead(node)) {}
        n2 = 0;
        this.printTag(lexer, out, n, n2, node);
        if (lexer.configuration.xHTML && node.content != null) {
            final AttVal attrByName = node.getAttrByName("type");
            if (attrByName != null) {
                if ("text/javascript".equalsIgnoreCase(attrByName.value)) {
                    s = "//";
                    s2 = "";
                }
                else if ("text/css".equalsIgnoreCase(attrByName.value)) {
                    s = "/*";
                    s2 = "*/";
                }
                else if ("text/vbscript".equalsIgnoreCase(attrByName.value)) {
                    s = "'";
                    s2 = "";
                }
            }
            hasCDATA = hasCDATA(lexer, node.content);
            if (!hasCDATA) {
                final int wraplen = lexer.configuration.wraplen;
                lexer.configuration.wraplen = 16777215;
                this.linelen = this.addAsciiString(s, this.linelen);
                this.linelen = this.addAsciiString("<![CDATA[", this.linelen);
                this.linelen = this.addAsciiString(s2, this.linelen);
                this.condFlushLine(out, n2);
                lexer.configuration.wraplen = wraplen;
            }
        }
        for (Node node2 = node.content; node2 != null; node2 = node2.next) {
            this.printTree(out, (short)(n | 0x1 | 0x8 | 0x10), 0, lexer, node2);
            if (node2.next == null) {
                textEndsWithNewline = this.textEndsWithNewline(lexer, node2);
            }
        }
        if (textEndsWithNewline < 0) {
            this.condFlushLine(out, n2);
            textEndsWithNewline = 0;
        }
        if (lexer.configuration.xHTML && node.content != null && !hasCDATA) {
            final int wraplen2 = lexer.configuration.wraplen;
            lexer.configuration.wraplen = 16777215;
            if (textEndsWithNewline > 0 && this.linelen < textEndsWithNewline) {
                this.linelen = textEndsWithNewline;
            }
            for (int n3 = 0; textEndsWithNewline < n2 && n3 < n2 - textEndsWithNewline; ++n3) {
                this.addC(32, this.linelen++);
            }
            this.linelen = this.addAsciiString(s, this.linelen);
            this.linelen = this.addAsciiString("]]>", this.linelen);
            this.linelen = this.addAsciiString(s2, this.linelen);
            lexer.configuration.wraplen = wraplen2;
            this.condFlushLine(out, 0);
        }
        this.printEndTag(n, n2, node);
        if (!lexer.configuration.indentContent && node.next != null && (node.tag == null || !TidyUtils.toBoolean(node.tag.model & 0x10)) && node.type == 4) {
            this.flushLine(out, n2);
        }
        this.flushLine(out, n2);
    }
    
    private boolean shouldIndent(Node node) {
        final TagTable tt = this.configuration.tt;
        if (!this.configuration.indentContent) {
            return false;
        }
        if (this.configuration.smartIndent) {
            if (node.content != null && TidyUtils.toBoolean(node.tag.model & 0x40000)) {
                for (node = node.content; node != null; node = node.next) {
                    if (node.tag != null && TidyUtils.toBoolean(node.tag.model & 0x8)) {
                        return true;
                    }
                }
                return false;
            }
            if (TidyUtils.toBoolean(node.tag.model & 0x4000)) {
                return false;
            }
            if (node.tag == tt.tagP) {
                return false;
            }
            if (node.tag == tt.tagTitle) {
                return false;
            }
        }
        return TidyUtils.toBoolean(node.tag.model & 0xC00) || node.tag == tt.tagMap || !TidyUtils.toBoolean(node.tag.model & 0x10);
    }
    
    void printBody(final Out out, final Lexer lexer, final Node node, final boolean b) {
        if (node == null) {
            return;
        }
        final Node body = node.findBody(lexer.configuration.tt);
        if (body != null) {
            for (Node node2 = body.content; node2 != null; node2 = node2.next) {
                if (b) {
                    this.printXMLTree(out, (short)0, 0, lexer, node2);
                }
                else {
                    this.printTree(out, (short)0, 0, lexer, node2);
                }
            }
        }
    }
    
    public void printTree(final Out out, final short n, int n2, final Lexer lexer, final Node node) {
        final TagTable tt = this.configuration.tt;
        if (node == null) {
            return;
        }
        if (node.type == 4 || (node.type == 8 && lexer.configuration.escapeCdata)) {
            this.printText(out, n, n2, node.textarray, node.start, node.end);
        }
        else if (node.type == 2) {
            this.printComment(out, n2, node);
        }
        else if (node.type == 0) {
            for (Node node2 = node.content; node2 != null; node2 = node2.next) {
                this.printTree(out, n, n2, lexer, node2);
            }
        }
        else if (node.type == 1) {
            this.printDocType(out, n2, lexer, node);
        }
        else if (node.type == 3) {
            this.printPI(out, n2, node);
        }
        else if (node.type == 13) {
            this.printXmlDecl(out, n2, node);
        }
        else if (node.type == 8) {
            this.printCDATA(out, n2, node);
        }
        else if (node.type == 9) {
            this.printSection(out, n2, node);
        }
        else if (node.type == 10) {
            this.printAsp(out, n2, node);
        }
        else if (node.type == 11) {
            this.printJste(out, n2, node);
        }
        else if (node.type == 12) {
            this.printPhp(out, n2, node);
        }
        else if (TidyUtils.toBoolean(node.tag.model & 0x1) || (node.type == 7 && !this.configuration.xHTML)) {
            if (!TidyUtils.toBoolean(node.tag.model & 0x10)) {
                this.condFlushLine(out, n2);
            }
            if (node.tag == tt.tagBr && node.prev != null && node.prev.tag != tt.tagBr && this.configuration.breakBeforeBR) {
                this.flushLine(out, n2);
            }
            if (this.configuration.makeClean && node.tag == tt.tagWbr) {
                this.printString(" ");
            }
            else {
                this.printTag(lexer, out, n, n2, node);
            }
            if (node.tag == tt.tagParam || node.tag == tt.tagArea) {
                this.condFlushLine(out, n2);
            }
            else if (node.tag == tt.tagBr || node.tag == tt.tagHr) {
                this.flushLine(out, n2);
            }
        }
        else {
            if (node.type == 7) {
                node.type = 5;
            }
            if (node.tag != null && node.tag.getParser() == ParserImpl.PRE) {
                this.condFlushLine(out, n2);
                n2 = 0;
                this.condFlushLine(out, n2);
                this.printTag(lexer, out, n, n2, node);
                this.flushLine(out, n2);
                for (Node node3 = node.content; node3 != null; node3 = node3.next) {
                    this.printTree(out, (short)(n | 0x1 | 0x8), n2, lexer, node3);
                }
                this.condFlushLine(out, n2);
                this.printEndTag(n, n2, node);
                this.flushLine(out, n2);
                if (!this.configuration.indentContent && node.next != null) {
                    this.flushLine(out, n2);
                }
            }
            else if (node.tag == tt.tagStyle || node.tag == tt.tagScript) {
                this.printScriptStyle(out, (short)(n | 0x1 | 0x8 | 0x10), n2, lexer, node);
            }
            else if (TidyUtils.toBoolean(node.tag.model & 0x10)) {
                if (this.configuration.makeClean) {
                    if (node.tag == tt.tagFont) {
                        for (Node node4 = node.content; node4 != null; node4 = node4.next) {
                            this.printTree(out, n, n2, lexer, node4);
                        }
                        return;
                    }
                    if (node.tag == tt.tagNobr) {
                        for (Node node5 = node.content; node5 != null; node5 = node5.next) {
                            this.printTree(out, (short)(n | 0x8), n2, lexer, node5);
                        }
                        return;
                    }
                }
                this.printTag(lexer, out, n, n2, node);
                if (this.shouldIndent(node)) {
                    this.condFlushLine(out, n2);
                    n2 += this.configuration.spaces;
                    for (Node node6 = node.content; node6 != null; node6 = node6.next) {
                        this.printTree(out, n, n2, lexer, node6);
                    }
                    this.condFlushLine(out, n2);
                    n2 -= this.configuration.spaces;
                    this.condFlushLine(out, n2);
                }
                else {
                    for (Node node7 = node.content; node7 != null; node7 = node7.next) {
                        this.printTree(out, n, n2, lexer, node7);
                    }
                }
                this.printEndTag(n, n2, node);
            }
            else {
                this.condFlushLine(out, n2);
                if (this.configuration.smartIndent && node.prev != null) {
                    this.flushLine(out, n2);
                }
                if (!this.configuration.hideEndTags || node.tag == null || !TidyUtils.toBoolean(node.tag.model & 0x200000) || node.attributes != null) {
                    this.printTag(lexer, out, n, n2, node);
                    if (this.shouldIndent(node)) {
                        this.condFlushLine(out, n2);
                    }
                    else if (TidyUtils.toBoolean(node.tag.model & 0x2) || node.tag == tt.tagNoframes || (TidyUtils.toBoolean(node.tag.model & 0x4) && node.tag != tt.tagTitle)) {
                        this.flushLine(out, n2);
                    }
                }
                if (node.tag == tt.tagBody && this.configuration.burstSlides) {
                    this.printSlide(out, n, this.configuration.indentContent ? (n2 + this.configuration.spaces) : n2, lexer);
                }
                else {
                    Node node8 = null;
                    for (Node node9 = node.content; node9 != null; node9 = node9.next) {
                        if (node8 != null && !this.configuration.indentContent && node8.type == 4 && node9.tag != null && !TidyUtils.toBoolean(node9.tag.model & 0x10)) {
                            this.flushLine(out, n2);
                        }
                        this.printTree(out, n, this.shouldIndent(node) ? (n2 + this.configuration.spaces) : n2, lexer, node9);
                        node8 = node9;
                    }
                }
                if (this.shouldIndent(node) || ((TidyUtils.toBoolean(node.tag.model & 0x2) || node.tag == tt.tagNoframes || (TidyUtils.toBoolean(node.tag.model & 0x4) && node.tag != tt.tagTitle)) && !this.configuration.hideEndTags)) {
                    this.condFlushLine(out, this.configuration.indentContent ? (n2 + this.configuration.spaces) : n2);
                    if (!this.configuration.hideEndTags || !TidyUtils.toBoolean(node.tag.model & 0x8000)) {
                        this.printEndTag(n, n2, node);
                        if (!lexer.seenEndHtml) {
                            this.flushLine(out, n2);
                        }
                    }
                }
                else {
                    if (!this.configuration.hideEndTags || !TidyUtils.toBoolean(node.tag.model & 0x8000)) {
                        this.printEndTag(n, n2, node);
                    }
                    this.flushLine(out, n2);
                }
            }
        }
    }
    
    public void printXMLTree(final Out out, final short n, int n2, final Lexer lexer, final Node node) {
        final TagTable tt = this.configuration.tt;
        if (node == null) {
            return;
        }
        if (node.type == 4 || (node.type == 8 && lexer.configuration.escapeCdata)) {
            this.printText(out, n, n2, node.textarray, node.start, node.end);
        }
        else if (node.type == 2) {
            this.condFlushLine(out, n2);
            this.printComment(out, 0, node);
            this.condFlushLine(out, 0);
        }
        else if (node.type == 0) {
            for (Node node2 = node.content; node2 != null; node2 = node2.next) {
                this.printXMLTree(out, n, n2, lexer, node2);
            }
        }
        else if (node.type == 1) {
            this.printDocType(out, n2, lexer, node);
        }
        else if (node.type == 3) {
            this.printPI(out, n2, node);
        }
        else if (node.type == 13) {
            this.printXmlDecl(out, n2, node);
        }
        else if (node.type == 8) {
            this.printCDATA(out, n2, node);
        }
        else if (node.type == 9) {
            this.printSection(out, n2, node);
        }
        else if (node.type == 10) {
            this.printAsp(out, n2, node);
        }
        else if (node.type == 11) {
            this.printJste(out, n2, node);
        }
        else if (node.type == 12) {
            this.printPhp(out, n2, node);
        }
        else if (TidyUtils.toBoolean(node.tag.model & 0x1) || (node.type == 7 && !this.configuration.xHTML)) {
            this.condFlushLine(out, n2);
            this.printTag(lexer, out, n, n2, node);
        }
        else {
            boolean b = false;
            for (Node node3 = node.content; node3 != null; node3 = node3.next) {
                if (node3.type == 4) {
                    b = true;
                    break;
                }
            }
            this.condFlushLine(out, n2);
            int n3;
            if (ParserImpl.XMLPreserveWhiteSpace(node, tt)) {
                n2 = 0;
                n3 = 0;
                b = false;
            }
            else if (b) {
                n3 = n2;
            }
            else {
                n3 = n2 + this.configuration.spaces;
            }
            this.printTag(lexer, out, n, n2, node);
            if (!b && node.content != null) {
                this.flushLine(out, n2);
            }
            for (Node node4 = node.content; node4 != null; node4 = node4.next) {
                this.printXMLTree(out, n, n3, lexer, node4);
            }
            if (!b && node.content != null) {
                this.condFlushLine(out, n3);
            }
            this.printEndTag(n, n2, node);
        }
    }
    
    public int countSlides(Node node) {
        int n = 1;
        final TagTable tt = this.configuration.tt;
        if (node != null && node.content != null && node.content.tag == tt.tagH2) {
            --n;
        }
        if (node != null) {
            for (node = node.content; node != null; node = node.next) {
                if (node.tag == tt.tagH2) {
                    ++n;
                }
            }
        }
        return n;
    }
    
    private void printNavBar(final Out out, final int n) {
        this.condFlushLine(out, n);
        this.printString("<center><small>");
        final NumberFormat instance = NumberFormat.getInstance();
        instance.setMinimumIntegerDigits(3);
        if (this.slide > 1) {
            this.printString("<a href=\"slide" + instance.format(this.slide - 1) + ".html\">previous</a> | ");
            this.condFlushLine(out, n);
            if (this.slide < this.count) {
                this.printString("<a href=\"slide001.html\">start</a> | ");
            }
            else {
                this.printString("<a href=\"slide001.html\">start</a>");
            }
            this.condFlushLine(out, n);
        }
        if (this.slide < this.count) {
            this.printString("<a href=\"slide" + instance.format(this.slide + 1) + ".html\">next</a>");
        }
        this.printString("</small></center>");
        this.condFlushLine(out, n);
    }
    
    public void printSlide(final Out out, final short n, final int n2, final Lexer lexer) {
        final TagTable tt = this.configuration.tt;
        final NumberFormat instance = NumberFormat.getInstance();
        instance.setMinimumIntegerDigits(3);
        this.printString("<div onclick=\"document.location='slide" + instance.format((this.slide < this.count) ? ((long)(this.slide + 1)) : 1L) + ".html'\">");
        this.condFlushLine(out, n2);
        if (this.slidecontent != null && this.slidecontent.tag == tt.tagH2) {
            this.printNavBar(out, n2);
            this.addC(60, this.linelen++);
            this.addC(TidyUtils.foldCase('h', this.configuration.upperCaseTags, this.configuration.xmlTags), this.linelen++);
            this.addC(TidyUtils.foldCase('r', this.configuration.upperCaseTags, this.configuration.xmlTags), this.linelen++);
            if (this.configuration.xmlOut) {
                this.printString(" />");
            }
            else {
                this.addC(62, this.linelen++);
            }
            if (this.configuration.indentContent) {
                this.condFlushLine(out, n2);
            }
            this.printTree(out, n, this.configuration.indentContent ? (n2 + this.configuration.spaces) : n2, lexer, this.slidecontent);
            this.slidecontent = this.slidecontent.next;
        }
        Node node = null;
        Node slidecontent;
        for (slidecontent = this.slidecontent; slidecontent != null && slidecontent.tag != tt.tagH2; slidecontent = slidecontent.next) {
            if (node != null && !this.configuration.indentContent && node.type == 4 && slidecontent.tag != null && TidyUtils.toBoolean(slidecontent.tag.model & 0x8)) {
                this.flushLine(out, n2);
                this.flushLine(out, n2);
            }
            this.printTree(out, n, this.configuration.indentContent ? (n2 + this.configuration.spaces) : n2, lexer, slidecontent);
            node = slidecontent;
        }
        this.slidecontent = slidecontent;
        this.condFlushLine(out, n2);
        this.printString("<br clear=\"all\">");
        this.condFlushLine(out, n2);
        this.addC(60, this.linelen++);
        this.addC(TidyUtils.foldCase('h', this.configuration.upperCaseTags, this.configuration.xmlTags), this.linelen++);
        this.addC(TidyUtils.foldCase('r', this.configuration.upperCaseTags, this.configuration.xmlTags), this.linelen++);
        if (this.configuration.xmlOut) {
            this.printString(" />");
        }
        else {
            this.addC(62, this.linelen++);
        }
        if (this.configuration.indentContent) {
            this.condFlushLine(out, n2);
        }
        this.printNavBar(out, n2);
        this.printString("</div>");
        this.condFlushLine(out, n2);
    }
    
    public void addTransitionEffect(final Lexer lexer, final Node node, final double n) {
        final Node head = node.findHEAD(lexer.configuration.tt);
        final String string = "blendTrans(Duration=" + new Double(n).toString() + ")";
        if (head != null) {
            final Node inferredTag = lexer.inferredTag("meta");
            inferredTag.addAttribute("http-equiv", "Page-Enter");
            inferredTag.addAttribute("content", string);
            head.insertNodeAtStart(inferredTag);
        }
    }
    
    public void createSlides(final Lexer lexer, final Node node) {
        final NumberFormat instance = NumberFormat.getInstance();
        instance.setMinimumIntegerDigits(3);
        final Node body = node.findBody(lexer.configuration.tt);
        this.count = this.countSlides(body);
        this.slidecontent = body.content;
        this.addTransitionEffect(lexer, node, 3.0);
        this.slide = 1;
        while (this.slide <= this.count) {
            final String string = "slide" + instance.format(this.slide) + ".html";
            try {
                final FileOutputStream fileOutputStream = new FileOutputStream(string);
                final Out out = OutFactory.getOut(this.configuration, fileOutputStream);
                this.printTree(out, (short)0, 0, lexer, node);
                this.flushLine(out, 0);
                fileOutputStream.close();
            }
            catch (final IOException ex) {
                System.err.println(string + ex.toString());
            }
            ++this.slide;
        }
        while (new File("slide" + instance.format(this.slide) + ".html").delete()) {
            ++this.slide;
        }
    }
}
