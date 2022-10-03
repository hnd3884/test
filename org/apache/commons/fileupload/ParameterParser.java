package org.apache.commons.fileupload;

import java.util.Locale;
import java.io.UnsupportedEncodingException;
import org.apache.commons.fileupload.util.mime.MimeUtility;
import java.util.HashMap;
import java.util.Map;

public class ParameterParser
{
    private char[] chars;
    private int pos;
    private int len;
    private int i1;
    private int i2;
    private boolean lowerCaseNames;
    
    public ParameterParser() {
        this.chars = null;
        this.pos = 0;
        this.len = 0;
        this.i1 = 0;
        this.i2 = 0;
        this.lowerCaseNames = false;
    }
    
    private boolean hasChar() {
        return this.pos < this.len;
    }
    
    private String getToken(final boolean quoted) {
        while (this.i1 < this.i2 && Character.isWhitespace(this.chars[this.i1])) {
            ++this.i1;
        }
        while (this.i2 > this.i1 && Character.isWhitespace(this.chars[this.i2 - 1])) {
            --this.i2;
        }
        if (quoted && this.i2 - this.i1 >= 2 && this.chars[this.i1] == '\"' && this.chars[this.i2 - 1] == '\"') {
            ++this.i1;
            --this.i2;
        }
        String result = null;
        if (this.i2 > this.i1) {
            result = new String(this.chars, this.i1, this.i2 - this.i1);
        }
        return result;
    }
    
    private boolean isOneOf(final char ch, final char[] charray) {
        boolean result = false;
        for (final char element : charray) {
            if (ch == element) {
                result = true;
                break;
            }
        }
        return result;
    }
    
    private String parseToken(final char[] terminators) {
        this.i1 = this.pos;
        this.i2 = this.pos;
        while (this.hasChar()) {
            final char ch = this.chars[this.pos];
            if (this.isOneOf(ch, terminators)) {
                break;
            }
            ++this.i2;
            ++this.pos;
        }
        return this.getToken(false);
    }
    
    private String parseQuotedToken(final char[] terminators) {
        this.i1 = this.pos;
        this.i2 = this.pos;
        boolean quoted = false;
        boolean charEscaped = false;
        while (this.hasChar()) {
            final char ch = this.chars[this.pos];
            if (!quoted && this.isOneOf(ch, terminators)) {
                break;
            }
            if (!charEscaped && ch == '\"') {
                quoted = !quoted;
            }
            charEscaped = (!charEscaped && ch == '\\');
            ++this.i2;
            ++this.pos;
        }
        return this.getToken(true);
    }
    
    public boolean isLowerCaseNames() {
        return this.lowerCaseNames;
    }
    
    public void setLowerCaseNames(final boolean b) {
        this.lowerCaseNames = b;
    }
    
    public Map<String, String> parse(final String str, final char[] separators) {
        if (separators == null || separators.length == 0) {
            return new HashMap<String, String>();
        }
        char separator = separators[0];
        if (str != null) {
            int idx = str.length();
            for (final char separator2 : separators) {
                final int tmp = str.indexOf(separator2);
                if (tmp != -1 && tmp < idx) {
                    idx = tmp;
                    separator = separator2;
                }
            }
        }
        return this.parse(str, separator);
    }
    
    public Map<String, String> parse(final String str, final char separator) {
        if (str == null) {
            return new HashMap<String, String>();
        }
        return this.parse(str.toCharArray(), separator);
    }
    
    public Map<String, String> parse(final char[] charArray, final char separator) {
        if (charArray == null) {
            return new HashMap<String, String>();
        }
        return this.parse(charArray, 0, charArray.length, separator);
    }
    
    public Map<String, String> parse(final char[] charArray, final int offset, final int length, final char separator) {
        if (charArray == null) {
            return new HashMap<String, String>();
        }
        final HashMap<String, String> params = new HashMap<String, String>();
        this.chars = charArray;
        this.pos = offset;
        this.len = length;
        String paramName = null;
        String paramValue = null;
        while (this.hasChar()) {
            paramName = this.parseToken(new char[] { '=', separator });
            paramValue = null;
            if (this.hasChar() && charArray[this.pos] == '=') {
                ++this.pos;
                paramValue = this.parseQuotedToken(new char[] { separator });
                if (paramValue != null) {
                    try {
                        paramValue = MimeUtility.decodeText(paramValue);
                    }
                    catch (final UnsupportedEncodingException ex) {}
                }
            }
            if (this.hasChar() && charArray[this.pos] == separator) {
                ++this.pos;
            }
            if (paramName != null && paramName.length() > 0) {
                if (this.lowerCaseNames) {
                    paramName = paramName.toLowerCase(Locale.ENGLISH);
                }
                params.put(paramName, paramValue);
            }
        }
        return params;
    }
}
