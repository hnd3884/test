package org.apache.lucene.search.highlight;

public class SpanGradientFormatter extends GradientFormatter
{
    private static final String TEMPLATE = "<span style=\"background: #EEEEEE; color: #000000;\">...</span>";
    private static final int EXTRA;
    
    public SpanGradientFormatter(final float maxScore, final String minForegroundColor, final String maxForegroundColor, final String minBackgroundColor, final String maxBackgroundColor) {
        super(maxScore, minForegroundColor, maxForegroundColor, minBackgroundColor, maxBackgroundColor);
    }
    
    @Override
    public String highlightTerm(final String originalText, final TokenGroup tokenGroup) {
        if (tokenGroup.getTotalScore() == 0.0f) {
            return originalText;
        }
        final float score = tokenGroup.getTotalScore();
        if (score == 0.0f) {
            return originalText;
        }
        final StringBuilder sb = new StringBuilder(originalText.length() + SpanGradientFormatter.EXTRA);
        sb.append("<span style=\"");
        if (this.highlightForeground) {
            sb.append("color: ");
            sb.append(this.getForegroundColorString(score));
            sb.append("; ");
        }
        if (this.highlightBackground) {
            sb.append("background: ");
            sb.append(this.getBackgroundColorString(score));
            sb.append("; ");
        }
        sb.append("\">");
        sb.append(originalText);
        sb.append("</span>");
        return sb.toString();
    }
    
    static {
        EXTRA = "<span style=\"background: #EEEEEE; color: #000000;\">...</span>".length();
    }
}
