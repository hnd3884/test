package org.bouncycastle.i18n.filter;

public class SQLFilter implements Filter
{
    public String doFilter(final String s) {
        final StringBuffer sb = new StringBuffer(s);
        for (int i = 0; i < sb.length(); ++i) {
            switch (sb.charAt(i)) {
                case '\'': {
                    sb.replace(i, i + 1, "\\'");
                    ++i;
                    break;
                }
                case '\"': {
                    sb.replace(i, i + 1, "\\\"");
                    ++i;
                    break;
                }
                case '=': {
                    sb.replace(i, i + 1, "\\=");
                    ++i;
                    break;
                }
                case '-': {
                    sb.replace(i, i + 1, "\\-");
                    ++i;
                    break;
                }
                case '/': {
                    sb.replace(i, i + 1, "\\/");
                    ++i;
                    break;
                }
                case '\\': {
                    sb.replace(i, i + 1, "\\\\");
                    ++i;
                    break;
                }
                case ';': {
                    sb.replace(i, i + 1, "\\;");
                    ++i;
                    break;
                }
                case '\r': {
                    sb.replace(i, i + 1, "\\r");
                    ++i;
                    break;
                }
                case '\n': {
                    sb.replace(i, i + 1, "\\n");
                    ++i;
                    break;
                }
            }
        }
        return sb.toString();
    }
    
    public String doFilterUrl(final String s) {
        return this.doFilter(s);
    }
}
