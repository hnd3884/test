package org.apache.lucene.search.highlight;

public class SimpleHTMLFormatter implements Formatter
{
    private static final String DEFAULT_PRE_TAG = "<B>";
    private static final String DEFAULT_POST_TAG = "</B>";
    private String preTag;
    private String postTag;
    
    public SimpleHTMLFormatter(final String preTag, final String postTag) {
        this.preTag = preTag;
        this.postTag = postTag;
    }
    
    public SimpleHTMLFormatter() {
        this("<B>", "</B>");
    }
    
    @Override
    public String highlightTerm(final String originalText, final TokenGroup tokenGroup) {
        if (tokenGroup.getTotalScore() <= 0.0f) {
            return originalText;
        }
        final StringBuilder returnBuffer = new StringBuilder(this.preTag.length() + originalText.length() + this.postTag.length());
        returnBuffer.append(this.preTag);
        returnBuffer.append(originalText);
        returnBuffer.append(this.postTag);
        return returnBuffer.toString();
    }
}
