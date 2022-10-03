package org.apache.commons.csv;

final class Constants
{
    static final char BACKSLASH = '\\';
    static final char BACKSPACE = '\b';
    static final char COMMA = ',';
    static final char COMMENT = '#';
    static final char CR = '\r';
    static final String CRLF = "\r\n";
    static final Character DOUBLE_QUOTE_CHAR;
    static final String EMPTY = "";
    static final int END_OF_STREAM = -1;
    static final char FF = '\f';
    static final char LF = '\n';
    static final String LINE_SEPARATOR = "\u2028";
    static final String NEXT_LINE = "\u0085";
    static final String PARAGRAPH_SEPARATOR = "\u2029";
    static final char PIPE = '|';
    static final char RS = '\u001e';
    static final char SP = ' ';
    static final char TAB = '\t';
    static final int UNDEFINED = -2;
    static final char US = '\u001f';
    
    static {
        DOUBLE_QUOTE_CHAR = '\"';
    }
}
