package org.apache.lucene.search.highlight;

public class SimpleHTMLEncoder implements Encoder
{
    @Override
    public String encodeText(final String originalText) {
        return htmlEncode(originalText);
    }
    
    public static final String htmlEncode(final String plainText) {
        if (plainText == null || plainText.length() == 0) {
            return "";
        }
        final StringBuilder result = new StringBuilder(plainText.length());
        for (int index = 0; index < plainText.length(); ++index) {
            final char ch = plainText.charAt(index);
            switch (ch) {
                case '\"': {
                    result.append("&quot;");
                    break;
                }
                case '&': {
                    result.append("&amp;");
                    break;
                }
                case '<': {
                    result.append("&lt;");
                    break;
                }
                case '>': {
                    result.append("&gt;");
                    break;
                }
                case '\'': {
                    result.append("&#x27;");
                    break;
                }
                case '/': {
                    result.append("&#x2F;");
                    break;
                }
                default: {
                    result.append(ch);
                    break;
                }
            }
        }
        return result.toString();
    }
}
