package org.bouncycastle.i18n.filter;

public class HTMLFilter implements Filter
{
    public String doFilter(final String s) {
        final StringBuffer sb = new StringBuffer(s);
        for (int i = 0; i < sb.length(); i += 4) {
            switch (sb.charAt(i)) {
                case '<': {
                    sb.replace(i, i + 1, "&#60");
                    break;
                }
                case '>': {
                    sb.replace(i, i + 1, "&#62");
                    break;
                }
                case '(': {
                    sb.replace(i, i + 1, "&#40");
                    break;
                }
                case ')': {
                    sb.replace(i, i + 1, "&#41");
                    break;
                }
                case '#': {
                    sb.replace(i, i + 1, "&#35");
                    break;
                }
                case '&': {
                    sb.replace(i, i + 1, "&#38");
                    break;
                }
                case '\"': {
                    sb.replace(i, i + 1, "&#34");
                    break;
                }
                case '\'': {
                    sb.replace(i, i + 1, "&#39");
                    break;
                }
                case '%': {
                    sb.replace(i, i + 1, "&#37");
                    break;
                }
                case ';': {
                    sb.replace(i, i + 1, "&#59");
                    break;
                }
                case '+': {
                    sb.replace(i, i + 1, "&#43");
                    break;
                }
                case '-': {
                    sb.replace(i, i + 1, "&#45");
                    break;
                }
                default: {
                    i -= 3;
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
