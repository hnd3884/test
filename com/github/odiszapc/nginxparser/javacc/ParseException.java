package com.github.odiszapc.nginxparser.javacc;

public class ParseException extends Exception
{
    private static final long serialVersionUID = 1L;
    public Token currentToken;
    public int[][] expectedTokenSequences;
    public String[] tokenImage;
    protected String eol;
    
    public ParseException(final Token currentToken, final int[][] expectedTokenSequences, final String[] tokenImage) {
        super(initialise(currentToken, expectedTokenSequences, tokenImage));
        this.eol = System.getProperty("line.separator", "\n");
        this.currentToken = currentToken;
        this.expectedTokenSequences = expectedTokenSequences;
        this.tokenImage = tokenImage;
    }
    
    public ParseException() {
        this.eol = System.getProperty("line.separator", "\n");
    }
    
    public ParseException(final String s) {
        super(s);
        this.eol = System.getProperty("line.separator", "\n");
    }
    
    private static String initialise(final Token token, final int[][] array, final String[] array2) {
        final String property = System.getProperty("line.separator", "\n");
        final StringBuffer sb = new StringBuffer();
        int length = 0;
        for (int i = 0; i < array.length; ++i) {
            if (length < array[i].length) {
                length = array[i].length;
            }
            for (int j = 0; j < array[i].length; ++j) {
                sb.append(array2[array[i][j]]).append(' ');
            }
            if (array[i][array[i].length - 1] != 0) {
                sb.append("...");
            }
            sb.append(property).append("    ");
        }
        String s = "Encountered \"";
        Token token2 = token.next;
        for (int k = 0; k < length; ++k) {
            if (k != 0) {
                s += " ";
            }
            if (token2.kind == 0) {
                s += array2[0];
                break;
            }
            s = s + " " + array2[token2.kind] + " \"" + add_escapes(token2.image) + " \"";
            token2 = token2.next;
        }
        final String string = s + "\" at line " + token.next.beginLine + ", column " + token.next.beginColumn + "." + property;
        String s2;
        if (array.length == 1) {
            s2 = string + "Was expecting:" + property + "    ";
        }
        else {
            s2 = string + "Was expecting one of:" + property + "    ";
        }
        return s2 + sb.toString();
    }
    
    static String add_escapes(final String s) {
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < s.length(); ++i) {
            switch (s.charAt(i)) {
                case '\0': {
                    break;
                }
                case '\b': {
                    sb.append("\\b");
                    break;
                }
                case '\t': {
                    sb.append("\\t");
                    break;
                }
                case '\n': {
                    sb.append("\\n");
                    break;
                }
                case '\f': {
                    sb.append("\\f");
                    break;
                }
                case '\r': {
                    sb.append("\\r");
                    break;
                }
                case '\"': {
                    sb.append("\\\"");
                    break;
                }
                case '\'': {
                    sb.append("\\'");
                    break;
                }
                case '\\': {
                    sb.append("\\\\");
                    break;
                }
                default: {
                    final char char1;
                    if ((char1 = s.charAt(i)) < ' ' || char1 > '~') {
                        final String string = "0000" + Integer.toString(char1, 16);
                        sb.append("\\u" + string.substring(string.length() - 4, string.length()));
                        break;
                    }
                    sb.append(char1);
                    break;
                }
            }
        }
        return sb.toString();
    }
}
