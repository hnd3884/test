package org.apache.taglibs.standard.lang.jstl;

public class StringLiteral extends Literal
{
    StringLiteral(final Object pValue) {
        super(pValue);
    }
    
    public static StringLiteral fromToken(final String pToken) {
        return new StringLiteral(getValueFromToken(pToken));
    }
    
    public static StringLiteral fromLiteralValue(final String pValue) {
        return new StringLiteral(pValue);
    }
    
    public static String getValueFromToken(final String pToken) {
        final StringBuffer buf = new StringBuffer();
        final int len = pToken.length() - 1;
        boolean escaping = false;
        for (int i = 1; i < len; ++i) {
            final char ch = pToken.charAt(i);
            if (escaping) {
                buf.append(ch);
                escaping = false;
            }
            else if (ch == '\\') {
                escaping = true;
            }
            else {
                buf.append(ch);
            }
        }
        return buf.toString();
    }
    
    public static String toStringToken(final String pValue) {
        if (pValue.indexOf(34) < 0 && pValue.indexOf(92) < 0) {
            return "\"" + pValue + "\"";
        }
        final StringBuffer buf = new StringBuffer();
        buf.append('\"');
        for (int len = pValue.length(), i = 0; i < len; ++i) {
            final char ch = pValue.charAt(i);
            if (ch == '\\') {
                buf.append('\\');
                buf.append('\\');
            }
            else if (ch == '\"') {
                buf.append('\\');
                buf.append('\"');
            }
            else {
                buf.append(ch);
            }
        }
        buf.append('\"');
        return buf.toString();
    }
    
    public static String toIdentifierToken(final String pValue) {
        if (isJavaIdentifier(pValue)) {
            return pValue;
        }
        return toStringToken(pValue);
    }
    
    static boolean isJavaIdentifier(final String pValue) {
        final int len = pValue.length();
        if (len == 0) {
            return false;
        }
        if (!Character.isJavaIdentifierStart(pValue.charAt(0))) {
            return false;
        }
        for (int i = 1; i < len; ++i) {
            if (!Character.isJavaIdentifierPart(pValue.charAt(i))) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public String getExpressionString() {
        return toStringToken((String)this.getValue());
    }
}
